<%@page language="java" session="true" pageEncoding="UTF-8" %>
<%@ include file="/netmarkets/jsp/util/beginShell.jspf" %>
<html>
<head>
    <title>ppm报表计算</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/netmarkets/jsp/ppm/static/bootstrap.css">

    <script type="text/javascript"
            src="${pageContext.request.contextPath}/netmarkets/jsp/ppm/static/jquery.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/netmarkets/jsp/ppm/static/echarts.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/netmarkets/jsp/ppm/static/My97DatePicker/My97DatePicker/WdatePicker.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/netmarkets/jsp/ppm/static/modifyBtnClass.js" >
    </script>
    <script type="text/javascript">


        $(function () {
            initPolyline(null,null);
            initHistogram(null,null,null,null,null);
            bindBtn();
            bindEven();
        })

        function bindBtn() {
            $("#statisticsBtn").click(function () {
                getData();
            })
            $("#exportBtn").click(function () {
                doExport();
            });
        }
        function bindEven() {
            $("select[name=timeType]").change(function () {
                var timeType=$(this).val();
                if("month"==timeType){
                    $("select[name=statisticalType]").prop({
                        "disabled":false,
                        "title":""
                    }).val("TB");
                    $("input[name=dateTime]").attr("onclick","WdatePicker({el:this,dateFmt:'yyyy-MM'})").val("");

                }else if("year"==timeType){
                    $("select[name=statisticalType]").prop({
                        "disabled":true,
                        "title":"对于年度的统计维度，环比不可选"
                    }).val("TB");
                    $("input[name=dateTime]").attr("onclick","WdatePicker({el:this,dateFmt:'yyyy'})").val("");
                }
            });
        }
        function doExport() {
            var dateTime=$("input[name=dateTime]").val();
            if(!dateTime){
                alert("请选择查询月份");
                return false;
            }

            window.location.href="${pageContext.request.contextPath}/netmarkets/jsp/ppm/template/PPMExport2.jsp?dateTime="+dateTime;
        }
        function getData() {
            //查询维度
            var dimension=$("select[name=timeType]").val();
            if(!dimension){
                alert("请选择查询维度");
                return false;
            }
            var dateTime=$("input[name=dateTime]").val();
            if(!dateTime){
                alert("请选择查询月份");
                return false;
            }
            var _type=$("select[name=statisticalType]").find("option:selected").val();
            if(!_type){
                alert("请选择统计类别");
                return false;
            }
            $.ajax({
                url: '/Windchill/servlet/Navigation/ppmCalculateDetail',
                type: 'get',
                dataType:"json",
                data: {'actionName':'getData','dateTime':dateTime},
                success: function (result) {
                    if (result.success) {
                        doData(result.data,_type);
                    } else {
                        alert(result.message)
                    }
                },
                error: function (a, b, c, d) {
                    alert(b);
                }
            });
        }

        /**
         *
         * @param data
         * @param type 环比（HB）或同比(TB)
         */
        function doData(data,type) {
            var keys2 = [];
            var value2 = [];
            var keys3 = [];
            var value3 = [];
            var percentageKey = [];
            var percentage = [];
            var thisTime;
            var prevTime;
            //时间维度
            var dateType=$("select[name=timeType]").val();
            if(dateType=="year"){
                thisTime="当年"
                prevTime="去年";
            }else{
                if(type=="TB"){
                    thisTime="当月"
                    prevTime="去年本月";
                }else if(type=="HB"){
                    thisTime="当月"
                    prevTime="上月";
                }

            }

            $.each(data[thisTime], function (k, v) {
                keys2.push(""+k);
                value2.push(v);
            });
            $.each(keys2,function(m,n){
                value3.push(data[prevTime][n]);
            });
            var typeKey=type=="HB"?"环比":"同比";
            $.each(value3,function(i,n){
                if(n==0){
                    percentage.push(0);
                    return true;
                }else{
                    var growthRate=(value2[i]-n)/n;
                    percentage.push((growthRate*100).toFixed(2));
                }

            });
            initHistogram(keys2,value3, value2,thisTime,prevTime);
            initPolyline(keys2,percentage);
        }

        /**
         * 折线图
         */
        function initPolyline(percentageKey, percentage) {
            var dom = document.getElementById("polyline");
            var myChart2 = echarts.init(dom);
            option2 = {
                title: {
                    text: 'ppm数据增长率'
                },
                tooltip: {
                    trigger: 'axis',
                    //在这里设置
                    formatter: '{a0}{c0}%'
                },
                legend: {
                    data: percentageKey
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                toolbox: {
                    feature: {
                        saveAsImage: {}
                    }
                },
                xAxis: {
                    type: 'category',
                    boundaryGap: false,
                    data: percentageKey
                },
                yAxis: {
                    type: 'value'
                },
                series: [{
                    name: '',
                    type: 'line',
                    data: percentage
                }]
            };
            if (option2 && typeof option2 === "object") {
                myChart2.setOption(option2, true);
            }
        }

        /**
         * 柱状图
         * @param keys
         * @param data1 上个时间周期数据
         * @param data2 当前时间周期数据
         * @param thisTime 上个时间周期名
         * @param prevTime 当前时间周期名
         */
        function initHistogram(keys, data1, data2,thisTime,prevTime) {
            //神经病一般的bug，传进来的数组对象就不能正确解析，非得在这里赋值
            var xAxisData = [];
            $.each(keys,function (i,n) {
                xAxisData.push(n);
            })
            option3 = {
                title: {
                    text: 'ppm数据柱状图'
                },
                legend: {
                    data: [prevTime, thisTime],
                    align: 'left'
                },
                toolbox: {
                    // y: 'bottom',
                    feature: {
                        magicType: {
                            type: ['stack', 'tiled']
                        },
                        dataView: {},
                        saveAsImage: {
                            pixelRatio: 2
                        }
                    }
                },
                tooltip: {},
                xAxis: {
                    data: xAxisData,
                    silent: false,
                    splitLine: {
                        show: false
                    }
                },
                yAxis: {
                },
                series: [{
                    name: prevTime,
                    type: 'bar',
                    data: data1,
                    animationDelay: function (idx) {
                        return idx * 10;
                    }
                }, {
                    name: thisTime,
                    type: 'bar',
                    data: data2,
                    animationDelay: function (idx) {
                        return idx * 10 + 100;
                    }
                }],
                animationEasing: 'elasticOut',
                animationDelayUpdate: function (idx) {
                    return idx * 5;
                }
            };
            myEcharts3 = echarts.init($("#histogram").get(0));
            myEcharts3.setOption(option3);
        }
    </script>

</head>
<body>
<div class="container-fluid">
    <div class="row" style="margin-top: 50px;">
        <div class="col-md-9">
            <form class="form-horizontal">
                <div class="form-group">
                    <label class="col-md-1">统计维度</label>
                    <div class="col-md-2">
                        <select name="timeType" class="form-control">
                            <option style="display: none;"></option>
                            <option value="month">月</option>
                            <option value="year">年</option>
                        </select>
                    </div>

                    <label class="col-md-1">统计类别</label>
                    <div class="col-md-2">
                        <select name="statisticalType" class="form-control">
                            <option style="display: none;"></option>
                            <option value="TB">同比</option>
                            <option value="HB">环比</option>
                        </select>

                    </div>

                    <label class="col-md-1">数据时间</label>
                    <div class="col-md-3">
                        <input name="dateTime" readonly="readonly" class="form-control" type="text" onClick="WdatePicker({el:this,dateFmt:'yyyy-MM'})">
                    </div>

                </div>
            </form>
        </div>
        <div class="col-md-3">
            <div class="btn-group">
                <input class="blist x-btn-text" id="statisticsBtn" type="button" value="统计"/>
                <input class="blist x-btn-text" id="exportBtn" type="button" value="导出"/>
            </div>
        </div>
    </div>
    <div class="row">
        <!-- 折线图 -->
        <div id="polyline" style="width: 45%;height:500px;float: left;"></div>
        <div id="histogram"
             style="width: 45%;height:500px;float: left;margin-top: 30px;"></div>
    </div>
</div>






</body>
</html>

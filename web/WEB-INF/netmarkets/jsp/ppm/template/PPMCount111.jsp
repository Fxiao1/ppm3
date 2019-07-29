<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>PPMCount</title>
    <link rel="stylesheet" type="text/css" href="./static/css/bootstrap.css"/>
    <script type="text/javascript" src="./static/jquery.min.js"></script>
    <script type="text/javascript" src="./static/bootstrap.js"></script>


    <style type="text/css">

        .row {
            border:1px solid #000;
        }
        .center-in-center{
            position: absolute;
            top: 15%;
            left: 15%;
        }
        .col-sm-2 {
            height:50px;
            line-height: 50px;
        }

        th {
            vertical-align: middle;
            text-align: center;
        }
        tr {
            height: 33px;

        }
        td {
            text-align: center;
        }

    </style>

</head>
<body>
<div class="center-in-center" style="width: 70%;" >
    <div align="center" class="row"><h2>PPM计算</h2></div>
    <div class="row">
        <div class='col-sm-12' >
            <form action="">
                <div class="form-group">
                    <label class="control-label col-sm-1">
                        开始时间：
                    </label>
                    <div class="col-sm-2">
                        <input type="date" class="form-control" id="startDate">
                    </div>
                    <label class="control-label col-sm-1">
                        结束时间：
                    </label>
                    <div class="col-sm-2">
                        <input type="date" class="form-control" id="endDate">
                    </div>
                </div>



                <div>
                    <input id="bt1" class="col-sm-1" type="button" style="margin-right: 40px" value="计算">

                    <input id="bt2" class="col-sm-1" type="button" value="导出" onclick="window.location='PPMExport.jsp'">

                </div>
            </form>
        </div>




        <table border="2" style="width: 100%; "  >
            <thead>
            <tr style="background-color: #9d9d9d;">
                <th >工序名称</th>
                <th >工序检验特性名称</th>
                <th >工序PPM</th>
            </tr>
            </thead>
            <tbody id="tbody">

            </tbody>

        </table>
    </div>


</div>

<script>


    //点击计算
    $("#bt1").click(function(){
        var startDate = document.getElementById("startDate").value;
        var endDate = document.getElementById("endDate").value;

        var options = {
            colums: [{
                "Index": "procedureName",
                "Name": "工序名称"
            }, {
                "Index": "characName",
                "Name": "工序检验特性名称"
            }, {
                "Index": "procedurePPM",
                "Name": "工序PPM"
            }]
        }

        add_detail(options);

    });



    //点击导出
    $("#bt2").click(function () {
        var html = "<html><head><meta charset='utf-8' /></head><body>"
            + document.getElementsByTagName("table")[0].outerHTML + "</body></html>";
        var blob = new Blob([html], { type: "application/vnd.ms-excel" });
        var a = document.getElementsByTagName("a")[0];
        a.href = URL.createObjectURL(blob);
        a.download = "PPM计算.xls";
    });



    function add_detail(options){
        var $tbody = $("#tbody");
        $tbody.empty();
        var startDate = document.getElementById("startDate").value;
        var endDate = document.getElementById("endDate").value;

        console.log(startDate)
        var colums = options.colums;
        var url = "data.json";//json文件或者数据库查询后的地址
        ///Windchill/servlet/Navigation/calculate
        var content = [];
        //ajax获取数据源后存入content数据中。
        $.ajax({
            type: "get",
            url: url,
            data: { "startDate": startDate, "endDate": endDate },
            dataType: "json",
            async: false,
            success: function(result) {
                content = result;

            }
        });
        for(var c = 0; c < content.length; c++) {
            //遍历所有列
            var cols = [];
            $.each(colums, function(key, value) {

                //遍历json数据
                $.each(content[c], function(key2, value2) {
                    if(key2 == value.Index) {
                        cols.push(value2);

                    }
                });
            });
            var html="<tr>";
            /*            $.each(cols,function(k,v){

                            html += "<td rowspan='1'>"+v+"</td>";

                        });*/
            html += "<td rowspan='1'>"+cols[0]+"</td>";
            var table2 = cols[1];
            console.log(cols[1]);

            html += "<td rowspan='1'><table border=\"2\" style=\"width: 100%; \">";
            $.each(table2,function (k,v) {
                html += "<tr><td>" +v+"</td></tr>";
            })
            //html += "<tr><td >" +cols[1]+"</td></tr>";

            html += "</table></td>"
            html += "<td rowspan='1'>"+cols[2]+"</td>";
            html += "</tr>";
            $tbody.append(html);

        }

    }




</script>
</body>
</html>
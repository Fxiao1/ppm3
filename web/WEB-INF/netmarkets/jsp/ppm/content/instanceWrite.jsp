<%--
  Created by IntelliJ IDEA.
  User: Fxiao
  Date: 2019/6/21
  Time: 11:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <title>内容填写页面</title>
    <link rel="stylesheet" type="text/css" href="../static/bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="../static/dataTables.bootstrap.min.css"/>
    <script type="text/javascript" src="../static/jquery.min.js"></script>
    <script type="text/javascript" src="../static/bootstrap.js"></script>
    <script type="text/javascript" src="../static/bootstrap-treeview.min.js"></script>
    <script type="text/javascript" src="../static/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="../static/dataTables.bootstrap.min.js"></script>
    <script type="text/javascript" src="../static/content/content.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/netmarkets/jsp/ppm/static/My97DatePicker/My97DatePicker/WdatePicker.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/netmarkets/jsp/ppm/static/modifyBtnClass.js" >
    </script>
    <script type="text/javascript">

        $(function() {
            var productId=$("#hideInfo").find("input[name=productId]").val();
            //首先检查一下是否已有数据了
            getDataInstance();
            bindBtn();
            bindEven();
        })

		//默认时间为当前时间ln
        function today(){
            var today=new Date();
            var h=today.getFullYear();
            var m=today.getMonth()+1;
            var d=today.getDate();
            var hh=today.getHours();
            var mm=today.getMinutes();
            var ss=today.getSeconds();
            m= m<10?"0"+m:m;
            d= d<10?"0"+d:d;
            hh = hh < 10 ? "0" + hh:hh;
            mm = mm < 10 ? "0" +  mm:mm;
            ss = ss < 10 ? "0" + ss:ss;
            return h+"-"+m+"-"+d+" "+hh+":"+mm+":"+ss;
        }

        function getDataInstance(){
            var formLogo='<%=request.getParameter("formLogo")%>';
            var checkType = $("#checkType").val();
            $.getJSON(
                "/Windchill/servlet/Navigation/datainstance",
                {"actionName":"getByCheckType","logo":formLogo,"checkType":checkType},
                function (result) {
                    if(result.data.length==0){
                        //说明当前页面是新增
                        getFormItem(formLogo,checkType);
                    }else{
                        //说明当前页面是修改
                        $("#hideInfo").find("input[name=pageType]").val("update");
                        $("#hideInfo").find("input[name=allDataInstance]").val(JSON.stringify(result.data));
                        showFormInfo(result.data[0]);
                        initMyTable2(result.data);
                        initMyTable3(result.data);
                    }
                }
            )
        }
        function bindBtn() {
            $("#confirmBtn").unbind("click").click(function(){
                // 检查表单数字类型合法性
                if(examine()==1){
                    return false;
                }
                calculation();
            });
        }
        /**
         * 检查表单
         */
        function examine() {
            //检查数字类型的合法性
            var numberInput=$("#form1").find("input[type=number]");
            var hasError=false;
            $.each(numberInput,function(i,n){
                var num=$(n).val();
                num=num==""?"0":num;
                reg=/^\d+$/;
                if(reg.test(num)){
                    if($(n).hasClass("productCount")&&num=="0"){
                        $(n).closest("td").addClass("has-error");
                        $(n).prop("title","本工序输入产品数必须大于零");
                        hasError=true;
                    }else{
                        $(n).closest("td").removeClass("has-error");
                        $(n).prop("title","");
                    }

                }else{
                    $(n).closest("td").addClass("has-error");
                    $(n).prop("title","该数字框内必须是正整数");
                    hasError=true;
                }
            });
            if(hasError){
                alert("检查发现数字输入框有部分错误，具体错误原因，请将鼠标移至红色框上查看")
                return 1;
            }else{
                return 0;
            }
        }
        //后台计算一些数值
        function calculation(){
            var formData=$("#form1").serializeArray();
            $("#log").text(JSON.stringify(formData));
            //actionName直接作为参数好像还解析不出来，不知道怎么回事。目前暂且先拼接到url中来
            $.ajax({
                url:'/Windchill/servlet/Navigation/datainstance?actionName=calculation',
                type:'post',
                dataType:'json',
                data:formData,
                success:function (result) {
                    if(result.success){
                        $("#hideInfo").find("input[name=allDataInstance]").val(JSON.stringify(result.data));
                        initMyTable2(result.data);
                        $("#formRow").addClass("hide");
                        $("#infoView").removeClass("hide");
                    }else{
                        alert(result.message);
                        updateProductCount();
                        setTimeout(function(){
                            var currentProductCounts=$("#instanceDataTable3").find("input.currentProductCount");
                            $.each(currentProductCounts,function(i,n){
                                maximum($(n));
                            })
                        },1000);
                    }
                },
                error:function (a, b) {
                    alert(b);
                }
            })
        }

        function bindEven() {
        }
        /**
         * 获取form表单定义，根据这个数据去动态生成表格
         */
        function getFormItem(formLogo,checkType){
            $.ajax({
                url:'/Windchill/servlet/Navigation/form',
                type:'get',
                data:{"actionName":"get","logo":formLogo,"checkType":checkType},
                dataType:'json',
                success:function (result) {
                    if(result.success){
                        showFormInfo(result.data[0]);
                        initMyTable2(result.data);
                        initMyTable3(result.data)
                    }else{
                        alert(result.message)
                    }
                },
                error:function (a, b) {
                    alert(b);
                }
            });

        }

        /**
         * 初始化“#instanceDataTable2”这张表
         * @param resultData “ext.modular.form.FormEntity”对象列表，或者
         * “ext.modular.datainstance.DatainstanceEntity”对象列表
         */
        function initMyTable2(resultData){
            $("#instanceDataTable2>tbody").html("");
            //封装数据成为一个对象“a”，该对象的key为工序名,该工序下所有的数据为value.也就是在前台对这个蛋疼的数据进行分组
            //对象形状为{"工序名1":[item1,item2],"工序名2":[item3,item4]}
            var a={};
            var defectNumberCounts={};
            $.each(resultData,function(i,n){
                if(a[n.procedureName]){//旧工序
                    a[n.procedureName].push(n);
                    //计算检验特性检出的缺陷总数
                    defectNumberCounts[n.procedureName]=
                        defectNumberCounts[n.procedureName]
                        +
                        (typeof(n.defectNumberItem)=="undefined"?0:parseInt(n.defectNumberItem));
                }else{//新工序
                    var b=[];
                    b.push(n);
                    a[n.procedureName]=b;
                    //计算检验特性检出的缺陷总数
                    defectNumberCounts[n.procedureName]=
                        typeof(n.defectNumberItem)=="undefined"?0:parseInt(n.defectNumberItem);
                }
            })

            //先遍历key,再遍历后面的数组
            var indexNum=0;
            var _input=$("<input>").addClass("form-control").css("width","100%");
            $.each(a,function(j,k){
                var le=k.length;
                if(le>1){
                    $.each(k,function(m,n){
                        if(m==0){
                            var _row=$('<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>');
                            _row.children("td:eq(0)").text(++indexNum).prop("rowspan",le);
                            _row.children("td:eq(1)").text(n.procedureName).prop("rowspan",le);
                            _row.children("td:eq(2)").prop("rowspan",le).html(
                                '<a href="javascript:;" onclick="toWirte()">填写</a>'
                            ).addClass(n.procedureName+"_productCount");
                            _row.children("td:eq(3)").text(n.characName);
                            _row.children("td:eq(4)").text(n.characQuantity);
                            _row.children("td:eq(5)").append(
                                _input.clone().prop({"type":"hidden","name":"procedureName"})
                                    .val(n.procedureName)
                            ).text(n.characteristicsTotal);
                            _row.children("td:eq(6)").text(n.defectNumber).addClass("qxNumber");
                            _row.children("td:eq(7)").text(n.kj);
                            _row.children("td:eq(8)").prop("rowspan",le).addClass(n.procedureName+"_proceQx")
                                .text(defectNumberCounts[n.procedureName]);

                            _row.children("td:eq(9)").prop("rowspan",le).text(typeof(n.procedurePpm)=="undefined"?0:n.procedurePpm);
                            $("#instanceDataTable2>tbody").append(_row);
                        }else{
                            var _row=$('<tr><td></td><td></td><td></td><td></td><td></td></tr>');
                            _row.children("td:eq(0)").text(n.characName);
                            _row.children("td:eq(1)").text(n.characQuantity);
                            _row.children("td:eq(2)").append(
                                _input.clone().prop({"type":"hidden","name":"procedureName"})
                                    .val(n.procedureName)
                            ).text(n.characteristicsTotal);
                            _row.children("td:eq(3)").text(n.defectNumber).addClass("qxNumber");
                            _row.children("td:eq(4)").text(n.kj);
                            $("#instanceDataTable2>tbody").append(_row);
                        }
                    });
                }else{
                    n=k[0];
                    var _row=$('<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>');
                    _row.children("td:eq(0)").text(++indexNum);
                    _row.children("td:eq(1)").text(n.procedureName);
                    _row.children("td:eq(2)").prop("rowspan",le).html(
                        '<a href="javascript:;" onclick="toWirte()">填写</a>'
                    ).addClass(n.procedureName+"_productCount");
                    _row.children("td:eq(3)").text(n.characName);
                    _row.children("td:eq(4)").text(n.characQuantity);
                    _row.children("td:eq(5)").append(
                        _input.clone().prop({"type":"hidden","name":"procedureName"})
                            .val(n.procedureName)
                    ).text(n.characteristicsTotal);
                    _row.children("td:eq(6)").text(n.defectNumber).addClass("qxNumber");
                    _row.children("td:eq(7)").text(n.kj);
                    _row.children("td:eq(8)").text(n.defectNumberItem?n.defectNumberItem:"0").addClass(n.procedureName+"_proceQx");
                    _row.children("td:eq(9)").text(n.procedurePpm?n.procedurePpm:"0");
                    $("#instanceDataTable2>tbody").append(_row);
                }
            })
            $("#instanceDataTable2>tbody").find("td").addClass("text-center");
        }

        /**
         * 初始化“#instanceDataTable3”这个table
         * @param resultData “ext.modular.form.FormEntity”对象列表，或者
         * “ext.modular.datainstance.DatainstanceEntity”对象列表
         */
        function initMyTable3(resultData) {
            $("#instanceDataTable3>tbody").html("");
            if(resultData.length==0){
               return false;
            }
            var formItem=resultData[0];
            //放入一些信息，在提交的时候需要传入后台
            $("#form1").find("input[name=quantity]").val(formItem.quantity);
            //产品批次
            $("#form1").prepend($("<input>").prop({"type":"hidden","name":"batch","value":formItem.batch}));
            //类别
            $("#form1").prepend($("<input>").prop({"type":"hidden","name":"category","value":formItem.category}));
            //整机、模件、线缆名称
            $("#form1").prepend($("<input>").prop({"type":"hidden","name":"moduleName","value":formItem.moduleName}));
            //产品阶段
            $("#form1").prepend($("<input>").prop({"type":"hidden","name":"ProductPhase","value":formItem.ProductPhase}));
          //模版名
            $("#form1").prepend($("<input>").prop({"type":"hidden","name":"templateName","value":formItem.templateName}));
          //模版id
            $("#form1").prepend($("<input>").prop({"type":"hidden","name":"templateId","value":formItem.templateId}));

            //封装数据成为一个对象，该对象的key为工序名,该工序下所有的数据为value.也就是在前台对这个蛋疼的数据进行分组
            //对象形状为{"工序名1":[item1,item2],"工序名2":[item3,item4]}
            var a={};
            $.each(resultData,function(i,n){
                if(a[n.procedureName]){
                    a[n.procedureName].push(n)
                }else{
                    var b=[];
                    b.push(n);
                    a[n.procedureName]=b;
                }

            });
            //先遍历key,再遍历后面的数组
            //工序数目
            var indexNum=0;
            //表格真实行数
            var rowNumber=0;
            //当前页面正在进行什么操作？
            var pageType=$("#hideInfo").find("input[name=pageType]").val();
            var _input=$("<input>").addClass("form-control").css("width","100%");
            var checkTypeObj={
            		 "DZZJ":"电装自检",
                     "DZHJ":"电装互检",
                     "DZJY":"电装检验",
                     "TSJY":"调试检验",
                     "TSZJ":"调试自检",
                     "DZJJ":"电装军检",
                     "TSJJ":"调试军检"
            }
            $.each(a,function(j,k){
                var le=k.length;
                //如果工序下面有特性
                if(le>1){
                    var procedureName,productCount,checkType,checkPerson,checkTime,kj,procedureId;
                    $.each(k,function(m,n){
                        //如果是当前工序的首行
                        if(m==0){
                            procedureName=n.procedureName;
                            procedureId=n.twId;
                            productCount=n.productCount;
                            checkType=n.checkType;
                            checkPerson=n.checkPerson;
                            checkTime=n.checkTime;
                            kj=n.kj;
                            var _row=$('<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td> <td class="hide Info"></td></tr>');
                            _row.children("td:eq(0)").text(++indexNum).prop("rowspan",le);
                            _row.children("td:eq(1)").prop("rowspan",le).append(
                                _input.clone()
                                    .attr({"readonly":"readonly","type":"text","name":"procedureName_"+rowNumber,"value":n.procedureName})

                            ).append(
                                _input.clone()
                                    .prop({
                                        "type":"hidden","name":"procedureId_"+rowNumber
                                    }).val(n.twId).addClass("procedureId")
                            );
                            var productCount=0;
                            var maxPruduct=0;
                            if(n.productCount){
                                productCount=n.productCount;
                            }
                            maxPruduct=n.quantity-productCount;
                            _row.children("td:eq(2)").prop("rowspan",le).append(
                                _input.clone().prop({
                                    "type":"hidden",
                                    "value":productCount
                                }).addClass("initialProductCount")
                            ).append(
                                _input.clone()
                                    .prop({"type":"hidden",
                                    	"value":productCount,
                                    	"min":0})
                                    .addClass("productCount")
                            ).append(
                                _input.clone().attr({
                                    "type":"number",
                                    "min":0,
                                    "value":0,
                                    "max":maxPruduct,
                                    "name":"productCount_"+rowNumber,
                                    "onChange":"maximum(this)"
                                }).addClass("currentProductCount")
                            ).append(
                                $("<p></p>").text("最多可以输入"+maxPruduct+"个产品数")
                            );
                            _row.children("td:eq(3)").append(
                                _input.clone().attr(
                                    {"type":"text","name":"characQuantity_"+rowNumber,"value":n.characQuantity,
                                    "readonly":"readonly"}
                                )

                            );
                            _row.children("td:eq(4)").append(
                                _input.clone().attr(
                                    {"type":"text","name":"characName_"+rowNumber,"value":n.characName,
                                        "readonly":"readonly"}
                                )

                            );
                            _row.children("td:eq(5)").append(
                                _input.clone()
                                    .prop({
                                        "type":"number",
                                        "name":"defectNumber_"+rowNumber,
                                        "value":n.defectNumber,
                                        "min":0
                                    })
                                    .addClass("qxNumber")
                            ).append(
                                _input.clone().prop({"type":"hidden","name":"rowNumber","value":rowNumber})
                            );
                            _row.children("td:eq(6)").prop("rowspan",le).append(
                                _input.clone().attr({
                                    "type":"hidden",
                                    "name":"checkType_"+rowNumber,
                                   "value":n.checkType
                                })

                            ).append(
                                _input.clone().attr({
                                    "value":checkTypeObj[n.checkType],
                                    "readonly":"readonly"
                                })
                            );
                            _row.children("td:eq(7)").append(
                                _input.clone()
                                    .prop({"type":"text","name":"checkPerson_"+rowNumber,
                                    "value":n.checkPerson})
                            ).prop("rowspan",le);

                            var checkTimeInput=_input.clone()
                                .attr({
                                    "type":"text","name":"checkTime_"+rowNumber,"readonly":"readonly",
                                    "onclick":"WdatePicker({el:this,dateFmt:'yyyy-MM-dd HH:mm:ss'})"
                                });
                            if(n.checkTime){
                                checkTimeInput.val(n.checkTime);
                            }else{
								checkTimeInput.val(today());
							}
                            _row.children("td:eq(8)").prop("rowspan",le).append(
                                checkTimeInput
                            );

                            _row.children("td:eq(9)").append(
                                _input.clone().prop({"type":"hidden","name":"kj_"+rowNumber}).val(n.kj)
                            );
                            //ceshi
                            
                            
                            if(n.id&&pageType=="update"){
                                var dataItemId=_input.clone().prop({"type":"hidden","name":"dataItemIds_"+rowNumber}).val(n.id);
                                _row.children("td:eq(9)").append(dataItemId);
                            }
                            $("#instanceDataTable3>tbody").append(_row);
                            $("#form1").find("input[name=maxRowNumber]").val(rowNumber);
                            rowNumber++;
                        }else{
                            var _row=$('<tr><td></td><td></td><td></td><td class="hide Info"></td></tr>');
                            _row.children("td:eq(0)").append(
                                _input.clone().attr(
                                    {"type":"text","name":"characQuantity_"+rowNumber,"value":n.characQuantity,
                                        "readonly":"readonly"}
                                )

                            );
                            _row.children("td:eq(1)").append(
                                _input.clone().attr(
                                    {"type":"text","name":"characName_"+rowNumber,"value":n.characName,
                                        "readonly":"readonly"}
                                )

                            );
                            _row.children("td:eq(2)").append(
                                _input.clone()
                                    .prop({
                                        "type":"number",
                                        "name":"defectNumber_"+rowNumber,
                                        "value":n.defectNumber,
                                        "min":0
                                    })
                                    .addClass("qxNumber").text(n.defectNumber)
                            ).append(
                                _input.clone().prop({"type":"hidden","name":"rowNumber","value":rowNumber})
                            );

                            _row.children("td:eq(3)").append(
                                _input.clone().attr({"type":"hidden","name":"kj_"+rowNumber,"value":kj})
                            ).append(
                                _input.clone().attr({"type":"hidden","name":"checkTime_"+rowNumber,"value":checkTime})
                            ).append(
                                _input.clone().attr({"type":"hidden","name":"checkPerson_"+rowNumber,"value":checkPerson})
                            ).append(
                                _input.clone().attr({"type":"hidden","name":"checkType_"+rowNumber,"value":checkType})
                            ).append(
                                _input.clone().attr({"type":"hidden","name":"productCount_"+rowNumber,"value":productCount})
                            ).append(
                                _input.clone().attr({"type":"hidden","name":"procedureName_"+rowNumber,"value":procedureName})
                            ).append(
                                _input.clone().attr({"type":"hidden","name":"procedureId_"+rowNumber,"value":procedureId})
                            );

                            if(n.id&&pageType=="update"){
                                var dataItemId=_input.clone().prop({"type":"hidden","name":"dataItemIds_"+rowNumber}).val(n.id);
                                _row.children("td:eq(3)").append(dataItemId);
                            }
                            $("#instanceDataTable3>tbody").append(_row);
                            $("#form1").find("input[name=maxRowNumber]").val(rowNumber);
                            rowNumber++;
                        }
                    });
                }else{
                    n=k[0];
                    var _row=$('<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td> <td class="hide Info"></td></tr>');
                    _row.children("td:eq(0)").text(++indexNum);
                    _row.children("td:eq(1)").append(
                        _input.clone().attr({
                            "readonly":"readonly",
                            "type":"text",
                            "name":"procedureName_"+rowNumber,
                            "value":n.procedureName
                        })
                    ).append(
                        _input.clone()
                            .prop({
                                "type":"hidden","name":"procedureId_"+rowNumber
                            }).val(n.twId).addClass("procedureId")
                    );
                    var productCount=0;
                    var maxPruduct=0;
                    if(n.productCount){
                        productCount=n.productCount;
                    }
                    maxPruduct=n.quantity-productCount;
                    _row.children("td:eq(2)").append(
                        _input.clone().prop({
                            "type":"hidden",
                            "value":productCount
                        }).addClass("initialProductCount")
                    ).append(
                        _input.clone()
                            .prop({"type":"hidden",
                                "name":"productCount_"+rowNumber,
                                "value":productCount,
                                "min":0})
                            .addClass("productCount")
                    ).append(
                        _input.clone().attr({
                            "type":"number",
                            "min":0,
                            "value":0,
                            "max":maxPruduct,
                            "onChange":"maximum(this)"
                        }).addClass("currentProductCount")
                    ).append(
                        $("<p></p>").text("最多可以输入"+maxPruduct+"个产品数")
                    );
                    _row.children("td:eq(3)").append(
                        _input.clone().attr(
                            {"type":"text","name":"characQuantity_"+rowNumber,"value":n.characQuantity,
                                "readonly":"readonly"}
                        )

                    );
                    _row.children("td:eq(4)").append(
                        _input.clone().attr(
                            {"type":"text","name":"characName_"+rowNumber,"value":n.characName,
                                "readonly":"readonly"}
                        )

                    );
                    _row.children("td:eq(5)").append(
                        _input.clone()
                            .prop({
                                "type":"number",
                                "name":"defectNumber_"+rowNumber,
                                "value":n.defectNumber
                            })
                            .addClass("qxNumber")
                    ).append(
                        _input.clone().prop({"type":"hidden","name":"rowNumber","value":rowNumber})
                    );
                    _row.children("td:eq(6)").append(
                        _input.clone().attr({
                            "type":"hidden",
                            "name":"checkType_"+rowNumber,
                            "value":n.checkType
                        })
                    ).append(
                        _input.clone().attr({
                            "value":checkTypeObj[n.checkType],
                            "readonly":"readonly"
                        })
                    );
                    _row.children("td:eq(7)").append(
                        _input.clone()
                            .prop({
                                "type":"text",
                                "name":"checkPerson_"+rowNumber,
                                "value":n.checkPerson
                            })
                    );

                    var checkTimeInput=_input.clone()
                        .attr({
                            "type":"text","name":"checkTime_"+rowNumber,"readonly":"readonly",
                            "onclick":"WdatePicker({el:this,dateFmt:'yyyy-MM-dd HH:mm:ss'})"
                        });
                    if(n.checkTime){
                        checkTimeInput.val(n.checkTime);
                    }else{
                    	checkTimeInput.val(today());
                    }
                    _row.children("td:eq(8)").append(
                        checkTimeInput
                    );
                    _row.children("td:eq(9)").append(
                        _input.clone().prop({"type":"hidden","name":"kj_"+rowNumber}).val(n.kj)
                    );
                    if(n.id&&pageType=="update"){
                        var dataItemId=_input.clone().prop({"type":"hidden","name":"dataItemIds_"+rowNumber}).val(n.id);
                        _row.children("td:eq(9)").append(dataItemId);
                    }
                    $("#instanceDataTable3>tbody").append(_row);
                    $("#form1").find("input[name=maxRowNumber]").val(rowNumber);
                    rowNumber++;
                }
            })
            $("#instanceDataTable3>tbody").find("td").addClass("text-center");
        }

        /**
         * 更新产品数，如果有其他人输入了的话，该值会发生变化，所以必要时候需要更新
         * @param data datainstanceList
         */
        function updateProductCount(){
            var formLogo='<%=request.getParameter("formLogo")%>';
            var checkType = $("#checkType").val();
            $.getJSON(
                "/Windchill/servlet/Navigation/datainstance",
                {"actionName":"getByCheckType","logo":formLogo,"checkType":checkType},
                function (result) {
                    if(result.data.length!=0){
                        //在这里要去重，调用procedureIds.indexOf() 的方法
                        var procedureIds=[];
                        $.each(result.data,function(i,n){
                            var procedureId=n.twId;
                            if(procedureIds.indexOf(procedureId)<0){
                                procedureIds.push(procedureId);
                                //新的
                                $("#instanceDataTable3").find("input.procedureId[value="+procedureId+"]").closest("tr")
                                    .find("input.initialProductCount").val(n.productCount);
                            }
                        })
                    }else{
                        alert("发生错误，更新产品数的时候,未获取到后台数据，发生位置请看浏览器控制台");
                        console.error("调用updateProductCount（）方法的时候没有返回数据");
                    }
                }
            )
        }

        /**
         * 计算最大输入的产品数
         */
        function maximum(thisInput) {
            var procedureId=parseInt($(thisInput).closest("tr").find("input.procedureId").val());
            var formLogo='<%=request.getParameter("formLogo")%>';
            var checkType = $("#checkType").val();
            //已有的初始值
            var initialProductCount=parseInt($(thisInput).parent().find("input.initialProductCount").val());
            if(!initialProductCount){
                initialProductCount=0;
            }
            //最大生产总数
            var quantity=parseInt($("#formInfo").find("span[name=quantity]").text());
            var currentInput=0;
            if($(thisInput).val()){
                currentInput=parseInt($(thisInput).val());
            }
            //还能输入的产品数
            var maxPruduct=quantity-currentInput-initialProductCount;
            if(maxPruduct<0){
                $(thisInput).val(0);
                var errorInput=currentInput;
                currentInput=0;
                maxPruduct=quantity-currentInput-initialProductCount;
                $(thisInput).css({"border-color":"#ff0000"}).closest("td")
                    .find("p").css({"color":"#ff0000"})
                    .html("当前输入的产品数过多!最多可以输入"+maxPruduct+"个产品数,您输入了"+errorInput+"个");
            }else{
                $(thisInput).css({"border-color":"#cccccc"}).closest("td")
                    .find("p").css({"color":"#000000"})
                    .text("最多可以输入"+maxPruduct+"个产品数");
            }
            var productCountObj=$(thisInput).parent().find("input.productCount");
            productCountObj.val(initialProductCount+currentInput);
        }
		//数据回写
        function showFormInfo(formItem) {
            var formInfo=$("#formInfo");
			//检验类别转换
			var checkCategoryObj={
                    "ZJ":"整机",
                    "MJ":"模件",
                    "XL":"线缆"
                }
            var checkTypeObj={
                "DZZJ":"电装自检",
                "DZHJ":"电装互检",
                "DZJY":"电装检验",
                "TSJY":"调试检验",
                "TSZJ":"调试自检",
                "DZJJ":"电装军检",
                "TSJJ":"调试军检"
            }
            var category="",moduleName="",batch="",quantity=0,checkType="",productPhase="";
            if(formItem){
                category=checkCategoryObj[formItem.category];
                moduleName=formItem.moduleName;
                batch=formItem.batch;
                quantity=formItem.quantity;
                checkType=checkTypeObj[formItem.checkType];
                productPhase=formItem.ProductPhase
            }
            formInfo.find("span[name=category]").text(category);
            formInfo.find("span[name=moduleName]").text(moduleName);
            formInfo.find("span[name=batch]").text(batch);
            formInfo.find("span[name=quantity]").text(quantity);
            formInfo.find("span[name=checkType]").text(checkType);
            formInfo.find("span[name=ProductPhase]").text(productPhase);
        }
        function submitForm(){
            //“填写”操作之后，会将里面的数据拿到后台进行计算，得到计算结果之后的表单实体列表会返回json，并暂存到下面这个隐藏域里面，所以此时，
            // 只需要从该隐藏域中将json传回后台并作保存操作即可
            var dataInstanceListStr=$("#hideInfo").find("input[name=allDataInstance]").val();
            var _data={"dataInstanceList":dataInstanceListStr};
            //actionName直接作为参数好像还解析不出来，不知道怎么回事。目前暂且先拼接到url中来
            var _url="";
            var pageType=$("#hideInfo").find("input[name=pageType]").val();
            if(pageType=="update"){
                _url='/Windchill/servlet/Navigation/datainstance?actionName=update';
            }else{
                _url='/Windchill/servlet/Navigation/datainstance?actionName=add';
            }
            $.ajax({
                url:_url,
                type:'post',
                dataType:'json',
                data:_data,
                success:function (result) {
                    if(result.success){
                        // $("#formRow").addClass("hide");
                        //关闭当前页面
                        window.opener = null;
                        window.open('', '_self');
                        window.close();
                        // $('#form1').get(0).reset();
                    }else{
                        alert(result.message)
                    }
                },
                error:function (a, b) {
                    alert(b);
                }
            })
        }
        function formCancel() {
            $("#formRow").addClass("hide");
            $("#infoView").removeClass("hide");
        }
        function toWirte() {
            $("#formRow").removeClass("hide");
            $("#infoView").addClass("hide");
        }
    </script>
</head>
<body>
<div class="container-fluid">
    <div id="hideInfo" class="hide">
        <input type="hidden" name="productId" value="<%=request.getParameter("productId")%>">
        <input type="hidden" name="allDataInstance" >
        <input type="hidden" name="pageType" value="add">
    </div>
    <div class="row">
        <h3 class="text-center">检验数据填写</h3>
    </div>
    <div class="row" id="formInfo">
        <div class="col-md-9 col-sm-10">
            <div class="row">
                <div class="col-md-2"><label>产品型号:</label><span name="modalName"><%=request.getParameter("modalName2")%></span></div>
                <div class="col-md-2"><label>产品代号:</label><span name="productCode"><%=request.getParameter("productCode")%></span></div>
                <div class="col-md-2"><label>产品名称:</label><span name="productName"><%=request.getParameter("productName")%></span></div>
                <div class="col-md-2"><label>类别:</label><span name="category"></span></div>
                <div class="col-md-2"><label>整机/模件/线缆:</label><span name="moduleName"></span></div>
            </div>
            <div class="row">
                <div class="col-md-2"><label>生产批次:</label><span name="batch"></span></div>
                <div class="col-lg-2"><label>生产数量:</label><span name="quantity"></span></div>
                <div class="col-lg-2"><label>检验类型:</label><span name="checkType"></span></div>
                <div class="col-lg-2"><label>产品阶段:</label><span name="ProductPhase"></span></div>
            </div>
        </div>
        <div class="col-md-3 col-md-2">
            <select id="checkType" class="form-control" onchange="getDataInstance()">
                <option value="DZZJ" selected="selected">电装自检</option>
                <option value="DZJY">电装检验</option>
                <option value="TSJY">调试检验</option>
                <option value="DZHJ">电装互检</option>
                <option value="TSZJ">调试自检</option>
                <option value="DZJJ">电装军检</option>
                <option value="TSJJ">调试军检</option>
            </select>
        </div>
    </div>
    <div id="infoView" class="row">
        <table id="instanceDataTable2" class="table table-bordered table-striped">
            <thead><tr>
                <th>序号</th>
                <th>工序名称</th>
                <th>工序检验特性检验处的缺陷数</th>
                <th>工序检验特性名称</th>
                <th>工序检验特性数量</th>
                <th>工序检验特性总数</th>
                <th>本工序检验特性缺陷数</th>
                <th>严酷度加权系数</th>
                <th>该工序检验特性检出的缺陷总数</th>
                <th>本工序ppm</th>
            </tr></thead>
            <tbody>
            </tbody>
        </table>
        <button type="button" onclick="submitForm()" class="btn btn-info col-lg-offset-8">保存</button>
    </div>

    <div id="formRow" class="row hide" style="margin-top:15px;">
    	<div>
    		<div style="float: left;">
	        	<h3>检验明细录入</h3>
	        </div>
         </div>
        <form id="form1">
            <input type="hidden" name="logo" value="<%=request.getParameter("formLogo")%>">
            <input type="hidden" name="productId" value="<%=request.getParameter("productId")%>">
            <input type="hidden" name="maxRowNumber" >
            <input type="hidden" name="quantity" >
            <table id="instanceDataTable3" class="table table-bordered table-striped">
                <thead><tr>
                    <th>序号</th>
                    <th>工序名称</th>
                    <th>本工序输入产品数</th>
                    <th>工序检验特性检验特性数</th>
                    <th>工序检验特性名称</th>
                    <th>工序检验特性检出的缺陷数</th>
                    <th>检验类型</th>
                    <th>检验人</th>
                    <th>检验时间</th>
                </tr></thead>
                <tbody>
                </tbody>
            </table>
        </form>
        <div class="col-lg-offset-8">
            <button class="btn btn-info" type="button" id="confirmBtn" >确定</button>
            <button class="btn btn-info" type="button" onclick="formCancel()">取消</button>
        </div>

    </div>

</div>
<div class="hide" id="log"></div>
</body>
</html>
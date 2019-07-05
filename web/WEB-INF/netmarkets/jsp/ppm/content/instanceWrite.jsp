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
    <script type="text/javascript">

        $(function() {
            var productId=$("#hideInfo").find("input[name=productId]").val();
            var formLogo='<%=request.getParameter("formLogo")%>';
            //首先检查一下是否已有数据了
            getDataInstance(formLogo);

            bindBtn();
            bindEven();
        })

        function getDataInstance(formLogo){
            $.ajax({
                url:'/Windchill/servlet/Navigation/datainstance',
                type:'get',
                data:{"actionName":"getByFormMark","logo":formLogo},
                dataType:'json',
                success:function (result) {
                    if(result.success){
                        if(result.data.length==0){
                            //说明当前页面是新增
                            getFormItem();
                        }else{
                            //说明当前页面是修改
                            $("#hideInfo").find("input[name=pageType]").val("update");
                            $("#hideInfo").find("input[name=allDataInstance]").val(JSON.stringify(result.data));
                            showFormInfo(result.data[0]);
                            initMyTable2(result.data);
                            initMyTable3(result.data);
                        }
                    }else{
                        alert(result.message)
                    }
                },
                error:function (a, b) {
                    alert(b);
                }
            });

        }
        function bindBtn() {
            $("#confirmBtn").unbind("click").click(function(){
                if(examine()==1){
                    return false;
                }
                calculation();
                $("#formRow").addClass("hide");
                $("#infoView").removeClass("hide");


            });
        }
        /**
         * 检查表单
         */
        function examine() {
            var numberInput=$("#form1").find("input[type=number]");
            var hasError=false;
            $.each(numberInput,function(i,n){
                var num=$(n).val();
                num=num==""?"0":num;
                reg=/^\d+$/;
                if(reg.test(num)){
                    $(n).closest("td").removeClass("has-error");
                    $(n).prop("title","");
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
                    }else{
                        alert(result.message)
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
        function getFormItem() {
            var formLogo="<%=request.getParameter("formLogo")%>";
            $.ajax({
                url:'/Windchill/servlet/Navigation/form',
                type:'get',
                data:{"actionName":"get","logo":formLogo},
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

            })

            //先遍历key,再遍历后面的数组
            var indexNum=0;
            var _input=$("<input>").addClass("form-control").css("width","100%");
            $.each(a,function(j,k){
                //特性总数相加值
                var defectNumberCount=0;
                //特性总数相加值将要投放的位置的工序名
                var _className2="";
                var _className3="";
                //工序特性相加
                var procePpmCount=0;
                var le=k.length;
                if(le>1){
                    $.each(k,function(m,n){
                        if(n.defectNumberItem) defectNumberCount+=n.defectNumberItem;
                        if(n.characPPM) procePpmCount+=n.characPPM;
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
                                .addClass("defectNumberCount"+indexNum).text(n.defectNumberItem);
                            _className2="defectNumberCount"+indexNum;
                            _row.children("td:eq(9)").prop("rowspan",le)
                                .addClass("_procePpm"+indexNum);
                            _className3="_procePpm"+indexNum;
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
                    $("#instanceDataTable2>tbody").find("td."+_className2).text(defectNumberCount);
                    $("#instanceDataTable2>tbody").find("td."+_className3).text(procePpmCount);
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
                    _row.children("td:eq(9)").text(n.characPPM?n.characPPM:"0");
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
            var indexNum=0;
            var rowNumber=0;
            //当前页面正在进行什么操作？
            var pageType=$("#hideInfo").find("input[name=pageType]").val();
            var _input=$("<input>").addClass("form-control").css("width","100%");
            var _select=$("<select></select>").addClass("form-control").css("width","100%");
            var _option=$("<option></option>");
            _select
                .append(_option.clone().css("display", "none"))
                .append(_option.clone().text("自检").val("ZiJ"))
                .append(_option.clone().text("互检").val("HuJ"))
                .append(_option.clone().text("专检").val("ZhJ"))
                .append(_option.clone().text("军检").val("JunJ"));
            $.each(a,function(j,k){
                var le=k.length;
                //如果工序下面有特性
                if(le>1){
                    $.each(k,function(m,n){
                        //如果是当前工序的首行
                        if(m==0){
                            var _row=$('<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td> <td class="hide Info"></td></tr>');
                            _row.children("td:eq(0)").text(++indexNum).prop("rowspan",le);
                            _row.children("td:eq(1)").text(n.procedureName).prop("rowspan",le).append(
                                _input.clone()
                                    .prop({"type":"hidden","name":"procedureName"}).val(n.procedureName)

                            );
                            _row.children("td:eq(2)").prop("rowspan",le).append(
                                _input.clone()
                                    .prop({"type":"number","name":n.procedureName+".productCount","value":n.productCount})
                                    .addClass(n.procedureName+"_productCount")
                            );
                            _row.children("td:eq(3)").append(
                                _input.clone().attr(
                                    {"type":"text","name":n.procedureName+".characQuantity","value":n.characQuantity,
                                    "readonly":"readonly"}
                                )

                            );
                            _row.children("td:eq(4)").append(
                                _input.clone().attr(
                                    {"type":"text","name":n.procedureName+".characName","value":n.characName,
                                        "readonly":"readonly"}
                                )

                            );
                            _row.children("td:eq(5)").append(
                                _input.clone()
                                    .prop({
                                        "type":"number",
                                        "name":n.procedureName+"_defectNumber",
                                        "value":n.defectNumber
                                    })
                                    .addClass("qxNumber")
                            ).append(
                                _input.clone().prop({"type":"hidden","name":"rowNumber","value":rowNumber++})
                            );
                            var temp_select=_select.clone().prop("name",n.procedureName+".checkType");
                            if(n.checkType){
                                temp_select.val(n.checkType);
                            }
                            _row.children("td:eq(6)").prop("rowspan",le).append(
                                temp_select

                            );
                            _row.children("td:eq(7)").append(
                                _input.clone()
                                    .prop({"type":"text","name":n.procedureName+".checkPerson",
                                    "value":n.checkPerson})
                            ).prop("rowspan",le);

                            var checkTimeInput=_input.clone()
                                .prop({"type":"datetime-local","name":n.procedureName+".checkTime"});
                            if(n.checkTime){
                                var checkTimeStr=n.checkTime.replace(" ","T");
                                checkTimeInput.val(checkTimeStr);
                            }
                            _row.children("td:eq(8)").prop("rowspan",le).append(
                                checkTimeInput
                            );

                            _row.children("td:eq(9)").append(
                                _input.clone().prop({"type":"hidden","name":n.procedureName+"_kj"}).val(n.kj)
                            );
                            if(n.id&&pageType=="update"){
                                var dataItemId=_input.clone().prop({"type":"hidden","name":"dataItemIds"}).val(n.id);
                                _row.children("td:eq(9)").append(dataItemId);
                            }
                            $("#instanceDataTable3>tbody").append(_row);
                        }else{
                            var _row=$('<tr><td></td><td></td><td></td> <td class="hide Info"></td></tr>');
                            _row.children("td:eq(0)").append(
                                _input.clone().attr(
                                    {"type":"text","name":n.procedureName+".characQuantity","value":n.characQuantity,
                                        "readonly":"readonly"}
                                )

                            );
                            _row.children("td:eq(1)").append(
                                _input.clone().attr(
                                    {"type":"text","name":n.procedureName+".characName","value":n.characName,
                                        "readonly":"readonly"}
                                )

                            );
                            _row.children("td:eq(2)").append(
                                _input.clone()
                                    .prop({
                                        "type":"number",
                                        "name":n.procedureName+"_defectNumber",
                                        "value":n.defectNumber
                                    })
                                    .addClass("qxNumber").text(n.defectNumber)
                            ).append(
                                _input.clone().prop({"type":"hidden","name":"rowNumber","value":rowNumber++})
                            );
                            _row.children("td:eq(3)").append(
                                _input.clone().prop({"type":"hidden","name":n.procedureName+"_kj"}).val(n.kj)
                            );
                            if(n.id&&pageType=="update"){
                                var dataItemId=_input.clone().prop({"type":"hidden","name":"dataItemIds"}).val(n.id);
                                _row.children("td:eq(3)").append(dataItemId);
                            }
                            $("#instanceDataTable3>tbody").append(_row);
                        }
                    });
                }else{
                    n=k[0];
                    var _row=$('<tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td> <td class="hide Info"></td></tr>');
                    _row.children("td:eq(0)").text(++indexNum);
                    _row.children("td:eq(1)").text(n.procedureName).append(
                        _input.clone()
                            .prop({"type":"hidden","name":"procedureName"}).val(n.procedureName)

                    );
                    _row.children("td:eq(2)").append(
                        _input.clone()
                            .attr({
                                "type":"number",
                                "name":n.procedureName+".productCount",
                                "value":n.productCount
                            })
                            .addClass(n.procedureName+"_productCount")
                    );
                    _row.children("td:eq(3)").append(
                        _input.clone().attr(
                            {"type":"text","name":n.procedureName+".characQuantity","value":n.characQuantity,
                                "readonly":"readonly"}
                        )

                    );
                    _row.children("td:eq(4)").append(
                        _input.clone().attr(
                            {"type":"text","name":n.procedureName+".characName","value":n.characName,
                                "readonly":"readonly"}
                        )

                    );
                    _row.children("td:eq(5)").append(
                        _input.clone()
                            .prop({
                                "type":"number",
                                "name":n.procedureName+"_defectNumber",
                                "value":n.defectNumber
                            })
                            .addClass("qxNumber")
                    ).append(
                        _input.clone().prop({"type":"hidden","name":"rowNumber","value":rowNumber++})
                    );
                    var temp_select=_select.clone().prop("name",n.procedureName+".checkType");
                    if(n.checkType){
                        temp_select.val(n.checkType);
                    }
                    _row.children("td:eq(6)").append(temp_select);
                    _row.children("td:eq(7)").append(
                        _input.clone()
                            .prop({
                                "type":"text",
                                "name":n.procedureName+".checkPerson",
                                "value":n.checkPerson
                            })
                    );

                    var checkTimeInput=_input.clone()
                        .prop({"type":"datetime-local","name":n.procedureName+".checkTime"});
                    if(n.checkTime){
                        var checkTimeStr=n.checkTime.replace(" ","T");
                        checkTimeInput.val(checkTimeStr);
                    }
                    _row.children("td:eq(8)").append(
                        checkTimeInput
                    );
                    _row.children("td:eq(9)").append(
                        _input.clone().prop({"type":"hidden","name":n.procedureName+"_kj"}).val(n.kj)
                    );
                    if(n.id&&pageType=="update"){
                        var dataItemId=_input.clone().prop({"type":"hidden","name":"dataItemIds"}).val(n.id);
                        _row.children("td:eq(9)").append(dataItemId);
                    }
                    $("#instanceDataTable3>tbody").append(_row);
                }
            })
            $("#instanceDataTable3>tbody").find("td").addClass("text-center");
        }


        function showFormInfo(formItem) {
            var formInfo=$("#formInfo");
            formInfo.find("span[name=category]").text(formItem.category);
            formInfo.find("span[name=moduleName]").text(formItem.moduleName);
            formInfo.find("span[name=batch]").text(formItem.batch);
            formInfo.find("span[name=quantity]").text(formItem.quantity);
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
                        history.go(-1);
                        $('#form1').get(0).reset();
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
            if(confirm("确定放弃所填写的数据？")){
                $("#formRow").addClass("hide");
                $("#infoView").removeClass("hide");
            }
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
    <div id="infoView" class="row">
        <div class="col-lg-12">
            <div class="row">
                <h3 class="text-center">检验数据填写</h3>
            </div>
            <div class="row" id="formInfo">
                <div class="col-md-1">产品型号:<span name="modalName"><%=request.getParameter("modalName2")%></span></div>
                <div class="col-md-2">产品代号:<span name="productCode"><%=request.getParameter("productCode")%></span></div>
                <div class="col-md-2">产品名称:<span name="productName"><%=request.getParameter("productName")%></span></div>
                <div class="col-md-1">类别:<span name="category"></span></div>
                <div class="col-md-2">整机/模件/线缆:<span name="moduleName"></span></div>
                <div class="col-md-1">生产批次:<span name="batch"></span></div>
                <div class="col-lg-1">生产数量:<span name="quantity"></span></div>
            </div>
            <div class="row">
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
            </div>
            <div class="row">
                <button type="button" onclick="submitForm()" class="btn btn-info col-lg-offset-8">保存</button>
            </div>
        </div>
    </div>

    <div id="formRow" class="row hide" style="margin-top:15px;">
        <h3>检验明细录入</h3>
        <form id="form1">
            <input type="hidden" name="logo" value="<%=request.getParameter("formLogo")%>">
            <input type="hidden" name="productId" value="<%=request.getParameter("productId")%>">
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

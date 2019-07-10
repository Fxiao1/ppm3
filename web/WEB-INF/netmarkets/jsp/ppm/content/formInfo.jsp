<%--
  Created by IntelliJ IDEA.
  User: Fxiao
  Date: 2019/6/17
  Time: 19:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>检验内容配置</title>
        <link rel="stylesheet" type="text/css" href="../static/bootstrap.css"/>
        <script type="text/javascript" src="../static/jquery.min.js"></script>
        <script type="text/javascript" src="../static/bootstrap.js"></script>
        <script type="text/javascript" >
            $(function () {
                //纠错，隐藏域的一些基础信息的纠错
                errorCorrection();

                getAllTemolate();
                bindBtn();
                bindEven();
                getFormData();
            })
            //纠错，隐藏域的一些基础信息的纠错
            function errorCorrection() {
                $.each($("#hideInfo").find("input"),function(i,n){
                    var _value=$(n).val();
                    if(_value=="null"){
                        $(n).val("");
                    }
                })
            }
            //如果有form标识，则获取当前form的信息
            function getFormData() {
                var formLogo=$("#hideInfo").find("input[name=formLogo]").val();
                if(!formLogo||formLogo=="null"){
                    return false;
                }
                var productId=$("#hideInfo").find("input[name=productId]").val()
                $.ajax({
                    url:'/Windchill/servlet/Navigation/form?actionName=getItemByProIdLogo',
                    type:'get',
                    data:{"productId":productId,"logo":formLogo},
                    dataType:'json',
                    success:function (result) {
                        if(result.success){
                            updataTabaleInit(result.data);
                        }else{
                            alert(result.message)
                        }
                    },
                    error:function (a, b) {
                        alert(b);
                    }
                })
            }

            function updataTabaleInit(data) {
                $("#tempForm").find("tbody").find("tr").remove();
                var num=1;
                var orderNum=50;
                var batch=data[0].batch;
                var quantity=data[0].quantity;
                var moduleName=data[0].moduleName;
                var category=data[0].category;
                $("#myFormEntity").find("input[name=batch]").attr("value",batch);
                $("#myFormEntity").find("input[name=quantity]").attr("value",quantity);
                $("#myFormEntity").find("input[name=moduleName]").attr("value",moduleName);
                $("#myFormEntity").find("select[name=category]").val(category);
                $.each(data,function (i,n) {
                    var _trStr='<tr><td></td><td></td><td></td><td><input type="number"class="form-control"></td><td><input type="number"class="form-control"></td><td></td><td></td><td></td></tr>';
                    var _tr=$(_trStr);
                    _tr.find("td:eq(0)").text(num++);
                    _tr.find("td:eq(1)").text(n.procedureName);
                    _tr.find("td:eq(2)").text(n.characName);
                    _tr.find("td:eq(3)").find('input').val(n.characQuantity);
                    _tr.find("td:eq(4)").find('input').val(n.kj);
                    _tr.find("td:eq(5)").text(n.twId).addClass("hide");
                    _tr.find("td:eq(6)").addClass("hide");
                    _tr.find("td:eq(7)").text(orderNum++).addClass("hide");
                    $("#tempForm>tbody").append(_tr);
                })
            }

            /**
             * 获取所有的模板信息，遍历到“填写模板”这个下拉框里面去
             */
            function getAllTemolate() {
                $.ajax({
                    url:"/Windchill/servlet/Navigation/template?actionName=get",
                    type:'get',
                    dataType:"json",
                    success:function (result) {
                        if(result.success){
                            var _optionFirst=$("<option></option>").css("display","none");
                            $("#modelList").append(_optionFirst)
                            $.each(result.data,function (i,n) {
                                var _option=$("<option></option>").text(n.name).prop("value",n.id);
                                $("#modelList").append(_option);
                            })
                        }else{
                            alert(result.message)
                        }
                    },
                    error:function (a, b) {
                        alert(b);
                    }
                });
            }
            //获取工序及其工序特性
            function getProcedure(templateId) {
                $.ajax({
                    url:"/Windchill/servlet/Navigation/procedure?actionName=getByTemplate&templateId="+templateId,
                    type:'get',
                    dataType:"json",
                    success:function (result) {
                        if(result.success){
                            var procedureList=result.data;
                            tableInit(procedureList);
                        }else{
                            alert(result.message)
                        }
                    },
                    error:function (a, b) {
                        alert(b);
                    }
                });
            }
            //表单表的初始化
            function tableInit(procedureList) {
                $("#tempForm").find("tbody").find("tr").remove();
                var num=1;
                var orderNum=50;
                $.each(procedureList,function (i,n) {
                    $.each(n.characList,function (j,m) {
                        var _trStr='<tr><td></td><td></td><td></td><td><input type="number"class="form-control" min=0></td><td><input type="number"class="form-control" min=0></td><td></td><td></td><td></td></tr>';
                        var _tr=$(_trStr);
                        _tr.find("td:eq(0)").text(num++);
                        _tr.find("td:eq(1)").text(n.name);
                        _tr.find("td:eq(2)").text(m.name);
                        _tr.find("td:eq(3)").find('input').val(m.total);
                        _tr.find("td:eq(4)").find('input').val(m.coefficient);
                        _tr.find("td:eq(5)").text(n.id).addClass("hide");
                        _tr.find("td:eq(6)").text(m.id).addClass("hide");
                        _tr.find("td:eq(7)").text(m.id).text(orderNum++).addClass("hide");
                        $("#tempForm").find("tbody").append(_tr);
                    });
                })
            }
            //自定义form提交
            function formSubmit() {
                //产品id
                var productId=$("input[name=productId]").val();
                //生产批次
                var batch=$("input[name=batch]").val();
                //生产数量
                var quantity=$("input[name=quantity]").val();
                if(!quantity){
                    quantity=0;
                }
                //类别
                var category=$("select[name=category]").find("option:selected").val();
                //模件名称
                var _data=[];
                var moduleName=$("input[name=moduleName]").val();

                var charaList=$("#tempForm>tbody>tr");
                var formLogo=$("#hideInfo").find("input[name=formLogo]").val();
                var _url="";
                if(formLogo){
                    _url="/Windchill/servlet/Navigation/form?actionName=update";
                }else{
                    _url="/Windchill/servlet/Navigation/form?actionName=post";
                }

                $.each(charaList,function (i, n) {
                    var chrarc=$(n);
                    var itemData={};
                    itemData.productId=productId;
                    itemData.moduleName=moduleName;
                    itemData.batch=batch;
                    itemData.quantity=quantity;
                    itemData.category=category;
                    itemData.twId=chrarc.find("td:eq(5)").text();
                    itemData.procedureName=chrarc.find("td:eq(1)").text();
                    itemData.characName=chrarc.find("td:eq(2)").text();
                    itemData.characQuantity=chrarc.find("td:eq(3)>input").val();
                    itemData.kj=chrarc.find("td:eq(4)>input").val();
                    itemData.ppmOrder=chrarc.find("td:eq(7)").text();
                    if(formLogo){
                        itemData.logo=formLogo;
                    }
                    _data.push(itemData);
                });
                var dataJsonStr=JSON.stringify(_data);
                $.ajax({
                    url:_url,
                    type:'post',
                    data:{"formList":dataJsonStr},
                    dataType:'json',
                    success:function (result) {
                        if(result.success){
                            window.location.href="/Windchill/netmarkets/jsp/ppm/content/content.jsp";
                        }else{
                            alert(result.message)
                        }
                    },
                    error:function (a, b) {
                        alert(b);
                    }
                })


            }
            //获取所有的工序，增加到下拉框里面
            function addProcedureList(defaultOptionValue,defaultOptionName) {
                $.ajax({
                    url:"/Windchill/servlet/Navigation/procedure?actionName=get",
                    type:'get',
                    dataType:'json',
                    success:function (result) {
                        if(result.success){
                            var procedureSelect=$("#chrarcForm").find("select[name=charaList]");
                            procedureSelect.find("option:not(:first)").remove();
                            var hasFindDefaultOption=false;
                            $.each(result.data,function (i, n) {
                                var _option=null;
                                //如果调用方设置了默认值，且在此之前还没有找到与默认name相同的,且遍历到当前对象的name与默认值name相同
                                if(defaultOptionValue&&!hasFindDefaultOption&&n.name==defaultOptionName){
                                    hasFindDefaultOption=true;
                                    if(n.id==defaultOptionValue){
                                        //添加option，并设置为默认值
                                        _option=$("<option></option>").prop({"value":n.id,"selected":"selected"})
                                            .text(n.name);
                                    }else{
                                        //将当前选项设置为不可选，然后加入默认值
                                        var _option2=$("<option></option>").text(n.name)
                                            .prop("value",n.id).css("display","none");
                                        procedureSelect.append(_option2);
                                        _option=$("<option></option>").prop({
                                            "value":defaultOptionValue,
                                            "selected":"selected"
                                        }).text(defaultOptionName);
                                    }
                                }else{
                                    _option=$("<option></option>").prop("value",n.id).text(n.name);
                                }
                                procedureSelect.append(_option);
                            });

                        }else{
                            alert(result.message)
                        }
                    },
                    error:function (a, b) {
                        alert(b);
                    }
                });
            }
            //刷新表单前的序号
            function refreshIndex() {
                var trList=$("#tempForm>tbody>tr");
                var index=1;
                $.each(trList,function (i, n) {
                    $(n).find("td:eq(0)").text(index++);
                });
            }

            //绑定按钮
            function bindBtn(){
                $("#createFormList").click(function () {
                    var templateId=$("#modelList").val();
                    var tempDataTr=$("#tempForm>tbody>tr");
                    if(tempDataTr.length!=0){
                        if(confirm("已有表单，仍然要放弃现在的数据而重新生成吗？")){
                            getProcedure(templateId);
                        }
                    }else{
                        getProcedure(templateId);
                    }


                })
                $("#submitBtn").click(function () {
                    formSubmit();
                });
                //"新增"按钮
                $("#addCharac").click(function () {
                    addProcedureList(null,null);
                    $("#characModal").modal("show");

                });
                //模态框里面的确认按钮。功能为获取当前弹窗form里面的数据，添加到#tempForm表中去
                $("#chrarcFormSubmit").click(function () {
                    var selectOption=$("#chrarcForm").find("select[name=charaList]").find("option:selected").val();
                    if(selectOption=="noSelect"){
                        alert("请先选择工序");
                        return false;
                    }
                    var seleProced=$("#chrarcForm").find("select[name=charaList]").find("option:selected");
                    //工序id
                    var proceId=seleProced.val();
                    //工序name
                    var proceName=seleProced.text();
                    var chrarcName=$("#chrarcForm").find("input[name=chrarcName]").val();
                    //特性数量
                    var total=$("#chrarcForm").find("input[name=total]").val();
                    //加权系数
                    var coefficient=$("#chrarcForm").find("input[name=coefficient]").val();

                    //获取序号
                    var lastTr=$("#tempForm").find("tbody>tr:last");
                    var lastIndex=parseInt(lastTr.find("td:eq(0)").text());
                    //获取最后一次的排序号
                    var lastOrder=parseInt(lastTr.find("td:eq(7)").text());

                    //添加到#tempForm表中
                    var _trStr='<tr><td></td><td></td><td></td><td><input type="number"class="form-control"></td><td><input type="number"class="form-control"></td><td></td><td></td><td></td></tr>';
                    var _tr=$(_trStr);

                    _tr.find("td:eq(0)").text(lastIndex+1);
                    _tr.find("td:eq(1)").text(proceName);
                    _tr.find("td:eq(2)").text(chrarcName);
                    _tr.find("td:eq(3)").find("input").val(total);
                    _tr.find("td:eq(4)").find("input").val(coefficient);
                    _tr.find("td:eq(5)").text(proceId).addClass("hide");
                    _tr.find("td:eq(6)").text("0").addClass("hide");


                    //保存或修改？
                    var formType=$("#chrarcForm").find("input[name=formType]").val();
                    if(formType=="update"){
                        var currentTr=$("#tempForm>tbody").find("tr.myActive");
                        _tr.find("td:eq(7)").text(currentTr.find("td:eq(7)").text()).addClass("hide");
                        currentTr.after(_tr);
                        currentTr.remove();
                    }else{
                        _tr.find("td:eq(7)").text(lastOrder+1).addClass("hide");
                        $("#tempForm").append(_tr);
                    }
                    $("#characModal").modal("hide");
                })
                $("#deleteCharac").click(function () {
                    var currentTr=$("#tempForm").find("tr.myActive");
                    if(currentTr.length!=1){
                        alert("请先选中一条数据");
                        return false;
                    }
                    if(confirm("确定要删除？")){
                        currentTr.remove();
                        refreshIndex();
                    }
                });
                $("#modifyCharac").click(function(){
                    var currentTr=$("#tempForm>tbody").find("tr.myActive");
                    if(currentTr.length==0){
                        alert("请选中一条数据");
                        return false;
                    }
                    //当前工序id
                    var currProceId=currentTr.find("td:eq(5)").text();
                    var procedureName=currentTr.find("td:eq(1)").text();
                    addProcedureList(currProceId,procedureName);
                    var characName=currentTr.find("td:eq(2)").text();
                    var total=currentTr.find("td:eq(3)").find("input").val();
                    var coefficient=currentTr.find("td:eq(4)").find("input").val();
                    $("#chrarcForm").find("input[name=chrarcName]").attr("value",characName);
                    $("#chrarcForm").find("input[name=total]").val(total);
                    $("#chrarcForm").find("input[name=coefficient]").val(coefficient);

                    //表单类型设置为update
                    $("#chrarcForm").find("input[name=formType]").val("update");
                    $("#characModal").modal("show");
                })

            }


            //绑定事件
            function bindEven() {
                $("#tempForm").on("click","tbody>tr",function () {
                    $(this).nextAll().add($(this).prevAll()).removeClass("myActive");
                    $(this).addClass("myActive");
                })

                //模态框暗淡下去时候
                $("#characModal").on("hidden.bs.modal",function () {
                    $("#chrarcForm").get(0).reset();
                    $("#chrarcForm").find("input[type=hidden]").val("");
                    $("#chrarcForm").find("select[name=charaList]")
                        .find("option:contains('未选择')").find("option:not(0)").remove();
                })
                
            }

        </script>
        <style rel="stylesheet" type="text/css">
            .myActive{
                background:#5BC0DE;
            }
        </style>
    </head>
    <body>
        <div id="hideInfo">
            <input type="hidden" name="formLogo" value="<%=request.getParameter("logo")%>">
            <input type="hidden" name="productId" value="<%=request.getParameter("productId")%>">

        </div>
        <div class="container-fluid">
            <div class="row">
                <div class="col-lg-12">
                    <h3>检验内容配置</h3>
                    <form class="form-horizontal" id="myFormEntity">
                        <div class="form-group">
                            <label class="control-label col-lg-2 col-md-2 col-sm-2 col-xs-4">产品型号</label>
                            <p class="form-control-static col-lg-4 col-md-4 col-sm-4 col-xs-8">
                                <%=request.getParameter("modelName")%>
                            </p>
                            <label class="control-label col-lg-2 col-md-2 col-sm-2 col-xs-4">填写模板</label>
                            <div class="col-lg-4 col-md-4 col-sm-4 col-xs-8">
                                <select id="modelList" class="form-control">

                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-lg-2 col-md-2 col-sm-2 col-xs-4">产品代号</label>
                            <p class="form-control-static col-lg-4 col-md-4 col-sm-4 col-xs-8">
                                <%=request.getParameter("productCode")%>
                            </p>
                            <label class="control-label col-lg-2 col-md-2 col-sm-2 col-xs-4">产品名称</label>
                            <div class="col-lg-4 col-md-4 col-sm-4 col-xs-8">
                                <p class="form-control-static">
                                    <%=request.getParameter("productName")%>
                                    <input type="hidden" name="productId" value="<%=request.getParameter("productId")%>" />
                                </p>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-lg-2 col-md-2 col-sm-2 col-xs-4">生产批次</label>
                            <div class="col-lg-4 col-md-4 col-sm-4 col-xs-8">
                                <input name="batch" type="text" class="form-control"/>
                            </div>
                            <label class="control-label col-lg-2 col-md-2 col-sm-2 col-xs-4">生产数量</label>
                            <div class="col-lg-4 col-md-4 col-sm-4 col-xs-8">
                                <input type="number" class="form-control"  name="quantity" min=0>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-lg-2 col-md-2 col-sm-2 col-xs-4">类别</label>
                            <div class="col-lg-4 col-md-4 col-sm-4 col-xs-8">
                                <select name="category" class="form-control">
                                    <option value="ZJ">整机</option>
                                    <option value="MJ">模件</option>
                                    <option value="XL">线缆</option>
                                </select>
                            </div>
                            <label class="control-label col-lg-2 col-md-2 col-sm-2 col-xs-4">整机/模件/线缆名称</label>
                            <div class="col-lg-4 col-md-4 col-sm-4 col-xs-8">
                                <input name="moduleName" class="form-control" type="text"/>
                            </div>
                        </div>


                    </form>
                </div>
            </div>
            <div class="row">
                <hr/>
                <div class="col-lg-offset-2 col-lg-8 col-md-12">
                    <div class="btn-group">
                        <button id="addCharac" type="button" class="btn btn-info">新增</button>
                        <button id="modifyCharac" type="button" class="btn btn-info">修改</button>
                        <button id="deleteCharac" type="button" class="btn btn-info">删除</button>
                    </div>
                    <button id="createFormList" type="button" class="btn btn-info pull-right">生成表单</button>
                    <table id="tempForm" class="table ">
                        <thead>
                            <tr>
                                <th>序号</th>
                                <th>工序名称</th>
                                <th>工序检验特性名称</th>
                                <th>检验特性数量</th>
                                <th>严酷度加权系数</th>
                                <th class="hide">工序id</th>
                                <th class="hide">特性id</th>
                                <th class="hide">排序数字</th>
                            </tr>
                        </thead>
                            <tbody>

                            </tbody>
                    </table>
                </div>

            </div>
            <div class="row">
                <button id="submitBtn" class="btn btn-info col-lg-offset-10 col-md-offset-10" type="button">保存</button>
            </div>
        </div>

        <%--新增按钮对应的弹出层--%>
        <div class="modal fade" id="characModal" >
            <div class="modal-dialog modal-md" >
                <div class="modal-content ">

                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">
                            &times;
                        </button>
                        <h4 class="modal-title" id="myModalLabel2">
                            表单项
                        </h4>
                    </div>

                    <div class="modal-body">
                        <form id="chrarcForm" class="form-horizontal">
                            <%--保存或修改--%>
                            <input type="hidden" name="formType">
                            <div class="form-group">
                                <label class="col-lg-4 control-label">工序名称</label>
                                <div class="col-lg-8">
                                    <select class="form-control" name="charaList">
                                        <option value="noSelect">未选择</option>

                                    </select>
                                </div>
                            </div>



                            <div class="form-group">
                                <label class="control-label col-lg-4">
                                    工序检验特性名称
                                </label>
                                <div class="col-lg-8">
                                    <input type="text" class="form-control" name="chrarcName">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="control-label col-lg-4">
                                    检验特性数量
                                </label>
                                <div class="col-lg-8">
                                    <input type="number" class="form-control" name="total">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="control-label col-lg-4">
                                    严酷度加权系数
                                </label>
                                <div class="col-lg-8">
                                    <input type="number" class="form-control" name="coefficient">
                                </div>
                            </div>
                        </form>

                    </div>

                    <div class="modal-footer">
                        <button id="chrarcFormSubmit" type="button" class="btn btn-primary">
                            确认
                        </button>
                    </div>

                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div><!--.modal-->

    </body>
</html>

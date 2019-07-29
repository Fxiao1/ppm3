
//user
$(function(){

    //初始化型号树
    getModelList();
    //注册按钮
    bindBtn();
    bindEvent();
    //初始化表格
    initTable([],false);


})
//获取型号，包含产品。然后初始化左侧型号产品树
function getModelList(){
    $.ajax({
        url:"/Windchill/servlet/Navigation/model",
        data:{"actionName":"get"},
        type:'post',
        dataType:"json",
        success:function (result) {
            if(result.success){
                var modelList=result.data;
                var treeviewData=[];
                $.each(modelList,function (i, n) {
                    var modelNode={};
                    modelNode.text=n.name;
                    modelNode.type="model";
                    modelNode.code=n.numberCode;
                    var nodes=[];
                    $.each(n.productEntityList,function (j, m) {
                        var productNode={};
                        productNode.text=m.name;
                        productNode.type="product";
                        productNode.id=m.id;
                        productNode.code=m.product_code
                        nodes.push(productNode);
                    });
                    modelNode.nodes=nodes;
                    treeviewData.push(modelNode);
                });
                treeviewInit(treeviewData);
            }else{
                alert(result.message);
            }

        },
        error:function (a,b,c,d) {
            alert(b);
        }
    });
}

//treeviewInit
function treeviewInit(treeviewData){
    var treeview=$("#tree").treeview({
        data:treeviewData,
        onNodeSelected:function(event,data){
            $("#log").text(JSON.stringify(data));
            if("model"==data.type){
                var currentNode=$("#tree").treeview('collapseAll',{ silent: true });//关闭所有节点
                var currentNodeId=data.nodeId;
                $('#tree').treeview('expandNode', [ currentNodeId, { levels: 2, silent: true } ]);//展开当前节点
            }else if("product"==data.type){
                var productId=data.id;
                getFormList(productId);

                //获取父节点的一些信息
                var parentNode=$('#tree').treeview('getNode', data.parentId);
                $("#info").find("input[name=modelName]").val(parentNode.text);
                $("#info").find("input[name=modalCode]").val(parentNode.code);
                $("#info").find("input[name=productName]").val(data.text);

            }
        }

    });
    $("#tree").treeview('collapseAll',{ silent: true });//全部关闭
    $('#tree').treeview('expandNode', [ 0, { levels: 2, silent: true } ]);//展开首节点
}
//根据产品id获取该产品下的表单列表
function getFormList(productId){
    $.ajax({
        url:"/Windchill/servlet/Navigation/form?actionName=getByProId&productId="+productId,
        type:'get',
        dataType:'json',
        success:function (result) {
            if(result.success){
                initTable(result.data,true);
            }else{
                alert(result.message);
            }
        },
        error:function (a,b,c,d) {
            alert(b)
        }
    })
}


/**
 * 初始化表
 * @param list 数据
 * @param rebuild 是否重建
 * 初始化检验内容页面
 */
function initTable(list,rebuild){
    var modelName=$("#info").find("input[name=modelName]").val();
    var modalCode=$("#info").find("input[name=modalCode]").val();
    var productName=$("#info").find("input[name=productName]").val();

    if(rebuild){
        myformTable.destroy()
    }
    var option={
        data:list,
        "columns": [
            /*{ "data": null ,
                "title":"型号代号",
                "width":"8.3%",
                "render":function () {
                    return modalCode.length>12?modalCode.substring(0,10)+"…":modalCode;
                },
                "createdCell":function(cell,cellData,rowData,rowIndex,colIndex){
                    $(cell).attr("title",modalCode);
                }

            },*/
            { "data": null ,"title":"产品型号","render":function () {
                    return modelName;
            }},
            { "data": "productId" ,"title":"产品代号"},
            { "data": null,"title" : "产品名称","render":function(){
                return productName;
            }},
            { "data": "category","title" : "类别","render":function (data,type,row,meta) {
                if("ZJ"==data){
                    return '整机';
                }else if("MJ"==data){
                    return '模件';
                }else if("XL"==data){
                    return '线缆';
                }
            }},
            { "data": "checkType","title" : "检验类型","render":function (data,type,row,meta) {
                if("DZZJ"==data){
                    return '电装自检';
                }else if("DZHJ"==data){
                    return '电装检验';
                }else if("DZJY"==data){
                    return '电装检验';
                }else if("TSJY"==data){
                    return '调试检验';
                }else if("TSZJ"==data){
                    return '调试自检';
                }else if("DZJJ"==data){
                    return '电装军检';
                }else if("TSJJ"==data){
                    return '调试军检';
                }
            }},
            { "data": "moduleName","title" : "名称","render":function(data){
                    return data==null?"":data;
            }},
            { "data": "batch","title" : "生产批次","render":function(data){
                return data==null?"":data;
            }},
            { "data": "quantity","title" : "生产数量"},
            { "data": "creator","title" : "创建人"},
            { "data": "createTime","title" : "创建时间"},
            { "data": "updateTime","title" : "修改时间"},
            {"data":"logo","title":"操作","render":function (data) {
                return   '<a onclick="toWritePage('+data+')" href="javascript:;">填写</a> ';
            }},
            {"data":"logo","title":"隐藏信息","className":"hide","render":function (data) {
                return '<input  type="hidden" name="formSign" value="'+data+'"/>';
            }}
        ],
        language:{"decimal":"","emptyTable":"无数据","info":"显示 _START_ 到 _END_ 页共 _TOTAL_ 条","infoEmpty":"显示 0 到 0 页共 0 条","infoFiltered":"(filtered from _MAX_ total entries)","infoPostFix":"","thousands":",","lengthMenu":"显示 _MENU_ 条","loadingRecords":"加载中...","processing":"Processing...","search":"搜索:","zeroRecords":"没有匹配项","paginate":{"first":"首页","last":"尾页","next":"下页","previous":"上页"},"aria":{"sortAscending":": activate to sort column ascending","sortDescending":": activate to sort column descending"},}

    }
    myformTable=$("#myform").DataTable(option);
}
//在instanceWrite.jsp页面进行数据加载
function toWritePage(logo){
    var currentNode=$('#tree').treeview('getSelected')[0];
    var parentNode=$('#tree').treeview('getNode', currentNode.parentId);
    var modalName=parentNode.text;
    var productCode=currentNode.code;
    var productName=currentNode.text;
    var currentNode=$('#tree').treeview('getSelected')[0];
    var productId=currentNode.id;

    window.location.href="./instanceWrite.jsp?productId="+productId+"&modalName2="+modalName+
        "&productCode="+productCode+"&productName="+productName+"&formLogo="+logo;
}




//绑定按钮
function bindBtn() {
    //新增产品的按钮
    $("#addModelBtn").click(function () {
        var currentNode=$('#tree').treeview('getSelected')[0];

        if(!currentNode||currentNode.type!="model"){
            alert("请选择型号");
            return false;
        }else{
            //型号id
            var modelId = currentNode.code;
            //型号名
            var modelType = currentNode.text;
            
            $("#productForm").find("input[name=modelId]").val(modelId);
            $("#productForm").find("input[name=model_type]").val(modelType);
            $("#productModal").modal("show");
        }

    });
    //修改产品的按钮
    $("#modifyModelBtn").click(function () {
        var currentNode=$('#tree').treeview('getSelected')[0];

        if(!currentNode||currentNode.type!="product"){
            alert("请选择产品");
            return false;
        }else{
            //产品id
            var productCode = currentNode.code;
            var productId = currentNode.id;
            var productName = currentNode.text;
            var productForm=$("#productForm");
            productForm.find("input[name=id]").val(productId);
            productForm.find("input[name=name]").val(productName);
            productForm.find("input[name=productCode]").val(productCode);

            var parentNodeId=currentNode.parentId;
            var parentNode=$('#tree').treeview('getNode', parentNodeId);
            var modelId=parentNode.code;
            $("#productForm").find("input[name=modelId]").val(modelId);
            

            $("#productModal").modal("show");
        }

    });
    //删除产品的按钮
    $("#deleteModelBtn").click(function () {
        var currentNode=$('#tree').treeview('getSelected')[0];
        if(!currentNode||currentNode.type!="product"){
            alert("请选择产品");
            return false;
        }else{
            //产品id
            var productId = currentNode.id;
            if(confirm("确定要删除该产品？")){
                deleProduct(productId);
            };
        }

    });

    //产品模态框里面的确定按钮
    $("#productFormSubmit").click(function () {
        addProduct();
        $("#productModal").modal("hide");
    });
    //增加表单定义的按钮
    $("#addProductBtn").click(function () {
        var currentNode=$('#tree').treeview('getSelected')[0];
        
        if(currentNode&&currentNode.type=="product"){
            //获取型号名称
            var parentNode=$('#tree').treeview('getNode', currentNode.parentId);
            var modelName=parentNode.text;
            //产品代号
            var productCode=currentNode.code;
            var productName=currentNode.text;
            var productId=currentNode.id;
            var formMark="";
            $.getJSON(
                "/Windchill/servlet/Navigation/form",
                {"actionName":"getByProId","productId":productId},
                function (result) {
                    if(result.success){
                        var _length=result.data.length;
                        if(_length>0){
                            var _form=result.data[_length-1]
                            formMark=_form.logo;
                        }
                        window.location.href="/Windchill/netmarkets/jsp/ppm/content/formInfo.jsp?modelName=" +
                            modelName+"&productCode="+productCode+"&productName="+productName+
                            "&productId="+productId+"&logo="+formMark+"&pageType=add";
                    }else{
                        alert(result.message)
                    }
                }
            )


        }else{
            alert("请选中一条产品");
        }
    });
    //表单内容修改按钮
    $("#modifyProductBtn").click(function () {
        var currentNode=$('#tree').treeview('getSelected')[0];
       
        if(currentNode&&currentNode.type=="product"){
            //获取型号名称
            var parentNode=$('#tree').treeview('getNode', currentNode.parentId);
            var modelName=parentNode.text;
            //产品代号
            var productCode=currentNode.code;
            var productName=currentNode.text;
            var productId=currentNode.id;
            var currentFormTr=$("#myform>tbody").find("tr.myActive");
            if(currentFormTr.length!=1){
                alert("请选中一条表单数据");
                return false;
            }
            var logo=currentFormTr.find("input[name=formSign]").val();
            window.location.href="/Windchill/netmarkets/jsp/ppm/content/formInfo.jsp?modelName=" +
                modelName+"&productCode="+productCode+"&productName="+productName+
                "&productId="+productId+"&logo="+logo;
        }else{
            alert("请选中一条产品");
        }
    });
    //表单内容删除按钮
    $("#deleteProductBtn").click(function () {
        var currentTr=$("#myform").find("tr.myActive");
        if(currentTr.length!=1){
            alert("请先选择中一条表单数据");
            return false;
        }
        if(!confirm("确定删除？"))return false;
        var formSign=currentTr.find("input[name=formSign]").val();
        $.ajax({
            url:'/Windchill/servlet/Navigation/form',
            type:'get',
            data:{'actionName':'delete','formSign':formSign},
            dataType:'json',
            success:function (result) {
                if(result.success){
                    var currentNode=$('#tree').treeview('getSelected')[0];
                    getFormList(currentNode.id);
                }else{
                    alert(result.message);
                }
            },
            error:function (a,b,c,d) {
                alert(b)
            }
        })
    });
}
//注册事件
function bindEvent() {
    //模态框暗淡事件
    $("#productModal").on("hidden.bs.modal",function () {
        $("#productForm").get(0).reset();
        $("#productForm").find("input[type=hidden]").val("");
    });
    //表单内容的选择事件
    $("#myform").on("click","tr:not(:first)",function () {
        $(this).prevAll().add($(this).nextAll()).removeClass("myActive");
        $(this).addClass("myActive");
    })

}
//删除产品
function deleProduct(productId) {
    var _data={"id":productId};
    $.ajax({
        url:"/Windchill/servlet/Navigation/product?actionName=delete",
        type:'get',
        data:_data,
        dataType:"json",
        success:function (result) {
            if(result.success){
                //刷新左侧的型号树
                getModelList();
            }else{
                alert(result.message);
            }
        },
        error:function (a,b,c,d) {
            alert(b)
        }
    })
}

//添加产品
function addProduct() {
    var _data=$("#productForm").serialize();
    $.ajax({
        url:"/Windchill/servlet/Navigation/product?actionName=post",
        type:'get',
        data:_data,
        dataType:"json",
        success:function (result) {
            if(result.success){
                //刷新左侧的型号树
                getModelList();
            }else{
                alert(result.message);
            }
        },
        error:function (a,b,c,d) {
            alert(b)
        }
    })
}
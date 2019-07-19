/*基础模块页面的js文件*/
    $(function(){
        //获取数据
        getModelList();
        //绑定按钮
        bindBtns();
        //绑定事件
        bindEvent();
    })

/**
 * 获取工序。会根据$("#gongxuContent")下的工序列表进行剔除选项
 */
function getProcedure(){
    $.ajax({
        url:"/Windchill/servlet/Navigation/procedure",
        data:{"actionName":"get"},
        type:"get",
        dataType:"json",
        success:function (result) {
            if(!result.success){
                alert(result.message);
            }else{
                $("#gongxu").children("option").remove();
                $.each(result.data,function (i,n) {
                    //判断模板下是否已经有该工序了
                    var temProce=$("#gongxuContent").find("input[value="+n.id+"]");
                    if(temProce.length==0){
                        var _option=$("<option></option>").text(n.name).prop("value",n.id);
                        $("#gongxu").append(_option);
                    }
                });
            }
        },
        error:function (a,b,c,d) {
            alert(c);
        }
    })
}


//去除模板中的工序的按钮
function removeProce(btn){
    //先将该工序重新添加会选项框，然后再删除该选项
    var thisParent=$(btn).parent();
    /*var proceName=thisParent.find("input.form-control ").val();
    var proceId=thisParent.find("input[name=procedure_id]").val();
    var _option=$("<option></option>").text(proceName).prop("value",proceId);
    $("#gongxu").append(_option);*/
    thisParent.remove();
}
//绑定按钮
function bindBtns(){
    //"增加模板"按钮
    $("#addTempBtn").click(function(){
        //动态获取所有工序
        getProcedure();
        $("#myModalLabel").text("新增模板");
        $("#addModel").modal("show");
    });
    //"选择工序"按钮
    $("#selectProcedureBtn").click(function () {
        var selected=$("#gongxu").find("option:selected");
        if(selected.length==0){
            return false;
        }
        //根据名字再去重
        var procedureName=selected.text();
        var _length=$("#gongxuContent").find("input[value="+procedureName+"]").length;
        if(_length>0){
            return false;
        }

        var _div=$("<div></div>").addClass("form-inline");
        var _input1=$('<input type="text" disabled="disabled" class="form-control " >').val(selected.text());
        //工序id
        var currentProcedureId=$(this).prev().val();
        var _input2=$('<input type="hidden" name="procedure_id">').val(currentProcedureId);
        //工序排序
        var currentOrderStr=$("#modelForm").find("input[name=currentOrder]").val();
        var currentOrder=parseInt(currentOrderStr);
        var nextOrder=++currentOrder;
        $("#modelForm").find("input[name=currentOrder]").val(nextOrder);
        var _input3=$('<input type="hidden">').val(nextOrder).prop("name",currentProcedureId+"_order");
        //工序名

        var _input4=$('<input type="hidden">').val(procedureName).prop("name",currentProcedureId+"_name");
        //删除按钮
        var closeBtn='<button onclick="removeProce(this)" type="button" class="myClose">&nbsp;&times;&nbsp;</button>';
        _div.append(_input1).append(_input2).append(closeBtn).append(_input3).append(_input4);
        $("#gongxuContent").append(_div);
        //清除这个下拉框选项
        selected.remove();
    });
    //"删除模板" 按钮
    $("#deleTempBtn").click(function(){

				var deleLi=$("#modelList").find("li.active");
				
				if(deleLi.length==1){
					if(confirm('确定要删除吗')==true){
						var _id=deleLi.prop("id");
						var _data={"actionName":"delete"};
						_data.id=_id;
						$.ajax({
							url:"/Windchill/servlet/Navigation/template",
							data:_data,
							type:"get",
							dataType:"json",
							success:function (result) {
								if(!result.success){
									alert(result.message);
								}
								getModelList();
							},
							error:function (a,b,c,d) {
								alert("发生错误，删除失败");
							}
						})
					 }else{

			    	       return false;

			    	    }
				}else{
					alert("请选中一条模板");
				}

    });

    //修改模板按钮
    $("#modifyTempBtn").click(function () {
        var seleLi=$("#modelList").find("li.active");

        if(seleLi.length==1){
            //带入模板基础信息
            var _id=seleLi.prop("id");
            $("#modelForm").find("input[name=id]").val(_id);
            $("#modelForm").find("input[name=name]").val(seleLi.text());
            $("#myModalLabel").text("修改模板");
            //获取全部工序放到下拉框里面
            getProceByTemplate(_id);
        }else{
            alert("请选中一条模板");
        }
    });
    //“添加工序按钮”
    $("#addProcedureBtn").click(function () {
        var _input=$(this).next();
        var procedureName=_input.val();
        if(procedureName==null||procedureName==""){
            alert("请输入工序名称");
            return false;
        }
        var _data={"actionName":"add"};
        _data.procedureName=procedureName;
        $.ajax({
            url:"/Windchill/servlet/Navigation/procedure",
            data:_data,
            type:"get",
            dataType:"json",
            success:function (result) {
                if(result.success){
                    getProcedure();
                    _input.val("");
                }else{
                    alert(result.message);
                }
            },
            error:function (a, b, c, d) {
                alert(b);
            }
        });
    });



    //特性的新增按钮
    $("#addCharBtn").click(function(){
        var currentTr=$("#procedureList").find("tr.info");
        if(currentTr.length==0){
            alert("请选中一条数据");
            return false;
        }
        $("#model2").modal("show");
    });
    //特性修改按钮
    $("#modifyCharBtn").click(function () {
        var currentTr=$("#procedureList").find("tr.info");
        var seleType=currentTr.children("td:eq(6)").text();
        if(currentTr.length==0||seleType=="procedure"){
            alert("请选中一条特性数据");
            return false;
        }

        $("#model2").modal("show");
        //特性id
        var characId=currentTr.find("td:eq(5)").text()
        $("#characForm").find("input[name=id]").val(characId);

        $.ajax({
            url:"/Windchill/servlet/Navigation/procedurelink?actionName=getById&id="+characId,
            type:"get",
            dataType:"json",
            success:function (result) {
                if(result.success){
                    var charac=result.data.character;
                    $("#characForm").find("input[name=name]").val(charac.name);
                    $("#characForm").find("input[name=total]").val(charac.total);
                    $("#characForm").find("input[name=coefficient]").val(charac.coefficient);
                }else{
                    alert(result.message);
                }
            },
            error:function (a, b, c, d) {
                alert(b);
            }
        });
    });

    //特性form的提交按钮
    $("#characFormSubmit").click(function () {

    	 if(examine()==1){
             return false;
         }
        var _data=$("#characForm").serialize();
        $.ajax({
            url:"/Windchill/servlet/Navigation/procedurelink?actionName=post",
            type:"get",
            data:_data,
            dataType:"json",
            success:function (result) {
                if(result.success){
                    $("#model2").modal("hide");
                    getProcedureByTemplate();
                }else{
                    alert(result.message);
                }
            },
            error:function (a, b, c, d) {
                alert(b);
            }
        })
    });


    /**
     * ln
     * 检查表单
     */
    function examine() {
        var numberInput=$("#characForm").find("input[name=coefficient]");
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



    //特性删除按钮
    $("#deleCharBtn").click(function () {
		var currentTr=$("#procedureList").find("tr.info");
        if(currentTr.length==0){
            alert("请选中一条特性数据");
            return false;
        }

    	if(confirm('确定要删除吗')==true){
	        var procedureId=$("#procedureList").find("tr.info").prop("id");
	        var tempId=$("#modelList").find("li.active").prop("id");
	       // alert(procedureId)
	        var characId=$("#procedureList").find("tr.info").find("td._characId").text();
	       // alert(characId)
	       var type=$("#procedureList").find("tr.info").find("td._type").text();
	       // alert(type);
	        if(type=="charac")
	        	{
	        	//删除特性
	        	var _data={"actionName":"deleteById","id":characId};
		        $.ajax({
		            url:"/Windchill/servlet/Navigation/procedurelink",
		            type:"get",
		            data:_data,
		            dataType:"json",
		            success:function (result) {
		                if(result.success){
		                    getProcedureByTemplate();
		                }else{
		                    alert(result.message);
		                }
		            },
		            error:function (a, b, c, d) {
		                alert(b);
		            }
		        });
	        	
	        	}
	        else if(type=="procedure")
	        	{
	        	//删除工序
	        	/*var _data={"actionName":"deleteById","tempId":tempId,"id":procedureId};
		        $.ajax({
		            url:"/Windchill/servlet/Navigation/templatelink",
		            type:"get",
		            data:_data,
		            dataType:"json",
		            success:function (result) {
		                if(result.success){
		                    getProcedureByTemplate();
		                }else{
		                    alert(result.message);
		                }
		            },
		            error:function (a, b, c, d) {
		                alert(b);
		            }
		        });*/
				alert("请选中一条特性数据");
	        	}
	        
    	 }else{

  	       return false;

  	    }
    });


    //导入工艺化结构文件按钮
    $("#importCharBtn").click(function() {
        getProcessData();
        $('#processModal').modal('show');
    });


    $("#doInputBtn").click(function () {
        var currentTr=$("#processTable>tbody>tr.info");
        if(currentTr.length!=1){
            alert("请选中一条数据");
            return false;
        }
        var mpm_number=currentTr.children("td:eq(0)").text();
        var mpm_oid=$("#processTable").DataTable().row(currentTr).data().oid;
        var currentTemplate=$("#modelList").find("li.active");
        if(currentTemplate.length!=1&&!confirm("您未选中模板，如果继续导入将仅导入到工序库，确认继续吗？")){
            return false;
        }
        var templateId=currentTemplate.prop("id");
        $.ajax({
            url:'/Windchill/servlet/Navigation/mpmprocessPlan?actionName=addOperation',
            type:'get',
            data:{"mpm_number":mpm_number,"mpm_oid":mpm_oid,"templateId":templateId},
            /*dataType:'json',*/
            success:function(result){
                getModelList();
                $("#processModal").modal("hide");
            },
            error:function(a,b,c,d){
                alert(b)
            }
        });
    });

}/*bindBtn()方法结束*/

/**
 * 获得当前模板下的所有工序，并添加到页面上展现。然后会向工序下拉框里面添加选项，然后开启浮层展现form
 * @param templateId 模板id
 */
function getProceByTemplate(templateId) {
    var _data={"actionName":"getByTemplate","templateId":templateId};
    $.ajax({
        url:"/Windchill/servlet/Navigation/procedure",
        type:"get",
        dataType:"json",
        data:_data,
        success:function (result) {
            if(result.success){
                $.each(result.data,function (i,n) {
                    var _div=$("<div></div>").addClass("form-inline");
                    var _input1=$('<input type="text" disabled="disabled" class="form-control " >')
                        .val(n.name);
                    //工序id
                    var currentProcedureId=n.id;
                    var _input2=$('<input type="hidden" name="procedure_id">').val(currentProcedureId);
                    //工序排序
                    var currentOrderStr=$("#modelForm").find("input[name=currentOrder]").val();
                    var currentOrder=parseInt(currentOrderStr);
                    var nextOrder=++currentOrder;
                    $("#modelForm").find("input[name=currentOrder]").val(nextOrder);
                    var _input3=$('<input type="hidden">').val(nextOrder).prop("name",currentProcedureId+"_order");
                    //工序名
                    var _input4=$('<input type="hidden">').val(n.name).prop("name",currentProcedureId+"_name");
                    //删除按钮
                    var closeBtn='<button onclick="removeProce(this)" type="button" class="myClose">&nbsp;&times;&nbsp;</button>';
                    _div.append(_input1).append(_input2).append(closeBtn).append(_input3).append(_input4);
                    $("#gongxuContent").append(_div);
                    //然后清除下拉框里面的这条选项
                    $("#gongxu").find("option[value="+n.id+"]").remove();
                });
                getProcedure();
                $("#addModel").modal("show");
            }else{
                alert(result.message);
            }
        },
        error:function (a, b, c, d) {
            alert(b);
        }
    })
}




//获取工艺化文件数据
function getProcessData() {
    $.ajax({
        url:'/Windchill/servlet/Navigation/mpmprocessPlan?actionName=searchMPM',
        type:"get",
        dataType:"json",
        success:function(result){
            if(result.success){
                initProcessTable(result.data);
            }else{
                alert(result.message);
            }
        },
        error:function(a,b,c,d){
            alert(b)
        }
    });
}
//初始化结构化工艺数据的表格
function initProcessTable(_data){
    if($("#processTable").find("tbody").length==1){
        myTable.destroy()
    }
    var option={
        data:_data,
        autoWidth:false,
        columns:[
            {data:'number',title:"编号"},
            {data:"name",title:"名称"},
            {data:"version",title:"版本"},
            {data:"oid",title:"oid",visible:false}
        ],
        language:{"decimal":"","emptyTable":"No data available in table","info":"显示 _START_ 到 _END_ 页共 _TOTAL_ 条","infoEmpty":"显示 0 到 0 页共 0 条","infoFiltered":"(filtered from _MAX_ total entries)","infoPostFix":"","thousands":",","lengthMenu":"显示 _MENU_ 条","loadingRecords":"加载中...","processing":"Processing...","search":"搜索:","zeroRecords":"没有匹配项","paginate":{"first":"首页","last":"尾页","next":"下页","previous":"上页"},"aria":{"sortAscending":": activate to sort column ascending","sortDescending":": activate to sort column descending"},}
    };
    myTable=$("#processTable").DataTable(option);
};


//获取全部模板
function getModelList(){
    $("#modelList").html("");
    $.ajax({
        url:"/Windchill/servlet/Navigation/template",
        data:{"actionName":"get"},
        type:"get",
        dataType:"json",
        success:function(result){
            if(result.success){
                $.each(result.data,function(i,n){
                    var listItem=$("<li></li>").addClass("list-group-item").prop("id",n.id).text(n.name);
                    $("#modelList").append(listItem);
                });
            }else{
                addLog("获取模板请求成功，但是未请求到数据。"+result.message);
            }

        },
        error:function (a,b,c,d) {
            alert("获取数据失败:"+b);
        }
    });
}
//表单提交
function templateSubmit(){

	var templateName = $("#modelForm").find("input[name=name]").val();

	if(templateName==null||templateName==""){
		alert("请输入模板名");
		return false;
	}


    $.ajax({
        url:"/Windchill/servlet/Navigation/templatelink?actionName=post",
        data:$("#modelForm").serialize(),
        type:"post",
        success:function (result) {
            if(!result.success){
                addLog(result.message);
            }
            $("#addModel").modal("hide");
            getModelList();
        },
        error:function (a,b,c,d) {
            alert("保存失败，"+b);
            $("#addModel").modal("hide");
        }
    })
}
//根据模板获取检验特性
function getProcedureByTemplate(){
    var _li=$("#modelList").find("li.active").prop("id");
    var templateId=_li;
    var _data={"actionName":"getByTemplate","templateId":templateId}
    var indexNum=0;
    $.ajax({
        url:"/Windchill/servlet/Navigation/procedure",
        type:"get",
        data:_data,
        dataType:"json",
        success:function(result){
            if(result.success){
                $("#procedureList").find("tr:not(:first)").remove();
                $.each(result.data,function (i, n) {
                    var _tr2=$("<tr> <td></td> <td></td> <td></td> <td></td> <td></td> <td></td> <td></td> <td></td> </tr>");
                    _tr2.prop("id",n.id);
                    _tr2.find("td:eq(0)").text(++indexNum);
                    _tr2.find("td:eq(1)").text(n.name);
                    _tr2.find("td:eq(5)").addClass("hide")
					_tr2.find("td:eq(6)").text("procedure").addClass("hide").addClass("_type");
                    _tr2.find("td:eq(7)").text("工序");
                    $("#procedureList").append(_tr2);
                    $.each(n.characList,function (j,m) {
                        var _tr=$("<tr> <td></td> <td></td> <td></td> <td></td> <td></td> <td></td> <td></td> <td></td> </tr>");
                        _tr.prop("id",n.id);
                        _tr.find("td:eq(0)").text(++indexNum);
                        _tr.find("td:eq(1)").text(n.name);
                        _tr.find("td:eq(2)").text(m.name);
                        _tr.find("td:eq(3)").text(m.total);
                        _tr.find("td:eq(4)").text(m.coefficient);
                        _tr.find("td:eq(5)").text(m.id).addClass("hide").addClass("_characId");
						_tr.find("td:eq(6)").text("charac").addClass("hide").addClass("_type");
                        _tr.find("td:eq(7)").text("特性");
                        $("#procedureList").append(_tr);
                    })

                });
                $("#procedureList").find("th,td").addClass("text-center");
            }else{
                alert(result.message);
            }

        },
        error:function(a,b,c,d){
            alert(b);
        }
    });
}

//为模板列表绑定事件
function bindEvent() {
    $("#modelList").on("click","li",function () {
        $(this).prevAll().add($(this).nextAll()).removeClass("active");
        $(this).addClass("active");
        getProcedureByTemplate();

    });
    //模板的模态框暗下去的时候
    $("#addModel").on("hidden.bs.modal",function () {
        $("#gongxuContent").html("");
        $("#modelForm").get(0).reset();
        $("#modelForm").find("input[type=hidden]").not("input[name=currentOrder]").val("");
    });

    //特性模态框出来时候需要做的事情
    $("#model2").on("show.bs.modal",function(){
        //拿到当前工序的名称和id
        var currentProcedure=$("#procedureList").find("tr.info");
        //带过来工序id和工序名称
        var procedureId=currentProcedure.prop("id");
        var _idInput=$("#characForm").find("input[name=twId]");
        _idInput.val(procedureId);
        var procedureName=currentProcedure.find("td:eq(1)").text();
        _idInput.prev().text(procedureName);

    });
    //特性编辑模态框暗下去的时候
    $("#model2").on("hidden.bs.modal",function () {
        $("#characForm").get(0).reset();
        $("#characForm").find("input[type=hidden]").val("");
    })

    //工序的点击选择效果
    $("#procedureList").add($("#processTable")).on("click","tr:not(:first)",function () {
        $(this).nextAll().add($(this).prevAll()).removeClass("info");
        $(this).addClass("info");
    });

}

function addLog(message){
    var _li=$("<li></li>").text(message);
    $("#log").children("ul").append(_li);
}


//根据工序id获取工序检验特性
function getCharaByProceId(procedureId){
        var _data={};
        _data.procedureId=procedureId;

}
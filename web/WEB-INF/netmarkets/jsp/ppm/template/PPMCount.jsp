<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>PPMCount</title>
    <link rel="stylesheet" type="text/css" href="../static/bootstrap.css"/>
    <script type="text/javascript" src="../static/jquery.min.js"></script>
    <script type="text/javascript" src="../static/bootstrap.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/netmarkets/jsp/ppm/static/My97DatePicker/My97DatePicker/WdatePicker.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/netmarkets/jsp/ppm/static/modifyBtnClass.js" >
    </script>
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
        	<!-- <form action="PPMExport.jsp" onSubmit="return inputNull(this)"> -->
            <form id="formAction" >
                <div class="form-group">
                    <label class="control-label col-sm-1">
                        开始时间：
                    </label>
                    <div class="col-sm-2">
                        <input name="startDate" id="startDate" readonly="readonly" class="form-control" type="text" onClick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd'})">
                    </div>
                    <label class="control-label col-sm-1">
                        结束时间：
                    </label>
                    <div class="col-sm-2">
                        <input name="endDate" id="endDate" readonly="readonly" class="form-control" type="text" onClick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd'})">
                    </div>
                </div>
				<div class="col-sm-1">
					<select id="channgeType" onchange="channgeType()">
						<option value="0" selected="selected">工序</option>
						<option value="1">产品</option>
						<option value="2">型号</option>
						<option value="3">模件</option>
					</select>
				</div>


                <div>
                    <input id="bt1" class="col-sm-1" type="button" style="margin-left: 100px" value="计算">

                    <%--<input id="bt2" class="col-sm-1" type="button" value="导出" onclick="window.location='PPMExport.jsp'">--%>
                    <input id="bt2" class="col-sm-1" type="button" value="导出" style="margin-left: 100px" onclick="formSubmit()">

                </div>
            </form>
        </div>




        <table border="2" style="width: 100%; display:none ;" id="gxTable"  >
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
        <table border="2" style="width: 100%; display:none ; " id="cpTable" >
            <thead>
            <tr style="background-color: #9d9d9d;" >
                <th >产品名称</th>
                <th >产品PPM</th>
            </tr>
            </thead>
            <tbody id="cpbody">

            </tbody>

        </table>
        <table border="2" style="width: 100%;display: none; " id="xhTable" >
            <thead>
            <tr style="background-color: #9d9d9d;">
                <th >型号名称</th>
                <th >型号PPM</th>
            </tr>
            </thead>
            <tbody id="xhbody">

            </tbody>

        </table>
        <table border="2" style="width: 100%; display: none; " id="mjTable" >
            <thead>
            <tr style="background-color: #9d9d9d;" >
            	<th >型号名称</th>
                <th >产品名称</th>
                <th >模件名称</th>
                <th >模件PPM</th>
            </tr>
            </thead>
            <tbody id="mjbody">

            </tbody>

        </table>
    </div>



</div>

<script>


    //点击计算
    $("#bt1").click(function(){
        var startDate = document.getElementById("startDate").value;
        var endDate = document.getElementById("endDate").value;
		var TypeVal = $("#channgeType option:selected").val();
        if(startDate == ''|| endDate == ''){
            alert("请选择有效时间");
        }
        
        if(TypeVal==0){
        	$("#gxTable").show();
	    	 $("#cpTable").hide();
	    	 $("#xhTable").hide();
	    	 $("#mjTable").hide();
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
        	
        }else if(TypeVal==1){
        	$("#gxTable").hide();
	    	 $("#cpTable").show();
	    	 $("#xhTable").hide();
	    	 $("#mjTable").hide();
        	 var options = {
     	            colums: [{
     	                "Index": "productName",
     	                "Name": "产品名称"
     	            },{
     	                "Index": "productPPM",
     	                "Name": "产品PPM"
     	            }]
     	        }
        }else if(TypeVal==2){
        	$("#gxTable").hide();
	    	 $("#cpTable").hide();
	    	 $("#xhTable").show();
	    	 $("#mjTable").hide();
	       	 var options = {
	  	            colums: [{
	  	                "Index":"xHName",
	  	                "Name": "型号名称"
	  	            },{
	  	                "Index":"xHPPM",
	  	                "Name": "型号PPM"
	  	            }]
	  	        }
	     }else if(TypeVal==3){
	    	 $("#gxTable").hide();
	    	 $("#cpTable").hide();
	    	 $("#xhTable").hide();
	    	 $("#mjTable").show();
        	 var options = {
      	            colums: [{
      	                "Index": "xHName",
      	                "Name": "型号名称"
      	            },{
      	                "Index": "productPPM",
      	                "Name": "产品名称"
      	            },{
      	                "Index": "mJName",
      	                "Name": "模件名称"
      	            },{
      	                "Index": "mJPPM",
      	                "Name": "模件PPM"
      	            }]
      	        }
         }

       

        add_detail(options);

    });

    function inputNull(form){


        var startDate = document.getElementById("startDate").value;
        var endDate = document.getElementById("endDate").value;

        if(startDate == ''|| endDate == ''){
            alert("请选择有效时间");
            return false;
        }

    }

    /*    //点击导出
        $("#bt2").click(function(){
            var startDate = document.getElementById("startDate").value;
            var endDate = document.getElementById("endDate").value;
            window.open("PPMExport.jsp?startDate=" + startDate+"&endDate=" + endDate);
        });*/




    function add_detail(options){
        var $tbody = $("#tbody");
        $tbody.empty();
        var startDate = document.getElementById("startDate").value;
        var endDate = document.getElementById("endDate").value;
        var TypeVal = $("#channgeType option:selected").val();
			alert(TypeVal);
        var colums = options.colums;
        var url = "/Windchill/servlet/Navigation/ppmCalculateDetail?actionName=getForm";//json文件或者数据库查询后的地址
        ///Windchill/servlet/Navigation/calculate
        var content = [];
        //ajax获取数据源后存入content数据中。
        $.ajax({
            type: "get",
            url: url,
            data: { "startDate": startDate, "endDate": endDate ,"TypeVal":TypeVal},
            dataType: "json",
            async: false,
            success: function(result) {
                content = result.data;
                var cp="";
                if(content.length==0){
                    alert("未查到数据，请扩大时间跨度再次尝试");
                    return false;
                }
                
                if(TypeVal==0){
                	$('#tbody tr:gt(0)').remove();//删除之前的数据
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
                         
                         html += "<td rowspan='1'>"+cols[0]+"</td>";
                         var table2 = cols[1];


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
                	
                }else if(TypeVal==1){
                	$("#cpbody tr:not(:first)").remove();//删除之前的数据
                	 cp = "";
                	 for (var i = 0; i < content.length; i++){
                		
                		 cp+="<tr id="+i+">";
                		 cp+="<td>"+content[i].productName+"</td>";
                		 cp+="<td>"+content[i].productPPM+"</td>";
                		 cp+="</tr>";
                	 }
                	 $("#cpbody").append(cp);
        		}
                else if(TypeVal==2){
                	$("#xhbody tr:not(:first)").remove();//删除之前的数据
                	 xh = "";
                	 for (var i = 0; i < content.length; i++){
                		
                		 xh+="<tr id="+i+">";
                		 xh+="<td>"+content[i].xHName+"</td>";
                		 xh+="<td>"+content[i].xHPPM+"</td>";
                		 xh+="</tr>";
                	 }
                	 $("#xhbody").append(xh);
        		}
                else if(TypeVal==3){
                	$("#mjbody tr:not(:first)").remove();//删除之前的数据
                	 mj = "";
                	 for (var i = 0; i < content.length; i++){
                		
                		 mj+="<tr id="+i+">";
                		 mj+="<td>"+content[i].xHName+"</td>";
                		 mj+="<td>"+content[i].productName+"</td>";
                		 mj+="<td>"+content[i].mJName+"</td>";
                		 mj+="<td>"+content[i].mJPPM+"</td>";
                		 mj+="</tr>";
                	 }
                	 $("#mjbody").append(mj);
        		}
            }
        });
		/* console.log(content);
		
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
            
            html += "<td rowspan='1'>"+cols[0]+"</td>";
            var table2 = cols[1];


            html += "<td rowspan='1'><table border=\"2\" style=\"width: 100%; \">";
            $.each(table2,function (k,v) {
                html += "<tr><td>" +v+"</td></tr>";
            })
            //html += "<tr><td >" +cols[1]+"</td></tr>";

            html += "</table></td>"
            html += "<td rowspan='1'>"+cols[2]+"</td>";
            html += "</tr>";
            $tbody.append(html);

        }  */

    }
	function channgeType() {
		var ty = $("#channgeType option:selected").val();
		
	}
	/*
	*ln
	*/
	function formSubmit() {
			var TypeVal = $("#channgeType option:selected").val();
			var formObj=document.getElementById("formAction");
			if(TypeVal==0){
				 formObj.action="PPMExport.jsp";
			        formObj.method="post";
			        formObj.submit();
			}else if(TypeVal==1){
				formObj.action="CpPPMExport.jsp";
		        formObj.method="post";
		        formObj.submit();
			}else if(TypeVal==2){
				formObj.action="XHPPMExport.jsp";
		        formObj.method="post";
		        formObj.submit();
			}else if(TypeVal==3){
				formObj.action="MJPPMExport.jsp";
		        formObj.method="post";
		        formObj.submit();
			}
	}



</script>
</body>
</html>
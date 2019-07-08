<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@ include file="/netmarkets/jsp/util/beginShell.jspf"%>
<html>
<head>
    <title>基础管理</title>
    <!--<link rel="stylesheet" href="../static/bootstrap.css">-->
    <style type="text/css" rel="stylesheet" >
        button.myClose{
            -webkit-appearance: none;
            padding: 0;
            cursor: pointer;
            background: transparent;
            border: 0;}
        .myActive{background: #337AB7;}
    </style>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/netmarkets/jsp/ppm/static/bootstrap.css">
<script type="text/javascript" src="${pageContext.request.contextPath}/netmarkets/jsp/ppm/static/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/netmarkets/jsp/ppm/static/bootstrap.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/netmarkets/jsp/ppm/static/template/template.js"></script>

	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/netmarkets/jsp/ppm/static/dataTables.bootstrap.min.css"/>
	<script type="text/javascript" src="${pageContext.request.contextPath}/netmarkets/jsp/ppm/static/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/netmarkets/jsp/ppm/static/dataTables.bootstrap.min.js"></script>


</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">

                <div class="well">
                    <div class="btn-group" >
                        <button class="btn btn-info" type="button" id="addTempBtn" >新增</button>
                        <button class="btn btn-info" type="button" id="modifyTempBtn">修改</button>
                        <button class="btn btn-info" type="button" id="deleTempBtn" >删除</button>
                    </div>

                    <ul id="modelList" class="list-group" style="margin-top:15px;">

                    </ul>
                </div>
            </div>
            <div class="col-xs-9 col-sm-9 col-md-9  col-lg-8">
                <div class="">
                    <h3>工序特性指标</h3>
                    <div class="btn-group" >
                        <button class="btn btn-info" type="button" id="addCharBtn" >新增</button>
                        <button class="btn btn-info" type="button" id="modifyCharBtn">修改</button>
                        <button class="btn btn-info" type="button" id="deleCharBtn" >删除</button>
                        <button class="btn btn-info" type="button" id="importCharBtn" >结构化工艺导入工序</button>
                    </div>
                    <table class="table" id="procedureList">
                        <tr>
                            <th>序号</th>
                            <th>工序名称</th>
                            <th>工序检验特性</th>
                            <th>检验特性数量</th>
                            <th>严酷度加权系数</th>
                            <th>数据类型</th>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
        <%--模板操作界面模态框--%>
        <div class="modal fade" id="addModel" >
            <div class="modal-dialog modal-lg" >
                <div class="modal-content ">

                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">
                            &times;
                        </button>
                        <h4 class="modal-title" id="myModalLabel">
                            增加模板
                        </h4>
                    </div>

                    <div class="modal-body">
                        <form class="form-horizontal" id="modelForm">
                            <div class="form-group">
                                <label  class="col-md-3 control-label">模板名</label>
                                <div class="col-md-7">
                                    <input class="form-control" type="text" placeholder="模板名" name="name" >
                                    <input type="hidden" name="id" >
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="gongxu" class="col-md-3 control-label">选择工序</label>
                                <div class="col-md-7 form-inline">
                                    <select id="gongxu" class="form-control">
                                        <option>数据获取中……</option>
                                    </select>
                                    <button id="selectProcedureBtn" type="button" class="btn btn-info">选择</button>
                                    <span class="pull-right">
                                        <button type="button" id="addProcedureBtn" class=" btn btn-info">添加工序</button>
                                        <input type="text" name="procedureName" class="pull-right form-control" placeholder="请输入工序名称">
                                    </span>
                                </div>
                            </div>
                            <div class="form-group ">
                                <div id="gongxuContent" class="col-md-6 col-md-offset-3">
                                </div>
                            </div>
                        </form>
                    </div>

                    <div class="modal-footer">
                        <button onclick="templateSubmit()" type="button" class="btn btn-primary">
                            确认
                        </button>
                    </div>

                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div><!--.modal-->

        <div class="modal fade" id="model2" >
            <div class="modal-dialog modal-lg" >
                <div class="modal-content ">

                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">
                            &times;
                        </button>
                        <h4 class="modal-title" id="myModalLabel2">
                            工序检验特性
                        </h4>
                    </div>

                    <div class="modal-body">
                        <form id="characForm" class="form-horizontal" >
                            <div class="form-group">
                                <label  class="col-md-3 control-label">工序名称</label>
                                <div class="col-md-7">
                                    <p class="form-control-static">工序2</p>
                                    <input type="hidden" name="twId" >
                                </div>
                            </div>
                            <div class="form-group">
                                <label  class="col-md-3 control-label">特性名称</label>
                                <div class="col-md-7">
                                    <input class="form-control" type="text" placeholder="点击输入" name="name" >
                                    <input type="hidden" name="id" >
                                </div>
                            </div>
                            <div class="form-group hide">
                                <label  class="col-md-3 control-label">检验特性数量</label>
                                <div class="col-md-7">
                                    <input class="form-control" value="0" type="number" placeholder="点击输入" name="total" >
                                </div>
                            </div>
                            <div class="form-group">
                                <label  class="col-md-3 control-label">严酷度加权系数</label>
                                <div class="col-md-7">
                                    <input class="form-control" type="number" placeholder="点击输入" name="coefficient" >
                                </div>
                            </div>
                        </form>
                    </div>

                    <div class="modal-footer">
                        <button id="characFormSubmit" type="button" class="btn btn-primary">
                            确认
                        </button>
                    </div>

                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div><!--.modal-->
		
		 <div class="modal fade" id="processModal">
                <div class="modal-dialog modal-md">
                    <div class="modal-content ">

                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal">
                                &times;
                            </button>
                            <h4 class="modal-title">
                                标题
                            </h4>
                        </div>

                        <div class="modal-body">
                            <table class="table" id="processTable"></table>
                        </div>

                        <div class="modal-footer">

                            <button type="button" id="doInputBtn" class="btn btn-primary">
                                确定
                            </button>
                        </div>

                    </div><!-- /.modal-content -->
                </div><!-- /.modal-dialog -->
            </div>
            <!--.modal-->


    </div>
    <div class="hide" id="log"><ul></ul></div>
</body>
</html>

<%--
  Created by IntelliJ IDEA.
  User: Fxiao
  Date: 2019/6/15
  Time: 15:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>ppm基础管理</title>
    <link rel="stylesheet" type="text/css" href="../static/bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="../static/dataTables.bootstrap.min.css"/>
    <script type="text/javascript" src="../static/jquery.min.js"></script>
    <script type="text/javascript" src="../static/bootstrap.js"></script>
    <script type="text/javascript" src="../static/bootstrap-treeview.min.js"></script>
    <script type="text/javascript" src="../static/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="../static/dataTables.bootstrap.min.js"></script>
    <script type="text/javascript" src="../static/content/content.js"></script>
    <style type="text/css" >
        .myActive{
            background-color: #428BCA;
            color: #FFFFFF;
        }
    </style>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/netmarkets/jsp/ppm/static/modifyBtnClass.js" >
    </script>
</head>
<body>
<div class="hide" id="info" >
    <input type="hidden" name="modelName" >
    <input type="hidden" name="modalCode" >
    <input type="hidden" name="productName" >
</div>
<div class="container-fluid">
    <div class="row">
        <div class="col-lg-12">
            <h3 class="text-center">产品检验内容配置管理</h3>
            <hr/>
        </div>
    </div>
    <div class="row">
        <div class=" col-lg-2">
            <h3>产品组成</h3>
            <div class="btn-group">
                <button id="addModelBtn" class="btn btn-info">增加</button>
                <button id="modifyModelBtn" class="btn btn-info">修改</button>
                <button id="deleteModelBtn" class="btn btn-info">删除</button>
            </div>
            <div id="tree">

            </div>
        </div>
        <div class="col-lg-10">
            <h3 class="text-center">检验内容</h3>
            <div class="btn-group operation">
                <button id="addProductBtn" class="btn btn-info">增加</button>
                <button id="modifyProductBtn" class="btn btn-info">修改</button>
                <button id="deleteProductBtn" class="btn btn-info">删除</button>
            </div>

            <table id="myform" class="table">

            </table>
        </div>
    </div>
</div>

    <%--增加产品的浮窗--%>

    <div class="modal fade" id="productModal" >
        <div class="modal-dialog modal-md" >
            <div class="modal-content ">

                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        &times;
                    </button>
                    <h4 class="modal-title" id="myModalLabel2">
                        产品
                    </h4>
                </div>

                <div class="modal-body">
                    <form id="productForm" class="form-horizontal">
                        <%--型号id--%>
                        <input type="hidden" name="modelId" />
                        <input type="hidden" name="model_type" />
                        <div class="form-group">
                            <label class="col-md-3 control-label">产品名称</label>
                            <div class="col-md-7">
                                <input class="form-control" type="text" name="name" placeholder="请输入产品名称" />
                                <input type="hidden" name="id">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-md-3 control-label">产品代号</label>
                            <div class="col-md-7">
                                <input class="form-control" type="text" name="productCode" placeholder="请输入产品代号" />
                            </div>
                        </div>

                    </form>


                </div>

                <div class="modal-footer">
                    <button id="productFormSubmit" type="button" class="btn btn-primary">
                        确认
                    </button>
                </div>

            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div><!--.modal-->
    <div class="hide" id="log"></div>
</body>
</html>


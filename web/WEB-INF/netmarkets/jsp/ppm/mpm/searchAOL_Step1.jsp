<%@page pageEncoding="utf-8" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<table align="center">
	 <tr>
    	<td>编号：</td><td><input type="text" size="20" id="number"></td>&nbsp;&nbsp;&nbsp;&nbsp;
		<td>名称：</td><td><input type="text" size="20" id="name"/></td>
		<td><input type="button" onclick="doSearch()" value="提交查询" class="x-btn-text"/></td>
	</tr>
</table>	
<jsp:include page="${mvc:getComponentURL('ext.modular.builder.searchAOL')}" flush="true"> 
    <jsp:param name="flag" value="CI"/>
</jsp:include>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>
<script type="text/javascript">
function doSearch(){
	var parentWin = window.opener;
	var number = $("number").value;
	var name = $("name").value;
	var params ={number:number,name:name,lineOid:"",caOid:"",stageOid:"",flag:"CI"};
    PTC.jca.table.Utils.reload('ext.modular.builder.searchAOL', params, true);
}
</script>
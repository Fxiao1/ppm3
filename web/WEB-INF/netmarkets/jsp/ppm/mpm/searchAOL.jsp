<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib prefix="docmgnt" uri="http://www.ptc.com/windchill/taglib/docmgnt" %>

<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

 <jca:wizard title="${param.titleString}" buttonList="SearchCABtn">
	 <jca:wizardStep action="searchAOL_Step1"  type="partCI" />
 </jca:wizard>
<script type="text/javascript">
function submitIt(){
	var parentWin = window.opener;
	var table = PTC.jca.table.Utils.getTable("ext.modular.builder.searchAOL");
	var selections = table.getSelectionModel().getSelections();
	var oidtemp = selections[0].item.oid;
	alert("oidtemp:" + oidtemp);
	Ext.Ajax.request({
         method : 'POST',
         url : '/Windchill/servlet/Navigation/mpmprocessPlan',
         params:{
              "actionName":"addOperation",
              "aolOid":oidtemp
    },
    success : function(response, action) {
	  // 这里需要刷新父页面中关联表格的内容
      window.close();
     },
    failure:function(response){
       Ext.MessageBox.alert("错误",response.responseText);
     }
    });
}
</script>
 <%@ include file="/netmarkets/jsp/util/end.jspf"%>
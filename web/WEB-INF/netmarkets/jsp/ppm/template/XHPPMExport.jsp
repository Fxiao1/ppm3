<%@page import="ext.modular.common.XHReportExcelUtil"%>
<%@page import="ext.modular.common.CpReportExcelUtil"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="java.io.File"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.OutputStream"%>
<%@page import="wt.part.WTPart"%>
<%@page import="java.util.*"%>
<%@ page import="ext.modular.calculate.PPMCalculateDetailEntity" %>
<%@ page import="ext.modular.calculate.PPMCalculateDetailController" %>


<%
    String startStr = request.getParameter("startDate");
    String endStr = request.getParameter("endDate");
    List<PPMCalculateDetailEntity> excelContent = new ArrayList<PPMCalculateDetailEntity>();
    try {
        PPMCalculateDetailController controller = new PPMCalculateDetailController();
        excelContent = controller.getXHPPMCalculateDetailList(startStr,endStr);
        System.out.println("excelContent size() " + excelContent.size());

        XHReportExcelUtil reportExcel=new XHReportExcelUtil();
        reportExcel.setDataList(excelContent);
        //"图号" + "版本" +".xls"
        String fileName = "型号PPM.xls";
        String filePath = reportExcel.writeExcelFile(fileName);
        System.out.println(filePath);
        if (!"".equals(filePath)) {
            CpReportExcelUtil.downloadFile(filePath,response);
            //删除生成压缩文件
            File tempFile = new File(filePath);
            if (tempFile.exists()) {
                tempFile.delete();
            }
            response.flushBuffer();
            out.clear();
            out = pageContext.pushBody();
        }
    }catch(Exception e){
        System.out.println(e.getMessage());

%>
<script>
    alert("<%=e.getMessage()%>");
    window.open('', '_self');
    window.opener = null;
    window.close();
</script>
<%
} finally {
%>
<script>
    window.close();
</script>
<%
    }
%>
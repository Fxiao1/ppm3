<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="java.io.File"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.OutputStream"%>
<%@page import="wt.part.WTPart"%>
<%@page import="java.util.*"%>
<%@page import="ext.modular.common.ReportExcelUtil"%>
<%@ page import="ext.modular.calculate.PPMCalculateDetailEntity" %>
<%@ page import="ext.modular.calculate.PPMCalculateDetailController" %>
<%@ page import="ext.modular.common.ReportExcelUtil2" %>


<%
    String date = request.getParameter("dateTime");
    Map<String,Map<String,Integer>> excelContent = new LinkedHashMap<>();
    try {
        PPMCalculateDetailController controller = new PPMCalculateDetailController();
        excelContent = controller.getPPMData(date);
        System.out.println("excelContent size() " + excelContent.size());

        ReportExcelUtil2 reportExcel=new ReportExcelUtil2();
        reportExcel.setDataList(excelContent);
        String fileName = "PPM统计.xls";
        String filePath = reportExcel.writeExcelFile(fileName);
        System.out.println(filePath);
        if (!"".equals(filePath)) {
            ReportExcelUtil2.downloadFile(filePath,response);
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
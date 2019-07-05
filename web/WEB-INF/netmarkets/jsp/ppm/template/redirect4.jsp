<%@page import="wt.session.SessionHelper,java.io.File,wt.util.HTMLEncoder" %>

<%
String encodedQueryString = HTMLEncoder.encodeForHTMLAttribute(request.getQueryString());
System.out.println("encodedQueryString::::"+encodedQueryString);
String iFrameRelativeURL = "netmarkets/jsp/ppm/template/ppmReport.jsp?" + encodedQueryString;
%>
<iframe id="templateIFrame" name="templateIFrame" scrolling="no" frameborder="0" style="width:100%; height:800px" src="<%=iFrameRelativeURL%>" >
</iframe>
<script type="text/javascript">

</script>
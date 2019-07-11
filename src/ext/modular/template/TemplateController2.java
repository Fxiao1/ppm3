package ext.modular.template;

import ext.modular.common.ConnectionUtil;
import ext.modular.common.ResultUtils;
import ext.modular.procedure.ProcedureSer;
import ext.modular.templatelink.TemplatelinkSer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 *  des:新的模板方式
 *
 * @author fxiao
 * @date 2019/6/4 12:13
 */
@Controller
public class TemplateController2 {
    private final Logger log= LoggerFactory.getLogger(this.getClass());
    private Connection connection=null;
    public TemplateController2() {
    }
    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST,RequestMethod.HEAD })
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, WTException {
        connection=ConnectionUtil.getJdbcConnection();
        String jsonStr = "";
        String actionName = request.getParameter("actionName");
        log.info("actionName={}",actionName);
        System.out.println("进入模板Controller，actionName="+actionName);
        TemplateSer templateSer=new TemplateSer();
        //获取模板列表
        if("get".equals(actionName)){
            List templateList=templateSer.getModelList();
            if(templateList!=null){
                log.info("获取的模板列表长度为{}",String.valueOf(templateList.size()));
                jsonStr = ResultUtils.succ(templateList);
                log.info(jsonStr);
            }else{
                log.info("获取模板列表失败！templateList==null");
                jsonStr=ResultUtils.error("获取模板列表失败！");
            }
        }
        //存储或修改模板的方法
        else if("post".equals(actionName)){
            TemplateEntity templateEntity=new TemplateEntity();
            String templateIdStr=request.getParameter("id");
            log.info("templateIdStr={}",templateIdStr);
            System.out.println("正在执行存储或修改模板的方法，接收到的参数templateId="+templateIdStr);
            templateEntity.setName(request.getParameter("name"));
            //准备工序数据
            ProcedureSer procedureSer=new ProcedureSer();
            String []procedureList=request.getParameterValues("procedure_id");
            System.out.println("接收到的工序的列表为："+ Arrays.toString(procedureList));
            int templateId=0;

            if(StringUtils.isEmpty(templateIdStr)){
                //新增
                templateEntity.setId(0);
                TemplateEntity newTemplate=templateSer.add(templateEntity);
                if(newTemplate==null){
                    jsonStr=ResultUtils.error("插入模板数据成功，但是未插入工序关系失败");
                }else{
                    log.info("新增加的模板的id为：{}",newTemplate.getId());
                    System.out.println("新增加的模板的id为："+newTemplate.getId());
                    templateId=newTemplate.getId();
                    jsonStr=ResultUtils.succ(null,"新增成功");
                }
            }else{
                //修改
                templateEntity.setId(Integer.valueOf(templateIdStr));
                templateSer.update(templateEntity);
                jsonStr=ResultUtils.succ(null,"修改成功");
                templateId=templateEntity.getId();
            }
            //添加工序到模板里去
            log.info("templateId={}",templateId);
            if(templateId!=0&&procedureList!=null&&procedureList.length>0) {
                WTPrincipal currentUser = SessionHelper.manager.getPrincipal();
                String currentUserName=currentUser.getName();
                //采用的是全部删除再全部重新存储的方式
                procedureSer.deleteFromeTemplate(templateId);
                procedureSer.addIntoTemplate(templateId,procedureList,currentUserName,connection);
            }
        }else if("delete".equals(actionName)){
        	
            String templateId=request.getParameter("id");
            
            TemplatelinkSer templink = new TemplatelinkSer();
            
            
            log.info("templateId={}",templateId);
            if(StringUtils.isEmpty(templateId)){
                jsonStr= ResultUtils.error("删除失败，缺少id信息");
            }else{
            	templink.deleteTemplinkByTemplateId(Integer.valueOf(templateId));
            	
                templateSer.delete(Integer.valueOf(templateId));
                jsonStr=ResultUtils.succ(null);
            }
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store,no-cache");
        response.setHeader("Pragma", "no-cache");
        try {
            PrintWriter pw = response.getWriter();
            if (null != jsonStr){
                pw.print(jsonStr);
            }
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            ConnectionUtil.close(connection,null);
        }

    }
}

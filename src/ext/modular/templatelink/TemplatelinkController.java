package ext.modular.templatelink;

import ext.modular.common.ResultUtils;
import ext.modular.procedure.ProcedureEntity;
import ext.modular.procedure.ProcedureSer;
import ext.modular.template.TemplateEntity;
import ext.modular.template.TemplateSer;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

@Controller
public class TemplatelinkController {
    private final Logger log= LoggerFactory.getLogger(this.getClass());
    private static Gson gson = new Gson();
    private Connection connection=null;
    
    public TemplatelinkController() {
    }
    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST,RequestMethod.HEAD })
    public String processRequest(HttpServletRequest request, HttpServletResponse response) throws WTException{
    	
    	TemplateSer templateSer=new TemplateSer();
    	
        String jsonStr="";
        String actionName=request.getParameter("actionName");
        log.info("actionName={}",actionName);
        TemplatelinkSer ser=new TemplatelinkSer();
        //获取工序模板关系列表
        if("get".equals(actionName)){
            List<TemplatelinkEntity> templatelinkList =ser.getTemplinkList();
            if(templatelinkList!=null){
                log.info("获取工序模板关系列表成功");
                jsonStr=ResultUtils.succ(templatelinkList);
                log.info("工序模板关系列表characList为",jsonStr);
            }else{
                log.info("获取工序模板关系列表失败！");
                jsonStr=ResultUtils.error("获取工序模板关系列表失败！");
            }
        }
        //存储或修改模板的方法
        else if("post".equals(actionName)){
            TemplateEntity templateEntity=new TemplateEntity();
            String templateId=request.getParameter("id");
            log.info("templateId={}",templateId);
            System.out.println("正在执行存储或修改模板的方法，接收到的参数templateId="+templateId);
            templateEntity.setName(request.getParameter("name"));
            //准备工序数据
            ProcedureSer procedureSer=new ProcedureSer();
            //n
            TemplatelinkSer templateLink = new TemplatelinkSer();
            String []procedureList=request.getParameterValues("procedure_id");
            System.out.println("接收到的工序的列表为："+ Arrays.toString(procedureList));
            int newTemplateId=0;

            if(StringUtils.isEmpty(templateId)){
                //新增
                templateEntity.setId(0);
                TemplateEntity newTemplate=templateSer.add(templateEntity);
                if(newTemplate==null){
                    jsonStr=ResultUtils.error("插入模板数据成功，但是未插入工序关系失败");
                }else{
                    log.info("新增加的模板的id为：{}",newTemplate.getId());
                    System.out.println("新增加的模板的id为："+newTemplate.getId());
                    newTemplateId=newTemplate.getId();
                    jsonStr=ResultUtils.succ(null,"新增成功");
                }
            }else{
                //修改
                templateEntity.setId(Integer.valueOf(templateId));
                templateSer.update(templateEntity);
                jsonStr=ResultUtils.succ(null,"修改成功");
                newTemplateId=templateEntity.getId();
            }
            //添加工序到模板里去
            log.info("newTemplateId={}",newTemplateId);
            if(newTemplateId!=0&&procedureList!=null&&procedureList.length>0) {
                WTPrincipal currentUser = SessionHelper.manager.getPrincipal();
                String currentUserName=currentUser.getName();
                //采用的是全部删除再全部重新存储的方式
                templateLink.deleteTemplinkByTemplateId(newTemplateId);
               // procedureSer.deleteFromeTemplate(newTemplateId);
                //准备工序模板关系数据
                for(int i =0;i<procedureList.length;i++)
                {
                TemplatelinkEntity temp = new TemplatelinkEntity();
                temp.setCreator(currentUserName);
                TemplateEntity template = new TemplateEntity();
                template.setId(newTemplateId);
                temp.setTemplateEntity(template);
               ProcedureEntity proce = procedureSer.getProcedureById(Integer.parseInt(procedureList[i]));
               temp.setProcedureEntity(proce);
                
                templateLink.addTemplink(temp);
                //procedureSer.addIntoTemplate(newTemplateId,procedureList,currentUserName,connection);
                }
            }
        }
        /*//新增
        else if("post".equals(actionName)){
            TemplatelinkEntity entity=new TemplatelinkEntity();
            String templinkId=request.getParameter("id");
            entity.setCreator(request.getParameter("creator"));
            entity.setCreateTime(new Date());
            entity.setUpdateTime(new Date());
            TemplateEntity templateEntity=new TemplateEntity();
            templateEntity.setId(Integer.parseInt(request.getParameter("template_id")));
            entity.setTemplateEntity(templateEntity);
            ProcedureEntity procedureEntity=new ProcedureEntity();
            procedureEntity.setId(Integer.parseInt(request.getParameter("tw_id")));
            procedureEntity.setName(request.getParameter("tw_name"));
            procedureEntity.setCreator(request.getParameter("tw_creator"));
            entity.setProcedureEntity(procedureEntity);
            entity.setPpm_order(Integer.parseInt(request.getParameter("ppm_order")));
            log.info("TemplatelinkEntity:",entity.toString());
            if(StringUtils.isEmpty(templinkId)){
                entity.setId(0);
                ser.addTemplink(entity);
                jsonStr=ResultUtils.succ(null,"新增成功");
            }else{
                entity.setId(Integer.parseInt(templinkId));
                ser.updateTemplink(entity);
                jsonStr=ResultUtils.succ(null,"修改成功");
            }

        }*/
        //删除
        else if("delete".equals(actionName)){
            String id =(request.getParameter("id"));
            if(StringUtils.isEmpty(id)){
                jsonStr= ResultUtils.error("删除失败，缺少id信息");
            }else{
                ser.deleteTemplinkByTemplateId(Integer.parseInt(id));
                jsonStr=ResultUtils.succ(null);
            }
        }
        //根据模板id和工序id删除
        else if("deleteById".equals(actionName)){
            String id =(request.getParameter("id"));
            String tempId = (request.getParameter("tempId"));
            if(StringUtils.isEmpty(id)){
                jsonStr= ResultUtils.error("删除失败，缺少id信息");
            }else{
                ser.deleteTemplinkByTemplateIdAndProcedureId(Integer.parseInt(tempId),Integer.parseInt(id));
                jsonStr=ResultUtils.succ(null);
            }
        }
        //删除根据模板id
        else if("deleteByTemplateId".equals(actionName)){
            String templatedId =(request.getParameter("templateId"));
            if(StringUtils.isEmpty(templatedId)){
                jsonStr= ResultUtils.error("删除失败，缺少id信息");
            }else{
                ser.deleteTemplinkByTemplateId(Integer.parseInt(templatedId));
                jsonStr=ResultUtils.succ(null);
            }
        }
        //添加工序集合到关系列表
        else if("addToTemplate".equals(actionName)){
        
        TemplatelinkSer templatelinkSer=new TemplatelinkSer();
        String jsonData = request.getParameter("templatelinkList");
        List<TemplatelinkEntity> templatelinkList = gson.fromJson(jsonData, new TypeToken<List<TemplatelinkEntity>>() {
        }.getType());
        templatelinkSer.addTemplink(templatelinkList);
        ResultUtils.succ(null,"增加成功");
        }

        response.setContentType("text/html;Charset=UTF-8");
        response.setHeader("Cache-Control", "no-store,no-cache");
        response.setHeader("Pragma", "no-cache");
        try {
            PrintWriter pw =response.getWriter();
            if(jsonStr!=null){
                pw.print(jsonStr);
            }
            pw.flush();
            pw.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return jsonStr;
    }
}


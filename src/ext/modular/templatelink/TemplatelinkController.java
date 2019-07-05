package ext.modular.templatelink;

import ext.modular.common.ResultUtils;
import ext.modular.procedure.ProcedureEntity;
import ext.modular.template.TemplateEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

@Controller
public class TemplatelinkController {
    private final Logger log= LoggerFactory.getLogger(this.getClass());

    public TemplatelinkController() {
    }
    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST,RequestMethod.HEAD })
    public String processRequest(HttpServletRequest request, HttpServletResponse response){
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
        //新增
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

        }
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
        //删除
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


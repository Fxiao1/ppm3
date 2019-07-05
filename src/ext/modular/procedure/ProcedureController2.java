package ext.modular.procedure;

import ext.modular.common.ConnectionUtil;
import ext.modular.common.ResultUtils;
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
import java.util.List;

/**
 * des:
 *
 * @author fxiao
 * @date 2019/6/6 10:02
 */
@Controller
public class ProcedureController2 {
    private final Logger log= LoggerFactory.getLogger(this.getClass());
    private Connection connection=null;
    public ProcedureController2() {
    }


    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST,RequestMethod.HEAD })
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException, WTException {
        connection= ConnectionUtil.getConnection();
        String jsonStr = "";
        String actionName = request.getParameter("actionName");
        log.info("actionName={}",actionName);
        ProcedureSer procedureSer=new ProcedureSer();
        if("get".equals(actionName)) {
            log.info("获取全部工序列表");
            List templateList = procedureSer.getProcedureList();
            jsonStr=ResultUtils.succ(templateList);
        }else if("getByTemplate".equals(actionName)){
            log.info("根据模板获得工序");
            String templateIdStr=request.getParameter("templateId");
            log.info("获取的templateIdStr={}",templateIdStr);
            System.out.println("获取的templateIdStr="+templateIdStr);
            if(StringUtils.isEmpty(templateIdStr)){
                jsonStr=ResultUtils.error("没有找到模板id");
                System.out.println("没有找到模板id");
            }else{
                int templateId=Integer.valueOf(templateIdStr);
                List<ProcedureEntity> list=procedureSer.getByTemplate(templateId,connection);
                jsonStr=ResultUtils.succ(list);
            }
        }
        else if("add".equals(actionName)){
            log.info("增加工序");
            String name=request.getParameter("procedureName");
            String creatorName="无名";
            ProcedureEntity entity=new ProcedureEntity();
            entity.setName(name);
            entity.setCreator(creatorName);
            procedureSer.addProcedure(entity);
            jsonStr=ResultUtils.succ(null,"工序保存成功");
            
        }else if("delete".equals(actionName)){
            String id =request.getParameter("id");
            if(StringUtils.isEmpty(id)){
                jsonStr= ResultUtils.error("删除失败，缺少id信息");
            }else{
            	procedureSer.deleteProcedure(Integer.parseInt(id));
                jsonStr=ResultUtils.succ(null);
            }
        }else if("addToTemplate".equals(actionName)){
        	
            TemplatelinkSer templatelinkSer=new TemplatelinkSer();
            String templateId=request.getParameter("templateId");
            String temp=request.getParameter("procedureId");
            String []procedureIds=temp.split(",");
            WTPrincipal currentUser = SessionHelper.manager.getPrincipal();
            String currentUserName=currentUser.getName();
            templatelinkSer.addTemplink(templateId,procedureIds,currentUserName);
            ResultUtils.succ(null,"增加成功");
        }else{
            log.info("其他分支");
            jsonStr=ResultUtils.error("actionName错误，actionName="+actionName);
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store,no-cache");
        response.setHeader("Pragma", "no-cache");
        ConnectionUtil.close(connection,null);
        try {
            PrintWriter pw = response.getWriter();
            if (null != jsonStr){
                pw.print(jsonStr);
            }
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

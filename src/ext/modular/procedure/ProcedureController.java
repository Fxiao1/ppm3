package ext.modular.procedure;

import ext.modular.common.ResultUtils;
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

/**
 * des:
 *
 * @author fxiao
 * @date 2019/6/6 10:02
 */
@Controller
public class ProcedureController {
        private final Logger log= LoggerFactory.getLogger(this.getClass());

        public ProcedureController() {
        }
        @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST,RequestMethod.HEAD })
        public void processRequest(HttpServletRequest request, HttpServletResponse response){
            String jsonStr="";
            String actionName=request.getParameter("actionName");
            log.info("actionName={}",actionName);
            ProcedureSer ser=new ProcedureSer();
            //获取工序列表
            if("getProcedureList".equals(actionName)){
                List<ProcedureEntity> procedureList =ser.getProcedureList();
                if(procedureList!=null){
                    log.info("获取工序列表成功");
                    jsonStr= ResultUtils.succ(procedureList);
                    log.info("工序列表procedureList为",jsonStr);
                }else{
                    log.info("获取工序列表失败！");
                    jsonStr=ResultUtils.error("获取工序列表失败！");
                }
            }
            //新增
            else if("postProcedure".equals(actionName)){
                ProcedureEntity entity=new ProcedureEntity();
                String procedureId=request.getParameter("id");
                entity.setName(request.getParameter("name"));
                entity.setCreator(request.getParameter("creator"));
                entity.setCreateTime(new Date());
                entity.setUpdateTime(new Date());
                log.info("ProcedureEntity:",entity.toString());
                if(StringUtils.isEmpty(procedureId)){
                    entity.setId(0);
                    ser.addProcedure(entity);
                    jsonStr=ResultUtils.succ(null,"新增成功");
                }else{
                    entity.setId(Integer.parseInt(procedureId));
                    ser.updateProcedure(entity);
                    jsonStr=ResultUtils.succ(null,"修改成功");
                }

            }
            //删除
            else if("deleteProcedure".equals(actionName)){
                String id =(request.getParameter("id"));
                if(StringUtils.isEmpty(id)){
                    jsonStr= ResultUtils.error("删除失败，缺少id信息");
                }else{
                    ser.deleteProcedure(Integer.parseInt(id));
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
        }
    }


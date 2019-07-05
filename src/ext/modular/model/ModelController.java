package ext.modular.model;

import ext.modular.common.ConnectionUtil;
import ext.modular.common.ResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import wt.util.WTException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Controller
public class ModelController {
    private final Logger log= LoggerFactory.getLogger(this.getClass());
    private Connection conn=null;
    public ModelController() {
    }

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST,RequestMethod.HEAD })
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws WTException, SQLException, ClassNotFoundException {
        conn= ConnectionUtil.getConnection();
        String jsonStr="";
        String actionName=request.getParameter("actionName");
        log.info("actionName={}",actionName);
        ModelSer ser=new ModelSer();
        if("get".equals(actionName)){
            List<ModelEntity> modelList=ser.getProduct();
            log.info("modelList的长度为{}",String.valueOf(modelList.size()));
            modelList=ser.getModel(modelList,conn);
            if(modelList!=null){
                log.info("获取型号列表成功");
                jsonStr= ResultUtils.succ(modelList);
            }else{
                log.info("获取型号列表失败！");
                jsonStr=ResultUtils.error("获取型号列表失败！");
            }

        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;Charset=UTF-8");
        response.setHeader("Cache-Control", "no-store,no-cache");
        response.setHeader("Pragma", "no-cache");
        ConnectionUtil.close(conn,null);
        try {
            PrintWriter pw =response.getWriter();
            if(null!=jsonStr){
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
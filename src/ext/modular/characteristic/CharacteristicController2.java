package ext.modular.characteristic;

import com.google.gson.Gson;
import ext.modular.common.ConnectionUtil;
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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Controller
public class CharacteristicController2 {
    private final Logger log= LoggerFactory.getLogger(this.getClass());

    public CharacteristicController2() {
    }
    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST,RequestMethod.HEAD })
    public void characteristicRequest(HttpServletRequest request, HttpServletResponse response){
        Connection conn=null;
        try {
            conn=ConnectionUtil.getJdbcConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String jsonStr="";
        String actionName=request.getParameter("actionName");
        log.info("actionName={}",actionName);
        CharacteristicSer ser=new CharacteristicSer();
        //获取工序检验特性列表
        if("getCharacList".equals(actionName)){
            //工序id
            String procedureIdStr=request.getParameter("procedureId");
            int procedureId=Integer.valueOf(procedureIdStr);
            List<CharacteristicEntity> characList =ser.getCharacList(procedureId,conn);
            if(characList!=null){
                log.info("获取工序检验特性列表成功");
                jsonStr=ResultUtils.succ(characList);
                log.info("工序id为{}的工序检验特性列表characList为：“{}”",procedureIdStr,jsonStr);
            }else{
                log.info("获取工序检验特性列表失败！");
                jsonStr=ResultUtils.error("获取工序检验特性列表失败！");
            }
        }
        //获取一个
        else if("getCharac".equals(actionName)){
            String idStr=request.getParameter("id");
            if(StringUtils.isEmpty(idStr)){
                jsonStr=ResultUtils.error("未获取到id");
            }else{
                CharacteristicEntity charac = ser.get(Integer.valueOf(idStr));
                jsonStr=ResultUtils.succ(charac);
            }
        }
        //新增
        else if("postCharac".equals(actionName)){
            CharacteristicEntity entity=new CharacteristicEntity();
            String characteristicId=request.getParameter("id");
            entity.setName(request.getParameter("name"));
            entity.setCreator("无名");

            int procedureId=Integer.parseInt(request.getParameter("twId"));
            entity.setTotal(Integer.parseInt(request.getParameter("total")));
            entity.setCoefficient(Integer.parseInt(request.getParameter("coefficient")));

            Gson gson=new Gson();
            log.info("特性:“{}”；工序id：“{}”",gson.toJson(entity),procedureId);
            if(StringUtils.isEmpty(characteristicId)){
                entity.setId(0);
                ser.addCharac(entity,procedureId);
                jsonStr=ResultUtils.succ(null,"新增成功");
            }else{
                entity.setId(Integer.parseInt(characteristicId));
                ser.updateCharac(entity,procedureId);
                jsonStr=ResultUtils.succ(null,"修改成功");
            }

        }
        //删除
        else if("deleteCharac".equals(actionName)){
            String id =request.getParameter("id");
            if(StringUtils.isEmpty(id)){
                jsonStr= ResultUtils.error("删除失败，缺少id信息");
            }else{
                ser.deleteCharac(Integer.parseInt(id));
                jsonStr=ResultUtils.succ(null);
            }
        }

        response.setContentType("text/html;Charset=UTF-8");
        response.setHeader("Cache-Control", "no-store,no-cache");
        response.setHeader("Pragma", "no-cache");
        ConnectionUtil.close(conn,null);
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


package ext.modular.form;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.util.Map;

/**
 * des:表单控制层
 *
 * @author renkai
 * @date 2019/6/16
 */
@Controller
public class FormController {
    private static Gson gson = new Gson();
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    //总是莫名其妙报“已关闭的链接”所以就再从这里获取传过去试试
    Connection connection=null;

    public FormController() {

    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.HEAD})
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws SQLException, ClassNotFoundException {
        connection= ConnectionUtil.getConnection();
        String jsonStr = "";
        String actionName = request.getParameter("actionName");
        log.info("ext.modular.form.FormController#processRequest#actionName={}", actionName);
        FormSer service = new FormSer();
        //查看表单
        if ("get".equals(actionName)) {
            String logo = request.getParameter("logo");
            List<FormEntity> formList = service.getFormList(Integer.parseInt(logo));
            if (formList != null) {
                log.info("获取的表单列表长度为{}", String.valueOf(formList.size()));
                jsonStr = ResultUtils.succ(formList, "获取表单列表成功");
                log.info(jsonStr);
            } else {
                log.info("获取表单列表失败！");
                jsonStr = ResultUtils.error("获取表单列表失败！");
            }

        }
        //根据产品id获取该产品下的表单列表
        else if ("getByProId".equals(actionName)) {
            String productId = request.getParameter("productId");
            log.info("产品id为{}", productId);
            List<Map<String, Object>> formList = service.getlistByProduct(Integer.parseInt(productId));
            if (formList != null) {
                log.info("该产品下的表单列表个数为{}", String.valueOf(formList.size()));
                jsonStr = ResultUtils.succ(formList, "获取产品下表单列表成功");
            } else {
                log.info("获取表单列表失败！");
                jsonStr = ResultUtils.error("获取产品下表单列表失败！");
            }
        }
        //获得产品具体列表的详细数据，对应到数据库表的每一行
        else if("getItemByProId".equals(actionName)){
            String productId = request.getParameter("productId");
            log.info("产品id为{}", productId);
            List<FormEntity> formList = service.getItemFormList(Integer.parseInt(productId));
            if (formList != null) {
                log.info("该产品下的表单列表个数为{}", String.valueOf(formList.size()));
                jsonStr = ResultUtils.succ(formList, "获取产品下表单列表成功");
            } else {
                log.info("获取表单列表失败！");
                jsonStr = ResultUtils.error("获取产品下表单列表失败！");
            }
        }
        else if("getItemByProIdLogo".equals(actionName)){
            String productIdStr = request.getParameter("productId");
            int productId=Integer.parseInt(productIdStr);
            int logo=Integer.parseInt(request.getParameter("logo"));
            log.info("产品id为{},logo={}", productId,logo);
            List<FormEntity> formList = service.getItemFormList(productId,logo,connection);
            jsonStr =ResultUtils.succ(formList);
        }
        else if ("post".equals(actionName)) {

            int logo = service.getLogo();
            String jsonData = request.getParameter("formList");
            log.info("接收的json数据为{}", jsonData);
            //json串转换为List<FormEntity>
            List<FormEntity> formEntityList = gson.fromJson(jsonData, new TypeToken<List<FormEntity>>() {
            }.getType());
            for (FormEntity formEntity : formEntityList) {
                String productId = Integer.toString(formEntity.getProductId());
                if (StringUtils.isEmpty(productId)) {
                    jsonStr = ResultUtils.error("缺少产品id");
                } else {
                    formEntity.setLogo(logo);
                    service.add(formEntity,connection);
                    log.info("logo为{}", logo);
                }
            }
            jsonStr = ResultUtils.succ(formEntityList, "新增表单列表成功");
            log.info(jsonStr);


        }
        /**根据表单标识修改表单数据
         1. 先调用get，根据表单标识获取对象表单列表
         2.然后根据表单标识删除表单列表
         3.点击保存然后调用新增(get)API
         */
        else if ("update".equals(actionName)) {
            String jsonData = request.getParameter("formList");
            //json串转换为List<FormEntity>
            List<FormEntity> formEntityList = gson.fromJson(jsonData, new TypeToken<List<FormEntity>>() {
            }.getType());
            int productId=formEntityList.get(0).getProductId();
            int logo=formEntityList.get(0).getLogo();
            log.info("当前正在执行ext.modular.form.FormController类的update方法，接收到的产品id={},表单标识={}",productId,logo
            );
            if (StringUtils.isEmpty(productId)) {
                jsonStr = ResultUtils.error("缺少产品id");
            } else {
                service.delete(logo);
                for (FormEntity form : formEntityList) {
                    service.add(form,connection);
                }
                jsonStr=ResultUtils.succ(null,"表单修改成功");
//                log.info("表单标识logo为{},产品id ", logo);
//                List<FormEntity> formList = new LinkedList<FormEntity>();
//                formList = service.getFormList(logo);
//                jsonStr = ResultUtils.succ(formList, "查询表单列表成功");
//                log.info("根据标识查询到表单listformList为{}", jsonStr);
//                //根据表单标识删除表单列表
//                service.delete(logo);
//                log.info("根据表单标识删除表单成功");
            }
        }
        //根据表单标识删除整个表单数据
        else if ("delete".equals(actionName)) {
            String logo = request.getParameter("formSign");
            if (StringUtils.isEmpty(logo)) {
                jsonStr = ResultUtils.error("删除失败，缺少表单标识信息");
            } else {
                service.delete(Integer.parseInt(logo));
                jsonStr = ResultUtils.succ(null, "根据表单标识删除表单成功");
            }

        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store,no-cache");
        response.setHeader("Pragma", "no-cache");
        ConnectionUtil.close(connection,null);
        try {
            PrintWriter pw = response.getWriter();
            if (null != jsonStr) {
                pw.print(jsonStr);
            }
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
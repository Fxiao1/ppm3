package ext.modular.form;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ext.modular.common.ConnectionUtil;
import ext.modular.common.ResultUtils;
import ext.modular.datainstance.DatainstanceSer;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
                    service.add(formEntity,connection,"add",null);
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
            int logo=Integer.parseInt(request.getParameter("logo"));

          //根据logo查出表单数据实例表中的生产数量。
            DatainstanceSer dataSer = new DatainstanceSer();
            int quantity = dataSer.getquantityByLogo(logo, connection);
            System.out.println("产品总数："+quantity);
            int newQuantity = 0;

            //创建人和创建时间应该得以保留
            List<FormEntity> list=service.getFormList(logo);
            String creator=list.get(0).getCreator();
            Date createTime=list.get(0).getCreateTime();
            String templateId = list.get(0).getTemplateId();
            String templateName = list.get(0).getTemplateName();
            DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:dd");
            log.info("删除之前获得旧数据（logo={}）的创建时间={}，创建人={}",logo,df.format(createTime),creator);
            service.delete(logo);
            if(jsonData!=null){
                List<FormEntity> formEntityList = gson.fromJson(jsonData, new TypeToken<List<FormEntity>>() {
                }.getType());
                log.info("当前正在执行ext.modular.form.FormController类的update方法，表单标识={}",logo);
                for (FormEntity form : formEntityList) {
                	newQuantity = form.getQuantity();

                }
                if(newQuantity>quantity) {
                	//修改表单数据实例中的产品总数
                	dataSer.updateData(logo, newQuantity, connection);

                	 for (FormEntity form : formEntityList) {
                		 form.setCreator(creator);
                         form.setCreateTime(createTime);
                         form.setTemplateId(templateId);
                         form.setTemplateName(templateName);
                		 service.add(form,connection,"update",df.format(createTime));
                     }
            	}else {
            		jsonStr = ResultUtils.error("修改失败，修改产品总数小于原产品总数");
            	}

            }
            jsonStr=ResultUtils.succ(null,"表单修改成功");
        }
        //根据表单标识删除整个表单数据
        else if ("delete".equals(actionName)) {
            String logoStr = request.getParameter("formSign");
            if (StringUtils.isEmpty(logoStr)) {
                jsonStr = ResultUtils.error("删除失败，缺少表单标识信息");
            } else {
                Integer logo=Integer.parseInt(logoStr);
                service.delete(logo);
                //删除表单数据实例
                DatainstanceSer datainstanceSer=new DatainstanceSer();
                datainstanceSer.deleteByFormLogo(logo);
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

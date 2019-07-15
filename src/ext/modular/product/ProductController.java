package ext.modular.product;

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
import java.util.List;
/**
 * des:
 *  产品的controller层
 * @author renkai
 * @date 2019/6/12
 */

@Controller
public class ProductController {
    private final Logger log= LoggerFactory.getLogger(this.getClass());

    public ProductController() {
    }
    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST,RequestMethod.HEAD })
    public void processRequest(HttpServletRequest request, HttpServletResponse response){
        String jsonStr="";
        String actionName=request.getParameter("actionName");
        log.info("actionName={}",actionName);
        ProductSer ser=new ProductSer();
        //获取产品列表
        if("get".equals(actionName)){
            List<ProductEntity> productList =ser.getProductList();
            if(productList!=null){
                log.info("获取产品列表成功");
                jsonStr=ResultUtils.succ(productList);
            }else{
                log.info("获取产品列表失败！");
                jsonStr=ResultUtils.error("获取产品列表失败！");
            }
        }
        //根据id获取产品
        else if("getById".equals(actionName)){
            String productId=request.getParameter("id");
            ProductEntity entity=new ProductEntity();
            if(productId!=null){
                log.info("获取产品id为“{}”成功",productId);
                entity=ser.getProductById(Integer.parseInt(productId));
                log.info("entity为{}",entity.toString());
                jsonStr=ResultUtils.succ(entity,"获取单个产品成功");
            }else{
                log.info("该产品不存在");
                jsonStr=ResultUtils.error("获取单个产品失败,产品不存在");
            }

        }
        //新增
        else if("post".equals(actionName)){
            ProductEntity entity=new ProductEntity();
            String productId=request.getParameter("id");
            String productName=request.getParameter("name");
            entity.setName(request.getParameter("name"));
            entity.setCreator(request.getParameter("creator"));
            entity.setModel_id(request.getParameter("modelId"));
            entity.setProduct_code(request.getParameter("productCode"));
            entity.setModel_type(request.getParameter("model_type"));
            log.info("ProductEntity={}",entity.toString());
            log.info("productName={}",productName);

            if(StringUtils.isEmpty(productId)){
                entity.setId(0);
                ser.addProduct(entity);
                jsonStr=ResultUtils.succ(null,"新增成功");
            }else{
                entity.setId(Integer.parseInt(productId));
                ser.updateProduct(entity);
                jsonStr=ResultUtils.succ(null,"修改成功");
            }

        }
        //删除
        else if("delete".equals(actionName)){
            String id =request.getParameter("id");
            log.info("id={}",id);
            if(StringUtils.isEmpty(id)){
                jsonStr= ResultUtils.error("删除失败，缺少id信息");
            }else{
                jsonStr=ser.deleteProduct(Integer.parseInt(id));
            }
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store,no-cache");
        response.setHeader("Pragma", "no-cache");
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
package ext.modular.product;

import ext.modular.common.ConnectionUtil;
import ext.modular.common.ResultUtils;
import ext.modular.datainstance.DatainstanceSer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTException;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * des:
 *  产品的ser层
 * @author renkai
 * @date 2019/6/12
 */

@Service
public class ProductSer {
    private final Logger log= LoggerFactory.getLogger(this.getClass());
    private String selectField="id,createTime,updateTime,creator,name,model_id,product_code,model_type";
    private String insertField="id,creator,name,model_id,product_code,model_type";
    /**
     * 获取产品列表
     * @Author renkai
     * @Description
     * @Date  2019/6/12
     * @param
     * @return java.util.List<ext.modular.product.ProductEntity>
     **/
    public List<ProductEntity> getProductList(){
        Connection connection=null;
        Statement statement=null;
        ResultSet resultSet=null;
        List<ProductEntity> productList=new LinkedList<ProductEntity>();
        try{
            connection=ConnectionUtil.getJdbcConnection();
            statement= connection.createStatement();
            String sqlStr=String.format("SELECT %s FROM ppm_product ORDER BY createTime",selectField);
            resultSet=statement.executeQuery(sqlStr);
            log.info("查询的sql为“{}”,查询到的结果resultSet为：“{}”",sqlStr,resultSet);
            if(resultSet!=null){
                while (resultSet.next()){
                    ProductEntity productEntity=new ProductEntity();
                    productEntity.setId(resultSet.getInt("ID"));
                    productEntity.setCreator(resultSet.getString("creator"));
                    productEntity.setName(resultSet.getString("name"));
                    productEntity.setCreateTime(resultSet.getDate("createTime"));
                    productEntity.setUpdateTime(resultSet.getTime("updateTime"));
                    productEntity.setModel_id(resultSet.getString("model_id"));
                    productEntity.setProduct_code(resultSet.getString("product_code"));
                    productEntity.setModel_type(resultSet.getString("model_type"));
                    productList.add(productEntity);
                }
            }
        }

        catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection,statement);
        }
        return productList;
    }
    /**
     * 根据型号id获取产品列表
     * @Author renkai
     * @Description
     * @Date  2019/6/15
     * @param id
     * @return java.util.List<ext.modular.product.ProductEntity>
     **/
    public List<ProductEntity> getProductListByModelId(String id,Connection conn){
        Statement statement=null;
        ResultSet resultSet=null;
        List<ProductEntity> productList=new LinkedList<ProductEntity>();
        try{
            statement= conn.createStatement();
            String sqlStr=String.format("SELECT %s FROM ppm_product WHERE model_id='%s' ORDER BY createTime",selectField,id);
            resultSet=statement.executeQuery(sqlStr);
            log.info("查询的sql为“{}”,查询到的结果resultSet为：“{}”",sqlStr,resultSet);
            if(resultSet!=null){
                while (resultSet.next()){
                    ProductEntity productEntity=new ProductEntity();
                    productEntity.setId(resultSet.getInt("ID"));
                    productEntity.setCreator(resultSet.getString("creator"));
                    productEntity.setName(resultSet.getString("name"));
                    productEntity.setCreateTime(resultSet.getDate("createTime"));
                    productEntity.setUpdateTime(resultSet.getTime("updateTime"));
                    productEntity.setModel_id(resultSet.getString("model_id"));
                    productEntity.setProduct_code(resultSet.getString("product_code"));
                    productEntity.setModel_type(resultSet.getString("model_type"));
                    productList.add(productEntity);
                }
            }
        }

        catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }
    /**
     * 获取单个产品
     * @Author renkai
     * @Description
     * @Date  2019/6/15
     * @param id
     * @return ProductEntity
     **/
     public ProductEntity getProductById(int id){
         Connection connection=null;
         Statement statement=null;
         ResultSet resultSet=null;
         ProductEntity productEntity=new ProductEntity();
         try{
             connection=ConnectionUtil.getJdbcConnection();
             statement=connection.createStatement();
             String sqlStr=String.format("SELECT %s FROM ppm_product WHERE id=%s",
                     selectField, id);
             log.info("新增单个产品的sql为“{}”",sqlStr);
             resultSet=statement.executeQuery(sqlStr);
             log.info("resultSet：{}",resultSet);
             if(resultSet!=null) {
                 while (resultSet.next()) {
                     productEntity.setId(resultSet.getInt("ID"));
                     productEntity.setCreator(resultSet.getString("creator"));
                     productEntity.setName(resultSet.getString("name"));
                     productEntity.setCreateTime(resultSet.getDate("createTime"));
                     productEntity.setUpdateTime(resultSet.getDate("updateTime"));
                     productEntity.setModel_id(resultSet.getString("model_id"));
                     productEntity.setProduct_code(resultSet.getString("product_code"));
                     productEntity.setModel_type(resultSet.getString("model_type"));
                     log.info("productEntity为{}",productEntity);
                 }
             }

         }

         catch (SQLException e) {
             e.printStackTrace();
         } catch (ClassNotFoundException e) {
             e.printStackTrace();
         }
         return productEntity;
     }
    /**
     * 增加产品
     * @Author renkai
     * @Description
     * @Date  2019/6/12
     * @param product
     * @return void
     **/
    public void addProduct(ProductEntity product){
        Connection connection=null;
        PreparedStatement ps=null;
        try{
            connection=ConnectionUtil.getConnection();
            String sqlStr=String.format("INSERT INTO ppm_product(%s) VALUES(ppm_seq.nextval,?,?,?,?,?)",
                    insertField);
            ps=connection.prepareStatement(sqlStr);
            WTPrincipal currentUser = SessionHelper.manager.getPrincipal();
            String currentUserName=currentUser.getName();
            ps.setString(1,currentUserName);
            ps.setString(2,product.getName());
            ps.setString(3,product.getModel_id());
            ps.setString(4,product.getProduct_code());
            ps.setString(5,product.getModel_type());
            int row=ps.executeUpdate();
            log.info("插入产品,insertRow={}条",row);
        }
        catch (SQLException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection,ps);
        }
    }
    /**
     * 修改产品
     * @Author renkai
     * @Description
     * @Date  2019/6/12
     * @param productEntity
     * @return void
     **/
    public void updateProduct(ProductEntity productEntity){
        Connection connection=null;
        PreparedStatement ps=null;
        try{
            connection=ConnectionUtil.getJdbcConnection();
            String sqlStr=String.format("UPDATE ppm_product SET name=?, model_id=?"+
                            ", product_code=? ,model_type=? WHERE id =?",
                    productEntity.getName());
            log.info("修改的sql为“{}”",sqlStr);
            ps=connection.prepareStatement(sqlStr);
            int num=1;
            ps.setString(num++,productEntity.getName());
            ps.setString(num++,productEntity.getModel_id());
            ps.setString(num++,productEntity.getProduct_code());
            ps.setString(num++,productEntity.getModel_type());
            ps.setInt(num++,productEntity.getId());
            ps.execute();
        }

        catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection,ps);
        }
    }
    /**
     * 删除产品
     * @Author renkai
     * @Description
     * @Date  2019/6/12
     * @param id
     * @return void
     **/
    public String deleteProduct(int id){
        Connection connection=null;
        Statement statement=null;
        try{
            connection= ConnectionUtil.getConnection();
            statement=connection.createStatement();
            //先检查是否有表单了
            String checkSql="SELECT count(ID) items_size FROM PPM_FORM WHERE PRODUCT_id="+id;
            ResultSet resultSet = statement.executeQuery(checkSql);
            if(resultSet!=null&&resultSet.next()){
                int itemsSize=resultSet.getInt("items_size");
                if(itemsSize>0){
                    return ResultUtils.error("发现该产品下已有表单，请先删除表单后再重新尝试");
                }else{
                    log.info("清理表单实例里面的相关数据");
                    //清理表单实例里面的相关数据
                    DatainstanceSer datainstanceSer=new DatainstanceSer();
                    datainstanceSer.deleteByProduct(id);
                }
            }
            String sqlStr=String.format("DELETE FROM ppm_product where ID ='%s'",id);
            log.info("删除的sql为“{}”",sqlStr);
            log.info("id={}",id);
            statement.executeQuery(sqlStr);
            return ResultUtils.succ(null,"删除成功");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionUtil.close(connection,statement);
        }
        return ResultUtils.error("删除失败");
    }
}

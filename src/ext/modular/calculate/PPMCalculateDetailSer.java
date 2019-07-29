package ext.modular.calculate;

import ext.modular.common.ConnectionUtil;
import ext.modular.datainstance.DatainstanceEntity;
import ext.modular.form.FormEntity;
import ext.modular.product.ProductEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * PPM计算service
 */
@Service
public class PPMCalculateDetailSer {
    private final Logger log= LoggerFactory.getLogger(this.getClass());
    private String selectField="id,createTime,updateTime,creator,product_id,chara_id,defect_number,tw_id,ppm_order," +
            "batch,quantity,check_type,check_person,check_person_id,check_time,product_count,category,module_Name,procedure_Name,charac_name,charac_quantity,kj";
    private String selectCPField="id,createTime,updateTime,creator,name,model_id,product_code,model_type";
    Connection connection= null;
    Statement statement=null;
    ResultSet resultSet=null;




    /**
     * 根据起止时间查询工序名称
     * @param startDate
     * @param endDate
     * @return
     */
    public List<String> getProcedureNameList(Connection connection,String startDate, String endDate){
        List<String> procedureNameList = new LinkedList<String>();
        try {
           statement=connection.createStatement();
            String sqlStr=String.format("SELECT procedure_Name FROM ppm_data_instance where createTime " +
                    "between to_date('%s','yyyy-MM-dd') and to_date('%s','yyyy-MM-dd') GROUP BY procedure_Name",startDate,endDate);
          resultSet=statement.executeQuery(sqlStr);
            log.info("查询的sql为“{}”,查询到的结果resultSet为{}",sqlStr,resultSet);
            if(resultSet!=null){
                while (resultSet.next()){
                    String name = resultSet.getString("procedure_Name");//工序名称
                    procedureNameList.add(name);
                }
                System.out.println("service>>>>procedureNameList size() === " + procedureNameList.size());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return procedureNameList;
    }


    /**
     * 根据起止时间查询工序名称、PPM
     * @param startDate
     * @param endDate
     * @return
     */
    public Map<String,Integer> getPPMData(Connection connection, String startDate, String endDate){

        Map<String,Integer> map = new LinkedHashMap<>();
        try {

            statement=connection.createStatement();
            String sqlStr="SELECT procedure_Name,SUM(charac_PPM) total  FROM ppm_data_instance where createTime " +
                    "between to_date('"+ startDate +"','yyyy-MM-dd') and to_date('"+ endDate +"','yyyy-MM-dd')" +
                    "GROUP BY procedure_Name";

            resultSet=statement.executeQuery(sqlStr);
            log.info("查询的sql为“{}”,查询到的结果resultSet为{}",sqlStr,resultSet);

            if(resultSet!=null){

                while (resultSet.next()){

                    String name = resultSet.getString("procedure_Name");//工序名称
                    Integer num = resultSet.getInt("total");//PPM
                    map.put(name,num);
                }

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }



    /**
     * 根据工序名称查询工序PPM
     * @param procedureName
     * @return
     */
    public int getPPMCalculateByProcedureName(Connection connection,String procedureName){
        int pPMCalculate = 0;
        try {
            statement=connection.createStatement();
            String sql=String.format("SELECT SUM(PROCEDURE_PPM) total FROM PPM_DATA_INSTANCE WHERE PROCEDURE_NAME='%s'"
                    ,procedureName);
            log.info("sql="+sql);
            resultSet=statement.executeQuery(sql);
            log.info("查询的sql为“{}”,查询到的结果resultSet为：“{}”",sql,resultSet);
            if(resultSet!=null){
                while (resultSet.next()){
                    pPMCalculate = resultSet.getInt("total");
                }
            }
            //检验特性的数量
            int countNum=0;
            sql=String.format("SELECT COUNT(id) COUNT FROM PPM_DATA_INSTANCE WHERE PROCEDURE_NAME='%s'"
                    ,procedureName);
            resultSet=statement.executeQuery(sql);
            if(resultSet!=null&&resultSet.next()){
                countNum=resultSet.getInt("COUNT");
            }else{
                log.error("有错误，查询到检验特性个数为空结果集");
            }
            pPMCalculate=countNum==0?0:pPMCalculate/countNum;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pPMCalculate;
    }

    /**
     * 根据产品ID查询产品PPM
     * @param productId
     * @return
     */
    public int getPPMCalculateByProductId(int productId){
        int PPMCalculate = 0;
        try {

            connection = ConnectionUtil.getJdbcConnection();
            statement=connection.createStatement();
            String sql = "SELECT SUM(charac_PPM) total FROM ppm_data_instance where product_id = "
                    + productId;
            System.out.println(sql);
            resultSet=statement.executeQuery(sql);
            log.info("查询的sql为“{}”,查询到的结果resultSet为：“{}”",sql,resultSet);
            if(resultSet!=null){
                while (resultSet.next()){

                    PPMCalculate = resultSet.getInt("total");
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionUtil.close(connection,statement);
        }
        return PPMCalculate;
    }

    /**
     * 根据型号ID查询产品
     * @param modelId
     * @return
     */
    public List<ProductEntity> getProductByModelId(int modelId){
        List<ProductEntity> list = new LinkedList<ProductEntity>();
        try {

            connection = ConnectionUtil.getJdbcConnection();
            statement=connection.createStatement();
            String sqlStr=String.format("SELECT %s FROM ppm_product where model_id = %s"
                    ,selectCPField,modelId);
            System.out.println(sqlStr);
            resultSet=statement.executeQuery(sqlStr);
            log.info("查询的sql为“{}”,查询到的结果resultSet为：“{}”",sqlStr,resultSet);
            if(resultSet!=null){
                while (resultSet.next()){

                    ProductEntity productEntity = new ProductEntity();

                    productEntity.setId(resultSet.getInt("ID"));
                    productEntity.setCreator(resultSet.getString("creator"));
                    productEntity.setName(resultSet.getString("name"));
                    productEntity.setCreateTime(resultSet.getDate("createTime"));
                    productEntity.setUpdateTime(resultSet.getTime("updateTime"));
                    productEntity.setModel_id(resultSet.getString("model_id"));
                    productEntity.setProduct_code(resultSet.getString("product_code"));
                    productEntity.setModel_type(resultSet.getString("model_type"));

                    list.add(productEntity);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionUtil.close(connection,statement);
        }
        return list;
    }

    /**
     * 根据型号ID查询型号PPM
     * @param modelId
     * @return
     */
    public int getPPMCalculateByModelId(int modelId){
        int PPMCalculate = 0;
        List<ProductEntity> list = getProductByModelId(modelId);
        for (ProductEntity product : list) {
            int num = getPPMCalculateByProductId(product.getId());
            PPMCalculate += num;
        }
        return PPMCalculate;
    }


    /**
     * 根据模件名称获取模件PPM
     * @param moduleName
     * @return
     */
    public int getPPMCalculateByModuleName(String moduleName) {
        int PPMCalculate = 0;
        try {

            connection = ConnectionUtil.getJdbcConnection();
            statement = connection.createStatement();
            String sql = "SELECT SUM(charac_PPM) total FROM ppm_data_instance where module_name = '"
                    + moduleName + "'";
            System.out.println(sql);
            resultSet = statement.executeQuery(sql);
            log.info("查询的sql为“{}”,查询到的结果resultSet为：“{}”", sql, resultSet);
            if (resultSet != null) {
                while (resultSet.next()) {

                    PPMCalculate = resultSet.getInt("total");
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection, statement);
        }
        return PPMCalculate;
    }

    /**
     * 根据工序名称获取工序检验特性名称列表
     * @param procedureName
     * @return
     */
    public List<String> getCharacNameList(Connection connection,String procedureName){
        List<String> list = new LinkedList<String>();;
        try {

 //           connection = ConnectionUtil.getJdbcConnection();
            statement=connection.createStatement();
            String sqlStr="SELECT CHARAC_NAME FROM ppm_data_instance where procedure_name = '" + procedureName + "'GROUP BY CHARAC_NAME";
            System.out.println(sqlStr);
            resultSet=statement.executeQuery(sqlStr);
            log.info("查询的sql为“{}”,查询到的结果resultSet为：“{}”",sqlStr,resultSet);
            if(resultSet!=null){
                while (resultSet.next()){
                    String name = resultSet.getString("charac_name");//检验特性名称
                    list.add(name);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * test method:get connection based on jdbc.
     * @method getConnection
     * @return
     * @return Connection
     * @date 2016年5月18日-下午2:22:55
     */
    public static Connection getConnection() {

        String url="jdbc:oracle:thin:@192.168.199.248:1521:wind"; //orcl为数据库的SID
        String user="wcadmin";
        String pwd="wcadmin";

        Connection conn = null;
        try {
            //Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(url,user,pwd);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
    
    /**
     * ln
     * 根据起止时间查询产品编号列表
     * @param startDate
     * @param endDate
     * @return
     */
    public List<String> getProductIdList(Connection connection,String startDate, String endDate){
        List<String> productIdList = new LinkedList<String>();
        try {
           statement=connection.createStatement();
            String sqlStr=String.format("SELECT PRODUCT_ID FROM ppm_data_instance where createTime " +
                    "between to_date('%s','yyyy-MM-dd') and to_date('%s','yyyy-MM-dd') GROUP BY PRODUCT_ID",startDate,endDate);
          resultSet=statement.executeQuery(sqlStr);
            log.info("查询的sql为“{}”,查询到的结果resultSet为{}",sqlStr,resultSet);
            if(resultSet!=null){
                while (resultSet.next()){
                    String productId = resultSet.getString("PRODUCT_ID");//产品名称
                    productIdList.add(productId);
                }
                System.out.println("service>>>>procedureNameList size() === " + productIdList.size());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productIdList;
    }
    /**
     * ln
     * 根据起止时间、产品id查询产品产品级工序总检验缺陷总数和产品级工序检验特性总数，计算产品ppm
     * @param product_id
     * @return
     */
    public int getPPMCalculateByProductId(Connection connection,int productId,String startDate, String endDate){
        int pPMCalculate = 0;
        int pPMProNum = 0;
        try {
            statement=connection.createStatement();
            String sql=String.format("SELECT sum(kj) numb,sum(CHARACTERISTICS_TOTAL) total FROM PPM_DATA_INSTANCE WHERE"
            		+ " createTime between to_date('%s','yyyy-MM-dd') and to_date('%s','yyyy-MM-dd')"
            		+ " and PRODUCT_ID='%s'"
                    ,startDate,endDate,productId);
            log.info("sql="+sql);
            resultSet=statement.executeQuery(sql);
            log.info("查询的sql为“{}”,查询到的结果resultSet为：“{}”",sql,resultSet);
            if(resultSet!=null){
                while (resultSet.next()){
                    pPMCalculate = resultSet.getInt("total");
                    pPMProNum = resultSet.getInt("numb");
                    System.out.println("分子"+pPMProNum+"分母"+pPMCalculate);
                }
            }
            pPMCalculate= pPMCalculate==0?0:(int)((float)pPMProNum/pPMCalculate*Math.pow(10, 6));
            System.out.println(pPMCalculate);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pPMCalculate;
    }
    
    
    
    /**
     * ln
     * 查询所有型号名列表
     * @param startDate
     * @param endDate
     * @return
     */
    public List<String> getModelNameList(Connection connection){
        List<String> modelList = new LinkedList<String>();
        try {
           statement=connection.createStatement();
            String sqlStr=String.format("select model_type from ppm_product group by model_type");
          resultSet=statement.executeQuery(sqlStr);
            log.info("查询的sql为“{}”,查询到的结果resultSet为{}",sqlStr,resultSet);
            if(resultSet!=null){
                while (resultSet.next()){
                    String modelType = resultSet.getString("model_type");//型号名称
                    System.out.println("型号名称"+modelType);
                    modelList.add(modelType);
                }
                System.out.println("service>>>>procedureNameList size() === " + modelList.size());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return modelList;
    }
    
    /**
     * ln
     * 根据型号名查询产品id集合
     * @param startDate
     * @param endDate
     * @return
     */
    public List<Integer> getProIdListByModelName(Connection connection,String modelType){
        List<Integer> proIdList = new LinkedList<Integer>();
        try {
           statement=connection.createStatement();
            String sqlStr=String.format("select id from ppm_product where model_type='%s'",modelType);
          resultSet=statement.executeQuery(sqlStr);
            log.info("查询的sql为“{}”,查询到的结果resultSet为{}",sqlStr,resultSet);
            if(resultSet!=null){
                while (resultSet.next()){
                    int proId = resultSet.getInt("id");//产品id
                    proIdList.add(proId);
                }
                System.out.println("service>>>>procedureNameList size() === " + proIdList.size());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proIdList;
    }
    
    /**
     * ln
     * 根据型号名查询产品id 和产品名集合
     * @param startDate
     * @param endDate
     * @return
     */
    public List<ProductEntity> getProListByModelName(Connection connection,String modelType){
        List<ProductEntity> proList = new LinkedList<ProductEntity>();
        try {
           statement=connection.createStatement();
            String sqlStr=String.format("select id ,name from ppm_product where model_type='%s'",modelType);
          resultSet=statement.executeQuery(sqlStr);
            log.info("查询的sql为“{}”,查询到的结果resultSet为{}",sqlStr,resultSet);
            if(resultSet!=null){
                while (resultSet.next()){
                	ProductEntity productEntity = new ProductEntity();
                	productEntity.setId(resultSet.getInt("id"));//产品id
                	productEntity.setName(resultSet.getString("name"));
                	proList.add(productEntity);
                }
                System.out.println("service>>>>productList size() === " + proList.size());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proList;
    }
    
    /**
     * ln
     * 根据产品名查询模板logo 和模件名集合
     * @param startDate
     * @param endDate
     * @return
     */
    public List<FormEntity> getFormListByProId(Connection connection,int proId){
        List<FormEntity> proList = new LinkedList<FormEntity>();
        try {
           statement=connection.createStatement();
            String sqlStr=String.format("select MODULE_NAME,LOGO from ppm_form where product_id='%s' GROUP BY MODULE_NAME,LOGO",proId);
          resultSet=statement.executeQuery(sqlStr);
            log.info("查询的sql为“{}”,查询到的结果resultSet为{}",sqlStr,resultSet);
            if(resultSet!=null){
                while (resultSet.next()){
                	FormEntity formEntity = new FormEntity();
                	formEntity.setLogo(resultSet.getInt("LOGO"));//表单logo
                	formEntity.setModuleName(resultSet.getString("MODULE_NAME"));//表单名
                	proList.add(formEntity);
                }
                System.out.println("service>>>>procedureNameList size() === " + proList.size()+"proList"+proList.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proList;
    }
    
    
    /**
     * ln 
     * 2019/7/26
     * 根据模板logo查询并计算模件的ppm集合
     * @param startDate
     * @param endDate
     * @return
     */
    public int getPPMCalculateBymj(Connection connection,int logo,String startDate, String endDate){
        int pPMCalculate = 0;
        int pPMProNum = 0;
        try {
            statement=connection.createStatement();
            String sql=String.format("SELECT sum(kj) numb,sum(CHARACTERISTICS_TOTAL) total FROM PPM_DATA_INSTANCE WHERE"
            		+ " createTime between to_date('%s','yyyy-MM-dd') and to_date('%s','yyyy-MM-dd')"
            		+ " and PRODUCT_ID in(select id from ppm_product where logo='%s')"
                    ,startDate,endDate,logo);
            log.info("sql="+sql);
            resultSet=statement.executeQuery(sql);
            log.info("查询的sql为“{}”,查询到的结果resultSet为：“{}”",sql,resultSet);
            if(resultSet!=null){
                while (resultSet.next()){
                    pPMCalculate = resultSet.getInt("total");
                    pPMProNum = resultSet.getInt("numb");
                    System.out.println("分子"+pPMProNum+"分母"+pPMCalculate);
                }
            }
            pPMCalculate= pPMCalculate==0?0:(int)((float)pPMProNum/pPMCalculate*Math.pow(10, 6));
            System.out.println(pPMCalculate);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pPMCalculate;
    }
    
   
    
    
    /**
     * ln
     * 根据起止时间、模件id查询模件级工序总检验缺陷总数和产品级工序检验特性总数，计算型号ppm
     * @param product_id
     * @return
     */
    public int getPPMCalculateByXH(Connection connection,String modelType,String startDate, String endDate){
        int pPMCalculate = 0;
        int pPMProNum = 0;
        try {
            statement=connection.createStatement();
            String sql=String.format("SELECT sum(kj) numb,sum(CHARACTERISTICS_TOTAL) total FROM PPM_DATA_INSTANCE WHERE"
            		+ " createTime between to_date('%s','yyyy-MM-dd') and to_date('%s','yyyy-MM-dd')"
            		+ " and PRODUCT_ID in(select id from ppm_product where model_type='%s')"
                    ,startDate,endDate,modelType);
            log.info("sql="+sql);
            resultSet=statement.executeQuery(sql);
            log.info("查询的sql为“{}”,查询到的结果resultSet为：“{}”",sql,resultSet);
            if(resultSet!=null){
                while (resultSet.next()){
                    pPMCalculate = resultSet.getInt("total");
                    pPMProNum = resultSet.getInt("numb");
                    System.out.println("分子"+pPMProNum+"分母"+pPMCalculate);
                }
            }
            pPMCalculate= pPMCalculate==0?0:(int)((float)pPMProNum/pPMCalculate*Math.pow(10, 6));
            System.out.println(pPMCalculate);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pPMCalculate;
    }
    
}

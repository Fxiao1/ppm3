package ext.modular.calculate;

import ext.modular.common.ConnectionUtil;
import ext.modular.product.ProductEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.*;

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
        int PPMCalculate = 0;
        try {
            statement=connection.createStatement();
            String sql=String.format("SELECT SUM(PROCEDURE_PPM) total FROM PPM_DATA_INSTANCE WHERE PROCEDURE_NAME='%s'"
                    ,procedureName);
            log.info("sql="+sql);
            resultSet=statement.executeQuery(sql);
            log.info("查询的sql为“{}”,查询到的结果resultSet为：“{}”",sql,resultSet);
            if(resultSet!=null){
                while (resultSet.next()){
                    PPMCalculate = resultSet.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return PPMCalculate;
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
}

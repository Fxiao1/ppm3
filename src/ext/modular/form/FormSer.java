package ext.modular.form;

import ext.modular.common.ConnectionUtil;
import ext.modular.common.DataPack;
import ext.modular.common.ResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTException;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * des:
 *  表单的ser层
 * @author renkai
 * @date 2019/6/16
 */
@Service
public class FormSer {
    private final Logger log= LoggerFactory.getLogger(this.getClass());
    /**
     * 增加表单
     * @Author renkai
     * @Description
     * @Date 12:17 2019/6/17
     * @param formEntity
     * @return void
     **/
    public void add(FormEntity formEntity,Connection connection )  {
        Statement statement=null;
        try {
            WTPrincipal current = SessionHelper.manager.getPrincipal();
            String currentUser=current.getName();
            statement=connection.createStatement();
            String sqlStr=String.format("insert into ppm_form(" +
                            "id,creator,product_id,chara_id,tw_id,logo,batch,quantity,category,module_name,procedure_name,charac_name,charac_quantity,kj,ppm_order) " +
                            "values(ppm_seq.nextval,'%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s',ppm_order_num_seq.nextval)"
                    ,currentUser,formEntity.getProductId(),formEntity.getCharacId(),formEntity.getTwId(),
                     formEntity.getLogo(),formEntity.getBatch(),formEntity.getQuantity(),formEntity.getCategory(),formEntity.getModuleName(),formEntity.getProcedureName(),
                     formEntity.getCharacName(),formEntity.getCharacQuantity(),formEntity.getKj());
            log.info("ext.modular.form.FormSer.add 新增的sql为“{}”",sqlStr);
            statement.execute(sqlStr);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        }
    }
    /**
     * 根据表单标识获取模板列表
     * @Author renkai
     * @Description
     * @Date 2019/6/16
     * @param logo
     * @return java.util.List<ext.modular.form.FormEntity>
     **/
    public List<FormEntity> getFormList(int logo){
        Connection connection= null;
        Statement statement=null;
        ResultSet resultSet=null;
        List<FormEntity> fromList=new LinkedList<FormEntity>();
        try {
            log.info("表单标识logo为{}",logo);
            connection = ConnectionUtil.getJdbcConnection();
            statement=connection.createStatement();
            String sqlStr=String.format("SELECT * FROM ppm_form where logo=%s order by ppm_order",logo);
            System.out.println(sqlStr);
            resultSet=statement.executeQuery(sqlStr);
            log.info("查询的sql为“{}”,查询到的结果resultSet为：“{}”",sqlStr,resultSet);
            DataPack<List<FormEntity>> dataPack=getFormListByResultSet(resultSet);
            if(dataPack.isSuccess()) fromList=dataPack.getData();
        }catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection,statement);
        }
        return fromList;
    }
    /**
     * 根据产品id获取form表的所有行数据
     * @Author renkai
     * @Description
     * @Date 2019/6/17
     * @param productId
     * @return java.util.List<ext.modular.form.FormEntity>
     **/
    public List<FormEntity> getItemFormList(int productId){
        Connection connection= null;
        Statement statement=null;
        List<FormEntity> fromList=new LinkedList<FormEntity>();
        try {
            log.info("产品id为{}",productId);
            connection = ConnectionUtil.getJdbcConnection();
            statement=connection.createStatement();
            String sqlStr=String.format("SELECT * FROM ppm_form where product_id=%s order by ppm_order"
                    ,productId);
            System.out.println(sqlStr);
            ResultSet resultSet=statement.executeQuery(sqlStr);
            log.info("查询的sql为“{}”,查询到的结果resultSet为：“{}”",sqlStr,resultSet);
            DataPack<List<FormEntity>> dataPack=getFormListByResultSet(resultSet);
            if(dataPack.isSuccess()) fromList=dataPack.getData();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection,statement);
        }
        return fromList;
    }
    /**
     *
     * @Author Fxiao
     * @Description
     * @Date 22:03 2019/6/27
     * @param productId
     * @param logo 表单标识
     * @return java.util.List<ext.modular.form.FormEntity>
     **/
    public List<FormEntity> getItemFormList(int productId,int logo,Connection conn){
        PreparedStatement ps=null;
        List<FormEntity> fromList=new LinkedList<FormEntity>();
        try {
            log.info("产品id为{},logo={}",productId,logo);
            String sqlStr="SELECT * FROM PPM_FORM WHERE PRODUCT_ID=? AND logo=? ORDER BY PPM_ORDER";
            ps=conn.prepareStatement(sqlStr);
            ps.setInt(1,productId);
            ps.setInt(2,logo);
            ResultSet rs=ps.executeQuery();
            log.info("查询的sql为“{}”,查询到的结果resultSet为：“{}”",sqlStr,rs);
            DataPack<List<FormEntity>> dataPack=getFormListByResultSet(rs);
            if(dataPack.isSuccess()) fromList=dataPack.getData();
        } catch (SQLException e) {
            e.printStackTrace();
        }  finally {
            ConnectionUtil.close(null,ps);
        }
        return fromList;
    }

    /**
     * 获取表单，而不是表单表里面的每一行数据。<br>
     *     与方法ext.modular.form.FormSer#getFroFormList(int)的区别是，本方法的最小单位是表单，而前者的最小数据单位是表单表里面的一行数据
     * @Description
     * @Date 14:24 2019/6/20
     * @param productId
     * @return java.util.List
     **/
    public List getlistByProduct(int productId){
        Connection connection= null;
        Statement statement=null;
        Statement statement2=null;
        List<Map<String,Object>> data=new LinkedList<>();
        try {
            connection=ConnectionUtil.getJdbcConnection();
            statement=connection.createStatement();
            statement2=connection.createStatement();
            String sql=String.format("SELECT logo,category,module_name,batch,quantity,creator,PRODUCT_ID FROM ppm_form GROUP BY logo,category,module_name,batch,quantity,creator,PRODUCT_ID HAVING PRODUCT_ID=%s",
                    productId);
            log.info("查询全部数据的sql为："+sql);
            ResultSet resultSet=statement.executeQuery(sql);
            ResultSet resultSet2=null;
            if(resultSet!=null){
                while(resultSet.next()){
                    Map<String,Object> map=new HashMap<>();
                    map.put("logo",resultSet.getInt("logo"));
                    map.put("category",resultSet.getString("category"));
                    map.put("moduleName",resultSet.getString("module_name"));
                    map.put("batch",resultSet.getString("batch"));
                    map.put("quantity",resultSet.getInt("quantity"));
                    map.put("creator",resultSet.getString("creator"));
                    map.put("productId",resultSet.getInt("product_id"));
                    //查询时间,条件是产品和form标识
                    String seleTimeSql=String.format("SELECT * FROM (SELECT  CREATETIME,UPDATETIME FROM ppm_form " +
                                "WHERE PRODUCT_ID=%s AND logo=%s  ORDER BY CREATETIME) WHERE rownum =1",
                                productId,map.get("logo")
                            );
                    resultSet2=statement2.executeQuery(seleTimeSql);
                    if(resultSet2!=null&&resultSet2.next()){
                        map.put("createTime",resultSet2.getDate("CREATETIME"));
                        map.put("updateTime",resultSet2.getDate("UPDATETIME"));
                    }
                    data.add(map);
                }
                return data;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection,statement);
        }
       return data;
    }


    
    /**
     * 根据表单标识删除表单
     * @Author renkai
     * @Description
     * @Date 2019/6/17
     * @param logo
     * @return void
     **/
    public void delete(int logo){
        Connection connection= null;
        Statement statement=null;
        try {
            connection = ConnectionUtil.getConnection();
            statement=connection.createStatement();
            String sqlStr=String.format("DELETE FROM ppm_form WHERE logo=%s",logo);
            statement.execute(sqlStr);
            log.info("删除的sql为“{}”",sqlStr);
            log.info("logo={}",logo);
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionUtil.close(connection,statement);
        }
    }
    //根据Oracle序列获取表达标识
    public int getLogo(){
        Connection connection= null;
        Statement statement=null;
        ResultSet resultSet=null;
        int logo=0;
        try {
            connection = ConnectionUtil.getJdbcConnection();
            statement=connection.createStatement();
            String sqlStr=String.format("SELECT ppm_seq.nextval FROM dual");
            System.out.println(sqlStr);
            resultSet=statement.executeQuery(sqlStr);
            if(resultSet!=null){
                resultSet.next();
                logo=resultSet.getInt("nextval");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection,statement);
        }
        return logo;
    }
    /**
     * 根据数据库查询结果封装form实体列表
     * @Author Fxiao
     * @Description
     * @Date 21:53 2019/6/27
     * @param rs
     * @return java.util.List<ext.modular.form.FormEntity>
     **/
    private DataPack<List<FormEntity>> getFormListByResultSet(ResultSet rs) throws SQLException {
        List<FormEntity> fromList=new LinkedList<>();
        if(rs!=null){
            while (rs.next()){
                FormEntity formEntity = new FormEntity();
                formEntity.setId(rs.getInt("ID"));
                formEntity.setCreator(rs.getString("creator"));
                formEntity.setCreateTime(rs.getDate("createTime"));
                formEntity.setUpdateTime(rs.getDate("updateTime"));
                formEntity.setProductId(rs.getInt("product_id"));
                formEntity.setLogo(rs.getInt("logo"));
                formEntity.setBatch(rs.getString("batch"));
                formEntity.setQuantity(rs.getInt("quantity"));
                formEntity.setCategory(rs.getString("category"));
                formEntity.setModuleName(rs.getString("module_name"));
                formEntity.setCharacName(rs.getString("charac_name"));
                formEntity.setProcedureName(rs.getString("procedure_name"));
                formEntity.setCharacQuantity(rs.getInt("charac_quantity"));
                formEntity.setKj(rs.getInt("kj"));
                formEntity.setTwId(rs.getInt("TW_ID"));
                fromList.add(formEntity);
            }
            return ResultUtils.packData(fromList,"",true);
        }else{
            return ResultUtils.packData(null,"未获得有效的ResultSet对象",false);
        }
    }


    
}

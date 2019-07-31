package ext.modular.datainstance;

import com.google.gson.Gson;
import ext.modular.common.ConnectionUtil;
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
 * 表单实例的ser层
 *
 * @author renkai
 * @date 2019/6/18
 */
@Service
public class DatainstanceSer {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private String selectField = "id,createTime,updateTime,creator,product_id,chara_id,tw_id,ppm_order,logo,batch,quantity,category,module_name,templateName,templateId,ProductPhase,procedure_name,charac_name,charac_quantity,kj,defect_number,check_type,check_person,check_person_id,check_time,product_count,charac_PPM";
    private String insertField =
            "id,creator,product_id,chara_id,tw_id,logo,batch,quantity,category,module_name,procedure_name,charac_name,charac_quantity,kj,defect_number,check_type,check_person,check_person_id,check_time,product_count,charac_PPM,ppm_order";

    public List<DatainstanceEntity> getListByProductId(int productId, Connection connection) {
        Statement statement = null;
        List<DatainstanceEntity> list = new LinkedList<>();
        try {
            statement = connection.createStatement();
            String sql = "SELECT * FROM PPM_DATA_INSTANCE where product_id=" + productId;
            ResultSet rs = statement.executeQuery(sql);
            if (rs != null) {
                while (rs.next()) {
                    DatainstanceEntity de = new DatainstanceEntity();
                    /*chara_id,tw_id,batch,quantity,category,module_name,procedure_name,charac_name,charac_quantity,kj,defect_number,check_type,check_person,check_person_id,check_time,product_count,charac_PPM,procedure_ppm,defect_number_item,characteristics_total,datains_mark*/
                    de.setCharaId(rs.getInt("chara_id"));
                    de.setTwId(rs.getInt("tw_id"));
                    de.setBatch(rs.getString("batch"));
                    de.setQuantity(rs.getInt("quantity"));
                    de.setCategory(rs.getString("category"));
                    de.setModuleName(rs.getString("module_name"));
                    de.setProcedureName(rs.getString("procedure_name"));
                    de.setCharacName(rs.getString("charac_name"));
                    de.setCharacQuantity(rs.getInt("charac_quantity"));
                    de.setTemplateName(rs.getString("templateName"));
                    de.setTemplateId(rs.getString("templateId"));
                    de.setProductPhase(rs.getString("ProductPhase"));
                    de.setKj(rs.getInt("kj"));
                    de.setDefectNumber(rs.getInt("defect_number"));
                    de.setCheckType(rs.getString("check_type"));
                    de.setCheckPerson(rs.getString("check_person"));
                    de.setCheckPersonId(rs.getString("check_person_id"));
                    long temp = rs.getDate("check_time").getTime();
                    java.util.Date checkTime = new java.util.Date();
                    checkTime.setTime(temp);
                    de.setCheckTime(checkTime);
                    de.setProductCount(rs.getInt("product_count"));
                    de.setCharacPPM(rs.getInt("charac_PPM"));
                    de.setProcedurePpm(rs.getInt("procedure_ppm"));
                    de.setDefectNumberItem(rs.getInt("defect_number_item"));
                    de.setCharacteristicsTotal(rs.getInt("characteristics_total"));
                    de.setDataInsMark(rs.getInt("datains_mark"));
                    list.add(de);
                }
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionUtil.close(null,statement);
        }
        return list;
    }

    public int getquantityByLogo(int logo,Connection connection){
    	 Statement statement = null;
    	 int quantity = 0;
    	 String sql = String.format("SELECT logo FROM PPM_DATA_INSTANCE where logo=%s group by logo",logo);

             try {
				statement=connection.createStatement();
				 ResultSet rs = statement.executeQuery(sql);
				 if (rs != null) {
		                while (rs.next()) {
		                	quantity=rs.getInt("quantity");
		                	System.out.println("产品总数"+quantity);
		                }
				 }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		return quantity;

    }

    public List<DatainstanceEntity> getListByLogo(int logo,Connection connection){
        Statement statement = null;
        List<DatainstanceEntity> list = new LinkedList<>();
        String sql = "SELECT * FROM PPM_DATA_INSTANCE where logo=" + logo;
        try {
            statement=connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            if (rs != null) {
                while (rs.next()) {
                    DatainstanceEntity de = new DatainstanceEntity();
                    /*chara_id,tw_id,batch,quantity,category,module_name,procedure_name,charac_name,charac_quantity,kj,defect_number,check_type,check_person,check_person_id,check_time,product_count,charac_PPM,procedure_ppm,defect_number_item,characteristics_total,datains_mark*/
                    de.setCharaId(rs.getInt("chara_id"));
                    de.setTwId(rs.getInt("tw_id"));
                    de.setBatch(rs.getString("batch"));
                    de.setQuantity(rs.getInt("quantity"));
                    de.setCategory(rs.getString("category"));
                    de.setModuleName(rs.getString("module_name"));
                    de.setProcedureName(rs.getString("procedure_name"));
                    de.setCharacName(rs.getString("charac_name"));
                    de.setCharacQuantity(rs.getInt("charac_quantity"));
                    de.setTemplateName(rs.getString("templateName"));
                    de.setTemplateId(rs.getString("templateId"));
                    de.setProductPhase(rs.getString("ProductPhase"));
                    de.setKj(rs.getInt("kj"));
                    de.setDefectNumber(rs.getInt("defect_number"));
                    de.setCheckType(rs.getString("check_type"));
                    de.setCheckPerson(rs.getString("check_person"));
                    de.setCheckPersonId(rs.getString("check_person_id"));
                    long temp = rs.getDate("check_time").getTime();
                    java.util.Date checkTime = new java.util.Date();
                    checkTime.setTime(temp);
                    de.setCheckTime(checkTime);
                    de.setProductCount(rs.getInt("product_count"));
                    de.setCharacPPM(rs.getInt("charac_PPM"));
                    de.setProcedurePpm(rs.getInt("procedure_ppm"));
                    de.setDefectNumberItem(rs.getInt("defect_number_item"));
                    de.setCharacteristicsTotal(rs.getInt("characteristics_total"));
                    de.setDataInsMark(rs.getInt("datains_mark"));
                    de.setId(rs.getInt("id"));
                    de.setCreator(rs.getString("CREATOR"));
                    de.setCreateTime(rs.getDate("CREATETIME"));
                    de.setUpdateTime(rs.getDate("UPDATETIME"));

                    list.add(de);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionUtil.close(null,statement);
        }
        return list;
    }

    /**
     * 根据表单标识和检验特性ID判断数据库是否已有数据
     *
     * @param datainstanceEntity
     * @return Boolean
     * @Author renkai
     * @Description
     * @Date 2019/6/19
     **/
    public Boolean getFlag(DatainstanceEntity datainstanceEntity) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        boolean flag = true;
        try {
            connection = ConnectionUtil.getJdbcConnection();
            statement = connection.createStatement();
            String sqlStr = String.format("SELECT %s FROM ppm_data_instance where logo=%s and tw_id=%s order by ppm_order", selectField, datainstanceEntity.getLogo(), datainstanceEntity.getTwId());
            log.info("查询的sql为“{}”", sqlStr);
            resultSet = statement.executeQuery(sqlStr);
            if (resultSet != null) {
                flag = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection, statement);
        }
        return flag;
    }

    /**
     * 增加表单实例
     *
     * @param de
     * @param conn
     * @param dataMark 数据标识，本组数据的标识应该都是一样的
     * @return void
     * @Author Fxiao
     * @Description
     * @Date 18:21 2019/6/27
     **/
    public void add(DatainstanceEntity de, Connection conn, int dataMark) {
        Gson gson = new Gson();
        log.info("正在执行“ext.modular.datainstance.DatainstanceSer.add”方法，接收到的参数DatainstanceEntity的实体为：“{}”",
                gson.toJson(de));
        PreparedStatement ps = null;
        try {
            //26个字段,24个占位符
            String sqlStr = "insert into ppm_data_instance(" +
                    //1-11
                    "id,creator,product_id,chara_id,tw_id,logo,batch,quantity,category,module_name,procedure_name," +
                    //12-19
                    "charac_name,charac_quantity,kj,defect_number,check_type,check_person,check_person_id,check_time," +
                    //20-25
                    "product_count,charac_PPM,ppm_order,procedure_ppm,defect_number_item,characteristics_total" +
                    //26-26
                    ",datains_mark,templateName,templateId,ProductPhase" +
                    ") values(" +
                    "ppm_seq.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,ppm_order_num_seq.nextval,?,?,?,?,?,?,?)";
            log.info("新增的sql为“{}”,下面将开始一一赋值", sqlStr);
            ps = conn.prepareStatement(sqlStr);
            WTPrincipal current = SessionHelper.manager.getPrincipal();
            String currentUser=current.getName();
            ps.setString(1, currentUser);
            ps.setInt(2, de.getProductId());
            ps.setInt(3, de.getCharaId());
            ps.setInt(4, de.getTwId());
            ps.setInt(5, de.getLogo());
            ps.setString(6, de.getBatch());
            ps.setInt(7, de.getQuantity());
            ps.setString(8, de.getCategory());
            ps.setString(9, de.getModuleName());
            ps.setString(10, de.getProcedureName());

            ps.setString(11, de.getCharacName());
            ps.setInt(12, de.getCharacQuantity());
            ps.setInt(13, de.getKj());
            ps.setInt(14, de.getDefectNumber());
            ps.setString(15, de.getCheckType());
            ps.setString(16, de.getCheckPerson());
            ps.setString(17, de.getCheckPersonId());
            Date checkTime = null;
            if (de.getCheckTime() != null) {
                checkTime = new Date(de.getCheckTime().getTime());
            }
            ps.setDate(18, checkTime);
            ps.setInt(19, de.getProductCount());
            ps.setInt(20, de.getCharacPPM());
            ps.setInt(21, de.getProcedurePpm());
            ps.setInt(22, de.getDefectNumberItem());
            ps.setInt(23, de.getCharacteristicsTotal());
            ps.setInt(24, dataMark);
            ps.setString(25, de.getTemplateName());
            ps.setString(26, de.getTemplateName());
            ps.setString(27, de.getTemplateId());
            int row = ps.executeUpdate();
            log.info("当前语句执行后修改行数为：" + row);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(null, ps);
        }
    }

    /**
     * 更新表单实例数据中的生产总数
     *
     * @param logo
     * @param conn
     * @return void
     * @Author ln
     * @Description
     * @Date 20:04 2019/6/27
     **/
    public void updateData(int logo,int quantity, Connection conn) {
        PreparedStatement ps = null;
        try {
        	String sqlStr =String.format("UPDATE  ppm_data_instance SET quantity =%s,updateTime=current_timestamp  WHERE logo=%s",logo,quantity);
            ps = conn.prepareStatement(sqlStr);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(null, ps);
        }
    }


    /**
     * 更新表单实例数据
     *
     * @param de
     * @param conn
     * @return void
     * @Author Fxiao
     * @Description
     * @Date 20:04 2019/6/27
     **/
    public void updateDataInstance(DatainstanceEntity de, Connection conn) {
        PreparedStatement ps = null;
        try {
            //20个字段,21个占位符
            String sqlStr = "UPDATE ppm_data_instance SET (" +
                    //1-8
                    "chara_id,tw_id,batch,quantity,category,module_name,procedure_name,charac_name," +
                    //9-15
                    "charac_quantity,kj,defect_number,check_type,check_person,check_person_id,check_time,product_count," +
                    //16-19
                    "charac_PPM,procedure_ppm,defect_number_item,characteristics_total,templateName,templateId,ProductPhase)=" +
                    "(SELECT ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? FROM dual )" +
                    "WHERE id=?";
            ps = conn.prepareStatement(sqlStr);
            log.info("修改的sql为“{}”,下面将开始一一赋值", sqlStr);
            ps.setInt(1, de.getCharaId());
            ps.setInt(2, de.getTwId());
            ps.setString(3, de.getBatch());
            ps.setInt(4, de.getQuantity());
            ps.setString(5, de.getCategory());
            ps.setString(6, de.getModuleName());
            ps.setString(7, de.getProcedureName());
            ps.setString(8, de.getCharacName());

            ps.setInt(9, de.getCharacQuantity());
            ps.setInt(10, de.getKj());
            ps.setInt(11, de.getDefectNumber());
            ps.setString(12, de.getCheckType());
            ps.setString(13, de.getCheckPerson());
            ps.setString(14, de.getCheckPersonId());
            Date temp = new Date(de.getCheckTime().getTime());
            ps.setDate(15, temp);
            ps.setInt(16, de.getProductCount());

            ps.setInt(17, de.getCharacPPM());
            ps.setInt(18, de.getProcedurePpm());
            ps.setInt(19, de.getDefectNumberItem());
            ps.setInt(20, de.getCharacteristicsTotal());
            ps.setString(21, de.getTemplateName());
            ps.setString(22, de.getTemplateId());
            ps.setString(23, de.getProductPhase());

            ps.setInt(24, de.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(null, ps);
        }
    }
    /**
     * 删除产品下的表单实例数据
     * @Author Fxiao
     * @Description
     * @Date 18:46 2019/7/14
     * @param productId
     * @return void
     **/
    public void deleteByProduct(int productId){
        Connection connection = null;
        Statement statement = null;
        try {
            connection = ConnectionUtil.getConnection();
            statement = connection.createStatement();
            String sqlStr = "DELETE FROM PPM_DATA_INSTANCE WHERE PRODUCT_ID="+productId;
            log.info("删除的sql为“{}”", sqlStr);
            statement.executeUpdate(sqlStr);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionUtil.close(connection, statement);
        }

    }
    /**
     * 根据表单标识去删除表单数据实例
     * @Author Fxiao
     * @Description
     * @Date 14:31 2019/7/15
     * @param formLogo
     * @return void
     **/
    public void deleteByFormLogo(int formLogo){
        Connection connection = null;
        Statement statement = null;
        try {
            connection = ConnectionUtil.getConnection();
            statement = connection.createStatement();
            String sqlStr = "DELETE FROM PPM_DATA_INSTANCE WHERE logo="+formLogo;
            log.info("删除的sql为“{}”", sqlStr);
            statement.executeUpdate(sqlStr);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionUtil.close(connection, statement);
        }
    }

}

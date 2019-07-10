package ext.modular.procedurelink;

import ext.modular.common.ConnectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@Service
public class ProcedureLinkSer {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private String insertField = "id,creator,chara_name,pro_link_id,total,COEFFICIENT,ppm_order";

    /**
     * 获取工序与检验特性模板关系列表
     *
     * @param
     * @return
     * @Author Fxiao
     * @Description
     * @Date 2019/6/11
     **/
    public List<ProcedureLinkEntity> getProcedureLinkList() {
        Connection connection = null;
        Statement statement = null;
        List<ProcedureLinkEntity> templinkList = new LinkedList<ProcedureLinkEntity>();
        try {
            connection = ConnectionUtil.getJdbcConnection();
            statement = connection.createStatement();
            String sqlStr = "SELECT * FROM ppm_produce_charac_link ORDER BY createTime";
            ResultSet resultSet = statement.executeQuery(sqlStr);
            if (resultSet != null) {
                while (resultSet.next()) {
                    ProcedureLinkEntity procudurelinkEntity = new ProcedureLinkEntity();
                    procudurelinkEntity.setId(resultSet.getInt("ID"));
                    procudurelinkEntity.setCreator(resultSet.getString("creator"));
                    procudurelinkEntity.setCreateTime(resultSet.getDate("createTime"));
                    procudurelinkEntity.setUpdateTime(resultSet.getTime("updateTime"));
                    procudurelinkEntity.getTemplatelink().setId(resultSet.getInt("pro_link_id"));
                    procudurelinkEntity.getCharacter().setName(resultSet.getString("chara_name"));
                    procudurelinkEntity.getCharacter().setTotal(resultSet.getInt("total"));
                    procudurelinkEntity.getCharacter().setCoefficient(resultSet.getInt("COEFFICIENT"));
                    procudurelinkEntity.setPpm_order(resultSet.getInt("ppm_order"));
                    templinkList.add(procudurelinkEntity);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection, statement);
        }
        return templinkList;
    }
    /**
     * 获取一个“工序-特性”关系的对象
     * @Author Fxiao
     * @Description
     * @Date 17:35 2019/7/10
     * @param id 关系表里面的数据id，而不是工序表里面的id
     * @return ext.modular.procedurelink.ProcedureLinkEntity
     **/
    public ProcedureLinkEntity getById(int id){
        Connection connection = null;
        Statement statement = null;
        ProcedureLinkEntity procedureLink=new ProcedureLinkEntity();
        try {
            connection = ConnectionUtil.getConnection();
            statement = connection.createStatement();
            String sqlStr = "SELECT * FROM ppm_produce_charac_link where id="+id;
            ResultSet resultSet = statement.executeQuery(sqlStr);
            if (resultSet != null) {
                while (resultSet.next()) {
                    procedureLink.setId(resultSet.getInt("ID"));
                    procedureLink.setCreator(resultSet.getString("creator"));
                    procedureLink.setCreateTime(resultSet.getDate("createTime"));
                    procedureLink.setUpdateTime(resultSet.getTime("updateTime"));
                    procedureLink.getTemplatelink().setId(resultSet.getInt("pro_link_id"));
                    procedureLink.getCharacter().setName(resultSet.getString("chara_name"));
                    procedureLink.getCharacter().setTotal(resultSet.getInt("total"));
                    procedureLink.getCharacter().setCoefficient(resultSet.getInt("COEFFICIENT"));
                    procedureLink.setPpm_order(resultSet.getInt("ppm_order"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionUtil.close(connection, statement);
        }
        return procedureLink;
    }

    public List<ProcedureLinkEntity> getByProcedure(int procedureId){
        List<ProcedureLinkEntity> list=new LinkedList<>();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = ConnectionUtil.getConnection();
            statement = connection.createStatement();
            String sqlStr =String.format(
                    "SELECT * FROM ppm_produce_charac_link WHERE PRO_LINK_ID=%s ORDER BY ppm_order",
                    procedureId
            );
            ResultSet resultSet = statement.executeQuery(sqlStr);
            if (resultSet != null) {
                while (resultSet.next()) {
                    ProcedureLinkEntity procudurelink = new ProcedureLinkEntity();
                    procudurelink.setId(resultSet.getInt("ID"));
                    procudurelink.setCreator(resultSet.getString("creator"));
                    procudurelink.setCreateTime(resultSet.getDate("createTime"));
                    procudurelink.setUpdateTime(resultSet.getTime("updateTime"));
                    procudurelink.getTemplatelink().setId(resultSet.getInt("pro_link_id"));
                    procudurelink.getCharacter().setName(resultSet.getString("chara_name"));
                    procudurelink.getCharacter().setTotal(resultSet.getInt("total"));
                    procudurelink.getCharacter().setCoefficient(resultSet.getInt("COEFFICIENT"));
                    procudurelink.setPpm_order(resultSet.getInt("ppm_order"));
                    list.add(procudurelink);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionUtil.close(connection, statement);
        }
        return list;
    }

    //根据工序ID获取检验特性
    public List<ProcedureLinkEntity> getByTemplate(int templateId, Connection conn) {
        List<ProcedureLinkEntity> list = new LinkedList<ProcedureLinkEntity>();
        Statement statement = null;
        try {
            statement = conn.createStatement();
            String sqlStr = String.format(
                    "SELECT a.* FROM ppm_produce_charac_link a,PPM_TEMPLATE_WORK_LINK b WHERE a.ID=b.TW_ID AND b.TEMPLATE_ID=%s and a.DEL_FLAG=0"
                    , templateId);
            log.debug("合成后的sql={}", sqlStr);
            ResultSet newResultSet = statement.executeQuery(sqlStr);
            if (newResultSet != null) {
                while (newResultSet.next()) {
                    ProcedureLinkEntity procedurelinkEntity = new ProcedureLinkEntity();
                    procedurelinkEntity.setId(newResultSet.getInt("id"));
                    procedurelinkEntity.setCreateTime(newResultSet.getDate("createTime"));
                    procedurelinkEntity.setUpdateTime(newResultSet.getDate("updateTime"));
                    procedurelinkEntity.setCreator(newResultSet.getString("creator"));
                    procedurelinkEntity.getCharacter().setName(newResultSet.getString("name"));
                    procedurelinkEntity.getCharacter().setTotal(newResultSet.getInt("total"));
                    procedurelinkEntity.getCharacter().setTotal(newResultSet.getInt("coefficient"));
                    procedurelinkEntity.getTemplatelink().setId(newResultSet.getInt("PRO_LINK_ID"));
                    list.add(procedurelinkEntity);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(null, statement);
        }
        return list;
    }

    /**
     * 增加工序与检验特性模板关系
     * @Author ln
     * @Description
     * @Date 17:06 2019/7/10
     * @param procudurelinkEntity
     * @return void
     **/
    public void addProcedureLink(ProcedureLinkEntity procudurelinkEntity) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = ConnectionUtil.getJdbcConnection();
            statement = connection.createStatement();
            String sqlStr = String.format("INSERT INTO ppm_produce_charac_link(%s) VALUES(ppm_seq.nextval,'%s','%s','%s','%s','%s',ppm_order_num_seq.nextval)",
                    insertField, procudurelinkEntity.getCreator(),
                    procudurelinkEntity.getCharacter().getName(),
                    procudurelinkEntity.getTemplatelink().getId(),
                    procudurelinkEntity.getCharacter().getTotal(),
                    procudurelinkEntity.getCharacter().getCoefficient());
            statement.executeQuery(sqlStr);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 修改工序与检验特性模板关系
     *
     * @param procudurelink 工序模板关系实体类
     * @return int 修改的行数，用以判断是否修改成功
     * @Author Fxiao
     * @Description
     * @Date 16:51 2019/7/10
     **/
    public int updateProcedureLink(ProcedureLinkEntity procudurelink) {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = ConnectionUtil.getJdbcConnection();
            String sqlStr = "UPDATE ppm_produce_charac_link SET (updateTime,TOTAL,COEFFICIENT,pro_link_id,chara_name)" +
                    " = (SELECT ?,?,?,?,? FROM dual) where id=? AND DEL_FLAG=0";
            ps = connection.prepareStatement(sqlStr);
            Date updateTime = new Date(System.currentTimeMillis());
            ps.setDate(1, updateTime);
            ps.setInt(2, procudurelink.getCharacter().getTotal());
            ps.setInt(3, procudurelink.getCharacter().getCoefficient());
            ps.setInt(4, procudurelink.getTemplatelink().getId());
            ps.setString(5, procudurelink.getCharacter().getName());
            ps.setInt(6, procudurelink.getTemplatelink().getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally{
            ConnectionUtil.close(connection,ps);
        }
        return 0;
    }

    /**
     * 根据检验特性id和工序模板关系id删除工序与检验特性模板关系
     *
     * @param id
     * @return void
     * @Author Fxiao
     * @Description
     * @Date 2019/6/11
     **/
    public void deleteProcedureLink(int id) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = ConnectionUtil.getJdbcConnection();
            statement = connection.createStatement();
            String sqlStr = String.format("DELETE FROM ppm_produce_charac_link where id ='%s'", id);
            statement.executeQuery(sqlStr);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}

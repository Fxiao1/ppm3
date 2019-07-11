package ext.modular.procedurelink;

import ext.modular.characteristic.CharacteristicEntity;
import ext.modular.common.ConnectionUtil;
import ext.modular.templatelink.TemplatelinkEntity;
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
                    TemplatelinkEntity templatelink=new TemplatelinkEntity();
                    templatelink.setId(resultSet.getInt("pro_link_id"));
                    procedureLink.setTemplatelink(templatelink);
                    CharacteristicEntity charac=new CharacteristicEntity();
                    charac.setName(resultSet.getString("chara_name"));
                    charac.setTotal(resultSet.getInt("total"));
                    charac.setCoefficient(resultSet.getInt("COEFFICIENT"));
                    procedureLink.setCharacter(charac);
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
            list=getListByResultSet(resultSet);
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
            list=getListByResultSet(newResultSet);
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
            ps.setInt(6, procudurelink.getId());
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
     * 结果集封装成对象list
     * @Author Fxiao
     * @Description
     * @Date 21:15 2019/7/10
     * @param resultSet
     * @return java.util.List<ext.modular.procedurelink.ProcedureLinkEntity>
     **/
    private List<ProcedureLinkEntity> getListByResultSet(ResultSet resultSet) throws SQLException {
        List<ProcedureLinkEntity>list=new LinkedList<>();
        if (resultSet != null) {
            while (resultSet.next()) {
                ProcedureLinkEntity procedurelinkEntity = new ProcedureLinkEntity();
                procedurelinkEntity.setId(resultSet.getInt("id"));
                procedurelinkEntity.setCreateTime(resultSet.getDate("createTime"));
                procedurelinkEntity.setUpdateTime(resultSet.getDate("updateTime"));
                procedurelinkEntity.setCreator(resultSet.getString("creator"));
                procedurelinkEntity.getCharacter().setName(resultSet.getString("name"));
                procedurelinkEntity.getCharacter().setTotal(resultSet.getInt("total"));
                procedurelinkEntity.getCharacter().setTotal(resultSet.getInt("coefficient"));
                procedurelinkEntity.getTemplatelink().setId(resultSet.getInt("PRO_LINK_ID"));
                list.add(procedurelinkEntity);
            }
        }
        return list;
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
    /**
     * 根据工序id(模板工序关系的id)删除，也就是删除该工序下的所有特性
     * @Description
     * @Date 10:20 2019/7/11
     * @param procedureLinkId 模板-工序关系数据的id
     * @return void
     **/
    public void deleteByProcedure(int procedureLinkId){
        Connection connection = null;
        Statement statement = null;
        try {
            connection = ConnectionUtil.getConnection();
            statement = connection.createStatement();
            String sqlStr ="DELETE FROM ppm_produce_charac_link where PRO_LINK_ID="+procedureLinkId;
            log.info("正在执行："+sqlStr);
            statement.executeQuery(sqlStr);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}

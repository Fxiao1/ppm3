package ext.modular.procedure;

import ext.modular.characteristic.CharacteristicEntity;
import ext.modular.characteristic.CharacteristicSer;
import ext.modular.common.ConnectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTException;

import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * des:
 *  工序service
 * @author fxiao
 * @date 2019/6/6 10:03
 */
@Service
public class ProcedureSer {
    private final Logger log= LoggerFactory.getLogger(this.getClass());
    private String selectField="id,createTime,updateTime,creator,name";
    /**
     * 获取工序列表
     * @Author Fxiao
     * @Description
     * @Date  2019/6/10
     * @param
     * @return java.util.List<ext.modular.characteristic.CharacteristicEntity>
     **/
    public List<ProcedureEntity> getProcedureList(){
        Connection connection=null;
        Statement statement=null;
        ResultSet resultSet=null;
        String str="";
        List<ProcedureEntity> procedureList=new LinkedList<ProcedureEntity>();
        try{
            connection=ConnectionUtil.getJdbcConnection();
            statement= connection.createStatement();
            String sqlStr=String.format("SELECT %s FROM ppm_working_procedure where DEL_FLAG=0  ORDER BY createTime",selectField);
            resultSet=statement.executeQuery(sqlStr);
            if(resultSet!=null){
                while (resultSet.next()){
                    ProcedureEntity procedureEntity=new ProcedureEntity();
                    procedureEntity.setId(resultSet.getInt("ID"));
                    procedureEntity.setCreator(resultSet.getString("creator"));
                    procedureEntity.setName(resultSet.getString("name"));
                    procedureEntity.setCreateTime(resultSet.getDate("createTime"));
                    procedureEntity.setUpdateTime(resultSet.getTime("updateTime"));
                    procedureList.add(procedureEntity);
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
        return procedureList;
    }
    /**
     * 增加工序
     * @Author Fxiao
     * @Description
     * @Date  2019/6/10
     * @param procedureEntity
     * @return void
     **/
    public ProcedureEntity addProcedure(ProcedureEntity procedureEntity){
        Connection connection=null;
        PreparedStatement preparedStatement=null;

            String sqlStr="INSERT INTO ppm_working_procedure(id,creator,name) VALUES(ppm_seq.nextval,?,?)";
        try {
        	
        	connection = ConnectionUtil.getConnection();
            preparedStatement=connection.prepareStatement(sqlStr);

            WTPrincipal currentUser = SessionHelper.manager.getPrincipal();
            String currentUserName=currentUser.getName();
            preparedStatement.setString(1,currentUserName);
            preparedStatement.setString(2,procedureEntity.getName());
            preparedStatement.executeUpdate();
            ResultSet resultSet=preparedStatement.getGeneratedKeys();
            if(resultSet!=null){
                resultSet.next();
                procedureEntity.setId(resultSet.getInt(1));
                log.info("插入工序后收到的id="+procedureEntity.getId());
                return procedureEntity;
            }else{
                log.info("未找到刚才插入的数据的id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } finally{
            ConnectionUtil.close(connection,preparedStatement);
        }
        return procedureEntity;

    }

    /**
     * 增加工序List集合，有防止插入重复数据的功能，判断重复的依据是name
     * @Author Fxiao
     * @Description
     * @Date  2019年6月19日
     * @param procedureList 工序集合
     * @return void
     **/
    public List<ProcedureEntity> addProcedure(List<ProcedureEntity> procedureList){
        Connection connection=null;
        PreparedStatement ps=null;
        List<ProcedureEntity> backData=new LinkedList<>();
        try{
            connection=ConnectionUtil.getConnection();
            for (int i = 0; i < procedureList.size(); i++) {
                ProcedureEntity procedureEntity=procedureList.get(i);
                //先查询有没有同名工序，如果有则塞入旧数据的id，并进行下一轮循环
                String haveDateSqlStr="SELECT id FROM ppm_working_procedure WHERE name=?";
                ps=connection.prepareStatement(haveDateSqlStr);
                ps.setString(1,procedureEntity.getName());
                ResultSet resultSet=ps.executeQuery();
                if(resultSet!=null&&resultSet.next()){
                    log.info("在插入工序名=“{}”的数据时，找到已有数据，dataId={}。故将跳过该数据的添加，并返回已存在的数据",
                            procedureEntity.getName(),
                            resultSet.getInt("id")
                    );
                    procedureEntity.setId(resultSet.getInt("id"));
                    backData.add(procedureEntity);
                    continue;
                }
                String sqlStr = "select ppm_seq.nextval as id from dual";
                ps=connection.prepareStatement(sqlStr);
                resultSet = ps.executeQuery();
                if(resultSet!=null&&resultSet.next()){
                    procedureEntity.setId(resultSet.getInt("id"));
                }
                sqlStr="INSERT INTO ppm_working_procedure(id,creator,name)" +
                        " VALUES(?,?,?)";
                ps=connection.prepareStatement(sqlStr);
                ps.setInt(1,procedureEntity.getId());
                WTPrincipal currentUser = SessionHelper.manager.getPrincipal();
                String currentUserName=currentUser.getName();
                ps.setString(2,currentUserName);
                ps.setString(3,procedureEntity.getName());
                ps.executeUpdate();
                backData.add(procedureEntity);
            }
            return backData;
        }

        catch (SQLException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection,ps);
        }
        return null;
    }

    /*批量增加工序到指定模板下*/
    /*public void addProcedure(List<ProcedureEntity> procedureList,int templateId){
        Connection connection=null;
        PreparedStatement ps=null;
        TemplatelinkSer templatelinkSer=new TemplatelinkSer();
        try{
            connection=ConnectionUtil.getJdbcConnection();
            ProcedureEntity procedureEntity;
            for (int i = 0; i < procedureList.size(); i++) {
                procedureEntity=procedureList.get(i);
                String sqlStr=String.format("INSERT INTO ppm_working_procedure(%s) VALUES(ppm_seq.nextval,'%s','%s')");
                ps=connection.prepareStatement(sqlStr);
                ps.setString(1,procedureEntity.getCreator());
                ps.setString(2,procedureEntity.getName());
                ps.executeUpdate();
                ResultSet resultSet=ps.getGeneratedKeys();
                TemplatelinkEntity templatelinkEntity=new TemplatelinkEntity();
                ProcedureEntity procedureEntity1=new ProcedureEntity();
                TemplateEntity templateEntity=new TemplateEntity();
                if(resultSet!=null){
                    resultSet.next();
                    ---templateId=resultSet.getInt(1);
                    templatelinkSer.addTemplink();

                }
            }
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

    //业务逻辑需要改变，如果是添加关系就单纯的添加关系，别还弄个删除
    public void addIntoTemplate(int templateId,String[]procedureIds,String currentUser,Connection connection){
        Connection connection2=ConnectionUtil.getConnection();
        Statement statement=null;
        try{
            statement=connection2.createStatement();
            log.info("procedureIds={}", Arrays.toString(procedureIds));
            String sqlStr;
            String seleStr;
            ResultSet rs=null;
            //循环插入关系数据
            for (int i = 0; i <procedureIds.length; i++) {
                seleStr=String.format("SELECT id FROM PPM_TEMPLATE_WORK_LINK WHERE TEMPLATE_ID=%s AND TW_ID=%s",
                        templateId,procedureIds[i]);
                rs=statement.executeQuery(seleStr);
                if(rs!=null&&rs.next()){
                    //当前关系已存在，进行下一轮存储
                    continue;
                }
                sqlStr=String.format(
                        "INSERT INTO PPM_TEMPLATE_WORK_LINK(id,creator,template_id,tw_id,ppm_order) " +
                                "VALUES (ppm_seq.nextval,'%s',%s,%s,ppm_order_num_seq.nextval)",
                        currentUser,templateId,procedureIds[i]);
                log.info("插入id为“{}”的工序时的sql为“{}”",procedureIds[i],sqlStr);
                statement.execute(sqlStr);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection2,statement);
        }
    }
    /**
     * 删除指定模板中的所有工序
     * @Author Fxiao
     * @Description
     * @Date 14:32 2019/7/4
     * @param templateId 模板id
     * @return void
     **/
    public void deleteFromeTemplate(int templateId){
        Connection connection=ConnectionUtil.getConnection();;
        Statement statement=null;
        try {
            statement=connection.createStatement();
            String sqlStr="DELETE FROM PPM_TEMPLATE_WORK_LINK WHERE TEMPLATE_ID="+templateId;
            statement.execute(sqlStr);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionUtil.close(connection,statement);
        }
    }


    /**
     * 修改工序
     * @Author Fxiao
     * @Description
     * @Date  2019/6/10
     * @param procedureEntity
     * @return void
     **/
    public void updateProcedure(ProcedureEntity procedureEntity){
        Connection connection=null;
        Statement statement=null;
        try{
            connection=ConnectionUtil.getJdbcConnection();
            statement=connection.createStatement();
            String sqlStr=String.format("UPDATE ppm_working_procedure SET name='%s' AND updateTime='%s' WHERE id ='%s'",
                    procedureEntity.getName(),procedureEntity.getUpdateTime(),procedureEntity.getId());
            statement.executeQuery(sqlStr);

        }

        catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection,statement);
        }
    }
    /**
     * 删除工序
     * @Author Fxiao
     * @Description
     * @Date  2019/6/10
     * @param id
     * @return void
     **/
    public void deleteProcedure(int id){
        Connection connection=null;
        Statement statement=null;
        try{
            connection= ConnectionUtil.getJdbcConnection();
            statement=connection.createStatement();
            String sqlStr=String.format("UPDATE ppm_working_procedure SET DEL_FLAG=1 where ID ="+id);
            statement.executeQuery(sqlStr);

        }

        catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection,statement);
        }
    }
    public List<ProcedureEntity> getByTemplate(int templateId,Connection conn){
        List<ProcedureEntity> list=new LinkedList<>();
        Statement statement=null;
        try{
            statement=conn.createStatement();
            String sqlStr=String.format(
                    "SELECT a.* FROM PPM_WORKING_PROCEDURE a,PPM_TEMPLATE_WORK_LINK b WHERE a.ID=b.TW_ID AND b.TEMPLATE_ID=%s and a.DEL_FLAG=0"
                    ,templateId);
            log.debug("合成后的sql={}",sqlStr);
            ResultSet newResultSet=statement.executeQuery(sqlStr);
            if(newResultSet!=null){
                while(newResultSet.next()){
                    ProcedureEntity procedureEntity=new ProcedureEntity();
                    procedureEntity.setId(newResultSet.getInt("id"));
                    procedureEntity.setCreateTime(newResultSet.getDate("createTime"));
                    procedureEntity.setUpdateTime(newResultSet.getDate("updateTime"));
                    procedureEntity.setCreator(newResultSet.getString("creator"));
                    procedureEntity.setName(newResultSet.getString("name"));

                    CharacteristicSer characteristicSer=new CharacteristicSer();
                    List<CharacteristicEntity> characList=characteristicSer.getCharacList(procedureEntity.getId(),conn);
                    procedureEntity.setCharacList(characList);
                    list.add(procedureEntity);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionUtil.close(null,statement);
        }
        return list;
    }

}

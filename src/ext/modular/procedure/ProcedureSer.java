package ext.modular.procedure;

import ext.modular.characteristic.CharacteristicEntity;
import ext.modular.characteristic.CharacteristicSer;
import ext.modular.common.ConnectionUtil;
import ext.modular.templatelink.TemplatelinkSer;
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
    //根据工序id查询工序信息
    public ProcedureEntity getProcedureById(int id){
        Connection connection=null;
        Statement statement=null;
        ResultSet resultSet=null;
        String str="";
        ProcedureEntity procedureEntity=new ProcedureEntity();
        try{
            connection=ConnectionUtil.getJdbcConnection();
            statement= connection.createStatement();
            String sqlStr=String.format("SELECT %s FROM ppm_working_procedure where id=%s and DEL_FLAG=0  ORDER BY createTime",selectField,id);
            resultSet=statement.executeQuery(sqlStr);
            if(resultSet!=null){
                while (resultSet.next()){
                    procedureEntity.setId(resultSet.getInt("ID"));
                    procedureEntity.setCreator(resultSet.getString("creator"));
                    procedureEntity.setName(resultSet.getString("name"));
                    procedureEntity.setCreateTime(resultSet.getDate("createTime"));
                    procedureEntity.setUpdateTime(resultSet.getTime("updateTime"));
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
        return procedureEntity;
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

    /**
     * 给模板下添加工序（兼容旧调用接口）
     * @Author Fxiao
     * @Description
     * @Date 10:20 2019/7/12
     * @param templateId 模板id
     * @param procedureIds 工序表已有数据的id数组
     * @param currentUser 当前用户名
     * @param connection 连接，可以传null
     * @return void
     **/
    public void addIntoTemplate(int templateId,String[]procedureIds,String currentUser,Connection connection){
        int []intIds=new int[procedureIds.length];
        for (int i = 0; i < procedureIds.length; i++) {
            intIds[i]=Integer.parseInt(procedureIds[i]);
        }
        TemplatelinkSer templatelinkSer=new TemplatelinkSer();
        templatelinkSer.addToTemplate(templateId,intIds,currentUser);
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
    /**
     * 重写该方法，将从关系表中去该工序，而不是从工序表
     * @Author Fxiao
     * @Description
     * @Date 15:32 2019/7/10
     * @param templateId 模板id
     * @param conn 数据库连接
     * @return java.util.List<ext.modular.procedure.ProcedureEntity>
     **/
    public List<ProcedureEntity> getByTemplate(int templateId,Connection conn){
        List<ProcedureEntity> list=new LinkedList<>();
        Statement statement=null;
        try{
            statement=conn.createStatement();
            String sqlStr=String.format(
                    "SELECT * FROM PPM_TEMPLATE_WORK_LINK where template_id=%s AND DEL_FLAG=0"
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
                    procedureEntity.setName(newResultSet.getString("tw_name"));
                    //获取工序中的特性
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

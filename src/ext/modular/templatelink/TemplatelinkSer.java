package ext.modular.templatelink;

import ext.modular.common.ConnectionUtil;
import org.springframework.stereotype.Service;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * des:
 *  工序模板关系的ser层
 * @author fxiao
 * @date 2019/6/10
 */

@Service
public class TemplatelinkSer {
    private String selectField="id,createTime,updateTime,creator,tw_id,template_id,tw_name,tw_creator,ppm_order";
    private String insertField="id,creator,tw_id,template_id,tw_name,tw_creator,ppm_order";
    /**
     * 获取工序模板关系列表
     * @Author Fxiao
     * @Description
     * @Date  2019/6/11
     * @param
     * @return java.util.List<ext.modular.templatelink.TemplatelinkEntity>
     **/
    public List<TemplatelinkEntity> getTemplinkList(){
        Connection connection=null;
        Statement statement=null;
        ResultSet resultSet=null;
        String str="";
        List<TemplatelinkEntity> templinkList=new LinkedList<TemplatelinkEntity>();
        try{
            connection=ConnectionUtil.getJdbcConnection();
            statement= connection.createStatement();
            String sqlStr=String.format("SELECT %s FROM ppm_template_work_link where DEL_FLAG=0 ORDER BY createTime",selectField);
            resultSet=statement.executeQuery(sqlStr);
            if(resultSet!=null){
                while (resultSet.next()){
                    TemplatelinkEntity templatelinkEntity=new TemplatelinkEntity();
                    templatelinkEntity.setId(resultSet.getInt("ID"));
                    templatelinkEntity.setCreator(resultSet.getString("creator"));
                    templatelinkEntity.setCreateTime(resultSet.getDate("createTime"));
                    templatelinkEntity.setUpdateTime(resultSet.getTime("updateTime"));
                    templatelinkEntity.getTemplateEntity().setId(resultSet.getInt("template_id"));
                    templatelinkEntity.getProcedureEntity().setId(resultSet.getInt("tw_id"));
                    templatelinkEntity.getProcedureEntity().setName(resultSet.getString("tw_name"));
                    templatelinkEntity.getProcedureEntity().setCreator(resultSet.getString("tw_creator"));
                    templatelinkEntity.setPpm_order(resultSet.getInt("ppm_order"));
                    templinkList.add(templatelinkEntity);
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
        return templinkList;
    }
    /**
     * 增加工序模板关系
     * @Author Fxiao
     * @Description
     * @Date  2019/6/11
     * @param templatelinkEntity
     * @return void
     **/
    public void addTemplink(TemplatelinkEntity templatelinkEntity){
        Connection connection=null;
        Statement statement=null;
        try{
            connection=ConnectionUtil.getJdbcConnection();
            statement=connection.createStatement();
            String sqlStr=String.format("INSERT INTO ppm_template_work_link(%s) VALUES(ppm_seq.nextval,'%s','%s','%s','%s','%s',ppm_order_num_seq.nextval)",
                    insertField,templatelinkEntity.getCreator(),
                    templatelinkEntity.getProcedureEntity().getId(),
                    templatelinkEntity.getTemplateEntity().getId(),
                    templatelinkEntity.getProcedureEntity().getName(),
                    templatelinkEntity.getProcedureEntity().getCreator());
            statement.executeQuery(sqlStr);

        }

        catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
      *增加工序List集合
     * @Author Fxiao
     * @Description
     * @Date 20:07 2019/6/20
     * @param templateId 模板id
     * @param procedureId 工序id列表
     * @param creator 创建人
     * @return void
     **/
    public void addTemplink(List<TemplatelinkEntity> templatelinkList){
        Connection connection=null;
        PreparedStatement ps=null;
        try{
            String sql="INSERT INTO ppm_template_work_link(id,CREATOR,TEMPLATE_ID,TW_ID,TW_NAME,TW_CREATOR,PPM_ORDER) " +
                    "values(ppm_seq.nextval,?,?,?,?,?,ppm_order_num_seq.nextval);";
            for(TemplatelinkEntity templatelink:templatelinkList)
            {
            	ps=connection.prepareStatement(sql);
            	ps.setString(1,templatelink.getCreator());
                ps.setInt(2,Integer.valueOf(templatelink.getTemplateEntity().getId()));
                ps.setInt(3,Integer.valueOf(templatelink.getProcedureEntity().getId()));
                ps.setString(4,templatelink.getProcedureEntity().getName());
                ps.setString(5,templatelink.getProcedureEntity().getCreator());
                ps.execute();
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionUtil.close(connection,ps);
        }
    }
    /**
     * 修改工序模板关系
     * @Author Fxiao
     * @Description
     * @Date  2019/6/11
     * @param templatelinkEntity
     * @return void
     **/
    public void updateTemplink(TemplatelinkEntity templatelinkEntity){
        Connection connection=null;
        Statement statement=null;
        try{
            connection=ConnectionUtil.getJdbcConnection();
            statement=connection.createStatement();
            String sqlStr=String.format("UPDATE ppm_template_work_link SET updateTime='%s', tw_id='%s'"+
                            ", template_id='%s', tw_name='%s', tw_creator='%s' , ppm_order='%s' WHERE id ='%s'",
                    templatelinkEntity.getUpdateTime(),templatelinkEntity.getProcedureEntity().getId(),
                    templatelinkEntity.getTemplateEntity().getId(),
                    templatelinkEntity.getProcedureEntity().getName(),
                    templatelinkEntity.getProcedureEntity().getCreator(),
                    templatelinkEntity.getPpm_order(),templatelinkEntity.getId());
            statement.executeQuery(sqlStr);

        }

        catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 删除工序模板关系
     * @Author Fxiao
     * @Description
     * @Date  2019/6/11
     * @param id
     * @return void
     **/
    public void deleteTemplinkById(int id){
        Connection connection=null;
        Statement statement=null;
        try{
            connection= ConnectionUtil.getJdbcConnection();
            statement=connection.createStatement();
            String sqlStr=String.format("DELETE FROM ppm_template_work_link where ID ='%s'",id);
            statement.executeQuery(sqlStr);

        }

        catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 根据模板id删除工序模板关系
     * @Author Fxiao
     * @Description
     * @Date  2019/6/11
     * @param id
     * @return void
     **/
    public void deleteTemplinkByTemplateId(int id){
        Connection connection=null;
        Statement statement=null;
        try{
            connection= ConnectionUtil.getJdbcConnection();
            statement=connection.createStatement();
            String sqlStr=String.format("DELETE FROM ppm_template_work_link where template_id ='%s'",id);
            statement.executeQuery(sqlStr);

        }

        catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    

    /**
     * 根据模板id和工序id删除工序模板关系
     * @Author Fxiao
     * @Description
     * @Date  2019/6/11
     * @param id
     * @return void
     **/
    public void deleteTemplinkByTemplateIdAndProcedureId(int templateId,int procedureId){
        Connection connection=null;
        Statement statement=null;
        try{
            connection= ConnectionUtil.getJdbcConnection();
            statement=connection.createStatement();
            String sqlStr=String.format("DELETE FROM ppm_template_work_link where template_id ='%s' and tw_Id = '%s'",templateId,procedureId);
            System.out.println(sqlStr);
            statement.executeQuery(sqlStr);

        }

        catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

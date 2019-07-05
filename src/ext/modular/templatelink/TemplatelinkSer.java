package ext.modular.templatelink;

import ext.modular.common.ConnectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private String selectField="id,createTime,updateTime,creator,tw_id,template_id,ppm_order";
    private final Logger log= LoggerFactory.getLogger(this.getClass());
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
            String sqlStr=String.format("SELECT %s FROM ppm_template_work_link ORDER BY createTime",selectField);
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
     * @param templatelink 模板工序关系表
     * @return void
     **/
    public void addTemplink(TemplatelinkEntity templatelink){
        Connection connection=null;
        Statement statement=null;
        PreparedStatement ps=null;
        try{
            connection=ConnectionUtil.getJdbcConnection();
            statement=connection.createStatement();

            //判断重复
            String haveRepeat="SELECT id FROM ppm_template_work_link WHERE TEMPLATE_ID =? AND TW_ID=?";
            ps=connection.prepareStatement(haveRepeat);
            ps.setInt(1,templatelink.getTemplateEntity().getId());
            ps.setInt(2,templatelink.getProcedureEntity().getId());
            ResultSet rs=ps.executeQuery();
            if(rs!=null&&rs.next()){
                log.info("正在执行插入模板工序关系的方法（ext.modular.templatelink.TemplatelinkSer.addTemplink" +
                        "(ext.modular.templatelink.TemplatelinkEntity)）。在插入templateId={},procedureId={}的数据" +
                        "时，在数据库中找到已有数据，dataId={}，所以将跳过该数据的插入操作",
                        templatelink.getTemplateEntity().getId(),
                        templatelink.getProcedureEntity().getId(),
                        rs.getInt("id")
                );
                return ;
            }

            String sqlStr=String.format("INSERT INTO ppm_template_work_link(id,creator,tw_id,template_id,ppm_order)" +
                            " VALUES(ppm_seq.nextval,'%s','%s','%s','%s')",
                    templatelink.getCreator(),templatelink.getProcedureEntity().getId(),
                    templatelink.getTemplateEntity().getId(),templatelink.getPpm_order());
            statement.executeQuery(sqlStr);
        }

        catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 增加工序模板关系
     * @Author Fxiao
     * @Description
     * @Date 20:07 2019/6/20
     * @param procedureIdStrs 模板id
     * @param procedureIdStrs 工序id列表
     * @param creator 创建人
     * @return void
     **/
    public void addTemplink(String templateIdStr,String[]procedureIdStrs,String creator){
        int templateId=Integer.parseInt(templateIdStr);
        Connection connection=null;
        PreparedStatement ps=null;
        try{
            String haveRepeat="SELECT id FROM ppm_template_work_link WHERE TEMPLATE_ID =? AND TW_ID=?";
            String sql="INSERT INTO ppm_template_work_link(id,CREATOR,TEMPLATE_ID,TW_ID) " +
                    "values(ppm_seq.nextval,?,?,?);";
            for (int i = 0; i < procedureIdStrs.length; i++) {
                int procedureId=Integer.parseInt(procedureIdStrs[i]);
                //判断重复
                ps=connection.prepareStatement(haveRepeat);
                ps.setInt(1,templateId);
                ps.setInt(2,procedureId);
                ResultSet rs=ps.executeQuery();
                if(rs!=null&rs.next()){
                    log.info("正在执行插入模板工序关系的方法（ext.modular.templatelink.TemplatelinkSer.addTemplink" +
                                    "(java.lang.String, java.lang.String[], java.lang.String)）。在插入templateId={},procedureId={}的数据" +
                                    "时，在数据库中找到已有数据，dataId={}，所以将跳过该数据的插入操作",
                            templateId,
                            procedureId,
                            rs.getInt("id")
                    );
                    continue;
                }
                ps=connection.prepareStatement(sql);
                ps.setString(1,creator);
                ps.setInt(2,templateId);
                ps.setInt(3,procedureId);
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
            String sqlStr=String.format("UPDATE ppm_template_work_link SET updateTime='%s'， tw_id='%s'"+
                            "， template_id='%s' ， ppm_order='%s' WHERE id ='%s'",
                    templatelinkEntity.getUpdateTime(),templatelinkEntity.getProcedureEntity().getId(),
                    templatelinkEntity.getTemplateEntity().getId(),
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
     * 删除工序模板关系
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
     * @param templateId
     * @param procedureId
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

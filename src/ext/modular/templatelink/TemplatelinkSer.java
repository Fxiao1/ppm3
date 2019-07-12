package ext.modular.templatelink;

import ext.modular.common.ConnectionUtil;
import ext.modular.procedure.ProcedureEntity;
import ext.modular.procedure.ProcedureSer;
import ext.modular.procedurelink.ProcedureLinkSer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * des:
 * 工序模板关系的ser层
 *
 * @author fxiao
 * @date 2019/6/10
 */

@Service
public class TemplatelinkSer {
    private String selectField = "id,createTime,updateTime,creator,tw_id,template_id,tw_name,tw_creator,ppm_order";
    private String insertField = "id,creator,tw_id,template_id,tw_name,tw_creator,ppm_order";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取工序模板关系列表
     *
     * @param
     * @return java.util.List<ext.modular.templatelink.TemplatelinkEntity>
     * @Author Fxiao
     * @Description
     * @Date 2019/6/11
     **/
    public List<TemplatelinkEntity> getTemplinkList() {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String str = "";
        List<TemplatelinkEntity> templinkList = new LinkedList<TemplatelinkEntity>();
        try {
            connection = ConnectionUtil.getConnection();
            statement = connection.createStatement();
            String sqlStr = String.format("SELECT %s FROM ppm_template_work_link where DEL_FLAG=0 ORDER BY createTime", selectField);
            resultSet = statement.executeQuery(sqlStr);
            templinkList = getListByResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection, statement);
        }
        return templinkList;
    }
   /**
    * 根据模板和工序获得该实体
    * @Author Fxiao
    * @Description
    * @Date 22:01 2019/7/10
    * @param templateId
    * @param proceId
    * @return ext.modular.templatelink.TemplatelinkEntity
    **/
    public TemplatelinkEntity get(int templateId,int proceId) {
        Connection connection = null;
        Statement statement = null;
        TemplatelinkEntity templatelinkEntity=new TemplatelinkEntity();
        try {
            connection = ConnectionUtil.getConnection();
            statement = connection.createStatement();
            String sqlStr =String.format("SELECT * FROM ppm_template_work_link WHERE TEMPLATE_ID=%s AND TW_ID=%s",
                        templateId,proceId
                    );
            ResultSet resultSet = statement.executeQuery(sqlStr);
            List<TemplatelinkEntity>list=getListByResultSet(resultSet);
            if(list.size()>0){
                templatelinkEntity =list.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection, statement);
        }
        return templatelinkEntity;
    }
    /**
     * 根据当前对象id获取
     * @Author Fxiao
     * @Description
     * @Date 11:21 2019/7/11
     * @param id
     * @return ext.modular.templatelink.TemplatelinkEntity
     **/
    public TemplatelinkEntity get(int id){
        Connection connection = null;
        Statement statement = null;
        TemplatelinkEntity templatelinkEntity=new TemplatelinkEntity();
        try {
            connection = ConnectionUtil.getConnection();
            statement = connection.createStatement();
            String sqlStr ="SELECT * FROM ppm_template_work_link WHERE id="+id;
            ResultSet resultSet = statement.executeQuery(sqlStr);
            templatelinkEntity = getListByResultSet(resultSet).get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection, statement);
        }
        return templatelinkEntity;
    }


    /**
     * 从结果集里面封装对象出来
     * @Author Fxiao
     * @Description
     * @Date 20:59 2019/7/10
     * @param resultSet
     * @return java.util.List<ext.modular.templatelink.TemplatelinkEntity>
     **/
    private List<TemplatelinkEntity> getListByResultSet(ResultSet resultSet) throws SQLException {
        List<TemplatelinkEntity> templinkList = new LinkedList<>();
        if (resultSet != null) {
            while (resultSet.next()) {
                TemplatelinkEntity templatelink = new TemplatelinkEntity();
                templatelink.setId(resultSet.getInt("ID"));
                templatelink.setCreator(resultSet.getString("creator"));
                templatelink.setCreateTime(resultSet.getDate("createTime"));
                templatelink.setUpdateTime(resultSet.getTime("updateTime"));
                templatelink.getTemplateEntity().setId(resultSet.getInt("template_id"));
                templatelink.getProcedureEntity().setId(resultSet.getInt("tw_id"));
                templatelink.getProcedureEntity().setName(resultSet.getString("tw_name"));
                templatelink.getProcedureEntity().setCreator(resultSet.getString("tw_creator"));
                templatelink.setPpm_order(resultSet.getInt("ppm_order"));
                templinkList.add(templatelink);
            }
        }
        return templinkList;
    }
    /**
     * 查询该模板下的工序
     * @Author Fxiao
     * @Description
     * @Date 21:31 2019/7/10
     * @param templateId
     * @return java.util.List<ext.modular.templatelink.TemplatelinkEntity>
     **/
    public List<TemplatelinkEntity> getListByTemplate(int templateId){
        Connection connection = null;
        Statement statement = null;
        List<TemplatelinkEntity> templinkList = new LinkedList<TemplatelinkEntity>();
        try {
            connection = ConnectionUtil.getConnection();
            statement = connection.createStatement();
            String sqlStr = "SELECT * FROM ppm_template_work_link WHERE TEMPLATE_ID="+templateId;
            ResultSet resultSet = statement.executeQuery(sqlStr);
            templinkList = getListByResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection, statement);
        }
        return templinkList;
    }

    /**
     * 增加工序模板关系
     *
     * @param templatelinkEntity
     * @return void
     * @Author Fxiao
     * @Description
     * @Date 2019/6/11
     **/
    public void addTemplink(TemplatelinkEntity templatelinkEntity) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = ConnectionUtil.getJdbcConnection();
            statement = connection.createStatement();
            String sqlStr = String.format("INSERT INTO ppm_template_work_link(%s) VALUES(ppm_seq.nextval,'%s','%s','%s','%s','%s',ppm_order_num_seq.nextval)",
                    insertField, templatelinkEntity.getCreator(),
                    templatelinkEntity.getProcedureEntity().getId(),
                    templatelinkEntity.getTemplateEntity().getId(),
                    templatelinkEntity.getProcedureEntity().getName(),
                    templatelinkEntity.getCreator());
            log.info("增加工序的语句为=“{}”", sqlStr);
            statement.executeQuery(sqlStr);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 给模板下添加工序
     * @Author Fxiao
     * @Description
     * @Date 10:10 2019/7/12
     * @param templateId 模板id
     * @param procedureIds 工序id数组（工序表里的真实id）
     * @param currentUser 当前用户
     * @return void
     **/
    public void addToTemplate(int templateId,int[]procedureIds,String currentUser){
        Connection connection=null;
        PreparedStatement ps=null;
        connection=ConnectionUtil.getConnection();
        String sql="INSERT INTO PPM_TEMPLATE_WORK_LINK(ID,CREATOR,TEMPLATE_ID,TW_ID,TW_NAME,TW_CREATOR,PPM_ORDER)" +
                "values(ppm_seq.nextval,?,?,?,?,?,ppm_order_num_seq.nextval)";
        ProcedureSer procedureSer=new ProcedureSer();

        try {
            for (int i = 0; i < procedureIds.length; i++) {
                //先去重
                TemplatelinkEntity templink=get(templateId,procedureIds[i]);
                if(templink.getId()>0){
                    //说明已有数据了
                    continue;
                }
                ps=connection.prepareStatement(sql);
                ps.setString(1,currentUser);
                ps.setInt(2,templateId);
                ps.setInt(3,procedureIds[i]);
                ProcedureEntity procedure=procedureSer.getProcedureById(procedureIds[i]);
                ps.setString(4,procedure.getName());
                ps.setString(5,procedure.getCreator());
                int updateRow=ps.executeUpdate();
                log.info("i={},updateRow={}",i,updateRow);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            ConnectionUtil.close(connection,ps);
        }
    }

    /**
     * 更新模板下的工序
     * @Description
     * @Date 10:11 2019/7/11
     * @param procedureIdList 前台工序集合
     * @param templateId 模板id
     * @param templateName 模板名称
     * @param currentUserName 当前登录用户名
     * @param request
     * @return void
     **/
    public void updateProceFromTemplate(List<String> procedureIdList, int templateId, String templateName, String currentUserName
        , HttpServletRequest request){
        //准备工序模板关系数据
        //找到模板中的工序
        List<TemplatelinkEntity>TemplatelinkList=getListByTemplate(templateId);
        log.info("进入ext.modular.templatelink.TemplatelinkSer.addToTemplate方法");

        ProcedureLinkSer procedureLinkSer=new ProcedureLinkSer();
        //前台一个工序都没有了，则删除这里的所有工序
        if(procedureIdList.size()==0){
            deleteTemplinkByTemplateId(templateId);
        }

        for (int j = 0; j < TemplatelinkList.size(); j++) {
            int currentProcedureLinkId=TemplatelinkList.get(j).getId();
            String currentProLinkIdStr=String.valueOf(currentProcedureLinkId);
            if(procedureIdList.contains(currentProLinkIdStr)){
                //前台也有，后台也有，则更新当前工序,并从前台工序集合里剔除当前元素
                int proceOrder=Integer.parseInt(request.getParameter(currentProLinkIdStr+"_order"));
                log.info("正在更新模板里面的工序，模板id={},工序id={},工序order={}",templateId,currentProLinkIdStr,proceOrder);
                procedureIdList.remove(currentProLinkIdStr);
                TemplatelinkEntity currentTemplatelink=get(currentProcedureLinkId);

                updateProceOrder(currentTemplatelink.getId(),proceOrder);
            }else{
                //后台有，前台没有，则删除后台工序。当然，需要先删除其下的特性
                log.info("正在删除模板里面的工序，模板id={}，模板下的工序关系id={}",templateId,currentProcedureLinkId);
                procedureLinkSer.deleteByProcedure(currentProcedureLinkId);

                deleteTemplinkById(currentProcedureLinkId);
            }
        }
        //最终剩下的数据将不是模板下的工序关系id，而是工序表里面的工序id，据此添加到模板关系里面去。
        ProcedureSer procedureSer=new ProcedureSer();
        for (int i = 0; i < procedureIdList.size(); i++) {
            int currentProcId=Integer.parseInt(procedureIdList.get(i));

            log.info("正在往模板里面添加工序，模板id={},工序id={}",templateId,currentProcId);

            TemplatelinkEntity templatelinkEntity=new TemplatelinkEntity();
            templatelinkEntity.getTemplateEntity().setId(templateId);
            templatelinkEntity.getTemplateEntity().setName(templateName);
            templatelinkEntity.getProcedureEntity().setId(currentProcId);
            templatelinkEntity.getProcedureEntity().setName(
                    request.getParameter(currentProcId+"_name")
            );
            templatelinkEntity.setPpm_order(
                    Integer.parseInt(
                            request.getParameter(currentProcId+"_order")
                    )
            );
            //当前用户名
            templatelinkEntity.setCreator(currentUserName);
            addTemplink(templatelinkEntity);
        }
    }


    /**
     * 增加工序List集合
     *
     * @param templatelinkList
     * @return void
     * @Author ln
     * @Description
     * @Date 19:53 2019/7/10
     **/
    public void addTemplink(List<TemplatelinkEntity> templatelinkList) {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO ppm_template_work_link(id,CREATOR,TEMPLATE_ID,TW_ID,TW_NAME,TW_CREATOR,PPM_ORDER) " +
                    "values(ppm_seq.nextval,?,?,?,?,?,ppm_order_num_seq.nextval);";
            for (TemplatelinkEntity templatelink : templatelinkList) {
                ps = connection.prepareStatement(sql);
                ps.setString(1, templatelink.getCreator());
                ps.setInt(2, Integer.valueOf(templatelink.getTemplateEntity().getId()));
                ps.setInt(3, Integer.valueOf(templatelink.getProcedureEntity().getId()));
                ps.setString(4, templatelink.getProcedureEntity().getName());
                ps.setString(5, templatelink.getProcedureEntity().getCreator());
                ps.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection, ps);
        }
    }

    /**
     * 修改工序模板关系
     *
     * @param templatelinkEntity
     * @return void
     * @Author Fxiao
     * @Description
     * @Date 2019/6/11
     **/
    public void updateTemplink(TemplatelinkEntity templatelinkEntity) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = ConnectionUtil.getJdbcConnection();
            statement = connection.createStatement();
            String sqlStr = String.format("UPDATE ppm_template_work_link SET updateTime='%s', tw_id='%s'" +
                            ", template_id='%s', tw_name='%s', tw_creator='%s' , ppm_order='%s' WHERE id ='%s'",
                    templatelinkEntity.getUpdateTime(), templatelinkEntity.getProcedureEntity().getId(),
                    templatelinkEntity.getTemplateEntity().getId(),
                    templatelinkEntity.getProcedureEntity().getName(),
                    templatelinkEntity.getProcedureEntity().getCreator(),
                    templatelinkEntity.getPpm_order(), templatelinkEntity.getId());
            statement.executeQuery(sqlStr);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            ConnectionUtil.close(connection,statement);
        }
    }
    /**
     * 更新工序顺序
     * @Author Fxiao
     * @Description
     * @Date 22:15 2019/7/10
     * @param id
     * @param proceOrder
     * @return void
     **/
    public void updateProceOrder(int id,int proceOrder){
        Connection connection = null;
        Statement statement = null;
        try {
            connection = ConnectionUtil.getJdbcConnection();
            statement = connection.createStatement();
            String sqlStr =String.format("UPDATE ppm_template_work_link SET PPM_ORDER=%s WHERE id=%s",
                        proceOrder,id
                    );
            statement.executeQuery(sqlStr);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            ConnectionUtil.close(connection,statement);
        }

    }



    /**
     * 删除工序模板关系
     *
     * @param id
     * @return void
     * @Author Fxiao
     * @Description
     * @Date 2019/6/11
     **/
    public void deleteTemplinkById(int id) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = ConnectionUtil.getJdbcConnection();
            statement = connection.createStatement();
            String sqlStr = String.format("DELETE FROM ppm_template_work_link where ID ='%s'", id);
            statement.executeQuery(sqlStr);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据模板id删除工序模板关系
     *
     * @param id
     * @return void
     * @Author Fxiao
     * @Description
     * @Date 2019/6/11
     **/
    public void deleteTemplinkByTemplateId(int id) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = ConnectionUtil.getJdbcConnection();
            statement = connection.createStatement();
            String sqlStr = String.format("DELETE FROM ppm_template_work_link where template_id =%s", id);
            statement.executeQuery(sqlStr);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据模板id和工序id删除工序模板关系
     *
     * @param templateId
     * @param procedureId
     * @return void
     * @Author ln
     * @Description
     * @Date 19:53 2019/7/10
     **/
    public void deleteTemplinkByTemplateIdAndProcedureId(int templateId, int procedureId) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = ConnectionUtil.getJdbcConnection();
            statement = connection.createStatement();
            String sqlStr = String.format("DELETE FROM ppm_template_work_link where template_id ='%s' and tw_Id = '%s'", templateId, procedureId);
            System.out.println(sqlStr);
            statement.executeQuery(sqlStr);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

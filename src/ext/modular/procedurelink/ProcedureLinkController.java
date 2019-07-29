package ext.modular.procedurelink;

import ext.modular.characteristic.CharacteristicEntity;
import ext.modular.common.ConnectionUtil;
import ext.modular.common.ResultUtils;
import ext.modular.templatelink.TemplatelinkEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import wt.session.SessionHelper;
import wt.util.WTException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;

/**
 * des:特性工序关系
 *
 * @author ln
 * @date 2019年7月10日16:13:42
 */
@Controller
public class ProcedureLinkController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private Connection connection = null;

    public ProcedureLinkController() {
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.HEAD})
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws WTException {
        connection = ConnectionUtil.getConnection();
        ProcedureLinkSer ser = new ProcedureLinkSer();

        String jsonStr = "";
        String actionName = request.getParameter("actionName");
        log.info("actionName={}", actionName);
        //获取检验特性与工序关系列表
        if ("get".equals(actionName)) {
            List<ProcedureLinkEntity> ProcedureLinkList = ser.getProcedureLinkList();
            if (ProcedureLinkList != null) {
                log.info("获取工序模板关系列表成功");
                jsonStr = ResultUtils.succ(ProcedureLinkList);
                log.info("工序模板关系列表characList为", jsonStr);
            } else {
                log.info("获取工序模板关系列表失败！");
                jsonStr = ResultUtils.error("获取工序模板关系列表失败！");
            }
        } else if ("getByTemplate".equals(actionName)) {
            log.info("根据模板获得工序");
            String templateIdStr = request.getParameter("templateId");
            log.info("获取的templateIdStr={}", templateIdStr);
            System.out.println("获取的templateIdStr=" + templateIdStr);
            if (StringUtils.isEmpty(templateIdStr)) {
                jsonStr = ResultUtils.error("没有找到模板id");
                System.out.println("没有找到模板id");
            } else {
                int templateId = Integer.valueOf(templateIdStr);
                List<ProcedureLinkEntity> ProcedureLinkList = ser.getByTemplate(templateId, connection);
                jsonStr = ResultUtils.succ(ProcedureLinkList);
            }
        }else if("getByProcedure".equals(actionName)){
            //根据工序id获取其下的特性
            //这里的工序id是模板里面独有的工序的id，而不是工序对象里面的工序id
            String procedureIdStr=request.getParameter("procedureId");
            if(StringUtils.isEmpty(procedureIdStr)){
                jsonStr=ResultUtils.error("未获取到工序id");
            }else{
                List<ProcedureLinkEntity> list=ser.getByProcedure(Integer.parseInt(procedureIdStr));
                jsonStr= ResultUtils.succ(list);
            }
        }else if("getById".equals(actionName)){
            //获取特性
            String idStr=request.getParameter("id");
            if(StringUtils.isEmpty(idStr)){
                jsonStr=ResultUtils.error("未获取到id");
            }else{
                ProcedureLinkEntity procedureLink=ser.getById(Integer.parseInt(idStr));
                jsonStr= ResultUtils.succ(procedureLink);
            }
        }
        /*
        *修改检验特性与工序关系
        *ln
        **/
        else if ("post".equals(actionName)) {
            ProcedureLinkEntity procedureLink = new ProcedureLinkEntity();
            String id = request.getParameter("id");
            String proLinkId = request.getParameter("twId");
            String chara_name = request.getParameter("name");
            String coffucient = request.getParameter("coefficient");
            String total = request.getParameter("total");
            String checkType = request.getParameter("checkType");
            log.info("id={},pro_link_id={},chara_name={},coffucient={},total={},checkType={}"
                    ,id, proLinkId, chara_name, coffucient, total,checkType);
            //准备检验特性关系数据
            TemplatelinkEntity templateLink = new TemplatelinkEntity();
            templateLink.setId(Integer.parseInt(proLinkId));
            CharacteristicEntity character = new CharacteristicEntity();
            character.setName(chara_name);
            character.setCoefficient(Integer.parseInt(coffucient));
            character.setTotal(Integer.parseInt(total));
            character.setCheckType(checkType);
            procedureLink.setTemplatelink(templateLink);
            procedureLink.setCharacter(character);

            //创建人
            wt.org.WTPrincipal current = SessionHelper.manager.getPrincipal();
            procedureLink.setCreator(current.getName());
            //n
            if (StringUtils.isEmpty(id)) {
                //新增
                ser.addProcedureLink(procedureLink);
                jsonStr = ResultUtils.succ(null, "新增成功");
            } else {
                procedureLink.setId(Integer.parseInt(id));
                int updateRow=ser.updateProcedureLink(procedureLink);
                if(updateRow==0){
                    jsonStr = ResultUtils.error("修改失败，具体原因请查看日志");
                }else{
                    jsonStr = ResultUtils.succ(null, "修改成功");
                }
            }
        }
        //根据检验特性关系id删除
        else if ("deleteById".equals(actionName)) {
            String id = (request.getParameter("id"));
            if (StringUtils.isEmpty(id)) {
                jsonStr = ResultUtils.error("删除失败，缺少id信息");
            } else {
                ser.deleteProcedureLink(Integer.parseInt(id));
                jsonStr = ResultUtils.succ(null);
            }
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store,no-cache");
        response.setHeader("Pragma", "no-cache");
        try {
            PrintWriter pw = response.getWriter();
            if (null != jsonStr) {
                pw.print(jsonStr);
            }
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

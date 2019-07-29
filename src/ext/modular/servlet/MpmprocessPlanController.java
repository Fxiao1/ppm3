package ext.modular.servlet;

import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import ext.modular.procedure.ProcedureEntity;
import ext.modular.procedure.ProcedureSer;
import ext.modular.util.MPMUtil;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


/**
 * 工艺规划相关操作servlet
 *
 */
@Controller
public class MpmprocessPlanController {

	public MpmprocessPlanController() {

	}
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST,RequestMethod.HEAD })
	protected void processRequest(HttpServletRequest request,HttpServletResponse response) throws WTPropertyVetoException, InstantiationException, IllegalAccessException, InvocationTargetException, WTException, JSONException, IntrospectionException {
		String jsonStr = "";
		String actionName = request.getParameter("actionName");
	    if ("searchMPM".equals(actionName)) {
			String name = request.getParameter("mpm_name");
			String number = request.getParameter("mpm_number");
			jsonStr = new MPMUtil().getALLMPM(name, number);
		}else if("addOperation".equals(actionName)){
			String oid = request.getParameter("mpm_oid");
			String number = request.getParameter("mpm_number");
			String templateId = request.getParameter("templateId");
			int tId = 0;
			if(StringUtils.isNotEmpty(templateId)){
				tId = Integer.parseInt(templateId);
			}
			jsonStr = new MPMUtil().addMPMoperation(oid,tId);
		}
	  
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Cache-Control", "no-store,no-cache");
		response.setHeader("Pragma", "no-cache");
		try {
			PrintWriter	pw = response.getWriter();
			if (null != jsonStr){
				pw.print(jsonStr);
			}
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
  /**
   * 保存工序表
   * @param oid
   * @return
   * @throws WTRuntimeException
   * @throws WTException
   */
   public String addMPMoperation(String oid) throws WTRuntimeException, WTException{
	   String flag = "0";
	
	    MPMProcessPlan pp = MPMUtil.getProcessPlanByOid(oid);
	    List<MPMOperation> OperationList = MPMUtil.getOperation(pp);
	    List<ProcedureEntity> peList = chang2ProcedureEntity(OperationList);
	    ProcedureSer ps = new ProcedureSer();
	    ps.addProcedure(peList);
	 
	   return flag;
	  
   }
   /**
    * 将系统工序转换为可用PPM中的工序
    * @param OperationList
    * @return
    */
   public List<ProcedureEntity> chang2ProcedureEntity(List<MPMOperation> OperationList){
	   List<ProcedureEntity> PEList = new ArrayList<ProcedureEntity>();
	   for(MPMOperation op:OperationList){
		   ProcedureEntity pe = new ProcedureEntity();
		   pe.setName(op.getName());
		   PEList.add(pe);
	   }
	   return PEList;
   }
}

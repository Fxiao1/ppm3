package ext.modular.util;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import wt.doc.WTDocument;
import wt.fc.ObjectVector;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigException;
import wt.vc.config.LatestConfigSpec;
import wt.vc.struct.StructHelper;

import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.windchill.mpml.MPMDocumentReferenceLink;
import com.ptc.windchill.mpml.processplan.MPMPartToProcessPlanLink;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.MPMProcessPlanHelper;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationHolder;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationMaster;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationToConsumableLink;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationToPartLink;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;
import com.ptc.windchill.mpml.resource.MPMTooling;

import ext.modular.builder.SearchAOLBuilder1;
import ext.modular.common.ResultUtils;
import ext.modular.procedure.ProcedureEntity;
import ext.modular.procedure.ProcedureSer;




public class MPMUtil1 {
	
	/**
	 * 返回工艺计划到操作的使用关系
	 * @param mpmProcessPlan
	 * @return
	 * @throws WTException
	 */
	public static  Vector<MPMOperationUsageLink> getMPMOperationUsageLinks(
			MPMOperationHolder holder) throws WTException {
		Vector<MPMOperationUsageLink> localVector = new Vector<MPMOperationUsageLink>();

		if(holder==null){
			return localVector;
		}
		QueryResult qr = MPMProcessPlanHelper.service.getMPMOperationUsageLinks(holder);
		while ((qr != null) && (qr.hasMoreElements())) {
			Object obj=qr.nextElement();
			localVector.addElement((MPMOperationUsageLink) obj);
		}
		return localVector;
	}

	/**
	 * 根据编号获得对应的操作对象
	 * @param number
	 * @return
	 * @throws WTException
	 */
	public static MPMOperation getMPMOperation(MPMOperationMaster mpmOperationMaster)throws WTException {
		try {
			QueryResult queryResult = VersionControlHelper.service.allVersionsOf(mpmOperationMaster);
			return (MPMOperation) queryResult.nextElement();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	/**
	 * 根据OID获取工艺计划
	 * */
	public static MPMProcessPlan getProcessPlanByOid(String oid){
		try {
			return (MPMProcessPlan) new ReferenceFactory().getReference(oid).getObject();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	
	
	/**
	 * 根据工艺计划获得工序
	 * @throws WTException 
	 * */
	public static  List<MPMOperation> getOperation(MPMOperationHolder holder) throws WTException{
		List<MPMOperation> mpmOperations = new ArrayList<MPMOperation>();
		Vector<MPMOperationUsageLink> mpmOperationUsageLinks = getMPMOperationUsageLinks(holder);
		for (int i = 0; i < mpmOperationUsageLinks.size(); i++) {
			MPMOperationUsageLink mpmOperationUsageLink = mpmOperationUsageLinks.get(i);
			MPMOperationMaster mpmOperationMaster = (MPMOperationMaster) mpmOperationUsageLink.getRoleBObject();
			MPMOperation mpmOperation = getMPMOperation(mpmOperationMaster);
			mpmOperations.add(mpmOperation);
		}
		return mpmOperations;
	}
	
	/**
	 * 根据工艺计划获得工序number
	 * @throws WTException 
	 * */
	public static  List<String> getOperationNumber(MPMOperationHolder holder) throws WTException{
		List<String> mpmOperations = new ArrayList<String>();
		Vector<MPMOperationUsageLink> mpmOperationUsageLinks = getMPMOperationUsageLinks(holder);
		for (int i = 0; i < mpmOperationUsageLinks.size(); i++) {
			MPMOperationUsageLink mpmOperationUsageLink = mpmOperationUsageLinks.get(i);
			MPMOperationMaster mpmOperationMaster = (MPMOperationMaster) mpmOperationUsageLink.getRoleBObject();
			mpmOperations.add(mpmOperationMaster.getNumber());
		}
		return mpmOperations;
	}
	
	
	/**
	 * 通过编号获得工艺计划
	 * number为空的时候查询所有工艺计划
	 * @param name
	 * @param number
	 * @return
	 * @throws WTException
	 */
	public static MPMProcessPlan getMaintenanceProcessByNumber(String number) throws WTException {
		QueryResult result = new QueryResult();
		MPMProcessPlan MaintenanceProcess = null;
		SessionServerHelper.manager.setAccessEnforced(true);
		try {
			QuerySpec spec = new QuerySpec(MPMProcessPlan.class);
			spec.setAdvancedQueryEnabled(true);
			if (spec.getConditionCount() > 0) {
				spec.appendAnd();
			}
			// 编号
			if (number != null && number.length() > 0) {
				if (spec.getConditionCount() > 0) {
					spec.appendAnd();
				}
				SearchCondition numberCondition = new SearchCondition(MPMProcessPlan.class, MPMProcessPlan.NUMBER,
						SearchCondition.EQUAL, number);
				spec.appendWhere(numberCondition, new int[] { 0 });
			}
			result = PersistenceHelper.manager.find(spec);
			while (result.hasMoreElements()) {
				Persistable persistableAOL = (Persistable) result.nextElement();
				if (persistableAOL instanceof MPMProcessPlan) {
					MaintenanceProcess = (MPMProcessPlan) persistableAOL;
				}
			}
			
		} catch (Exception e) {
			throw new WTException(e.getMessage());
		} finally {
			SessionServerHelper.manager.setAccessEnforced(false);
		}
		return MaintenanceProcess;
	}
	
	/**
	 * 返回工艺计划的json格式
	 * @param name
	 * @param number
	 * @return
	 * @throws WTException
	 */
	public String getALLMPM(String name,String number) throws WTException{
		List list = new ArrayList();
		SearchAOLBuilder1 search = new SearchAOLBuilder1();
		QueryResult qr = search.getAllAOL(name, number);
		System.out.println("q.r:"+qr.size());
		while (qr.hasMoreElements()) {
            Persistable persistableAOL = (Persistable) qr.nextElement();
            if (persistableAOL instanceof MPMProcessPlan) {
            	MPMProcessPlan aol = (MPMProcessPlan) persistableAOL;
                Map mp = new HashMap();
                mp.put("name", aol.getName());
                mp.put("number", aol.getNumber());
                mp.put("version", aol.getVersionIdentifier().getValue());
                String oid = new ReferenceFactory().getReferenceString(aol).toString();
                mp.put("oid", oid);
                list.add(mp);
            }
        }
		System.out.println("list:"+list.size());
		String backStr = ResultUtils.succ(list);
		return backStr;
	}
	
	/**
	 * 查询AOl对象
	 * lijingtao
	 */
	public QueryResult getAllAOL(String name,String number) throws WTException{
		QueryResult result = new QueryResult();
		ObjectVector resultVector = new ObjectVector();
		SessionServerHelper.manager.setAccessEnforced(true);		
		try {
			QuerySpec spec = new QuerySpec(MPMProcessPlan.class);
			spec.setAdvancedQueryEnabled(true);
			
			// 搜索最新小版本
			if (spec.getConditionCount() > 0) {
				spec.appendAnd();
			}
			SearchCondition condition = new SearchCondition(MPMProcessPlan.class, "iterationInfo.latest", SearchCondition.IS_TRUE);
			spec.appendWhere(condition, new int[] { 0 });
			

/*		        TypeDefinitionReference tdr = TypedUtility.getTypeDefinitionReference(Constants.domain+"."+Constants.TYPE_AOL);
		        SearchCondition typeDefinitionCondition = new SearchCondition(MPMProcessPlan.class,
		        		MPMProcessPlan.TYPE_DEFINITION_REFERENCE + "." +
		                TypeDefinitionReference.KEY + "." +
		                TypeDefinitionForeignKey.BRANCH_ID, SearchCondition.EQUAL,
		                tdr.getKey().getBranchId());
		        spec.appendWhere(typeDefinitionCondition, new int[] { 0 });*/
			// 编号
			if (number != null && number.length() > 0) {
				if (spec.getConditionCount() > 0) {
					spec.appendAnd();
				}
				SearchCondition numberCondition = new SearchCondition(MPMProcessPlan.class, MPMProcessPlan.NUMBER, SearchCondition.EQUAL, number);
				spec.appendWhere(numberCondition, new int[] { 0 });
			}
			// 名称
			if (name != null && name.length() > 0) {
				if (spec.getConditionCount() > 0) {
					spec.appendAnd();
				}
				SearchCondition nameCondition = new SearchCondition(MPMProcessPlan.class, MPMProcessPlan.NAME, SearchCondition.EQUAL, name);
				spec.appendWhere(nameCondition, new int[] { 0 });
			}
			result = PersistenceHelper.manager.find(spec);
//			while (result.hasMoreElements()) {
//		            Persistable persistableAOL = (Persistable) result.nextElement();
//		            if (persistableAOL instanceof MPMProcessPlan) {
//		            	MPMProcessPlan aol = (MPMProcessPlan) persistableAOL;
//		                resultVector.addElement(aol);
//		            }
//		        }
//			result.append(resultVector);
			System.out.println("result:" + result.size());
			
		} catch (Exception e) {
			throw new WTException(e.getMessage());
		} finally {
			SessionServerHelper.manager.setAccessEnforced(false);
		}
		return result;
	}
	
	  /**
	   * 保存工序表
	   * @param oid
	   * @return
	   * @throws WTRuntimeException
	   * @throws WTException
	   */
	   public String addMPMoperation(String oid,int templateId) throws WTRuntimeException, WTException{
		   String flag = "0";
	       System.out.println("mpm_oid:" + oid);
	       
		    MPMProcessPlan pp = MPMUtil1.getProcessPlanByOid(oid);
		    List<MPMOperation> OperationList = MPMUtil1.getOperation(pp);
		    List<ProcedureEntity> peList = chang2ProcedureEntity(OperationList);
		    ProcedureSer ps = new ProcedureSer();
		    //保存工序
		    List<ProcedureEntity> lists = ps.addProcedure(peList);
		    //保存工序和模板关系  
		    List peIds = new ArrayList();
		    for(ProcedureEntity pe:lists){
		    	peIds.add(pe.getId()+"");
		    }
		    String[] procedureIds = new String[peIds.size()];
		    //peIds.toArray(procedureIds);
		    for(int i=0;i<peIds.size();i++){
		    	procedureIds[i] = (String) peIds.get(i);
		    }
		    //获得当前用户
		    wt.org.WTPrincipal current = SessionHelper.manager.getPrincipal();
		    System.out.println("current:" + current.getName());
		    ps.addIntoTemplate(templateId, procedureIds, current.getName(),null);
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

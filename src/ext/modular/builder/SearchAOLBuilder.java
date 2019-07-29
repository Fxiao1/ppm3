package ext.modular.builder;

import com.ptc.mvc.components.*;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import ext.modular.common.ResultUtils;
import org.apache.log4j.Logger;
import wt.fc.ObjectVector;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ComponentBuilder("ext.modular.builder.searchAOL")
public class SearchAOLBuilder extends AbstractComponentBuilder {
	
	private static final Logger log = LogR.getLogger(SearchAOLBuilder.class.getName());
	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
        String number = (String) arg1.getParameter("number");
        String name = (String) arg1.getParameter("name");
		QueryResult result = new QueryResult();
        result = getAllAOL(name,number);
		return result;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {

	        ComponentConfigFactory factory = getComponentConfigFactory();
	        TableConfig table = factory.newTableConfig();
	        ColumnConfig cTypeIconConf = factory.newColumnConfig("type_icon", true);
	        table.addComponent(cTypeIconConf);
	        
	        // 主题
	        ColumnConfig cNameConf = factory.newColumnConfig("name",true);;
	        table.addComponent(cNameConf);
	        // 编码
	        ColumnConfig cNumberConf = factory.newColumnConfig("number",true);
	        table.addComponent(cNumberConf);
	        // 版本
	        ColumnConfig cVersionConf = factory.newColumnConfig("version",true);
	        table.addComponent(cVersionConf);
	        // 状态
	        ColumnConfig cStateConf = factory.newColumnConfig(WTPart.STATE, true);
	        table.addComponent(cStateConf);
	        // 表格右键菜单
	        ColumnConfig nmActions = factory.newColumnConfig("nmActions", true);
	        nmActions.setActionModel("PartCIToolBar");
	    
	        table.addComponent(nmActions);
			table.setLabel("DM");
			table.setId("ext.modular.builder.searchAOL");
			table.setShowCount(true);
			table.setShowCustomViewLink(false);
			table.setSelectable(true);
			table.setSingleSelect(true);
			table.setActionModel("AOLAddToolBar");
	        return table;
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
		SearchAOLBuilder search = new SearchAOLBuilder();
		QueryResult qr = search.getAllAOL(name, number);
		while (qr.hasMoreElements()) {
            Persistable persistableAOL = (Persistable) qr.nextElement();
            if (persistableAOL instanceof MPMProcessPlan) {
            	MPMProcessPlan aol = (MPMProcessPlan) persistableAOL;
                Map mp = new HashMap();
                mp.put("name", aol.getName());
                mp.put("number", aol.getNumber());
                list.add(mp);
            }
        }
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
			if (spec.getConditionCount() > 0) {
		            spec.appendAnd();
		        }
			// 搜索最新小版本
	
			SearchCondition condition = new SearchCondition(MPMProcessPlan.class, "iterationInfo.latest", SearchCondition.IS_TRUE);
			spec.appendWhere(condition, new int[] { 0  });

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
			while (result.hasMoreElements()) {
		            Persistable persistableAOL = (Persistable) result.nextElement();
		            if (persistableAOL instanceof MPMProcessPlan) {
		            	MPMProcessPlan aol = (MPMProcessPlan) persistableAOL;
		                resultVector.addElement(aol);
		            }
		        }
			result.append(resultVector);
			
		} catch (Exception e) {
			throw new WTException(e.getMessage());
		} finally {
			SessionServerHelper.manager.setAccessEnforced(false);
		}
		return result;
	}
}



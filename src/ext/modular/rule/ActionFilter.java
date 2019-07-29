package ext.modular.rule;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;

public class ActionFilter extends DefaultSimpleValidationFilter {
	
	public UIValidationStatus preValidateAction(UIValidationKey uivalidationkey,
            UIValidationCriteria uivalidationcriteria) {
		
		UIValidationStatus  uivalidationstatus = UIValidationStatus.ENABLED;
		//System.out.println("过滤器");
		
		 
	    //得到action中的name   key=name；
	    String key = uivalidationkey.getComponentID();
		try {
			//根据组名获取组对象
			WTGroup sysGroup = GetGroupByName.findGroupByName("系统管理员");
			WTGroup ppmGroup = GetGroupByName.findGroupByName("PPM管理员");
			WTGroup designGroup = GetGroupByName.findGroupByName("工艺设计师");
			WTGroup checkGroup = GetGroupByName.findGroupByName("检验员");
			//获取当前用户对象
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal(); 
			//ppmModel	jynr	ppmProcess	ppmEcharts 四种actionname
			boolean  sysMember = OrganizationServicesHelper.manager.isMember(sysGroup, user); 
			boolean  ppmMember = OrganizationServicesHelper.manager.isMember(ppmGroup, user); 
			boolean  designMember = OrganizationServicesHelper.manager.isMember(designGroup, user); 
			boolean  checkMember = OrganizationServicesHelper.manager.isMember(checkGroup, user); 
			if("ppmModel".equals(key)&&sysMember)
			{
				System.out.println("当前用户为系统管理员，模板定制功能显示");
				return UIValidationStatus.ENABLED;
			}else if("ppmModel".equals(key)&&ppmMember){
				System.out.println("当前用户为ppm管理组成员，模板定制功能显示");
				return UIValidationStatus.ENABLED;
			}else if("jynr".equals(key)&&designMember) {
				return UIValidationStatus.ENABLED;
			}
			else if("jynr".equals(key)&&checkMember) {
				return UIValidationStatus.ENABLED;
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
        return uivalidationstatus.HIDDEN;
    }

   
	
}

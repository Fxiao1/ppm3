package ext.modular.filter;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTException;

public class ActionFilter extends DefaultSimpleValidationFilter {
	
	public UIValidationStatus preValidateAction(UIValidationKey uivalidationkey,
            UIValidationCriteria uivalidationcriteria) {
		
		UIValidationStatus  uivalidationstatus = UIValidationStatus.ENABLED;
		System.out.println("过滤器");
	    String username="";
	    String key = uivalidationkey.getComponentID();
		try {
			username = SessionHelper.getPrincipal().getName();
			System.out.println(username);
			if(username.equals("Administrator") && key.equals("helloWorld"))
			{
				System.out.println("action过滤，隐藏");

				return UIValidationStatus.DISABLED;
			}else if("myProductListTable".equals(key)){
				System.out.println("action过滤，显示");
				return UIValidationStatus.ENABLED;
			}else if("simpleTable".equals(key)) {
				return UIValidationStatus.HIDDEN;
			}
			else if("first".equals(key)) {
				return UIValidationStatus.HIDDEN;
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
        return uivalidationstatus.HIDDEN;
    }

   
	
}

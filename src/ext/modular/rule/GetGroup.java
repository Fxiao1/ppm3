package ext.modular.rule;

import java.util.Enumeration;

import com.ptc.ddl.wtutil.PrincipalHelper;

import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;

public class GetGroup {
	
	public WTGroup group() {
		
		try {
			WTGroup group = GetGroupByName.findGroupByName("设计师");
			WTGroup wtgroup = OrganizationServicesHelper.manager.getGroup("Test");
			 WTUser user = (WTUser) SessionHelper.manager.getPrincipal(); //获取当前用户
			 
			 
			// boolean  isMember = OrganizationServicesHelper.manager.isMember(admingroup, user); 
			 
			 System.out.println("当前组名"+wtgroup.getName());
			 System.out.println("当前用户名"+user.getName());
			  Enumeration enu = OrganizationServicesHelper.manager.members(wtgroup, true);
			  String wt=wtgroup.getPrincipalIdentifier();
			  Enumeration enu1 = wtgroup.parentGroups();
			  System.out.println(enu1);
			  System.out.println("是否含有子组"+enu.hasMoreElements());
			  System.out.println(wt);
			  System.out.println(enu.hasMoreElements());
			  WTGroup wtgroup4 = OrganizationServicesHelper.manager.getGroup("设计师","Test");
			  System.out.println(wtgroup4.getName());
			  
			  while (enu.hasMoreElements()) {
					Object obj = enu.nextElement();
					if (obj != null) {
						
						if (obj instanceof WTGroup) {
                            WTGroup tempUser = (WTGroup) obj;
                            System.out.println("child group name =======>>"+tempUser.getName());
									}
								}
		                   }
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		return null;
		
	}

}

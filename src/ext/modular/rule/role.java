package ext.modular.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainer;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeam;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.project.Role;
import wt.projmgmt.admin.Project2;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;

public class role {
	
	public static WTContainer getContainer(String containerName) throws WTException {
		WTContainer container = null;

		QuerySpec query = new QuerySpec(WTContainer.class);
		query.appendWhere(
				new SearchCondition(WTContainer.class, WTContainer.NAME, SearchCondition.EQUAL, containerName),
				new int[]{0});
		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);

		if (result.hasMoreElements()){
		    container = (WTContainer) result.nextElement();
		}	
		return container;
	}

      /**
       * 判断当前用户是否在容器的角色中
       * @param container
       * @param roleStr
       * @param pri
       * @return
       * @throws WTException
       */
	  public boolean isMemberOfContainerRole(WTContainer container, String roleStr)
	    throws WTException
	  {
		WTPrincipal currentuser = SessionHelper.manager.getPrincipal();
	    ContainerTeam team = null;
	    if ((container instanceof WTLibrary)) {
	      WTLibrary library = (WTLibrary)container;
	      team = (ContainerTeam)library.getContainerTeamManagedInfo()
	        .getContainerTeamId().getObject();
	    }
	    else if ((container instanceof PDMLinkProduct)) {
	      PDMLinkProduct pdm = (PDMLinkProduct)container;
	      team = (ContainerTeam)pdm.getContainerTeamManagedInfo()
	        .getContainerTeamId().getObject();
	    } else if ((container instanceof Project2)) {
	      Project2 project = (Project2)container;
	      team = (ContainerTeam)project.getContainerTeamManagedInfo()
	        .getContainerTeamId().getObject();
	    } else {
	      throw new WTException(
	        "Container must WTLibrary or PDMLinkProduct or Project2!");
	    }
	    if (team != null) {
	      HashMap map = team.getAllMembers();
	      if (map != null) {
	        WTPrincipalReference princialReference = 
	          WTPrincipalReference.newWTPrincipalReference(currentuser);
	        Object obj = map.get(princialReference);
	        if (obj != null) {
	          List list = (ArrayList)obj;
	          Role role = Role.toRole(roleStr);
	          if (list.contains(role)) {
	            return true;
	          }
	        }
	      }
	    }
	    return false;
	  }
	


}

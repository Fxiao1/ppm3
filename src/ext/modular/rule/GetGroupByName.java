package ext.modular.rule;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTGroup;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class GetGroupByName implements RemoteAccess {
	
	public static WTGroup findGroupByName(String groupName) {
		WTGroup group =null;
		if (!RemoteMethodServer.ServerFlag) {
			   Class[] aclass = { GetGroupByName.class };
			   Object[] aobj = { groupName };
			   try {
			    RemoteMethodServer.getDefault().invoke("findGroupByName", GetGroupByName.class.getName(), null, aclass, aobj);
			   } catch (RemoteException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			   } catch (InvocationTargetException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			   }
			  }else {
				 try {
					   QuerySpec qs = new QuerySpec(WTGroup.class);
					   SearchCondition sc = new SearchCondition(WTGroup.class, WTGroup.NAME, SearchCondition.EQUAL, groupName);
					   qs.appendWhere(sc);
					   System.out.println("qs=="+qs.toString());
					   QueryResult qr = PersistenceHelper.manager.find(qs);
					   while (qr.hasMoreElements()) {
					    group = (WTGroup) qr.nextElement();
					    return group;
					   }
					  } catch (Exception e) {
					   e.printStackTrace();
					  }
						
			}
		return group;
	}
}

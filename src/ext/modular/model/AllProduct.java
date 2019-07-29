package ext.modular.model;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.ContainerSpec;
import wt.inf.container.WTContainerHelper;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.pdmlink.PDMLinkProduct;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.OrderByExpression;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * *@author 作者：*
 * 
 * @data 创建时间：2018年10月9日 下午2:43:56*
 * @parameter *
 * @return *
 * @throws *
 */
public class AllProduct implements RemoteAccess {

	private static final String CLASSNAME = AllProduct.class.getName();
	private static final Logger logger = Logger.getLogger(AllProduct.class);
	
	public static void main(String[] args) {

		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		Class cla[] = { String.class };
		Scanner sc = new Scanner(System.in);
		System.out.println("请输入文档编号：");
		String number = sc.next();
		Object obj[] = { number };
		rms.setUserName("pdm");
		rms.setPassword("1");
		try {
			rms.invoke("test", CLASSNAME, null, cla, obj);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 测试方法
	 */
	public static void test(String number) {
		
		 System.out.println("number=="+number);
		 try {
			 WTPrincipal principal = SessionHelper.getPrincipal();
			 System.out.println("获取系统当前用户登录名：principal.getName()="+principal.getName());
			 List<String> getAllProductName = new ArrayList<String>();
			 getAllProductName = getAllProduct(principal, false);
					
		} catch (WTException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * 获取用户有权限的所有的产品
	 * 
	 * @Description:
	 * @Title: getAllProductName
	 * @param principal         用户对象    SessionHelper.getPrincipal()
	 * @param accessControlled  默认false
	 * @return List<String>
	 * @author yli   
	 * @date 2018年12月6日 下午3:49:45
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List getAllProduct(WTPrincipal principal
			, boolean accessControlled) {
		if (logger.isDebugEnabled()) {
			logger.debug("enter the gteALLProductsName() method");
		}
		List modelList=new LinkedList();

		try {
			if (!RemoteMethodServer.ServerFlag) {
				Class[] aclass = { WTPrincipal.class, boolean.class };
				Object[] aobj = { principal, accessControlled };
				return (List) RemoteMethodServer.getDefault().invoke("getAllProduct", CLASSNAME, null,
						aclass, aobj);
			} else {
				SessionServerHelper.manager.setAccessEnforced(false);
				//List<PDMLinkProduct> products = getPDMLinkProductList("", false);
				QueryResult qr = getProductsByPrincipal(principal);
				System.out.println("当前用户下的所有产品数量：qr.size()="+qr.size()+" 产品列表：");
				System.out.println("---------------------------------------------------");
				while (qr.hasMoreElements()) {
					PDMLinkProduct prod = (PDMLinkProduct) qr.nextElement();
					modelList.add(prod);
					System.out.println("prod.getName()="+prod.getName());
				}
				System.out.println("---------------------------------------------------");
				// 获取所有产品的名称
				/*System.out.println("----------------------获取所有产品的名称---------------------------="+products.size());
				for (int i = 0; i < products.size(); i++) {
					System.out.println("product getName is:" + products.get(i).getName());
				}*/
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return modelList;
	}

	/**
	 * 根据用户获取产品列表
	 * 
	 * @Description:
	 * @Title: getProductsByPrincipal
	 * @param principal
	 * @return QueryResult
	 * @author yli
	 * @date 2018年12月12日 上午10:01:59
	 */
	public static QueryResult getProductsByPrincipal(WTPrincipal principal) {
		QueryResult queryResult = null;
		try {
			ContainerSpec containerSpec = new ContainerSpec(PDMLinkProduct.class);
			containerSpec.setUser(WTPrincipalReference.newWTPrincipalReference(principal));
			containerSpec.setMembershipState(ContainerSpec.MEMBERS_AND_GUEST_ONLY);
			queryResult = WTContainerHelper.service.getContainers(containerSpec);
			System.out.println("查询语句"+containerSpec.toString());
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return queryResult;
	}
	
	/**
	 * 通过产品名称查找PDMLinkProduct
	 * 
	 * @Description:
	 * @Title: getPDMLinkProduct
	 * @param name
	 *            产品名称
	 * @param accessControlled
	 *            默认
	 * @return
	 * @throws WTException
	 *             PDMLinkProduct
	 * @author yli
	 * @date 2018年12月12日 上午9:37:05
	 */
	@SuppressWarnings("deprecation")
	public static PDMLinkProduct getPDMLinkProduct(String name, boolean accessControlled) throws WTException {
		PDMLinkProduct product = null;
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (PDMLinkProduct) RemoteMethodServer.getDefault().invoke("getPDMLinkProduct", CLASSNAME, null,
						new Class[] { String.class, boolean.class }, new Object[] { name, accessControlled });
			} else {
				boolean enforce = SessionServerHelper.manager.setAccessEnforced(accessControlled);
				try {
					QuerySpec qus = new QuerySpec(PDMLinkProduct.class);
					SearchCondition sec = new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME,
							SearchCondition.EQUAL, name, false);
					qus.appendSearchCondition(sec);
					ClassAttribute clsAttr = new ClassAttribute(PDMLinkProduct.class, PDMLinkProduct.MODIFY_TIMESTAMP);
					OrderBy order = new OrderBy((OrderByExpression) clsAttr, true);
					qus.appendOrderBy(order);

					QueryResult qur = PersistenceHelper.manager.find(qus);
					if (qur.hasMoreElements()) {
						product = (PDMLinkProduct) qur.nextElement();
					}
				} finally {
					SessionServerHelper.manager.setAccessEnforced(enforce);
				}
			}
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		}
		return product;
	}
	/**
	 * 通过产品名称查找PDMLinkProduct，可支持模糊查找
	 * 
	 * @Description:
	 * @Title: getPDMLinkProductList
	 * @param name               产品名称
	 * @param accessControlled   默认false
	 * @return List<PDMLinkProduct>
	 * @author yli
	 * @date 2018年12月6日 下午3:50:33
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static List<PDMLinkProduct> getPDMLinkProductList(String name, boolean accessControlled) {

		SearchCondition sc = null;
		List<PDMLinkProduct> prodcuts_list = new ArrayList<PDMLinkProduct>();
		if (logger.isDebugEnabled()) {
			logger.debug("enter the getPDMLinkProduct() method，search productName:" + name);
		}
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (List<PDMLinkProduct>) RemoteMethodServer.getDefault().invoke("getPDMLinkProductList", CLASSNAME,
						null, new Class[] { String.class, boolean.class }, new Object[] { name, accessControlled });
			} else {
				SessionServerHelper.manager.setAccessEnforced(accessControlled);
				QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
				// 如果名称为空，查询所有的
				if (null == name || "".equals(name)) {
					sc = new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, SearchCondition.LIKE,
							"%" + name + "%", false);
				} else {// 否则查询指定名称的产品
					sc = new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, SearchCondition.EQUAL, name,
							false);
				}
				qs.appendSearchCondition(sc);
				QueryResult qr = PersistenceHelper.manager.find(qs);

				while (qr.hasMoreElements()) {
					prodcuts_list.add((PDMLinkProduct) qr.nextElement());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (logger.isDebugEnabled()) {
			System.out.println("finish called the getPDMLinkProduct() method, the product size is:" + prodcuts_list.size());
		}
		return prodcuts_list;
	}
	
	
}

package ext.modular.calculate;

import com.google.gson.Gson;
import ext.modular.common.ConnectionUtil;
import ext.modular.common.ResultUtils;
import ext.modular.datainstance.DatainstanceEntity;
import ext.modular.form.FormEntity;
import ext.modular.product.ProductEntity;
import ext.modular.product.ProductSer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * PPM计算控制层
 */
@Controller
public class PPMCalculateDetailController {
    private static Gson gson=new Gson();
    Connection connection= null;
    private final Logger log= LoggerFactory.getLogger(this.getClass());

    public PPMCalculateDetailController(){}

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST,RequestMethod.HEAD })
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("23232323232");
        String jsonStr = "";
        String startStr = request.getParameter("startDate");
        String endStr = request.getParameter("endDate");
        String TypeVal = request.getParameter("TypeVal");
        System.out.println("接收到的字符串"+TypeVal);
        int useTypeVal = Integer.parseInt(TypeVal);
        System.out.println("转换成整型"+useTypeVal);
        String actionName = request.getParameter("actionName");
        System.out.println("actionName = " + actionName);
        String date = request.getParameter("dateTime");
        System.out.println("echars 统计中的 dataTime = " + date);
        List<PPMCalculateDetailEntity> formList=null;
        if ("getForm".equals(actionName)){
            if (!"".equals(startStr) && !"".equals(endStr)) {
            	if(useTypeVal==0) {
            		formList = getPPMCalculateDetailList(startStr,endStr);
            	}else if(useTypeVal==1) {
            		formList = getProductPPMCalculateDetailList(startStr,endStr);
            	}else if(useTypeVal==2) {
            		formList = getXHPPMCalculateDetailList(startStr,endStr);
            	}else if(useTypeVal==3) {
            		formList = getMJPPMCalculateDetailList(startStr,endStr);
            	}
                
                if (formList != null) {
                    log.info("获取的列表长度为{}", String.valueOf(formList.size()));
                    jsonStr = ResultUtils.succ(formList, "获取表单列表");
                    log.info(jsonStr);
                } else {
                    log.info("获取列表失败！");
                    jsonStr = ResultUtils.error("获取列表失败！");
                }
            }
        } else if ("getData".equals(actionName)){
            System.out.println("获取Map数据方法");
            Map<String,Map<String,Integer>> mapData = getPPMData(date);
            if (mapData != null) {
                log.info("获取的数据长度为{}", String.valueOf(mapData.size()));
                jsonStr = ResultUtils.succ(mapData, "获取数据成功");
                log.info(jsonStr);
            } else {
                log.info("获取数据失败！");
                jsonStr = ResultUtils.error("获取数据列表失败！");
            }
        }

        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store,no-cache");
        response.setHeader("Pragma", "no-cache");
        try {
            PrintWriter pw =response.getWriter();
            if(null!=jsonStr){
                pw.print(jsonStr);
            }
            pw.flush();
            pw.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }


    /**
     * 根据起始时间获取工序统计列表
     * @param startStr
     * @param endStr
     * @return
     */
    public  List<PPMCalculateDetailEntity> getPPMCalculateDetailList(String startStr, String endStr){
        List<PPMCalculateDetailEntity> formList = new LinkedList<PPMCalculateDetailEntity>();
        try {
            connection = ConnectionUtil.getJdbcConnection();
            log.info("parameterName={ "+ startStr +" "+ endStr +" }");
            PPMCalculateDetailSer service = new PPMCalculateDetailSer();
            List<String> procedureNameList = service.getProcedureNameList(connection,startStr,endStr);//获取工序名称列表
            System.out.println("controller>>>>procedureNameList size() === " + procedureNameList.size());
            //去重
            List<String> listNew=new ArrayList<>();
            Set set=new HashSet();
            for (String str:procedureNameList) {
                if(set.add(str)){
                    listNew.add(str);
                }
            }
            for (String name: listNew) {
                PPMCalculateDetailEntity calculateDetailEntity = new PPMCalculateDetailEntity();
                calculateDetailEntity.setProcedureName(name);//工序名称

                calculateDetailEntity.setCharacName(service.getCharacNameList(connection,name));//工序检验特性名称列表
                calculateDetailEntity.setProcedurePPM(service.getPPMCalculateByProcedureName(connection,name));//工序PPM
                formList.add(calculateDetailEntity);
            }
            System.out.println("工序列表信息"+formList.toString());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection, null);
        }
        return formList;
    }
    
    
    /**
     * LN
     * 根据起始时间获取产品统计列表
     * @param startStr
     * @param endStr
     * @return
     */
    public  List<PPMCalculateDetailEntity> getProductPPMCalculateDetailList(String startStr, String endStr){
        List<PPMCalculateDetailEntity> formList = new LinkedList<PPMCalculateDetailEntity>();
        try {
            connection = ConnectionUtil.getJdbcConnection();
            log.info("parameterName={ "+ startStr +" "+ endStr +" }");
            PPMCalculateDetailSer service = new PPMCalculateDetailSer();
            List<String> productIdList = service.getProductIdList(connection,startStr,endStr);//获取产品id列表
            System.out.println("controller>>>>procedureNameList size() === " + productIdList.size());
          //去重
            List<String> listNew=new ArrayList<>();
            Set set=new HashSet();
            for (String str:productIdList) {
                if(set.add(str)){
                    listNew.add(str);
                }
            }
            for (String proid: listNew) {
                PPMCalculateDetailEntity calculateDetailEntity = new PPMCalculateDetailEntity();
                ProductSer productSer = new ProductSer();
                ProductEntity productEntity = productSer.getProductById(Integer.parseInt(proid));
                calculateDetailEntity.setProductName(productEntity.getName());//产品名称
                calculateDetailEntity.setProductPPM(service.getPPMCalculateByProductId(connection,Integer.parseInt(proid),startStr,endStr));//获取productppm

                formList.add(calculateDetailEntity);
            }
            System.out.println("产品列表信息"+formList.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection, null);
        }
        return formList;
    }
    
    /**
     * LN
     * 根据起始时间获取型号统计列表
     * @param startStr
     * @param endStr
     * @return
     */
    public  List<PPMCalculateDetailEntity> getXHPPMCalculateDetailList(String startStr, String endStr){
        List<PPMCalculateDetailEntity> formList = new LinkedList<PPMCalculateDetailEntity>();
        try {
            connection = ConnectionUtil.getJdbcConnection();
            log.info("parameterName={ "+ startStr +" "+ endStr +" }");
            PPMCalculateDetailSer service = new PPMCalculateDetailSer();
            List<String> modelList = service.getModelNameList(connection);//获取型号列表
            System.out.println("controller>>>>procedureNameList size() === " + modelList.size());
          /*//去重
            List<String> listNew=new ArrayList<>();
            Set set=new HashSet();
            for (String str:modelList) {
                if(set.add(str)){
                    listNew.add(str);
                }
            }*/
            for (String modelName: modelList) {
                PPMCalculateDetailEntity calculateDetailEntity = new PPMCalculateDetailEntity();
                calculateDetailEntity.setxHName(modelName);//产品名称
                System.out.println(modelName);
                calculateDetailEntity.setxHPPM(service.getPPMCalculateByXH(connection,modelName,startStr,endStr));//获取xhppm
                formList.add(calculateDetailEntity);
            }
            System.out.println("产品列表信息"+formList.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection, null);
        }
        return formList;
    }
    
    
    /**
     * LN
     * 根据起始时间获取模件统计列表
     * @param startStr
     * @param endStr
     * @return
     */
    public  List<PPMCalculateDetailEntity> getMJPPMCalculateDetailList(String startStr, String endStr){
        List<PPMCalculateDetailEntity> formList = new LinkedList<PPMCalculateDetailEntity>();
        try {
            connection = ConnectionUtil.getJdbcConnection();
            log.info("parameterName={ "+ startStr +" "+ endStr +" }");
            PPMCalculateDetailSer service = new PPMCalculateDetailSer();
            List<String> modelList = service.getModelNameList(connection);//获取型号列表
            System.out.println("controller>>>>modelList size() === " + modelList.size());
            System.out.println("modelList:型号名"+modelList.toString());
            
            for(String modelname:modelList) {//遍历型号名集合
            	List<ProductEntity> productList = service.getProListByModelName(connection,modelname);//根据型号名查产品集合
            	//便利产品集合，获取产品id
            	for(ProductEntity proentity:productList) {
            		//根据产品id查询表单数据列表
            		List<FormEntity> formEntityList = service.getFormListByProId(connection, proentity.getId());
            		//便利表单列表拿去有用信息
            		for(FormEntity data:formEntityList) {
            			
            			PPMCalculateDetailEntity calculateDetailEntity = new PPMCalculateDetailEntity();
            			//型号名
            			calculateDetailEntity.setxHName(modelname);
            			//产品名
            			calculateDetailEntity.setProductName(proentity.getName());
            			//模件名
            			calculateDetailEntity.setmJName(data.getModuleName());
            			
            			//获取模件ppm
            			int mjPPM = service.getPPMCalculateBymj(connection, data.getLogo(), startStr, endStr);
            			
            			calculateDetailEntity.setmJPPM(mjPPM);
            			
            			formList.add(calculateDetailEntity);
            			
            		}
            	}
            }
          
            System.out.println("模件列表信息"+formList.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection, null);
        }
        return formList;
    }


    /**
     * 根据时间查询PPM统计数据
     * @param date
     * @return
     */
    public  Map<String,Map<String,Integer>> getPPMData(String date){
        Map<String,Map<String,Integer>> map = new LinkedHashMap<>();
        Map<String,Integer> HB = new LinkedHashMap<>();
        Map<String,Integer> TB = new LinkedHashMap<>();
        boolean isMonth=false;
        if(date.length()>4){
            isMonth=true;
        }
        //key的判断，应该以什么为key
        String prevTimeKey=null;
        String currentTimeKey=null;
        String timeOnLastYearKey=null;
        if(isMonth){
            prevTimeKey="上月";
            currentTimeKey="当月";
            timeOnLastYearKey="去年本月";
        }else{
            prevTimeKey="去年";
            currentTimeKey="当年";
        }
        try {
            connection = ConnectionUtil.getJdbcConnection();
            System.out.println("date.length() == " + date.length());
            if (isMonth){//统计月
                log.info("统计月数据");
                String startStr = date + "-01";
                String endStr = date + "-" + getEndStr(date);
                log.info("parameterName={ 上月为："+ toLastMonth(date)[0] +" "+ toLastMonth(date)[1] +" }");
                log.info("parameterName={ 去年同期为："+ toLastYearMonth(date)[0] +" "+ toLastYearMonth(date)[1] +" }");
                PPMCalculateDetailSer service = new PPMCalculateDetailSer();
                Map<String,Integer> procedureData = service.getPPMData(connection,startStr,endStr);//获取当前月的数据
                Map<String,Integer> lastMonthProcedureData =
                        service.getPPMData(connection,toLastMonth(date)[0],toLastMonth(date)[1]);//获取当前月的上一月数据
                Map<String,Integer> lastYearProcedureData =
                        service.getPPMData(connection,toLastYearMonth(date)[0],toLastYearMonth(date)[1]);//获取当前月的去年同月数据

                List<String> listHB = new LinkedList<>(allProcedure(procedureData,lastMonthProcedureData));
                log.info("listHB 的 size()："+ listHB.size() +" }");
                List<String> listTB = new LinkedList<>(allProcedure(procedureData,lastYearProcedureData));
                log.info("listTB 的 size()："+ listTB.size() +" }");
                for (String name: listHB) {

                    double fenzi = 0.0d;
                    if (procedureData.get(name) != null) {
                        fenzi = procedureData.get(name);
                    }
                    System.out.println(name + " HB fenzi " + fenzi);

                    double fenmu = 0.0d;
                    if (lastMonthProcedureData.get(name) != null) {
                        fenmu = lastMonthProcedureData.get(name);
                    }
                    System.out.println(name + " HB fenmu " + fenmu);
                    if (fenzi == 0.0d || fenmu == 0.0d){
                        HB.put(name, 0);
                    }else {
                        System.out.println("fenzi/fenmu = " + (fenzi/fenmu));
                        HB.put(name, (int) ((fenzi/fenmu)*100 -100));
                    }


                }
                for (String name: listTB) {
                    double fenzi = 0.0d;
                    if (procedureData.get(name) != null) {
                        fenzi = procedureData.get(name);
                    }
                    System.out.println(name + " HB fenzi " + fenzi);

                    double fenmu = 0.0d;
                    if (lastYearProcedureData.get(name) != null) {
                        fenmu = lastYearProcedureData.get(name);
                    }
                    System.out.println(name + " HB fenmu " + fenmu);
                    if (fenzi == 0.0d || fenmu == 0.0d){
                        TB.put(name, 0);
                    }else {
                        System.out.println("fenzi/fenmu = " + (fenzi/fenmu));
                        TB.put(name, (int) ((fenzi/fenmu)*100 -100));
                    }


                }


                map.put(currentTimeKey,procedureData);
                map.put(prevTimeKey,lastMonthProcedureData);
                map.put(timeOnLastYearKey,lastYearProcedureData);
                map.put("环比",HB);
                map.put("同比",TB);
            }else{//统计年
                log.info("统计年数据");
                String startStr = date + "-01-01";
                String endStr = date + "-12-31";
                log.info("parameterName={ 当年为："+ startStr +" "+ endStr +" }");
                log.info("parameterName={ 上年为："+ toLastYear(date)[0] +" "+ toLastYear(date)[1] +" }");
                PPMCalculateDetailSer service = new PPMCalculateDetailSer();
                Map<String,Integer> procedureData = service.getPPMData(connection,startStr,endStr);//获取当年的数据
                Map<String,Integer> lastYearProcedureData =
                        service.getPPMData(connection,toLastYear(date)[0],toLastYear(date)[1]);//获取当年的去年数据
                List<String> listHB =new LinkedList<>(allProcedure(procedureData,lastYearProcedureData)) ;
                log.info("listHB 的 size()："+ listHB.size() +" }");
                for (String name: listHB) {

                    double fenzi = 0.0d;
                    if (procedureData.get(name) != null) {
                        fenzi = procedureData.get(name);
                    }
                    System.out.println(name + " HB fenzi " + fenzi);

                    double fenmu = 0.0d;
                    if (lastYearProcedureData.get(name) != null) {
                        fenmu = lastYearProcedureData.get(name);
                    }
                    System.out.println(name + " HB fenmu " + fenmu);
                    if (fenzi == 0.0d || fenmu == 0.0d){
                        HB.put(name, 0);
                    }else {
                        System.out.println("fenzi/fenmu = " + (fenzi/fenmu));
                        HB.put(name, (int) ((fenzi/fenmu)*100 -100));
                    }
                }
                map.put(currentTimeKey,procedureData);
                map.put(prevTimeKey,lastYearProcedureData);
                map.put("环比",HB);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection, null);
        }
        Set<String>allProcedureName=null;
        //包装一下数据，如果没有值的应该将值设置为0,而不是直接没有这项
        allProcedureName=allProcedure(map.get(currentTimeKey),map.get(prevTimeKey));
        if(map.get(timeOnLastYearKey)!=null){
            for (String procedureName : map.get(timeOnLastYearKey).keySet()) {
                allProcedureName.add(procedureName);
            } 
        }
        for (String procedureName : allProcedureName) {
            if(map.get(currentTimeKey).get(procedureName)==null) map.get(currentTimeKey).put(procedureName,0);
            if(map.get(prevTimeKey).get(procedureName)==null) map.get(prevTimeKey).put(procedureName,0);
            if(map.get(timeOnLastYearKey)!=null && map.get(timeOnLastYearKey).get(procedureName)==null){
                map.get(timeOnLastYearKey).put(procedureName,0);
            }
        }
        return map;
    }


    /**
     * 根据当前月份获取上一月
     * @param date
     * @return
     */
    public String[] toLastMonth(String date){//2019-06  2019-12
        String[] strs = new String[2];

        String month = date.substring(5);//06 12
        int n = Integer.parseInt(month);//6  12
        if (n == 1){
            //                                       2019          -1
            date =  String.valueOf(Integer.parseInt(date.substring(0,4)) - 1) + "-12";//2018-12
        } else if (n<11 && n > 1){
            //        2019-               0             5
            date = date.substring(0,5) + "0" + String.valueOf(n - 1); //2019-05
        }else {
            //       2019-                      11
            date = date.substring(0,5) + String.valueOf(n - 1);//2019-11
        }

        String startStr = date + "-01";
        String endStr = date + "-" +getEndStr(date);

        strs[0] = startStr;
        strs[1] = endStr;
        return strs;
    }

    /**
     * 根据当前月份获取上年这一月
     * @param date
     * @return
     */
    public String[] toLastYearMonth(String date){//2019-06
        String year = date.substring(0,4);//2019 String
        int n = Integer.parseInt(year);//2019 int
        //      2018                   -06
        date = String.valueOf(n - 1) + date.substring(4); //2018-06

        String startStr = date + "-01"; //2018-06-01
        String endStr = date + "-" + getEndStr(date);//2018-06-31
        String[] strs = new String[2];
        strs[0] = startStr;
        strs[1] = endStr;
        return strs;
    }

    /**
     * 根据当前年份获取上年
     * @param date
     * @return
     */
    public String[] toLastYear(String date){//2019
        String year = date.substring(0,4);//2019 String
        int n = Integer.parseInt(year);//2019 int
        //      2018
        date = String.valueOf(n - 1); //2018

        String startStr = date + "-01-01"; //2018-01-01
        String endStr = date + "-12-31";//2018-12-31
        String[] strs = new String[2];
        strs[0] = startStr;
        strs[1] = endStr;
        return strs;
    }


    /**
     * 获取两个map的String合集
     * @param map1
     * @param map2
     * @return
     */
    public Set<String> allProcedure(Map<String,Integer> map1, Map<String,Integer> map2){
        /*List<String> procedureList = new LinkedList<>();
        Set<String> set = map1.keySet();
        for (String name:set) {
            procedureList.add(name);
        }

        Set<String> set2 = map2.keySet();
        for (String name:set2) {
            procedureList.add(name);
        }

        List<String> listNew = new ArrayList<>();
        Set set3=new HashSet();
        for (String str:procedureList) {
            if(set3.add(str)){
                listNew.add(str);
            }
        }*/
        //对不起，上面这过程，我是在看不下去了！
        Set<String> set=new HashSet<>();
        for (String key: map1.keySet()) {
            set.add(key);
        }
        for (String key: map2.keySet()) {
            set.add(key);
        }
        return set;
    }

    /**
     * 获取当前月的最后一天
     * @param date
     * @return
     */
    public String getEndStr(String date){
        int year = Integer.parseInt(date.substring(0,4));
        int month = Integer.parseInt(date.substring(5));
        Calendar c = Calendar.getInstance();
        c.set(year,month,0);
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        return String.valueOf(dayOfMonth);
    }
}

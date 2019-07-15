package ext.modular.calculate;

import com.google.gson.Gson;
import ext.modular.common.ConnectionUtil;
import ext.modular.common.ResultUtils;
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
        String actionName = request.getParameter("actionName");
        System.out.println("actionName = " + actionName);
        String date = request.getParameter("dateTime");
        System.out.println("echars 统计中的 dataTime = " + date);
        if ("getForm".equals(actionName)){


            if (!"".equals(startStr) && !"".equals(endStr)) {

            List<PPMCalculateDetailEntity> formList = getPPMCalculateDetailList(startStr,endStr);


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

        try {
            connection = ConnectionUtil.getJdbcConnection();
            System.out.println("date.length() == " + date.length());
            if (date.length()>4){//统计月
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

                List<String> listHB = allProcedure(procedureData,lastMonthProcedureData);
                log.info("listHB 的 size()："+ listHB.size() +" }");
                List<String> listTB = allProcedure(procedureData,lastYearProcedureData);
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


                map.put("当月",procedureData);
                map.put("上月",lastMonthProcedureData);
                map.put("去年本月",lastYearProcedureData);
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
                List<String> listHB = allProcedure(procedureData,lastYearProcedureData);
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
                map.put("当年",procedureData);
                map.put("去年",lastYearProcedureData);
                map.put("环比",HB);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtil.close(connection, null);
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
    public List<String> allProcedure(Map<String,Integer> map1, Map<String,Integer> map2){
        List<String> procedureList = new LinkedList<>();
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
        }
        return listNew;
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

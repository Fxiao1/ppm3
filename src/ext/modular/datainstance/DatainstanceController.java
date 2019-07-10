package ext.modular.datainstance;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ext.modular.common.ConnectionUtil;
import ext.modular.common.DataPack;
import ext.modular.common.ResultUtils;
import ext.modular.form.FormSer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * des:
 *  表单数据实例的controller层
 * @author renkai
 * @date 2019/6/18
 */
@Controller
public class DatainstanceController {
    Gson gson=new Gson();
    private Connection conn=null;
    private final Logger log= LoggerFactory.getLogger(this.getClass());

    public DatainstanceController() {
    }
    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST,RequestMethod.HEAD })
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ParseException, SQLException {
        conn= ConnectionUtil.getConnection();
        String jsonStr="";
        String actionName=request.getParameter("actionName");
        log.info("actionName={}",actionName);
        DatainstanceSer ser=new DatainstanceSer();
        //根据表单logo获取表单实例列表
        if("get".equals(actionName)){
            log.info("正在执行ext.modular.datainstance.DatainstanceController.processRequest方法,进入到了“get”分支");
            int productId=strToInt(request.getParameter("productId"));
            if(productId==0){
                jsonStr=ResultUtils.error("未获取到产品id");
            }else{
                List list=ser.getListByProductId(productId,conn);
                log.info("查询到的数据实例条数为：{}条",list.size());
                jsonStr=ResultUtils.succ(list);
            }

        }
        //根据表单标识
        else if("getByFormMark".equals(actionName)){
            int logo=strToInt(request.getParameter("logo"));
            if(logo==0){
                jsonStr=ResultUtils.error("未获取到表单标识");
            }else{
                List list=ser.getListByLogo(logo,conn);
                log.info("查询到的数据实例条数为：{}条",list.size());
                jsonStr=ResultUtils.succ(list);
            }
        }
        //检验明细录入保存
        else if("add".equals(actionName)){
            String dataInstanceListJsonstr=request.getParameter("dataInstanceList");
            log.info("正在进行保存表单实例操作");
            List<DatainstanceEntity> formEntityList = gson.fromJson(dataInstanceListJsonstr,new TypeToken<List<DatainstanceEntity>>() {}.getType());
            if(formEntityList!=null&&formEntityList.size()>0){
                FormSer formSer=new FormSer();
                int dataMark=formSer.getLogo();
                for (DatainstanceEntity datainstance : formEntityList) {
                    ser.add(datainstance,conn,dataMark);
                }
            }
            jsonStr = ResultUtils.succ(null,"保存成功");
        }
        //计算表单实体的其他信息，然后返回
        else if("calculation".equals(actionName)){
            DataPack<List<DatainstanceEntity>> datapack=getAllDataInstanceForRequest(request);
            if(datapack.isSuccess()){
                jsonStr=ResultUtils.succ(datapack.getData());
            }else{
                jsonStr=ResultUtils.error(datapack.getMessage());
            }
        }else if("update".equals(actionName)){
            String dataInstanceListJsonstr=request.getParameter("dataInstanceList");
            log.info("正在进行修改表单实例操作");
            List<DatainstanceEntity> formEntityList = gson.fromJson(dataInstanceListJsonstr,new TypeToken<List<DatainstanceEntity>>() {}.getType());
            if(formEntityList!=null&&formEntityList.size()>0){
                for (DatainstanceEntity datainstance : formEntityList) {
                    ser.updateDataInstance(datainstance,conn);
                }
            }
            jsonStr = ResultUtils.succ(null,"保存成功");

        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store,no-cache");
        response.setHeader("Pragma", "no-cache");
        ConnectionUtil.close(conn,null);
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

    private int strToInt(String str){
        if(StringUtils.isEmpty(str)){
            return 0;
        }else{
            return Integer.parseInt(str);
        }
    }
    private Date strToDate(String str) throws ParseException {
        if(StringUtils.isEmpty(str)){
            return new Date();
        }else{
            SimpleDateFormat dataformat=new SimpleDateFormat("yyyy-MM-dd hh:mm");
            return dataformat.parse(str.replace("T"," "));
        }
    }
    /**
     * 从请求中获取参数封装成表单数据实体的list集合
     * @Author Fxiao
     * @Description
     * @Date 19:52 2019/6/27
     * @param request
     * @return ext.modular.common.DataPack<java.util.List<ext.modular.datainstance.DatainstanceEntity>>
     **/
    private DataPack<List<DatainstanceEntity>> getAllDataInstanceForRequest(HttpServletRequest request){
        /*String currentProcedureName;
        String []procedureNames=request.getParameterValues("procedureName");
        if(procedureNames==null){
            log.info("procedureNames==null");
            return ResultUtils.packData(null,"procedureNames==null",false);
        }
        log.info("procedureNames.length="+procedureNames.length);
        int num=1;
        int indexNum=0;

        //所有的条目的容器
        List<DatainstanceEntity> AllDataInstance=new LinkedList<>();
        //循环工序

        for (int i = 0; i < procedureNames.length; i++) {
            currentProcedureName=procedureNames[i];
            String []defectNumbers=request.getParameterValues(currentProcedureName+"_defectNumber");
            String []kjs=request.getParameterValues(currentProcedureName+"_kj");
            String []dataItemIds=request.getParameterValues("dataItemIds");
            int logo=strToInt(request.getParameter("logo"));
            boolean isUp=dataItemIds!=null;
            log.info("defectNumbers.length={},defectNumbers=“{}”",defectNumbers.length,gson.toJson(defectNumbers));
            //循环当前工序下的条目
            for (int j = 0; j < defectNumbers.length; j++) {
                log.info("正在尝试获取第{}条数据",num++);
                DatainstanceEntity datainstance=new DatainstanceEntity();
                String productCountStr=request.getParameter(currentProcedureName+".productCount");
                if(isUp){
                    datainstance.setId(strToInt(dataItemIds[indexNum++]));
                }
                datainstance.setProductCount(strToInt(productCountStr));
                datainstance.setKj(strToInt(kjs[j]));
                String defectNumberStr=defectNumbers[j];
                int defectNumber=strToInt(defectNumberStr);
                datainstance.setDefectNumber(defectNumber);
                datainstance.setLogo(logo);
                datainstance.setCheckType(request.getParameter(currentProcedureName+".checkType"));
                datainstance.setCheckPerson(request.getParameter(currentProcedureName+".checkPerson"));
                try {
                    datainstance.setCheckTime(strToDate(request.getParameter(currentProcedureName+".checkTime")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //检验特性数量
                String characQuantitystr=request.getParameter(currentProcedureName+".characQuantity");
                int characQuantity=strToInt(characQuantitystr);
                datainstance.setCharacQuantity(characQuantity);
                String productIdStr=request.getParameter("productId");
                datainstance.setProductId(strToInt(productIdStr));
                datainstance.setCharacName(request.getParameter(currentProcedureName+".characName"));
                datainstance.setProcedureName(currentProcedureName);
                //工序检验特性总数
                datainstance.setCharacteristicsTotal(datainstance.getProductCount()*datainstance.getCharacQuantity());
                //工序检验特性检出的缺陷总数对应的每条特性的值
                datainstance.setDefectNumberItem(
                        datainstance.getProductCount()
                                *datainstance.getKj()
                                *datainstance.getDefectNumber()
                );

                if(datainstance.getCharacQuantity()==0) {
                    //避免下一步计算的除数为0，直接退出循环。本次数据肯定有误
                    log.error("正在执行ext.modular.datainstance.DatainstanceController方法，" +
                                    "即将发生By zero错误。datainstance.getCharacQuantity()的数字为0，而除数不能为0，本次接收的key为{}，接收到的值为{}",
                            currentProcedureName+".characQuantity",characQuantitystr
                    );
                    return ResultUtils.packData(null,"数据错误",false);
                }
                //特性的ppm值
                Integer a=datainstance.getDefectNumber();
                Integer b=datainstance.getCharacQuantity();
                Integer c=1000000;
                BigDecimal bi1=new BigDecimal(a.toString());
                BigDecimal bi2=new BigDecimal(b.toString());
                BigDecimal bi3=new BigDecimal(c.toString());
                BigDecimal divide=bi1.divide(bi2,6, RoundingMode.HALF_UP);
                BigDecimal multiply=divide.multiply(bi3);
                datainstance.setCharacPPM(multiply.intValue());
                log.info("正在计算第{}条特性的ppm值，参数为：defectNumber={}、characQuantity={}；结果为characPPM={}"
                        ,num,datainstance.getDefectNumber(),datainstance.getCharacQuantity(),datainstance.getCharacPPM());
                AllDataInstance.add(datainstance);
            }
        }
        return ResultUtils.packData(AllDataInstance,"",true);*/

        int dataRowNum=strToInt(request.getParameter("maxRowNumber"));
        int quantity=strToInt(request.getParameter("quantity"));
        //产品数
        int productCount=0;
        String checkType=null,checkPerson=null,oldProcedureIdStr=null,newProcedureIdStr;
        Date checkTime=null;
        //所有的条目的容器
        List<DatainstanceEntity> AllDataInstance=new LinkedList<>();
        //当前工序下的数据
        List<DatainstanceEntity>currentProcedure=new LinkedList<>();
        boolean isNewProcedure=false;
        for (int i = 0; i < dataRowNum + 1; i++) {
            DatainstanceEntity ins=new DatainstanceEntity();
            newProcedureIdStr=request.getParameter("procedureId_"+i);
            ins.setProcedureName(request.getParameter("procedureName_"+i));
            //本行数据有工序id，且当前行与旧行数据的id不同，则说明到了新的工序行了。如果是新工序了，则更新产品数、检验类型、检验人、检验时间这几个变量
            if(newProcedureIdStr!=null&&(!newProcedureIdStr.equals(oldProcedureIdStr))){
                oldProcedureIdStr=newProcedureIdStr;
                productCount=strToInt(request.getParameter("productCount_"+i));
                checkType=request.getParameter("checkType_"+i);
                checkPerson=request.getParameter("checkPerson_"+i);
                try {
                    checkTime=strToDate(request.getParameter("checkTime_"+i));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                isNewProcedure=true;
            }else{
                isNewProcedure=false;
            }
            ins.setProductCount(productCount);
            String characQuantityStr=request.getParameter("characQuantity_"+i);
            ins.setCharacQuantity(strToInt(characQuantityStr));

            ins.setCharacName(request.getParameter("characName_"+i));
            ins.setDefectNumber(strToInt(request.getParameter("defectNumber_"+i)));
            ins.setCheckType(checkType);
            ins.setCheckPerson(checkPerson);
            ins.setCheckTime(checkTime);
            ins.setKj(strToInt(request.getParameter("kj_"+i)));
            ins.setLogo(strToInt(request.getParameter("logo")));
            ins.setProductId(strToInt(request.getParameter("productId")));
            log.info("接收到的logo={},接收到的产品id={}。",request.getParameter("logo")
                ,request.getParameter("productId"));
            Integer id=strToInt(request.getParameter("dataItemIds_"+i));
            if(id!=null){
                ins.setId(id);
            }
            //工序检验特性总数
            ins.setCharacteristicsTotal(ins.getProductCount()*ins.getCharacQuantity());
            //工序检验特性检出的缺陷总数对应的每条特性的值
            ins.setDefectNumberItem(ins.getDefectNumber()*ins.getKj());

            if(ins.getCharacQuantity()==0) {
                //避免下一步计算的除数为0，直接退出循环。本次数据肯定有误
                log.error("正在执行ext.modular.datainstance.DatainstanceController方法，" +
                                "即将发生By zero错误。datainstance.getCharacQuantity()的数字为0，而除数不能为0，本次接收的key为{}，接收到的值为{}",
                        "characQuantity_"+i,characQuantityStr
                );
                return ResultUtils.packData(null,"数据错误，详细错误请查看日志",false);
            }
            //特性的ppm值
            Integer a=ins.getDefectNumber();
            Integer b=ins.getCharacQuantity();
            Integer c=1000000;
            BigDecimal bi1=new BigDecimal(a.toString());
            BigDecimal bi2=new BigDecimal(b.toString());
            BigDecimal bi3=new BigDecimal(c.toString());
            BigDecimal divide=bi1.divide(bi2,6, RoundingMode.HALF_UP);
            BigDecimal multiply=divide.multiply(bi3);
            ins.setCharacPPM(multiply.intValue());
            log.info("正在计算第{}条特性的ppm值，参数为：defectNumber={}、characQuantity={}；结果为characPPM={}"
                    ,i+1,ins.getDefectNumber(),ins.getCharacQuantity(),ins.getCharacPPM());
            ins.setTwId(strToInt(request.getParameter("procedureId_"+i)));
            ins.setQuantity(quantity);
            //这是新工序的数据，且前一个工序下有数据。或者当前是最后一次遍历。则对其进行一些计算和赋值
            log.info("i={},isNewProcedure={},currentProcedure.size={}",i,isNewProcedure,currentProcedure.size());
            if((isNewProcedure&&currentProcedure.size()>0)||i==dataRowNum){
                //如果是最后一次，需要将最后一次的数据添加进去
                if(i==dataRowNum){
                    currentProcedure.add(ins);
                }
                int procedurePpm=0;
                //进行一些赋值
                for (int j = 0; j < currentProcedure.size(); j++) {
                    procedurePpm+=currentProcedure.get(j).getCharacPPM();
                }
                for (int j = 0; j < currentProcedure.size(); j++) {
                    currentProcedure.get(j).setProcedurePpm(procedurePpm);
                }
                log.info("AllDataInstance.size={},currentProcedure.size={},i={}",
                        AllDataInstance.size(),
                        currentProcedure.size(),
                        i
                );
                AllDataInstance.addAll(currentProcedure);
                currentProcedure.clear();
            }
            log.info("正在添加实例,i={}，添加前currentProcedure.size={},当前实例={}",i,currentProcedure.size(),ins.toString());
            currentProcedure.add(ins);

        }
        return ResultUtils.packData(AllDataInstance,"",true);
    }

}
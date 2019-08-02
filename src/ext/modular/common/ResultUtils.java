package ext.modular.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * des:
 *  用作返回数据的类
 * @author fxiao
 * @date 2019/6/11 18:11
 */
public class ResultUtils {
    private static Gson gson=new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    public static<T> String succ(T t){
        DataPack<T> dataPack=new DataPack<>();
        dataPack.setData(t);
        dataPack.setSuccess(true);
        return gson.toJson(dataPack);
    }
    public static<T> String succ(T t,String message){
        DataPack<T> dataPack=new DataPack<>();
        dataPack.setData(t);
        dataPack.setMessage(message);
        dataPack.setSuccess(true);
        return gson.toJson(dataPack);
    }
    public static String error(String message){
        DataPack dataPack=new DataPack<>();
        dataPack.setSuccess(false);
        dataPack.setMessage(message);
        return gson.toJson(dataPack);
    }
    public static <T> String error(T t,String message){
        DataPack dataPack=new DataPack<>();
        dataPack.setSuccess(false);
        dataPack.setMessage(message);
        dataPack.setData(t);
        return gson.toJson(dataPack);
    }
    /**
     * 打包数据
     * @Author Fxiao
     * @Description
     * @Date 19:51 2019/6/27
     * @param data
     * @param message
     * @param isSucc
     * @return ext.modular.common.DataPack<T>
     **/
    public static <T> DataPack<T> packData(T data,String message,boolean isSucc){
        DataPack<T> dataPack=new DataPack<>();
        dataPack.setData(data);
        dataPack.setMessage(message);
        dataPack.setSuccess(isSucc);
        return dataPack;
    }
}

package ext.modular.common;

/**
 * des:
 *   数据包
 * @author fxiao
 * @date 2019/6/11 18:11
 */
public class  DataPack<T> {
    private String message;
    private boolean success;
    private T data;

    protected DataPack() {
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    protected void setMessage(String message) {
        this.message = message;
    }

    protected void setSuccess(boolean success) {
        this.success = success;
    }

    protected void setData(T data) {
        this.data = data;
    }
}

package ext.modular.product;

import ext.modular.common.BasicEntity;

public class ProductEntity extends BasicEntity {
    //产品名称
    private String name;
    //
    private String model_id;//型号id
    //产品代号
    private String product_code;
    private String model_type; //型号名
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel_id() {
        return model_id;
    }

    public void setModel_id(String model_id) {
        this.model_id = model_id;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getModel_type() {
        return model_type;
    }

    public void setModel_type(String model_type) {
        this.model_type = model_type;
    }

}


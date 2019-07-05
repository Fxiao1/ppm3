package ext.modular.model;

import ext.modular.common.BasicEntity;
import ext.modular.product.ProductEntity;

import java.util.List;

public class ModelEntity extends BasicEntity {
    private String numberCode;
    private String name;
    private String model_code;
    private List<ProductEntity> productEntityList;

    public String getNumberCode() {
        return numberCode;
    }

    public void setNumberCode(String numberCode) {
        this.numberCode = numberCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel_code() {
        return model_code;
    }

    public void setModel_code(String model_code) {
        this.model_code = model_code;
    }

    public List<ProductEntity> getProductEntityList() {
        return productEntityList;
    }

    public void setProductEntityList(List<ProductEntity> productEntityList) {
        this.productEntityList = productEntityList;
    }

    @Override
    public String toString() {
        return "ModelEntity{" +
                "numberCode='" + numberCode + '\'' +
                ", name='" + name + '\'' +
                ", model_code='" + model_code + '\'' +
                ", productEntityList=" + productEntityList +
                '}';
    }
}

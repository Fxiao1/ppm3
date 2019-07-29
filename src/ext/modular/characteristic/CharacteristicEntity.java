package ext.modular.characteristic;

import ext.modular.common.BasicEntity;

/**
 * des:
 *  工艺检验特性实体类
 * @author fxiao
 * @date 2019/6/4 14:22
 */
public class CharacteristicEntity extends BasicEntity {

    private String name;
    //检验特性数量
    private int total;
    //严酷度加权系数
    private int coefficient;
    private String checkType;                         //检验类型
	private int ppmOrder;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(int coefficient) {
        this.coefficient = coefficient;
    }

    public int getPpmOrder() {
        return ppmOrder;
    }

    public void setPpmOrder(int ppmOrder) {
        this.ppmOrder = ppmOrder;
    }
    
    public String getCheckType() {
		return checkType;
	}

	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}


}


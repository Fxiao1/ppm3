package ext.modular.form;

import ext.modular.common.BasicEntity;

public class FormEntity extends BasicEntity{
	
	private String name; 
	private int productId;                            //产品ID
	private int characId;	                          //检验特性id(已废除)
	private int twId; 		                          //工序ID
	private int ppmOrder;                             //排序数字
	private int logo;                                 //表单标识
	private String batch;                             //生产批次
	private int quantity;                             //生产数量
    private String category;                          //类别
	private String moduleName;                        //模件名称
	private String procedureName;                     //工序名称
	private String characName;                       //工序检验特性名称
	private int characQuantity;                       //检验特性数量
	private int kj;                                   //严酷度加权系数
	private String  ProductPhase ;					  //产品阶段  ln
	 private String checkType;                         //检验类型
	 private String templateName;						//模板名称  ln
	 private String templateId;						//模板id  ln
	 
	
	

    public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getCheckType() {
		return checkType;
	}

	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}

	public String getProductPhase() {
		return ProductPhase;
	}

	public void setProductPhase(String productPhase) {
		ProductPhase = productPhase;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCharacId() {
        return characId;
    }

    public void setCharacId(int characId) {
        this.characId = characId;
    }

    public int getTwId() {
        return twId;
    }

    public void setTwId(int twId) {
        this.twId = twId;
    }

    public int getPpmOrder() {
        return ppmOrder;
    }

    public void setPpmOrder(int ppmOrder) {
        this.ppmOrder = ppmOrder;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public String getCharacName() {
        return characName;
    }

    public void setCharacName(String characName) {
        this.characName = characName;
    }

    public int getCharacQuantity() {
        return characQuantity;
    }

    public void setCharacQuantity(int characQuantity) {
        this.characQuantity = characQuantity;
    }

    public int getKj() {
        return kj;
    }

    public void setKj(int kj) {
        this.kj = kj;
    }

	@Override
	public String toString() {
		return "FormEntity [name=" + name + ", productId=" + productId + ", characId=" + characId + ", twId=" + twId
				+ ", ppmOrder=" + ppmOrder + ", logo=" + logo + ", batch=" + batch + ", quantity=" + quantity
				+ ", category=" + category + ", moduleName=" + moduleName + ", procedureName=" + procedureName
				+ ", characName=" + characName + ", characQuantity=" + characQuantity + ", kj=" + kj + ", ProductPhase="
				+ ProductPhase + ", checkType=" + checkType + ", templateName=" + templateName + ", templateId="
				+ templateId + "]";
	}






	
}

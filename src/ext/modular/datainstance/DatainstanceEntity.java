package ext.modular.datainstance;

import ext.modular.common.BasicEntity;

import java.util.Date;

public class DatainstanceEntity extends BasicEntity {

    private int productId;                            //产品ID
    private int charaId;	                          //检验特性id
    private int twId; 		                          //工序ID
    private int ppmOrder;                             //排序数字
    private int logo;                                 //表单标识
    private int dataInsMark;                          //数据实例标识
    private String batch;                             //生产批次
    private int quantity;                             //生产数量
    private String category;                          //类别
    private String moduleName;                        //模件名称
    private String procedureName;                     //工序名称
    private int defectNumber;                         //工序检验特性缺陷数
    private int defectNumberItem;                    //工序检验特性检出的缺陷总数对应的每条特性的值,该工序下的总数就是将其下的条目加起来
    private String characName;                        //工序检验特性名称
    private int characQuantity;                       //检验特性数量
    private int kj;                                   //严酷度加权系数
    private String checkType;                         //检验类型
    private String  ProductPhase ;					  //产品阶段  ln
    private String templateName;                      //模板名   ln
    private String templateId;						//模板id  ln
    private String checkPerson;                       //检验人
    private String checkPersonId;                     //检验人ID
    private Date checkTime;                           //检验时间
    private int productCount;                         //产品数
    private int characPPM;                            //工序检验特性PPM
    private int procedurePpm;                          //工序ppm
    private int characteristicsTotal;                 //工序检验特性总数
    
    
    


    public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getProductPhase() {
		return ProductPhase;
	}

	public void setProductPhase(String productPhase) {
		ProductPhase = productPhase;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public int getDataInsMark() {
        return dataInsMark;
    }

    public void setDataInsMark(int dataInsMark) {
        this.dataInsMark = dataInsMark;
    }

    public int getProcedurePpm() {
        return procedurePpm;
    }

    public void setProcedurePpm(int procedurePpm) {
        this.procedurePpm = procedurePpm;
    }

    public int getDefectNumberItem() {
        return defectNumberItem;
    }

    public void setDefectNumberItem(int defectNumberItem) {
        this.defectNumberItem = defectNumberItem;
    }

    public int getCharacteristicsTotal() {
        return characteristicsTotal;
    }

    public void setCharacteristicsTotal(int characteristicsTotal) {
        this.characteristicsTotal = characteristicsTotal;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCharaId() {
        return charaId;
    }

    public void setCharaId(int charaId) {
        this.charaId = charaId;
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

    public int getDefectNumber() {
        return defectNumber;
    }

    public void setDefectNumber(int defectNumber) {
        this.defectNumber = defectNumber;
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

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getCheckPerson() {
        return checkPerson;
    }

    public void setCheckPerson(String checkPerson) {
        this.checkPerson = checkPerson;
    }

    public String getCheckPersonId() {
        return checkPersonId;
    }

    public void setCheckPersonId(String checkPersonId) {
        this.checkPersonId = checkPersonId;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public int getCharacPPM() {
        return characPPM;
    }

    public void setCharacPPM(int characPPM) {
        this.characPPM = characPPM;
    }

	@Override
	public String toString() {
		return "DatainstanceEntity [productId=" + productId + ", charaId=" + charaId + ", twId=" + twId + ", ppmOrder="
				+ ppmOrder + ", logo=" + logo + ", dataInsMark=" + dataInsMark + ", batch=" + batch + ", quantity="
				+ quantity + ", category=" + category + ", moduleName=" + moduleName + ", procedureName="
				+ procedureName + ", defectNumber=" + defectNumber + ", defectNumberItem=" + defectNumberItem
				+ ", characName=" + characName + ", characQuantity=" + characQuantity + ", kj=" + kj + ", checkType="
				+ checkType + ", ProductPhase=" + ProductPhase + ", templateName=" + templateName + ", templateId="
				+ templateId + ", checkPerson=" + checkPerson + ", checkPersonId=" + checkPersonId + ", checkTime="
				+ checkTime + ", productCount=" + productCount + ", characPPM=" + characPPM + ", procedurePpm="
				+ procedurePpm + ", characteristicsTotal=" + characteristicsTotal + "]";
	}


    
}

package ext.modular.calculate;

import ext.modular.common.BasicEntity;
import ext.modular.form.FormEntity;

import java.util.List;

public class PPMCalculateDetailEntity extends BasicEntity {

    private String procedureName;   //工序名称
    private List<String> characName;  //工序检验特性名称
    private int procedurePPM; //工序PPM
    private int productId; //产品id
    private int productPPM; //产品ppm
    private String productName;//产品名
    private int xHPPM; //型号ppm
    private String xHName;//产品名
    private int mJPPM; //模件ppm
    private String mJName;//模件名


    public int getxHPPM() {
		return xHPPM;
	}

	public void setxHPPM(int xHPPM) {
		this.xHPPM = xHPPM;
	}

	public String getxHName() {
		return xHName;
	}

	public void setxHName(String xHName) {
		this.xHName = xHName;
	}

	public int getmJPPM() {
		return mJPPM;
	}

	public void setmJPPM(int mJPPM) {
		this.mJPPM = mJPPM;
	}

	public String getmJName() {
		return mJName;
	}

	public void setmJName(String mJName) {
		this.mJName = mJName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getProductPPM() {
		return productPPM;
	}

	public void setProductPPM(int productPPM) {
		this.productPPM = productPPM;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public List<String> getCharacName() {
        return characName;
    }

    public void setCharacName(List<String> characName) {
        this.characName = characName;
    }

    public int getProcedurePPM() {
        return procedurePPM;
    }

    public void setProcedurePPM(int procedurePPM) {
        this.procedurePPM = procedurePPM;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

	@Override
	public String toString() {
		return "PPMCalculateDetailEntity [procedureName=" + procedureName + ", characName=" + characName
				+ ", procedurePPM=" + procedurePPM + ", productId=" + productId + ", productPPM=" + productPPM
				+ ", productName=" + productName + ", xHPPM=" + xHPPM + ", xHName=" + xHName + ", mJPPM=" + mJPPM
				+ ", mJName=" + mJName + "]";
	}



}

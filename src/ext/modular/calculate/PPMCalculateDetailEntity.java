package ext.modular.calculate;

import ext.modular.common.BasicEntity;
import ext.modular.form.FormEntity;

import java.util.List;

public class PPMCalculateDetailEntity extends BasicEntity {

    private String procedureName;   //工序名称
    private List<String> characName;  //工序检验特性名称
    private int procedurePPM; //工序PPM


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
        return "PPMCalculateDetailEntity{" +
                "procedureName='" + procedureName + '\'' +
                ", characName=" + characName +
                ", procedurePPM=" + procedurePPM +
                '}';
    }
}

package ext.modular.templatelink;

import ext.modular.common.BasicEntity;
import ext.modular.procedure.ProcedureEntity;
import ext.modular.template.TemplateEntity;

public class TemplatelinkEntity extends BasicEntity {
    private TemplateEntity templateEntity;
    private ProcedureEntity procedureEntity;
    private int ppm_order;
  

	public TemplateEntity getTemplateEntity() {
        return templateEntity;
    }

    public void setTemplateEntity(TemplateEntity templateEntity) {
        this.templateEntity = templateEntity;
    }

    public ProcedureEntity getProcedureEntity() {
        return procedureEntity;
    }

    public void setProcedureEntity(ProcedureEntity procedureEntity) {
        this.procedureEntity = procedureEntity;
    }

    public int getPpm_order() {
        return ppm_order;
    }

    public void setPpm_order(int ppm_order) {
        this.ppm_order = ppm_order;
    }
}

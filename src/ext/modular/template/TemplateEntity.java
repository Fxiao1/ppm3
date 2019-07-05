package ext.modular.template;

import ext.modular.common.BasicEntity;
import ext.modular.procedure.ProcedureEntity;

/**
 * des:
 *
 * @author fxiao
 * @date 2019/6/4 14:14
 */

public class TemplateEntity extends BasicEntity {
    private String name;
    private ProcedureEntity procedure;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProcedureEntity getProcedure() {
        return procedure;
    }

    public void setProcedure(ProcedureEntity procedure) {
        this.procedure = procedure;
    }

}

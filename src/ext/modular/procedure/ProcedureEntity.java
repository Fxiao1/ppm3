package ext.modular.procedure;

import ext.modular.characteristic.CharacteristicEntity;
import ext.modular.common.BasicEntity;

import java.util.List;

/**
 * des:
 *
 * @author fxiao
 * @date 2019/6/4 14:25
 */
public class ProcedureEntity extends BasicEntity {
    //检验特性
    private List<CharacteristicEntity> characList;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CharacteristicEntity> getCharacList() {
        return characList;
    }

    public void setCharacList(List<CharacteristicEntity> characList) {
        this.characList = characList;
    }
}

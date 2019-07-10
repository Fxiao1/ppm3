package ext.modular.procedurelink;

import ext.modular.characteristic.CharacteristicEntity;
import ext.modular.common.BasicEntity;
import ext.modular.templatelink.TemplatelinkEntity;

/**
 * des:特性工序关系
 *
 * @author ln
 * @date 2019年7月10日16:13:42
 */
public class ProcedureLinkEntity extends BasicEntity{
	private TemplatelinkEntity templatelink;
	private CharacteristicEntity character;
	private int ppm_order;
	
	
	public TemplatelinkEntity getTemplatelink() {
		return templatelink;
	}
	public void setTemplatelink(TemplatelinkEntity templatelink) {
		this.templatelink = templatelink;
	}
	public CharacteristicEntity getCharacter() {
		return character;
	}
	public void setCharacter(CharacteristicEntity character) {
		this.character = character;
	}
	public int getPpm_order() {
		return ppm_order;
	}
	public void setPpm_order(int ppm_order) {
		this.ppm_order = ppm_order;
	}
	
	

}

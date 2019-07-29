package ext.modular.util;

import ext.modular.procedure.ProcedureEntity;

import java.util.ArrayList;
import java.util.List;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<ProcedureEntity> lists= new ArrayList();
		ProcedureEntity pe2 = new ProcedureEntity();
		pe2.setId(123);
		ProcedureEntity pe1 = new ProcedureEntity();
		pe1.setId(1234);
		lists.add(pe2);
		lists.add(pe1);
		  List peIds = new ArrayList();
		    for(ProcedureEntity pe:lists){
		    	peIds.add(pe.getId()+"");
		    }
		    String[] procedureIds = new String[peIds.size()];
		    //peIds.toArray(procedureIds);
		    for(int i=0;i<peIds.size();i++){
		    	procedureIds[i] = (String) peIds.get(i);
		    }
		    
		    System.out.println(procedureIds);
		
	}

}

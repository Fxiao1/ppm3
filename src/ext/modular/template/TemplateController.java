package ext.modular.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * des:
 *
 * @author fxiao
 * @date 2019/6/4 12:13
 */
@Controller
@RequestMapping("template")
public class TemplateController {

    @Autowired
    private TemplateSer templateSer;
    @RequestMapping(value = "templatehome",method = RequestMethod.GET)
    public String page(){
        return "template/template";
    }
    @ResponseBody
    @RequestMapping(value = "templates",method = RequestMethod.GET)
    public List<TemplateEntity> List(){
        List<TemplateEntity> list = templateSer.getModelList();
        return list;
    }
    @ResponseBody
    @RequestMapping(value = "templates",method = RequestMethod.POST)
    public void addOrUpdate(TemplateEntity templateEntity){
        if (templateEntity.getId()==0) {
            templateSer.add(templateEntity);
        }else{
            templateSer.update(templateEntity);
        }
    }
    @ResponseBody
    @RequestMapping(value = "templates/{id}",method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") int id){
        templateSer.delete(id);
    }
}

package fr.triedge.uchuu.api;

import fr.triedge.uchuu.utils.Vars;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    @GetMapping(Vars.VIEW_HOME)
    public ModelAndView home(){
        ModelAndView model = new ModelAndView("home.html");

        return model;
    }
}

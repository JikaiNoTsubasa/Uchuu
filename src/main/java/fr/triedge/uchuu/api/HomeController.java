package fr.triedge.uchuu.api;

import fr.triedge.uchuu.model.Model;
import fr.triedge.uchuu.model.Quest;
import fr.triedge.uchuu.model.User;
import fr.triedge.uchuu.utils.Vars;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class HomeController extends AbstractController{

    @GetMapping(Vars.VIEW_HOME)
    public ModelAndView home(){
        ModelAndView model = new ModelAndView("home.html");

        ArrayList<Quest> allQuests = Model.getInstance().getQuests();
        User user = (User) getSession().getAttribute(Vars.USER);
        List<Quest> quests = allQuests.stream().filter(q -> q.getLevel()>= user.getLevel()).collect(Collectors.toList());
        model.addObject("quests", quests);

        return model;
    }
}

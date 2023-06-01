package fr.triedge.uchuu.api;

import fr.triedge.uchuu.db.DB;
import fr.triedge.uchuu.model.Model;
import fr.triedge.uchuu.model.Quest;
import fr.triedge.uchuu.model.User;
import fr.triedge.uchuu.utils.Vars;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @GetMapping(Vars.VIEW_HOME)
    public ModelAndView home(){
        ModelAndView model = new ModelAndView("home.html");

        ArrayList<Quest> allQuests = Model.getInstance().getQuests();
        User user = (User) getSession().getAttribute("user");
        List<Quest> quests = allQuests.stream().filter(q -> q.getLevel()>= user.getLevel()).collect(Collectors.toList());
        model.addObject("quests", quests);

        return model;
    }

    public HttpSession getSession(){
        return getHttpReq().getSession(true); // true == allow create
    }

    public HttpServletRequest getHttpReq(){
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest();
    }
}

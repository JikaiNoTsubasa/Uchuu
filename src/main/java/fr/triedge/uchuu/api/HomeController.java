package fr.triedge.uchuu.api;

import fr.triedge.uchuu.db.DB;
import fr.triedge.uchuu.model.Model;
import fr.triedge.uchuu.model.Quest;
import fr.triedge.uchuu.model.User;
import fr.triedge.uchuu.utils.Utils;
import fr.triedge.uchuu.utils.Vars;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
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
        // Get latest user
        try {
            user = DB.getInstance().getUser(user.getUsername());
            getSession().setAttribute(Vars.USER, user);
            model.addObject("user", user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        int uLevel = user.getLevel();
        List<Quest> quests = allQuests.stream().filter(q -> q.getLevel()<= uLevel).collect(Collectors.toList());
        model.addObject("quests", quests);

        model.addObject("percentXP", Utils.getNextLevelPercent(user.getXp(), user.getLevel()));
        model.addObject("currentXP", user.getXp());
        model.addObject("nextXP", Utils.getNextLevelXP(user.getLevel()));

        return model;
    }
}

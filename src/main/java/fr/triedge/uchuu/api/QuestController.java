package fr.triedge.uchuu.api;

import fr.triedge.uchuu.db.DB;
import fr.triedge.uchuu.model.Model;
import fr.triedge.uchuu.model.Quest;
import fr.triedge.uchuu.model.User;
import fr.triedge.uchuu.utils.Utils;
import fr.triedge.uchuu.utils.Vars;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;

@Controller
public class QuestController {

    @RequestMapping(path = Vars.VIEW_QUEST, method = {RequestMethod.GET})
    public ModelAndView quest(@RequestParam(value = "id")Integer id){
        ModelAndView model = new ModelAndView("quest.html");
        User user = (User) getSession().getAttribute("user");
        Quest quest = Model.getInstance().getQuest(id);
        if (quest != null){
            if (conditionMet(quest)){
                model.addObject("quest", quest);
                try {
                    int res = DB.getInstance().isUserInQuest(user, id);
                    if (res == 0){
                        model.addObject("noquest", true);
                    }else if(res > 0){
                        model.addObject("currentQuest", true);
                    }else{
                        model.addObject("anotherQuest", true);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }else{
                model.addObject("error", "Votre niveau est insufisant.");
            }
        }else{
            model.addObject("error", "Quête non trouvée.");
        }

        return model;
    }

    @RequestMapping(path = Vars.QUEST_START, method = {RequestMethod.GET})
    public ModelAndView startQuest(@RequestParam(value = "id")Integer id){
        Quest quest = Model.getInstance().getQuest(id);
        User user = (User) getSession().getAttribute("user");
        if (Utils.isValid(id) && conditionMet(quest)){
            try {
                boolean started = DB.getInstance().startQuestForUser(user.getId(), id);
                if (!started){
                    return new ModelAndView("quest.html").addObject("error", "La quête ne peu pas être lancée!");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else{
            return new ModelAndView("quest.html").addObject("error", "Conditions non remplies pour démarrer la quête!");
        }
        return new ModelAndView("redirect:quest");
    }

    private boolean conditionMet(Quest quest){
        if (quest == null)
            return false;
        User user = (User) getSession().getAttribute("user");
        try {
            User u = DB.getInstance().getUser(user.getUsername());
            if (u.getLevel() >= quest.getLevel())
                return true;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public HttpSession getSession(){
        return getHttpReq().getSession(true); // true == allow create
    }

    public HttpServletRequest getHttpReq(){
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest();
    }
}

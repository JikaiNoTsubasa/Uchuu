package fr.triedge.uchuu.api;

import fr.triedge.uchuu.db.DB;
import fr.triedge.uchuu.model.AdminUserEntry;
import fr.triedge.uchuu.model.Building;
import fr.triedge.uchuu.model.User;
import fr.triedge.uchuu.utils.Vars;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.util.ArrayList;

@RestController
public class AdminController extends AbstractController{

    @GetMapping(Vars.VIEW_ADMIN)
    public ModelAndView admin(){
        ModelAndView model = new ModelAndView("admin.html");
        User user = getUser();
        if (!user.isAdmin()){
            return new ModelAndView("redirect:home");
        }

        try{
            ArrayList<AdminUserEntry> users = DB.getInstance().adminGetUsers();
            model.addObject("users", users);

            ArrayList<Building> buildings = DB.getInstance().getBuildings();
            model.addObject("buildings", buildings);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return model;
    }

    @GetMapping(Vars.AJAX_ADMIN_FINISH_QUEST)
    public ResponseEntity<?> ajaxFinishQuest(@RequestParam(value = "userid")int userId,
                                             @RequestParam(value = "questid")int questId){
        User user = getUser();
        if (user.isAdmin()){
            try {
                DB.getInstance().adminFinishQuest(userId, questId);
                return ResponseEntity.ok("Updated!");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return ResponseEntity.ok("No update");
    }
}

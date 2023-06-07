package fr.triedge.uchuu.api;

import fr.triedge.uchuu.db.DB;
import fr.triedge.uchuu.model.Building;
import fr.triedge.uchuu.model.User;
import fr.triedge.uchuu.utils.Vars;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.util.ArrayList;

@RestController
public class BuildingController extends AbstractController{

    @GetMapping(Vars.VIEW_BUILDING)
    public ModelAndView building(){
        ModelAndView model = new ModelAndView("building.html");
        User user = getUser();

        if (user.getLevel() >= 5){
            try{
                ArrayList<Building> buildings = DB.getInstance().getUserAvailableBuildings(user);
                model.addObject("buildings", buildings);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else{
            model.addObject("error", "Vous n'avez pas le niveau 5.");
        }

        return model;
    }
}

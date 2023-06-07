package fr.triedge.uchuu.api;

import fr.triedge.uchuu.db.DB;
import fr.triedge.uchuu.model.Building;
import fr.triedge.uchuu.model.User;
import fr.triedge.uchuu.model.UserBuilding;
import fr.triedge.uchuu.utils.Utils;
import fr.triedge.uchuu.utils.Vars;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
                ArrayList<UserBuilding> buildings = DB.getInstance().getUserAvailableBuildings(user);
                if (buildings != null && buildings.size()>0){
                    model.addObject("buildings", buildings);

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else{
            model.addObject("error", "Vous n'avez pas le niveau 5.");
        }

        return model;
    }

    @GetMapping(Vars.VIEW_BUILDING_SPECIFIC)
    public ModelAndView viewBuilding(@RequestParam(value = "id") Integer id){
        ModelAndView model = new ModelAndView("viewBuilding.html");
        if (Utils.isValid(id)){
            try {
                ArrayList<UserBuilding> ub = DB.getInstance().getUserAvailableBuildings(getUser());
                UserBuilding b = ub.stream().filter(u -> u.getBuilding().getId()==id).findFirst().orElse(null);
                if (b == null){
                    model.addObject("error", "Vous ne possédé pas ce batiment.");
                    return model;
                }

                model.addObject("building", b);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else{
            model.addObject("error", "L'id n'est pas correcte");
        }
        return model;
    }
}

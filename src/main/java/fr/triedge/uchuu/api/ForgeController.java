package fr.triedge.uchuu.api;

import fr.triedge.uchuu.db.DB;
import fr.triedge.uchuu.model.Inventory;
import fr.triedge.uchuu.model.User;
import fr.triedge.uchuu.utils.Vars;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;

@RestController
public class ForgeController extends AbstractController{

    @GetMapping(Vars.VIEW_FORGE)
    public ModelAndView forge(){
        ModelAndView model = new ModelAndView("forge.html");
        // Check if forge is available
        User user = getUser();
        try {
            Inventory inv = DB.getInstance().getInventory(user);
            model.addObject("inv", inv);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return model;
    }
}

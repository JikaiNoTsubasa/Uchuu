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
public class InventoryController extends AbstractController{

    @GetMapping(Vars.VIEW_INVENTORY)
    public ModelAndView inventory(){
        ModelAndView model = new ModelAndView("inventory.html");
        User user = (User) getSession().getAttribute(Vars.USER);
        try{
            Inventory inv = DB.getInstance().getInventory(user);
            model.addObject("inventory", inv);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return model;
    }
}

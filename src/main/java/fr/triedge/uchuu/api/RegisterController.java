package fr.triedge.uchuu.api;

import fr.triedge.uchuu.db.DB;
import fr.triedge.uchuu.model.User;
import fr.triedge.uchuu.utils.Utils;
import fr.triedge.uchuu.utils.Vars;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;

@RestController
public class RegisterController {

    @RequestMapping(path = Vars.VIEW_REGISTER, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView register(
            @RequestParam(value = "username", required = false)String username,
            @RequestParam(value = "password", required = false)String password,
            @RequestParam(value = "password2", required = false)String password2
    ){
        ModelAndView model = new ModelAndView("register.html");
        if (Utils.isValid(username, password, password2)){
            if (!password.equals(password2)){
                model.addObject("error", "Passwords do not match.");
            }else{
                try {
                    User u = DB.getInstance().getUser(username);
                    if (u == null){ // no user exist for this username
                        DB.getInstance().createUser(username, password);
                        u = DB.getInstance().getUser(username, password);
                        if (u == null){
                            model.addObject("error", "Could not create account.");
                        }else{
                            return new ModelAndView("redirect:/home");
                        }
                    }else{
                        model.addObject("error", "Username already exists.");
                    }
                } catch (SQLException e) {
                    System.err.println(e);
                }
            }

        }
        return model;
    }
}

package fr.triedge.uchuu.api;

import com.idorsia.research.sbilib.utils.SPassword;
import fr.triedge.uchuu.db.DB;
import fr.triedge.uchuu.model.User;
import fr.triedge.uchuu.utils.Utils;
import fr.triedge.uchuu.utils.Vars;
import jakarta.servlet.http.Cookie;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;

@RestController
public class LoginController extends AbstractController{

    @RequestMapping(path = Vars.VIEW_LOGIN, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView login(
            @RequestParam(value = "username", required = false)String username,
            @RequestParam(value = "password", required = false)String password
            ){
        ModelAndView model = new ModelAndView("login.html");

        if (Utils.isValid(username) && Utils.isValid(password)){
            try {
                User user = DB.getInstance().getUser(username, password);
                if (user != null){
                    getSession().setAttribute(Vars.USER, user);
                    createLoginCookie("UUser", new SPassword(username).getEncrypted());
                    return new ModelAndView("redirect:/home");
                }else{
                    model.addObject("error", "Username or password incorrect");
                }
            } catch (SQLException e) {
                System.err.println(e);
                model.addObject("error", "Unexpected server error");
            }
        }

        return model;
    }

    @GetMapping(Vars.DISCONNECT)
    public ModelAndView disconnect(){
        getSession().setAttribute(Vars.USER, null);
        deleteLoginCookie("UUser");
        return new ModelAndView("redirect:/login");
    }

    public void createLoginCookie(String name, String value){
        SPassword pwd = new SPassword(value);
        Cookie cookie = new Cookie(name, pwd.getEncrypted());
        cookie.setMaxAge(86400);
        getHttpRep().addCookie(cookie);
    }

    public void deleteLoginCookie(String name){
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        getHttpRep().addCookie(cookie);
    }
}

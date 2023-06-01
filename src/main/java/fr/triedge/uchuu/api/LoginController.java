package fr.triedge.uchuu.api;

import com.idorsia.research.sbilib.utils.SPassword;
import fr.triedge.uchuu.db.DB;
import fr.triedge.uchuu.model.User;
import fr.triedge.uchuu.utils.Utils;
import fr.triedge.uchuu.utils.Vars;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;

@RestController
public class LoginController {

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
                    getSession().setAttribute("user", user);
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
        getSession().setAttribute("user", null);
        deleteLoginCookie("UUser");
        return new ModelAndView("redirect:/login");
    }

    public HttpSession getSession(){
        return getHttpReq().getSession(true); // true == allow create
    }

    public HttpServletRequest getHttpReq(){
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest();
    }

    public HttpServletResponse getHttpRep(){
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getResponse();
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

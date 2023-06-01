package fr.triedge.uchuu.auth;

import com.idorsia.research.sbilib.utils.SPassword;
import fr.triedge.uchuu.db.DB;
import fr.triedge.uchuu.model.User;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@Component
@Order(1)
public class SecurityFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        String url = req.getRequestURI();
        if (url.contains("login") || url.contains("register") || url.contains("css") || url.contains("png")){
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = req.getSession(true);
        User user = (User)session.getAttribute("user");

        if (user == null){
            Cookie cookie = getCookie(req.getCookies(), "UUser");
            if (cookie != null) {
                SPassword pwd = new SPassword(cookie.getValue());
                User u = null;
                try {
                    u = DB.getInstance().getUser(pwd.getDecrypted());
                    if (u != null){
                        session.setAttribute("user", u);
                        res.sendRedirect("home");
                        return;
                    }else{
                        res.sendRedirect("login");
                        return;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }else{
                res.sendRedirect("login");
                return;
            }
        }

        chain.doFilter(req, res);
    }

    private Cookie getCookie(Cookie[] cookies, String name){
        if (cookies == null || name == null)
            return null;
        for (Cookie c : cookies){
            if (c.getName().equals(name))
                return c;
        }
        return null;
    }
}

package fr.triedge.uchuu.auth;

import fr.triedge.uchuu.model.User;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
@Order(1)
public class SecurityFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        System.out.println("Run security filter");
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        String url = req.getRequestURI();
        if (url.contains("login") || url.contains("css") || url.contains("png")){
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = req.getSession(true);
        User user = (User)session.getAttribute("user");

        if (user == null){

        }

        chain.doFilter(req, res);
    }
}

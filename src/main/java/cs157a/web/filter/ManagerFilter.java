package cs157a.web.filter;

import java.io.IOException;

import cs157a.model.User;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Only a Team Manager may open the FR3 / FR4 pages.
 * Everyone else gets "403 Forbidden".
 */
@WebFilter({"/app/roster", "/app/cars", "/app/tires"})
public class ManagerFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        User user = (User) req.getSession().getAttribute("user");

        if (user == null || !"Manager".equals(user.getRole())) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Only a team manager can open this page.");
            return;
        }

        chain.doFilter(request, response);   // a manager: let them through
    }
}
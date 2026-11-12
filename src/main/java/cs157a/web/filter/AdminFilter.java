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
 * Only the system administrator may open the FR10 page.
 *
 * An administrator belongs to no team, which is why this filter checks the
 * role and not a team id.
 */
@WebFilter("/app/admin")
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        User user = (User) req.getSession().getAttribute("user");

        if (user == null || !"Admin".equals(user.getRole())) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN,
                          "Only the system administrator can open this page.");
            return;
        }

        chain.doFilter(request, response);
    }
}

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
 * The FR9 pages are engineering tools. A Race Engineer may open them, and so
 * may a Team Manager, because the design says a manager can do everything an
 * engineer can. A Driver cannot: setups are not theirs to read.
 */
@WebFilter("/app/setups")
public class EngineerFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        User user = (User) req.getSession().getAttribute("user");
        boolean allowed = user != null
                && ("Engineer".equals(user.getRole()) || "Manager".equals(user.getRole()));

        if (!allowed) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN,
                          "Only a race engineer or a team manager can open this page.");
            return;
        }

        chain.doFilter(request, response);
    }
}

package cs157a.web.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Guards every URL under /app/. A request that arrives without a logged-in
 * session is sent back to the login page.
 *
 * Written once here instead of repeated inside each servlet - forgetting the
 * check in one servlet would be a hole in the whole application.
 *
 * This is also why the JSP files live under WEB-INF/views/: a browser cannot
 * request them directly, so nobody can reach a page without passing here.
 */
@WebFilter("/app/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // getSession(false) means "give me the session only if one exists",
        // rather than silently creating an empty one.
        HttpSession session = req.getSession(false);
        boolean loggedIn = session != null && session.getAttribute("user") != null;

        if (!loggedIn) {
            res.sendRedirect(req.getContextPath() + "/index.jsp?error=timeout");
            return;                       // stop here, the servlet never runs
        }

        chain.doFilter(request, response);   // logged in, carry on to the servlet
    }
}

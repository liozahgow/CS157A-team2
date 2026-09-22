package cs157a.web;

import java.io.IOException;
import java.sql.SQLException;

import cs157a.dao.UserDAO;
import cs157a.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Step 2 of the login flow: receives the form posted by index.jsp.
 *
 * A servlet coordinates and nothing more - it reads the request, asks a DAO to
 * do the database work, and decides where to send the user next. No SQL and
 * no HTML appear in this file.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final UserDAO userDao = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email    = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            User user = userDao.findByEmailAndPassword(email, password);

            if (user == null) {
                // Same message whether the email or the password was wrong.
                response.sendRedirect(request.getContextPath() + "/index.jsp?error=bad");
                return;
            }

            // Login succeeded. Store the user in the session; from now on the
            // AuthFilter sees it and lets this browser through to /app/.
            request.getSession().setAttribute("user", user);

            // Redirect rather than forward, so the address bar shows /app/home
            // and a page refresh does not re-submit the login form.
            response.sendRedirect(request.getContextPath() + "/app/home");

        } catch (SQLException e) {
            throw new ServletException("Login failed while reading the database", e);
        }
    }
}

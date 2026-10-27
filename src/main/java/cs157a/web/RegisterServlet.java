package cs157a.web;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import cs157a.dao.TeamDAO;
import cs157a.dao.UserDAO;
import cs157a.model.Team;
import cs157a.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * FR1 - account registration.
 *
 *   GET  /register  shows the sign-up form
 *   POST /register  checks the form, then asks UserDAO to create the account
 *
 * Drivers and engineers join an existing team with its invite code. A manager
 * names a new team instead, which starts as 'Pending' until the administrator
 * approves it.
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String VIEW = "/WEB-INF/views/register.jsp";

    private final UserDAO userDao = new UserDAO();
    private final TeamDAO teamDao = new TeamDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String displayName  = trim(request.getParameter("displayName"));
        String email        = trim(request.getParameter("email"));
        String password     = request.getParameter("password");    // spaces are allowed in a password
        String phone        = trim(request.getParameter("phone"));
        String role         = trim(request.getParameter("role"));
        String inviteCode   = trim(request.getParameter("inviteCode")).toUpperCase();
        String teamName     = trim(request.getParameter("teamName"));
        String driverNumber = trim(request.getParameter("driverNumber"));

        try {
            // ---- 1. Fields every role needs ----------------------------------
            if (displayName.isEmpty() || email.isEmpty() || password == null || password.isEmpty()) {
                fail(request, response, "Display name, email and password are required.");
                return;
            }
            if (displayName.length() > 100 || email.length() > 255 || phone.length() > 20) {
                fail(request, response, "Display name, email or phone is too long.");
                return;
            }
            if (!email.contains("@")) {
                fail(request, response, "Please enter a valid email address.");
                return;
            }
            if (password.length() < 8) {
                fail(request, response, "Password must be at least 8 characters.");
                return;
            }
            if (!role.equals("Driver") && !role.equals("Engineer") && !role.equals("Manager")) {
                fail(request, response, "Please choose a role.");
                return;
            }
            if (userDao.emailExists(email)) {
                fail(request, response, "That email is already registered.");
                return;
            }

            User user = new User();
            user.setDisplayName(displayName);
            user.setEmail(email);
            user.setPhone(phone.isEmpty() ? null : phone);
            user.setRole(role);

            // ---- 2. Role-specific fields --------------------------------------
            String newTeamName = null;

            if (role.equals("Manager")) {
                if (teamName.isEmpty() || teamName.length() > 100) {
                    fail(request, response, "Please enter a team name (up to 100 characters).");
                    return;
                }
                if (teamDao.nameExists(teamName)) {
                    fail(request, response, "A team with that name already exists.");
                    return;
                }
                newTeamName = teamName;

            } else {
                Team team = teamDao.findByInviteCode(inviteCode);
                if (team == null) {
                    fail(request, response, "That invite code does not match any team.");
                    return;
                }
                if (!"Approved".equals(team.getStatus())) {
                    fail(request, response, "That team has not been approved yet, or is suspended.");
                    return;
                }
                user.setTeamId(team.getTeamId());

                if (role.equals("Driver")) {
                    Integer number = parseDriverNumber(driverNumber);
                    if (number == null) {
                        fail(request, response, "Drivers need a car number between 0 and 999.");
                        return;
                    }
                    user.setDriverNumber(number);
                }
            }

            // ---- 3. Write everything in one transaction -----------------------
            userDao.register(user, password, newTeamName);

        } catch (SQLIntegrityConstraintViolationException e) {
            // Someone else took the same email or team name between our check
            // above and the INSERT. The UNIQUE constraint caught it.
            fail(request, response, "That email or team name was just taken. Please try again.");
            return;

        } catch (SQLException e) {
            throw new ServletException("Registration failed while writing to the database", e);
        }

        // Redirect, so refreshing the next page does not submit the form again.
        String done = role.equals("Manager") ? "pending" : "ok";
        response.sendRedirect(request.getContextPath() + "/index.jsp?registered=" + done);
    }

    /** Shows the form again with an error. The JSP refills the fields from the request. */
    private void fail(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {

        request.setAttribute("error", message);
        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    /** Returns the car number, or null when it is missing or not 0-999. */
    private Integer parseDriverNumber(String text) {
        try {
            int n = Integer.parseInt(text);
            return (n >= 0 && n <= 999) ? n : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** A missing parameter becomes "", so the checks above never see null. */
    private String trim(String s) {
        return s == null ? "" : s.trim();
    }
}

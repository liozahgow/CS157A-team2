package cs157a.web;

import java.io.IOException;
import java.sql.SQLException;

import cs157a.dao.UserDAO;
import cs157a.model.User;
import cs157a.service.ProfileService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * FR2 - view and change your own profile.
 *
 *   GET  /app/profile  shows the current details
 *   POST /app/profile  checks the current password, then saves the changes
 *
 * Under /app/, so AuthFilter has already made sure someone is logged in.
 * The user_id always comes from the session, never from the form, so nobody
 * can edit another person's profile by changing a hidden field.
 */
@WebServlet("/app/profile")
public class ProfileServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String VIEW = "/WEB-INF/views/profile.jsp";

    private final UserDAO userDao = new UserDAO();

    private final ProfileService profileService = new ProfileService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            show(request, response, null);
        } catch (SQLException e) {
            throw new ServletException("Could not load the profile", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User sessionUser = (User) request.getSession().getAttribute("user");

        String displayName     = trim(request.getParameter("displayName"));
        String phone           = trim(request.getParameter("phone"));
        String driverNumber    = trim(request.getParameter("driverNumber"));
        String currentPassword = request.getParameter("currentPassword");
        String newPassword     = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        try {
            // ---- 1. Check the form ------------------------------------------
            if (displayName.isEmpty() || displayName.length() > 100) {
                show(request, response, "Display name is required (up to 100 characters).");
                return;
            }
            if (phone.length() > 20) {
                show(request, response, "Phone is too long.");
                return;
            }
            if (currentPassword == null || currentPassword.isEmpty()) {
                show(request, response, "Enter your current password to save changes.");
                return;
            }

            // An empty new-password box means "keep my password".
            if (newPassword == null || newPassword.isEmpty()) {
                newPassword = null;
            } else if (newPassword.length() < 8) {
                show(request, response, "New password must be at least 8 characters.");
                return;
            } else if (!newPassword.equals(confirmPassword)) {
                show(request, response, "The two new passwords do not match.");
                return;
            }

            User changes = new User();
            changes.setUserId(sessionUser.getUserId());
            changes.setRole(sessionUser.getRole());
            changes.setDisplayName(displayName);
            changes.setPhone(phone.isEmpty() ? null : phone);

            if ("Driver".equals(sessionUser.getRole())) {
                Integer number = parseDriverNumber(driverNumber);
                if (number == null) {
                    show(request, response, "Car number must be between 0 and 999.");
                    return;
                }
                changes.setDriverNumber(number);
            }

            // ---- 2. Save ----------------------------------------------------
            if (!profileService.updateProfile(changes, currentPassword, newPassword)) {
                show(request, response, "Current password is incorrect. Nothing was changed.");
                return;
            }

            // ---- 3. Refresh the session copy, so the header shows the new name
            request.getSession().setAttribute("user", userDao.findById(sessionUser.getUserId()));

        } catch (SQLException e) {
            throw new ServletException("Could not save the profile", e);
        }

        // Redirect, so a refresh does not submit the form again.
        response.sendRedirect(request.getContextPath() + "/app/profile?saved=1");
    }

    /**
     * Loads the user fresh from the database and renders the page. With an
     * error, the JSP refills the fields from what was submitted instead.
     */
    private void show(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException, SQLException {

        User sessionUser = (User) request.getSession().getAttribute("user");

        request.setAttribute("profile", userDao.findById(sessionUser.getUserId()));
        request.setAttribute("error", error);
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

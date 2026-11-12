package cs157a.web;

import java.io.IOException;
import java.sql.SQLException;

import cs157a.dao.TeamDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * FR10 - team approval.
 *
 *   GET  /app/admin     every team with its status and member count
 *   POST /app/admin     approve, suspend or reactivate one team
 *
 * A new team is created as Pending when a manager signs up. Until an
 * administrator approves it the team is not meant to be used, and a
 * suspended team is one that has been stopped again.
 *
 * AdminFilter keeps everybody else out.
 */
@WebServlet("/app/admin")
public class AdminServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final TeamDAO teamDao = new TeamDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        show(request, response, null, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = trim(request.getParameter("action"));
        String idText = trim(request.getParameter("teamId"));

        // Only these three words may ever reach the status column.
        String status;
        if ("approve".equals(action)) {
            status = "Approved";
        } else if ("suspend".equals(action)) {
            status = "Suspended";
        } else if ("pending".equals(action)) {
            status = "Pending";
        } else {
            show(request, response, null, "Unknown action.");
            return;
        }

        try {
            teamDao.updateStatus(Integer.parseInt(idText), status);

            // Redirect after the change so a refresh does not repeat it.
            response.sendRedirect(request.getContextPath() + "/app/admin?done=" + status);

        } catch (NumberFormatException e) {
            show(request, response, null, "That team id is not a number.");
        } catch (SQLException e) {
            throw new ServletException("Could not change the team status", e);
        }
    }

    private void show(HttpServletRequest request, HttpServletResponse response,
                      String notice, String error)
            throws ServletException, IOException {

        try {
            request.setAttribute("teams", teamDao.findAllWithMemberCount());
            request.setAttribute("notice", notice);
            request.setAttribute("error", error);
            request.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Could not load the team list", e);
        }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}

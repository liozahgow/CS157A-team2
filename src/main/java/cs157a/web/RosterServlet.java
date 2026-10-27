package cs157a.web;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import cs157a.dao.RosterDAO;
import cs157a.model.RosterMember;
import cs157a.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * FR3 - shows the team roster.
 *
 *   /app/roster             active members only
 *   /app/roster?inactive=1  also members who left the team
 */
@WebServlet("/app/roster")
public class RosterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;   // Eclipse wants this line in every servlet

    private final RosterDAO rosterDao = new RosterDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // The logged-in manager. Their team_id decides whose roster we show.
        User manager = (User) request.getSession().getAttribute("user");

        boolean showInactive = "1".equals(request.getParameter("inactive"));

        try {
            List<RosterMember> members = rosterDao.findByTeam(manager.getTeamId(), showInactive);

            request.setAttribute("members", members);
            request.setAttribute("showInactive", showInactive);
            request.getRequestDispatcher("/WEB-INF/views/roster.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Could not load the team roster", e);
        }
    }
}
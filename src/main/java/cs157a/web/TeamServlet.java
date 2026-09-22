package cs157a.web;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import cs157a.dao.TeamDAO;
import cs157a.model.Team;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles the "List all racing teams" button.
 *
 * Follow the five steps below and you have seen the whole architecture:
 * filter -> servlet -> DAO -> SQL -> model -> JSP.
 */
@WebServlet("/app/teams")
public class TeamServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final TeamDAO teamDao = new TeamDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 1. Ask the DAO for the data. The SQL itself is in TeamDAO.
            List<Team> teams = teamDao.findAll();

            // 2. Put the result where the JSP can reach it. The JSP will read
            //    it back with request.getAttribute("teams").
            request.setAttribute("teams", teams);

            // 3. Hand over to the view, which only prints what it was given.
            request.getRequestDispatcher("/WEB-INF/views/home.jsp")
                   .forward(request, response);

        } catch (SQLException e) {
            // Let Tomcat show its error page. A real application would render
            // a friendly message instead.
            throw new ServletException("Could not load the team list", e);
        }
    }
}

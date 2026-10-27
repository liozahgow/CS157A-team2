package cs157a.web;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import cs157a.dao.TireSetDAO;
import cs157a.model.TireSet;
import cs157a.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * FR4 - the tire sets a team owns.
 *
 *   GET  /app/tires               sets that are still in service
 *   GET  /app/tires?retired=1     include retired sets
 *   POST /app/tires               add a set, or retire / reactivate one
 *
 * A tire set is a weak entity, so it is identified by the team id together
 * with the set number. The team id always comes from the logged-in manager,
 * never from the form, so a manager cannot reach another team's sets.
 */
@WebServlet("/app/tires")
public class TireSetServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final TireSetDAO tireDao = new TireSetDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        show(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User manager = (User) request.getSession().getAttribute("user");
        String action = request.getParameter("action");

        try {
            if ("retire".equals(action) || "reactivate".equals(action)) {
                int setNo = Integer.parseInt(request.getParameter("setNo"));
                tireDao.setStatus(manager.getTeamId(), setNo,
                                  "retire".equals(action) ? "Retired" : "In Use");
                response.sendRedirect(request.getContextPath() + "/app/tires");
                return;
            }

            // ---- add a set --------------------------------------------------
            String compound = trim(request.getParameter("compound"));
            String tireSize = trim(request.getParameter("tireSize"));
            String received = trim(request.getParameter("receivedDate"));
            String maxText  = trim(request.getParameter("maxHeatCycles"));

            if (compound.isEmpty()) {
                show(request, response, "Please choose a compound.");
                return;
            }

            int maxCycles;
            try {
                maxCycles = Integer.parseInt(maxText);
            } catch (NumberFormatException e) {
                show(request, response, "Maximum heat cycles must be a whole number.");
                return;
            }
            if (maxCycles < 1 || maxCycles > 99) {
                show(request, response, "Maximum heat cycles must be between 1 and 99.");
                return;
            }

            Date receivedDate = null;
            if (!received.isEmpty()) {
                try {
                    receivedDate = Date.valueOf(LocalDate.parse(received));
                } catch (DateTimeParseException e) {
                    show(request, response, "The received date is not a valid date.");
                    return;
                }
            }

            TireSet set = new TireSet();
            set.setTeamId(manager.getTeamId());
            set.setCompound(compound);
            set.setTireSize(tireSize.isEmpty() ? null : tireSize);
            set.setReceivedDate(receivedDate);
            set.setMaxHeatCycles(maxCycles);

            int setNo = tireDao.insert(set);
            response.sendRedirect(request.getContextPath() + "/app/tires?added=" + setNo);

        } catch (NumberFormatException e) {
            show(request, response, "That set number is not a number.");
        } catch (SQLException e) {
            throw new ServletException("Could not save the tire set", e);
        }
    }

    private void show(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {

        User manager = (User) request.getSession().getAttribute("user");
        boolean includeRetired = "1".equals(request.getParameter("retired"));

        try {
            request.setAttribute("sets", tireDao.findByTeam(manager.getTeamId(), includeRetired));
            request.setAttribute("includeRetired", includeRetired);
            request.setAttribute("error", error);
            request.getRequestDispatcher("/WEB-INF/views/tires.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Could not load the tire inventory", e);
        }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}

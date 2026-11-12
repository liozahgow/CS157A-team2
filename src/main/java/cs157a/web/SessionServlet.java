package cs157a.web;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import cs157a.dao.CarDAO;
import cs157a.dao.CarSetupDAO;
import cs157a.dao.EventDAO;
import cs157a.dao.SessionDAO;
import cs157a.model.SessionRow;
import cs157a.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * FR6 - the sessions an engineer records.
 *
 *   GET  /app/sessions        the session list and the form to add one
 *   POST /app/sessions        add a session
 *
 * EngineerFilter keeps drivers out. Every list is loaded with the logged-in
 * user team id, so an engineer only ever sees their own team.
 */
@WebServlet("/app/sessions")
public class SessionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final SessionDAO  sessionDao = new SessionDAO();
    private final EventDAO    eventDao   = new EventDAO();
    private final CarDAO      carDao     = new CarDAO();
    private final CarSetupDAO setupDao   = new CarSetupDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        show(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");

        String eventText   = trim(request.getParameter("eventId"));
        String carText     = trim(request.getParameter("carId"));
        String driverText  = trim(request.getParameter("driverUserId"));
        String setupText   = trim(request.getParameter("setupId"));
        String type        = trim(request.getParameter("sessionType"));
        String dateText    = trim(request.getParameter("sessionDate"));
        String weather     = trim(request.getParameter("weather"));
        String tempText    = trim(request.getParameter("airTempC"));
        String feedback    = trim(request.getParameter("driverFeedback"));

        try {
            if (eventText.isEmpty() || carText.isEmpty() || driverText.isEmpty()) {
                show(request, response, "Event, car and driver are all required.");
                return;
            }
            if (type.isEmpty()) {
                show(request, response, "Please choose a session type.");
                return;
            }

            Date sessionDate;
            try {
                sessionDate = Date.valueOf(LocalDate.parse(dateText));
            } catch (DateTimeParseException e) {
                show(request, response, "Please give a valid session date.");
                return;
            }

            BigDecimal airTemp = null;
            if (!tempText.isEmpty()) {
                try {
                    airTemp = new BigDecimal(tempText);
                } catch (NumberFormatException e) {
                    show(request, response, "Air temperature must be a number.");
                    return;
                }
            }

            // An empty setup box means the sheet has not been written yet.
            // setup_id allows NULL for exactly that case.
            Integer setupId = setupText.isEmpty() ? null : Integer.valueOf(setupText);

            // The event belongs to this team, or the insert is refused. This
            // is the check that stops somebody posting another team event id.
            if (eventDao.findByIdForTeam(Integer.parseInt(eventText), user.getTeamId()) == null) {
                show(request, response, "That event does not belong to your team.");
                return;
            }

            int sessionId = sessionDao.insert(
                    Integer.parseInt(eventText),
                    Integer.parseInt(carText),
                    Integer.parseInt(driverText),
                    setupId, type, sessionDate, weather.isEmpty() ? null : weather,
                    airTemp, feedback.isEmpty() ? null : feedback);

            response.sendRedirect(request.getContextPath() + "/app/sessions?added=" + sessionId);

        } catch (NumberFormatException e) {
            show(request, response, "One of the chosen values is not a number.");
        } catch (SQLException e) {
            throw new ServletException("Could not save the session", e);
        }
    }

    private void show(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");

        try {
            List<SessionRow> sessions = sessionDao.findByTeam(user.getTeamId());

            request.setAttribute("sessions", sessions);
            request.setAttribute("events", eventDao.findByTeam(user.getTeamId()));
            request.setAttribute("cars", carDao.findByTeam(user.getTeamId(), false));
            request.setAttribute("drivers", sessionDao.findDriversForTeam(user.getTeamId()));
            request.setAttribute("setups", setupDao.findByTeam(user.getTeamId()));
            request.setAttribute("error", error);

            request.getRequestDispatcher("/WEB-INF/views/sessions.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Could not load the sessions", e);
        }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}

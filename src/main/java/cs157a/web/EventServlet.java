package cs157a.web;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import cs157a.dao.EventDAO;
import cs157a.dao.TrackDAO;
import cs157a.model.Event;
import cs157a.model.SessionSummary;
import cs157a.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * FR5 - the team calendar.
 *
 *   GET  /app/events                  the calendar
 *   GET  /app/events?eventId=3        the calendar plus that event's sessions
 *   POST /app/events                  add an event
 *
 * ManagerFilter keeps everyone but a Team Manager out, and every query is
 * given the manager's own team id, so one team never sees another's calendar.
 */
@WebServlet("/app/events")
public class EventServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final EventDAO eventDao = new EventDAO();
    private final TrackDAO trackDao = new TrackDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        show(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User manager = (User) request.getSession().getAttribute("user");

        String name      = trim(request.getParameter("eventName"));
        String trackText = trim(request.getParameter("trackId"));
        String startText = trim(request.getParameter("startDate"));
        String endText   = trim(request.getParameter("endDate"));
        String type      = trim(request.getParameter("eventType"));

        try {
            if (name.isEmpty() || name.length() > 100) {
                show(request, response, "Event name is required (up to 100 characters).");
                return;
            }
            if (trackText.isEmpty()) {
                show(request, response, "Please choose a track.");
                return;
            }

            Date start;
            Date end;
            try {
                start = Date.valueOf(LocalDate.parse(startText));
                end   = Date.valueOf(LocalDate.parse(endText));
            } catch (DateTimeParseException e) {
                show(request, response, "Please give a valid start and end date.");
                return;
            }
            if (end.before(start)) {
                show(request, response, "The end date cannot be before the start date.");
                return;
            }

            Event event = new Event();
            event.setEventName(name);
            event.setTrackId(Integer.parseInt(trackText));
            event.setStartDate(start);
            event.setEndDate(end);
            event.setEventType(type.isEmpty() ? "Race Weekend" : type);

            eventDao.insert(manager.getTeamId(), event);
            response.sendRedirect(request.getContextPath() + "/app/events?added=1");

        } catch (NumberFormatException e) {
            show(request, response, "That track id is not a number.");
        } catch (SQLException e) {
            throw new ServletException("Could not save the event", e);
        }
    }

    private void show(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {

        User manager = (User) request.getSession().getAttribute("user");
        String eventIdText = trim(request.getParameter("eventId"));

        try {
            List<Event> events = eventDao.findByTeam(manager.getTeamId());
            request.setAttribute("events", events);
            request.setAttribute("tracks", trackDao.findAll());
            request.setAttribute("error", error);

            // When one event is selected, also load the sessions run at it.
            if (!eventIdText.isEmpty()) {
                int eventId = Integer.parseInt(eventIdText);

                // findByIdForTeam returns null when the event is not this
                // team's, which is what stops somebody reading another team's
                // weekend by typing an id into the address bar.
                Event selected = eventDao.findByIdForTeam(eventId, manager.getTeamId());
                if (selected != null) {
                    List<SessionSummary> sessions = eventDao.findSessionsForEvent(eventId);
                    request.setAttribute("selectedEvent", selected);
                    request.setAttribute("sessions", sessions);
                }
            }

            request.getRequestDispatcher("/WEB-INF/views/events.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "That event id is not a number.");
            request.getRequestDispatcher("/WEB-INF/views/events.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Could not load the calendar", e);
        }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}

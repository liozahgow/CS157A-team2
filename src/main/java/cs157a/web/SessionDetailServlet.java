package cs157a.web;

import java.io.IOException;
import java.sql.SQLException;

import cs157a.dao.LapDAO;
import cs157a.dao.SessionDAO;
import cs157a.dao.TireSetDAO;
import cs157a.dao.TireUsageDAO;
import cs157a.model.SessionRow;
import cs157a.model.User;
import cs157a.service.LapEntryService;
import cs157a.service.TireUsageService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * One session page, which is where FR7 and FR8 are done.
 *
 *   GET  /app/session?sessionId=5       the session, its laps and its tires
 *   POST /app/session  action=laps      FR8, add lap times
 *   POST /app/session  action=tires     FR7, record a tire set and its heat cycle
 *   POST /app/session  action=feedback  save the driver debrief
 *
 * Every request reloads the session with findByIdForTeam, so an engineer who
 * types another team session id into the address bar gets nothing back.
 */
@WebServlet("/app/session")
public class SessionDetailServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final SessionDAO       sessionDao  = new SessionDAO();
    private final LapDAO           lapDao      = new LapDAO();
    private final TireUsageDAO     usageDao    = new TireUsageDAO();
    private final TireSetDAO       tireDao     = new TireSetDAO();
    private final LapEntryService  lapService  = new LapEntryService();
    private final TireUsageService tireService = new TireUsageService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        show(request, response, null, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User   user   = (User) request.getSession().getAttribute("user");
        String action = trim(request.getParameter("action"));

        try {
            int sessionId = Integer.parseInt(trim(request.getParameter("sessionId")));

            SessionRow session = sessionDao.findByIdForTeam(sessionId, user.getTeamId());
            if (session == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                                   "No such session for your team.");
                return;
            }

            if ("laps".equals(action)) {
                handleLaps(request, response, sessionId);

            } else if ("tires".equals(action)) {
                handleTires(request, response, user.getTeamId(), sessionId);

            } else if ("feedback".equals(action)) {
                sessionDao.updateFeedback(sessionId, user.getTeamId(),
                                          trim(request.getParameter("driverFeedback")));
                show(request, response, "Driver feedback saved.", null);

            } else {
                show(request, response, null, "Unknown action.");
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "That session id is not a number.");
        } catch (SQLException e) {
            throw new ServletException("Could not update the session", e);
        }
    }

    /** FR8 - a batch of lap times. */
    private void handleLaps(HttpServletRequest request, HttpServletResponse response, int sessionId)
            throws ServletException, IOException, SQLException {

        String lapText = request.getParameter("lapTimes");
        if (lapText == null || lapText.trim().isEmpty()) {
            show(request, response, null, "Type at least one lap time.");
            return;
        }

        LapEntryService.Result r = lapService.addLaps(sessionId, lapText);

        String note = r.message;
        if (!r.rejected.isEmpty()) {
            note = note + " These lines were not understood: " + String.join(" | ", r.rejected);
        }
        show(request, response, r.saved ? note : null, r.saved ? null : note);
    }

    /** FR7 - a tire set used in this session, which is one heat cycle. */
    private void handleTires(HttpServletRequest request, HttpServletResponse response,
                             int teamId, int sessionId)
            throws ServletException, IOException, SQLException {

        String setText  = trim(request.getParameter("setNo"));
        String lapsText = trim(request.getParameter("lapsRun"));

        int setNo;
        int lapsRun;
        try {
            setNo   = Integer.parseInt(setText);
            lapsRun = Integer.parseInt(lapsText);
        } catch (NumberFormatException e) {
            show(request, response, null, "Choose a tire set and give the number of laps it ran.");
            return;
        }
        if (lapsRun < 0 || lapsRun > 999) {
            show(request, response, null, "Laps run must be between 0 and 999.");
            return;
        }

        TireUsageService.Result r = tireService.record(teamId, sessionId, setNo, lapsRun);

        String note = r.message;
        if (r.saved && r.retired) {
            note = note + " That was its last cycle, so the set is now retired.";
        } else if (r.saved && r.nearLimit) {
            note = note + " Warning: one cycle left on this set.";
        }
        show(request, response, r.saved ? note : null, r.saved ? null : note);
    }

    /** Loads everything the page shows and renders it. */
    private void show(HttpServletRequest request, HttpServletResponse response,
                      String notice, String error)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");

        try {
            int sessionId = Integer.parseInt(trim(request.getParameter("sessionId")));

            SessionRow session = sessionDao.findByIdForTeam(sessionId, user.getTeamId());
            if (session == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                                   "No such session for your team.");
                return;
            }

            request.setAttribute("session", session);
            request.setAttribute("laps", lapDao.findBySession(sessionId));
            request.setAttribute("stats", lapDao.statsForSession(sessionId));
            request.setAttribute("usages", usageDao.findByTeam(user.getTeamId()));
            request.setAttribute("tireSets", tireDao.findByTeam(user.getTeamId(), false));
            request.setAttribute("notice", notice);
            request.setAttribute("error", error);

            request.getRequestDispatcher("/WEB-INF/views/session.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "That session id is not a number.");
        } catch (SQLException e) {
            throw new ServletException("Could not load the session", e);
        }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}

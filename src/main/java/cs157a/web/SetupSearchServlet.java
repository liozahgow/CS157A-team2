package cs157a.web;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import cs157a.dao.SetupSearchDAO;
import cs157a.model.SetupResult;
import cs157a.model.Track;
import cs157a.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * FR9 - best setup search.
 *
 *   GET /app/setups                                   the empty search form
 *   GET /app/setups?trackId=1&carClass=...&weather=Dry  the ranked answer
 *
 * The search is a GET on purpose: it only reads, so the result has its own
 * address that an engineer can bookmark or send to a team mate.
 */
@WebServlet("/app/setups")
public class SetupSearchServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final SetupSearchDAO searchDao = new SetupSearchDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");

        String trackIdText = trim(request.getParameter("trackId"));
        String carClass    = blankToNull(request.getParameter("carClass"));
        String weather     = blankToNull(request.getParameter("weather"));

        try {
            // The drop-downs only offer what this team has actually used.
            List<Track>  tracks  = searchDao.findTracksUsedByTeam(user.getTeamId());
            List<String> classes = searchDao.findCarClassesUsedByTeam(user.getTeamId());
            List<String> weathers = searchDao.findWeatherUsedByTeam(user.getTeamId());

            request.setAttribute("tracks", tracks);
            request.setAttribute("carClasses", classes);
            request.setAttribute("weatherOptions", weathers);
            request.setAttribute("selectedTrackId", trackIdText);
            request.setAttribute("selectedCarClass", carClass);
            request.setAttribute("selectedWeather", weather);

            if (!trackIdText.isEmpty()) {
                int trackId = Integer.parseInt(trackIdText);
                List<SetupResult> results =
                        searchDao.findBestSetups(user.getTeamId(), trackId, carClass, weather);
                request.setAttribute("results", results);
            }

            request.getRequestDispatcher("/WEB-INF/views/setups.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "That track id is not a number.");
            request.getRequestDispatcher("/WEB-INF/views/setups.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Could not run the setup search", e);
        }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    /** An empty drop-down means "do not filter on this", which the DAO reads as null. */
    private static String blankToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}

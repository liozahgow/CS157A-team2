package cs157a.web;

import java.io.IOException;
import java.sql.SQLException;

import cs157a.dao.CarDAO;
import cs157a.model.Car;
import cs157a.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * FR4 - the cars a team owns.
 *
 *   GET  /app/cars                 list the active cars
 *   GET  /app/cars?retired=1       include retired cars
 *   POST /app/cars                 add a car, or retire / reactivate one
 *
 * ManagerFilter already guarantees that only a Team Manager gets here, so
 * this servlet does not repeat the role check. It does pass the manager's own
 * team id into every query, so the page can only ever show that team's cars.
 */
@WebServlet("/app/cars")
public class CarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final CarDAO carDao = new CarDAO();

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
                int carId = Integer.parseInt(request.getParameter("carId"));
                carDao.setActive(carId, manager.getTeamId(), "reactivate".equals(action));

                // Redirect after a change so a refresh does not repeat it.
                response.sendRedirect(request.getContextPath() + "/app/cars");
                return;
            }

            // ---- add a car -------------------------------------------------
            String chassisNo = trim(request.getParameter("chassisNo"));
            String model     = trim(request.getParameter("model"));
            String carClass  = trim(request.getParameter("carClass"));

            if (chassisNo.isEmpty() || model.isEmpty() || carClass.isEmpty()) {
                show(request, response, "Chassis number, model and class are all required.");
                return;
            }
            if (chassisNo.length() > 50 || model.length() > 100 || carClass.length() > 50) {
                show(request, response, "One of the fields is too long.");
                return;
            }
            if (carDao.chassisExists(chassisNo)) {
                show(request, response, "A car with chassis number " + chassisNo + " already exists.");
                return;
            }

            Car car = new Car();
            car.setTeamId(manager.getTeamId());
            car.setChassisNo(chassisNo);
            car.setModel(model);
            car.setCarClass(carClass);
            carDao.insert(car);

            response.sendRedirect(request.getContextPath() + "/app/cars?added=1");

        } catch (NumberFormatException e) {
            show(request, response, "That car id is not a number.");
        } catch (SQLException e) {
            throw new ServletException("Could not save the car", e);
        }
    }

    /** Loads the list and renders the page, with an optional error message. */
    private void show(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {

        User manager = (User) request.getSession().getAttribute("user");
        boolean includeRetired = "1".equals(request.getParameter("retired"));

        try {
            request.setAttribute("cars", carDao.findByTeam(manager.getTeamId(), includeRetired));
            request.setAttribute("includeRetired", includeRetired);
            request.setAttribute("error", error);
            request.getRequestDispatcher("/WEB-INF/views/cars.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Could not load the car list", e);
        }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}

package cs157a.web;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * The page shown right after login: the button, and an empty result area.
 *
 * It sets no attributes, so home.jsp finds no team list and shows the hint
 * text instead. Pressing the button sends the browser to TeamServlet, which
 * renders the same JSP with the list filled in.
 */
@WebServlet("/app/home")
public class HomeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // forward() is an internal hand-off. The browser never sees this path,
        // which is why a WEB-INF file can be used as the view.
        request.getRequestDispatcher("/WEB-INF/views/home.jsp")
               .forward(request, response);
    }
}

<%--
    FR9 - best setup search. Rendered only by SetupSearchServlet.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List, cs157a.model.SetupResult, cs157a.model.Track,
                 cs157a.model.User, cs157a.util.Html" %>

<%
    @SuppressWarnings("unchecked")
    List<Track> tracks = (List<Track>) request.getAttribute("tracks");

    @SuppressWarnings("unchecked")
    List<String> carClasses = (List<String>) request.getAttribute("carClasses");

    @SuppressWarnings("unchecked")
    List<String> weatherOptions = (List<String>) request.getAttribute("weatherOptions");

    @SuppressWarnings("unchecked")
    List<SetupResult> results = (List<SetupResult>) request.getAttribute("results");

    String selTrack   = (String) request.getAttribute("selectedTrackId");
    String selClass   = (String) request.getAttribute("selectedCarClass");
    String selWeather = (String) request.getAttribute("selectedWeather");
    String error      = (String) request.getAttribute("error");

    User   user = (User) session.getAttribute("user");
    String ctx  = request.getContextPath();

    if (selTrack == null)   { selTrack = ""; }
    if (selClass == null)   { selClass = ""; }
    if (selWeather == null) { selWeather = ""; }
%>

<html>
<head>
  <title>Pitwall - Best setup search</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="panel">
  <h1>Best setup search</h1>

  <p class="nav">
    <a href="<%= ctx %>/app/home">Home</a>
    <% if ("Manager".equals(user.getRole())) { %>
      <a href="<%= ctx %>/app/roster">Roster</a>
      <a href="<%= ctx %>/app/cars">Cars</a>
      <a href="<%= ctx %>/app/tires">Tire sets</a>
    <% } %>
    <a href="<%= ctx %>/app/setups">Setups</a>
    <a href="<%= ctx %>/logout">Log out</a>
  </p>

  <p class="hint">
    Which setup produced the fastest lap at a track? Pick a track, narrow it by
    car class or weather if you want, and the setups come back ranked by their
    best valid lap.
  </p>
</div>

<div class="panel">
  <h2>Search</h2>

  <% if (error != null) { %>
    <p class="error"><%= Html.escape(error) %></p>
  <% } %>

  <% if (tracks.isEmpty()) { %>
    <p class="hint">Your team has no events on record yet, so there is nothing to search.</p>
  <% } else { %>
    <form method="get" action="<%= ctx %>/app/setups">
      <p>
        <label for="trackId">Track</label><br>
        <select id="trackId" name="trackId" required>
          <option value="">-- choose a track --</option>
          <% for (Track t : tracks) {
               boolean on = String.valueOf(t.getTrackId()).equals(selTrack); %>
            <option value="<%= t.getTrackId() %>" <%= on ? "selected" : "" %>>
              <%= Html.escape(t.getTrackName()) %>
            </option>
          <% } %>
        </select>
      </p>
      <p>
        <label for="carClass">Car class</label><br>
        <select id="carClass" name="carClass">
          <option value="">Any class</option>
          <% for (String cc : carClasses) { %>
            <option value="<%= Html.escape(cc) %>" <%= cc.equals(selClass) ? "selected" : "" %>>
              <%= Html.escape(cc) %>
            </option>
          <% } %>
        </select>
      </p>
      <p>
        <label for="weather">Weather</label><br>
        <select id="weather" name="weather">
          <option value="">Any weather</option>
          <%-- weather is free text in sessions, so the choices are whatever
               this team has actually recorded. --%>
          <% for (String w : weatherOptions) { %>
            <option value="<%= Html.escape(w) %>" <%= w.equals(selWeather) ? "selected" : "" %>>
              <%= Html.escape(w) %>
            </option>
          <% } %>
        </select>
      </p>
      <p>
        <button type="submit">Search</button>
      </p>
    </form>
  <% } %>
</div>

<% if (results != null) { %>
<div class="panel">
  <h2>Result</h2>

  <% if (results.isEmpty()) { %>
    <p class="hint">
      No laps on record for that combination. Try removing the car class or the
      weather filter.
    </p>
  <% } else { %>
    <p class="hint">
      <%= results.size() %> setup(s), fastest first. The top row is the one to start from.
    </p>

    <table>
      <tr>
        <th>#</th>
        <th>Setup</th>
        <th>Car</th>
        <th>Class</th>
        <th>Weather</th>
        <th>Best lap</th>
        <th>Sessions</th>
        <th>Laps</th>
        <th>F wing</th>
        <th>R wing</th>
        <th>Ride ht</th>
        <th>Pressure</th>
        <th>Spring</th>
      </tr>

      <% int rank = 1;
         for (SetupResult r : results) { %>
        <tr>
          <td><%= rank++ %></td>
          <td><%= Html.escape(r.getSetupName()) %></td>
          <td><%= Html.escape(r.getChassisNo()) %></td>
          <td><%= Html.escape(r.getCarClass()) %></td>
          <td><%= Html.escape(r.getWeather()) %></td>
          <td><strong><%= r.getBestLapFormatted() %></strong></td>
          <td><%= r.getSessionCount() %></td>
          <td><%= r.getLapCount() %></td>
          <td><%= r.getFrontWingAngle()  == null ? "" : r.getFrontWingAngle().toString() %></td>
          <td><%= r.getRearWingAngle()   == null ? "" : r.getRearWingAngle().toString() %></td>
          <td><%= r.getRideHeightMm()    == null ? "" : r.getRideHeightMm().toString() %></td>
          <td><%= r.getTirePressurePsi() == null ? "" : r.getTirePressurePsi().toString() %></td>
          <td><%= r.getSpringRate()      == null ? "" : r.getSpringRate().toString() %></td>
        </tr>
      <% } %>
    </table>
  <% } %>
</div>
<% } %>

</body>
</html>

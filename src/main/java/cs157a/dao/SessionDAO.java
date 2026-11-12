package cs157a.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import cs157a.model.DriverOption;
import cs157a.model.SessionRow;
import cs157a.util.Db;

/** FR6 - the SQL for the sessions a team has run. */
public class SessionDAO {

    /**
     * The columns every session list needs. sessions only stores ids, so four
     * JOINs turn them into names an engineer can read. car_setups is a LEFT
     * JOIN because setup_id is allowed to be empty: a session can be recorded
     * before anybody writes the setup sheet down.
     *
     * The two sub-queries add the lap count and the best valid lap for each
     * session. Doing it this way keeps the main query free of GROUP BY, which
     * would otherwise have to list every selected column.
     */
    private static final String SELECT_SESSION =
              "SELECT s.session_id, s.event_id, e.event_name, t.track_name, "
            + "       c.chassis_no, u.display_name AS driver_name, "
            + "       cs.setup_name, s.session_type, s.session_date, "
            + "       s.weather, s.air_temp_c, s.driver_feedback, "
            + "       (SELECT COUNT(*) FROM laps l "
            + "         WHERE l.session_id = s.session_id) AS lap_count, "
            + "       (SELECT MIN(l.lap_time_sec) FROM laps l "
            + "         WHERE l.session_id = s.session_id "
            + "           AND l.is_valid = 1) AS best_lap_sec "
            + "FROM sessions s "
            + "JOIN events e ON s.event_id = e.event_id "
            + "JOIN tracks t ON e.track_id = t.track_id "
            + "JOIN cars c ON s.car_id = c.car_id "
            + "JOIN users u ON s.driver_user_id = u.user_id "
            + "LEFT JOIN car_setups cs ON s.setup_id = cs.setup_id ";

    /** Every session of one team, newest first. */
    public List<SessionRow> findByTeam(int teamId) throws SQLException {

        String sql = SELECT_SESSION
                   + "WHERE e.team_id = ? "
                   + "ORDER BY s.session_date DESC, s.session_id DESC";

        List<SessionRow> rows = new ArrayList<>();

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapRow(rs));
                }
            }
        }
        return rows;
    }

    /**
     * One session, but only when it belongs to this team. Used before writing
     * lap times or tire usage, so an engineer cannot reach another team
     * session by typing its id into the address bar. Returns null when there
     * is no such session for that team.
     */
    public SessionRow findByIdForTeam(int sessionId, int teamId) throws SQLException {

        String sql = SELECT_SESSION + "WHERE s.session_id = ? AND e.team_id = ?";

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, sessionId);
            ps.setInt(2, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /**
     * Records one session and returns its new id.
     *
     * driver_user_id points at the drivers table, so the database itself
     * refuses a session whose driver is really an engineer or a manager.
     * setupId may be null, which is why setObject is used instead of setInt.
     */
    public int insert(int eventId, int carId, int driverUserId, Integer setupId,
                      String sessionType, Date sessionDate, String weather,
                      BigDecimal airTempC, String driverFeedback) throws SQLException {

        String sql = "INSERT INTO sessions "
                   + "(event_id, car_id, setup_id, driver_user_id, session_type, "
                   + " session_date, weather, air_temp_c, driver_feedback) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, eventId);
            ps.setInt(2, carId);
            ps.setObject(3, setupId, Types.INTEGER);
            ps.setInt(4, driverUserId);
            ps.setString(5, sessionType);
            ps.setDate(6, sessionDate);
            ps.setString(7, weather);
            ps.setBigDecimal(8, airTempC);
            ps.setString(9, driverFeedback);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    /** Saves the driver feedback an engineer types in after a session. */
    public void updateFeedback(int sessionId, int teamId, String feedback) throws SQLException {

        // The sub-query is what keeps the team check in a single statement:
        // the row is only touched when its event belongs to this team.
        String sql = "UPDATE sessions SET driver_feedback = ? "
                   + "WHERE session_id = ? "
                   + "  AND event_id IN (SELECT event_id FROM events WHERE team_id = ?)";

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, feedback);
            ps.setInt(2, sessionId);
            ps.setInt(3, teamId);
            ps.executeUpdate();
        }
    }

    /**
     * The drivers on one team, for the drop-down on the session form.
     *
     * A driver is a user who also has a row in drivers, so the JOIN is what
     * picks them out: an engineer or a manager simply has no matching row.
     */
    public List<DriverOption> findDriversForTeam(int teamId) throws SQLException {

        String sql = "SELECT u.user_id, u.display_name, d.driver_number "
                   + "FROM users u "
                   + "JOIN drivers d ON u.user_id = d.user_id "
                   + "WHERE u.team_id = ? AND u.active = 1 "
                   + "ORDER BY d.driver_number";

        List<DriverOption> drivers = new ArrayList<>();

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DriverOption d = new DriverOption();
                    d.setUserId(rs.getInt("user_id"));
                    d.setDisplayName(rs.getString("display_name"));
                    d.setDriverNumber(rs.getInt("driver_number"));
                    drivers.add(d);
                }
            }
        }
        return drivers;
    }

    private SessionRow mapRow(ResultSet rs) throws SQLException {
        SessionRow r = new SessionRow();
        r.setSessionId(rs.getInt("session_id"));
        r.setEventId(rs.getInt("event_id"));
        r.setEventName(rs.getString("event_name"));
        r.setTrackName(rs.getString("track_name"));
        r.setChassisNo(rs.getString("chassis_no"));
        r.setDriverName(rs.getString("driver_name"));
        r.setSetupName(rs.getString("setup_name"));
        r.setSessionType(rs.getString("session_type"));
        r.setSessionDate(rs.getDate("session_date"));
        r.setWeather(rs.getString("weather"));
        r.setAirTempC(rs.getBigDecimal("air_temp_c"));
        r.setDriverFeedback(rs.getString("driver_feedback"));
        r.setLapCount(rs.getInt("lap_count"));
        r.setBestLapSec(rs.getBigDecimal("best_lap_sec"));
        return r;
    }
}

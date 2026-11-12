package cs157a.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cs157a.model.Event;
import cs157a.model.SessionSummary;
import cs157a.util.Db;

/** FR5 - the SQL for a team's race weekends and test days. */
public class EventDAO {

    /**
     * The whole calendar for one team, soonest first.
     *
     * The JOIN brings in the track name: events only stores track_id, but the
     * page has to show something a person can read.
     */
    public List<Event> findByTeam(int teamId) throws SQLException {

        String sql = "SELECT e.event_id, e.event_name, e.track_id, t.track_name, "
                   + "       e.start_date, e.end_date, e.event_type "
                   + "FROM events e "
                   + "JOIN tracks t ON e.track_id = t.track_id "
                   + "WHERE e.team_id = ? "
                   + "ORDER BY e.start_date";

        List<Event> events = new ArrayList<>();

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    events.add(mapRow(rs));
                }
            }
        }
        return events;
    }

    /**
     * One event, but only if it belongs to this team. The team id is in the
     * WHERE clause so a manager cannot open another team's event by guessing
     * its id. Returns null when there is no such event for that team.
     */
    public Event findByIdForTeam(int eventId, int teamId) throws SQLException {

        String sql = "SELECT e.event_id, e.event_name, e.track_id, t.track_name, "
                   + "       e.start_date, e.end_date, e.event_type "
                   + "FROM events e "
                   + "JOIN tracks t ON e.track_id = t.track_id "
                   + "WHERE e.event_id = ? AND e.team_id = ?";

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ps.setInt(2, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /** Adds one event to the manager's calendar. */
    public void insert(int teamId, Event event) throws SQLException {

        String sql = "INSERT INTO events "
                   + "(event_name, track_id, team_id, start_date, end_date, event_type) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, event.getEventName());
            ps.setInt(2, event.getTrackId());
            ps.setInt(3, teamId);
            ps.setDate(4, event.getStartDate());
            ps.setDate(5, event.getEndDate());
            ps.setString(6, event.getEventType());
            ps.executeUpdate();
        }
    }

    /**
     * The sessions that were run at one event.
     *
     * Three JOINs here: the driver's name is in users, the car's chassis
     * number is in cars, and sessions only stores their ids.
     */
    public List<SessionSummary> findSessionsForEvent(int eventId) throws SQLException {

        String sql = "SELECT s.session_date, s.session_type, s.weather, "
                   + "       u.display_name AS driver_name, c.chassis_no "
                   + "FROM sessions s "
                   + "JOIN users u ON s.driver_user_id = u.user_id "
                   + "JOIN cars c  ON s.car_id = c.car_id "
                   + "WHERE s.event_id = ? "
                   + "ORDER BY s.session_date, s.session_id";

        List<SessionSummary> list = new ArrayList<>();

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SessionSummary s = new SessionSummary();
                    s.setSessionDate(rs.getDate("session_date"));
                    s.setSessionType(rs.getString("session_type"));
                    s.setWeather(rs.getString("weather"));
                    s.setDriverName(rs.getString("driver_name"));
                    s.setChassisNo(rs.getString("chassis_no"));
                    list.add(s);
                }
            }
        }
        return list;
    }

    private Event mapRow(ResultSet rs) throws SQLException {
        Event e = new Event();
        e.setEventId(rs.getInt("event_id"));
        e.setEventName(rs.getString("event_name"));
        e.setTrackId(rs.getInt("track_id"));
        e.setTrackName(rs.getString("track_name"));
        e.setStartDate(rs.getDate("start_date"));
        e.setEndDate(rs.getDate("end_date"));
        e.setEventType(rs.getString("event_type"));
        return e;
    }
}

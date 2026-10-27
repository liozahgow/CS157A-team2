package cs157a.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cs157a.model.SetupResult;
import cs157a.model.Track;
import cs157a.util.Db;

/**
 * FR9 - the best-setup search.
 *
 * This is the query the whole database exists for. Before an event an engineer
 * asks "what did we run here last time, and how fast was it", and the answer
 * has to come from six tables at once:
 *
 *   sessions    the time the car went out
 *   events      which weekend that session belonged to
 *   tracks      which circuit the event was at
 *   cars        which class of car it was
 *   car_setups  the wing angles, ride height and pressures used
 *   laps        the lap times that setup actually produced
 *
 * The rows are grouped by setup and ordered by the fastest valid lap, so the
 * first row is the setup to copy.
 */
public class SetupSearchDAO {

    /**
     * Ranked setups for one track.
     *
     * carClass and weather are optional filters: pass null to ignore either.
     * teamId is never optional, because an engineer must not see the setups
     * another team has found.
     */
    public List<SetupResult> findBestSetups(int teamId, int trackId,
                                            String carClass, String weather) throws SQLException {

        StringBuilder sql = new StringBuilder(
              "SELECT cs.setup_id, cs.setup_name, t.track_name, c.car_class, c.chassis_no, "
            + "       s.weather, "
            + "       cs.front_wing_angle, cs.rear_wing_angle, cs.ride_height_mm, "
            + "       cs.tire_pressure_psi, cs.spring_rate, "
            + "       MIN(l.lap_time_sec)        AS best_lap_sec, "
            + "       COUNT(DISTINCT s.session_id) AS session_count, "
            + "       COUNT(l.lap_number)        AS lap_count "
            + "FROM sessions s "
            + "JOIN events e      ON s.event_id = e.event_id "
            + "JOIN tracks t      ON e.track_id = t.track_id "
            + "JOIN cars c        ON s.car_id = c.car_id "
            + "JOIN car_setups cs ON s.setup_id = cs.setup_id "
            + "JOIN laps l        ON l.session_id = s.session_id "
            + "WHERE e.team_id = ? "            // one team only
            + "  AND t.track_id = ? "
            + "  AND l.is_valid = 1 ");         // a lap that ran wide does not count

        // The filters are optional, so the ? placeholders are added only when
        // they are used. The values are still bound, never glued into the text.
        if (carClass != null) {
            sql.append("  AND c.car_class = ? ");
        }
        if (weather != null) {
            sql.append("  AND s.weather = ? ");
        }

        sql.append("GROUP BY cs.setup_id, cs.setup_name, t.track_name, c.car_class, "
                 + "         c.chassis_no, s.weather, cs.front_wing_angle, cs.rear_wing_angle, "
                 + "         cs.ride_height_mm, cs.tire_pressure_psi, cs.spring_rate "
                 + "ORDER BY best_lap_sec");

        List<SetupResult> out = new ArrayList<>();

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int i = 1;
            ps.setInt(i++, teamId);
            ps.setInt(i++, trackId);
            if (carClass != null) {
                ps.setString(i++, carClass);
            }
            if (weather != null) {
                ps.setString(i++, weather);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SetupResult r = new SetupResult();
                    r.setSetupId(rs.getInt("setup_id"));
                    r.setSetupName(rs.getString("setup_name"));
                    r.setTrackName(rs.getString("track_name"));
                    r.setCarClass(rs.getString("car_class"));
                    r.setChassisNo(rs.getString("chassis_no"));
                    r.setWeather(rs.getString("weather"));
                    r.setBestLapSec(rs.getBigDecimal("best_lap_sec"));
                    r.setSessionCount(rs.getInt("session_count"));
                    r.setLapCount(rs.getInt("lap_count"));
                    r.setFrontWingAngle(rs.getBigDecimal("front_wing_angle"));
                    r.setRearWingAngle(rs.getBigDecimal("rear_wing_angle"));
                    r.setRideHeightMm(rs.getBigDecimal("ride_height_mm"));
                    r.setTirePressurePsi(rs.getBigDecimal("tire_pressure_psi"));
                    r.setSpringRate(rs.getBigDecimal("spring_rate"));
                    out.add(r);
                }
            }
        }
        return out;
    }

    /** The tracks this team has actually been to, for the drop-down. */
    public List<Track> findTracksUsedByTeam(int teamId) throws SQLException {

        String sql = "SELECT DISTINCT t.track_id, t.track_name, t.location, t.length_km, t.turns "
                   + "FROM tracks t "
                   + "JOIN events e ON e.track_id = t.track_id "
                   + "WHERE e.team_id = ? "
                   + "ORDER BY t.track_name";

        List<Track> tracks = new ArrayList<>();

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Track t = new Track();
                    t.setTrackId(rs.getInt("track_id"));
                    t.setTrackName(rs.getString("track_name"));
                    t.setLocation(rs.getString("location"));
                    t.setLengthKm(rs.getBigDecimal("length_km"));
                    t.setTurns(rs.getInt("turns"));
                    tracks.add(t);
                }
            }
        }
        return tracks;
    }

    /**
     * The weather values this team has actually recorded, for the drop-down.
     * weather is free text in the sessions table, so the only honest way to
     * offer choices is to read back what has been typed.
     */
    public List<String> findWeatherUsedByTeam(int teamId) throws SQLException {

        String sql = "SELECT DISTINCT s.weather "
                   + "FROM sessions s "
                   + "JOIN events e ON s.event_id = e.event_id "
                   + "WHERE e.team_id = ? AND s.weather IS NOT NULL "
                   + "ORDER BY s.weather";

        List<String> weather = new ArrayList<>();

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    weather.add(rs.getString("weather"));
                }
            }
        }
        return weather;
    }

    /** The car classes this team has run, for the drop-down. */
    public List<String> findCarClassesUsedByTeam(int teamId) throws SQLException {

        String sql = "SELECT DISTINCT car_class FROM cars WHERE team_id = ? ORDER BY car_class";

        List<String> classes = new ArrayList<>();

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    classes.add(rs.getString("car_class"));
                }
            }
        }
        return classes;
    }
}

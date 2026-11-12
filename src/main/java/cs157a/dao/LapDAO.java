package cs157a.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cs157a.model.Lap;
import cs157a.util.Db;

/**
 * FR8 - the SQL for lap times.
 *
 * laps is a weak entity owned by sessions. Its key is (session_id,
 * lap_number), and lap_number only has to be unique inside its own session,
 * so every statement here works on the pair.
 */
public class LapDAO {

    /** Every lap of one session, in the order they were driven. */
    public List<Lap> findBySession(int sessionId) throws SQLException {

        String sql = "SELECT session_id, lap_number, lap_time_sec, is_valid "
                   + "FROM laps "
                   + "WHERE session_id = ? "
                   + "ORDER BY lap_number";

        List<Lap> laps = new ArrayList<>();

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, sessionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Lap l = new Lap();
                    l.setSessionId(rs.getInt("session_id"));
                    l.setLapNumber(rs.getInt("lap_number"));
                    l.setLapTimeSec(rs.getDouble("lap_time_sec"));
                    l.setValid(rs.getBoolean("is_valid"));
                    laps.add(l);
                }
            }
        }
        return laps;
    }

    /**
     * The next free lap number for one session.
     *
     * Lap numbers restart at 1 in every session, so this is the highest
     * number used in THIS session plus one. MAX over no rows is SQL NULL,
     * which getInt reports as 0, so wasNull tells us the session is empty.
     */
    public int nextLapNumber(Connection con, int sessionId) throws SQLException {

        String sql = "SELECT MAX(lap_number) AS highest FROM laps WHERE session_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, sessionId);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                int highest = rs.getInt("highest");
                return rs.wasNull() ? 1 : highest + 1;
            }
        }
    }

    /** Writes one lap. The caller supplies the Connection so a whole batch is one transaction. */
    public void insert(Connection con, int sessionId, int lapNumber,
                       double lapTimeSec, boolean valid) throws SQLException {

        String sql = "INSERT INTO laps (session_id, lap_number, lap_time_sec, is_valid) "
                   + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            ps.setInt(2, lapNumber);
            ps.setDouble(3, lapTimeSec);
            ps.setBoolean(4, valid);
            ps.executeUpdate();
        }
    }

    /**
     * Best, average and count over the valid laps of one session.
     *
     * An invalid lap is one where the car ran wide, so it must not be allowed
     * to look like the fastest lap of the session.
     * Returns null when the session has no valid laps at all.
     */
    public LapStats statsForSession(int sessionId) throws SQLException {

        String sql = "SELECT COUNT(*) AS lap_count, "
                   + "       MIN(lap_time_sec) AS best_lap, "
                   + "       AVG(lap_time_sec) AS average_lap "
                   + "FROM laps "
                   + "WHERE session_id = ? AND is_valid = 1";

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, sessionId);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                int count = rs.getInt("lap_count");
                if (count == 0) {
                    return null;
                }
                LapStats s = new LapStats();
                s.count   = count;
                s.best    = rs.getDouble("best_lap");
                s.average = rs.getDouble("average_lap");
                return s;
            }
        }
    }

    /** The three numbers FR8 asks for at the bottom of the lap table. */
    public static class LapStats {
        public int    count;
        public double best;
        public double average;

        public String getBestFormatted()    { return format(best); }
        public String getAverageFormatted() { return format(average); }

        private static String format(double seconds) {
            int    minutes = (int) seconds / 60;
            double rest    = seconds - minutes * 60;
            return minutes > 0 ? String.format("%d:%06.3f", minutes, rest)
                               : String.format("%.3f", rest);
        }
    }
}

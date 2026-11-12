package cs157a.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cs157a.model.TireUsage;
import cs157a.util.Db;

/**
 * FR7 - the SQL for which tire set ran in which session.
 *
 * session_tire_usages is the many-to-many between sessions and tire_sets, so
 * its key is the session id together with the tire set key. tire_sets is a
 * weak entity, so that key is two columns: team_id and set_no.
 */
public class TireUsageDAO {

    /**
     * Everything one team has recorded, newest session first.
     *
     * The JOINs bring in the readable parts: which session it was, at which
     * event, and what compound the set is.
     */
    public List<TireUsage> findByTeam(int teamId) throws SQLException {

        String sql = "SELECT stu.session_id, stu.team_id, stu.set_no, stu.laps_run, "
                   + "       s.session_type, s.session_date, e.event_name, "
                   + "       ts.compound, ts.heat_cycles, ts.max_heat_cycles, ts.status "
                   + "FROM session_tire_usages stu "
                   + "JOIN sessions s ON stu.session_id = s.session_id "
                   + "JOIN events e ON s.event_id = e.event_id "
                   + "JOIN tire_sets ts ON stu.team_id = ts.team_id "
                   + "                 AND stu.set_no = ts.set_no "
                   + "WHERE stu.team_id = ? "
                   + "ORDER BY s.session_date DESC, stu.set_no";

        List<TireUsage> list = new ArrayList<>();

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TireUsage u = new TireUsage();
                    u.setSessionId(rs.getInt("session_id"));
                    u.setTeamId(rs.getInt("team_id"));
                    u.setSetNo(rs.getInt("set_no"));
                    u.setLapsRun(rs.getInt("laps_run"));
                    u.setSessionType(rs.getString("session_type"));
                    u.setSessionDate(rs.getDate("session_date"));
                    u.setEventName(rs.getString("event_name"));
                    u.setCompound(rs.getString("compound"));
                    u.setHeatCycles(rs.getInt("heat_cycles"));
                    u.setMaxHeatCycles(rs.getInt("max_heat_cycles"));
                    u.setStatus(rs.getString("status"));
                    list.add(u);
                }
            }
        }
        return list;
    }

    /**
     * True when this set has already been recorded against this session.
     * The primary key would reject a second row anyway, but checking first
     * lets the page show a sentence instead of a database error.
     */
    public boolean exists(Connection con, int sessionId, int teamId, int setNo) throws SQLException {

        String sql = "SELECT 1 FROM session_tire_usages "
                   + "WHERE session_id = ? AND team_id = ? AND set_no = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            ps.setInt(2, teamId);
            ps.setInt(3, setNo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Writes the usage row itself. The heat cycle update is a separate step. */
    public void insert(Connection con, int sessionId, int teamId, int setNo, int lapsRun)
            throws SQLException {

        String sql = "INSERT INTO session_tire_usages "
                   + "(session_id, team_id, set_no, laps_run) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            ps.setInt(2, teamId);
            ps.setInt(3, setNo);
            ps.setInt(4, lapsRun);
            ps.executeUpdate();
        }
    }
}

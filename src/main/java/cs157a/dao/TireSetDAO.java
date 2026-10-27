package cs157a.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cs157a.model.TireSet;
import cs157a.util.Db;

/**
 * FR4 - the SQL for a team's tire sets.
 *
 * tire_sets is a weak entity owned by teams, so every statement here works on
 * the pair (team_id, set_no). There is no id that identifies a set on its own.
 */
public class TireSetDAO {

    /** Every tire set of one team, newest set number last. */
    public List<TireSet> findByTeam(int teamId, boolean includeRetired) throws SQLException {

        String sql = "SELECT team_id, set_no, compound, tire_size, received_date, "
                   + "       heat_cycles, max_heat_cycles, status "
                   + "FROM tire_sets "
                   + "WHERE team_id = ? ";

        if (!includeRetired) {
            sql = sql + "AND status <> 'Retired' ";
        }
        sql = sql + "ORDER BY set_no";

        List<TireSet> sets = new ArrayList<>();

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sets.add(mapRow(rs));
                }
            }
        }
        return sets;
    }

    /**
     * The next free set number for this team.
     *
     * A team numbers its own sets from 1 upwards, and the numbers only have to
     * be unique inside that team, so this is MAX + 1 within the team. Returns
     * 1 when the team has no sets yet, because MAX of nothing is NULL.
     */
    public int nextSetNo(Connection con, int teamId) throws SQLException {

        String sql = "SELECT COALESCE(MAX(set_no), 0) + 1 FROM tire_sets WHERE team_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    /**
     * Adds one tire set and returns the set number it was given.
     *
     * Picking the number and inserting the row happen on one Connection inside
     * one transaction, so two managers adding a set at the same moment cannot
     * both be handed the same number.
     * A new set always starts at zero heat cycles with the status New.
     */
    public int insert(TireSet set) throws SQLException {

        String sql = "INSERT INTO tire_sets "
                   + "(team_id, set_no, compound, tire_size, received_date, "
                   + " heat_cycles, max_heat_cycles, status) "
                   + "VALUES (?, ?, ?, ?, ?, 0, ?, 'New')";

        try (Connection con = Db.get()) {

            con.setAutoCommit(false);

            try {
                int setNo = nextSetNo(con, set.getTeamId());

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, set.getTeamId());
                    ps.setInt(2, setNo);
                    ps.setString(3, set.getCompound());
                    ps.setString(4, set.getTireSize());
                    ps.setDate(5, set.getReceivedDate());
                    ps.setInt(6, set.getMaxHeatCycles());
                    ps.executeUpdate();
                }

                con.commit();
                return setNo;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    /**
     * Retires a set by hand, or puts a retired set back into use.
     * Both key columns are in the WHERE clause, which is what identifying a
     * weak entity means in practice.
     */
    public void setStatus(int teamId, int setNo, String status) throws SQLException {

        String sql = "UPDATE tire_sets SET status = ? WHERE team_id = ? AND set_no = ?";

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, teamId);
            ps.setInt(3, setNo);
            ps.executeUpdate();
        }
    }

    private TireSet mapRow(ResultSet rs) throws SQLException {
        TireSet t = new TireSet();
        t.setTeamId(rs.getInt("team_id"));
        t.setSetNo(rs.getInt("set_no"));
        t.setCompound(rs.getString("compound"));
        t.setTireSize(rs.getString("tire_size"));
        t.setReceivedDate(rs.getDate("received_date"));
        t.setHeatCycles(rs.getInt("heat_cycles"));
        t.setMaxHeatCycles(rs.getInt("max_heat_cycles"));
        t.setStatus(rs.getString("status"));
        return t;
    }
}

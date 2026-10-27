package cs157a.dao;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cs157a.model.Team;
import cs157a.util.Db;

/**
 * All SQL statements that touch the teams table
 *
 * Project rule: no SELECT / INSERT / UPDATE outside cs157a.dao.
 */
public class TeamDAO {

    private static final SecureRandom RANDOM = new SecureRandom();

    public List<Team> findAll() throws SQLException {

        String sql = "SELECT team_id, team_name, series, invite_code, status, created_at "
                   + "FROM teams "
                   + "ORDER BY team_id";

        List<Team> teams = new ArrayList<>();

        // try-with-resources closes all three in reverse order, even on an exception.
        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                teams.add(mapRow(rs));
            }
        }
        return teams;
    }

    /**
     * Looks up the team a new driver or engineer wants to join.
     * Returns null when no team has that invite code.
     */
    public Team findByInviteCode(String inviteCode) throws SQLException {

        String sql = "SELECT team_id, team_name, series, invite_code, status, created_at "
                   + "FROM teams "
                   + "WHERE invite_code = ?";

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, inviteCode);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /** True when a team already uses this name. team_name is UNIQUE. */
    public boolean nameExists(String teamName) throws SQLException {

        String sql = "SELECT 1 FROM teams WHERE team_name = ?";

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, teamName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Creates a new team for a registering manager and returns its team_id.
     *
     * Takes the caller's Connection instead of opening its own, so the INSERT
     * runs inside the same transaction as the manager's users row - see
     * UserDAO.register. The team starts as 'Pending' until the administrator
     * approves it.
     */
    public int insert(Connection con, String teamName) throws SQLException {

        String sql = "INSERT INTO teams (team_name, invite_code, status) "
                   + "VALUES (?, ?, 'Pending')";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, teamName);
            ps.setString(2, newInviteCode(con));
            ps.executeUpdate();

            // AUTO_INCREMENT picked the team_id; ask the driver what it was.
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    /**
     * Makes a random 8-character code such as "K7Q2MX9A" that no team uses yet.
     * Letters that look alike (O/0, I/1) are left out so the code is easy to
     * read out loud to a new driver.
     */
    private String newInviteCode(Connection con) throws SQLException {

        String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        String sql      = "SELECT 1 FROM teams WHERE invite_code = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            while (true) {
                StringBuilder code = new StringBuilder();
                for (int i = 0; i < 8; i++) {
                    code.append(alphabet.charAt(RANDOM.nextInt(alphabet.length())));
                }

                ps.setString(1, code.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return code.toString();    // unused, take it
                    }
                }
            }
        }
    }

    /** Copies the current row of a ResultSet into a Team object. */
    private Team mapRow(ResultSet rs) throws SQLException {
        Team t = new Team();
        t.setTeamId(rs.getInt("team_id"));
        t.setTeamName(rs.getString("team_name"));
        t.setSeries(rs.getString("series"));
        t.setInviteCode(rs.getString("invite_code"));
        t.setStatus(rs.getString("status"));
        t.setCreatedAt(rs.getTimestamp("created_at"));
        return t;
    }
}

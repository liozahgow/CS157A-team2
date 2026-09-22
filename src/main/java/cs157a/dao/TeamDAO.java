package cs157a.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cs157a.model.Team;
import cs157a.util.Db;

/**
 * Every SQL statement that touches the teams table lives here.
 *
 * Rule for the whole project: no SELECT / INSERT / UPDATE outside cs157a.dao.
 * A DAO returns model objects, never a ResultSet - a ResultSet stops working
 * the moment the connection closes.
 */
public class TeamDAO {

    /** Loads every team, newest last. Used by the "List all racing teams" button. */
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

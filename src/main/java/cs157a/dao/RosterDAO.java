package cs157a.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cs157a.model.RosterMember;
import cs157a.util.Db;

/** FR3 - the SQL for the team roster. */
public class RosterDAO {

    /**
     * Everyone on one team. LEFT JOIN keeps every user, even though each
     * user only has a row in ONE of the three role tables.
     * Members who left the team (active = 0) are skipped unless showInactive is true.
     */
    public List<RosterMember> findByTeam(int teamId, boolean showInactive) throws SQLException {

        String sql = "SELECT u.user_id, u.display_name, u.email, u.role, u.active, "
                   + "       d.driver_number, re.specialty, tm.office_phone "
                   + "FROM users u "
                   + "LEFT JOIN drivers d         ON u.user_id = d.user_id "
                   + "LEFT JOIN race_engineers re ON u.user_id = re.user_id "
                   + "LEFT JOIN team_managers tm  ON u.user_id = tm.user_id "
                   + "WHERE u.team_id = ? ";

        if (!showInactive) {
            sql = sql + "AND u.active = 1 ";
        }

        sql = sql + "ORDER BY u.role, u.display_name";

        List<RosterMember> members = new ArrayList<>();

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RosterMember m = new RosterMember();
                    m.setUserId(rs.getInt("user_id"));
                    m.setDisplayName(rs.getString("display_name"));
                    m.setEmail(rs.getString("email"));
                    m.setRole(rs.getString("role"));
                    m.setActive(rs.getBoolean("active"));

                    // getObject(..., Integer.class) gives null for a NULL column
                    m.setDriverNumber(rs.getObject("driver_number", Integer.class));
                    m.setSpecialty(rs.getString("specialty"));
                    m.setOfficePhone(rs.getString("office_phone"));

                    members.add(m);
                }
            }
        }
        return members;
    }
}
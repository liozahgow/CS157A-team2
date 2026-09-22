package cs157a.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import cs157a.model.User;
import cs157a.util.Db;
import cs157a.util.PasswordHasher;

/**
 * Every SQL statement that touches the users table lives here.
 */
public class UserDAO {

    /**
     * Checks one login attempt.
     *
     * Reads the stored hash for that email and asks PasswordHasher whether the
     * attempt matches. Returns the User on success, or null when the email is
     * unknown, the account is deactivated, or the password is wrong - the
     * caller shows the same message for all three so an attacker cannot use
     * the error text to discover which emails exist.
     */
    public User findByEmailAndPassword(String email, String plainPassword) throws SQLException {

        String sql = "SELECT user_id, team_id, email, password_hash, display_name, "
                   + "       phone, driver_number, role, active "
                   + "FROM users "
                   + "WHERE email = ? AND active = 1";

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // The ? placeholder is what stops SQL injection. Never build a
            // query by gluing user input onto the string.
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;                       // no such active account
                }

                String storedHash = rs.getString("password_hash");
                if (!PasswordHasher.verify(plainPassword, storedHash)) {
                    return null;                       // wrong password
                }
                return mapRow(rs);
            }
        }
    }

    /** Copies the current row of a ResultSet into a User object. */
    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));

        // getInt returns 0 for a NULL column, so ask wasNull() to tell the
        // difference between "team 0" and "no team" (the administrator).
        int teamId = rs.getInt("team_id");
        u.setTeamId(rs.wasNull() ? null : teamId);

        u.setEmail(rs.getString("email"));
        u.setDisplayName(rs.getString("display_name"));
        u.setPhone(rs.getString("phone"));

        int driverNumber = rs.getInt("driver_number");
        u.setDriverNumber(rs.wasNull() ? null : driverNumber);

        u.setRole(rs.getString("role"));
        u.setActive(rs.getBoolean("active"));
        return u;
    }
}

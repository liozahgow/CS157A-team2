package cs157a.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

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
     * unknown, the account is deactivated, or the password is wrong, the
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

            // The ? placeholder stops SQL injection
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;                       
                }

                String storedHash = rs.getString("password_hash");
                if (!PasswordHasher.verify(plainPassword, storedHash)) {
                    return null;                      
                }
                return mapRow(rs);
            }
        }
    }

    /** True when an account already uses this email. email is UNIQUE. */
    public boolean emailExists(String email) throws SQLException {

        String sql = "SELECT 1 FROM users WHERE email = ?";

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    
    public int register(User user, String plainPassword, String newTeamName) throws SQLException {

        try (Connection con = Db.get()) {

            // Turn off auto-commit so nothing is saved until commit() below.
            con.setAutoCommit(false);

            try {
                if (newTeamName != null) {
                    user.setTeamId(new TeamDAO().insert(con, newTeamName));
                }

                int userId = insertUser(con, user, PasswordHasher.hash(plainPassword));
                insertRoleRow(con, userId, user);

                con.commit();
                user.setUserId(userId);
                return userId;

            } catch (SQLException e) {
                con.rollback();              
                throw e;
            }
        }
    }

    /** Step 2 of register: the users row. Returns the generated user_id. */
    private int insertUser(Connection con, User user, String passwordHash) throws SQLException {

        String sql = "INSERT INTO users "
                   + "(team_id, email, password_hash, display_name, phone, driver_number, role) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, user.getTeamId());
            ps.setString(2, user.getEmail());
            ps.setString(3, passwordHash);
            ps.setString(4, user.getDisplayName());
            ps.setString(5, user.getPhone());

            // setObject writes SQL NULL for a null Integer; setInt cannot.
            ps.setObject(6, user.getDriverNumber(), Types.INTEGER);
            ps.setString(7, user.getRole());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    /**
     * Step 3 of register: the subtype row. Only the key (and the driver
     * number, which is NOT NULL) is known at sign-up
     */
    private void insertRoleRow(Connection con, int userId, User user) throws SQLException {

        String sql = switch (user.getRole()) {
            case "Driver"   -> "INSERT INTO drivers (user_id, driver_number) VALUES (?, ?)";
            case "Engineer" -> "INSERT INTO race_engineers (user_id) VALUES (?)";
            case "Manager"  -> "INSERT INTO team_managers (user_id) VALUES (?)";
            default -> throw new IllegalArgumentException("Cannot register role " + user.getRole());
        };

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            if ("Driver".equals(user.getRole())) {
                ps.setInt(2, user.getDriverNumber());
            }
            ps.executeUpdate();
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

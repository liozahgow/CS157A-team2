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

        // driver_number lives in the drivers table, which is where the design
        // report puts it. The LEFT JOIN brings it back for a driver and leaves
        // it NULL for everybody else, so the rest of the app is unaffected.
        String sql = "SELECT u.user_id, u.team_id, u.email, u.password_hash, u.display_name, "
                   + "       u.phone, d.driver_number, u.role, u.active "
                   + "FROM users u "
                   + "LEFT JOIN drivers d ON u.user_id = d.user_id "
                   + "WHERE u.email = ? AND u.active = 1";

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

    /**
     * Loads one user by primary key, or null if there is no such row.
     * Used by the profile page so it always shows what is in the database,
     * not what was copied into the session at login.
     */
    public User findById(int userId) throws SQLException {

        String sql = "SELECT u.user_id, u.team_id, u.email, u.display_name, "
                   + "       u.phone, d.driver_number, u.role, u.active "
                   + "FROM users u "
                   + "LEFT JOIN drivers d ON u.user_id = d.user_id "
                   + "WHERE u.user_id = ?";

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /*
     * The methods below take the caller's Connection instead of opening their
     * own, so a service class can run several of them as one transaction -
     * see cs157a.service.RegistrationService and ProfileService.
     */

    /** The stored hash for one user, or null if the user does not exist. */
    public String findPasswordHash(Connection con, int userId) throws SQLException {

        String sql = "SELECT password_hash FROM users WHERE user_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("password_hash") : null;
            }
        }
    }

    /**
     * Saves the editable profile fields that belong to users (FR2): display
     * name and phone. The car number is not here, because it belongs to the
     * drivers table - see updateDriverNumber.
     */
    public void updateProfile(Connection con, User user) throws SQLException {

        String sql = "UPDATE users "
                   + "SET display_name = ?, phone = ? "
                   + "WHERE user_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getDisplayName());
            ps.setString(2, user.getPhone());
            ps.setInt(3, user.getUserId());
            ps.executeUpdate();
        }
    }

    /**
     * Changes a driver's car number. The drivers table is the only place it is
     * stored, so there is nothing to keep in step anywhere else.
     * The column is NOT NULL, so callers must pass a real number.
     */
    public void updateDriverNumber(Connection con, int userId, int driverNumber) throws SQLException {

        String sql = "UPDATE drivers SET driver_number = ? WHERE user_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, driverNumber);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    /** Stores a new password. passwordHash must come from PasswordHasher.hash. */
    public void updatePasswordHash(Connection con, int userId, String passwordHash) throws SQLException {

        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setInt(2, userId);
            ps.executeUpdate();
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

    /** Adds the users row for a new account (FR1). Returns the generated user_id. */
    public int insert(Connection con, User user, String passwordHash) throws SQLException {

        // The car number is not inserted here. insertRoleRow writes it into
        // the drivers table, which is the only place it is stored.
        String sql = "INSERT INTO users "
                   + "(team_id, email, password_hash, display_name, phone, role) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, user.getTeamId());
            ps.setString(2, user.getEmail());
            ps.setString(3, passwordHash);
            ps.setString(4, user.getDisplayName());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getRole());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    /**
     * Adds the subtype row (drivers, race_engineers or team_managers) for a
     * new account. Only the key (and the driver number, which is NOT NULL) is
     * known at sign-up
     */
    public void insertRoleRow(Connection con, int userId, User user) throws SQLException {

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

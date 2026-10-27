package cs157a.service;

import java.sql.Connection;
import java.sql.SQLException;

import cs157a.dao.UserDAO;
import cs157a.model.User;
import cs157a.util.Db;
import cs157a.util.PasswordHasher;

/**
 * FR2 - saves a profile edit as one transaction.
 *
 * The password check and every UPDATE share one Connection, so either the
 * whole edit is saved or none of it is.
 */
public class ProfileService {

    private final UserDAO userDao = new UserDAO();

    /**
     * Returns false, and changes nothing, when currentPassword is wrong.
     *
     * Reads display name, phone and driver number from user. newPassword is
     * null when the user left the new-password box empty.
     *
     * For a driver the number is written to drivers (where FR2 says it lives)
     * and copied to users.driver_number so the two never disagree.
     */
    public boolean updateProfile(User user, String currentPassword, String newPassword)
            throws SQLException {

        try (Connection con = Db.get()) {

            con.setAutoCommit(false);

            try {
                String storedHash = userDao.findPasswordHash(con, user.getUserId());
                if (!PasswordHasher.verify(currentPassword, storedHash)) {
                    con.rollback();
                    return false;
                }

                userDao.updateProfile(con, user);

                if ("Driver".equals(user.getRole())) {
                    userDao.updateDriverNumber(con, user.getUserId(), user.getDriverNumber());
                }

                if (newPassword != null) {
                    userDao.updatePasswordHash(con, user.getUserId(), PasswordHasher.hash(newPassword));
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }
}

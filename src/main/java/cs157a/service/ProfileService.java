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
     * For a driver the car number is written to the drivers table, which is
     * the only place it is stored.
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

                // drivers.driver_number is NOT NULL: every driver has a car
                // number. A null here means the caller did not supply one, so
                // the stored number is left as it was rather than letting the
                // database reject the whole edit.
                if ("Driver".equals(user.getRole()) && user.getDriverNumber() != null) {
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

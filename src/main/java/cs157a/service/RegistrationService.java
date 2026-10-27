package cs157a.service;

import java.sql.Connection;
import java.sql.SQLException;

import cs157a.dao.TeamDAO;
import cs157a.dao.UserDAO;
import cs157a.model.User;
import cs157a.util.Db;
import cs157a.util.PasswordHasher;

/**
 * FR1 - creates a new account as one transaction.
 *
 * A service exists only for actions that need several SQL statements to
 * succeed or fail together. It owns the Connection and the commit/rollback;
 * the SQL itself still lives in the DAOs.
 */
public class RegistrationService {

    private final UserDAO userDao = new UserDAO();
    private final TeamDAO teamDao = new TeamDAO();

    /**
     * Creates the account and returns its user_id.
     *
     * Up to three INSERTs happen here:
     *   1. teams          - managers only, when newTeamName is not null
     *   2. users          - always
     *   3. drivers / race_engineers / team_managers - one of them, by role,
     *                       with the same user_id (ISA subtype tables)
     *
     * Either every row is written or none is. Without the transaction, a
     * failure on step 3 would leave a users row with no subtype row, or a
     * team with no manager.
     *
     * The caller fills in user.teamId for drivers and engineers (from the
     * invite code); for a manager it is set here from the new team.
     */
    public int register(User user, String plainPassword, String newTeamName) throws SQLException {

        try (Connection con = Db.get()) {

            // Turn off auto-commit so nothing is saved until commit() below.
            con.setAutoCommit(false);

            try {
                if (newTeamName != null) {
                    user.setTeamId(teamDao.insert(con, newTeamName));
                }

                int userId = userDao.insert(con, user, PasswordHasher.hash(plainPassword));
                userDao.insertRoleRow(con, userId, user);

                con.commit();
                user.setUserId(userId);
                return userId;

            } catch (SQLException e) {
                con.rollback();               // undo whatever part already ran
                throw e;
            }
        }
    }
}

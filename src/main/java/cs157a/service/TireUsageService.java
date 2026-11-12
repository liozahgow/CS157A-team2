package cs157a.service;

import java.sql.Connection;
import java.sql.SQLException;

import cs157a.dao.TireSetDAO;
import cs157a.dao.TireUsageDAO;
import cs157a.model.TireSet;
import cs157a.util.Db;

/**
 * FR7 - records that a tire set ran in a session, and keeps the tire life up
 * to date.
 *
 * Three things have to happen together:
 *   1. write the usage row
 *   2. add one heat cycle to the set
 *   3. retire the set when it has reached its limit
 *
 * If step 2 failed on its own, the team would have a usage record for a set
 * whose cycle count never moved, and the tire would look fresher than it is.
 * That is exactly the kind of mistake a transaction exists to prevent.
 */
public class TireUsageService {

    private final TireUsageDAO usageDao = new TireUsageDAO();
    private final TireSetDAO   tireDao  = new TireSetDAO();

    /** What happened, so the page can tell the engineer. */
    public static class Result {
        public boolean saved;
        public String  message;
        public boolean retired;      // the set reached its limit on this save
        public boolean nearLimit;    // one cycle left after this save
    }

    public Result record(int teamId, int sessionId, int setNo, int lapsRun) throws SQLException {

        Result result = new Result();

        try (Connection con = Db.get()) {

            con.setAutoCommit(false);

            try {
                TireSet set = tireDao.findOne(con, teamId, setNo);
                if (set == null) {
                    con.rollback();
                    result.message = "Your team has no tire set number " + setNo + ".";
                    return result;
                }
                if ("Retired".equals(set.getStatus())) {
                    con.rollback();
                    result.message = "Set " + setNo + " is retired and cannot be used again.";
                    return result;
                }
                if (usageDao.exists(con, sessionId, teamId, setNo)) {
                    con.rollback();
                    result.message = "Set " + setNo + " is already recorded against this session.";
                    return result;
                }

                // 1. the usage row
                usageDao.insert(con, sessionId, teamId, setNo, lapsRun);

                // 2. one heat cycle
                tireDao.addHeatCycle(con, teamId, setNo);

                // 3. retire the set if that was its last cycle
                int used = set.getHeatCycles() + 1;
                if (used >= set.getMaxHeatCycles()) {
                    tireDao.setStatus(con, teamId, setNo, "Retired");
                    result.retired = true;
                } else {
                    // A set that has been on the car is no longer new.
                    tireDao.setStatus(con, teamId, setNo, "In Use");
                    result.nearLimit = (set.getMaxHeatCycles() - used) <= 1;
                }

                con.commit();

                result.saved = true;
                result.message = "Recorded " + lapsRun + " lap(s) on set " + setNo
                               + ". Heat cycles now " + used + " of " + set.getMaxHeatCycles() + ".";
                return result;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }
}

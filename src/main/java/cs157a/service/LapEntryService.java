package cs157a.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cs157a.dao.LapDAO;
import cs157a.util.Db;

/**
 * FR8 - saves a batch of lap times as one transaction.
 *
 * An engineer types in a whole run at once. Either every lap of that run is
 * stored or none of it is, so a half-written run can never be mistaken for
 * the real session.
 */
public class LapEntryService {

    private final LapDAO lapDao = new LapDAO();

    public static class Result {
        public boolean      saved;
        public int          inserted;
        public int          firstLapNumber;
        public List<String> rejected = new ArrayList<>();
        public String       message;
    }

    /**
     * Each line of lapText is one lap time. Two forms are accepted:
     *     101.482        plain seconds
     *     1:41.482       minutes and seconds
     * A line ending in x marks the lap invalid, for example "1:38.210 x",
     * which is how an engineer records a lap where the car ran wide.
     *
     * Lines that are not a time are collected in result.rejected and the rest
     * are still saved, so one typo does not throw away the whole run.
     */
    public Result addLaps(int sessionId, String lapText) throws SQLException {

        Result result = new Result();

        List<double[]> parsed = new ArrayList<>();   // {seconds, validFlag}

        for (String rawLine : lapText.split("\\R")) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }

            boolean valid = true;
            if (line.toLowerCase().endsWith("x")) {
                valid = false;
                line = line.substring(0, line.length() - 1).trim();
            }

            Double seconds = parseLapTime(line);
            if (seconds == null) {
                result.rejected.add(rawLine.trim());
            } else if (seconds <= 0 || seconds > 3600) {
                result.rejected.add(rawLine.trim() + "  (not a believable lap time)");
            } else {
                parsed.add(new double[] { seconds, valid ? 1 : 0 });
            }
        }

        if (parsed.isEmpty()) {
            result.message = "No lap times could be read from what you typed.";
            return result;
        }

        try (Connection con = Db.get()) {

            con.setAutoCommit(false);

            try {
                // Lap numbers restart at 1 in every session, so the next one
                // is read inside the transaction. Two engineers saving at the
                // same moment cannot be handed the same number.
                int lapNumber = lapDao.nextLapNumber(con, sessionId);
                result.firstLapNumber = lapNumber;

                for (double[] lap : parsed) {
                    lapDao.insert(con, sessionId, lapNumber, lap[0], lap[1] == 1);
                    lapNumber++;
                }

                con.commit();

                result.saved = true;
                result.inserted = parsed.size();
                result.message = "Saved " + parsed.size() + " lap(s), numbered "
                               + result.firstLapNumber + " to " + (lapNumber - 1) + ".";
                return result;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    /** Reads "101.482" or "1:41.482". Returns null when the text is not a time. */
    private static Double parseLapTime(String text) {
        try {
            int colon = text.indexOf(':');
            if (colon < 0) {
                return Double.valueOf(text);
            }
            int    minutes = Integer.parseInt(text.substring(0, colon).trim());
            double seconds = Double.parseDouble(text.substring(colon + 1).trim());
            if (minutes < 0 || seconds < 0 || seconds >= 60) {
                return null;
            }
            return Double.valueOf(minutes * 60 + seconds);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

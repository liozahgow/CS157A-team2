package cs157a.model;

/**
 * One row of the laps table (FR8).
 *
 * laps is a weak entity owned by sessions: lap number 3 only means something
 * once you know which session it came from, so the key is both columns.
 */
public class Lap {

    private int     sessionId;    // owner key
    private int     lapNumber;    // partial key, unique inside the session only
    private double  lapTimeSec;
    private boolean valid;

    /** The lap written the way a timing screen shows it: 1:44.771 */
    public String getFormatted() {
        int    minutes = (int) lapTimeSec / 60;
        double seconds = lapTimeSec - minutes * 60;
        return minutes > 0 ? String.format("%d:%06.3f", minutes, seconds)
                           : String.format("%.3f", seconds);
    }

    public int getSessionId()           { return sessionId; }
    public void setSessionId(int v)     { this.sessionId = v; }

    public int getLapNumber()           { return lapNumber; }
    public void setLapNumber(int v)     { this.lapNumber = v; }

    public double getLapTimeSec()       { return lapTimeSec; }
    public void setLapTimeSec(double v) { this.lapTimeSec = v; }

    public boolean isValid()            { return valid; }
    public void setValid(boolean v)     { this.valid = v; }
}

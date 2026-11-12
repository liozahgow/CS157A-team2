package cs157a.model;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * One session as the engineer's session list shows it (FR6).
 *
 * Several columns are gathered from other tables by JOINs, because sessions
 * only stores ids: the event name, the track, the driver and the car.
 */
public class SessionRow {

    private int        sessionId;
    private int        eventId;
    private String     eventName;
    private String     trackName;
    private String     chassisNo;
    private String     driverName;
    private String     setupName;      // null when no setup was recorded
    private String     sessionType;
    private Date       sessionDate;
    private String     weather;
    private BigDecimal airTempC;
    private String     driverFeedback;

    private int        lapCount;       // from a count over laps
    private BigDecimal bestLapSec;     // null when no laps have been entered

    /** The best lap written the way a timing screen shows it: 1:44.771 */
    public String getBestLapFormatted() {
        if (bestLapSec == null) {
            return "";
        }
        int    total   = bestLapSec.intValue();
        int    minutes = total / 60;
        double seconds = bestLapSec.doubleValue() - minutes * 60;
        return minutes > 0 ? String.format("%d:%06.3f", minutes, seconds)
                           : String.format("%.3f", seconds);
    }

    public int getSessionId()             { return sessionId; }
    public void setSessionId(int v)       { this.sessionId = v; }

    public int getEventId()               { return eventId; }
    public void setEventId(int v)         { this.eventId = v; }

    public String getEventName()          { return eventName; }
    public void setEventName(String v)    { this.eventName = v; }

    public String getTrackName()          { return trackName; }
    public void setTrackName(String v)    { this.trackName = v; }

    public String getChassisNo()          { return chassisNo; }
    public void setChassisNo(String v)    { this.chassisNo = v; }

    public String getDriverName()         { return driverName; }
    public void setDriverName(String v)   { this.driverName = v; }

    public String getSetupName()          { return setupName; }
    public void setSetupName(String v)    { this.setupName = v; }

    public String getSessionType()        { return sessionType; }
    public void setSessionType(String v)  { this.sessionType = v; }

    public Date getSessionDate()          { return sessionDate; }
    public void setSessionDate(Date v)    { this.sessionDate = v; }

    public String getWeather()            { return weather; }
    public void setWeather(String v)      { this.weather = v; }

    public BigDecimal getAirTempC()       { return airTempC; }
    public void setAirTempC(BigDecimal v) { this.airTempC = v; }

    public String getDriverFeedback()         { return driverFeedback; }
    public void setDriverFeedback(String v)   { this.driverFeedback = v; }

    public int getLapCount()              { return lapCount; }
    public void setLapCount(int v)        { this.lapCount = v; }

    public BigDecimal getBestLapSec()         { return bestLapSec; }
    public void setBestLapSec(BigDecimal v)   { this.bestLapSec = v; }
}

package cs157a.model;

import java.math.BigDecimal;

/**
 * One row of the best-setup search (FR9).
 *
 * This is not a table. It is the answer to a question that spans six of them,
 * so it carries columns from several entities at once.
 */
public class SetupResult {

    private int        setupId;
    private String     setupName;
    private String     trackName;
    private String     carClass;
    private String     chassisNo;
    private String     weather;
    private BigDecimal bestLapSec;
    private int        sessionCount;
    private int        lapCount;

    private BigDecimal frontWingAngle;
    private BigDecimal rearWingAngle;
    private BigDecimal rideHeightMm;
    private BigDecimal tirePressurePsi;
    private BigDecimal springRate;

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

    public int getSetupId()                   { return setupId; }
    public void setSetupId(int v)             { this.setupId = v; }

    public String getSetupName()              { return setupName; }
    public void setSetupName(String v)        { this.setupName = v; }

    public String getTrackName()              { return trackName; }
    public void setTrackName(String v)        { this.trackName = v; }

    public String getCarClass()               { return carClass; }
    public void setCarClass(String v)         { this.carClass = v; }

    public String getChassisNo()              { return chassisNo; }
    public void setChassisNo(String v)        { this.chassisNo = v; }

    public String getWeather()                { return weather; }
    public void setWeather(String v)          { this.weather = v; }

    public BigDecimal getBestLapSec()         { return bestLapSec; }
    public void setBestLapSec(BigDecimal v)   { this.bestLapSec = v; }

    public int getSessionCount()              { return sessionCount; }
    public void setSessionCount(int v)        { this.sessionCount = v; }

    public int getLapCount()                  { return lapCount; }
    public void setLapCount(int v)            { this.lapCount = v; }

    public BigDecimal getFrontWingAngle()       { return frontWingAngle; }
    public void setFrontWingAngle(BigDecimal v) { this.frontWingAngle = v; }

    public BigDecimal getRearWingAngle()        { return rearWingAngle; }
    public void setRearWingAngle(BigDecimal v)  { this.rearWingAngle = v; }

    public BigDecimal getRideHeightMm()         { return rideHeightMm; }
    public void setRideHeightMm(BigDecimal v)   { this.rideHeightMm = v; }

    public BigDecimal getTirePressurePsi()      { return tirePressurePsi; }
    public void setTirePressurePsi(BigDecimal v){ this.tirePressurePsi = v; }

    public BigDecimal getSpringRate()           { return springRate; }
    public void setSpringRate(BigDecimal v)     { this.springRate = v; }
}

package cs157a.model;

import java.math.BigDecimal;

/** One row of the tracks table. The catalog is shared by every team. */
public class Track {

    private int        trackId;
    private String     trackName;
    private String     location;
    private BigDecimal lengthKm;
    private int        turns;

    public int getTrackId()               { return trackId; }
    public void setTrackId(int v)         { this.trackId = v; }

    public String getTrackName()          { return trackName; }
    public void setTrackName(String v)    { this.trackName = v; }

    public String getLocation()           { return location; }
    public void setLocation(String v)     { this.location = v; }

    public BigDecimal getLengthKm()       { return lengthKm; }
    public void setLengthKm(BigDecimal v) { this.lengthKm = v; }

    public int getTurns()                 { return turns; }
    public void setTurns(int v)           { this.turns = v; }
}

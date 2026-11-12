package cs157a.model;

/** A driver as it appears in a drop-down: the key plus something readable. */
public class DriverOption {

    private int    userId;
    private String displayName;
    private int    driverNumber;

    public int getUserId()               { return userId; }
    public void setUserId(int v)         { this.userId = v; }

    public String getDisplayName()       { return displayName; }
    public void setDisplayName(String v) { this.displayName = v; }

    public int getDriverNumber()         { return driverNumber; }
    public void setDriverNumber(int v)   { this.driverNumber = v; }
}

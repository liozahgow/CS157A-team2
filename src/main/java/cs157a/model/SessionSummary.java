package cs157a.model;

import java.sql.Date;

/**
 * One session run at an event, as shown on the FR5 calendar page.
 * driverName and chassisNo come from JOINs with users and cars.
 */
public class SessionSummary {

    private Date   sessionDate;
    private String sessionType;   // Practice, Qualifying, Race or Test
    private String weather;
    private String driverName;
    private String chassisNo;

    public Date getSessionDate()          { return sessionDate; }
    public void setSessionDate(Date v)    { this.sessionDate = v; }

    public String getSessionType()        { return sessionType; }
    public void setSessionType(String v)  { this.sessionType = v; }

    public String getWeather()            { return weather; }
    public void setWeather(String v)      { this.weather = v; }

    public String getDriverName()         { return driverName; }
    public void setDriverName(String v)   { this.driverName = v; }

    public String getChassisNo()          { return chassisNo; }
    public void setChassisNo(String v)    { this.chassisNo = v; }
}
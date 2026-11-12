package cs157a.model;

import java.sql.Date;

/**
 * One row of session_tire_usages (FR7), with the readable columns the usage
 * page needs pulled in from sessions, events and tire_sets.
 *
 * Every row here is one heat cycle for that tire set.
 */
public class TireUsage {

    private int    sessionId;
    private int    teamId;        // first half of the tire_sets key
    private int    setNo;         // second half, unique inside the team only
    private int    lapsRun;

    private String sessionType;
    private Date   sessionDate;
    private String eventName;

    private String compound;
    private int    heatCycles;
    private int    maxHeatCycles;
    private String status;

    public int getRemainingCycles() {
        return Math.max(0, maxHeatCycles - heatCycles);
    }

    public int getSessionId()             { return sessionId; }
    public void setSessionId(int v)       { this.sessionId = v; }

    public int getTeamId()                { return teamId; }
    public void setTeamId(int v)          { this.teamId = v; }

    public int getSetNo()                 { return setNo; }
    public void setSetNo(int v)           { this.setNo = v; }

    public int getLapsRun()               { return lapsRun; }
    public void setLapsRun(int v)         { this.lapsRun = v; }

    public String getSessionType()        { return sessionType; }
    public void setSessionType(String v)  { this.sessionType = v; }

    public Date getSessionDate()          { return sessionDate; }
    public void setSessionDate(Date v)    { this.sessionDate = v; }

    public String getEventName()          { return eventName; }
    public void setEventName(String v)    { this.eventName = v; }

    public String getCompound()           { return compound; }
    public void setCompound(String v)     { this.compound = v; }

    public int getHeatCycles()            { return heatCycles; }
    public void setHeatCycles(int v)      { this.heatCycles = v; }

    public int getMaxHeatCycles()         { return maxHeatCycles; }
    public void setMaxHeatCycles(int v)   { this.maxHeatCycles = v; }

    public String getStatus()             { return status; }
    public void setStatus(String v)       { this.status = v; }
}

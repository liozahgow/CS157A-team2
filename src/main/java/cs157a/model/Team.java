package cs157a.model;

import java.sql.Timestamp;

/**
 * One row of the teams table, carried around inside Java.
 *
 * A model holds data and nothing else: no SQL, no java.sql imports beyond the
 * Timestamp type, no knowledge of HTTP. The DAO fills it in, the servlet passes
 * it along, the JSP reads it.
 */
public class Team {

    private int       teamId;
    private String    teamName;
    private String    series;
    private String    inviteCode;
    private String    status;
    private Timestamp createdAt;

    public int getTeamId()            { return teamId; }
    public void setTeamId(int v)      { this.teamId = v; }

    public String getTeamName()       { return teamName; }
    public void setTeamName(String v) { this.teamName = v; }

    public String getSeries()         { return series; }
    public void setSeries(String v)   { this.series = v; }

    public String getInviteCode()          { return inviteCode; }
    public void setInviteCode(String v)    { this.inviteCode = v; }

    public String getStatus()         { return status; }
    public void setStatus(String v)   { this.status = v; }

    public Timestamp getCreatedAt()        { return createdAt; }
    public void setCreatedAt(Timestamp v)  { this.createdAt = v; }
}

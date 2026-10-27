package cs157a.model;

import java.sql.Date;

/**
 * One row of the tire_sets table (FR4).
 *
 * tire_sets is a weak entity: a set is only identified inside its own team,
 * so the key is teamId together with setNo. setNo on its own means nothing.
 */
public class TireSet {

    private int    teamId;          // owner key, first half of the primary key
    private int    setNo;           // partial key, unique inside the team only
    private String compound;        // Soft / Medium / Hard / Wet
    private String tireSize;
    private Date   receivedDate;
    private int    heatCycles;
    private int    maxHeatCycles;
    private String status;          // New / In Use / Retired

    /** Cycles left before the set reaches its limit, never below zero. */
    public int getRemainingCycles() {
        return Math.max(0, maxHeatCycles - heatCycles);
    }

    /** True when the set is one cycle away from its limit (FR7 warns on this). */
    public boolean isNearLimit() {
        return getRemainingCycles() <= 1 && !"Retired".equals(status);
    }

    public int getTeamId()              { return teamId; }
    public void setTeamId(int v)        { this.teamId = v; }

    public int getSetNo()               { return setNo; }
    public void setSetNo(int v)         { this.setNo = v; }

    public String getCompound()         { return compound; }
    public void setCompound(String v)   { this.compound = v; }

    public String getTireSize()         { return tireSize; }
    public void setTireSize(String v)   { this.tireSize = v; }

    public Date getReceivedDate()       { return receivedDate; }
    public void setReceivedDate(Date v) { this.receivedDate = v; }

    public int getHeatCycles()          { return heatCycles; }
    public void setHeatCycles(int v)    { this.heatCycles = v; }

    public int getMaxHeatCycles()       { return maxHeatCycles; }
    public void setMaxHeatCycles(int v) { this.maxHeatCycles = v; }

    public String getStatus()           { return status; }
    public void setStatus(String v)     { this.status = v; }
}

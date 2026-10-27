package cs157a.model;

/** One row of the cars table (FR4). A car belongs to exactly one team. */
public class Car {

    private int     carId;
    private int     teamId;
    private String  chassisNo;
    private String  model;
    private String  carClass;
    private boolean active;

    public int getCarId()              { return carId; }
    public void setCarId(int v)        { this.carId = v; }

    public int getTeamId()             { return teamId; }
    public void setTeamId(int v)       { this.teamId = v; }

    public String getChassisNo()       { return chassisNo; }
    public void setChassisNo(String v) { this.chassisNo = v; }

    public String getModel()           { return model; }
    public void setModel(String v)     { this.model = v; }

    public String getCarClass()        { return carClass; }
    public void setCarClass(String v)  { this.carClass = v; }

    public boolean isActive()          { return active; }
    public void setActive(boolean v)   { this.active = v; }
}

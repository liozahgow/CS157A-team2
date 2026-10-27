package cs157a.model;

/**
 * One row of the team roster (FR3).
 *
 * The first five fields come from users. The last three come from the
 * role tables, so they are null when the person has a different role.
 */
public class RosterMember {

    private int     userId;
    private String  displayName;
    private String  email;
    private String  role;
    private boolean active;

    private Integer driverNumber;   // from drivers        (Driver only)
    private String  specialty;      // from race_engineers (Engineer only)
    private String  officePhone;    // from team_managers  (Manager only)

    public int getUserId()                 { return userId; }
    public void setUserId(int v)           { this.userId = v; }

    public String getDisplayName()         { return displayName; }
    public void setDisplayName(String v)   { this.displayName = v; }

    public String getEmail()               { return email; }
    public void setEmail(String v)         { this.email = v; }

    public String getRole()                { return role; }
    public void setRole(String v)          { this.role = v; }

    public boolean isActive()              { return active; }
    public void setActive(boolean v)       { this.active = v; }

    public Integer getDriverNumber()       { return driverNumber; }
    public void setDriverNumber(Integer v) { this.driverNumber = v; }

    public String getSpecialty()           { return specialty; }
    public void setSpecialty(String v)     { this.specialty = v; }

    public String getOfficePhone()         { return officePhone; }
    public void setOfficePhone(String v)   { this.officePhone = v; }
}
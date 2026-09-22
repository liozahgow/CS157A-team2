package cs157a.model;

/**
 * One row of the users table.
 *
 * password_hash is deliberately NOT a field here. Once the login check has
 * passed, nothing else in the application needs it, and leaving it out means
 * it can never be printed onto a page by accident.
 */
public class User {

    private int     userId;
    private Integer teamId;        // Integer, not int, because the admin has no team (NULL)
    private String  email;
    private String  displayName;
    private String  phone;
    private Integer driverNumber;  // NULL for anyone who is not a driver
    private String  role;
    private boolean active;

    public int getUserId()          { return userId; }
    public void setUserId(int v)    { this.userId = v; }

    public Integer getTeamId()          { return teamId; }
    public void setTeamId(Integer v)    { this.teamId = v; }

    public String getEmail()          { return email; }
    public void setEmail(String v)    { this.email = v; }

    public String getDisplayName()       { return displayName; }
    public void setDisplayName(String v) { this.displayName = v; }

    public String getPhone()          { return phone; }
    public void setPhone(String v)    { this.phone = v; }

    public Integer getDriverNumber()       { return driverNumber; }
    public void setDriverNumber(Integer v) { this.driverNumber = v; }

    public String getRole()           { return role; }
    public void setRole(String v)     { this.role = v; }

    public boolean isActive()         { return active; }
    public void setActive(boolean v)  { this.active = v; }
}

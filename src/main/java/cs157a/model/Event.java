package cs157a.model;
import java.sql.Date;

/**
 * One row of the events table (FR5).
*/

public class Event {
	private int eventId;
	private String eventName;
	private int trackId;
	private String trackName;
	private Date startDate;
	private Date endDate;
	private String eventType; // either test weekend or race weekend
	
	// getters and setters 
	
    public int getEventId() 			{ return eventId; }
    public void setEventId(int v)       { this.eventId = v; }

    public String getEventName()        { return eventName; }
    public void setEventName(String v)  { this.eventName = v; }

    public int getTrackId()             { return trackId; }
    public void setTrackId(int v)       { this.trackId = v; }

    public String getTrackName()        { return trackName; }
    public void setTrackName(String v)  { this.trackName = v; }

    public Date getStartDate()          { return startDate; }
    public void setStartDate(Date v)    { this.startDate = v; }

    public Date getEndDate()            { return endDate; }
    public void setEndDate(Date v)      { this.endDate = v; }

    public String getEventType()        { return eventType; }
    public void setEventType(String v)  { this.eventType = v; }
}
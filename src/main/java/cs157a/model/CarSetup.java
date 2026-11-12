package cs157a.model;

import java.math.BigDecimal;

/** One row of the car_setups table. A setup belongs to one car and can be reused. */
public class CarSetup {

    private int        setupId;
    private int        carId;
    private String     chassisNo;      // from a JOIN with cars, so the page can name the car
    private String     setupName;
    private BigDecimal frontWingAngle;
    private BigDecimal rearWingAngle;
    private BigDecimal rideHeightMm;
    private BigDecimal tirePressurePsi;
    private BigDecimal springRate;
    private String     notes;

    public int getSetupId()                { return setupId; }
    public void setSetupId(int v)          { this.setupId = v; }

    public int getCarId()                  { return carId; }
    public void setCarId(int v)            { this.carId = v; }

    public String getChassisNo()           { return chassisNo; }
    public void setChassisNo(String v)     { this.chassisNo = v; }

    public String getSetupName()           { return setupName; }
    public void setSetupName(String v)     { this.setupName = v; }

    public BigDecimal getFrontWingAngle()       { return frontWingAngle; }
    public void setFrontWingAngle(BigDecimal v) { this.frontWingAngle = v; }

    public BigDecimal getRearWingAngle()        { return rearWingAngle; }
    public void setRearWingAngle(BigDecimal v)  { this.rearWingAngle = v; }

    public BigDecimal getRideHeightMm()         { return rideHeightMm; }
    public void setRideHeightMm(BigDecimal v)   { this.rideHeightMm = v; }

    public BigDecimal getTirePressurePsi()      { return tirePressurePsi; }
    public void setTirePressurePsi(BigDecimal v){ this.tirePressurePsi = v; }

    public BigDecimal getSpringRate()           { return springRate; }
    public void setSpringRate(BigDecimal v)     { this.springRate = v; }

    public String getNotes()               { return notes; }
    public void setNotes(String v)         { this.notes = v; }
}

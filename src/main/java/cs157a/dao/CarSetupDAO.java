package cs157a.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cs157a.model.CarSetup;
import cs157a.util.Db;

/** FR6 and FR7 - the SQL for saved car setups. */
public class CarSetupDAO {

    /**
     * Every saved setup belonging to one team.
     *
     * car_setups has no team_id of its own: a setup belongs to a car, and the
     * car belongs to the team. The JOIN is what connects the two, and it is
     * also what keeps one team's setups away from another team.
     */
    public List<CarSetup> findByTeam(int teamId) throws SQLException {

        String sql = "SELECT cs.setup_id, cs.car_id, c.chassis_no, cs.setup_name, "
                   + "       cs.front_wing_angle, cs.rear_wing_angle, cs.ride_height_mm, "
                   + "       cs.tire_pressure_psi, cs.spring_rate, cs.notes "
                   + "FROM car_setups cs "
                   + "JOIN cars c ON cs.car_id = c.car_id "
                   + "WHERE c.team_id = ? "
                   + "ORDER BY c.chassis_no, cs.setup_name";

        List<CarSetup> setups = new ArrayList<>();

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    setups.add(mapRow(rs));
                }
            }
        }
        return setups;
    }

    /** Saves a new setup for one car (FR6 lets the engineer write one down). */
    public void insert(CarSetup setup) throws SQLException {

        String sql = "INSERT INTO car_setups "
                   + "(car_id, setup_name, front_wing_angle, rear_wing_angle, "
                   + " ride_height_mm, tire_pressure_psi, spring_rate, notes) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, setup.getCarId());
            ps.setString(2, setup.getSetupName());
            ps.setBigDecimal(3, setup.getFrontWingAngle());
            ps.setBigDecimal(4, setup.getRearWingAngle());
            ps.setBigDecimal(5, setup.getRideHeightMm());
            ps.setBigDecimal(6, setup.getTirePressurePsi());
            ps.setBigDecimal(7, setup.getSpringRate());
            ps.setString(8, setup.getNotes());
            ps.executeUpdate();
        }
    }

    private CarSetup mapRow(ResultSet rs) throws SQLException {
        CarSetup s = new CarSetup();
        s.setSetupId(rs.getInt("setup_id"));
        s.setCarId(rs.getInt("car_id"));
        s.setChassisNo(rs.getString("chassis_no"));
        s.setSetupName(rs.getString("setup_name"));
        s.setFrontWingAngle(rs.getBigDecimal("front_wing_angle"));
        s.setRearWingAngle(rs.getBigDecimal("rear_wing_angle"));
        s.setRideHeightMm(rs.getBigDecimal("ride_height_mm"));
        s.setTirePressurePsi(rs.getBigDecimal("tire_pressure_psi"));
        s.setSpringRate(rs.getBigDecimal("spring_rate"));
        s.setNotes(rs.getString("notes"));
        return s;
    }
}

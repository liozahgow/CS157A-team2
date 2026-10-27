package cs157a.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cs157a.model.Car;
import cs157a.util.Db;

/** FR4 - the SQL for the cars a team owns. */
public class CarDAO {

    /**
     * Every car of one team. The team id is a parameter and goes straight into
     * the WHERE clause, so one team can never see another team's cars.
     */
    public List<Car> findByTeam(int teamId, boolean includeRetired) throws SQLException {

        String sql = "SELECT car_id, team_id, chassis_no, model, car_class, active "
                   + "FROM cars "
                   + "WHERE team_id = ? ";

        if (!includeRetired) {
            sql = sql + "AND active = 1 ";
        }
        sql = sql + "ORDER BY car_class, chassis_no";

        List<Car> cars = new ArrayList<>();

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, teamId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cars.add(mapRow(rs));
                }
            }
        }
        return cars;
    }

    /** True when some other car already uses this chassis number. */
    public boolean chassisExists(String chassisNo) throws SQLException {

        String sql = "SELECT 1 FROM cars WHERE chassis_no = ?";

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, chassisNo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Adds one car to the manager's team. */
    public void insert(Car car) throws SQLException {

        String sql = "INSERT INTO cars (team_id, chassis_no, model, car_class, active) "
                   + "VALUES (?, ?, ?, ?, 1)";

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, car.getTeamId());
            ps.setString(2, car.getChassisNo());
            ps.setString(3, car.getModel());
            ps.setString(4, car.getCarClass());
            ps.executeUpdate();
        }
    }

    /**
     * Retires or reactivates a car. We never DELETE: a retired car is still
     * referenced by old sessions, and that history has to stay readable.
     * The team id is in the WHERE clause so a manager cannot touch another
     * team's car by guessing its id.
     */
    public void setActive(int carId, int teamId, boolean active) throws SQLException {

        String sql = "UPDATE cars SET active = ? WHERE car_id = ? AND team_id = ?";

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, active);
            ps.setInt(2, carId);
            ps.setInt(3, teamId);
            ps.executeUpdate();
        }
    }

    private Car mapRow(ResultSet rs) throws SQLException {
        Car c = new Car();
        c.setCarId(rs.getInt("car_id"));
        c.setTeamId(rs.getInt("team_id"));
        c.setChassisNo(rs.getString("chassis_no"));
        c.setModel(rs.getString("model"));
        c.setCarClass(rs.getString("car_class"));
        c.setActive(rs.getBoolean("active"));
        return c;
    }
}

package cs157a.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cs157a.model.Track;
import cs157a.util.Db;

/**
 * The shared track catalog. A track does not belong to any team, so there is
 * no team_id to filter on here.
 */
public class TrackDAO {

    /** Every track, for the drop-down on the event form. */
    public List<Track> findAll() throws SQLException {

        String sql = "SELECT track_id, track_name, location, length_km, turns "
                   + "FROM tracks "
                   + "ORDER BY track_name";

        List<Track> tracks = new ArrayList<>();

        try (Connection con = Db.get();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Track t = new Track();
                t.setTrackId(rs.getInt("track_id"));
                t.setTrackName(rs.getString("track_name"));
                t.setLocation(rs.getString("location"));
                t.setLengthKm(rs.getBigDecimal("length_km"));
                t.setTurns(rs.getInt("turns"));
                tracks.add(t);
            }
        }
        return tracks;
    }
}

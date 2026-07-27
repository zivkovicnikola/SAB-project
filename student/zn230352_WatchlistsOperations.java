package student;

import rs.ac.bg.etf.sab.operations.WatchlistsOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class zn230352_WatchlistsOperations implements WatchlistsOperations {

    private Connection connection = zn230352_DB.getInstance().getConnection();

    @Override
    public boolean addMovieToWatchlist(Integer integer, Integer integer1) {
        try (PreparedStatement ps1 = connection.prepareStatement("select * from Wishlist where IdU = ? and IdM = ?")) {
            ps1.setInt(1, integer);
            ps1.setInt(2, integer1);
            try (ResultSet rs1 = ps1.executeQuery()) {
                if (rs1.next())
                    return false;
            }

            try (PreparedStatement ps2 = connection.prepareStatement("insert into Wishlist(IdU, IdM) values (?, ?)")) {
                ps2.setInt(1, integer);
                ps2.setInt(2, integer1);
                ps2.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean removeMovieFromWatchlist(Integer integer, Integer integer1) {
        try (PreparedStatement ps = connection.prepareStatement("delete from Wishlist where IdU = ? and IdM = ?")) {
            ps.setInt(1, integer);
            ps.setInt(2, integer1);
            int rows = ps.executeUpdate();
            if (rows == 0)
                return false;
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean isMovieInWatchlist(Integer integer, Integer integer1) {
        try (PreparedStatement ps = connection.prepareStatement("select * from Wishlist where IdU = ? and IdM = ?")) {
            ps.setInt(1, integer);
            ps.setInt(2, integer1);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<Integer> getMoviesInWatchlist(Integer integer) {
        List<Integer> ids = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement("select IdM from Wishlist where IdU = ?")) {
            ps.setInt(1, integer);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    ids.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ids;
    }

    @Override
    public List<Integer> getUsersWithMovieInWatchlist(Integer integer) {
        List<Integer> ids = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement("select IdU from Wishlist where IdM = ?")) {
            ps.setInt(1, integer);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    ids.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ids;
    }

}
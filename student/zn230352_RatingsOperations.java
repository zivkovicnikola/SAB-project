package student;

import rs.ac.bg.etf.sab.operations.RatingsOperations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class zn230352_RatingsOperations implements RatingsOperations {

    private Connection connection = zn230352_DB.getInstance().getConnection();

    @Override
    public boolean addRating(Integer integer, Integer integer1, Integer integer2) {
        try (PreparedStatement ps1 = connection.prepareStatement("select * from Grade where IdU = ? and IdM = ?")) {
            ps1.setInt(1, integer);
            ps1.setInt(2, integer1);

            try (ResultSet rs1 = ps1.executeQuery()) {
                if (rs1.next())
                    return false;
            }

            try (PreparedStatement ps = connection.prepareStatement("insert into Grade(IdU, IdM, Grade) values (?, ?, ?)")) {
                ps.setInt(1, integer);
                ps.setInt(2, integer1);
                ps.setInt(3, integer2);
                ps.executeUpdate();
            }

            try (CallableStatement cs = connection.prepareCall("{ call SP_REWARD_USER_(?, ?) }")) {
                cs.setInt(1, integer);
                cs.setInt(2, integer1);
                cs.execute();
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateRating(Integer integer, Integer integer1, Integer integer2) {
        try (PreparedStatement ps = connection.prepareStatement("update Grade set Grade = ? where IdU = ? and IdM = ?")) {
            ps.setInt(1, integer2);
            ps.setInt(2, integer);
            ps.setInt(3, integer1);
            int rows = ps.executeUpdate();
            if (rows == 0)
                return false;

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeRating(Integer integer, Integer integer1) {
        try (PreparedStatement ps = connection.prepareStatement("delete from Grade where IdU = ? and IdM = ?")) {
            ps.setInt(1, integer);
            ps.setInt(2, integer1);
            int rows = ps.executeUpdate();
            if (rows == 0)
                return false;
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Integer getRating(Integer integer, Integer integer1) {
        try (PreparedStatement ps = connection.prepareStatement("select Grade from Grade where IdU = ? and IdM = ?")) {
            ps.setInt(1, integer);
            ps.setInt(2, integer1);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Integer> getRatedMoviesByUser(Integer integer) {
        List<Integer> ids = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement("select IdM from Grade where IdU = ?")) {
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
    public List<Integer> getUsersWhoRatedMovie(Integer integer) {
        List<Integer> ids = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement("select IdU from Grade where IdM = ?")) {
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

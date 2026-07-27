package student;

import rs.ac.bg.etf.sab.operations.GenresOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class zn230352_GenresOperations implements GenresOperations {

    private Connection connection = zn230352_DB.getInstance().getConnection();

    @Override
    public boolean doesGenreExist(String s) {
        try (PreparedStatement ps = connection.prepareStatement("select * from Genre where Name = ?")) {
            ps.setString(1, s);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Integer addGenre(String s) {
        if (doesGenreExist(s))
            return null;

        try (PreparedStatement ps = connection.prepareStatement("insert into Genre(Name) values (?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Integer updateGenre(Integer integer, String s) {
        try (PreparedStatement ps = connection.prepareStatement("update Genre set Name = ? where Id = ?")) {
            ps.setString(1, s);
            ps.setInt(2, integer);
            int rows = ps.executeUpdate();
            if (rows == 0)
                return null;
            return integer;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Integer removeGenre(Integer integer) {
        try (PreparedStatement ps = connection.prepareStatement("delete from Genre where Id = ?")) {
            ps.setInt(1, integer);
            int rows = ps.executeUpdate();
            if (rows == 0)
                return null;
            return integer;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Integer getGenreId(String s) {
        try (PreparedStatement ps = connection.prepareStatement("select Id from Genre where Name = ?")) {
            ps.setString(1, s);
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
    public List<Integer> getAllGenreIds() {
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("select Id from Genre")) {
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
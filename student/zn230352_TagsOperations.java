package student;

import rs.ac.bg.etf.sab.operations.TagsOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class zn230352_TagsOperations implements TagsOperations {

    private Connection connection = zn230352_DB.getInstance().getConnection();

    @Override
    public Integer addTag(Integer integer, String s) {
        try (PreparedStatement ps1 = connection.prepareStatement("select * from Movie where Id = ?")) {
            ps1.setInt(1, integer);
            try (ResultSet rs1 = ps1.executeQuery()) {
                if (!rs1.next())
                    return null;
            }

            try (PreparedStatement ps2 = connection.prepareStatement("select * from Tag t join MovieTag mt on mt.IdT = t.Id where mt.IdM = ? and t.Name = ?")) {
                ps2.setInt(1, integer);
                ps2.setString(2, s);
                try (ResultSet rs2 = ps2.executeQuery()) {
                    if (rs2.next())
                        return null;
                }
            }

            Integer tag;
            try (PreparedStatement ps3 = connection.prepareStatement("select Id from Tag where Name = ?")) {
                ps3.setString(1, s);
                try (ResultSet rs3 = ps3.executeQuery()) {
                    if (rs3.next()) {
                        tag = rs3.getInt(1);
                    } else {
                        try (PreparedStatement ps4 = connection.prepareStatement("insert into Tag(Name) values (?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                            ps4.setString(1, s);
                            ps4.executeUpdate();
                            try (ResultSet rs4 = ps4.getGeneratedKeys()) {
                                rs4.next();
                                tag = rs4.getInt(1);
                            }
                        }
                    }
                }
            }

            try (PreparedStatement ps5 = connection.prepareStatement("insert into MovieTag(IdM, IdT) values (?, ?)")) {
                ps5.setInt(1, integer);
                ps5.setInt(2, tag);
                ps5.executeUpdate();
                return integer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Integer removeTag(Integer integer, String s) {
        try (PreparedStatement ps = connection.prepareStatement(
                "delete from MovieTag where IdM = ? and IdT = (select Id from Tag where Name = ?)"
        )) {
            ps.setInt(1, integer);
            ps.setString(2, s);
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
    public int removeAllTagsForMovie(Integer integer) {
        try (PreparedStatement ps = connection.prepareStatement("delete from MovieTag where IdM = ?")) {
            ps.setInt(1, integer);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public boolean hasTag(Integer integer, String s) {
        try (PreparedStatement ps = connection.prepareStatement("select * from Tag t join MovieTag mt on mt.IdT = t.Id where mt.IdM = ? and t.Name = ?")) {
            ps.setInt(1, integer);
            ps.setString(2, s);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<String> getTagsForMovie(Integer integer) {
        List<String> tags = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("select T.Name from Tag t join MovieTag mt on t.Id = mt.IdT where mt.IdM = ?")) {
            ps.setInt(1, integer);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    tags.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tags;
    }

    @Override
    public List<Integer> getMovieIdsByTag(String s) {
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("select mt.IdM from Tag t join MovieTag mt on mt.IdT = t.Id where t.Name = ?")) {
            ps.setString(1, s);
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
    public List<String> getAllTags() {
        List<String> tags = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("select distinct Name from Tag")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    tags.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tags;
    }

}
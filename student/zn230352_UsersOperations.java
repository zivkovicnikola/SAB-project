package student;

import rs.ac.bg.etf.sab.operations.UsersOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class zn230352_UsersOperations implements UsersOperations {

    private Connection connection = zn230352_DB.getInstance().getConnection();

    @Override
    public Integer addUser(String s) {
        try (PreparedStatement ps1 = connection.prepareStatement("select * from Users where Username = ?")) {
            ps1.setString(1, s);
            try (ResultSet rs1 = ps1.executeQuery()) {
                if (rs1.next())
                    return null;
            }

            try (PreparedStatement ps2 = connection.prepareStatement("insert into Users(Username, Awards) values (?, 0)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps2.setString(1, s);
                ps2.executeUpdate();
                try (ResultSet rs = ps2.getGeneratedKeys()) {
                    if (rs.next())
                        return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Integer updateUser(Integer integer, String s) {
        try (PreparedStatement ps = connection.prepareStatement("update Users set Username = ? where Id = ?")) {
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
    public Integer removeUser(Integer integer) {
        try (PreparedStatement ps1 = connection.prepareStatement("delete from Grade where IdU = ?")) {
            ps1.setInt(1, integer);
            ps1.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        try (PreparedStatement ps2 = connection.prepareStatement("delete from Wishlist where IdU = ?")) {
            ps2.setInt(1, integer);
            ps2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        try (PreparedStatement ps3 = connection.prepareStatement("delete from Users where Id = ?")) {
            ps3.setInt(1, integer);
            int rows = ps3.executeUpdate();
            if (rows == 0)
                return null;
            return integer;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean doesUserExist(String s) {
        try (PreparedStatement ps = connection.prepareStatement("select * from Users where Username = ?")) {
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
    public Integer getUserId(String s) {
        try (PreparedStatement ps = connection.prepareStatement("select Id from Users where Username = ?")) {
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
    public List<Integer> getAllUserIds() {
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("select Id from Users")) {
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
    public List<Integer> getRecommendedMoviesFromFavoriteGenres(Integer integer) {
        List<Integer> ids = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement("select m.Id from Movie m join Grade gm on gm.IdM = m.Id where m.Id in ( " +
                "select mg.IdM from MovieGenre mg where mg.IdG in ( " +
                "select mg2.IdG from MovieGenre mg2 join Grade g on g.IdM = mg2.IdM where g.IdU = ? group by mg2.IdG having avg(cast(g.Grade as decimal(10,3))) >= 8 ) " +
                ") and m.Id not in ( " +
                "select g2.IdM from Grade g2 where g2.IdU = ? " +
                ") and m.Id not in ( " +
                "select w.IdM from Wishlist w where w.IdU = ? " +
                ") group by m.Id having (count(gm.Grade) >= 4 and avg(cast(gm.Grade as decimal(10,3))) >= 7.5) or (count(gm.Grade) < 4 and avg(cast(gm.Grade as decimal(10,3))) >= 9) order by avg(cast(gm.Grade as decimal(10,3))) desc, m.Id asc")) {
            ps.setInt(1, integer);
            ps.setInt(2, integer);
            ps.setInt(3, integer);

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
    public Integer getRewards(Integer integer) {
        try (PreparedStatement ps = connection.prepareStatement("select Awards from Users where Id = ?")) {
            ps.setInt(1, integer);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public List<String> getThematicSpecializations(Integer integer) {
        List<String> tags = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement("select t.Name from Grade g join MovieTag mt on g.IdM = mt.IdM join Tag t on mt.IdT = t.Id where g.IdU = ? and g.Grade >= 8 group by t.Name having count(*) >= 2")) {
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
    public String getUserDescription(Integer integer) {
        try (PreparedStatement ps = connection.prepareStatement("select count(distinct mt.IdT), count(distinct g.IdM) from Grade g left join MovieTag mt on g.IdM = mt.IdM where g.IdU = ?")) {
            ps.setInt(1, integer);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int tagCount = rs.getInt(1);
                    int movieCount = rs.getInt(2);
                    if (movieCount < 10)
                        return "undefined";
                    else if (tagCount >= 10)
                        return "curious";
                    else
                        return "focused";

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "undefined";
    }

}
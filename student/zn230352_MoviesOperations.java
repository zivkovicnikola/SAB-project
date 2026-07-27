package student;

import rs.ac.bg.etf.sab.operations.MoviesOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class zn230352_MoviesOperations implements MoviesOperations {

    private Connection connection = zn230352_DB.getInstance().getConnection();

    @Override
    public Integer addMovie(String s, Integer integer, String s1) {
        try (PreparedStatement ps1 = connection.prepareStatement("select * from Genre where Id = ?")) {
            ps1.setInt(1, integer);
            try (ResultSet rs1 = ps1.executeQuery()) {
                if (!rs1.next())
                    return null;
            }

            try (PreparedStatement ps2 = connection.prepareStatement("insert into Movie(Title, Director) values (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps2.setString(1, s);
                ps2.setString(2, s1);
                ps2.executeUpdate();

                try (ResultSet rs2 = ps2.getGeneratedKeys()) {
                    if (rs2.next()) {
                        Integer movieId = rs2.getInt(1);

                        try (PreparedStatement ps3 = connection.prepareStatement("insert into MovieGenre(IdM, IdG) values (?, ?)")) {
                            ps3.setInt(1, movieId);
                            ps3.setInt(2, integer);
                            ps3.executeUpdate();
                            return movieId;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Integer updateMovieTitle(Integer integer, String s) {
        try (PreparedStatement ps = connection.prepareStatement("update Movie set Title = ? where Id = ?")) {
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
    public Integer addGenreToMovie(Integer integer, Integer integer1) {
        try (PreparedStatement ps1 = connection.prepareStatement("select * from Movie where Id = ?")) {
            ps1.setInt(1, integer);

            try (ResultSet rs1 = ps1.executeQuery()) {
                if (!rs1.next())
                    return null;
            }

            try (PreparedStatement ps2 = connection.prepareStatement("select * from Genre where Id = ?")) {
                ps2.setInt(1, integer1);

                try (ResultSet rs2 = ps2.executeQuery()) {
                    if (!rs2.next())
                        return null;
                }
            }

            try (PreparedStatement ps3 = connection.prepareStatement("select * from MovieGenre where IdM = ? and IdG = ?")) {
                ps3.setInt(1, integer);
                ps3.setInt(2, integer1);

                try (ResultSet rs3 = ps3.executeQuery()) {
                    if (rs3.next())
                        return null;
                }
            }

            try (PreparedStatement ps4 = connection.prepareStatement("insert into MovieGenre(IdM, IdG) values (?, ?)")) {
                ps4.setInt(1, integer);
                ps4.setInt(2, integer1);
                ps4.executeUpdate();
                return integer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Integer removeGenreFromMovie(Integer integer, Integer integer1) {
        try (PreparedStatement ps1 = connection.prepareStatement("select * from Movie where Id = ?")) {
            ps1.setInt(1, integer);

            try (ResultSet rs1 = ps1.executeQuery()) {
                if (!rs1.next())
                    return null;
            }

            try (PreparedStatement ps2 = connection.prepareStatement("select * from Genre where Id = ?")) {
                ps2.setInt(1, integer1);

                try (ResultSet rs2 = ps2.executeQuery()) {
                    if (!rs2.next())
                        return null;
                }
            }

            try (PreparedStatement ps3 = connection.prepareStatement("select * from MovieGenre where IdM = ? and IdG = ?")) {
                ps3.setInt(1, integer);
                ps3.setInt(2, integer1);

                try (ResultSet rs3 = ps3.executeQuery()) {
                    if (!rs3.next())
                        return null;
                }
            }

            try (PreparedStatement ps4 = connection.prepareStatement("delete from MovieGenre where IdM = ? and IdG = ?")) {
                ps4.setInt(1, integer);
                ps4.setInt(2, integer1);
                int rows = ps4.executeUpdate();
                return integer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Integer updateMovieDirector(Integer integer, String s) {
        try (PreparedStatement ps = connection.prepareStatement("update Movie set Director = ? where Id = ?")) {
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
    public Integer removeMovie(Integer integer) {
        try (PreparedStatement ps1 = connection.prepareStatement("delete from Grade where IdM = ?")) {
            ps1.setInt(1, integer);
            ps1.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        try (PreparedStatement ps2 = connection.prepareStatement("delete from Wishlist where IdM = ?")) {
            ps2.setInt(1, integer);
            ps2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        try (PreparedStatement ps3 = connection.prepareStatement("delete from MovieTag where IdM = ?")) {
            ps3.setInt(1, integer);
            ps3.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        try (PreparedStatement ps4 = connection.prepareStatement("delete from MovieGenre where IdM = ?")) {
            ps4.setInt(1, integer);
            ps4.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        try (PreparedStatement ps5 = connection.prepareStatement("delete from Movie where Id = ?")) {
            ps5.setInt(1, integer);
            int rows = ps5.executeUpdate();
            if (rows == 0)
                return null;
            return integer;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Integer> getMovieIds(String s, String s1) {
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("select Id from Movie where Title = ? and Director = ?")) {
            ps.setString(1, s);
            ps.setString(2, s1);
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
    public List<Integer> getAllMovieIds() {
        List<Integer> ids = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement("select Id from Movie")) {
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
    public List<Integer> getMovieIdsByGenre(Integer integer) {
        List<Integer> ids = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement("select IdM from MovieGenre where IdG = ?")) {
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
    public List<Integer> getGenreIdsForMovie(Integer integer) {
        List<Integer> ids = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement("select IdG from MovieGenre where IdM = ?")) {
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
    public List<Integer> getMovieIdsByDirector(String s) {
        List<Integer> ids = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement("select Id from Movie where Director = ?")) {
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
    public String getMovieTrend(Integer integer) {
        try (PreparedStatement ps = connection.prepareStatement("select Status from Movie where Id = ?")) {
            ps.setInt(1, integer);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
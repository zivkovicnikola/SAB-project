package student;

import rs.ac.bg.etf.sab.operations.GeneralOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class zn230352_GeneralOperations implements GeneralOperations {

    private Connection connection = zn230352_DB.getInstance().getConnection();

    @Override
    public void eraseAll() {

        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM Wishlist")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM Grade")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM MovieGenre")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM MovieTag")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM Movie")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM Genre")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM Tag")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM Users")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
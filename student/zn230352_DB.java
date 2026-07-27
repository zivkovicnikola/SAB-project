package student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class zn230352_DB {

    private static final String database = "Movies_SAB";
    private static final int port = 1433;
    private static final String serverName = "localhost\\SQLEXPRESS";
    private static final String connectionString = "jdbc:sqlserver://"
                                                + serverName + ";"
//                                                + port + ";"
                                                + "databaseName=" + database + ";"
                                                + "integratedSecurity=true;"
                                                + "encrypt=true;"
                                                + "trustServerCertificate=true;";

    private Connection connection;

    private zn230352_DB() {
        try {
            connection = DriverManager.getConnection(connectionString);
        } catch (SQLException ex) {
            connection =  null;
            Logger.getLogger(zn230352_DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private static zn230352_DB db = null;

    public static zn230352_DB getInstance() {
        if (db == null) {
            db = new zn230352_DB();
        }
        return db;
    }

}

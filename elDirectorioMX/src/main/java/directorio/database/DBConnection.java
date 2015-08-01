package directorio.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import net.sourceforge.jtds.jdbc.Driver;

/**
 * Created by Hiram González on 31/07/15.
 */
public class DBConnection {

    private static DBConnection instance = null;
    private static final String URL = "jdbc:jtds:sqlserver://173.193.3.218:1433/ElDirectorioDb;";
    private static final String USER = "Carlos";
    private static final String PASS = "cqcg+";
    private static Connection connection = null;

    private DBConnection(){}

    public static DBConnection getInstance(){
        if(instance == null)
            instance = new DBConnection();
        return instance;
    }

    public Connection conectar(){
        Connection conn = null;
        try {
            (new Driver()).getClass();
            conn = DriverManager.getConnection(URL,USER,PASS);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return conn;
    }

    public Connection getConnection(){
        if(connection == null)
            connection = conectar();
        return connection;
    }


}

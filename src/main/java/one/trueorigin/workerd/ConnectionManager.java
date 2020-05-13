package one.trueorigin.workerd;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import one.trueorigin.migrator.Database;

import java.sql.SQLException;

public class ConnectionManager {

    public static final String JDBC = "jdbc:mysql://root@localhost:3306/trueorigin?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static ConnectionSource connectionSource;

    public synchronized static ConnectionSource get() throws SQLException, ClassNotFoundException {
        if(connectionSource == null){
            Class.forName(Database.MySQLDriver);
            connectionSource =
                    new JdbcConnectionSource(JDBC);
        }

        return connectionSource;
    }

    public synchronized static ConnectionSource get(String conn) throws SQLException, ClassNotFoundException {
        if(connectionSource == null){
            Class.forName(Database.MySQLDriver);
            connectionSource =
                    new JdbcConnectionSource(conn);
        }

        return connectionSource;
    }

}
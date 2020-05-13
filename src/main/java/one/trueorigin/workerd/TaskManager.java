package one.trueorigin.workerd;

import one.trueorigin.migrator.DatabaseConnectionManager;
import one.trueorigin.migrator.exception.ConnectionStringException;
import one.trueorigin.workerd.mysql.MySQLSchedular;

import java.sql.SQLException;
import java.util.Date;

public class TaskManager {

    public static final String OnBoarding = "onboarding";
    public static final String OffBoarding = "offboarding";

    public static <T> void  submit(String type, T payload) throws SQLException, ConnectionStringException, ClassNotFoundException {
        MySQLSchedular mySQLSchedularClient = MySQLSchedular.initializeClient(
                DatabaseConnectionManager.withConnection("jdbc:mysql://root@localhost:3306/jobmanager?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC")
        );

        Job job = new Job(type, payload);
        mySQLSchedularClient.submit(job);
    }

    public static <T> void  submit(String type, T payload, Date scheduledDate) throws SQLException, ConnectionStringException, ClassNotFoundException {
        MySQLSchedular mySQLSchedularClient = MySQLSchedular.initializeClient(
                DatabaseConnectionManager.withConnection("jdbc:mysql://root@localhost:3306/jobmanager?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC")
        );

        Job job = new Job(type, payload);

        mySQLSchedularClient.submit(job, scheduledDate);
    }
}

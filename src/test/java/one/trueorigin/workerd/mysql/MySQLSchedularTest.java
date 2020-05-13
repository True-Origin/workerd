package one.trueorigin.workerd.mysql;

import one.trueorigin.migrator.DatabaseConnectionManager;
import one.trueorigin.migrator.exception.ConnectionStringException;
import one.trueorigin.workerd.SchedularConfiguration;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.SQLException;

public class MySQLSchedularTest {

    @Ignore
    @Test
    public void shouldSchedule() throws InterruptedException, SQLException, ConnectionStringException, ClassNotFoundException {

        SchedularConfiguration.SchedularConfigurationBuilder init = SchedularConfiguration.SchedularConfigurationBuilder.init();

        SchedularConfiguration config = init
                .setRetries(6)
                .setJobTimeout(60000)
                .setScheduleTimeout(60000)
                .setPollTime(1000)
                .setBackground(false)
                .setPoolSize(10)
                .setBackground(true)
                .build();


        MySQLSchedular mySQLSchedular = MySQLSchedular.initializeServer(
                DatabaseConnectionManager.withConnection("jdbc:mysql://root@localhost:3306/jobmanager?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"),
                config
        );

        mySQLSchedular.start();
        mySQLSchedular.register("test", new JobProcessor<>(MysqlDataTest.class) );

        while(true){
            Thread.sleep(1000);
        }
//        mySQLSchedularClient.submit(job);
    }
}

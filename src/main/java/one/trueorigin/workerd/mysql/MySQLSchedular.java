package one.trueorigin.workerd.mysql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import one.trueorigin.migrator.SchemaInterpreter;
import one.trueorigin.migrator.TableAnnotationNotFound;
import one.trueorigin.migrator.exception.ConnectionStringException;
import one.trueorigin.migrator.exception.NoFieldDefinedException;
import one.trueorigin.workerd.*;
import one.trueorigin.migrator.Database;
import one.trueorigin.migrator.*;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static one.trueorigin.workerd.ObjectParser.parse;

public class MySQLSchedular implements JobSchedular {

    private final SchedularConfiguration schedularConfiguration;
    private final Database database;
    private Map<String, Processor> processRegister;
    private final ExecutorService executorService;

    public MySQLSchedular(SchedularConfiguration schedularConfiguration, Database database, Map<String, Processor> processRegister, ExecutorService executorService) {
        this.schedularConfiguration = schedularConfiguration;
        this.database = database;
        this.processRegister = processRegister;
        this.executorService = executorService;
    }

    public static MySQLSchedular initializeServer(Database database, SchedularConfiguration config) {
        return new MySQLSchedular(config, database, new HashMap<>(), Executors.newFixedThreadPool(config.getPoolSize()));
    }

    public static MySQLSchedular initializeClient(Database database) {
        return new MySQLSchedular(null, database, null, null);
    }

    @Override
    public Optional<String> submit(final Job job) {
        try {
            Dao<JobEntry, ?> dao = DaoManager.createDao(ConnectionManager.get(database.getConnectionString()), JobEntry.class);
            JobEntry entry = JobEntry.createJob(job, JobStatus.Scheduled);
            entry.setRetryCount(0);
            dao.create(entry);
            return Optional.of(entry.getJobId());
        } catch (SQLException | ClassNotFoundException | JsonProcessingException throwables) {
            throwables.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Optional<String> submit(Job job, Date scheduledDate) {
        try {
            Dao<JobEntry, ?> dao = DaoManager.createDao(ConnectionManager.get(database.getConnectionString()), JobEntry.class);
            JobEntry entry = JobEntry.createJob(job, JobStatus.Scheduled);
            entry.setRetryCount(0);
            entry.setScheduledDate(scheduledDate);
            dao.create(entry);
            return Optional.of(entry.getJobId());
        } catch (SQLException | ClassNotFoundException | JsonProcessingException throwables) {
            throwables.printStackTrace();
        }

        return Optional.empty();    }

    public void start() {

        try {
            SchemaInterpreter schemaManager = DatabaseConnectionManager.withConnection(database.getConnectionString()).
                    getSchemaManager();
            schemaManager.
                    model(JobEntry.class).
                    migrate();
        } catch (ConnectionStringException | ClassNotFoundException | SQLException | TableAnnotationNotFound | NoFieldDefinedException e) {
            e.printStackTrace();
        }

        Runnable jobRunner = () -> {
            try {
                jobProcessor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable busyTaskTimeEnforcer = () -> {

            while(true){

            try {
                Dao<JobEntry, String> dao = DaoManager.createDao(ConnectionManager.get(database.getConnectionString()), JobEntry.class);
                List<JobEntry> allJobs = dao.queryBuilder()
                        .orderBy("createdAt", true)
                        .where()
                        .eq("jobStatus", JobStatus.Busy.toString())
                        .query();

                allJobs.forEach(job -> {
                    try {

                        System.out.println("now processing - " + job.getJobId());

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(job.getCreatedAt());
                        calendar.add(Calendar.MILLISECOND, schedularConfiguration.getJobTimeout());

                        System.out.println(calendar.getTime());
                        System.out.println(new Date());
                        System.out.println(job.getJobId());
                        if(new Date().after(calendar.getTime())) {
                            job.setJobStatus(JobStatus.Failed);
                            dao.createOrUpdate(job);
                        }

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                });


        } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        } ;

        ExecutorService jobExecutionService = Executors.newFixedThreadPool(1);
        Future<?> jobExecutionSubmission = jobExecutionService.submit(jobRunner);

        ExecutorService busyTaskExecutionService = Executors.newFixedThreadPool(1);
        Future<?> busyTaskExecutionSubmission = busyTaskExecutionService.submit(busyTaskTimeEnforcer);


        if (jobExecutionSubmission.isDone()) {
            System.out.println("done");
        }

        if (busyTaskExecutionSubmission.isDone()) {
            System.out.println("done");
        }

        if (this.schedularConfiguration.isBackground()) {
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            Future<?> submit = executorService.submit(this::alive);
            if(submit.isDone()) {
                System.out.println("task completed");
            }
        } else {
            alive();
        }

        return;
    }

    private void alive() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void jobProcessor() throws InterruptedException {

        while (true) {
            try {
                Dao<JobEntry, String> dao = DaoManager.createDao(ConnectionManager.get(database.getConnectionString()), JobEntry.class);
                List<JobEntry> allJobs = dao.queryBuilder()
                        .orderBy("createdAt", true)
                        .where()
                        .eq("jobStatus", JobStatus.Scheduled.toString())
                        .query();

                allJobs.forEach(job -> {
                    try {

                        System.out.println("now processing - " + job.getJobId());
                        Processor processor = processRegister.get(job.getType());

                        job.setJobStatus(JobStatus.Busy);
                        dao.createOrUpdate(job);

                        Future<?> submit = executorService.submit(
                                getRunnable(dao, job, processor));

                        if(submit.isDone()) {
                            System.out.println("completed");
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                });

            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }

            Thread.sleep(schedularConfiguration.getPollTime());
        }
    }

    private Runnable getRunnable(Dao<JobEntry, String> dao, JobEntry job, Processor processor) {
        return () -> {

            try {
                try {
                    Boolean process = processor.process(parse().readValue(job.getData(), processor.getProcessorClass()));
                    System.out.println(process);
                    if (!process) {
                        job.setJobStatus(JobStatus.Failed);
                    } else {
                        job.setJobStatus(JobStatus.Processed);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    job.setJobStatus(JobStatus.Failed);
                }

                dao.createOrUpdate(job);

            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    public void register(String type, Processor processor) {
        processRegister.put(type, processor);

        System.out.println(processRegister);
    }
}

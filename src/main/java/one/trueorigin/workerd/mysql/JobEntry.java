package one.trueorigin.workerd.mysql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import one.trueorigin.workerd.Job;
import one.trueorigin.workerd.JobStatus;

import java.util.Date;
import java.util.UUID;

import static one.trueorigin.workerd.ObjectParser.parse;

@DatabaseTable(tableName = "job_entries")
public class JobEntry {

    @DatabaseField(id = true)
    private String jobId;

    @DatabaseField
    private Date createdAt;

    @DatabaseField
    private String type;

    @DatabaseField
    private String jobStatus;

    @DatabaseField
    private Date scheduledDate;

    @DatabaseField(canBeNull = false)
    private Integer retryCount;

    @DatabaseField(dataType = DataType.LONG_STRING)
    private String data;

    public JobEntry(String id, String type, String output, Date date, String jobStatus) {
        this.jobId = id;
        this.data = output;
        this.createdAt = date;
        this.type = type;
        this.jobStatus = jobStatus;
    }

    public JobEntry() {
    }

    public static JobEntry createJob(final Job job, JobStatus jobStatus) throws JsonProcessingException {

        String output = parse().writeValueAsString(job.getPayload());

        return new JobEntry(
                UUID.randomUUID().toString(),
                job.getType(),
                output,
                new Date(),
                jobStatus.toString()
        );
    }

    public String getData() {
        return data;
    }

    public String getType() {
        return type;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getJobId() {
        return jobId;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus.toString();
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
}

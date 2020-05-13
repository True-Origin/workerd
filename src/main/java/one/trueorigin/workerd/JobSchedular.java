package one.trueorigin.workerd;

import java.util.Date;
import java.util.Optional;

public interface JobSchedular {
    public Optional<String> submit(Job job);
    public Optional<String> submit(Job job, Date scheduledDate);
    public void start() throws InterruptedException;
}

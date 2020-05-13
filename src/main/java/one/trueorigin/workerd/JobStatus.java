package one.trueorigin.workerd;

public enum JobStatus {
    Processed("processed"),
    Failed("failed"),
    Busy("busy"),
    Enqueued("enqueued"),
    Retries("retries"),
    Scheduled("scheduled"),
    Dead("dead");

    private String dead;

    JobStatus(String dead) {
        this.dead = dead;
    }

    @Override
    public String toString() {
        return dead;
    }
}

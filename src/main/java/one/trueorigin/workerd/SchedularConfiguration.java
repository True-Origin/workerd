package one.trueorigin.workerd;

public class SchedularConfiguration {

    private long scheduleTimeout;

    public int getJobTimeout() {
        return jobTimeout;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public boolean isBackground() {
        return background;
    }

    public long getPollTime() {
        return pollTime;
    }

    public static class SchedularConfigurationBuilder {
        private SchedularConfiguration schedularConfiguration;

        public SchedularConfigurationBuilder(SchedularConfiguration schedularConfiguration) {
            this.schedularConfiguration = schedularConfiguration;
        }

        public  static SchedularConfigurationBuilder init() {
            return new SchedularConfigurationBuilder(new SchedularConfiguration());
        }

        public SchedularConfigurationBuilder setRetries(int retries) {
             this.schedularConfiguration.retries = retries;
             return new SchedularConfigurationBuilder(this.schedularConfiguration);
        }

        public SchedularConfigurationBuilder setDelayOnStart(long timeInMillis) {
            this.schedularConfiguration.delayedStart = timeInMillis;
            return new SchedularConfigurationBuilder(this.schedularConfiguration);
        }

        public SchedularConfigurationBuilder setPoolSize(int size) {
            this.schedularConfiguration.poolSize = size;
            return new SchedularConfigurationBuilder(this.schedularConfiguration);
        }

        public SchedularConfigurationBuilder setJobTimeout(int timeoutInMillis) {
            this.schedularConfiguration.jobTimeout = timeoutInMillis;
            return new SchedularConfigurationBuilder(this.schedularConfiguration);
        }

        public SchedularConfigurationBuilder setBackground(boolean background) {
            this.schedularConfiguration.background = background;
            return new SchedularConfigurationBuilder(this.schedularConfiguration);
        }

        public SchedularConfigurationBuilder setScheduleTimeout(long scheduleTimeout) {
            this.schedularConfiguration.scheduleTimeout = scheduleTimeout;
            return new SchedularConfigurationBuilder(this.schedularConfiguration);
        }

        public SchedularConfigurationBuilder setPollTime(long time) {
            this.schedularConfiguration.pollTime = time;
            return new SchedularConfigurationBuilder(this.schedularConfiguration);
        }

        public SchedularConfiguration build(){
            return this.schedularConfiguration;
        }
    }
    private int retries;
    private long delayedStart;
    private int jobTimeout;
    private int poolSize;
    private boolean background;
    private long pollTime;

    public int getRetries() {
        return retries;
    }

    public long isDelayedStart() {
        return delayedStart;
    }
}

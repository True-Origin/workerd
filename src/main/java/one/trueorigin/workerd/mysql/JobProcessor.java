package one.trueorigin.workerd.mysql;

import one.trueorigin.workerd.Processor;

public class JobProcessor<T> implements Processor<T> {

    private final Class<T> type;

    public JobProcessor(final Class<T> type) {
        this.type = type;
    }

    @Override
    public Boolean process(T payload) {
        return true;
    }

    @Override
    public Class<T> getProcessorClass() {
        return type;
    }
}

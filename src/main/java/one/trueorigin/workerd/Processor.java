package one.trueorigin.workerd;

public interface Processor<T> {
    public Boolean process(T payload);
    public Class<T> getProcessorClass();
}

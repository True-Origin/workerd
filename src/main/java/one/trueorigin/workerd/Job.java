package one.trueorigin.workerd;

public class Job<T> {

    private final T payload;
    private final String type;

    public Job(final String type, final T payload) {
        this.type = type;
        this.payload = payload;
    }

    public T getPayload() {
        return payload;
    }

    public String getType() {
        return type;
    }


}

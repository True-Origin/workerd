package one.trueorigin.workerd;

import org.ocpsoft.prettytime.PrettyTime;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectParser {
    private static ObjectMapper objectMapper;
    private static PrettyTime prettyTime;

    public synchronized static ObjectMapper parse() {
        if(objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }

    public synchronized static PrettyTime prettifyTime() {
        if(prettyTime == null) {
            prettyTime = new PrettyTime();
        }
        return prettyTime;
    }
}

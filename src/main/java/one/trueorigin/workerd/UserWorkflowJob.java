package one.trueorigin.workerd;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

//TODO
public class UserWorkflowJob {

    public UserWorkflowJob(String userId, String workflowId) {
        this.userId = userId;
        this.workflowId = workflowId;
    }

    @JsonSerialize
    private String userId;

    public String getUserId() {
        return userId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public UserWorkflowJob() {
    }

    @JsonSerialize
    private String workflowId;
}

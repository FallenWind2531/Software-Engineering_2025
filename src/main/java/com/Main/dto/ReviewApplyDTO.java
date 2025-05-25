package com.Main.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReviewApplyDTO {
    @JsonProperty("audit_status")
    private int auditStatus; // 0-待审核, 1-通过, 2-拒绝

    public ReviewApplyDTO() {
    }
    // Getter 和 Setter
    public int getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(int auditStatus) {
        this.auditStatus = auditStatus;
    }
}

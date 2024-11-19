package unisinos.model.message;

import unisinos.model.results.OffloadingInfo;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class EdgeMessage {

    private UUID id;
    private String userId;
    private Integer userPriority;
    private ServiceType service;
    private VitalSign vitalSign;
    private List<OffloadingInfo> offloadedBy;
    private Instant firstArrivalAtNode;

    public EdgeMessage ()
    {
    }

    public EdgeMessage (UUID id,
                        String userId,
                        Integer userPriority,
                        ServiceType service,
                        VitalSign vitalSign,
                        List<OffloadingInfo> offloadedBy,
                        Instant firstArrivalAtNode)
    {
        this.id = id;
        this.userId = userId;
        this.userPriority = userPriority;
        this.service = service;
        this.vitalSign = vitalSign;
        this.offloadedBy = offloadedBy;
        this.firstArrivalAtNode = firstArrivalAtNode;
    }

    public UUID getId ()
    {
        return id;
    }

    public void setId (UUID id)
    {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getUserPriority() {
        return userPriority;
    }

    public void setUserPriority(Integer userPriority) {
        this.userPriority = userPriority;
    }

    public ServiceType getService() {
        return service;
    }

    public void setService (ServiceType service) {
        this.service = service;
    }

    public VitalSign getVitalSign() {
        return vitalSign;
    }

    public void setVitalSign(VitalSign vitalSign) {
        this.vitalSign = vitalSign;
    }

    public List<OffloadingInfo> getOffloadedBy() {
        return offloadedBy;
    }

    public void setOffloadedBy(List<OffloadingInfo> offloadedBy) {
        this.offloadedBy = offloadedBy;
    }

    public Instant getFirstArrivalAtNode() {
        return firstArrivalAtNode;
    }

    public void setFirstArrivalAtNode(Instant firstArrivalAtNode) {
        this.firstArrivalAtNode = firstArrivalAtNode;
    }
}

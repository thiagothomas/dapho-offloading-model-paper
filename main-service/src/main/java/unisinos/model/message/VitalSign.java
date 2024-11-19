package unisinos.model.message;

public class VitalSign {

    private Integer respRate;
    private Boolean hypercapnicFailure;
    private Double o2Sat;
    private Boolean o2Supplement;
    private Double temperature;
    private Integer systolicBp;
    private Integer pulseRate;
    private String consciousness;

    public VitalSign() {
    }

    public VitalSign(Integer respRate,
                     Boolean hypercapnicFailure,
                     Double o2Sat,
                     Boolean o2Supplement,
                     Double temperature,
                     Integer systolicBp,
                     Integer pulseRate,
                     String consciousness) {
        this.respRate = respRate;
        this.hypercapnicFailure = hypercapnicFailure;
        this.o2Sat = o2Sat;
        this.o2Supplement = o2Supplement;
        this.temperature = temperature;
        this.systolicBp = systolicBp;
        this.pulseRate = pulseRate;
        this.consciousness = consciousness;
    }

    public Integer getRespRate() {
        return respRate;
    }

    public void setRespRate(Integer respRate) {
        this.respRate = respRate;
    }

    public Boolean getHypercapnicFailure() {
        return hypercapnicFailure;
    }

    public void setHypercapnicFailure(Boolean hypercapnicFailure) {
        this.hypercapnicFailure = hypercapnicFailure;
    }

    public Double getO2Sat() {
        return o2Sat;
    }

    public void setO2Sat(Double o2Sat) {
        this.o2Sat = o2Sat;
    }

    public Boolean getO2Supplement() {
        return o2Supplement;
    }

    public void setO2Supplement(Boolean o2Supplement) {
        this.o2Supplement = o2Supplement;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getSystolicBp() {
        return systolicBp;
    }

    public void setSystolicBp(Integer systolicBp) {
        this.systolicBp = systolicBp;
    }

    public Integer getPulseRate() {
        return pulseRate;
    }

    public void setPulseRate(Integer pulseRate) {
        this.pulseRate = pulseRate;
    }

    public String getConsciousness() {
        return consciousness;
    }

    public void setConsciousness(String consciousness) {
        this.consciousness = consciousness;
    }
}
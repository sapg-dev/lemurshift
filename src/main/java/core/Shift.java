package core;



import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Shift {
    private String shiftId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Employee assignedEmployee;
    private Map<String, Object> additionalAttributes;

    public Shift(String shiftId, LocalDateTime startTime, LocalDateTime endTime) {
        this.shiftId = shiftId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.additionalAttributes = new HashMap<>();
    }

    // Getters and setters for fixed attributes
    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Employee getAssignedEmployee() {
        return assignedEmployee;
    }

    public void setAssignedEmployee(Employee assignedEmployee) {
        this.assignedEmployee = assignedEmployee;
    }

    // Methods to manage additional attributes
    public void setAdditionalAttribute(String key, Object value) {
        additionalAttributes.put(key, value);
    }

    public Object getAdditionalAttribute(String key) {
        return additionalAttributes.get(key);
    }

    public boolean hasAdditionalAttribute(String key) {
        return additionalAttributes.containsKey(key);
    }

    public Object removeAdditionalAttribute(String key) {
        return additionalAttributes.remove(key);
    }

    // Example usage of a toString method to print out the shift's properties
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Shift{shiftId='").append(shiftId).append("', startTime=").append(startTime).append(", endTime=").append(endTime).append(", assignedEmployee=").append(assignedEmployee != null ? assignedEmployee.getName() : "none").append(", additionalAttributes=").append(additionalAttributes).append('}');
        return sb.toString();
    }
}
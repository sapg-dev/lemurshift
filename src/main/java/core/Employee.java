package core;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Employee {
    private String id;
    
    private String name;
    private Set<DayOfWeek> daysOff; // Days when the employee is not available to work
    private int maxConsecutiveShifts; // The maximum number of consecutive shifts allowed
    private LocalDateTime lastShiftEndTime; // The end time of the last shift worked
    private int currentConsecutiveShiftCount; // The current count of consecutive shifts worked

    public Employee(String id, String name, Set<DayOfWeek> daysOff, int maxConsecutiveShifts) {
        this.id = id;
        this.name = name;
        this.daysOff = daysOff;
        this.maxConsecutiveShifts = maxConsecutiveShifts;
        this.lastShiftEndTime = null;
        this.currentConsecutiveShiftCount = 0;
    }

    // Standard getters and setters

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<DayOfWeek> getDaysOff() {
        return new HashSet<>(daysOff); // Return a copy to prevent outside modification
    }

    public int getMaxConsecutiveShifts() {
        return maxConsecutiveShifts;
    }

    public LocalDateTime getLastShiftEndTime() {
        return lastShiftEndTime;
    }

    public int getCurrentConsecutiveShiftCount() {
        return currentConsecutiveShiftCount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDaysOff(Set<DayOfWeek> daysOff) {
        this.daysOff = new HashSet<>(daysOff); // Store a copy to prevent outside modification
    }

    public void setMaxConsecutiveShifts(int maxConsecutiveShifts) {
        this.maxConsecutiveShifts = maxConsecutiveShifts;
    }

    public void setLastShiftEndTime(LocalDateTime lastShiftEndTime) {
        this.lastShiftEndTime = lastShiftEndTime;
    }

    public void setCurrentConsecutiveShiftCount(int currentConsecutiveShiftCount) {
        this.currentConsecutiveShiftCount = currentConsecutiveShiftCount;
    }

    // Method to check if the employee is available for a given shift
    public boolean isAvailable(LocalDateTime shiftTime) {
        DayOfWeek shiftDay = shiftTime.getDayOfWeek();
        return !daysOff.contains(shiftDay) && (lastShiftEndTime == null || !shiftTime.isBefore(lastShiftEndTime.plusHours(12)));
    }
    
    public boolean isForcedToWork(Shift shift) {
        boolean isDayOff = daysOff.contains(shift.getStartTime().getDayOfWeek());
        boolean isWithinRestPeriod = lastShiftEndTime != null && shift.getStartTime().isBefore(lastShiftEndTime.plusHours(12));
        return isDayOff || isWithinRestPeriod;
    }

    // Method to update employee's shift end time and consecutive shift count
    public void updateShiftEndAndCount(LocalDateTime shiftEnd) {
        this.lastShiftEndTime = shiftEnd;
        this.currentConsecutiveShiftCount++;
        // Check if the employee needs a reset on their consecutive shift count
        if (this.currentConsecutiveShiftCount > this.maxConsecutiveShifts) {
            this.currentConsecutiveShiftCount = 0; // or handle as per policy
        }
    }

    // Additional logic and methods as necessary
}
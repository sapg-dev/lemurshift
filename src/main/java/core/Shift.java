package core;



import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Shift {
    private String id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Constructor using string inputs for start and end time, parsing them into LocalDateTime objects
    public Shift(String id, String start, String end) {
        this.id = id;
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        this.startTime = LocalDateTime.parse(start, formatter);
        this.endTime = LocalDateTime.parse(end, formatter);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    // Helper method to check if a shift occurs on a given day
    public boolean isOnDay(String day) {
        DateTimeFormatter dayFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        return this.startTime.toLocalDate().toString().equals(day);
    }

    // Override toString() for easier logging and debugging
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm");
        return String.format("Shift ID: %s, Start Time: %s, End Time: %s", 
                getId(), 
                getStartTime().format(formatter), 
                getEndTime().format(formatter));
    }

    // Other methods can be added as required to facilitate shift management
}


package functionality;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import core.Shift;

public class Generate {
	
	
	
	//Before assigning any employees, it is nice to see a template
	
	public List<Shift> generateShiftTemplates(LocalDateTime scheduleStart, LocalDateTime scheduleEnd, int shiftDurationHours) {
        List<Shift> shifts = new ArrayList<>();

        // Ensure that the schedule start time is before the end time and shift duration is positive.
        if (scheduleStart.isAfter(scheduleEnd) || shiftDurationHours <= 0) {
            throw new IllegalArgumentException("Invalid schedule start/end time or shift duration.");
        }

        LocalDateTime shiftStart = scheduleStart;
        // Generate shifts until the entire schedule period is covered.
        while (shiftStart.isBefore(scheduleEnd)) {
            // Calculate the end time for this shift. It's the start time plus the shift duration.
            LocalDateTime shiftEnd = shiftStart.plusHours(shiftDurationHours);

            // If the calculated end time is after the schedule end time, set it to the schedule end time.
            if (shiftEnd.isAfter(scheduleEnd)) {
                shiftEnd = scheduleEnd;
            }

            // Create a new shift with the calculated start and end time.
            Shift shift = new Shift(null, shiftStart, shiftEnd); // Shift ID is null for a template.
            shifts.add(shift);

            // Move to the next shift start time.
            shiftStart = shiftEnd;
        }

        return shifts;
    }
	
	
	public static void printScheduleToConsole(List<Shift> shifts) {
        // Assuming a fixed column width, adjust as necessary.
        System.out.printf("%-20s %-30s %-30s%n", "Shift ID", "Start Time", "End Time");
        
        for (Shift shift : shifts) {
            // Print each shift in a row.
            System.out.printf("%-20s %-30s %-30s%n",
                shift.getShiftId() != null ? shift.getShiftId() : "Template",
                shift.getStartTime().toString(),
                shift.getEndTime().toString());
        }
    }
	
	public static void main(String[] args) {
		
		LocalDateTime scheduleStart = LocalDateTime.of(2023, 11, 1, 8, 0); // Nov 1, 2023, 08:00
        LocalDateTime scheduleEnd = LocalDateTime.of(2023, 11, 1, 20, 0); // Nov 1, 2023, 20:00
        int shiftDurationHours = 2;
		Generate generate = new Generate();
		List<Shift> shiftTemplates = generate.generateShiftTemplates(scheduleStart, scheduleEnd, shiftDurationHours);
		printScheduleToConsole(shiftTemplates);
	}

}

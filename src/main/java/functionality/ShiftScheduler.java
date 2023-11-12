package functionality;

import java.io.IOException;
import java.io.PrintStream;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import core.CSVUtils;
import core.Employee;

import core.Shift;

import java.util.*;


/*
 * 
 * 
 * 
 * The ShiftScheduler class is responsible for assigning employees to shifts based on their availability and ensuring a fair workload distribution. Let's walk through the class's workflow and method interactions in chronological order:

Constructor - ShiftScheduler(List<Employee> employees):

When a ShiftScheduler object is instantiated, it receives a list of Employee objects as an argument.
It initializes shiftPriorityQueue, a PriorityQueue that orders employees based on the number of shifts already assigned to them (the fewer the shifts, the higher the priority for new assignments). This helps in ensuring a balanced distribution of work.
The constructor adds all passed employees to this queue, preparing the scheduler for the assignment process.
Shift Assignment - assignShifts(List<Shift> shifts):

This method is called with a list of Shift objects that need to be assigned to employees.
It iterates over each Shift and calls findEligibleEmployeeForShift(Shift shift) to find an employee who can work on this shift.
Once an eligible employee is found, assignShiftToEmployee(Employee employee, Shift shift) is called to officially assign the shift to that employee.
Finding an Eligible Employee - findEligibleEmployeeForShift(Shift shift):

This method is invoked by assignShifts to find an employee who is both available and has not exceeded their maximum allowed consecutive shifts.
It checks each employee in the shiftPriorityQueue for availability during the shift's time. The priority queue ensures that it starts with employees who have the least number of assigned shifts.
Availability is determined by calling employee.isAvailable(LocalDateTime shiftTime) and canWorkShift(Shift shift, Employee employee) for each employee. If both return true, the employee is considered eligible.
Checking Work Shift Eligibility - canWorkShift(Shift shift, Employee employee):

This method is used to implement any additional business rules for shift assignment beyond basic availability. For instance, it may check for overlapping shifts or ensure compliance with labor regulations regarding shift spacing.
It's used within findEligibleEmployeeForShift to provide a secondary check after basic availability is confirmed.
Assigning Shift to Employee - assignShiftToEmployee(Employee employee, Shift shift):

This method takes a specific Employee object and a Shift object and assigns the shift to the employee.
It updates the employee's record with the end time of the new shift and increments their consecutive shift count.
The shift is added to the employee's list of assigned shifts in the employeeShifts map, which tracks which shifts are assigned to which employees.
After the shift is assigned, refreshPriorityQueue() is called to reflect the change in the number of shifts assigned to employees, ensuring that the queue remains accurate for subsequent assignments.
Getting Assigned Shifts - getAssignedShifts(Employee employee):

This method is not directly involved in the shift assignment process but is used to retrieve the list of shifts assigned to a specific employee. It's useful for outputting the schedule after all assignments have been made or for any other operation that requires inspection of an employee's workload.
Refreshing the Priority Queue - refreshPriorityQueue():

This method is invoked after a shift is assigned to an employee to update the shiftPriorityQueue.
It's necessary because the priority of each employee might have changed due to the new shift assignment. The method rebuilds the queue so that it reflects the current state of shift assignments.
This is a simple but potentially inefficient way to maintain the priority queue, as the entire structure is cleared and rebuilt. For a large number of employees or frequent updates, a different approach might be required to optimize performance.
The class methods operate in a coordinated manner to ensure that shifts are distributed as evenly as possible according to the predefined rules of availability and fairness. The actual process of determining if an employee can work a shift (canWorkShift method) should be fleshed out with all the necessary logic specific to the application's requirements. The ShiftScheduler is a framework that can be adapted to different scheduling needs with additional logic as required.
*/


public class ShiftScheduler {
 private Map<Employee, List<Shift>> employeeShifts = new HashMap<>();
 private PriorityQueue<Employee> shiftPriorityQueue; // Define comparator based on the least number of shifts

 public ShiftScheduler(List<Employee> employees) {
     // Initialize priority queue with a comparator based on the number of assigned shifts
     shiftPriorityQueue = new PriorityQueue<>(Comparator.comparingInt(e -> getAssignedShifts(e).size()));
     shiftPriorityQueue.addAll(employees);
 }
 
 public void assignShifts(List<Shift> shifts) {
	    for (Shift shift : shifts) {
	        Optional<Employee> eligibleEmployee = findEligibleEmployeeForShift(shift);
	        if (eligibleEmployee.isPresent()) {
	            // Regular assignment
	            assignShiftToEmployee(eligibleEmployee.get(), shift, false);
	        } else {
	            // Forced assignment as no eligible employee was found
	            Optional<Employee> forcedEmployee = findEligibleEmployeeForced(shift);
	            forcedEmployee.ifPresent(employee -> assignShiftToEmployee(employee, shift, true));
	        }
	    }
	}
 private Optional<Employee> findEligibleEmployeeForShift(Shift shift) {
	    List<Employee> tempChecked = new ArrayList<>(); // Temporary storage for checked employees
	    while (!shiftPriorityQueue.isEmpty()) {
	        Employee employee = shiftPriorityQueue.poll(); // Retrieve and remove the head of the queue
	        tempChecked.add(employee); // Add to temp storage

	        if (employee.isAvailable(shift.getStartTime()) && canWorkShift(shift, employee)) {
	            // Assign the shift and exit
	            return Optional.of(employee);
	        }
	    }

	    // Re-add all checked employees back to the queue if they weren't assigned
	    shiftPriorityQueue.addAll(tempChecked);

	    System.out.println("No eligible employee found for shift starting at " + shift.getStartTime());
	    return Optional.empty();
	}
 private void assignShiftToEmployee(Employee employee, Shift shift, boolean isForced) {
	    List<Shift> assignedShifts = employeeShifts.computeIfAbsent(employee, k -> new ArrayList<>());
	    assignedShifts.add(shift);

	    // Update the employee's last shift end time and reset consecutive shift count if needed
	    employee.setLastShiftEndTime(shift.getEndTime()); // Correctly updates last shift end time
	    if(employee.getCurrentConsecutiveShiftCount() >= employee.getMaxConsecutiveShifts()) {
	        employee.setCurrentConsecutiveShiftCount(0); // Resets the shift count
	    } else {
	        employee.setCurrentConsecutiveShiftCount(employee.getCurrentConsecutiveShiftCount() + 1);
	    }

	    // Add a note to the shift if it's forced due to under-staffing.
	    if (isForced) {
	        System.out.println("Note: " + employee.getName() + " was forced to work on " + shift.getStartTime().getDayOfWeek() + " or during rest period for shift: " + shift);
	    }

	    refreshPriorityQueue();
	}

 private List<Shift> getAssignedShifts(Employee employee) {
     return employeeShifts.getOrDefault(employee, Collections.emptyList());
 }

 private void refreshPriorityQueue() {
	    // This list will hold the employees temporarily while the priority queue is being refreshed.
	    List<Employee> employees = new ArrayList<>(shiftPriorityQueue);

	    // Clear the existing priority queue
	    shiftPriorityQueue.clear();

	    // Re-add all employees to the priority queue which will reorder them based on the comparator defined in the priority queue's constructor.
	    shiftPriorityQueue.addAll(employees);
	}

 private boolean canWorkShift(Shift shift, Employee employee) {
	    // Check if the employee has reached the maximum number of consecutive shifts
	    if (employee.getCurrentConsecutiveShiftCount() >= employee.getMaxConsecutiveShifts()) {
	        return false;
	    }

	    // Check if the shift overlaps with any shifts the employee is already assigned to
	    for (Shift assignedShift : getAssignedShifts(employee)) {
	        if (shift.getStartTime().isBefore(assignedShift.getEndTime()) && shift.getEndTime().isAfter(assignedShift.getStartTime())) {
	            return false; // Shift overlaps, so the employee cannot work this shift
	        }
	    }

	    // Add any other conditions you need to check before confirming an employee can work a shift
	    // For example, qualifications, preferences, etc.

	    return true; // If none of the conditions fail, the employee can work the shift
	}
 
 public static List<Shift> generateShiftTemplates(LocalDateTime scheduleStart, LocalDateTime scheduleEnd, int shiftDurationHours) {
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

         // If the calculated end time is after the schedule end time, adjust it to the schedule end time.
         if (shiftEnd.isAfter(scheduleEnd)) {
             shiftEnd = scheduleEnd;
         }

         // Create a new shift with the calculated start and end time.
         Shift shift = new Shift(null, shiftStart.toString(), shiftEnd.toString()); // Shift ID is null for a template.
         shifts.add(shift); // Add to the shifts list

         // Prepare for the next shift, ensuring it starts immediately after the current shift ends.
         shiftStart = shiftEnd;
     }

     return shifts; // Return the generated list of shifts
 }
 
 

 
 
 private Optional<Employee> findEligibleEmployeeForced(Shift shift) {
	    // This will stream over the employees to find one that can be forced to take the shift due to understaffing.
	    // It ignores the day-off restriction but still prevents shift overlap.
	    return shiftPriorityQueue.stream()
	            .filter(e -> canWorkShiftIgnoringDaysOff(shift, e))
	            .findFirst();
	}
 
 private boolean canWorkShiftIgnoringDaysOff(Shift shift, Employee employee) {
	    // This method ignores the day off but still checks for shift overlap and maximum consecutive shifts.
	    boolean exceedsConsecutiveShifts = employee.getCurrentConsecutiveShiftCount() >= employee.getMaxConsecutiveShifts();
	    boolean hasShiftOverlap = employeeShifts.getOrDefault(employee, Collections.emptyList()).stream()
	            .anyMatch(existingShift -> shift.getStartTime().isBefore(existingShift.getEndTime()) && shift.getEndTime().isAfter(existingShift.getStartTime()));
	    
	    return !exceedsConsecutiveShifts && !hasShiftOverlap;
	}

public static void main(String[] args) {
    // Path to the CSV file containing employee data
    String csvFilePath = "C:\\\\Users\\\\sacha\\\\lemurshift\\\\src\\\\main\\\\java\\\\core\\\\employee_data.csv"; // Replace with your actual CSV file path

    // Headers for the employee data in the CSV file
    String employeeIdHeader = "Employee ID";
    String employeeFirstNameHeader = "First Name";
    String employeeLastNameHeader = "Last Name";

    try {
        // Use CSVUtils to parse employee data from the CSV file
        List<Employee> employees = CSVUtils.parseEmployeeDataFromCSV(
            csvFilePath, 
            employeeIdHeader, 
            employeeFirstNameHeader, 
            employeeLastNameHeader
        );

        // Use ShiftGenerator to create a list of shifts (method implementation not shown here)
        List<Shift> shifts = generateShiftTemplates(
            LocalDateTime.of(2023, 11, 1, 8, 0),
            LocalDateTime.of(2023, 11, 1, 20, 0),
            2 // Shift duration in hours
        );

        // Create a shift scheduler and assign shifts
        ShiftScheduler scheduler = new ShiftScheduler(employees);
        scheduler.assignShifts(shifts);
        for (Shift shift : shifts) {
            System.out.println(shift);
        }
        // Output the schedule
        for (Employee employee : employees) {
        	System.out.println(employee.getDaysOff());
            List<Shift> assignedShifts = scheduler.getAssignedShifts(employee);
            System.out.println(employee.getName() + " is assigned to the following shifts:");
            for (Shift assignedShift : assignedShifts) {
                System.out.println(assignedShift);
            }
            System.out.println(); // Print a newline for better readability
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}
}


	
	
	



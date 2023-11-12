package core;

import core.Employee;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.stream.Collectors;

public class CSVUtils {

	  public static List<Employee> parseEmployeeDataFromCSV(String filePath, String idHeader, String nameHeader, String lastNameHeader) throws IOException {
	        List<Employee> employees = new ArrayList<>();
	        Path pathToFile = Paths.get(filePath);

	        // Try-with-resources to ensure the reader is closed after use
	        try (BufferedReader br = Files.newBufferedReader(pathToFile)) {
	            String line = br.readLine(); // Read the header line

	            // Parse the header to find indexes using a method that accounts for optional headers
	            String[] headers = line.split(","); // Split the headers on comma
	            int idIndex = findHeaderIndex(headers, idHeader);
	            int nameIndex = findHeaderIndex(headers, nameHeader);
	            int lastNameIndex = findHeaderIndex(headers, lastNameHeader);
	            int daysOffIndex = findHeaderIndex(headers, "Days Off"); // 'Days Off' column is mandatory and fixed

	            while ((line = br.readLine()) != null) { // Read until the end of the file
	                // Split line using regex to handle commas inside quotes
	                String[] attributes = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

	                // Check if indexes are found, if not, default to a safe value or handle error
	                String id = (idIndex != -1) ? attributes[idIndex].replaceAll("\"", "") : ""; // Handle missing ID
	                String firstName = (nameIndex != -1) ? attributes[nameIndex].replaceAll("\"", "") : "";
	                String lastName = (lastNameIndex != -1) ? attributes[lastNameIndex].replaceAll("\"", "") : "";
	                String fullName = firstName + " " + lastName;

	                Set<DayOfWeek> daysOff = parseDaysOff(attributes[daysOffIndex].replaceAll("\"", "")); // Remove quotes

	                // Create employee object and add it to the list
	                Employee employee = new Employee(id, fullName, daysOff, 5); // Assuming '5' is maxConsecutiveShifts
	                employees.add(employee);
	            }
	        }

	        return employees;
	    }


	  private static int findHeaderIndex(String[] headers, String headerName) {
		    // If headerName is provided, find and return its index.
		    if (headerName != null && !headerName.isEmpty()) {
		        for (int i = 0; i < headers.length; i++) {
		            if (headerName.equalsIgnoreCase(headers[i].trim())) {
		                return i;
		            }
		        }
		    }
		    // If the headerName is not provided or not found, return -1.
		    return -1;
		}
    private static Set<DayOfWeek> parseDaysOff(String daysOffString) {
        Set<DayOfWeek> daysOff = EnumSet.noneOf(DayOfWeek.class);


        // Remove leading and trailing quotes if they exist and then split by commas
        daysOffString = daysOffString.replaceAll("^\"|\"$", "");
        String[] days = daysOffString.split("\\s*,\\s*");

        // Debugging line: Print the split days before adding to the set
        

        for (String day : days) {
            try {
                daysOff.add(DayOfWeek.valueOf(day.toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid day of week in CSV: " + day);
            }
        }
        return daysOff;
    }
    public static void main(String[] args) {
        CSVUtils csvUtils = new CSVUtils(); // Assuming CSVUtils provides the readEmployeesFromCsv method.
        String csvFilePath = "C:\\Users\\sacha\\lemurshift\\src\\main\\java\\core\\employees.csv"; // Replace with your actual CSV file path.

        // Optional headers - assuming you have a CSV with 'Employee ID', 'First Name', 'Last Name'
        String employeeIdHeader = "Employee ID"; // Could be replaced with any other column name you wish to use as ID
        String employeeFirstNameHeader = "First Name";
        String employeeLastNameHeader = "Last Name";

        try {
            // Read employees from the CSV file
            List<Employee> employees = CSVUtils.parseEmployeeDataFromCSV(
                    csvFilePath, 
                    employeeIdHeader, 
                    employeeFirstNameHeader, 
                    employeeLastNameHeader);

            // Print the list of employees
            for (Employee employee : employees) {
                System.out.println(employee.getName());
              
            }
            
            for (Employee employee : employees) {
            	 Set<DayOfWeek> daysOff = employee.getDaysOff();
            	   int numberOfDaysOff = daysOff.size();
            	   System.out.println(numberOfDaysOff);
                // Collects all days off into a string separated by commas
                String daysOffString = employee.getDaysOff().stream()
                                               .map(day -> day.toString())
                                               .collect(Collectors.joining(", "));
                System.out.println("Employee ID: " + employee.getId() + ", Name: " + employee.getName() + ", Days Off: " + daysOffString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
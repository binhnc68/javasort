package com.javacore.comparator2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
// sort value null Comparator.nullsFirst(Comparator.naturalOrder())
public class main {

	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		
		SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );  
		// create list Employees
		List<Employee> listEmployees = new ArrayList<Employee>();
		// add Employees to list
		listEmployees.add(new Employee(1, "Vinh", 19, "Hanoi", dateFormat.parse("2021-01-01")));
		listEmployees.add(new Employee(1, "Vinh", 19, "Hanoi", dateFormat.parse("2021-01-01")));
		listEmployees.add(new Employee(2, "An", 19, "Hanoi", dateFormat.parse("2021-01-01")));
		listEmployees.add(new Employee(2, "Hoa", 25, "Hanoi", dateFormat.parse("2021-01-01")));
		listEmployees.add(new Employee(2, "Hoa", 19, "2Hanoi", dateFormat.parse("2021-02-01")));
		listEmployees.add(new Employee(2, "Hoa", 19, "Hanoi", null));

		System.out.printf("----------id->Name->Age-------------");
		// Compare by first name and then last name
		// Comparator.nullsFirst(Comparator.naturalOrder() sort value null
		Comparator<Employee> compareByIdNameAge = Comparator
				.comparing(Employee::getId)
				.thenComparing(Employee::getName)
				.thenComparing(Employee::getAge)
				.thenComparing(Employee::getDate, Comparator.nullsFirst(
                        Comparator.naturalOrder()));

		List<Employee> sortedEmployees = listEmployees.stream().sorted(compareByIdNameAge).collect(Collectors.toList());
		// show list students
		for (Employee employee : sortedEmployees) {
			System.out.println(employee.toString());
		}
		
		
	}
	
	// https://www.programcreek.com/java-api-examples/?class=java.util.Comparator&method=nullsFirst
}

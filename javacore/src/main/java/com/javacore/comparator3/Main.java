package com.javacore.comparator3;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import com.javacore.comparator3.SortedEmployee;
// sort customer
public class Main {

//	private static SortedEmployee sortedEmployee;
	public static void main(String[] args) throws ParseException {
		List<String> sortByMulti = new ArrayList<>();
		sortByMulti.add("id");
		sortByMulti.add("name");
		sortByMulti.add("age");
		sortByMulti.add("date");
		
		SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );  
//		// create list Employees
		List<Employee> listEmployees = new ArrayList<Employee>();
//		// add Employees to list
		listEmployees.add(new Employee(1, "Vinh", 19, "Hanoi", dateFormat.parse("2021-01-01")));
		listEmployees.add(new Employee(1, "Vinh", 19, "Hanoi", dateFormat.parse("2021-01-02")));
		listEmployees.add(new Employee(2, "An", 19, "Hanoi", dateFormat.parse("2021-01-03")));
		listEmployees.add(new Employee(2, "Hoa", 25, "Hanoi", dateFormat.parse("2021-01-04")));
		listEmployees.add(new Employee(2, "Hoa", 19, "2Hanoi", dateFormat.parse("2021-02-05")));
		listEmployees.add(new Employee(2, "Hoa", 19, "Hanoi", null));

		System.out.printf("----------id->Name->Age-------------");
		Main main = new Main();
		// Compare by first name and then last name	
		List<Employee> sortedEmployee = main.sortedEmployeeList(listEmployees, sortByMulti);

//		
//		Comparator<Employee> compareByIdNameAge = Comparator
//				.comparing(Employee::getId)
//				.thenComparing(Employee::getName)
//				.thenComparing(Employee::getAge)
//				.thenComparing(Employee::getDate, Comparator.nullsFirst(
//                        Comparator.naturalOrder()));
//
//		List<Employee> sortedEmployees = listEmployees.stream().sorted(compareByIdNameAge).collect(Collectors.toList());
//		// show list sortedEmployee
		for (Employee employee : sortedEmployee) {
			System.out.println(employee.toString());
		}
//		
		
	}
//	
	private List<Employee> sortedEmployeeList(List<Employee> listEmployees, List<String> sortByMulti) {
		List<Employee> sortedEmployeeList = listEmployees
	    .stream()
	    .sorted(employeeComparator(sortByMulti))
	    .collect(Collectors.toList());
		return sortedEmployeeList;
	}
	private Comparator<Employee> employeeComparator(List<String> sortByMulti) {
		return (a1, a2) -> {
			if (a1 == null) {
				return -1;
			}

			if (a2 == null) {
				return 1;
			}

			int result = 0;
			for (String sortType : sortByMulti) {
				switch (sortType) {
				case "id":
					result = Long.compare(a1.getId(), a2.getId());
					break;
				case "name":
					result = a1.getName().compareTo(a2.getName());
					break;
				case "date":
					result = dateCompareNullFirst(a1.getDate(), a2.getDate());
					break;
//				default:
//					if (a1.getTitle() == null) {
//						return -1;
//					}
//
//					if (a2.getTitle() == null) {
//						return 1;
//					}
//
//					result = a1.getTitle().toLowerCase().compareTo(a2.getTitle().toLowerCase());
				}

				if (result != 0) {
					return result;
				}
			}

			return result;
		};
	}
	
	private int dateCompareNullFirst(Date a1, Date a2) {
		if (a1 == null) {
			return (a2 == null) ? 0 : -1;
		} else if (a2 == null)
			return 1;
		else
			return a1.compareTo(a2);

	}
	
	public Comparator<Integer> getComparator()
	{
		return (a1, a2) -> {
			if (a1 == null) {
				return -1;
			}

			if (a2 == null) {
				return 1;
			}
			return a1;
		};
	}
	
	// https://www.programcreek.com/java-api-examples/?class=java.util.Comparator&method=nullsFirst
}

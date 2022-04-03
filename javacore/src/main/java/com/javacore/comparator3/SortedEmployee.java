package com.javacore.comparator3;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SortedEmployee {
	private List<String> sortByMulti;

	
	public List<Employee> sortedEmployeeList() throws ParseException {
		
		sortByMulti.add("id");
		sortByMulti.add("name");
		sortByMulti.add("age");
		sortByMulti.add("date");
		
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
		for (Employee employee : listEmployees) {
			System.out.println("1.employee: " + employee.toString());
		}
		List<Employee> sortedEmployeeList = listEmployees
	    .stream()
	    .sorted(employeeComparator())
	    .collect(Collectors.toList());
		
		for (Employee employee : sortedEmployeeList) {
			System.out.println("employee: " + employee.toString());
		}
		return sortedEmployeeList;
	}
	private Comparator<Employee> employeeComparator() {
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
//				case "name":
//					result = compare(a1.getName(), a2.getName());
//					
//					break;
//				case "size":
//					result = Long.compare(a1.getFileSize(), a2.getFileSize());
//					break;
				case "date":
					result = dateCompareNullFirst(a1.getDate(), a2.getDate());
					break;
//				case "name":
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
	
	public Comparator<Integer> getComparator()
	{
//	 return Comparator.naturalOrder();
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

	private int dateCompareNullFirst(Date a1, Date a2) {
		if (a1 == null)
			return (a2 == null) ? 0 : -1;
		else if (a2 == null)
			return 1;
		else
			return a1.compareTo(a2);
	}
}

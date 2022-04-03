package com.javacore.comparator;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.javacore.comparator.Employee;

// sort more param
public class main {
    public static void main(String[] args) {

            // create list students
            List<Employee> listStudents = new ArrayList<Employee>();
            // add students to list
            listStudents.add(new Employee(1, "Vinh", 19, "Hanoi"));
            listStudents.add(new Employee(2, "Hoa", 19, "Hanoi"));
            listStudents.add(new Employee(3, "Phu", 20, "Hanoi"));
            listStudents.add(new Employee(4, "Quy", 22, "Hanoi"));
            // sort list student
            Collections.sort(listStudents, new Comparator<Employee>() {
//            	@Override
                public int compare(Employee o1, Employee o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            // show list students
            for (Employee student : listStudents) {
                System.out.println(student.toString());
            }
                    
    }
}




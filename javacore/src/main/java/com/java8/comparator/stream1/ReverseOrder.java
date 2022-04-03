package com.java8.comparator.stream1;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ReverseOrder {
	public static void main(String[] args) {

        List<String> list = Arrays.asList("9", "A", "Z", "1", "B", "Y", "4", "a", "c");

        /*
		List<String> sortedList = list.stream()
			.sorted((o1,o2)-> o2.compareTo(o1))
			.collect(Collectors.toList());
		*/
		// sort decrease giam dan
        List<String> sortedList = list.stream()
			.sorted(Comparator.reverseOrder())
			.collect(Collectors.toList());

        sortedList.forEach(System.out::println);

    }
}
// ket qua
//c
//a
//Z
//Y
//B
//A
//9
//4
//1

package com.java8.comparator.stream1;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamApplication {

    public static void main(String[] args) {

        List<String> list = Arrays.asList("9", "A", "Z", "1", "B", "Y", "4", "a", "c");

        /* 
		List<String> sortedList = list.stream()
			.sorted(Comparator.naturalOrder())
			.collect(Collectors.toList());
			
        List<String> sortedList = list.stream()
			.sorted((o1,o2)-> o1.compareTo(o2))
			.collect(Collectors.toList());
		*/
        // sort ascending tang dan
		List<String> sortedList = list.stream().sorted().collect(Collectors.toList());
		
        sortedList.forEach(System.out::println);

    }
}
// ket qua
//1
//4
//9
//A
//B
//Y
//Z
//a
//c
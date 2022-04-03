package com.javacore.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Map<String, Map<String, String>> keyMap = new HashMap<String, Map<String, String>>();
		Map<String, String> map1 = new HashMap<>();
		map1.put("key", "key 2");
		map1.put("name", "name a");
		keyMap.put("keyMap1", map1);
		
		Map<String, String> map2 = new HashMap<>();
		map2.put("key", "key 5");
		map2.put("name", "name b");
		keyMap.put("keyMap2", map2);
		
		Map<String, String> map3 = new HashMap<>();
		map3.put("key", "key 4");
		map3.put("name", "name 1");
		keyMap.put("keyMap3", map3);
		System.out.println("sorts: " + keyMap);
		List<Map.Entry<String, Map<String, String>>> sort = new ArrayList<>();
		List<Map.Entry<String, Map<String, String>>> sortMap = new ArrayList<>(keyMap.entrySet());
		List<Map.Entry<String, Map<String, String>>> sort1 = new ArrayList<>(keyMap.entrySet());
		
//		Collections.sort(sortMap, new Comparator<Map.Entry<String, Map<String, String>>>() {
//			@Override
//			public int compare(Map.Entry<String, Map<String, String>> o1, Map.Entry<String, Map<String, String>> o2) {
//				return o1.getValue().get("key").compareToIgnoreCase(o2.getValue().get("key"));
//			}
//			
//		} 
//		);
		Collections.sort(sortMap, new Comparator<Map.Entry<String, Map<String, String>>>() {
			@Override
			public int compare(Map.Entry<String, Map<String, String>> o1, Map.Entry<String, Map<String, String>> o2) {
				return o1.getValue().get("key").compareTo(o2.getValue().get("key"));
			}
			
		});
		
		List<Map.Entry<String, Map<String, String>>> sortMap2 = new ArrayList<>(keyMap.entrySet());
		Collections.sort(sortMap2, new Comparator<Map.Entry<String, Map<String, String>>>() {
			public int compare(Map.Entry<String, Map<String, String>> o1, Map.Entry<String, Map<String, String>> o2) {
				return o1.getValue().get("key").compareTo(o2.getValue().get("key"));
			}
			
		});
		
		System.out.println("sorts2: " + sortMap2);

	}

}

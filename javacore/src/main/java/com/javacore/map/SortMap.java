package com.javacore.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SortMap {
	public static void main(String[] argv) {

		Map<String, Map<String, String>> mapSpaceKey = new HashMap<>();
		Map<String, String> obj1 = new HashMap<String, String>();
		// row 1
		obj1.put("key", "baseSpaceKey 1");
		obj1.put("title", "title B");
		obj1.put("ishonyakukanri", "1");
		mapSpaceKey.put("baseSpaceKey1", obj1);
		// row 2
		Map<String, String> obj2 = new HashMap<String, String>();
		obj2.put("key", "baseSpaceKey 2");
		obj2.put("title", "title A");
		obj2.put("ishonyakukanri", "2");
		mapSpaceKey.put("baseSpaceKey2", obj2);
		// row 3
		Map<String, String> obj3 = new HashMap<String, String>();
		obj3.put("key", "baseSpaceKey 3");
		obj3.put("title", "title c");
		obj3.put("ishonyakukanri", "3");
		mapSpaceKey.put("baseSpaceKey3", obj3);
		System.out.println("mapSpaceKey: " + mapSpaceKey);

		// sort by title
		List<Map.Entry<String, Map<String, String>>> sorts = new ArrayList<>(mapSpaceKey.entrySet());
		Collections.sort(sorts, new Comparator<Map.Entry<String, Map<String, String>>>() {
			@Override
			public int compare(Map.Entry<String, Map<String, String>> o1, Map.Entry<String, Map<String, String>> o2) {
				return o1.getValue().get("title").compareToIgnoreCase(o2.getValue().get("title"));
			}
		});
		
		System.out.println("sorts: " + sorts);

	}

}

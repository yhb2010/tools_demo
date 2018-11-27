package com.demo.tools.caffeine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class DataObject {

	private final String data;

	private static int objectCounter = 0;

	public static DataObject get(String data) {
		objectCounter++;
		return new DataObject(data);
	}

}

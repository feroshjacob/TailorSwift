package com.recipegrace.tailorswift.launch.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class KeyValuePair {

	private static final String NAME_VALUE_SEPARATOR = "=";

	private static final String PAIR_SEPARATOR = "\n";

	// Column constants
	public static final int NAME = 0;

	public static final int VALUE = 1;

	private String name;

	private String value;

	public String getName() {
		return name;
	}

	public KeyValuePair(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof KeyValuePair))
			return false;
		KeyValuePair pair = (KeyValuePair) obj;
		return (pair.name + pair.value).equals(this.name + this.value);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return (this.name + this.value).hashCode();
	}

	public static String mkString(Iterable<KeyValuePair> values){
		// if the array is null or empty return an empty string
		if(values == null || !values.iterator().hasNext())
			return "";
		
		// move all non-empty values from the original array to a new list (empty is a null, empty or all-whitespace string)
		List<String> nonEmptyVals = new LinkedList<String>();
		for (KeyValuePair val : values) {
			if(val != null && val.toString().trim().length() > 0){
				nonEmptyVals.add(val.getName()+NAME_VALUE_SEPARATOR+ val.getValue());
			}
		}
		
		// if there are no "non-empty" values return an empty string
		if(nonEmptyVals.size() == 0)
			return "";
		
		// iterate the non-empty values and concatenate them with the separator, the entire string is surrounded with "start" and "end" parameters
		StringBuilder result = new StringBuilder();
	
		int i = 0; 
		for (String val : nonEmptyVals) {
			if(i > 0)
				result.append(PAIR_SEPARATOR);
			result.append(val);
			i++;
		}
		
		return result.toString();
	}
	public static List<KeyValuePair> parseString(String content){
		// if the array is null or empty return an empty string
		if(content == null ||  content.toString().trim().length() <1)
			return new ArrayList<KeyValuePair>();
		
	     List<KeyValuePair> list = new ArrayList<KeyValuePair>();
		for(String pair : content.split(PAIR_SEPARATOR)) {
			list.add(new KeyValuePair(pair.split(NAME_VALUE_SEPARATOR)[0], pair.split(NAME_VALUE_SEPARATOR)[1]));
		}
		
		return list;
	}

}

package com.example.android_test_assignment_xml_parser.model.mapping.pojo;

import com.example.android_test_assignment_xml_parser.model.mapping.annotation.POJOListClass;

import java.util.ArrayList;

public class Trips {

	@POJOListClass(Trip.class)
	public ArrayList<Trip> trip;

	public Trips() {
	}
}

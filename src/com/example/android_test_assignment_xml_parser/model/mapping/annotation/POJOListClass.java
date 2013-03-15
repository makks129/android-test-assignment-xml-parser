package com.example.android_test_assignment_xml_parser.model.mapping.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface POJOListClass {
	Class<?> value();
}

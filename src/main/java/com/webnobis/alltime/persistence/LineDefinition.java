package com.webnobis.alltime.persistence;

public enum LineDefinition {

	R, E, I, A;

	public static String ATTRIBUTE_SEPARATOR = ";";

	public static String MISSING_VALUE = "-";

	public static String DAY_FORMAT = "dd.MM.yyyy";

	public static String TIME_FORMAT = "HH:mm";

	public static String TIME_FORMAT_WITH_DAYS = "dd:" + TIME_FORMAT;

}
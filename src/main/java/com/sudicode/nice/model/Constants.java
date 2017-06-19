package com.sudicode.nice.model;

import java.util.Objects;

/**
 * Constant values and strings.
 */
public class Constants {

    public static final double WINDOW_WIDTH = 1000;
    public static final double WINDOW_HEIGHT = 600;
    public static final String DB_NAME = Objects.requireNonNull(System.getenv("DB_NAME"));
    public static final String DB_USER = Objects.requireNonNull(System.getenv("DB_USER"));
    public static final String DB_PW = Objects.requireNonNull(System.getenv("DB_PW"));
    public static final String DB_SERVER = Objects.requireNonNull(System.getenv("DB_SERVER"));

}

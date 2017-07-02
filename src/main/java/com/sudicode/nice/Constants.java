package com.sudicode.nice;

import org.apache.commons.lang3.StringUtils;

/**
 * Constant values and strings.
 */
public class Constants {

    public static final double WINDOW_WIDTH = 1000;
    public static final double WINDOW_HEIGHT = 600;
    public static final String DB_NAME = StringUtils.defaultString(System.getenv("DB_NAME"), "nicedb");
    public static final String DB_USER = StringUtils.defaultString(System.getenv("DB_USER"), "root");
    public static final String DB_PW = System.getenv("DB_PW");
    public static final String DB_SERVER = StringUtils.defaultString(System.getenv("DB_SERVER"), "localhost");
    public static final int PLACEHOLDER_SIZE = 24;
    public static final String ICON_URL = Constants.class.getResource("wit.png").toExternalForm();
    public static final String STYLESHEET_URL = Constants.class.getResource("bootstrap3.css").toExternalForm();

}

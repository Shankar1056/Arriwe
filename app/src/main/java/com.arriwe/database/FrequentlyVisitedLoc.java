package com.arriwe.database;

public class FrequentlyVisitedLoc {
    // Labels table name
    public static final String TABLE = "FrequentlyVisitedLoc";
    public static final String TEMPTABLE = "TempFrequentlyVisitedLoc";

    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_place_name = "place_name";
    public static final String KEY_user_saved_name = "user_saved_name";
    public static final String KEY_Address = "address";
    public static final String KEY_lat = "latitude";
    public static final String KEY_long = "longitude";

    // Labels Table Columns names
    //Temp is a temporary varialbe to display last 5 searched location
    public static final String TEMP_KEY_ID = "temp_id";
    public static final String TEMP_KEY_place_name = "temp_place_name";
    public static final String TEMP_KEY_user_saved_name = "temp_user_saved_name";
    public static final String TEMP_KEY_Address = "temp_address";
    public static final String TEMP_KEY_lat = "temp_latitude";
    public static final String TEMP_KEY_long = "temp_longitude";



    public int visited_location_ID;
    public String place_name;
    public String user_saved_name;
    public String address;
    public Double latitude;
    public Double longitude;

    public int temp_visited_location_ID;
    public String temp_place_name;
    public String temp_user_saved_name;
    public String temp_address;
    public Double temp_latitude;
    public Double temp_longitude;



}
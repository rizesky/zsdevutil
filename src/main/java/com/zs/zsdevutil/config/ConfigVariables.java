package com.zs.zsdevutil.config;

public class ConfigVariables {
    private ConfigVariables(){
        throw new UnsupportedOperationException(getClass()+"is only for holding variables");
    }

    public static final String APP_ENV = "app.env" ;
    public static final String APP_NAME = "app.name";
    public static final String APP_NAME_LONG = "app.name.long";
}

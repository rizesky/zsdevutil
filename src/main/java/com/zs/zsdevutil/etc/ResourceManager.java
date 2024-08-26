package com.zs.zsdevutil.etc;

import java.net.URL;
import java.util.Objects;

public class ResourceManager {
    private static final String FXML_PATH = "/com/zs/zsdevutil/fxml/";
    private static final String CSS_PATH = "/com/zs/zsdevutil/css/";


    public static URL getFXML(String fxmlFileName) {
        return ResourceManager.class.getResource(FXML_PATH + fxmlFileName);
    }
    
    public static String getCSS(String cssFileName) {
        return Objects.requireNonNull(ResourceManager.class.getResource(CSS_PATH + cssFileName)).toExternalForm();
    }

}
open module com.zs.zsdevutil {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.bootstrapfx.core;
    requires java.logging;
    requires org.json;
    requires json.path;
    requires org.slf4j;

    requires org.fxmisc.richtext;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    requires java.desktop;
    requires java.prefs;
    requires typesafe.config;


    exports com.zs.zsdevutil;
    exports com.zs.zsdevutil.model;
    exports com.zs.zsdevutil.view;
    exports com.zs.zsdevutil.viewmodel;
    exports com.zs.zsdevutil.controls;
}
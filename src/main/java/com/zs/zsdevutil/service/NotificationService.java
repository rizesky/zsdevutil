package com.zs.zsdevutil.service;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private static NotificationService instance;
    private final StringProperty message = new SimpleStringProperty("");
    private final List<NotificationListener> listeners = new ArrayList<>();

    private NotificationService() {}

    public static synchronized NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    public void setMessage(String msg) {
        Platform.runLater(() -> {
            message.set(msg);
            notifyListeners(msg);
        });
    }

    public void addListener(NotificationListener listener) {
        listeners.add(listener);
    }

    public void removeListener(NotificationListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(String msg) {
        for (NotificationListener listener : listeners) {
            listener.onNotification(msg);
        }
    }

    public interface NotificationListener {
        void onNotification(String message);
    }
}
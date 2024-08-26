package com.zs.zsdevutil.etc;

import com.zs.zsdevutil.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);
    public static void logAndNotify(Exception e) {
        logger.error(e.getMessage(),e);
        NotificationService.getInstance().setMessage("Error: " + e.getMessage());
    }

    public static void showError(String message) {
        NotificationService.getInstance().setMessage("Error: " + message);
    }
}
package com.zs.zsdevutil.config;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class LoggerConfig {
    public static void initializeLogger() {
        String env = ConfigManager.getString(ConfigVariables.APP_ENV);
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();

        if ("dev".equals(env)) {
            configureDevelopmentLogger(context);
        } else if ("prod".equals(env)) {
            configureProductionLogger(context);
        } else {
            System.err.println("Unknown environment: " + env + ". Using development configuration.");
            configureDevelopmentLogger(context);
        }

        System.out.println("Configured Logback for environment: " + env);
    }

private static void configureDevelopmentLogger(LoggerContext context) {
    ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
    consoleAppender.setContext(context);
    consoleAppender.setName("STDOUT");

    PatternLayoutEncoder encoder = new PatternLayoutEncoder();
    encoder.setContext(context);
    encoder.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
    encoder.start();

    consoleAppender.setEncoder(encoder);
    consoleAppender.start();

    Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
    rootLogger.setLevel(Level.DEBUG);
    rootLogger.addAppender(consoleAppender);
}

private static void configureProductionLogger(LoggerContext context) {
    RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<>();
    fileAppender.setContext(context);
    fileAppender.setName("FILE");
    fileAppender.setFile(getLogFilePath());

    TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
    rollingPolicy.setContext(context);
    rollingPolicy.setParent(fileAppender);
    rollingPolicy.setFileNamePattern(getLogFilePattern());
    rollingPolicy.setMaxHistory(30);
    rollingPolicy.start();

    PatternLayoutEncoder encoder = new PatternLayoutEncoder();
    encoder.setContext(context);
    encoder.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
    encoder.start();

    fileAppender.setRollingPolicy(rollingPolicy);
    fileAppender.setEncoder(encoder);
    fileAppender.start();

    // Create AsyncAppender
    AsyncAppender asyncAppender = new AsyncAppender();
    asyncAppender.setContext(context);
    asyncAppender.setName("ASYNC");
    asyncAppender.setQueueSize(500);
    asyncAppender.setDiscardingThreshold(0);
    asyncAppender.addAppender(fileAppender);
    asyncAppender.start();

    Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
    rootLogger.setLevel(Level.INFO);
    rootLogger.addAppender(asyncAppender);
}

private static String getLogFilePath() {
    String os = System.getProperty("os.name").toLowerCase();
    String userHome = System.getProperty("user.home");
    String appName = "YourAppName"; // Replace with your app name

    if (os.contains("win")) {
        return Paths.get(userHome, "AppData", "Local", appName, "logs", "app.log").toString();
    } else if (os.contains("mac")) {
        return Paths.get(userHome, "Library", "Application Support", appName, "logs", "app.log").toString();
    } else {
        return Paths.get(userHome, ".config", appName, "logs", "app.log").toString();
    }
}

private static String getLogFilePattern() {
    String os = System.getProperty("os.name").toLowerCase();
    String userHome = System.getProperty("user.home");
    String appName = "YourAppName"; // Replace with your app name

    if (os.contains("win")) {
        return Paths.get(userHome, "AppData", "Local", appName, "logs", "app-%d{yyyy-MM-dd}.log").toString();
    } else if (os.contains("mac")) {
        return Paths.get(userHome, "Library", "Application Support", appName, "logs", "app-%d{yyyy-MM-dd}.log").toString();
    } else {
        return Paths.get(userHome, ".config", appName, "logs", "app-%d{yyyy-MM-dd}.log").toString();
    }
}
}
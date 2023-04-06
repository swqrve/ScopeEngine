package org.scope.logger;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Debug {
    public enum LogLevel {
        ALL("Misc", 0), DEBUG("Debug", 1), INFO("Info", 2), WARN("Warn", 3), ERROR("Error", 4), FATAL("Fatal", 5);
        @Getter
        private final String name;
        @Getter
        private final int logLevel;

        LogLevel(String name, int logLevel) {
            this.name = name;
            this.logLevel = logLevel;
        }
    }
    @Getter private static final List<Logged> loggedList = new ArrayList<>();

    @Getter @Setter private static String defaultScopeName = "Engine";
    @Getter @Setter private static String scopeName = "Engine";
    @Getter @Setter private static LogLevel currentLogLevel = LogLevel.ALL;
    @Getter @Setter private static LogLevel logVisibility = LogLevel.ALL;
    @Getter @Setter private static boolean defaultSaveLogs = false;


    public static void log(LogLevel level, String toLog, boolean save) {
        boolean send = logVisibility.getLogLevel() <= level.getLogLevel();
        String date = new Date().toString();

        Logged logged = new Logged(scopeName, level, toLog, date);

        if (save) loggedList.add(logged);
        if (send) logged.print();
    }

    public static void log(String scopeName, LogLevel level, String toLog, boolean save) {
        setScopeName(scopeName);
        log(level, toLog, save);
        resetScope();
    }

    public static void log(String scopeName, LogLevel level, String toLog) {
        setScopeName(scopeName);
        log(level, toLog);
        resetScope();
    }

    public static void log(String toLog, boolean save) {
        log(currentLogLevel, toLog, save);
    }

    public static void log(LogLevel level, String toLog) {
        log(level, toLog, defaultSaveLogs);
    }

    public static void log(String toLog) {
        log(toLog, defaultSaveLogs);
    }


    public static void printDebugLog(LogLevel level) {
        for (Logged log : loggedList) if (level == log.getLogLevel() || level == LogLevel.ALL) log.print();
    }

    public static void resetScope() {
        scopeName = defaultScopeName;
    }
}

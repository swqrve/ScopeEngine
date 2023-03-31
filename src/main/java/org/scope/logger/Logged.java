package org.scope.logger;

import lombok.Getter;

public class Logged {
    @Getter private final String scopeName;

    @Getter private final Debug.LogLevel logLevel;
    @Getter private final String log;

    @Getter private final String date;

    public Logged(String scopeName, Debug.LogLevel logLevel, String log, String date) {
        this.scopeName = scopeName;
        this.logLevel = logLevel;
        this.log = log;
        this.date = date;
    }

    public void print() {
        System.out.println(scopeName + " (" + logLevel.getName() + "): " + log + " " + date);
    }
}

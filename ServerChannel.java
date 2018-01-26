package com.tistory.chebaum.httpurlconnectiontest;

/**
 * Created by cheba on 2018-01-26.
 */

public class ServerChannel {
    private int number;
    private boolean isActive;
    private String name;

    public ServerChannel() {
    }

    public ServerChannel(int number, boolean isActive, String name) {
        this.number = number;
        this.isActive = isActive;
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

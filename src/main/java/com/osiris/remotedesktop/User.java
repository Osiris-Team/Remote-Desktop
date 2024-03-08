package com.osiris.remotedesktop;

public class User {
    public String name, password;
    public boolean auth;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public boolean isAllowed(){
        return auth;
    }

    public boolean isDenied(){
        return !auth;
    }

    public boolean login(){
        return login(name, password);
    }

    public boolean login(String username, String password) {
        this.name = username;
        this.password = password;
        if(username.equals("admin") && password.equals("admin")) auth = true;
        else auth = false;
        return auth;
    }
}

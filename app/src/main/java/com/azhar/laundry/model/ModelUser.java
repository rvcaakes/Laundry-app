package com.azhar.laundry.model;

public class ModelUser {
    private String message;
    private String token;
    private User user;

    public class User {
        public int id;
        public String name;
        public String email;
        public String no_hp;
        public String alamat;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }
}

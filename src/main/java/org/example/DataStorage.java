package org.example;

import java.util.HashMap;
import java.util.Map;

public class DataStorage {
    private Map<String, User> users;

    public DataStorage() {
        this.users = new HashMap<>();
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public void setUsers(Map<String, User> users) {
        this.users = users;
    }
}

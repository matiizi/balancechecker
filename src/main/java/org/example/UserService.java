package org.example;

import java.util.HashMap;
import java.util.Map;

public class UserService {

//    private Map<String, User> users = new HashMap<>();
//
//    public boolean registerUser(String username, String password) {
//        if (users.containsKey(username)) {
//            System.out.println("Пользователь с таким именем уже существует.");
//            return false;
//        }
//        // Для простоты сохраняем пароль как есть, в реальном проекте нужен хэш
//        users.put(username, new User(username, password));
//        return true;
//    }
//
//    public User loginUser(String username, String password) {
//        User user = users.get(username);
//        if (user != null && user.getPasswordHash().equals(password)) {
//            return user;
//        }
//        System.out.println("Неверный логин или пароль.");
//        return null;
//    }

    private DataStorage dataStorage;

    public UserService() {
        this.dataStorage = FileService.loadData();
        if (this.dataStorage == null) {
            this.dataStorage = new DataStorage();
        }
    }

    public boolean registerUser(String username, String password) {
        if (dataStorage.getUsers().containsKey(username)) {
            System.out.println("Пользователь с таким именем уже существует.");
            return false;
        }
        dataStorage.getUsers().put(username, new User(username, password));
        FileService.saveData(dataStorage);
        return true;
    }

    public User loginUser (String username, String password) {
        User user = dataStorage.getUsers().get(username);
        if (user != null && user.getPasswordHash().equals(password)) {
            return user;
        }
        System.out.println("Неверный логин или пароль.");
        return null;
    }

    public void saveData() {
        FileService.saveData(dataStorage);
    }
}

package com.cts.demo.service;

import com.cts.demo.model.User;

import java.util.List;

public interface UserService {


    void registerUser(User user);

    User getUserByEmail(String email);

    User getUserById(int userId);

    boolean login(String email, String password, String role);

    List<User> getAllUser();

    int updateUserName(String name, String email, String password);

}

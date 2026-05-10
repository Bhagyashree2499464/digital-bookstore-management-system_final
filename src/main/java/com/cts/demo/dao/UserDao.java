package com.cts.demo.dao;

import com.cts.demo.model.User;

import java.util.List;

public interface UserDao {

    void save(User user);

    User findByEmail(String email);

    User findByName(String name);

    boolean login(String email, String password, String role);

    List<User> findAllUser();

    User findById(int id);

    int updateUserName(String name, String email, String password);
}


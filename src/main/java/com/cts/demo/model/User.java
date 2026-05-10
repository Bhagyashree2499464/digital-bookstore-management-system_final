package com.cts.demo.model;


import lombok.*;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private int userId;

    private String name;

    private String email;

    private String password;

    private String role;

//    List<Order> orderList;
}

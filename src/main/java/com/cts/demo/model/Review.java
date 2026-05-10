package com.cts.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    private int reviewId;

    private Integer userId;

    private Integer bookId;

    private int rating;

    private String comment;

}
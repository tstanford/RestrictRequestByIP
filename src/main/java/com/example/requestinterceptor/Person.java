package com.example.requestinterceptor;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Person {
    private int id;
    private String firstname;
    private String surname;
}

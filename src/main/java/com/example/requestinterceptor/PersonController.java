package com.example.requestinterceptor;

import com.example.requestinterceptor.requestinterception.RestrictToIp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/people")
public class PersonController {

    @GetMapping("/one")
    public List<Person> getOne(){
        List<Person> myList = new ArrayList<>();
        myList.add(new Person(1,"Tim", "Stanford"));
        myList.add(new Person(2,"Bob", "Smith"));

        return myList;
    }

    @RestrictToIp(ipAddresses = {"192.168.1.2", "192.168.1.3"})
    @GetMapping("/two")
    public List<Person> getTwo(){
        List<Person> myList = new ArrayList<Person>();
        myList.add(new Person(3,"Bill", "Green"));
        return myList;
    }
}
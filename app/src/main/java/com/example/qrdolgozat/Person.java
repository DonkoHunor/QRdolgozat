package com.example.qrdolgozat;

public class Person {
    private int id;
    private String name;
    private String jegy;

    public Person(int id, String name,  String jegy) {
        this.id = id;
        this.name = name;
        this.jegy = jegy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJegy() {
        return jegy;
    }

    public void setJegy(String jegy) {
        this.jegy = jegy;
    }
}

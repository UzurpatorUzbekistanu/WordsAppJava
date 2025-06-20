package com.bkleszcz.WordApp.controller;

public class UserResponseDto {

    private final String firstName;
    private final String lastName;

    public UserResponseDto(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

}

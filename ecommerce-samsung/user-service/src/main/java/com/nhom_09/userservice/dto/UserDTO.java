package com.nhom_09.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
}

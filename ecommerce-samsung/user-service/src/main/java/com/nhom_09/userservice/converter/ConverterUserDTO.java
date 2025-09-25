package com.nhom_09.userservice.converter;

import com.nhom_09.userservice.dto.UserDTO;
import com.nhom_09.userservice.model.Role;
import com.nhom_09.userservice.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConverterUserDTO {

   public UserDTO toDTO(User user){
        List<String> roleNames = user.getRoles().stream().
                map(Role::getName)
                .collect(Collectors.toList());
        return new UserDTO(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), roleNames);
   }
}

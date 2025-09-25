package com.nhom_09.userservice.controller;

import com.nhom_09.userservice.converter.ConverterUserDTO;
import com.nhom_09.userservice.dto.UserDTO;
import com.nhom_09.userservice.model.User;
import com.nhom_09.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private ConverterUserDTO converterUserDTO;


    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal Jwt jwt){
        User user = userService.findOrCreateUser(jwt);
        UserDTO userDTO = converterUserDTO.toDTO(user);

        return ResponseEntity.ok(userDTO);
    }

}

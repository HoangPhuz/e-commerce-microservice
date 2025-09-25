package com.nhom_09.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AddressDTO {
    private Long id;
    private String street;
    private String district;
    private String city;
    private String phoneNumber;
    private boolean isDefault;
}

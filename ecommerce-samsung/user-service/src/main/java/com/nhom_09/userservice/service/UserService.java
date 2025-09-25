package com.nhom_09.userservice.service;

import com.nhom_09.userservice.dto.AddressDTO;
import com.nhom_09.userservice.model.Address;
import com.nhom_09.userservice.model.Role;
import com.nhom_09.userservice.model.User;
import com.nhom_09.userservice.repository.AddressRepository;
import com.nhom_09.userservice.repository.RoleRepository;
import com.nhom_09.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AddressRepository addressRepository;

    //Quản lý hồ sơ
    @Transactional
    public User findOrCreateUser(Jwt jwt){
        String keycloakId = jwt.getSubject();
        return userRepository.findById(keycloakId)
                .orElseGet(() ->{
                    User user = new User();
                    user.setEmail(jwt.getClaimAsString("email"));
                    user.setFirstName(jwt.getClaimAsString("given_name"));
                    user.setLastName(jwt.getClaimAsString("family_name"));

                    // Gán vai trò mặc định là ROLE_USER
                    Role defaultRole = roleRepository.findByName("ROLE_USER")
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò mặc định: ROLE_USER"));
                    user.setRoles(List.of(defaultRole));

                    return userRepository.save(user);

                });
    }

    //Quản lý địa chỉ
    @Transactional
    public List<Address> getAddresses(String userId){
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("Không tìm thấy người dùng"));
                return user.getAddresses();
    }

    @Transactional
    public Address addAddress(String userId, AddressDTO addressDTO){
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("Không tìm thấy người dùng"));

        if(addressDTO.isDefault()){
            user.getAddresses().forEach(it->it.setDefault(false));
        }
        Address newAddress = new Address();
        newAddress.setUser(user);
        newAddress.setStreet(addressDTO.getStreet());
        newAddress.setDistrict(addressDTO.getDistrict());
        newAddress.setCity(addressDTO.getCity());
        newAddress.setPhoneNumber(addressDTO.getPhoneNumber());
        newAddress.setDefault(addressDTO.isDefault());

        return addressRepository.save(newAddress);
    }

    @Transactional
    public Address updateAddress(String userId, Long addressId, AddressDTO addressDTO){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        Address addressToUpdate = user.getAddresses().stream()
                .filter(a -> a.getId().equals(addressId))
                .findFirst()
                .orElseThrow(()  -> new RuntimeException("Không tìm thấy đỉa chỉ của người dùng"));

        if (addressDTO.isDefault()) {
            user.getAddresses().forEach(addr -> addr.setDefault(false));
        }

        addressToUpdate.setStreet(addressDTO.getStreet());
        addressToUpdate.setDistrict(addressDTO.getDistrict());
        addressToUpdate.setCity(addressDTO.getCity());
        addressToUpdate.setPhoneNumber(addressDTO.getPhoneNumber());
        addressToUpdate.setDefault(addressDTO.isDefault());

        return addressRepository.save(addressToUpdate);

    }

    @Transactional
    public void deleteAddress(Long addressId){
        addressRepository.deleteById(addressId);
    }

}

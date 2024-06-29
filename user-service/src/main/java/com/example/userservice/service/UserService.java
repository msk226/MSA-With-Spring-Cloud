package com.example.userservice.service;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.repository.UserEntity;

public interface UserService {
    UserDTO createUser(UserDTO userDto);
    UserDTO getUserByUserId(String userId);
    Iterable<UserEntity> getUserByAll();

}

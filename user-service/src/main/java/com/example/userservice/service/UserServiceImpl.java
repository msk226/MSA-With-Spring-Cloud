package com.example.userservice.service;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.repository.UserEntity;
import com.example.userservice.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    @Override
    public UserDTO createUser(UserDTO userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity user = modelMapper.map(userDto, UserEntity.class);
        user.setEncryptedPwd("encryptedPwd");
        userRepository.save(user);
        return null;
    }
}

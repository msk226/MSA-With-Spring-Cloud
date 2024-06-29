package com.example.userservice.controller;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user-service")
@RequiredArgsConstructor
public class UserController {

    private final Environment env;
    private final UserService userService;

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in User Service on Port %s", env.getProperty("local.server.port"));
    }

    @GetMapping("/welcome")
    public String welcome() {
        return env.getProperty("greeting.message");
    }

    @PostMapping("/users")
    public ResponseEntity createUser(@RequestBody RequestUser user){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDTO userDto = modelMapper.map(user, UserDTO.class);
        userService.createUser(userDto);

        ResponseUser responseUser = modelMapper.map(userDto, ResponseUser.class);
        return new ResponseEntity(responseUser, HttpStatus.CREATED);
    }
}

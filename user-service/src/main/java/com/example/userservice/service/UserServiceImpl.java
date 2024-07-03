package com.example.userservice.service;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.UserDTO;
import com.example.userservice.repository.UserEntity;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OrderServiceClient orderServiceClient;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RestTemplate restTemplate;
    private final Environment environment;

    @Override
    public UserDTO createUser(UserDTO userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity user = modelMapper.map(userDto, UserEntity.class);
        user.setEncryptedPwd(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
        return null;
    }

    @Override
    public UserDTO getUserByUserId(String userId) {
        UserEntity user = userRepository.findByUserId(userId);
        if(user == null){
            throw new RuntimeException("User not found");
        }
        UserDTO userDTO = new ModelMapper().map(user, UserDTO.class);
//        String orderUrl = String.format(environment.getProperty("order_service.url"), userId);
//        ResponseEntity<List<ResponseOrder>> orderListResponse = restTemplate.exchange(orderUrl,
//            HttpMethod.GET, null,
//            new ParameterizedTypeReference<List<ResponseOrder>>(){});
//        List<ResponseOrder> orders = orderListResponse.getBody();
        List<ResponseOrder> orders = orderServiceClient.getOrders(userId);
        userDTO.setOrders(orders);
        return userDTO;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDTO getUserDetailsByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email);
        return new ModelMapper().map(user, UserDTO.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(username);
        if (user == null)
            throw new RuntimeException(username);

        return new User(user.getEmail(), user.getEncryptedPwd(),
            true, true,
            true, true,
            new ArrayList<>()
        );
    }
}

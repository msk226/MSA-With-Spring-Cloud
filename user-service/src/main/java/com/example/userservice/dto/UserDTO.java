package com.example.userservice.dto;

import com.example.userservice.vo.ResponseOrder;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class UserDTO {

    private String email;
    private String name;
    private String password;
    private String userId;
    private String encryptedPwd;
    private Date createdAt;

    private List<ResponseOrder> orders;

}

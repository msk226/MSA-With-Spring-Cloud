package com.example.userservice.dto;

import java.util.Date;
import lombok.Data;

@Data
public class UserDTO {

    private String email;
    private String name;
    private String pwd;
    private String userId;
    private String encryptedPwd;
    private Date createdAt;

}

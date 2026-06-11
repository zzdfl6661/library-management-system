
package com.library.dto.response;

import lombok.Data;

@Data
public class LoginResponse {
    private Long id;
    private String username;
    private String role;
    private String token;
    private String studentNo;
    private String cardNo;
}

package com.mailcursor.auth;

import com.mailcursor.common.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResult<Map<String, Object>> login(@RequestBody @javax.validation.Valid LoginRequest request) {
        Map<String, Object> data = authService.login(request.getUsername(), request.getPassword());
        return ApiResult.success("登录成功", data);
    }

    @GetMapping("/me")
    public ApiResult<Map<String, Object>> me() {
        AuthUser user = AuthUserContext.get();
        Map<String, Object> data = authService.currentUserInfo(user.getId(), user.isSuperAdmin());
        return ApiResult.success(data);
    }

    public static class LoginRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}

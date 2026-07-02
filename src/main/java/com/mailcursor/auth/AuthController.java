package com.mailcursor.auth;

import com.mailcursor.common.ApiResult;
import com.mailcursor.util.ClientIpUtils;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResult<Map<String, Object>> loginJson(@RequestBody @javax.validation.Valid LoginRequest request,
                                                      HttpServletRequest httpRequest) {
        return doLogin(request.getUsername(), request.getPassword(), httpRequest);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ApiResult<Map<String, Object>> loginForm(@RequestParam String username,
                                                    @RequestParam String password,
                                                    HttpServletRequest httpRequest) {
        return doLogin(username, password, httpRequest);
    }

    private ApiResult<Map<String, Object>> doLogin(String username, String password, HttpServletRequest httpRequest) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return ApiResult.fail(400, "用户名和密码不能为空");
        }
        Map<String, Object> data = authService.login(username, password, ClientIpUtils.getClientIp(httpRequest));
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

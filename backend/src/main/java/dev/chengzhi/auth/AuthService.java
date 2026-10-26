package dev.chengzhi.auth;

import dev.chengzhi.common.ApiException;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserMapper users;
    private final UserRoleMapper roles;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokens;

    public AuthService(UserMapper users, UserRoleMapper roles, PasswordEncoder passwordEncoder, TokenService tokens) {
        this.users = users;
        this.roles = roles;
        this.passwordEncoder = passwordEncoder;
        this.tokens = tokens;
    }

    public LoginResponse login(String username, String password) {
        UserEntity user = users.findByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }
        Set<String> roleNames = new LinkedHashSet<>(roles.findRoleNames(user.getId()));
        UserPrincipal principal = UserPrincipal.from(user, roleNames);
        return new LoginResponse(tokens.issue(principal), principal.username(), principal.scope().roles());
    }
}

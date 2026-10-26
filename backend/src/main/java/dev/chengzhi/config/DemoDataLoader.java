package dev.chengzhi.config;

import dev.chengzhi.auth.UserEntity;
import dev.chengzhi.auth.UserMapper;
import dev.chengzhi.auth.UserRoleMapper;
import dev.chengzhi.common.Ids;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DemoDataLoader {

    @Bean
    CommandLineRunner loadDemoUsers(UserMapper users, UserRoleMapper roles, PasswordEncoder passwordEncoder) {
        return args -> {
            if (users.selectCount(null) > 0) {
                return;
            }
            save(users, roles, passwordEncoder, "student", "student123", "dept-student", Set.of("STUDENT"));
            save(users, roles, passwordEncoder, "hr", "hr123456", "dept-hr", Set.of("HR"));
            save(users, roles, passwordEncoder, "admin", "admin123", "dept-admin", Set.of("ADMIN"));
        };
    }

    private void save(
            UserMapper users,
            UserRoleMapper roles,
            PasswordEncoder encoder,
            String username,
            String password,
            String department,
            Set<String> roleNames) {
        UserEntity user = new UserEntity(Ids.newId("usr"), username, encoder.encode(password), department);
        users.insert(user);
        for (String roleName : roleNames) {
            roles.insertRole(user.getId(), roleName);
        }
    }
}

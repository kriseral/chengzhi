package dev.chengzhi.auth;

import dev.chengzhi.acl.AccessScope;
import java.util.Collection;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record UserPrincipal(AccessScope scope, String username, String passwordHash) implements UserDetails {

    public static UserPrincipal from(UserEntity user, Set<String> roles) {
        return new UserPrincipal(
                new AccessScope(user.getId(), user.getDepartmentId(), Set.copyOf(roles)),
                user.getUsername(),
                user.getPasswordHash());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return scope.roles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();
    }

    @Override
    public String getPassword() { return passwordHash; }

    @Override
    public String getUsername() { return username; }
}

package org.spring.aicloud.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * @Author: JarvanW
 * @Date: 2024/8/2
 * @Description:
 * @Requirements:
 */
@Data
@Builder
public class SecurityUserDetails implements UserDetails {
    private long uid;
    private String username;
    private String password;

    public SecurityUserDetails(long uid,String username, String password){
        this.uid = uid;
        this.username = username;
        this.password = password;
    }

    // 权限
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    // 密码
    @Override
    public String getPassword() {
        return null;
    }

    // 用户名
    @Override
    public String getUsername() {
        return null;
    }

    // 账号是否过期
    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    // 账号是否锁定
    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    // 密码是否过期
    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    // 账号是否可用
    @Override
    public boolean isEnabled() {
        return true;
    }
}

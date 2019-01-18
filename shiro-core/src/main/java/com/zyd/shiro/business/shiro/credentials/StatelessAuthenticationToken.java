package com.zyd.shiro.business.shiro.credentials;

/**
 * Created by betty77 on 2017/7/31.
 */

import lombok.Data;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * 用于授权的Token对象：
 * 用户身份即用户名；
 * 凭证即客户端传入的消息摘要。
 */
@Data
public class StatelessAuthenticationToken implements AuthenticationToken {
    private static final long serialVersionUID = 1L;
    private String username;//用户身份即用户名；
    private char[] password;//密码
    private boolean rememberMe = false;
    private String host;
    private char[] token; //jwt

    public StatelessAuthenticationToken() {

    }


    public StatelessAuthenticationToken(String username, String password, String token) {
        this.username = username;
        this.password = password.toCharArray();
        this.token = token.toCharArray();
    }
    public StatelessAuthenticationToken(String username, String password, String token, boolean rememberMe) {
        this.username = username;
        this.password = password.toCharArray();
        this.token = token.toCharArray();
        this.rememberMe = rememberMe;
    }
    @Override
    public Object getPrincipal() {
        return this.username;
    }

    @Override
    public Object getCredentials() {
        return this.password;
    }
}

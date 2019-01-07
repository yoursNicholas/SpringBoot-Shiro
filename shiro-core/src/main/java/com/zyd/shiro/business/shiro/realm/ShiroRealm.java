/**
 * MIT License
 * Copyright (c) 2018 yadong.zhang
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zyd.shiro.business.shiro.realm;

import com.zyd.shiro.business.entity.Resources;
import com.zyd.shiro.business.entity.Role;
import com.zyd.shiro.business.entity.User;
import com.zyd.shiro.business.enums.UserStatusEnum;
import com.zyd.shiro.business.service.SysResourcesService;
import com.zyd.shiro.business.service.SysRoleService;
import com.zyd.shiro.business.service.SysUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * Shiro-密码输入错误的状态下重试次数的匹配管理
 *
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @version 1.0
 * @website https://www.zhyd.me
 * @date 2018/4/24 14:37
 * @since 1.0
 */
public class ShiroRealm extends AuthorizingRealm {

    @Resource
    private SysUserService userService;
    @Resource
    private SysResourcesService resourcesService;
    @Resource
    private SysRoleService roleService;

    /**
     * 第二步
     * 提供账户信息返回认证信息（用户的角色信息集合）
     * 认证回调函数,登录时调用.
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //获取用户的输入的账号.
        String username = (String) token.getPrincipal();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>username<<<<<<<<<<<<<<<<<<<<:"+username);

        User user = userService.getByUserName(username);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>user<<<<<<<<<<<<<<<<<<<<:"+user.getId()+"---"+user.getPassword());

        if (user == null) {
            throw new UnknownAccountException("账号不存在！");
        }
        if (user.getStatus() != null && UserStatusEnum.DISABLE.getCode().equals(user.getStatus())) {
            throw new LockedAccountException("帐号已被锁定，禁止登录！");
        }

        // principal参数使用用户Id，方便动态刷新用户权限
        return new SimpleAuthenticationInfo(
                user.getId(),
                user.getPassword(),
                ByteSource.Util.bytes(username),
                getName()
        );
    }

    /**
     * 第七步
     * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
     * 权限认证，为当前登录的Subject授予角色和权限（角色的权限信息集合）
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 权限信息对象info,用来存放查出的用户的所有的角色（role）及权限（permission）
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>doGetAuthorizationInfo<<<<<<<<<<<<<<<<<<<<:"+info);
        Session session = SecurityUtils.getSubject().getSession();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>doGetAuthorizationInfo  session<<<<<<<<<<<<<<<<<<<<:"+session.getTimeout());
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>doGetAuthorizationInfo  getStartTimestamp<<<<<<<<<<<<<<<<<<<<:"+session.getStartTimestamp());
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>doGetAuthorizationInfo  getLastAccessTime<<<<<<<<<<<<<<<<<<<<:"+session.getLastAccessTime());

        session.setAttribute("key", "123");

        Long userId = (Long) SecurityUtils.getSubject().getPrincipal();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>doGetAuthorizationInfo  userId<<<<<<<<<<<<<<<<<<<<:"+userId);

        // 赋予角色
        List<Role> roleList = roleService.listRolesByUserId(userId);
        for (Role role : roleList) {
            info.addRole(role.getName());
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>doGetAuthorizationInfo  rolename<<<<<<<<<<<<<<<<<<<<:"+role.getName());
        }

        // 赋予权限
        List<Resources> resourcesList = resourcesService.listByUserId(userId);
        if (!CollectionUtils.isEmpty(resourcesList)) {
            for (Resources resources : resourcesList) {
                String permission = resources.getPermission();
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>doGetAuthorizationInfo resourcesname<<<<<<<<<<<<<<<<<<<<:"+resources.getName() + "   " + permission);
                if (!StringUtils.isEmpty(permission)) {
                    info.addStringPermission(permission);
                }
            }
        }
        return info;
    }

}

package com.zyd.shiro.business.service;

import com.zyd.shiro.business.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=SysUserService.class)
public class SysUserServiceTest {
    SysUserService sysUserService;
    @Test
    public void queryRoleListWithSelected() {
        User liming = new User("liming", "111111");
        sysUserService.insert(liming);
    }
}
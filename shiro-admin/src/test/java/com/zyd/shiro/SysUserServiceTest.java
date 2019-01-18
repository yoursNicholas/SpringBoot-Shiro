package com.zyd.shiro;

import com.zyd.shiro.business.entity.User;
import com.zyd.shiro.business.enums.UserStatusEnum;
import com.zyd.shiro.framework.holder.RequestHolder;
import com.zyd.shiro.persistence.mapper.SysUserMapper;
import com.zyd.shiro.util.common.IpUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SysUserServiceTest {
    @Autowired
    private SysUserMapper sysUserMapper;

    @Test
    public void queryRoleListWithSelected() {
        User user = new User("liming", "111111");
        user.setUpdateTime(new Date());
        user.setCreateTime(new Date());
        user.setRegIp(IpUtil.getRealIp(RequestHolder.getRequest()));
        user.setStatus(UserStatusEnum.NORMAL.getCode());
        int id = sysUserMapper.insertSelective(user.getSysUser());
    }
}
package com.zyd.shiro;

import com.zyd.shiro.persistence.beans.SysRole;
import com.zyd.shiro.persistence.mapper.SysRoleMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SysRoleServiceImplTest {
    @Autowired
    private SysRoleMapper roleMapper;
    @Test
    public void queryRoleListWithSelected() {
        List<SysRole> sysRole = roleMapper.queryRoleListWithSelected(1);
        if (CollectionUtils.isEmpty(sysRole)) {
        }
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;
        for (SysRole role : sysRole) {
            map = new HashMap<String, Object>(3);
            map.put("id", role.getId());
            map.put("pId", 0);
            map.put("checked", role.getSelected() != null && role.getSelected() == 1);
            map.put("name", role.getDescription());
            mapList.add(map);
        }
        System.out.println(mapList);


    }
}
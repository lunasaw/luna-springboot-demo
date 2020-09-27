package com.xkcoding.orm.jpa.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.xkcoding.orm.jpa.SpringBootDemoOrmJpaApplicationTests;
import com.xkcoding.orm.jpa.entity.User;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * jpa 测试类
 * </p>
 *
 * @package: com.xkcoding.orm.jpa.repository
 * @description: jpa 测试类
 * @author: yangkai.shen
 * @date: Created in 2018/11/7 14:09
 * @copyright: Copyright (c) 2018
 * @version: V1.0
 * @modified: yangkai.shen
 */
@Slf4j
public class UserInsertTest extends SpringBootDemoOrmJpaApplicationTests {
    @Autowired
    private UserDao userDao;

    /**
     * 测试保存
     */
    @Test
    public void atest() {
        String salt = IdUtil.fastSimpleUUID();
        // User testSave3 = User.builder().name("testSave3").password(SecureUtil.md5("123456" +
        // salt)).salt(salt).email("testSave3@xkcoding.com").phoneNumber("17300000003").status(1).lastLoginTime(new
        // DateTime()).build();
        User testSave3 = new User("testSave3", SecureUtil.md5("123456" + salt), salt, "testSave3@xkcoding.com", "17300000003", 1, new DateTime(),null);
        userDao.save(testSave3);

        Assert.assertNotNull(testSave3.getId());
        Optional<User> byId = userDao.findById(testSave3.getId());
        Assert.assertTrue(byId.isPresent());
        log.debug("【byId】= {}", byId.get());
    }

    @Test
    public void btest() {
        String salt = IdUtil.fastSimpleUUID();
        // User testSave3 = User.builder().name("testSave3").password(SecureUtil.md5("123456" +
        // salt)).salt(salt).email("testSave3@xkcoding.com").phoneNumber("17300000003").status(1).lastLoginTime(new
        // DateTime()).build();
        User testSave4 = new User("testSave4", SecureUtil.md5("123456" + salt), salt, "testSave4@xkcoding.com", "17300000004", 1, new DateTime(),null);
        userDao.save(testSave4);

        Assert.assertNotNull(testSave4.getId());
        Optional<User> byId = userDao.findById(testSave4.getId());
        Assert.assertTrue(byId.isPresent());
        log.debug("【byId】= {}", byId.get());
    }

}

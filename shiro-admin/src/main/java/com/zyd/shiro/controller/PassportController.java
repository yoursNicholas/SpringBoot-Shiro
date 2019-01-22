
package com.zyd.shiro.controller;

import com.zyd.shiro.business.entity.User;
import com.zyd.shiro.business.service.SysUserService;
import com.zyd.shiro.business.shiro.credentials.StatelessAuthenticationToken;
import com.zyd.shiro.framework.common.Constant;
import com.zyd.shiro.framework.object.ResponseVO;
import com.zyd.shiro.persistence.beans.SysUser;
import com.zyd.shiro.util.JwtUtil;
import com.zyd.shiro.util.common.ResultUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * 登录相关
 *
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @version 1.0
 * @website https://www.zhyd.me
 * @date 2018/4/24 14:37
 * @since 1.0
 */
@Controller
@RequestMapping(value = "/passport")
public class PassportController {

    /**
     * RefreshToken过期时间
     */
    @Value("${refreshTokenExpireTime}")
    private String refreshTokenExpireTime;

    private static final Logger logger = LoggerFactory.getLogger(PassportController.class);

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private RedisTemplate redisTemplate;
    @GetMapping("/login")
    public ModelAndView login(Model model) {
        Subject subject = SecurityUtils.getSubject();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>PassportController subject<<<<<<<<<<<<<<<<<<<<<<"+subject.getPrincipal());

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>PassportController getSession<<<<<<<<<<<<<<<<<<<<<<"+subject.getSession());

        if (subject.isAuthenticated()||subject.isRemembered()){
            return ResultUtil.redirect("/index");
        }
        return ResultUtil.view("/login");
    }

    /**
     * 登录
     *  第一步
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/signin")
    @ResponseBody
    public ResponseVO submitLogin(@RequestParam(value = "username") String username,
                                  @RequestParam(value = "password")  String password, @RequestParam(value = "rememberMe",defaultValue ="false"
    ) boolean rememberMe, HttpServletResponse httpServletResponse) {
        //UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
        JwtUtil jwtUtil = new JwtUtil();
        // 设置RefreshToken，时间戳为当前时间戳，直接设置即可(不用先删后设，会覆盖已有的RefreshToken)
        String currentTimeMillis = String.valueOf(System.currentTimeMillis());

        String token = jwtUtil.sign(username, currentTimeMillis);
        StatelessAuthenticationToken authenticationToken = new StatelessAuthenticationToken(username,password, token, rememberMe);

        //获取当前的Subject 一个http请求一个subject,并绑定到当前线程。
        Subject currentUser = SecurityUtils.getSubject();
/*        if (redisTemplateUtil.hasKey(Constant.PREFIX_SHIRO_CACHE + username)) {
            redisTemplateUtil.delete(Constant.PREFIX_SHIRO_CACHE + username);
        }*/
        // 设置RefreshToken，时间戳为当前时间戳，直接设置即可(不用先删后设，会覆盖已有的RefreshToken)
        redisTemplate.opsForValue().set(Constant.PREFIX_SHIRO_REFRESH_TOKEN + username, currentTimeMillis, Integer.parseInt(refreshTokenExpireTime),TimeUnit.MILLISECONDS);

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>PassportController currentUser<<<<<<<<<<<<<<<<<<<<<<"+(String) authenticationToken.getPrincipal());
        try {
            //SecurityUtils.getSubject().login()这里是登陆调用开始，进入到Shiro内部后就会调用到 doGetAuthenticationInfo()登陆认证，
            // 成功登陆后调用 doGetAuthorizationInfo() 加载用户角色和权限（RBAC）。
            // 在调用了login方法后,SecurityManager会收到AuthenticationToken,并将其发送给已配置的Realm执行必须的认证检查
            // 每个Realm都能在必要时对提交的AuthenticationTokens作出反应
            // 所以这一步在调用login(token)方法时,它会走到xxRealm.doGetAuthenticationInfo()方法中,具体验证方式详见此方法
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>PassportController token<<<<<<<<<<<<<<<<<<<<<<" + authenticationToken.getCredentials());
            currentUser.login(authenticationToken);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>PassportController login<<<<<<<<<<<<<<<<<<<<<<");
            httpServletResponse.setHeader("Authorization", token);
            httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
            return ResultUtil.success("登录成功！");
        } catch (Exception e) {
            logger.error("登录失败，用户名[{}]", username, e);
            return ResultUtil.error("登录失败!用户名或密码错误！");
        }
    }


    @PostMapping("/register")
    @ResponseBody
    public ResponseVO submitRegister(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password,
                                     @RequestParam(value = "confirmPass") String confirmPass,
                                     @RequestParam(value = "phoneNumber") String phoneNumber) {
            SysUser sysUser = new SysUser(username, password, phoneNumber);
            User user = new User(sysUser);
            System.out.println(">>>>>>user<<<<<<<<"+user);
            sysUserService.insert(user);
            return ResultUtil.success("注册成功！");
    }

    /**
     * 使用权限管理工具进行用户的退出，跳出登录，给出提示信息
     *
     * @param redirectAttributes
     * @return
     */
    @GetMapping("/logout")
    public ModelAndView logout(RedirectAttributes redirectAttributes) {
        // http://www.oschina.net/question/99751_91561
        // 此处有坑： 退出登录，其实不用实现任何东西，只需要保留这个接口即可，也不可能通过下方的代码进行退出
        // SecurityUtils.getSubject().logout();
        // 因为退出操作是由Shiro控制的
        redirectAttributes.addFlashAttribute("message", "您已安全退出");
        return ResultUtil.redirect("index");
    }
}

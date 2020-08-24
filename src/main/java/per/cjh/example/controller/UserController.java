package per.cjh.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import per.cjh.example.domain.*;
import per.cjh.example.service.impl.UserServiceImpl;
import per.cjh.example.utils.SendMessage;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Random;

/**
 * @author cjh
 * @description: 和三类用户相关的通用操作，
 * 按方法顺序为：login -登录、name -获取当前账号姓名、exit -退出账号
 * user/password - 输入手机号，获取密码
 * @date 2020/5/4 9:05
 */
@Api(tags = "用户通用的相关 API")
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserServiceImpl service;
    private User user;

    @ApiOperation("用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "登录账号，学生学号或者教师、管理员手机号"),
            @ApiImplicitParam(name = "password", value = "登录密码"),
            @ApiImplicitParam(name = "type", value = "登录用户选择的角色类型"),
            @ApiImplicitParam(name = "system", value = "登录的系统类型"),
    })
    @GetMapping("/login")
    public String login(String username, String password, String type, String system, @ApiIgnore HttpSession session) {
        User user = null;
        // 调用 service 层进行登录
        user = service.login(username, password, type, system);
        log.info("登录用户为：" + user);
        if (user == null) {
            return "账号或密码错误！";
        }
        log.info("登录用户为：" + user);
        // 登录成功，则在 session 设置用户名、密码、姓名、角色类型、所用系统（理论 or 实验）
        session.setAttribute("username", username);
        session.setAttribute("password", password);
        session.setAttribute("type", type);
        session.setAttribute("name", user.getName());
        session.setAttribute("system", system);
        return "欢迎您 " + user;
    }

    @ApiOperation("理论课用户登录，返回类别：学生、管理员、教师")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "登录账号，学生学号或者教师、管理员手机号"),
            @ApiImplicitParam(name = "password", value = "登录密码"),
            @ApiImplicitParam(name = "type", value = "登录用户选择的角色类型"),
            @ApiImplicitParam(name = "system", value = "登录的系统类型"),
    })
    @GetMapping("/lblogin")
    public String lblogin(String username, String password, @ApiIgnore HttpSession session) {
        // 不为1肯定不是手机登录
        String type = "";
        Lbteacher lbteacher = null;
        Lbsc lbsc = null;
        if (username.charAt(0) == '1') {
            // 手机号肯定以1开头
            lbteacher = service.lbtlogin(username, password);
        }
        if (lbteacher != null) {
            log.info(lbteacher.toString());
            if ("".equals(lbteacher.getTerm())) {
                type = "管理员";
            } else {
                type = "教师";
            }
        } else {
            // 教师记录为null， 查找学生记录
            lbsc = service.lbslogin(username, password);
        }
        if (lbsc != null) {
            type = "学生";
        }
        // 判断教师和学生是否都为空
        if (lbsc == null && lbteacher == null) {
            return "账号或密码错误！";
        }
        // 登录成功，则在 session 设置用户名、密码、姓名、角色类型、所用系统（理论 or 实验）
        if (lbteacher != null) {
            session.setAttribute("name", lbteacher.getName());
//            log.info(lbteacher.toString());
        } else if (lbsc != null) {
            session.setAttribute("name", lbsc.getName());
//            log.info(lbsc.toString());
        }
        session.setAttribute("username", username);
        session.setAttribute("password", password);
        session.setAttribute("type", type);
        session.setAttribute("system", "理论");
        return type;
    }
    @ApiOperation("理论课用户忘记密码")
    @GetMapping("/send")
    public String send(String username, String phone, @ApiIgnore HttpSession session) {
        try {
            String type = "";
            Lbteacher lbteacher = null;
            Lbsc lbsc = null;
            // 不为1肯定不是手机登录
            if (username.charAt(0) == '1') {
                // 手机号肯定以1开头
                lbteacher = service.lbisTeacher(username);
            }
            if (lbteacher != null) {
                type = "教师";
            } else {
                // 教师记录为null， 查找学生记录
                lbsc = service.lbisStudent(username);
            }
            if (lbsc != null) {
                type = "学生";
            }
            // 生成4位数的验证码
            Random random = new Random();
            int captcha = 0;
            while((captcha = random.nextInt(10000)) < 1000) {
            }
            // 设置session 验证码
            session.setAttribute("captcha", captcha);
            // 给手机发送验证码信息
            SendMessage.send(phone, String.valueOf(captcha));
            return type;
        } catch (Throwable throwable) {
            // 教师和学生都为空
            return "用户名不存在！";
        }
    }

    @ApiOperation("获取账号姓名显示在每个页面右上角")
    @GetMapping("/name")
    public String name(@ApiIgnore HttpSession session) {
        try {
            // 从 session 中获取用户的姓名
            return (String) session.getAttribute("name");
        } catch (Throwable e) {
            log.info(e.toString());
            return "";
        }
    }

    @ApiOperation("每个页面右上角退出登录，清除 session")
    @GetMapping("/exit")
    public void exit(@ApiIgnore HttpSession session) {
        // 前端点击退出登录，清空 session
        session.removeAttribute("username");
        session.removeAttribute("type");
        session.invalidate();
    }

    @ApiOperation("忘记密码页面，传入手机号、学号、系统类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号"),
            @ApiImplicitParam(name = "sno", value = "学号"),
            @ApiImplicitParam(name = "system", value = "登录的系统类型"),
    })
    @GetMapping("/password")
    public String password(String phone, String sno, String system) {
        try {
            // 根据手机号、学号查找管理员或者教师或者学生账号。根据第一条记录的电话和密码发送短信
            if (sno != null) {
                ExSC stu = service.findOneStu(sno, phone, system);
                SendMessage.send(stu.getPhone(), stu.getPassword());
            } else {
                ExTC teacher = service.findOneTeacherOrManager(phone, system);
                SendMessage.send(teacher.getPhone(), teacher.getPassword());
            }
            return "信息发送成功";
        } catch (Throwable e) {
            e.printStackTrace();
            log.info("手机号：" + phone + " 学号：" + sno + e.toString());
            return "信息发送失败";
        }
    }

    @ApiOperation("理论课修改个人信息页面开局获取信息")
    @GetMapping("/lbmessage")
    public Object messageGet(@ApiIgnore HttpSession session) {
        Lbteacher lbteacher = new Lbteacher();
        lbteacher.setPhone(session.getAttribute("username") + "").
                setName(session.getAttribute("name") + "");
        return lbteacher;
    }

    @ApiOperation("理论课教师修改个人信息页面开局获取信息")
    @PutMapping("/lbtmessage")
    public Object tmessagePut(Lbteacher lbteacher, String username, String captcha,String password, HttpSession session) {
        try {
            // 验证码不为空，此时不是教师修改个人页面的提交，而是忘记密码页面的提交
            if (captcha != null) {
                String generateCode = session.getAttribute("captcha") + "";
                if (!captcha.equals(generateCode)) {
                    return "验证码错误！";
                }
                // 验证码使用一次以后就删除
                session.setAttribute("captcha", 7654321);
                // 教师忘记密码，修改密码
                service.teacherForgetPassword(username, password);
            } else {
                service.tmessagePut(lbteacher);
            }
            return "修改成功";
        } catch (Throwable e) {
            e.printStackTrace();
            return "修改失败";
        }
    }
    @ApiOperation("理论课学生修改个人信息页面开局获取信息")
    @PutMapping("/lbsmessage")
    public Object smessagePut(Lbsc lbsc, String username, String captcha,String password, HttpSession session) {
        try {
            // 验证码不为空，此时不是教师修改个人页面的提交，而是忘记密码页面的提交
            if (captcha != null) {
                String generateCode = session.getAttribute("captcha") + "";
                if (!captcha.equals(generateCode)) {
                    return "验证码错误！";
                }
                // 验证码使用一次以后就删除
                session.setAttribute("captcha", 7654321);
                // 学生忘记密码，修改密码
                service.studentForgetPassword(username, password);
            } else {
                service.smessagePut(lbsc);
            }
            return "修改成功";
        } catch (Throwable e) {
            e.printStackTrace();
            return "修改失败";
        }
    }
}

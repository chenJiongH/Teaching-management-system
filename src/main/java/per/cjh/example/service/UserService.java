package per.cjh.example.service;

import per.cjh.example.domain.*;

import java.util.List;

/**
 * @author cjh
 * @description: TODO
 * @date 2020/5/4 14:46
 */
public interface UserService {
    /**
     * 根据用户名和密码登录，并且根据 type 判断用户的角色，根据 system 判断用户所要登录的系统
     * @param username 用户名
     * @param password 密码
     * @param type 角色类型（取值）：学生、教师、管理员
     * @param system 系统类型（取值）：实验、理论
     * @return 用户信息
     */
    User login(String username, String password, String type, String system);

    ExSC findOneStu(String sno, String phone, String system) throws Throwable;

    ExTC findOneTeacherOrManager(String phone, String system);

    void tmessagePut(Lbteacher lbteacher) throws Throwable;

    void smessagePut(Lbsc lbsc) throws Throwable;

    Lbsc lbslogin(String username, String password);

    Lbteacher lbtlogin(String username, String password);
}

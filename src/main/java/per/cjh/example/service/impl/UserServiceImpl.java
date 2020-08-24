package per.cjh.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.cjh.example.domain.*;
import per.cjh.example.mappers.ExSCMapper;
import per.cjh.example.mappers.ExTCMapper;
import per.cjh.example.mappers.LbscMapper;
import per.cjh.example.mappers.LbteacherMapper;
import per.cjh.example.service.UserService;
import tk.mybatis.mapper.common.ExampleMapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author cjh
 * @description: TODO
 * @date 2020/5/4 9:05
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private ExSCMapper scMapper;
    @Resource
    private ExTCMapper tcMapper;
    @Resource
    private LbteacherMapper lbteacherMapper;
    @Resource
    private LbscMapper lbscMapper;

    private User user;

    @Override
    public User login(String username, String password, String type, String system) {
        switch (type) {
            case "学生":
                user = scMapper.selectOneByUsernamePassword(username, password);
                break;
            case "教师":
                user = tcMapper.selectOneTeacherByUsernamePassword(username, password);
                break;
            case "管理员":
                user = tcMapper.selectOneByUsernamePassword(username, password);
                break;
            default:
                break;
        }
        return user;
    }

    @Override
    public Lbsc lbslogin(String username, String password) {
        Example example = new Example(Lbsc.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sno", username)
                .andEqualTo("password", password);

        RowBounds rowBounds = new RowBounds(0, 1);
        List<Lbsc> lbscs = lbscMapper.selectByExampleAndRowBounds(example, rowBounds);
        if (lbscs == null || lbscs.isEmpty()) {
            return null;
        } else {
            return lbscs.get(0);
        }

    }

    @Override
    public Lbteacher lbtlogin(String username, String password) {
        Example example = new Example(Lbteacher.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("phone", username)
                .andEqualTo("password", password);
        RowBounds rowBounds = new RowBounds(0, 1);
        List<Lbteacher> lbteachers = lbteacherMapper.selectByExampleAndRowBounds(example, rowBounds);
        if (lbteachers == null ||lbteachers.isEmpty()) {
            return null;
        } else {
            return lbteachers.get(0);
        }
    }

    @Override
    public ExSC findOneStu(String sno, String phone, String system) throws Throwable {
        Example example = new Example(ExSC.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sno", sno)
                .andEqualTo("phone", phone);
        return scMapper.selectByExample(example).get(0);
    }

    @Override
    public ExTC findOneTeacherOrManager(String phone, String system) {
        Example example = new Example(ExTC.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("phone", phone);
        return tcMapper.selectByExample(example).get(0);
    }

    @Override
    public void tmessagePut(Lbteacher lbteacher) throws Throwable {
        // 修改 teacher 表里面的同个手机号所有记录的密码
        Example example = new Example(Lbteacher.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("phone", lbteacher.getPhone());
        lbteacherMapper.updateByExampleSelective(lbteacher, example);
    }

    @Override
    public void smessagePut(Lbsc lbsc) throws Throwable {
        // 修改 teacher 表里面的同个手机号所有记录的密码
        Example example = new Example(Lbsc.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sno", lbsc.getPhone());
        log.info(lbsc.toString());
        lbscMapper.updateByExampleSelective(lbsc, example);
    }

    public Lbteacher lbisTeacher(String username) throws Throwable {
        // 查找是否有手机号为 username 的教师记录
        Example example = new Example(Lbteacher.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("phone", username);
        RowBounds rowBounds = new RowBounds(0, 1);
        return lbteacherMapper.selectByExampleAndRowBounds(example, rowBounds).get(0);
    }

    public Lbsc lbisStudent(String username) throws Throwable {
        Example example = new Example(Lbsc.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sno", username);
        RowBounds rowBounds = new RowBounds(0, 1);
        return lbscMapper.selectByExampleAndRowBounds(example, rowBounds).get(0);
    }

    public void teacherForgetPassword(String username, String password) {
        // 修改 teacher 表里面的同个手机号所有记录的密码
        Example example = new Example(Lbteacher.class);
        Example.Criteria criteria = example.createCriteria();

        Lbteacher lbteacher = new Lbteacher();
        lbteacher.setPhone(username).setPassword(password);
        criteria.andEqualTo("phone", lbteacher.getPhone());
        lbteacherMapper.updateByPrimaryKeySelective(lbteacher);
    }
    public void studentForgetPassword(String username, String password) {
        // 修改 sc 表里面的同个手机号所有记录的密码
        Example example = new Example(Lbsc.class);
        Example.Criteria criteria = example.createCriteria();

        Lbsc lbsc = new Lbsc();
        lbsc.setSno(username).setPassword(password);
        criteria.andEqualTo("phone", lbsc.getSno());
        lbscMapper.updateByPrimaryKeySelective(lbsc);
    }
}

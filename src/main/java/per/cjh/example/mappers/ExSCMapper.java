package per.cjh.example.mappers;

import org.apache.ibatis.annotations.Update;
import per.cjh.example.domain.ExSC;
import per.cjh.example.domain.User;
import tk.mybatis.mapper.common.Mapper;

public interface ExSCMapper extends Mapper<ExSC> {
    /**
     * 根据用户名和密码查询一条学生记录
     * @param username
     * @param password
     * @return
     */
    User selectOneByUsernamePassword(String username, String password);

}
package per.cjh.example.mappers;

import org.apache.ibatis.annotations.Update;
import per.cjh.example.domain.ExTC;
import per.cjh.example.domain.User;
import tk.mybatis.mapper.common.Mapper;

public interface ExTCMapper extends Mapper<ExTC> {

    User selectOneTeacherByUsernamePassword(String username, String password);

    User selectOneByUsernamePassword(String username, String password);

    @Update("update ")
    int updateByOriKey(ExTC exTC, String oriPhone, String oriTerm, String oriCname);
}
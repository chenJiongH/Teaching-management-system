package per.cjh.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import per.cjh.example.domain.ExTC;
import per.cjh.example.mappers.ExCourceMapper;
import per.cjh.example.mappers.ExTCMapper;
import per.cjh.example.service.TeacherService;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author cjh
 * @description: TODO
 * @date 2020/5/6 8:47
 */
@Slf4j
@Service
public class TeacherServiceImpl implements TeacherService {
    @Resource
    private ExTCMapper tcMapper;
    @Resource
    private ExCourceMapper courceMapper;
    @Override
    public List<ExTC> get(Integer curpage) throws Throwable {
        Example example = new Example(ExTC.class);
        example.orderBy("term").orderBy("name").asc();
        RowBounds rowBounds = new RowBounds((curpage - 1) * 20, 20);
        return tcMapper.selectByExampleAndRowBounds(example, rowBounds);
    }

    @Override
    public void insertOne(ExTC exTC) throws Throwable {
        // 不同教师的手机号不能重复，根据是否有不同名的教师，为同一手机号
        Example example = new Example(ExTC.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("phone", exTC.getPhone())
                .andNotEqualTo("name", exTC.getName());
        RowBounds rowBounds = new RowBounds(0, 1);
        // 不存在不同教师的重复手机
        if (tcMapper.selectByExampleAndRowBounds(example, rowBounds).isEmpty()) {
            tcMapper.insert(exTC);
        } else {
            throw new Throwable();
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void update(String password, String oriPhone) throws Throwable {
        ExTC exTC = new ExTC();
        exTC.setPassword(password);
        Example example = new Example(ExTC.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("phone", oriPhone);
        log.info(oriPhone + " " + password);
        tcMapper.updateByExampleSelective(exTC, example);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateOne(String phone, String term, String cname, ExTC exTC) throws Throwable {
        // 删除记录，再插入记录，因为修改涉及到了主键
        this.deleteOne(phone, term, cname);
        tcMapper.insert(exTC);
        // 把之前的电话都改变成，现在的电话、密码、姓名
        Example example = new Example(ExTC.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("phone", phone);
        List<ExTC> exTCS = tcMapper.selectByExample(example);
        for (int i = 0; i < exTCS.size(); i++) {
            // 第一次循环前删除所有记录
            if (i == 0) {
                tcMapper.deleteByExample(example);
            }
            // 所有记录的课程名和学期名为原记录不变。但是手机号、姓名、密码用新的值
            exTC.setCname(exTCS.get(i).getCname());
            exTC.setTerm(exTCS.get(i).getTerm());
            tcMapper.insert(exTC);
        }
    }

    @Override
    public void deleteOne(String phone, String term, String cname) throws Throwable {
        Example example = new Example(ExTC.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("phone", phone).
                 andEqualTo("term", term).
                 andEqualTo("cname", cname);
        tcMapper.deleteByExample(example);
    }

    @Override
    public ExTC teacherSelfGet(String phone) {
        Example example = new Example(ExTC.class);
        Example.Criteria criteria = example.createCriteria();
        // 根据手机号，查询一条记录即可
        criteria.andEqualTo("phone", phone);
        RowBounds rowBounds = new RowBounds(0, 1);
        return tcMapper.selectByExampleAndRowBounds(example, rowBounds).get(0);

    }
}

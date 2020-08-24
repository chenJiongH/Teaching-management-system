package per.cjh.example.service.impl;

import com.google.common.collect.Lists;
import com.mysql.cj.util.StringUtils;
import com.sun.org.apache.bcel.internal.generic.NEW;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.UpgradeToken;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import per.cjh.example.domain.ExGrade;
import per.cjh.example.domain.ExSC;
import per.cjh.example.mappers.ExGradeMapper;
import per.cjh.example.mappers.ExSCMapper;
import per.cjh.example.service.ExgradeService;
import per.cjh.example.utils.FileUtil;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * (Exgrade)表服务实现类
 * @author cjh
 * @description: TODO
 * @date 2020-05-07 17:04:42
 */
@Slf4j
@Service
public class ExgradeServiceImpl implements ExgradeService {
    @Resource
    private ExGradeMapper exgradeMapper;
    @Resource
    private ExSCMapper scMapper;
    @Autowired
    private FileUtil fileUtil;
    /**
     * 通过主键查询单条数据
     * @param grade 主键
     * @return 实例对象
     */
    @Override
    public ExGrade queryById(ExGrade grade) {
        return exgradeMapper.selectByPrimaryKey(grade);
    }

    /**
     * 查询多条数据
     * @param curpage 查询页码
     * @return 对象列表
     */
    @Override
    public List<ExGrade> queryAllByPage(int curpage) {
        Example example = new Example(ExGrade.class);
        example.orderBy("待排序字段").desc();
        RowBounds rowBounds = new RowBounds((curpage - 1) * 20, 20);
        return exgradeMapper.selectByExampleAndRowBounds(example, rowBounds);
    }

    /**
     * 新增数据
     * @param exgrade 实例对象
     * @return 实例对象
     */
    @Override
    public void insert(ExGrade exgrade) throws Throwable {
        log.info("学生的答题情况为：" + exgrade.toString());
        exgradeMapper.updateByPrimaryKeySelective(exgrade);
    }

    /**
     * 修改数据
     * @param exgrade 实例对象
     * @return 实例对象
     */
    @Override
    public void update(ExGrade exgrade) throws Throwable {
        log.info("提交的评分为：" + exgrade);
        exgradeMapper.updateByPrimaryKeySelective(exgrade);
    }

    /**
     * 通过主键删除数据
     * @param sno 主键
     * @return 是否成功
     */
    @Override
    public void deleteById(String sno) throws Throwable {
        exgradeMapper.deleteByPrimaryKey(sno);
    }

    @Override
    public List<ExGrade> queryListById(ExGrade grade) {
        Example example = new Example(ExSC.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("term", grade.getTerm()).
                andEqualTo("cname", grade.getCname());
        List<ExSC> exSCS = scMapper.selectByExample(example);
        // 根据学号、学期、课程、项目 查找每个学生自己的答题记录
        List<ExGrade> grades = new ArrayList<>();
        for (ExSC sc : exSCS) {
            grade.setSno(sc.getSno());
            grades.add(exgradeMapper.selectByPrimaryKey(grade));
        }
        return grades;
    }

    /**
     * 源码说 select 方法，会将实体类中 不为null的属性加入 WHERE 判断中
     * 以上待确认
     * @param grade
     * @return
     */
    @Override
    public List<ExGrade> queryByConditions(ExGrade grade) {
        return exgradeMapper.select(grade);
    }

    @Override
    public String generatExcel(List<ExGrade> grades) throws Throwable {
        List<String> title = Arrays.asList("学号", "姓名", "课程", "项目", "分数", "学期");
//        String filename = "成绩表";
        String filename = UUID.randomUUID().toString();
        return fileUtil.generatExcel(title, grades, filename);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void del(List<ExGrade> grades) throws Throwable {
        Example example = new Example(ExGrade.class);
        Example.Criteria criteria = example.createCriteria();
        for (ExGrade grade : grades) {
            criteria.andEqualTo("sno", grade.getSno())
                    .andEqualTo("cname", grade.getCname())
                    .andEqualTo("Term", grade.getTerm())
                    .andEqualTo("pname", grade.getPname());
            exgradeMapper.deleteByExample(example);
        }
    }
}
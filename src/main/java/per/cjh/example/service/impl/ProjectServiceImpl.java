package per.cjh.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import per.cjh.example.domain.*;
import per.cjh.example.mappers.ExGradeMapper;
import per.cjh.example.mappers.ExProjectMapper;
import per.cjh.example.mappers.ExSCMapper;
import per.cjh.example.service.ExproblemService;
import per.cjh.example.service.ProjectService;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author cjh
 * @description: TODO
 * @date 2020/5/6 19:59
 */
@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {
    @Resource
    private ExProjectMapper projectMapper;
    @Resource
    private ExSCMapper scMapper;
    @Resource
    private ExGradeMapper gradeMapper;
    @Autowired
    private ExproblemService problemService;
    @Override
    public List<ExProject> projectGet(String term, String cname) {
        Example example = new Example(ExProject.class);
        example.orderBy("pname").desc();
//        example.selectProperties("pname");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("term", term).
                andEqualTo("cname", cname);
        return projectMapper.selectByExample(example);
    }

    @Override
    public List<ExProject> projectGet(Integer curpage) {
        Example example = new Example(ExProject.class);
        // 按照学期、结束时间、开始时间降序显示
        example.orderBy("term").desc().
                orderBy("end").desc().
                orderBy("start").desc();
        RowBounds rowBounds = new RowBounds((curpage - 1) * 20, 20);
        return projectMapper.selectByExampleAndRowBounds(example, rowBounds);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public CommonRet insertOne(ExProject project) throws Throwable {
        // 存在则按主键修改时间，不存在则添加记录
        if (projectMapper.selectByPrimaryKey(project) != null) {
            projectMapper.updateByPrimaryKeySelective(project);
            return new CommonRet("400", "修改成功", project);
        }
        // 不存在则添加记录
        projectMapper.insertSelective(project);

        // 查找该课程的所有学生，让这些学生都有该项目的评分记录，分数和答案为空
        Example example = new Example(ExSC.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("cname", project.getCname()).
                andEqualTo("term", project.getTerm());
        List<ExSC> exSCS = scMapper.selectByExample(example);
        ExGrade exGrade = new ExGrade();
        // 设置所有学生对于当前项目评分记录统一的课程名、项目名、学期名
        exGrade.setPname(project.getPname()).
                setCname(project.getCname()).
                setTerm(project.getTerm());
        // 根据不同的学生，设置评分记录的学号、姓名、密码
        for (ExSC sc : exSCS) {
            exGrade.setSno(sc.getSno()).
                    setName(sc.getName()).
                    setPassword(sc.getPassword());
            gradeMapper.insertSelective(exGrade);
        }
        return new CommonRet("200", "添加成功", project);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void delete(ExProject project) throws Throwable {
        // 删除项目信息
        projectMapper.deleteByPrimaryKey(project);
        // 删除题目表 -学期、课程、项目（内部包含删除评分表）
        ExProblem problem = new ExProblem();
        problem.setTerm(project.getTerm()).
                setCname(project.getCname()).
                setPname(project.getPname());
        problemService.deleteById(problem);
    }
}

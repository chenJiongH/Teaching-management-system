package per.cjh.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import per.cjh.example.domain.*;
import per.cjh.example.mappers.LbgradeMapper;
import per.cjh.example.mappers.LbproblemMapper;
import per.cjh.example.mappers.LbprojectMapper;
import per.cjh.example.mappers.LbscMapper;
import per.cjh.example.service.LbprojectService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * (Lbproject)表服务实现类
 *
 * @author cjh
 * @description: TODO
 * @date 2020-06-03 07:34:52
 */
@Slf4j
@Service
public class LbprojectServiceImpl implements LbprojectService {
    @Resource
    private LbprojectMapper lbprojectMapper;
    @Resource
    private LbproblemMapper lbproblemMapper;
    @Resource
    private LbscMapper lbscMapper;
    @Resource
    private LbgradeMapper lbgradeMapper;
    @Autowired
    private Lbproject lbproject;
    /**
     * 查询多条数据
     *
     * @param term
     * @param cname
     * @param curpage 查询页码
     * @return 对象列表
     */
    @Override
    public List<LbProjectAndProblem> lbprojectGet(String term, String cname, int curpage) {
        Example example = new Example(Lbproject.class);
        example.orderBy("end").orderBy("start");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("term", term)
                .andEqualTo("cname", cname);
        RowBounds rowBounds = new RowBounds((curpage - 1) * 20, 20);
        // 给每个项目查询他们的题目
        List<Lbproject> lbprojects = lbprojectMapper.selectByExampleAndRowBounds(example, rowBounds);
        List<LbProjectAndProblem> projectAndProblems = new ArrayList<>();
        for (Lbproject project : lbprojects) {
            LbProjectAndProblem lbProjectAndProblem = new LbProjectAndProblem();
            lbProjectAndProblem.setCname(project.getCname()).
                                setEnd(project.getEnd()).
                                setNumber(project.getNumber()).
                                setPname(project.getPname()).
                                setStart(project.getStart()).
                                setTerm(project.getTerm());
            lbProjectAndProblem.setLbproblem(new Lbproblem());
            lbProjectAndProblem.getLbproblem().setTerm(project.getTerm()).
                                                setCname(project.getCname()).
                                                setPname(project.getPname());
            lbProjectAndProblem.setLbproblem(lbproblemMapper.selectByPrimaryKey(lbProjectAndProblem.getLbproblem()));
            projectAndProblems.add(lbProjectAndProblem);
        }
        return projectAndProblems;
    }

    /**
     * 教师评分页面，教师获取某个章节下的题目
     * @param term
     * @param cname
     * @param pname
     * @return
     */
    @Override
    public Lbproblem lbproblemGet(String term, String cname, String pname) {
        Example example = new Example(Lbproblem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("term", term).
                 andEqualTo("cname", cname).
                 andEqualTo("pname", pname);
        return lbproblemMapper.selectOneByExample(example);
    }

    /**
     * 查询多条数据
     *
     * @param term
     * @param cname
     * @param username 查询页码
     * @return 对象列表
     */
    @Override
    public List<Lbproject> lbprojectGetByCname(String term, String cname, String username) {
        Example example = new Example(Lbproject.class);
        example.orderBy("end").orderBy("start");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("term", term)
                .andEqualTo("cname", cname);
        return lbprojectMapper.selectByExample(example);
    }

    /**
     * 新增数据
     *
     * @param lbprojectAndProblem 实例对象
     * @throws Throwable 新增异常
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void  lbprojectPost(LbProjectAndProblem lbprojectAndProblem) throws Throwable {
        lbproject = this.getlbprojectBylbprojectAndProblem(lbprojectAndProblem);
        // 设置项目，设置项目以后该课程下的所有学生有了评分记录
        lbprojectMapper.insertSelective(this.lbproject);
        // 设置题目表
        lbprojectAndProblem.
                getLbproblem().setCname(lbproject.getCname())
                            .setPname(lbproject.getPname())
                            .setTerm(lbproject.getTerm())
                            .setNumber(lbproject.getNumber());
        lbproblemMapper.insertSelective(lbprojectAndProblem.getLbproblem());
        // 设置评分表
        // 查找该课程的所有学生，让这些学生都有该项目的评分记录，分数和答案为空
        Example example = new Example(Lbsc.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("cname", lbproject.getCname()).
                andEqualTo("term", lbproject.getTerm());
        List<Lbsc> lbscs = lbscMapper.selectByExample(example);
        Lbgrade lbgrade = new Lbgrade();
        // 设置所有学生对于当前项目评分记录统一的课程名、项目名、学期名
        lbgrade.setPname(lbproject.getPname()).
                setCname(lbproject.getCname()).
                setTerm(lbproject.getTerm());
        // 根据不同的学生，设置评分记录的学号、姓名、密码
        for (Lbsc sc : lbscs) {
            lbgrade.setSno(sc.getSno()).
                    setName(sc.getName()).
                    setPassword(sc.getPassword());
            lbgradeMapper.insertSelective(lbgrade);
        }
    }

    /**
     * 删除项目，插入新项目。删除题目，插入新题目
     * 查找所有的该项目的学生信息，一个个消除，一个个插入
     * @throws Throwable 修改异常
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void lbprojectPut(String oriTerm, String oriCname, String oriPname,  LbProjectAndProblem lbprojectAndProblem) throws Throwable {
        lbproject = this.getlbprojectBylbprojectAndProblem(lbprojectAndProblem);
        // 1、删除旧项目，插入新项目
        Example example = new Example(Lbproject.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("term", oriTerm)
                .andEqualTo("cname", oriCname)
                .andEqualTo("pname", oriPname);
        Lbproject oriLbproject = lbprojectMapper.selectByExample(example).get(0);
        // 删除旧项目
        lbprojectMapper.deleteByPrimaryKey(oriLbproject);
        // 设置新项目
        lbprojectMapper.insertSelective(lbproject);

        // 2、删除旧项目下的题目，插入新题目
        // 删除题目表
        lbproblemMapper.deleteByPrimaryKey(oriLbproject);
        // 设置新题目
        lbprojectAndProblem.
                getLbproblem().setCname(lbproject.getCname())
                .setPname(lbproject.getPname())
                .setTerm(lbproject.getTerm())
                .setNumber(lbproject.getNumber());
        lbproblemMapper.insertSelective(lbprojectAndProblem.getLbproblem());

        // 3、查找所有的学生评分记录，删除并修改之
        Example example1 = new Example(Lbgrade.class);
        Example.Criteria criteria1 = example.createCriteria();
        criteria.andEqualTo("term", oriTerm)
                .andEqualTo("cname", oriCname)
                .andEqualTo("pname", oriPname);
        List<Lbgrade> lbgrades = lbgradeMapper.selectByExample(example);
        for (Lbgrade lbgrade : lbgrades) {
            lbgradeMapper.deleteByPrimaryKey(lbgrade);
            // 改变项目名称等主键。已有的评分不变
            lbgrade.setCname(lbproject.getCname()).
                    setTerm(lbproject.getTerm()).
                    setPname(lbproject.getPname());
            lbgradeMapper.insertSelective(lbgrade);
        }
    }

    /**
     * 通过主键删除数据，同时删除项目下的题目。项目下的所有学生评分记录
     * @param lbproject 主键
     * @throws Throwable 删除异常
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void lbprojectDelete(Lbproject lbproject) throws Throwable {
        // 删除项目表
        lbprojectMapper.deleteByPrimaryKey(lbproject);
        // 删除题目表
        lbproblemMapper.deleteByPrimaryKey(lbproject);
        // 删除评分表
        Example example = new Example(Lbgrade.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("term", lbproject.getTerm())
                .andEqualTo("cname", lbproject.getCname())
                .andEqualTo("pname", lbproject.getPname());
        lbgradeMapper.deleteByExample(example);
    }

    @Override
    public Lbproject getlbprojectBylbprojectAndProblem(LbProjectAndProblem lbprojectAndProblem) {
        lbproject.setCname(lbprojectAndProblem.getCname())
                .setEnd(lbprojectAndProblem.getEnd())
                .setNumber(lbprojectAndProblem.getNumber())
                .setPname(lbprojectAndProblem.getPname())
                .setStart(lbprojectAndProblem.getStart())
                .setTerm(lbprojectAndProblem.getTerm());
        return lbproject;
    }
}
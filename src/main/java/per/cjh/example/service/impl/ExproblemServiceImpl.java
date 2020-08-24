package per.cjh.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import per.cjh.example.domain.CommonRet;
import per.cjh.example.domain.ExGrade;
import per.cjh.example.domain.ExProblem;
import per.cjh.example.mappers.ExGradeMapper;
import per.cjh.example.mappers.ExProblemMapper;
import per.cjh.example.service.ExproblemService;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * (Exproblem)表服务实现类
 * @author cjh
 * @description: TODO
 * @date 2020-05-07 17:09:54
 */
@Slf4j
@Service
public class ExproblemServiceImpl implements ExproblemService {
    @Resource
    private ExProblemMapper exproblemMapper;
    @Resource
    private ExGradeMapper gradeMapper;

    /**
     * 通过ID查询单条数据
     * @param term 主键
     * @return 实例对象
     */
    @Override
    public ExProblem queryById(ExProblem term) {
        return exproblemMapper.selectByPrimaryKey(term);
    }

    /**
     * 查询多条数据
     * @param curpage 查询页码
     * @return 对象列表
     */
    @Override
    public List<ExProblem> queryAllByPage(int curpage) {
        Example example = new Example(ExProblem.class);
        example.orderBy("term").desc().
                orderBy("cname").desc().
                orderBy("pname").desc();
        RowBounds rowBounds = new RowBounds((curpage - 1) * 20, 20);
        return exproblemMapper.selectByExampleAndRowBounds(example, rowBounds);
    }

    /**
     * 新增数据
     * @param exproblem 实例对象
     * @return 实例对象
     */
    @Override
    public CommonRet insert(ExProblem exproblem) throws Throwable {
        CommonRet commonRet = new CommonRet();
        if (exproblemMapper.selectByPrimaryKey(exproblem) == null) {
            exproblemMapper.insertSelective(exproblem);
            commonRet.setCode("200")
                    .setMessage("添加成功")
                    .setJson(exproblem);
        } else {
            this.update(exproblem);
            commonRet.setCode("400")
                    .setMessage("修改成功")
                    .setJson(exproblem);
        }
        return commonRet;
    }

    /**
     * 修改数据
     * @param exproblem 实例对象
     * @return 实例对象
     */
    @Override
    public void update(ExProblem exproblem) throws Throwable {
    
        exproblemMapper.updateByPrimaryKey(exproblem);
    }

    /**
     * 通过主键删除数据
     * @param term 主键
     * @return 是否成功
     */
    @Override
    public void deleteById(ExProblem term) throws Throwable {
        exproblemMapper.deleteByPrimaryKey(term);
        // 删除评分表 -学期、课程、项目
        Example example = new Example(ExGrade.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("term", term.getTerm()).
                andEqualTo("cname", term.getCname()).
                andEqualTo("pname", term.getPname());
        gradeMapper.deleteByExample(example);
    }
}
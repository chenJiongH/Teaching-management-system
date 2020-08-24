package per.cjh.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import per.cjh.example.domain.Lbteacher;
import per.cjh.example.mappers.LbteacherMapper;
import per.cjh.example.service.LbteacherService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * (Lbteacher)表服务实现类
 * @author cjh
 * @description: TODO
 * @date 2020-06-02 15:38:12
 */
@Slf4j
@Service
public class LbteacherServiceImpl implements LbteacherService {

    @Resource
    private LbteacherMapper lbteacherMapper;


    /**
     * 查询多条数据
     *
     * @param term
     * @param curpage 查询页码
     * @return 对象列表
     */
    @Override
    public List<Lbteacher> lbteacherGet(String term, int curpage) {
        Example example = new Example(Lbteacher.class);
        example.orderBy("term").desc();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("term", term);
        RowBounds rowBounds = new RowBounds((curpage - 1) * 20, 20);
        return lbteacherMapper.selectByExampleAndRowBounds(example, rowBounds);
    }

    /**
     * 新增数据
     * @param lbteacher 实例对象
     * @throws Throwable 新增异常
     */
    @Override
    public void lbteacherPost(Lbteacher lbteacher) throws Throwable {
        log.info(lbteacherMapper.selectByPrimaryKey(lbteacher) +"");
        if (lbteacherMapper.selectByPrimaryKey(lbteacher) != null) {
            lbteacherMapper.updateByPrimaryKeySelective(lbteacher);
        } else {
            lbteacherMapper.insertSelective(lbteacher);
        }
    }

    /**
     * 修改数据
     * @param lbteacher 实例对象
     * @throws Throwable 修改异常
     */
    @Override
    public void lbteacherPut(Lbteacher lbteacher) throws Throwable {
    
        lbteacherMapper.updateByPrimaryKey(lbteacher);
    }

    /**
     * 通过主键删除数据
     * @param lbteacher 主键
     * @throws Throwable 删除异常
     */
    @Override
    public void lbteacherDelete(Lbteacher lbteacher) throws Throwable {
        lbteacherMapper.deleteByPrimaryKey(lbteacher);
    }
}
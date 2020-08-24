package per.cjh.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import per.cjh.example.domain.Lbtc;
import per.cjh.example.mappers.LbtcMapper;
import per.cjh.example.mappers.LbteacherMapper;
import per.cjh.example.service.LbtcService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * (Lbtc)表服务实现类
 * @author cjh
 * @description: TODO
 * @date 2020-06-02 14:49:53
 */
@Slf4j
@Service
public class LbtcServiceImpl implements LbtcService {
    @Resource
    private LbtcMapper lbtcMapper;
    @Resource
    private LbteacherMapper lbteacherMapper;

    /**
     * 按学期分页查询教师数据
     * @param curpage 查询页码
     * @param term
     * @return 对象列表
     */
    @Override
    public List<Lbtc> lbtcGet(int curpage, String term) {
        Example example = new Example(Lbtc.class);
        example.orderBy("term").orderBy("name");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("term", term);
        RowBounds rowBounds = new RowBounds((curpage - 1) * 20, 20);
        return lbtcMapper.selectByExampleAndRowBounds(example, rowBounds);
    }

    /**
     * 有则新增，没有则修改
     * @param lbtc 实例对象
     * @throws Throwable 新增异常
     */
    @Override
    public String lbtcPost(Lbtc lbtc) throws Throwable {
        // 查找是否有该教师存在，不存在该教师则无法设置教师的课程。
        String name = lbteacherMapper.selectByPrimaryKey(lbtc).getName();
        lbtc.setName(name);
        int i = Integer.parseInt(lbtc.getTerm().substring(0, 4));
        String term = lbtc.getTerm().substring(11);
        if (lbtcMapper.selectByPrimaryKey(lbtc) != null) {
            // 删除同时删除。修改同时修改。比如给2020学年-2021学年第一学期增加课程1，则该老师在2021学年-2022学年第一学期也有了课程1
            /* 修改由于教师课程页面上都是主键，故可能没有修改操作*/
            for (; i <= 2030; i++) {
                String time = i+"";
                lbtc.setTerm(time + "-" + (i + 1) + "学年" + term);
                lbtcPut(lbtc);
            }
            return "修改成功";
        }
        // 增加的话，之后的学年也会共同继承这个学期。
        // 比如给2020学年-2021学年第一学期增加课程1，则该老师在2021学年-2022学年第一学期也有了课程1
        for (; i <= 2030; i++) {
            String time = i+"";
            lbtc.setTerm(time + "-" + (i + 1) + "学年" + term);
            lbtcMapper.insertSelective(lbtc);
        }
        return "增加成功";
    }

    /**
     * 修改数据
     * @param lbtc 实例对象
     * @throws Throwable 修改异常
     */
    @Override
    public void lbtcPut(Lbtc lbtc) throws Throwable {
    
        lbtcMapper.updateByPrimaryKeySelective(lbtc);
    }

    /**
     * 通过主键删除数据
     * @param lbtc 主键
     * @throws Throwable 删除异常
     */
    @Override
    public void lbtcDelete(Lbtc lbtc) throws Throwable {
        // 删除同时删除。修改同时修改。
        int i = Integer.parseInt(lbtc.getTerm().substring(0, 4));
        String term = lbtc.getTerm().substring(11);
        for (; i <= 2030; i++) {
            String time = i + "";
            lbtc.setTerm(time + "-" + (i + 1) + "学年" + term);
            lbtcMapper.deleteByPrimaryKey(lbtc);
        }
    }
}
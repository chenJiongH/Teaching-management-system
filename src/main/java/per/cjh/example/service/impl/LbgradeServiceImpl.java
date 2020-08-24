package per.cjh.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import per.cjh.example.domain.ExGrade;
import per.cjh.example.domain.Lbgrade;
import per.cjh.example.mappers.LbgradeMapper;
import per.cjh.example.service.LbgradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import per.cjh.example.utils.FileUtil;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * (Lbgrade)表服务实现类
 * @author cjh
 * @description: TODO
 * @date 2020-06-07 08:08:35
 */
@Slf4j
@Service
public class LbgradeServiceImpl implements LbgradeService {
    @Resource
    private LbgradeMapper lbgradeMapper;
    @Autowired
    private FileUtil fileUtil;


    /**
     * 教师评分页面查询多条数据
     * @param term, cname, pname 查询页码
     * @return 对象列表
     */
    @Override
    public List<Lbgrade> lbgradeGet(String term, String cname, String pname) {
        Example example = new Example(Lbgrade.class);
        example.orderBy("sno");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("term", term).
                 andEqualTo("cname", cname).
                 andEqualTo("pname", pname);
        return lbgradeMapper.selectByExample(example);
    }


    /**
     * 学生答题、查分页面查询多条数据
     * @param term, cname, pname
     * @return 对象列表
     */
    @Override
    public Lbgrade lbstugradeGet(String term, String cname, String pname, String username) throws Throwable {
        Example example = new Example(Lbgrade.class);
        example.orderBy("sno");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("term", term).
                andEqualTo("cname", cname).
                andEqualTo("pname", pname);
        if (!"null".equals(username)) {
            criteria.andEqualTo("sno", username);
        }
        return lbgradeMapper.selectByExample(example).get(0);
    }

    @Override
    public Lbgrade lbstugradePut(Lbgrade lbgrade) {
        lbgradeMapper.updateByPrimaryKeySelective(lbgrade);
        return null;
    }

    @Override
    public String generatExcel(List<Lbgrade> grades) throws Throwable {
        List<String> title = Arrays.asList("学号", "姓名", "课程", "章节", "分数", "学期");
//        String filename = "成绩表";
        String filename = UUID.randomUUID().toString();
        return fileUtil.generatlbExcel(title, grades, filename);
    }

    /**
     * 新增数据
     * @param lbgrade 实例对象
     * @throws Throwable 新增异常
     */
    @Override
    public void lbgradePost(Lbgrade lbgrade) throws Throwable {
    
        lbgradeMapper.insertSelective(lbgrade);
    }

    /**
     * 修改数据
     * @param lbgrade 实例对象
     * @throws Throwable 修改异常
     */
    @Override
    public void lbgradePut(Lbgrade lbgrade) throws Throwable {

        lbgradeMapper.updateByPrimaryKey(lbgrade);
    }

    /**
     * 通过查询到的所有评分记录删除数据
     * @param grades 查询到的所有评分记录
     * @throws Throwable 删除异常
     */
    @Override
    public void lbgradeDelete(List<Lbgrade> grades) throws Throwable {

        Example example = new Example(ExGrade.class);
        Example.Criteria criteria = example.createCriteria();
        for (Lbgrade grade : grades) {
            lbgradeMapper.deleteByPrimaryKey(grade);
        }
    }
}
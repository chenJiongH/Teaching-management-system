package per.cjh.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.springframework.stereotype.Service;
import per.cjh.example.domain.*;
import per.cjh.example.mappers.*;
import per.cjh.example.service.TermCourseService;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author cjh
 * @description: TODO
 * @date 2020/5/6 17:40
 */
@Slf4j
@Service
public class TermCourseServiceImpl implements TermCourseService {

    @Resource
    private ExTCMapper tcMapper;
    @Resource
    private LbtcMapper lbtcMapper;
    @Resource
    private ExSCMapper scMapper;
    @Resource
    private LbscMapper lbscMapper;
    @Resource
    private ExCourceMapper courceMapper;
    @Resource
    private ExTermMapper termMapper;

    @Override
    public void addTerms(Set<ExTerm> terms) throws Throwable {
        for (ExTerm term : terms) {
            if (termMapper.selectByPrimaryKey(term) == null) {
                termMapper.insert(term);
            }
        }
    }
    @Override
    public void addCourses(Set<ExCource> cources) throws Throwable {
        for (ExCource cource : cources) {
            if (courceMapper.selectByPrimaryKey(cource) == null) {
                courceMapper.insert(cource);
            }
        }
    }

    @Override
    public List<String> termGet(String username, String type) {
        List<String> set = new ArrayList<>();
        if ("教师".equals(type)) {
            // 从教师课程表获取该老师的所有学期
            Example example = new Example(ExTC.class);
            example.setDistinct(true);
            example.selectProperties("term");
            example.orderBy("term").asc();
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("phone", username);
            List<ExTC> exTCS = tcMapper.selectByExample(example);
            for (ExTC extc : exTCS) {
                set.add(extc.getTerm());
            }
            log.info("查询学期为："+ username + " " + set);
        } else if ("学生".equals(type)) {
            // 从学生课程表获取所有学期
            Example example2 = new Example(ExSC.class);
            example2.setDistinct(true);
            example2.selectProperties("term");
            example2.orderBy("term");
            List<ExSC> exSCS = scMapper.selectByExample(example2);
            for (ExSC exsc : exSCS) {
                set.add(exsc.getTerm());
            }
        }
        return set;
    }

    @Override
    public List<String> lbtermGet(String username, String type) {
        List<String> set = new ArrayList<>();
        if (type == null) {
            type = "教师";
        }
        if ("教师".equals(type)) {
            // 从教师课程表获取该老师的所有学期
            Example example = new Example(Lbtc.class);
            example.setDistinct(true);
            example.selectProperties("term");
            example.orderBy("term").asc();
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("phone", username);
            List<Lbtc> exTCS = lbtcMapper.selectByExample(example);
            for (Lbtc lbtc : exTCS) {
                set.add(lbtc.getTerm());
            }
            log.info("查询学期为："+ username + " " + set);
        } else if ("学生".equals(type)) {
            // 从学生课程表获取所有学期
            Example example2 = new Example(Lbsc.class);
            example2.setDistinct(true);
            example2.selectProperties("term");
            example2.orderBy("term");
            List<Lbsc> exSCS = lbscMapper.selectByExample(example2);
            for (Lbsc lbsc : exSCS) {
                set.add(lbsc.getTerm());
            }
        }
        // 如果当前访问账号没有登录，则可以看到所有学期
        if (username == null) {
            set = new ArrayList<>();
            for (int i = 2020; i <= 2030; i++) {
                String year = i+"-"+(i+1)+"学年";
                set.add(year + "第一学期");
                set.add(year + "第二学期");
            }
            return set;
        }
        return set;
    }

    @Override
    public Set<String> courseGetByTerm(String username, String termName, String type) {
        HashSet<String> retSet = new HashSet<>();
        if (type == null) {
            type = "管理员";
        }
        log.info("开始查询课程！！");
        log.info("username: " + username + " type: " + type + " termName: " + termName );
        switch (type) {
            case "学生":
                this.stuGetCourse(username, termName, type, retSet);
                break;
            case "教师":
                this.teacherGetCourse(username, termName, type, retSet);
                break;
            case "管理员":
                this.stuGetCourse(username, termName, type, retSet);
                this.teacherGetCourse(username, termName, type, retSet);
                break;
            default:
        }
        log.info("查询到的课程有：" + retSet);
        return retSet;
    }

    @Override
    public Set<String> lbcourseGetByTerm(String username, String termName, String type) {
        HashSet<String> retSet = new HashSet<>();
        if (type == null) {
            type = "管理员";
        }
        log.info("开始查询课程！！");
        log.info("username: " + username + " type: " + type + " termName: " + termName );
        switch (type) {
            case "学生":
                this.lbstuGetCourse(username, termName, type, retSet);
                break;
            case "教师":
                this.lbteacherGetCourse(username, termName, type, retSet);
                break;
            case "管理员":
                this.lbstuGetCourse(username, termName, type, retSet);
                this.lbteacherGetCourse(username, termName, type, retSet);
                break;
            default:
        }
        log.info("查询到的课程有：" + retSet);
        return retSet;
    }

    /**
     * 教师获取课程，以传入引用的方式改变 retSet 的值
     * @param username
     * @param termName
     * @param type
     * @param retSet
     * @return
     */
    private void teacherGetCourse(String username, String termName, String type, HashSet<String> retSet) {
        // 从教师课程表获取所有课程
        Example example2 = new Example(ExTC.class);
        example2.setDistinct(true);
        example2.selectProperties("cname");
        Example.Criteria criteria2 = example2.createCriteria();
        if (!"管理员".equals(type)) {
            criteria2.andEqualTo("phone", username);
        }
        criteria2.andEqualTo("term", termName);
        List<ExTC> exTCS = tcMapper.selectByExample(example2);
        // 把 List javaBean 集合转换成 Set 的字符串型集合
        for (ExTC exTC : exTCS) {
            retSet.add(exTC.getCname());
        }
    }

    /**
     * 教师获取课程，以传入引用的方式改变 retSet 的值
     * @param username
     * @param termName
     * @param type
     * @param retSet
     * @return
     */
    private void lbteacherGetCourse(String username, String termName, String type, HashSet<String> retSet) {
        // 从教师课程表获取所有课程
        Example example2 = new Example(Lbtc.class);
        example2.setDistinct(true);
        example2.selectProperties("cname");
        Example.Criteria criteria2 = example2.createCriteria();
        if (!"管理员".equals(type)) {
            criteria2.andEqualTo("phone", username);
        }
        criteria2.andEqualTo("term", termName);
        List<Lbtc> exTCS = lbtcMapper.selectByExample(example2);
        // 把 List javaBean 集合转换成 Set 的字符串型集合
        for (Lbtc exTC : exTCS) {
            retSet.add(exTC.getCname());
        }
    }

    /**
     * 学生获取课程，以传入引用的方式改变 retSet 的值
     * @param username
     * @param termName
     * @param type
     * @param retSet
     * @return
     */
    private void stuGetCourse(String username, String termName, String type, HashSet<String> retSet) {
        // 从学生课程表获取所有课程
        Example example = new Example(ExSC.class);
        example.setDistinct(true);
        example.selectProperties("cname");
        Example.Criteria criteria = example.createCriteria();
        if (!"管理员".equals(type)) {
            criteria.andEqualTo("sno", username);
        }
        criteria.andEqualTo("term", termName);
        List<ExSC> exSCS = scMapper.selectByExample(example);
        // 把 List javaBean 集合转换成 Set 的字符串型集合
        for (ExSC exSC : exSCS) {
            retSet.add(exSC.getCname());
        }
    }
    /**
     * 学生获取课程，以传入引用的方式改变 retSet 的值
     * @param username
     * @param termName
     * @param type
     * @param retSet
     * @return
     */
    private void lbstuGetCourse(String username, String termName, String type, HashSet<String> retSet) {
        // 从学生课程表获取所有课程
        Example example = new Example(Lbsc.class);
        example.setDistinct(true);
        example.selectProperties("cname");
        Example.Criteria criteria = example.createCriteria();
        if (!"管理员".equals(type)) {
            criteria.andEqualTo("sno", username);
        }
        criteria.andEqualTo("term", termName);
        List<Lbsc> lbscs = lbscMapper.selectByExample(example);
        // 把 List javaBean 集合转换成 Set 的字符串型集合
        for (Lbsc exSC : lbscs) {
            retSet.add(exSC.getCname());
        }
    }
}

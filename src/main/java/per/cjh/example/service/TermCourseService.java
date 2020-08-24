package per.cjh.example.service;

import per.cjh.example.domain.ExCource;
import per.cjh.example.domain.ExTerm;

import java.util.List;
import java.util.Set;
/**
 * @author cjh
 * @description: TODO
 * @date 2020/5/6 17:40
 */
public interface TermCourseService {
    /**
     * 由于导入学生课程记录，而增加的学期数
     * 使用主键查询，有结果则不插入，没结果则新增
     * @param terms
     * @throws Throwable
     */
    void addTerms(Set<ExTerm> terms) throws Throwable;

    /**
     * 由于导入学生课程记录，而增加的课程数
     * 使用主键查询，有结果则不插入，没结果则新增
     * @param cources
     * @throws Throwable
     */
    void addCourses(Set<ExCource> cources) throws Throwable;

    /**
     * 获取所有的学期
     * @return
     * @param username
     * @param type
     */
    List<String> termGet(String username, String type);

    /**
     *  判断三种角色
     * 根据学期，获取该学期下该角色能够看见的所有课程
     * @param username 教师则为手机号、学生则为学号
     * @param termName 当前选中的学期
     * @param type 区分教师和管理员、学生
     * @return
     * @throws Throwable
     */
     Set<String> courseGetByTerm(String username, String termName, String type);

    List<String> lbtermGet(String username, String type);

    Set<String> lbcourseGetByTerm(String username, String term, String type);
}

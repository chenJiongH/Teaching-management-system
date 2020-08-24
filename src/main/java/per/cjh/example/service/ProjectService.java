package per.cjh.example.service;

import per.cjh.example.domain.CommonRet;
import per.cjh.example.domain.ExProject;

import java.util.List;

/**
 * @author cjh
 * @description: TODO
 * @date 2020/5/6 19:58
 */
public interface ProjectService {
    /**
     * 三种角色在下拉框中，根据学期、课程，查找项目名集合。因为在选中学期，查找课程之时，已经对课程做了角色限定，故项目可以不再限定
     * @param term
     * @param cname
     * @return
     */
    List<ExProject> projectGet(String term, String cname);

    /**
     * 管理员设置项目页面，根据页码查找已有的所有项目
     * @param curpage
     * @return
     */
    List<ExProject> projectGet(Integer curpage);

    /**
     * 管理员设置项目，传入完整的项目信息
     * @param project
     * @throws Throwable
     */
    CommonRet insertOne(ExProject project) throws Throwable;

    /**
     * 管理员删除项目，有效信息为学期、课程名、项目名
     * 删除项目时，连同删除题目表、答题评分表
     * @param project
     * @throws Throwable
     */
    void delete(ExProject project) throws Throwable;
}

package per.cjh.example.service;

import per.cjh.example.domain.LbProjectAndProblem;
import per.cjh.example.domain.Lbproblem;
import per.cjh.example.domain.Lbproject;

import java.util.List;

/**
 * (Lbproject)表服务接口
 * @author cjh
 * @description: TODO
 * @date 2020-06-03 07:34:52
 */
public interface LbprojectService {

    /**
     * 查询多条数据
     *
     * @param term
     * @param cname
     * @param curpage 查询页码
     * @return 对象列表
     */
    List<LbProjectAndProblem> lbprojectGet(String term, String cname, int curpage);

    /**
     * 新增数据
     * @param lbproject 实例对象
     * @return 实例对象
     * @throws Throwable 增加出错
     */
    void lbprojectPost(LbProjectAndProblem lbproject) throws Throwable;

    /**
     * 修改数据
     * @throws Throwable 修改出错
     */
    void lbprojectPut(String oriTerm, String oriCname, String pname,  LbProjectAndProblem lbprojectAndProblem) throws Throwable;

    /**
     * 通过主键删除数据
     * @param pname 主键
     * @throws Throwable 删除出错
     */
    void lbprojectDelete(Lbproject pname) throws Throwable;

    Lbproject getlbprojectBylbprojectAndProblem(LbProjectAndProblem lbprojectAndProblem);

    List<Lbproject> lbprojectGetByCname(String term, String cname, String username);

    Lbproblem lbproblemGet(String term, String cname, String pname);
}
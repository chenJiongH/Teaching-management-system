package per.cjh.example.service;

import per.cjh.example.domain.Lbgrade;

import java.util.List;

/**
 * (Lbgrade)表服务接口
 * @author cjh
 * @description: TODO
 * @date 2020-06-07 08:08:35
 */
public interface LbgradeService {

    /**
     * 查询多条学生评分答题数据
     * @param  term,  cname,  pname
     * @return 对象列表
     */
    List<Lbgrade> lbgradeGet(String term, String cname, String pname);
    String generatExcel(List<Lbgrade> grades) throws Throwable;

    /**
     * 新增数据
     * @param lbgrade 实例对象
     * @return 实例对象
     * @throws Throwable 增加出错
     */
    void lbgradePost(Lbgrade lbgrade) throws Throwable;

    /**
     * 修改数据
     * @param lbgrade 实例对象
     * @throws Throwable 修改出错
     */
    void lbgradePut(Lbgrade lbgrade) throws Throwable;

    /**
     * 通过主键删除数据
     * @param sno 主键
     * @throws Throwable 删除出错
     */
    void lbgradeDelete(List<Lbgrade> sno) throws Throwable;

    Lbgrade lbstugradeGet(String term, String cname, String pname, String username) throws Throwable;

    Lbgrade lbstugradePut(Lbgrade lbgrade);
}
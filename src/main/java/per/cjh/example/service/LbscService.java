package per.cjh.example.service;


import per.cjh.example.domain.Lbsc;

import java.util.List;

/**
 * (Lbsc)表服务接口
 * @author cjh
 * @description: TODO
 * @date 2020-06-03 06:32:09
 */
public interface LbscService {

    /**
     * 查询多条数据
     *
     * @param term
     * @param curpage 查询页码
     * @return 对象列表
     */
    List<Lbsc> lbscGet(String term, String cname, int curpage);

    /**
     * 新增数据
     * @param lbsc 实例对象
     * @return 实例对象
     * @throws Throwable 增加出错
     */
    String lbscPost(Lbsc lbsc) throws Throwable;

    /**
     * 修改数据
     * @param lbsc 实例对象
     * @throws Throwable 修改出错
     */
    void lbscPut(Lbsc lbsc) throws Throwable;

    /**
     * 通过主键删除数据
     * @param sno 主键
     * @throws Throwable 删除出错
     */
    void lbscDelete(Lbsc sno) throws Throwable;

    void parseStuFile(String dest, String term, String cname) throws Throwable;

    List<Lbsc> lbscCourseGet(String sno);
}
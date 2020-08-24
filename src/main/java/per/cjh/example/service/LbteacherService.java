package per.cjh.example.service;

import per.cjh.example.domain.Lbteacher;

import java.util.List;

/**
 * (Lbteacher)表服务接口
 * @author cjh
 * @description: TODO
 * @date 2020-06-02 15:38:12
 */
public interface LbteacherService {

    /**
     * 查询多条数据
     *
     * @param term
     * @param curpage 查询页码
     * @return 对象列表
     */
    List<Lbteacher> lbteacherGet(String term, int curpage);

    /**
     * 新增数据
     * @param lbteacher 实例对象
     * @return 实例对象
     * @throws Throwable 增加出错
     */
    void lbteacherPost(Lbteacher lbteacher) throws Throwable;

    /**
     * 修改数据
     * @param lbteacher 实例对象
     * @throws Throwable 修改出错
     */
    void lbteacherPut(Lbteacher lbteacher) throws Throwable;

    /**
     * 通过主键删除数据
     * @param term 主键
     * @throws Throwable 删除出错
     */
    void lbteacherDelete(Lbteacher term) throws Throwable;

}
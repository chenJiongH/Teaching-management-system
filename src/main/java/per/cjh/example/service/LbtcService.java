package per.cjh.example.service;

import per.cjh.example.domain.Lbtc;

import java.util.List;

/**
 * (Lbtc)表服务接口
 * @author cjh
 * @description: TODO
 * @date 2020-06-02 14:49:53
 */
public interface LbtcService {

    /**
     * 查询多条数据
     * @param curpage 查询页码
     * @param term
     * @return 对象列表
     */
    List<Lbtc> lbtcGet(int curpage, String term);

    /**
     * 新增数据
     * @param lbtc 实例对象
     * @return 实例对象
     * @throws Throwable 增加出错
     */
    String lbtcPost(Lbtc lbtc) throws Throwable;

    /**
     * 修改数据
     * @param lbtc 实例对象
     * @throws Throwable 修改出错
     */
    void lbtcPut(Lbtc lbtc) throws Throwable;

    /**
     * 通过主键删除数据
     * @param phone 主键
     * @throws Throwable 删除出错
     */
    void lbtcDelete(Lbtc phone) throws Throwable;

}
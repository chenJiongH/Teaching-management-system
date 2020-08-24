package per.cjh.example.service;

import per.cjh.example.domain.CommonRet;
import per.cjh.example.domain.ExProblem;

import java.util.List;

/**
 * (Exproblem)表服务接口
 * @author cjh
 * @description: TODO
 * @date 2020-05-07 17:09:54
 */
public interface ExproblemService {

    /**
     * 通过 ID 查询单条数据
     * @param term 主键
     * @return 实例对象
     */
    ExProblem queryById(ExProblem term);

    /**
     * 查询多条数据
     * @param curpage 查询页码
     * @return 对象列表
     */
    List<ExProblem> queryAllByPage(int curpage);

    /**
     * 新增数据，有则覆盖，无则更新
     * @param exproblem 实例对象
     * @return 实例对象
     * @throws Throwable 增加出错
     */
    CommonRet insert(ExProblem exproblem) throws Throwable;

    /**
     * 修改数据
     * @param exproblem 实例对象
     * @throws Throwable 修改出错
     */
    void update(ExProblem exproblem) throws Throwable;

    /**
     * 通过主键删除数据
     * @param term 主键
     * @throws Throwable 删除出错
     */
    void deleteById(ExProblem term) throws Throwable;

}
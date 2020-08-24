package per.cjh.example.service;

import per.cjh.example.domain.ExGrade;

import java.util.List;

/**
 * (Exgrade)表服务接口
 * @author cjh
 * @description: TODO
 * @date 2020-05-07 17:04:42
 */
public interface ExgradeService {

    /**
     * 通过 ID 查询单条数据
     * @param sno 主键
     * @return 实例对象
     */
    ExGrade queryById(ExGrade sno);

    /**
     * 查询多条数据
     * @param curpage 查询页码
     * @return 对象列表
     */
    List<ExGrade> queryAllByPage(int curpage);

    /**
     * 新增数据
     * @param exgrade 实例对象
     * @throws Throwable
     */
    void insert(ExGrade exgrade) throws Throwable;

    /**
     * 修改数据
     * @param exgrade 实例对象
     */
    void update(ExGrade exgrade) throws Throwable;

    /**
     * 通过主键删除数据
     * @param sno 主键
     */
    void deleteById(String sno) throws Throwable;

    /**
     * 教师评分页面
     * 教师根据学期、课程、项目 获取所有的学生答题情况
     * 需要先获取所有学生，再获取学生评分表的情况
     * @param grade
     * @return
     */
    List<ExGrade> queryListById(ExGrade grade);

    /**
     * 教师评分管理页面、管理数据维护页面：通过项目、学期、课程任意种条件查询学生的答题数据
     * @param grade
     * @return
     */
    List<ExGrade> queryByConditions(ExGrade grade);

    /**
     * 解析上传的评分数据到 Excel 文件中，返回 Excel 文件目录
     * @param grades
     * @return
     */
    String generatExcel(List<ExGrade> grades) throws Throwable;

    /**
     * 管理员的数据维护页面，点击删除评分按钮，上传查询到的json数据
     */
    void del(List<ExGrade> grades) throws Throwable ;
}
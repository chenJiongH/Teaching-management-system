package per.cjh.example.service;

import per.cjh.example.domain.ExSC;

import java.util.List;

/**
 * @author cjh
 * @description: TODO
 * @date 2020/5/5 19:58
 */
public interface StuService {

    /**
     * 按照页码获取所有学生课程记录
     * @param curpage
     * @return
     */
    List<ExSC> stuGet(Integer curpage);

    /**
     * 解析管理员导入的学生课程表，并返回 学生课程 记录集合
     * 导入的 Excel 表格头为：学号 姓名 密码 课程 学期 电话
     * @param dest 学生课程表所在的绝对路径
     * @param term
     * @param cname
     * @return
     * @throws Throwable 上传文件不是 Excel
     */
    List<ExSC> parseStuFile(String dest, String term, String cname) throws Throwable;

    /**
     * 将管理员上传的学生课程记录集合，全部插入到数据库中
     * 需要增加事务管理
     * @param exSCS 学生课程记录集合
     * @throws Throwable
     */
    void insertAll(List<ExSC> exSCS) throws Throwable;

    /**
     * 管理员按照姓名、手机号、学号查询一条学生记录，再修改该记录
     * 根据实体中的属性进行查询，只能有一个返回值，有多个结果是抛出异常，查询条件使用等号
     * @param exSC
     * @throws Throwable
     * @return ExSC
     */
    ExSC selectOne(ExSC exSC) throws Throwable;

    /**
     * 根据查询到的学号、手机号、姓名作为修改的 where 条件，然后进行课程表内的所有记录查询修改
     * 不仅仅以学号作为查询是因为，可能导入的时候，导入了重复的学号，此时修改该学号，就相当于修改了两个学生
     * @param exSC
     * @param oriSno 查询到的学号
     * @param oriName 查询到的姓名
     * @param oriPassword 查询到的密码
     * @throws Throwable
     */
    void updateOne(ExSC exSC, String oriSno, String oriName, String oriPassword) throws Throwable;

    /**
     * 借助分页查询，利用学号查询一条学生信息
     * @param sno
     * @return
     * @throws Throwable
     */
    ExSC stuSelfGet(String sno) throws Throwable;
}

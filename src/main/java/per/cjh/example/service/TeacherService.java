package per.cjh.example.service;

import per.cjh.example.domain.ExTC;

import java.util.List;

/**
 * @author cjh
 * @description: TODO
 * @date 2020/5/6 8:47
 */
public interface TeacherService {

    /**
     * 分页查询。按照学期降序和姓名降序，获取所有的教师课程记录
     * @return
     * @throws Throwable
     * @param curpage
     */
    List<ExTC> get(Integer curpage) throws Throwable;

    /**
     * 教师信息管理页面，点击添加教师
     * @param exTC
     * @throws Throwable
     */
    void insertOne(ExTC exTC) throws Throwable;

    /**
     * 根据用户传入的教师手机、课程、学期，对该条记录进行修改
     * @param phone 原记录手机号
     * @param term 原记录学期
     * @param cname 原记录课程
     * @param exTC
     * @throws Throwable
     */
    void updateOne(String phone, String term, String cname, ExTC exTC) throws Throwable;

    /**
     * 根据三主键确定一条记录，然后删除该记录
     * @param phone 手机主键
     * @param term 学期主键
     * @param cname 课程主键
     * @throws Throwable
     */
    void deleteOne(String phone, String term, String cname) throws Throwable;

    /**
     * 借助分页查询，利用教师手机号查询一条教师信息
     * @param phone
     * @return
     * @throws Throwable
     */
    ExTC teacherSelfGet(String phone);

    /**
     * 修改教师个人信息页面，根据原手机号修改教师密码
     * @param exTC
     * @param oriPhone
     */
    void update(String exTC, String oriPhone) throws Throwable;
}

package per.cjh.example.service;

import org.springframework.web.multipart.MultipartFile;
import per.cjh.example.domain.*;

import java.util.List;

/**
 * @author cjh
 * @description: TODO
 * @date 2020/5/5 10:54
 */
public interface FileService {


    /**
     * 根据文件类型（fileType= [video、schedule] 来插入视频 或者进度表）
     * @param file
     * @param fileType
     * @param name 用户名字
     * @param summary 对于视频来说，这是视频简介，对于进度表来说，这是类别
     * @param dest
     * @param category
     * @throws Throwable
     */
    Object insertOne(MultipartFile file, String fileType, String name, String summary, String dest, String category) throws Throwable;

    /**
     * 根据id删除一个视频 或者进度表、新闻文件类型（fileType= [video、schedule、news] ）
     * @param fileType 操作的文件类型
     * @param id 被称作的主键 id
     * @throws Throwable
     */
    void delete(String fileType, Integer id) throws Throwable;

    /**
     * 按照页码。获取所有的进度表记录
     * @return
     */
    Object getScheduleByPage(Integer curpage, String category);

    /**
     * 按照页码。获取所有的视频记录
     * 实验课：工科、理科
     * 理论课： 理论课，查询课程为空的公共视频
     * @return
     * @param category
     */
    Object getVideoByPage(String category);

    /**
     * 按照页码。获取所有的新闻记录
     * @return
     * @param curpage
     * @param type
     */
    List<ExNews> getNewsByPage(Integer curpage, String type);

    /**
     * 用户传入 title、content、upTime、summary
     * @param exNews
     * @throws Throwable
     */
    ExNews insertOneNews(ExNews exNews) throws Throwable;

    ExNews getNewsById(int id);

    Object getVideoByPage(Integer curpage, String term, String cname);

    Object getScheduleByPage(Integer curpage, String term, String cname);

    Object insertOneLbnews(Lbnews news) throws Throwable ;
    Object insertOneLbnotice(Lbnotice lbnotice) throws Throwable ;

    /**
     * 理论课管理员移动两条新闻的位置，传送被点击的id、移动的方向、学期和课程名。返回当前的被点击行应该被替换的记录
     * @param id
     * @param dir
     * @param term 学期和课程是为了确定他的上一条记录或者是下一条记录
     * @param cname
     * @return
     * @throws Throwable
     */
    Object lbnewsPut(Long id, String dir, String term, String cname) throws Throwable;
    Object lbnoticePut(Long id, String dir, String term, String cname) throws Throwable;
    Object lbschedulePut(Long id, String dir, String term, String cname) throws Throwable;
    Object lbvideoPut(Long id, String dir, String term, String cname) throws Throwable;

    /**
     * 这是对理论课四类资源的统一查询，登录页面课程为null。管理员页面课程为''，教师页面有具体的课程
     * @param curpage
     * @param term
     * @param cname
     * @param fileType 四类资源的区分
     * @param pageNumber
     * @return
     */
    Object getFileByCnamePage(Integer curpage, String term, String cname, String fileType, int pageNumber);

    void insertOneLb(MultipartFile file, String name, String summary, String dest, String term, String cname, String fileType) throws Throwable;

    Lbview lbviewPut(Long id, String dir, String term, String cname) throws Throwable;

    Object getNewsFirst() throws Throwable;

    Lbnews getLbewsById(int i);

    Lbnotice getLbnoticeById(int i);

    Lbvideo getLbvideoById(int i);

    List<Lbvideo> getUDLbvideoById(int i);

    List<Lbnews> getUDLbnewsById(int i);

    List<Lbnotice> getUDLbnoticeById(int i);

    Lbschedule getLbscheduleById(int i);

    List<Lbschedule> getUDLbscheduleById(int i);

    Object getScheduleCountByCnamePage(Integer curpage, String term, String cname, String lbschedule, int pageNumber);
}

package per.cjh.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import per.cjh.example.domain.*;
import per.cjh.example.mappers.*;
import per.cjh.example.service.FileService;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cjh
 * @description: TODO
 * @date 2020/5/4 22:18
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {


    @Resource
    private ExScheduleMapper scheduleMapper;
    @Resource
    private ExVideoMapper videoMapper;
    @Resource
    private ExNewsMapper newsMapper;

    @Resource
    private LbvideoMapper lbvideoMapper;
    @Resource
    private LbviewMapper lbviewMapper;
    @Resource
    private LbscheduleMapper lbscheduleMapper;
    @Resource
    private LbnewsMapper lbnewsMapper;
    @Resource
    private LbnoticeMapper lbnoticeMapper;

    private ExVideo video = new ExVideo();
    private ExSchedule schedule = new ExSchedule();
    private ExNews news = new ExNews();
    private Lbvideo lbvideo = new Lbvideo();
    private Lbview lbview = new Lbview();
    private Lbschedule lbschedule = new Lbschedule();
    private Lbnews lbnews = new Lbnews();
    private Lbnotice lbnotice = new Lbnotice();

    private String fileName;
    private String fileSize;


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Object insertOne(MultipartFile file, String fileType, String name, String summary, String dest, String category) throws Throwable {
        LocalDate localDate = LocalDate.now();
        fileName = file.getOriginalFilename();
        if (file.getSize() >= 1024) {
            fileSize = file.getSize() / 1024 + "KB";
        } else {
            fileSize = file.getSize() + "B";
        }
        if ("video".equals(fileType)) {
            // 上传视频表
            video.setAuthor(name).
                    setId(null).
                    setPath(dest).
                    setSize(fileSize).
                    setCategory(category).
                    setSummary(summary).
                    setUptime(localDate).
                    setVname(fileName.substring(0, fileName.lastIndexOf(".")));
            // 理工科视频各最多二十个，超出则覆盖最旧的
            Example example = new Example(ExVideo.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("category", category);
            int deleteNumber = videoMapper.selectCountByExample(example) - 20;
            example.orderBy("id").desc();
            List<ExVideo> exVideos = videoMapper.selectByExample(example);
            int videoNumber = exVideos.size();
            for (int i = 19; i < videoNumber; i++) {
                videoMapper.deleteByPrimaryKey(exVideos.get(i));
            }
            videoMapper.insert(video);
            return video;
        } else {
            // 上传进度表
            schedule.setAuthor(name).
                    setId(null).
                    setPath(dest).
                    setSize(fileSize).
                    setUptime(localDate).
                    setCategory(category).
                    setSname(fileName.substring(0, fileName.lastIndexOf(".")));
            // 理工科进度表各最多二十个，超出则覆盖最旧的
            Example example = new Example(ExSchedule.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("category", category);

            int deleteNumber = scheduleMapper.selectCountByExample(example) - 20;
            example.orderBy("id").desc();
            List<ExSchedule> exSchedules = scheduleMapper.selectByExample(example);
            int scheduleNumber = exSchedules.size();
            for (int i = 19; i < scheduleNumber; i++) {
                scheduleMapper.deleteByPrimaryKey(exSchedules.get(i));
            }
            scheduleMapper.insert(schedule);
            return schedule;
        }
    }

    @Override
    public void insertOneLb(MultipartFile file, String name, String summary, String dest, String term, String cname, String fileType) throws Throwable {
        log.info(term, cname);
        LocalDate localDate = LocalDate.now();
        fileName = file.getOriginalFilename();
        if (file.getSize() >= 1024) {
            fileSize = file.getSize() / 1024 + "KB";
        } else {
            fileSize = file.getSize() + "B";
        }
        // 如果标题相同就是修改，否则就是新加。视频的标题是指视频简介
        if ("lbvideo".equals(fileType)) {
            // 查找是否已经存在该视频简介
            Example example1 = new Example(Lbvideo.class);
            Example.Criteria criteria1 = example1.createCriteria();

            criteria1.andEqualTo("summary", summary)
                    .andEqualTo("term", term)
                    .andEqualTo("cname", cname);
            int changeNumber = lbvideoMapper.updateByExampleSelective(new Lbvideo().setPath(dest), example1);
            if (changeNumber != 0) {
                return;
            }
            // 上传视频表
            lbvideo.setAuthor(name).
                    setCname(cname).
                    setTerm(term).
                    setId(null).
                    setPath(dest).
                    setSummary(summary).
                    setUptime(localDate).
                    setVname(fileName.substring(0, fileName.lastIndexOf(".")));
            // 需要获取所属资源类别（属于哪一门课、公共资源等）的最后一条记录的level
            Example example = new Example(Lbvideo.class);
            example.orderBy("level");
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("term", lbvideo.getTerm())
                    .andEqualTo("cname", lbvideo.getCname());

            List<Lbvideo> lbvideos = lbvideoMapper.selectByExample(example);
            // 删除超过20个的部分，等级数字越大，等级越高
            int deleteSurplus = lbvideos.size()-20;
            for (int i = 0; i <= deleteSurplus; i++) {
                lbvideoMapper.deleteByPrimaryKey(lbvideos.get(i));
            }
            int level = lbvideos.isEmpty() ? 0 : lbvideos.get(lbvideos.size()-1).getLevel();
            // 设置当前的 level，并插入记录
            lbvideo.setLevel(level + 1);
            lbvideo.setUptime(LocalDate.now());
            lbvideoMapper.insertSelective(lbvideo);
        } else if ("lbschedule".equals(fileType)) {

            String[] fileDestAndImgsDest = dest.split("\\?");
            // 查找是否已经存在该资料文件名
//            Example example1 = new Example(Lbvideo.class);
//            Example.Criteria criteria1 = example1.createCriteria();
//            criteria1.andEqualTo("sname", fileName.substring(0, fileName.lastIndexOf(".")))
//                    .andEqualTo("term", term)
//                    .andEqualTo("cname", cname);
//            Lbschedule lbschedule1 = new Lbschedule();
//            // 修改文件地址和图片地址
//            lbschedule1.setPath(fileDestAndImgsDest[0]).setSize(fileDestAndImgsDest[1]);
//            int changeNumber = lbscheduleMapper.updateByExampleSelective(lbschedule1, example1);
//            if (changeNumber != 0) {
//                return;
//            }
            // 上传资料
            this.lbschedule.setAuthor(name).
                    setCname(cname).
                    setTerm(term).
                    setId(null).
                    setPath(fileDestAndImgsDest[0]).
                    setUptime(localDate).
                    setSname(fileName.substring(0, fileName.lastIndexOf(".")));
            if (fileDestAndImgsDest.length == 1) {
                // 无需转换图片的文件
                lbschedule.setSize("");
            } else {
                lbschedule.setSize(fileDestAndImgsDest[1]);
            }
            // 需要获取所属资源类别（属于哪一门课、公共资源等）的最后一条记录的level
            Example example = new Example(Lbschedule.class);
            example.orderBy("level");
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("term", this.lbschedule.getTerm())
                    .andEqualTo("cname", this.lbschedule.getCname());

            List<Lbschedule> lbschedules = lbscheduleMapper.selectByExample(example);
            // 删除超过20个的部分，等级数字越大，等级越高
            int deleteSurplus = lbschedules.size()-20;
            for (int i = 0; i <= deleteSurplus; i++) {
                lbscheduleMapper.deleteByPrimaryKey(lbschedules.get(i));
            }
            int level = lbschedules.isEmpty() ? 0 : lbschedules.get(lbschedules.size()-1).getLevel();
            // 设置当前的 level，并插入记录
            lbschedule.setLevel(level + 1);
            lbschedule.setUptime(LocalDate.now());
            lbscheduleMapper.insertSelective(lbschedule);
        } else if ("lbview".equals(fileType)) {
            // 上传风景
            lbview.setAuthor(name).
                    setCname(cname).
                    setTerm(term).
                    setId(null).
                    setPath(dest).
                    setSummary(summary).
                    setUptime(localDate).
                    setVname(fileName.substring(0, fileName.lastIndexOf(".")));

            // 需要获取所属资源类别（属于哪一门课、公共资源等）的最后一条记录的level
            Example example = new Example(Lbview.class);
            example.orderBy("level");
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("term", lbview.getTerm())
                    .andEqualTo("cname", lbview.getCname());

            List<Lbview> lbviews = lbviewMapper.selectByExample(example);
            // 删除超过20个的部分，等级数字越大，等L级越高
            int deleteSurplus = lbviews.size()-10;
            for (int i = 0; i <= deleteSurplus; i++) {
                lbviewMapper.deleteByPrimaryKey(lbviews.get(i));
            }
            int level = lbviews.isEmpty() ? 0 : lbviews.get(lbviews.size()-1).getLevel();
            // 设置当前的 level，并插入记录
            lbview.setLevel(level + 1);
            lbview.setUptime(LocalDate.now());
            lbviewMapper.insertSelective(lbview);
        }
    }

    @Override
    public void delete(String fileType, Integer id) throws Throwable {
        log.info("删除资源为：" + fileType + " 资源id为：" + id);
        if ("video".equals(fileType)) {
            videoMapper.deleteByPrimaryKey(id);
        } else if ("schedule".equals(fileType)) {
            scheduleMapper.deleteByPrimaryKey(id);
        } else if ("news".equals(fileType)) {
            newsMapper.deleteByPrimaryKey(id);
        } else if ("lbnews".equals(fileType)) {
            lbnewsMapper.deleteByPrimaryKey(id);
        } else if ("lbschedule".equals(fileType)) {
            lbscheduleMapper.deleteByPrimaryKey(id);
        } else if ("lbvideo".equals(fileType)) {
            lbvideoMapper.deleteByPrimaryKey(id);
        } else if ("lbnotice".equals(fileType)) {
            lbnoticeMapper.deleteByPrimaryKey(id);
        } else if ("lbview".equals(fileType)) {
            lbviewMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public Object getVideoByPage(String category) {
        // 理论课获取公共资源无需上传category参数
        if (category != null) {
            Example example = new Example(ExVideo.class);
            example.orderBy("uptime").desc();
            // 实验课登录页面显示工科，学生主页显示理科
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("category", category);
            List<ExVideo> exVideos = videoMapper.selectByExample(example);
            return exVideos;
        } else {
            Example example = new Example(Lbvideo.class);
            example.orderBy("level").asc();
            return lbvideoMapper.selectByExample(example);
        }
    }

    @Override
    public Object getVideoByPage(Integer curpage, String term, String cname) {
        RowBounds rowBounds = new RowBounds((curpage - 1) * 20, 20);
        if (term == null) {
            // 学期、课程为null，则为实验课
            Example example = new Example(ExVideo.class);
            example.orderBy("uptime").desc();
            return videoMapper.selectByExampleAndRowBounds(example, rowBounds);
        } else {
            // 理论课，除了按页码查询还要按学期、课程查询
            Example example = new Example(Lbvideo.class);
            example.orderBy("level").asc();
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("term", term)
                    .andEqualTo("cname", cname);
            return lbvideoMapper.selectByExampleAndRowBounds(example, rowBounds);
        }
    }

    @Override
    public List<ExNews> getNewsByPage(Integer curpage, String type) {
        Example example = new Example(ExNews.class);
        example.orderBy("uptime").desc();
        Example.Criteria criteria = example.createCriteria();
        int pageNumber = 5;
        if ("新闻详情页面".equals(type)) {
            pageNumber = 20;
        } else if ("登录页面".equals(type)) {
            criteria.andEqualTo("category", "工科");
        } else {
            criteria.andEqualTo("category", "理科");
        }
        RowBounds rowBounds = new RowBounds((curpage - 1) * pageNumber, pageNumber);
        return newsMapper.selectByExampleAndRowBounds(example, rowBounds);
    }

    @Override
    public ExNews insertOneNews(ExNews exNews) throws Throwable {

        // 理工科新闻分别最多二十个，超出则覆盖最旧的。
        Example example = new Example(ExNews.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("category", exNews.getCategory());

        int deleteNumber = newsMapper.selectCountByExample(example) - 20;
        example.orderBy("id").desc();
        List<ExNews> News = newsMapper.selectByExample(example);
        int newsNumber = News.size();
        for (int i = 19; i < newsNumber; i++) {
            newsMapper.deleteByPrimaryKey(News.get(i));
        }
        newsMapper.insert(exNews);
        return exNews;
    }

    @Override
    public ExNews getNewsById(int id) {
        // 查询该条新闻，并让观看数+1
        ExNews news = newsMapper.selectByPrimaryKey(id);
        news.setViewnum(news.getViewnum() + 1);
        newsMapper.updateByPrimaryKeySelective(news);
        return news;
    }

    @Override
    public Lbnews getLbewsById(int id) {
        Lbnews news = lbnewsMapper.selectByPrimaryKey(id);
        return news;
    }

    @Override
    public Lbnotice getLbnoticeById(int id) {
        Lbnotice notice = lbnoticeMapper.selectByPrimaryKey(id);
        return notice;
    }

    @Override
    public Lbvideo getLbvideoById(int id) {
        Lbvideo lbvideo = lbvideoMapper.selectByPrimaryKey(id);
        return lbvideo;
    }

    @Override
    public Lbschedule getLbscheduleById(int id) {
        return lbscheduleMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Lbvideo> getUDLbvideoById(int id) {
        List<Lbvideo> retUDLbvide = new ArrayList<>();
        Example example = new Example(Lbvideo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", id);
        // 当前正在查看的资源
        Lbvideo nowLbvideo = lbvideoMapper.selectByExample(example).get(0);
        Example example1 = new Example(Lbvideo.class);
        Example.Criteria criteria1 = example1.createCriteria();
        example1.orderBy("level").asc();
        criteria1.andEqualTo("term", nowLbvideo.getTerm())
                .andEqualTo("cname", nowLbvideo.getCname());
        List<Lbvideo> lbvideos = lbvideoMapper.selectByExample(example1);

        for (int i = 0; i < lbvideos.size(); i++) {
            if (lbvideos.get(i).getId() == id) {
                // 获取当前资源的上一个资源，如果没有则以内容Summary为空""，来标记
                if (i == 0) {
                    Lbvideo ulbvideo = new Lbvideo();
                    ulbvideo.setSummary("");
                    retUDLbvide.add(ulbvideo);
                } else {
                    retUDLbvide.add(lbvideos.get(i - 1));
                }
                // 获取当前资源的下一个资源
                if (i == lbvideos.size() - 1) {
                    Lbvideo dlbvideo = new Lbvideo();
                    dlbvideo.setAuthor("");
                    retUDLbvide.add(dlbvideo);
                } else {
                    retUDLbvide.add(lbvideos.get(i + 1));
                }
            }
        }
        return retUDLbvide;
    }

    @Override
    public List<Lbnews> getUDLbnewsById(int id) {
        List<Lbnews> retUDLbvide = new ArrayList<>();
        Example example = new Example(Lbnews.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", id);
        // 当前正在查看的资源
        Lbnews nowLbnews = lbnewsMapper.selectByExample(example).get(0);
        Example example1 = new Example(Lbnews.class);
        example1.orderBy("level").asc();
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("term", nowLbnews.getTerm())
                .andEqualTo("cname", nowLbnews.getCname());
        List<Lbnews> lbnewss = lbnewsMapper.selectByExample(example1);

        for (int i = 0; i < lbnewss.size(); i++) {
            if (lbnewss.get(i).getId() == id) {
                // 获取当前资源的上一个资源，如果没有则以内容Summary为空""，来标记
                if (i == 0) {
                    Lbnews ulbnews = new Lbnews();
                    ulbnews.setSummary("");
                    retUDLbvide.add(ulbnews);
                } else {
                    retUDLbvide.add(lbnewss.get(i - 1));
                }
                // 获取当前资源的下一个资源
                if (i == lbnewss.size() - 1) {
                    Lbnews dlbnews = new Lbnews();
                    dlbnews.setSummary("");
                    retUDLbvide.add(dlbnews);
                } else {
                    retUDLbvide.add(lbnewss.get(i + 1));
                }
            }
        }
        return retUDLbvide;
    }

    @Override
    public List<Lbnotice> getUDLbnoticeById(int id) {
        List<Lbnotice> retUDLbvide = new ArrayList<>();
        Example example = new Example(Lbnotice.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", id);
        // 当前正在查看的资源
        Lbnotice nowLbnotice = lbnoticeMapper.selectByExample(example).get(0);
        Example example1 = new Example(Lbnotice.class);
        Example.Criteria criteria1 = example1.createCriteria();
        example1.orderBy("level").asc();
        criteria1.andEqualTo("term", nowLbnotice.getTerm())
                .andEqualTo("cname", nowLbnotice.getCname());
        List<Lbnotice> Lbnotices = lbnoticeMapper.selectByExample(example1);

        for (int i = 0; i < Lbnotices.size(); i++) {
            if (Lbnotices.get(i).getId() == id) {
                // 获取当前资源的上一个资源，如果没有则以内容Summary为空""，来标记
                if (i == 0) {
                    Lbnotice ulbnotice = new Lbnotice();
                    ulbnotice.setSummary("");
                    retUDLbvide.add(ulbnotice);
                } else {
                    retUDLbvide.add(Lbnotices.get(i - 1));
                }
                // 获取当前资源的下一个资源
                if (i == Lbnotices.size() - 1) {
                    Lbnotice dlbnotice = new Lbnotice();
                    dlbnotice.setSummary("");
                    retUDLbvide.add(dlbnotice);
                } else {
                    retUDLbvide.add(Lbnotices.get(i + 1));
                }
            }
        }
        return retUDLbvide;
    }

    @Override
    public List<Lbschedule> getUDLbscheduleById(int id) {
        List<Lbschedule> retUDLbvide = new ArrayList<>();
        Example example = new Example(Lbschedule.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", id);
        // 当前正在查看的资源
        Lbschedule nowLbschedule = lbscheduleMapper.selectByExample(example).get(0);
        Example example1 = new Example(Lbschedule.class);
        Example.Criteria criteria1 = example1.createCriteria();
        example1.orderBy("level").asc();
        criteria1.andEqualTo("term", nowLbschedule.getTerm())
                .andEqualTo("cname", nowLbschedule.getCname());
        List<Lbschedule> Lbschedules = lbscheduleMapper.selectByExample(example1);

        for (int i = 0; i < Lbschedules.size(); i++) {
            if (Lbschedules.get(i).getId() == id) {
                // 获取当前资源的上一个资源，如果没有则以内容size为空""，来标记;size存放的是文件所对应的图片位置
                if (i == 0) {
                    Lbschedule ulbschedule = new Lbschedule();
                    ulbschedule.setSize("");
                    retUDLbvide.add(ulbschedule);
                } else {
                    retUDLbvide.add(Lbschedules.get(i - 1));
                }
                // 获取当前资源的下一个资源
                if (i == Lbschedules.size() - 1) {
                    Lbschedule dlbschedule = new Lbschedule();
                    dlbschedule.setSize("");
                    retUDLbvide.add(dlbschedule);
                } else {
                    retUDLbvide.add(Lbschedules.get(i + 1));
                }
            }
        }
        return retUDLbvide;
    }

    @Override
    public Object getScheduleCountByCnamePage(Integer curpage, String term, String cname, String lbschedule, int pageNumber) {
        // 计算该课程下的资料有多少页
        Example example = new Example(Lbschedule.class);
        Example.Criteria criteria = example.createCriteria();
        if (cname == null) {
            criteria.andEqualTo("cname", "");
        } else {
            criteria.andEqualTo("term", term).
                    andEqualTo("cname", cname);
        }
//        log.info(lbscheduleMapper.selectCountByExample(example) +"");
        return lbscheduleMapper.selectCountByExample(example) / pageNumber + 1;
    }

    @Override
    public Object getScheduleByPage(Integer curpage, String category) {
        Example example = new Example(ExSchedule.class);
        // 学生主页或者是登录页面。只显示理科或者工科资源
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("category", category);
        example.orderBy("uptime").desc();
        int pageNumber = 13;
        RowBounds rowBounds = new RowBounds((curpage - 1) * pageNumber, pageNumber);
        return scheduleMapper.selectByExampleAndRowBounds(example, rowBounds);
    }

    @Override
    public Object getScheduleByPage(Integer curpage, String term, String cname) {

        // 理论课按照学期、课程查询视频。管理员课程为空
        Example example = new Example(Lbschedule.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("term", term)
                .andEqualTo("cname", cname);
        example.orderBy("level").asc();
        RowBounds rowBounds = new RowBounds((curpage - 1) * 20, 20);
        return lbscheduleMapper.selectByExampleAndRowBounds(example, rowBounds);
    }

    @Override
    public Object getFileByCnamePage(Integer curpage, String term, String cname, String fileType, int pageNumber) {
        Example example = null;
        if ("lbnews".equals(fileType)) {
            // 登录页面不上传课程cname。每页只显示4个公共新闻，所有公共视频，13个公共进度表，5个公共通知
            example = new Example(Lbnews.class);
        } else if ("lbvideo".equals(fileType)) {
            example = new Example(Lbvideo.class);
        } else if ("lbnotice".equals(fileType)) {
            example = new Example(Lbvideo.class);
        } else if ("lbschedule".equals(fileType)) {
            example = new Example(Lbschedule.class);
        } else if ("lbview".equals(fileType)) {
            example = new Example(Lbview.class);
        }
        example.orderBy("level").desc();

        Example.Criteria criteria = example.createCriteria();
        // 主页的视频查看栏，不用上传视频。故自定义为第一页
        curpage = curpage == null ? 1 : curpage;
        RowBounds rowBounds = new RowBounds((curpage - 1) * pageNumber, pageNumber);
        if (cname == null) {
            // 每一页显示的都是头条新闻
            if ("lbnews".equals(fileType)) {
                rowBounds = new RowBounds((curpage - 1) * pageNumber + 1, pageNumber);
            }
            criteria.andEqualTo("cname", "");
            criteria.andEqualTo("term", "");
        } else {
            criteria.andEqualTo("term", term)
                    .andEqualTo("cname", cname);
        }
        // 分四类资源开始查询
        if ("lbnews".equals(fileType)) {
            return lbnewsMapper.selectByExampleAndRowBounds(example, rowBounds);
        } else if ("lbvideo".equals(fileType)) {
            return lbvideoMapper.selectByExampleAndRowBounds(example, rowBounds);
        } else if ("lbnotice".equals(fileType)) {
            return lbnoticeMapper.selectByExampleAndRowBounds(example, rowBounds);
        } else if ("lbschedule".equals(fileType)) {
            return lbscheduleMapper.selectByExampleAndRowBounds(example, rowBounds);
        } else if ("lbview".equals(fileType)) {
            return lbviewMapper.selectByExampleAndRowBounds(example, rowBounds);
        }
        return null;
    }

    @Override
    public Object getNewsFirst() throws Throwable {
        Example example = new Example(Lbnews.class);
        RowBounds rowBounds = new RowBounds(0, 1);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("term", "")
                .andEqualTo("cname", "");
        example.orderBy("level").desc();
        return lbnewsMapper.selectByExampleAndRowBounds(example, rowBounds).get(0);
    }

    @Override
    public Object insertOneLbnews(Lbnews news) throws Throwable {

        // 查找是否已经存在该新闻标题
        Example example1 = new Example(Lbnews.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("title", news.getTitle())
                .andEqualTo("term", news.getTerm())
                .andEqualTo("cname", news.getCname());
        int changeNumber = lbnewsMapper.updateByExampleSelective(news, example1);

        if (changeNumber != 0) {
            return null;
        }
        // 需要获取所属资源类别（属于哪一门课、公共资源等）的最后一条记录的level
        Example example = new Example(Lbnews.class);
        example.orderBy("level");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("term", news.getTerm())
                .andEqualTo("cname", news.getCname());

        List<Lbnews> lbnews = lbnewsMapper.selectByExample(example);
        // 删除超过20个的部分，等级数字越大，等级越高
        int deleteSurplus = lbnews.size()-20;
        for (int i = 0; i <= deleteSurplus; i++) {
            lbnewsMapper.deleteByPrimaryKey(lbnews.get(i));
        }
        int level = lbnews.isEmpty() ? 0 : lbnews.get(lbnews.size()-1).getLevel();
        // 设置当前的 level，并插入记录
        news.setLevel(level + 1);
        news.setUptime(LocalDate.now());
        lbnewsMapper.insertSelective(news);
        return news;
    }

    @Override
    public Object insertOneLbnotice(Lbnotice notice) throws Throwable {
        // 查找是否已经存在该新闻标题
        Example example1 = new Example(Lbnews.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("title", notice.getTitle())
                .andEqualTo("term", notice.getTerm())
                .andEqualTo("cname", notice.getCname());
        int changeNumber = lbnoticeMapper.updateByExampleSelective(notice, example1);
        if (changeNumber != 0) {
            return null;
        }
        // 需要获取所属资源类别（属于哪一门课、公共资源等）的最后一条记录的level
        Example example = new Example(Lbnotice.class);
        example.orderBy("level");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("term", notice.getTerm())
                .andEqualTo("cname", notice.getCname());

        List<Lbnotice> lbnotices = lbnoticeMapper.selectByExample(example);
        // 删除超过20个的部分，等级数字越大，等级越高
        int deleteSurplus = lbnotices.size()-20;
        for (int i = 0; i <= deleteSurplus; i++) {
            lbnoticeMapper.deleteByPrimaryKey(lbnotices.get(i));
        }
        log.info(lbnotices.toString());
        int level = lbnotices.isEmpty() ? 0 : lbnotices.get(lbnotices.size()-1).getLevel();
        // 设置当前的 level，并插入记录
        notice.setLevel(level + 1);
        notice.setUptime(LocalDate.now());
        lbnoticeMapper.insertSelective(notice);
        return notice;
    }

    @Override
    public Object lbnewsPut(Long id, String dir, String term, String cname) throws Throwable {
        // 需要获取该学期，该课程下的所有新闻。然后找到当前新闻id的level和他的上下level
        Example example = new Example(Lbnews.class);
        example.orderBy("level").asc();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("cname", cname)
                .andEqualTo("term", term);
        List<Lbnews> lbnews = lbnewsMapper.selectByExample(example);
        // 这两条记录的级别，和另外一条记录的id
        Integer level1, level2;
        Lbnews file1 = null, file2 = null;

        for (int i = 0; i < lbnews.size(); i++) {
            file1 = lbnews.get(i);
            if (file1.getId().equals(id)) {
                level1 = file1.getLevel();
                // 获取被动移位的资源
                if (dir.indexOf("u") != -1) {
                    // id 上移
                    if (i == 0) {
                        throw new Throwable("当前是第一个资源，不可再上移");
                    }
                    file2 = lbnews.get(i - 1);
                } else {
                    // id 下移
                    if (i == lbnews.size() - 1) {
                        throw new Throwable("当前是最后一个资源，不可再下移");
                    }
                    file2 = lbnews.get(i + 1);
                }
                level2 = file2.getLevel();
                file2.setLevel(level1);
                file1.setLevel(level2);
                break;
            }
        }
        example.clear();
        lbnewsMapper.updateByPrimaryKey(file1);
        lbnewsMapper.updateByPrimaryKey(file2);
        return file1;
    }

    @Override
    public Object lbschedulePut(Long id, String dir, String term, String cname) throws Throwable {
        // 需要获取该学期，该课程下的所有新闻。然后找到当前新闻id的level和他的上下level
        Example example = new Example(Lbschedule.class);
        example.orderBy("level").asc();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("cname", cname)
                .andEqualTo("term", term);
        List<Lbschedule> lbschedules = lbscheduleMapper.selectByExample(example);
        // 这两条记录的级别，和另外一条记录的id
        Integer level1, level2;
        Lbschedule file1 = null, file2 = null;

        for (int i = 0; i < lbschedules.size(); i++) {
            file1 = lbschedules.get(i);
            if (file1.getId().equals(id)) {
                level1 = file1.getLevel();
                // 获取被动移位的资源
                if (dir.indexOf("u") != -1) {
                    // id 上移
                    if (i == 0) {
                        throw new Throwable("当前是第一个资源，不可再上移");
                    }
                    file2 = lbschedules.get(i - 1);
                } else {
                    // id 下移
                    if (i == lbschedules.size() - 1) {
                        throw new Throwable("当前是最后一个资源，不可再下移");
                    }
                    file2 = lbschedules.get(i + 1);
                }
                level2 = file2.getLevel();
                file2.setLevel(level1);
                file1.setLevel(level2);
                break;
            }
        }
        example.clear();
        lbscheduleMapper.updateByPrimaryKey(file1);
        lbscheduleMapper.updateByPrimaryKey(file2);
        return file1;
    }


    @Override
    public Object lbnoticePut(Long id, String dir, String term, String cname) throws Throwable {
        // 需要获取该学期，该课程下的所有新闻。然后找到当前新闻id的level和他的上下level
        Example example = new Example(Lbnotice.class);
        example.orderBy("level").asc();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("cname", cname)
                .andEqualTo("term", term);
        List<Lbnotice> lbnotices = lbnoticeMapper.selectByExample(example);
        // 这两条记录的级别，和另外一条记录的id
        Integer level1, level2;
        Lbnotice file1 = null, file2 = null;

        for (int i = 0; i < lbnotices.size(); i++) {
            file1 = lbnotices.get(i);
            if (file1.getId().equals(id)) {
                level1 = file1.getLevel();
                // 获取被动移位的资源
                if (dir.indexOf("u") != -1) {
                    // id 上移
                    if (i == 0) {
                        throw new Throwable("当前是第一个资源，不可再上移");
                    }
                    file2 = lbnotices.get(i - 1);
                } else {
                    // id 下移
                    if (i == lbnotices.size() - 1) {
                        throw new Throwable("当前是最后一个资源，不可再下移");
                    }
                    file2 = lbnotices.get(i + 1);
                }
                level2 = file2.getLevel();
                file2.setLevel(level1);
                file1.setLevel(level2);
                break;
            }
        }
        example.clear();
        lbnoticeMapper.updateByPrimaryKey(file1);
        lbnoticeMapper.updateByPrimaryKey(file2);
        return file1;
    }

    @Override
    public Object lbvideoPut(Long id, String dir, String term, String cname) throws Throwable {
        // 需要获取该学期，该课程下的所有新闻。然后找到当前新闻id的level和他的上下level
        Example example = new Example(Lbvideo.class);
        example.orderBy("level").asc();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("cname", cname)
                .andEqualTo("term", term);
        List<Lbvideo> lbvideos = lbvideoMapper.selectByExample(example);
        log.info(term + " " + cname + " " + lbvideos);
        // 这两条记录的级别，和另外一条记录的id
        Integer level1, level2;
        Lbvideo file1 = null, file2 = null;

        for (int i = 0; i < lbvideos.size(); i++) {
            file1 = lbvideos.get(i);
            if (file1.getId().equals(id)) {
                level1 = file1.getLevel();
                // 获取被动移位的资源
                if (dir.indexOf("u") != -1) {
                    // id 上移
                    if (i == 0) {
                        throw new Throwable("当前是第一个资源，不可再上移");
                    }
                    file2 = lbvideos.get(i - 1);
                } else {
                    // id 下移
                    if (i == lbvideos.size() - 1) {
                        throw new Throwable("当前是最后一个资源，不可再下移");
                    }
                    file2 = lbvideos.get(i + 1);
                }
                level2 = file2.getLevel();
                file2.setLevel(level1);
                file1.setLevel(level2);
                break;
            }
        }
        example.clear();
        lbvideoMapper.updateByPrimaryKey(file1);
        lbvideoMapper.updateByPrimaryKey(file2);
        return file1;
    }

    @Override
    public Lbview lbviewPut(Long id, String dir, String term, String cname) throws Throwable {
        // 需要获取该学期，该课程下的所有新闻。然后找到当前新闻id的level和他的上下level
        Example example = new Example(Lbview.class);
        example.orderBy("level").asc();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("cname", cname)
                .andEqualTo("term", term);
        List<Lbview> lbviews = lbviewMapper.selectByExample(example);
        log.info(term + " " + cname + " " + lbviews);
        // 这两条记录的级别，和另外一条记录的id
        Integer level1, level2;
        Lbview file1 = null, file2 = null;

        for (int i = 0; i < lbviews.size(); i++) {
            file1 = lbviews.get(i);
            if (file1.getId().equals(id)) {
                level1 = file1.getLevel();
                // 获取被动移位的资源
                if (dir.indexOf("u") != -1) {
                    // id 上移
                    if (i == 0) {
                        throw new Throwable("当前是第一个资源，不可再上移");
                    }
                    file2 = lbviews.get(i - 1);
                } else {
                    // id 下移
                    if (i == lbviews.size() - 1) {
                        throw new Throwable("当前是最后一个资源，不可再下移");
                    }
                    file2 = lbviews.get(i + 1);
                }
                level2 = file2.getLevel();
                file2.setLevel(level1);
                file1.setLevel(level2);
                break;
            }
        }
        example.clear();
        lbviewMapper.updateByPrimaryKey(file1);
        lbviewMapper.updateByPrimaryKey(file2);
        return file1;
    }
}

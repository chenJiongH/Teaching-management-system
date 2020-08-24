package per.cjh.example.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import per.cjh.example.domain.*;
import per.cjh.example.service.FileService;
import per.cjh.example.utils.FileUtil;
import per.cjh.example.utils.TransformOffice2Image;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.temporal.ValueRange;
import java.util.List;

/**
 * @author cjh
 * @description: 用于进度表、视频、新闻的上传 和进度表的下载
 *              /video/get -展示视频、/video/post -上传视频、/video/delete -删除视频
 *              /schedule/get -展示进度表、/schedule/post -上传进度表、/schedule/delete -删除进度表
 *              /news/page -展示新闻、/news/post -上传新闻、/news/delete -删除新闻
 *              /news/id 点击新闻，发送新闻id绑定到该账户。
 *              /news 然后页面跳转到新闻详情页面，发送请求，获取该账号之前绑定的新闻详情
 *              /schedule/download -下载进度表
 * @date 2020/5/4 18:11
 */
@Api(tags = "多媒体管理的相关 API")
@RestController
@Slf4j
@RequestMapping()
public class FileController {

    @Autowired
    private FileService fileService;
    @Autowired
    private FileUtil fileUtil;
    @Autowired
    private TransformOffice2Image office2Image;
    @Value("${file.save.location}")
    private String runClassPath;

    private String name;

    @ApiOperation("3实验课按照工理查询所有视频， 理论课查询公共视频")
    @GetMapping(value = "/video")
    public Object videoGet(String category) {
        return fileService.getVideoByPage(category);
    }

    @ApiOperation("2理论课: 根据学期、课程分页查询视频，登录页面上传的cname为null，管理员页面上传的cname为空字符串''，有助于判断分页数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "term", value = "学期"),
            @ApiImplicitParam(name = "cname", value = "课程"),
            @ApiImplicitParam(name = "curpage", value = "页码")
    })
    @GetMapping("/lbvideo")
    public Object lbvideoCname(String term, String cname, Integer curpage, int pageNumber) {
        return fileService.getFileByCnamePage(curpage, term, cname, "lbvideo", pageNumber);
    }
    @GetMapping("/lbview")
    public Object lbviewCname(String term, String cname, Integer curpage, int pageNumber) {
        return fileService.getFileByCnamePage(curpage, term, cname, "lbview", pageNumber);
    }

    @ApiOperation("3分页查询视频，理论课新增参数:学期、课程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curpage", value = "页码"),
            @ApiImplicitParam(name = "term", value = "新增学期"),
            @ApiImplicitParam(name = "cname", value = "新增课程")
    })
    @GetMapping(value = "/video/page")
    public Object videoPage(Integer curpage, String term, String cname) {
        return fileService.getVideoByPage(curpage, term, cname);
    }

    @ApiOperation("1实验课：页码和理工分类查询进度表列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "category", value = "类型（工科查工科，理科查全部"),
            @ApiImplicitParam(name = "curpage", value = "页码")
    })
    @GetMapping("/schedule")
    public Object scheduleGet(String category, Integer curpage) {
        return fileService.getScheduleByPage(curpage, category);
    }

    @ApiOperation("2理论课：查询进度表")
    @GetMapping("/lbschedule")
    public Object lbscheduleGet(String term, String cname, Integer curpage, int pageNumber) {
        return fileService.getFileByCnamePage(curpage, term, cname, "lbschedule", pageNumber);
    }
    @ApiOperation("2理论课：查看最多有多少页")
    @GetMapping("/lbschedule/count")
    public Object lbscheduleCountGet(String term, String cname, Integer curpage, int pageNumber) {
        return fileService.getScheduleCountByCnamePage(curpage, term, cname, "lbschedule", pageNumber);
    }

    @ApiOperation("2理论课查询通知")
    @GetMapping("/lbnotice")
    public Object lbnoticeGet(String term, String cname, Integer curpage, int pageNumber) {
        return fileService.getFileByCnamePage(curpage, term, cname, "lbnotice", pageNumber);
    }

    @ApiOperation("1页码查询新闻列表")
    @ApiImplicitParam(name = "curpage", value = "页码")
    @ApiResponse(code = 200, message = "成功返回实体类 ExNews 数组")
    @GetMapping("/news/page")
    public List<ExNews> newsGetPage(Integer curpage, String type) {
        // type表示登录页面（看5个工科）还是新闻详情页面查询（看20个），或者是学生页面（看5个）
        return fileService.getNewsByPage(curpage, type);
    }

    @ApiOperation("2理论课: 根据学期、课程分页查询新闻，登录页面上传的cname为null，有助于判断分页数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "term", value = "学期"),
            @ApiImplicitParam(name = "cname", value = "课程"),
            @ApiImplicitParam(name = "curpage", value = "页码")
    })
    @GetMapping("/lbnews")
    public Object lbnewsGet(String term, String cname, Integer curpage, int pageNumber) {
        return fileService.getFileByCnamePage(curpage, term, cname, "lbnews", pageNumber);
    }
    @GetMapping("/lbnews/first")
    public Object lbnewsGetFirst() {
        try {
            return fileService.getNewsFirst();
        } catch (Throwable throwable) {
            return "没有新闻，请上传";
        }
    }

    @ApiOperation("为该账号绑定所点击的新闻id")
    @ApiImplicitParam(name = "id", value = "被点击新闻的 id")
    @GetMapping("/news/id")
    public String newsGetId(@Param("id") Integer id, @ApiIgnore HttpSession session) {
        session.setAttribute("newsId", String.valueOf(id));
        return "绑定成功";
    }
    @GetMapping("/lbnews/id")
    public String lbnewsGetId(@Param("id") Integer id, @ApiIgnore HttpSession session) {
        session.setAttribute("lbnewsId", String.valueOf(id));
        return "绑定成功";
    }
    @GetMapping("/lbschedule/id")
    public String lbscheduleGetId(@Param("id") Integer id, @ApiIgnore HttpSession session) {
        session.setAttribute("lbscheduleId", String.valueOf(id));
        return "绑定成功";
    }
    @GetMapping("/lbnotice/id")
    public String lbnoticeGetId(@Param("id") Integer id, @ApiIgnore HttpSession session) {
        session.setAttribute("lbnoticeId", String.valueOf(id));
        return "绑定成功";
    }
    @GetMapping("/lbvideo/id")
    public String lbvideoGetId(@Param("id") Integer id, @ApiIgnore HttpSession session) {
        session.setAttribute("lbvideoId", String.valueOf(id));
        return "绑定成功";
    }

    @ApiOperation("新闻详情页面发送获取新闻请求，不用带参数，因为新闻id参数的绑定已经在登录页面绑定过了")
    @ApiImplicitParam(name = "id", value = "被点击新闻的 id")
    @GetMapping("/news")
    public ExNews newsGet(@ApiIgnore HttpServletRequest request, @ApiIgnore HttpSession session) {
        String id = "";
        try {
            id = (String) session.getAttribute("newsId");
            String type = session.getAttribute("type") +"";
            ExNews newsById = fileService.getNewsById(Integer.parseInt(id));
            // 学生主页面的访问，右上角显示退出登录。
            // 登录页面的访问，右上角显示返回登录
            if (!"学生".equals(type)) {
                newsById.setCategory("工科");
            } else {
                newsById.setCategory("理科");
            }
            log.info(type);
            return newsById;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/lbnews/page")
    public Lbnews lbnewsGet(@ApiIgnore HttpServletRequest request, @ApiIgnore HttpSession session) {
        String id = "";
        try {
            id = (String) session.getAttribute("lbnewsId");
            String type = session.getAttribute("type") +"";
            Lbnews newsById = fileService.getLbewsById(Integer.parseInt(id));
            // 学生主页面的访问，右上角显示退出登录。
            // 登录页面的访问，右上角显示返回登录
            return newsById;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
    @GetMapping("/lbschedule/page")
    public Lbschedule lbscheduleGet(String curpage, @ApiIgnore HttpServletRequest request, @ApiIgnore HttpSession session) {
        String id = "";
        try {
            id = (String) session.getAttribute("lbscheduleId");
            String type = session.getAttribute("type") +"";

            return fileService.getLbscheduleById(Integer.parseInt(id));
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
    @GetMapping("/lbnotice/page")
    public Lbnotice lbnoticeGet(@ApiIgnore HttpServletRequest request, @ApiIgnore HttpSession session) {
        String id = "";
        try {
            id = (String) session.getAttribute("lbnoticeId");
            String type = session.getAttribute("type") +"";
            Lbnotice noticeById = fileService.getLbnoticeById(Integer.parseInt(id));
            // 学生主页面的访问，右上角显示退出登录。
            // 登录页面的访问，右上角显示返回登录
            return noticeById;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
    @GetMapping("/lbvideo/page")
    public Lbvideo lbvideoGet(@ApiIgnore HttpServletRequest request, @ApiIgnore HttpSession session) {
        String id = "";
        try {
            id = (String) session.getAttribute("lbvideoId");
            String type = session.getAttribute("type") +"";
            Lbvideo lbvideoById = fileService.getLbvideoById(Integer.parseInt(id));
            // 学生主页面的访问，右上角显示退出登录。
            // 登录页面的访问，右上角显示返回登录
            return lbvideoById;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
    @GetMapping("/lbvideo/getUD")
    public List<Lbvideo> lbvideoGetUD(@ApiIgnore HttpServletRequest request, @ApiIgnore HttpSession session) {
        String id = "";
        // 根据用户当前查看的资源ID，获取这个资源同学期同课程的上下类资源
        try {
            id = (String) session.getAttribute("lbvideoId");
            String type = session.getAttribute("type") +"";
            return fileService.getUDLbvideoById(Integer.parseInt(id));
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
    @GetMapping("/lbnews/getUD")
    public List<Lbnews> lbnewsGetUD(@ApiIgnore HttpServletRequest request, @ApiIgnore HttpSession session) {
        String id = "";
        // 根据用户当前查看的资源ID，获取这个资源同学期同课程的上下类资源
        try {
            id = (String) session.getAttribute("lbnewsId");
            String type = session.getAttribute("type") +"";
            return fileService.getUDLbnewsById(Integer.parseInt(id));
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
    @GetMapping("/lbschedule/getUD")
    public List<Lbschedule> lbscheduleGetUD(@ApiIgnore HttpServletRequest request, @ApiIgnore HttpSession session) {
        String id = "";
        // 根据用户当前查看的资源ID，获取这个资源同学期同课程的上下类资源
        try {
            id = (String) session.getAttribute("lbscheduleId");
            String type = session.getAttribute("type") +"";
            return fileService.getUDLbscheduleById(Integer.parseInt(id));
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
    @GetMapping("/lbnotice/getUD")
    public List<Lbnotice> lbnoticeGetUD(@ApiIgnore HttpServletRequest request, @ApiIgnore HttpSession session) {
        String id = "";
        // 根据用户当前查看的资源ID，获取这个资源同学期同课程的上下类资源
        try {
            id = (String) session.getAttribute("lbnoticeId");
            String type = session.getAttribute("type") +"";
            return fileService.getUDLbnoticeById(Integer.parseInt(id));
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    @ApiOperation(value = "上传视频文件", httpMethod = "POST", notes = "notes")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "summary", value = "视频简介", paramType = "query")
    })
    @ApiResponse(code = 200, message = "成功返回’上传成功‘，或者’上传失败‘")
    @PostMapping(value = "/video")
    public ExVideo  videoPost(@ApiParam(name = "file",value = "视频文件", required = true) MultipartFile file, String summary, String category, @ApiIgnore HttpSession session) {

        try {
            // 把文件保存到服务器上，并且返回服务器文件绝对路径
            String dest = fileUtil.upload(file, "video");
            name = (String) session.getAttribute("name");
            dest = dest.substring(dest.indexOf("video") - 1);
            return (ExVideo) fileService.insertOne(file, "video", name, summary, dest, category);
        } catch (Throwable e) {
            log.error(e.toString());
            return null;
        }
    }

    @ApiOperation(value = "上传视频文件", httpMethod = "POST", notes = "notes")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "summary", value = "视频简介", paramType = "query")
    })
    @ApiResponse(code = 200, message = "成功返回’上传成功‘，或者’上传失败‘")
    @PostMapping(value = "/lbvideo")
    public String lbvideoPost(MultipartFile file, String summary, String term, String cname, @ApiIgnore HttpSession session) {
        try {
            // 把文件保存到服务器上，并且返回服务器文件绝对路径
            String dest = fileUtil.upload(file, "lbvideo");
            log.info(dest);
            name = (String) session.getAttribute("name");
            dest = dest.substring(dest.indexOf("lbvideo") - 1);
            fileService.insertOneLb(file, name, summary, dest, term, cname, "lbvideo");
            return "上传成功";
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return "上传失败";
        }
    }

    @PostMapping(value = "/lbview")
    public String lbviewPost(MultipartFile file, String summary, String term, String cname, @ApiIgnore HttpSession session) {
        try {
            // 把文件保存到服务器上，并且返回服务器文件绝对路径
            String dest = fileUtil.upload(file, "lbview");
            log.info(dest);
            name = (String) session.getAttribute("name");
            dest = dest.substring(dest.indexOf("lbview") - 1);
            fileService.insertOneLb(file, name, summary, dest, term, cname, "lbview");
            return "上传成功";
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return "上传失败";
        }
    }

    @ApiOperation(value = "上传进度表文件", httpMethod = "POST", notes = "notes")
    @ApiResponse(code = 200, message = "成功返回’上传成功‘，或者’上传失败‘")
    @PostMapping(value = "/lbschedule")
    public String lbschedulePost(MultipartFile file, String summary, String term, String cname, @ApiIgnore HttpSession session) {

        try {
            name = (String) session.getAttribute("name");
            // 把文件保存到服务器上，并且返回服务器文件绝对路径
            String fileDest = fileUtil.upload(file, "lbschedule");
            // 判断是否已经是pdf文件
            String fileName = file.getOriginalFilename();
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
            String pdfPath;
            pdfPath = fileDest;
            // 规定文件地址和转换来的图片地址之间用问号？相隔。
            String fileDestAndImgsDest = fileDest + "?";
            // 是需要转化成图片的文件类型
            String pptExcelPdfWordSuffix = "pptx,pptm,ppt,potx,potm,pot,ppsx,ppsm,ppsm,ppam,ppa,xml,pptx,odp, xls,xlsx, pdf, docx,doc";
            if (pptExcelPdfWordSuffix.indexOf(fileType) != -1) {
                if (!"pdf".equals(fileType)) {
                    pdfPath = office2Image.transformOffice2Pdf(fileDest, runClassPath + "/lbschedule");
                } else {

                }
                List<String> imgslocation = office2Image.transformPdf2Image(pdfPath, runClassPath + "/lbschedule/img");
                StringBuilder strB = new StringBuilder();
                // 每个图片的路径都是绝对地址，要获取相对地址，而转换后的图片都是放在 lbschedule\img 下
                int startIndex = imgslocation.get(0).indexOf("/lbschedule/img");
                for (int i = 0; i < imgslocation.size(); i++) {
                    strB.append(imgslocation.get(i).substring(startIndex) + ",");
                }
                // 把文件路径和转换后的各图片都放在同一个字符串内，service内部再去以字符 '?' 拆分
                fileDestAndImgsDest += strB.toString();
            } else {
                fileDestAndImgsDest += "";
            }

            fileService.insertOneLb(file, name, summary, fileDestAndImgsDest, term, cname, "lbschedule");
            return "上传成功";
        } catch (Throwable e) {
            e.printStackTrace();
            log.error(e.toString());
            return "上传失败";
        }
    }


    @ApiOperation("上传进度表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "category", value = "进度表的分类")
    })
    @PostMapping("/schedule")
    public ExSchedule schedulePost(@ApiParam(name = "file",value = "进度表", required = true) MultipartFile file, String category, @ApiIgnore HttpSession session) {
        try {
            // 把文件保存到服务器上，并且返回服务器文件绝对路径
            String dest = fileUtil.upload(file, "schedule");
            name = (String) session.getAttribute("name");
            return (ExSchedule) fileService.insertOne(file, "schedule", name, category, dest, category);
        } catch (Throwable e) {
            e.printStackTrace();
            log.error(e.toString());
            return null;
        }
    }

    @ApiOperation("上传新闻，传送 title、content、uptime、summary 即可")
    @ApiResponse(code = 200, message = "返回 ‘上传失败’，或者 ’上传成功‘")
    @PostMapping("/news")
    public ExNews newsPost(ExNews news, @ApiIgnore HttpSession session) {
        try {
            news.setAuthor((String) session.getAttribute("name"));
            news.setViewnum(0);
            return fileService.insertOneNews(news);
        } catch (Throwable e) {
            e.printStackTrace();
            log.error(e.toString());
            return null;
        }
    }

    @ApiOperation("理论课管理员上传新闻，公共新闻，课程名、学期为''空字符串")
    @ApiResponse(code = 200, message = "返回 ‘上传失败’，或者 ’上传成功‘")
    @PostMapping("/lbnews")
    public Object lbnewsPost(Lbnews news, @ApiIgnore HttpSession session) {
        try {
            news.setAuthor(session.getAttribute("name")+"");
            Object o = fileService.insertOneLbnews(news);
            return "上传成功";
        } catch (Throwable e) {
            e.printStackTrace();
            log.error(e.toString());
            return "上传失败";
        }
    }
    @ApiOperation("理论课管理员上传新闻，公共新闻，课程名、学期为''空字符串")
    @ApiResponse(code = 200, message = "返回 ‘上传失败’，或者 ’上传成功‘")
    @PostMapping("/lbnotice")
    public Object lbnoticePost(Lbnotice lbnotice, @ApiIgnore HttpSession session) {
        try {
            lbnotice.setAuthor((String) session.getAttribute("name"));
            log.info(String.valueOf(lbnotice.getContent().length()));
            Object o = fileService.insertOneLbnotice(lbnotice);
            return "上传成功";
        } catch (Throwable e) {
            e.printStackTrace();
            log.error(e.toString());
            return "上传失败";
        }
    }

    @ApiOperation("删除视频")
    @ApiImplicitParam(name = "id", value = "视频的主键 id")
    @DeleteMapping("/video")
    public String videoDelete(String id, @ApiIgnore HttpSession session) {
        try {

            fileService.delete("video", Integer.parseInt(id));
            return "删除成功";
        } catch (Throwable throwable) {
            log.error(throwable.toString());
            return "删除失败";
        }
    }

    @ApiOperation("删除视频")
    @ApiImplicitParam(name = "id", value = "视频的主键 id")
    @DeleteMapping("/lbvideo")
    public String lbvideoDelete(String id, @ApiIgnore HttpSession session) {
        try {

            fileService.delete("lbvideo", Integer.parseInt(id));
            return "删除成功";
        } catch (Throwable throwable) {
            log.error("视频id" + id + " " + throwable.toString());
            return "删除失败";
        }
    }
    @DeleteMapping("/lbview")
    public String lbviewDelete(String id, @ApiIgnore HttpSession session) {
        try {

            fileService.delete("lbview", Integer.parseInt(id));
            return "删除成功";
        } catch (Throwable throwable) {
            log.error("视频id" + id + " " + throwable.toString());
            return "删除失败";
        }
    }

    @ApiOperation("删除进度表")
    @ApiImplicitParam(name = "id", value = "进度表的主键 id")
    @DeleteMapping("/schedule")
    public String scheduleDelete(String id) {
        try {
            fileService.delete("schedule", Integer.parseInt(id));
            return "删除成功";
        } catch (Throwable throwable) {
            log.error("进度表id" + id + " " + throwable.toString());
            return "删除失败";
        }
    }

    @ApiOperation("删除进度表")
    @ApiImplicitParam(name = "id", value = "进度表的主键 id")
    @DeleteMapping("/lbschedule")
    public String lbscheduleDelete(String id) {
        try {
            fileService.delete("lbschedule", Integer.parseInt(id));
            return "删除成功";
        } catch (Throwable throwable) {
            log.error("进度表id" + id + " " + throwable.toString());
            return "删除失败";
        }
    }

    @ApiOperation("删除新闻")
    @ApiImplicitParam(name = "id", value = "新闻的主键 id")
    @DeleteMapping("/news")
    public String newsDelete(Integer id) {
        try {
            fileService.delete("news", id);
            return "删除成功";
        } catch (Throwable throwable) {
            log.error("新闻id" + id + " " + throwable.toString());
            return "删除失败";
        }
    }

    @ApiOperation("删除新闻")
    @ApiImplicitParam(name = "id", value = "新闻的主键 id")
    @DeleteMapping("/lbnews")
    public String lbnewsDelete(Integer id) {
        try {
            fileService.delete("lbnews", id);
            return "删除成功";
        } catch (Throwable throwable) {
            log.error("新闻id：" + id + throwable.toString());
            return "删除失败";
        }
    }
    @ApiOperation("删除进度表")
    @ApiImplicitParam(name = "id", value = "新闻的主键 id")
    @DeleteMapping("/lbnotice")
    public String lbnoticeDelete(Integer id) {
        try {
            fileService.delete("lbnotice", id);
            return "删除成功";
        } catch (Throwable throwable) {
            log.error("进度表id：" + id + throwable.toString());
            return "删除失败";
        }
    }

    @ApiOperation("下载列表中下载进度表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileDest", value = "进度表服务器地址", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "fileName", value = "文件名", required = true, dataType = "String", paramType = "query")
    })
    @RequestMapping("/schedule/download")
    public void insert(String fileName, String fileDest, @ApiIgnore HttpServletResponse response) {
        // 先把数据生成 Excel 文件，然后再把文件关联到 response 流中
        try {
            // 以二进制传送该文件
            response.addHeader("content-Type", "application/octet-stream");
            // 设置文件名编码，防止文件名乱码
            String str = fileName;
            String filename =  new String(str.getBytes("UTF-8"), "iso-8859-1");
            // 是以什么方式下载，如attachment为以附件方式下载，这个信息头会告诉浏览器这个文件的名字和类型。
            response.addHeader("content-Disposition","attachment;filename=" + filename);
            log.info(fileDest);
            fileUtil.download(fileDest, response);
        } catch (Throwable e) {
            e.printStackTrace();
            log.error("下载资源失败", e, fileDest);
        }
    }

    @ApiOperation("2理论课移动两条新闻的位置，传送被点击的id、移动的方向、学期和课程名。返回当前的被点击行应该被替换的记录")
    @ApiResponse(code = 200, message = "返回 ‘上传失败’，或者 ’上传成功‘")
    @PutMapping("/lbnews")
    public Object lbnewsPut(Long id, String dir, String term, String cname) {
        try {
            // dir 上：u，下： d
            fileService.lbnewsPut(id, dir, term, cname);
            return "移动成功";
        } catch (Throwable e) {
            e.printStackTrace();
            log.error(e.toString());
            return "移动失败";
        }
    }

    @ApiOperation("2理论课移动两条视频的位置，传送被点击的id、移动的方向、学期和课程名。返回当前的被点击行应该被替换的记录")
    @ApiResponse(code = 200, message = "返回 ‘上传失败’，或者 ’上传成功‘")
    @PutMapping("/lbvideo")
    public Object lbvideoPut(Long id, String dir, String term, String cname) {
        try {
            // dir 上：u，下： d
            fileService.lbvideoPut(id, dir, term, cname);
            return "移动成功";
        } catch (Throwable e) {
            e.printStackTrace();
            log.error(e.toString());
            return "移动失败";
        }
    }
    @PutMapping("/lbview")
    public Object lbviewPut(Long id, String dir, String term, String cname) {
        try {
            // dir 上：u，下： d
            fileService.lbviewPut(id, dir, term, cname);
            return "移动成功";
        } catch (Throwable e) {
            log.error(e.toString());
            return "移动失败";
        }
    }

    @ApiOperation("2理论课移动两条进度表的位置，传送被点击的id、移动的方向、学期和课程名。返回当前的被点击行应该被替换的记录")
    @ApiResponse(code = 200, message = "返回 ‘上传失败’，或者 ’上传成功‘")
    @PutMapping("/lbschedule")
    public Object lbschedulePut(Long id, String dir, String term, String cname) {
        try {
            // dir 上：u，下： d
            fileService.lbschedulePut(id, dir, term, cname);
            return "移动成功";
        } catch (Throwable e) {
            e.printStackTrace();
            log.error(e.toString());
            return "移动失败";
        }
    }

    @ApiOperation("2理论课移动两条通知的位置，传送被点击的id、移动的方向、学期和课程名。返回当前的被点击行应该被替换的记录")
    @ApiResponse(code = 200, message = "返回 ‘上传失败’，或者 ’上传成功‘")
    @PutMapping("/lbnotice")
    public Object lbnoticePut(Long id, String dir, String term, String cname) {
        try {
            // dir 上：u，下： d
            fileService.lbnoticePut(id, dir, term, cname);
            return "移动成功";
        } catch (Throwable e) {
            e.printStackTrace();
            log.error(e.toString());
            return "移动失败";
        }
    }

}


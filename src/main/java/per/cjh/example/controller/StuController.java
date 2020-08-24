package per.cjh.example.controller;

import com.google.common.collect.HashBasedTable;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import per.cjh.example.domain.ExSC;
import per.cjh.example.service.StuService;
import per.cjh.example.utils.FileUtil;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author cjh
 * @description: 用于学生课程的导入、确认上传、查找学生记录、修改该生记录
 *              get -学生展示、import -学生导入预览、upload -学生上传确认导入
 *              selectOne -管理员查找一个学生记录、put -修改当前查找到的学生
 * @date 2020/5/4 20:44
 */
@Api(tags = "学生信息管理的相关 API")
@RestController
@RequestMapping("/stu")
@Slf4j
public class StuController {

    @Autowired
    private StuService stuService;
    @Autowired
    private FileUtil fileUtil;
    private ExSC exSC;

    @ApiOperation("页码获取学生列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curpage", value = "页码")
    })
    @GetMapping()
    public List<ExSC> stuGet(Integer curpage) {
        return stuService.stuGet(curpage);
    }

    @ApiOperation("学生修改个人信息一刷新，获取个人信息")
    @GetMapping("/self")
    public ExSC stuSelf(@ApiIgnore HttpSession session) {
        try {

            String sno = (String) session.getAttribute("username");
            return stuService.stuSelfGet(sno);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    @ApiOperation("导入学生课程 Excel")
    @ApiImplicitParams({
    })
    @ApiResponses({
            @ApiResponse(code = 200, responseContainer = "List", message = "成功是返回数组，不成功返回空串"),
    })
    @PostMapping("/import")
    public List<ExSC> stuImport(@ApiParam(name = "file", value = "学生课程 Excel 文件", required = true) MultipartFile file, String term, String cname) {
        try {
            String dest = fileUtil.upload(file, "stuCourse");
            return stuService.parseStuFile(dest, term, cname);
        } catch (Throwable e) {
            e.printStackTrace();
            log.error(e.toString());
            return null;
        }
    }

    @ApiOperation("上传已经导入的学生课程列表")
    @ApiImplicitParams({
    })
    @PostMapping()
    public String stuUpload(@RequestBody List<ExSC> stuCourses) {
        try {
            stuService.insertAll(stuCourses);
            return "上传成功";
        } catch (Throwable e) {
            e.printStackTrace();
            return "上传失败";
        }
    }

    @ApiOperation("查询一条学生记录，用于修改")
    @ApiImplicitParams({
    })
    @GetMapping(value = "/selectOne")
    public ExSC stupSelectOne(ExSC stuCourse) {
        try {
            return stuService.selectOne(stuCourse);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    @ApiOperation("修改查询到的那一条学生记录，增加三个隐藏的文本框，放置原来的姓名、学号、手机号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oriSno", value = "原学号"),
            @ApiImplicitParam(name = "oriName", value = "原姓名"),
            @ApiImplicitParam(name = "oriPhone", value = "原手机号"),
    })
    @PutMapping()
    public String stuPut(ExSC stuCourse, String oriSno, String oriName, String oriPhone) {
        try {
            stuService.updateOne(stuCourse, oriSno, oriName, oriPhone);
            return "修改成功";
        } catch (Throwable e) {
            e.printStackTrace();
            return "修改失败";
        }
    }


}

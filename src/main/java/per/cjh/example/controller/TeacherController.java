package per.cjh.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import per.cjh.example.domain.ExSC;
import per.cjh.example.domain.ExTC;
import per.cjh.example.service.TeacherService;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author cjh
 * @description: 和教师相关的操作
 *              get -教师展示、post -教师添加、put -教师更新、delete -教师删除
 * @date 2020/5/4 20:47
 */
@Api(tags = "教师信息管理的相关 API")
@RestController
@RequestMapping("/teacher")
@Slf4j
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    @ApiOperation("页码查询教师课程列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curpage", value = "页码")
    })
    @GetMapping()
    public List<ExTC> teaGet(Integer curpage) {
        try {
            return teacherService.get(curpage);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
    @ApiOperation("教师修改个人信息一刷新，获取个人信息")
    @GetMapping("/self")
    public ExTC stuSelf(@ApiIgnore HttpSession session) {
        try {
            String phone = (String) session.getAttribute("username");
            log.info("教师：" + phone + " 正在修改个人信息");
            return teacherService.teacherSelfGet(phone);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    @ApiOperation("新增教师课程记录")
    @ApiImplicitParams({
    })
    @PostMapping()
    public String teaPost(ExTC teacherCourse) {
        try {
            log.info(teacherCourse.toString());
            teacherService.insertOne(teacherCourse);
            return "添加成功";
        } catch (Throwable e) {
            e.printStackTrace();
            return "添加失败";
        }
    }

    @ApiOperation("管理员修改教师课程记录，增加三个隐藏文本框，放置原手机号、学期名、课程名")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oriPhone", value = "原手机号"),
            @ApiImplicitParam(name = "oriTerm", value = "原学期名"),
            @ApiImplicitParam(name = "oriCname", value = "原课程名"),
    })
    @PutMapping()
    public String teaPut(ExTC teacherCourse, String oriPhone, String oriTerm, String oriCname) {
        try {
            teacherService.updateOne(oriPhone, oriTerm, oriCname, teacherCourse);
            return "修改成功";
        } catch (Throwable e) {
            e.printStackTrace();
            return "修改失败";
        }
    }

    @ApiOperation("修改教师个人信息页面，根据原手机号修改教师密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oriPhone", value = "原手机号"),
            @ApiImplicitParam(name = "password", value = "新密码"),
    })
    @PutMapping("/self")
    public String teaSelfPut(String password, String oriPhone) {
        try {
            teacherService.update(password, oriPhone);
            return "修改成功";
        } catch (Throwable e) {
            e.printStackTrace();
            return "修改失败";
        }
    }
    @ApiOperation("删除一条教师课程记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号"),
            @ApiImplicitParam(name = "term", value = "学期名"),
            @ApiImplicitParam(name = "cname", value = "课程名"),
    })
    @DeleteMapping()
    public String teaDelete(String oriPhone, String oriTerm, String oriCname) {
        try {
            teacherService.deleteOne(oriPhone, oriTerm, oriCname);
            return "删除成功";
        } catch (Throwable e) {
            e.printStackTrace();
            return "删除失败";
        }
    }
}
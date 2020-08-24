package per.cjh.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import per.cjh.example.domain.CommonRet;
import per.cjh.example.domain.ExProject;
import per.cjh.example.service.ProjectService;
import sun.rmi.runtime.Log;

import java.util.List;

/**
 * @author cjh
 * @description: 和项目相关的操作
 * /project/getAll -根据学期和课程展示项目、/project/post -增加项目记录、/project/delete -删除项目记录
 * /project/getPage -管理员设置项目页面，获取已有项目
 * @date 2020/5/4 21:05
 */
@Api(tags = "项目管理的相关 API")
@RestController
@RequestMapping("/project")
@Slf4j
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @ApiOperation("学期、课程获取旗下项目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "term", value = "学期名"),
            @ApiImplicitParam(name = "cname", value = "课程名")
    })
    @GetMapping("/getAll")
    public List<ExProject> projectGet(String term, String cname) {
        return projectService.projectGet(term, cname);
    }

    @ApiOperation("页码获取项目列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curpage", value = "页码")
    })
    @GetMapping("/getPage")
    public List<ExProject> projectGet(Integer curpage) {
        return projectService.projectGet(curpage);
    }

    @ApiOperation("管理员设置项目，于此同时增加上该课程的所有同学的评分记录，以此保证项目一设置，管理员就能开始看见所有同学对该项目的记录")
    @ApiImplicitParams({
    })
    @PostMapping()
    public CommonRet projectPost(ExProject project) {
        try {
            return projectService.insertOne(project);
        } catch (Throwable e) {
            e.printStackTrace();
            return new CommonRet("400", "添加失败", project);
        }
    }

    @ApiOperation("管理员删除项目，传入点击行的课程、学期、项目名")
    @ApiImplicitParams({
    })
    @DeleteMapping()
    public String projectDelete(ExProject project) {
        try {
            log.info(project.toString());
            projectService.delete(project);
            return "删除成功";
        } catch (Throwable e) {
            e.printStackTrace();
            return "删除失败";
        }
    }
}

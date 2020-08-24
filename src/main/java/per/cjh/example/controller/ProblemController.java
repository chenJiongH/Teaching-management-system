package per.cjh.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import per.cjh.example.domain.CommonRet;
import per.cjh.example.domain.ExProblem;
import per.cjh.example.service.ExproblemService;

import java.util.List;

/**
 * @author cjh
 * @description: 和题目相关的操作。包括学生答题、教师评分、管理员设置题目
 *              /problem/get -根据学期、课程、项目获取所有学生的题目信息
 *              /problem/page -根据页码获取所有的题目列表
 *              /problem/post -根据学期、课程、项目增加该项目下的题目。如果已经有则覆盖，如果没有则新增
 * @date 2020/5/8 21:18
 */
@Api(tags = "题目 的相关 API")
@RestController
@Slf4j
@RequestMapping("/problem")
public class ProblemController {

    @Autowired
    private ExproblemService exproblemService;

    @ApiOperation("通过学期、课程名、项目名查询该项目下的三道题目（教师评分和评分管理、学生答题、管理员设置题目")
    @ApiImplicitParams({
    })
    @GetMapping()
    public ExProblem queryById(ExProblem problem) {
        log.info(problem.toString());
        return exproblemService.queryById(problem);
    }

    @ApiOperation("页码获取题目列表（管理员设置题目页面）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curpage", value = "页码")
    })
    @GetMapping("/page")
    public List<ExProblem> queryAllByPage(Integer curpage) {
        return exproblemService.queryAllByPage(curpage);
    }

    @ApiOperation("新增数据，如果已经有了，则覆盖")
    @ApiImplicitParams({
    })
    @PostMapping()
    public CommonRet insert(ExProblem exproblem) throws Throwable {
        try {
            return exproblemService.
                    insert(exproblem);
        } catch (Throwable e) {
            log.info("设置题目 " + exproblem + " " +  e);
            return new CommonRet("400", "数据无效", null);
        }
    }

    @ApiOperation("通过学期、课程、项目删除一条数据（管理员设置题目）")
    @ApiImplicitParams({
    })
    @DeleteMapping()
    public String deleteById(ExProblem problem) {
        try {
            exproblemService.deleteById(problem);
            return "删除成功";
        } catch (Throwable e) {
            e.printStackTrace();
            return "删除失败";
        }
    }
}
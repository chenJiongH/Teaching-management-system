package per.cjh.example.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import per.cjh.example.service.TermCourseService;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

/**
 * @author cjh
 * @description: 查询学期和课程
 *        term/get -学期展示、course/get -根据学期和角色类型查询课程
 * @date 2020/5/4 20:55
 */
@Api(tags = "学期、课程查询的相关 API")
@RestController
@RequestMapping()
public class TermCourseController {
    @Autowired
    private TermCourseService termCourseService;

    @ApiOperation("下拉框获取所有学期")
    @GetMapping("/term")
    public List<String> termGet(HttpSession session) {
        String username = (String) session.getAttribute("username");
        String type = (String) session.getAttribute("type");
        return termCourseService.termGet(username, type);
    }
    @ApiOperation("理论课下拉框获取所有学期")
    @GetMapping("/lbterm")
    public List<String> lbtermGet(HttpSession session) {
        String username = (String) session.getAttribute("username");
        String type = (String) session.getAttribute("type");
        return termCourseService.lbtermGet(username, type);
    }

    @ApiOperation("下拉框根据学期获取该角色下的课程：三类角色都有用到")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "term", value = "学期名"),
//            @ApiImplicitParam(name = "username", value = "从 session 中获取的当前账号"),
//            @ApiImplicitParam(name = "type", value = "从 session 中获取的当前用户角色类型")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "返回字符串数组")
    })
    @GetMapping("/course")
    public Set<String> courseGetByPage(String term,@ApiIgnore HttpSession session) {
        String username = (String) session.getAttribute("username");
        String type = (String) session.getAttribute("type");
        return termCourseService.courseGetByTerm(username, term, type);
    }

    @ApiOperation("下拉框根据学期获取该角色下的课程：三类角色都有用到")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "term", value = "学期名"),
//            @ApiImplicitParam(name = "username", value = "从 session 中获取的当前账号"),
//            @ApiImplicitParam(name = "type", value = "从 session 中获取的当前用户角色类型")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "返回字符串数组")
    })
    @GetMapping("/lbcname")
    public Set<String> lbcourseGetByPage(String term,@ApiIgnore HttpSession session) {
        String username = (String) session.getAttribute("username");
        String type = (String) session.getAttribute("type");
        return termCourseService.lbcourseGetByTerm(username, term, type);
    }
}

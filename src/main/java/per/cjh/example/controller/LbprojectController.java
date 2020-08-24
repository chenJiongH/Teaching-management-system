package per.cjh.example.controller;

import io.swagger.annotations.*;
import per.cjh.example.domain.LbProjectAndProblem;
import per.cjh.example.domain.Lbproblem;
import per.cjh.example.domain.Lbproject;
import per.cjh.example.service.LbprojectService;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * (Lbproject)表控制层
 * @author cjh
 * @description: TODO
 * @date 2020-06-03 07:34:52
 */
@Api(tags = "理论课章节布置 的相关 API")
@RestController
@Slf4j
@RequestMapping()
public class LbprojectController {

    @Resource
    private LbprojectService lbprojectService;

    @ApiOperation("学生下拉框查询课程下的项目，教师评分页面下拉框")
    @ApiResponse(code = 200, message = "成功返回实体类 Lbproject 数组")
    @GetMapping("/lbproject")
    public List<Lbproject> lbprojectGet(String term, String cname, @ApiIgnore  HttpSession session) {
        String username = session.getAttribute("username") +"";
        return lbprojectService.lbprojectGetByCname(term, cname, username);
    }

    @ApiOperation("分页查询数据")
    @ApiResponse(code = 200, message = "成功返回实体类 Lbproject 数组")
    @GetMapping("/lbprojectAndProblem")
    public List<LbProjectAndProblem> lbprojectAndProblemGet(String term, String cname, int curpage) {
        return lbprojectService.lbprojectGet(term, cname, curpage);
    }

    @ApiOperation("分页查询数据")
    @ApiResponse(code = 200, message = "成功返回实体类 Lbproject 数组")
    @GetMapping("/lbproblem")
    public Lbproblem lbprojectAndProblemGet(String term, String cname, String pname) {
        return lbprojectService.lbproblemGet(term, cname, pname);
    }


    @ApiOperation("新增项目数据和题目数据")
    @ApiResponse(code = 200, message = "返回’增加成功‘，或者’增加失败‘")
    @PostMapping("/lbproject")
    public String lbprojectPost(LbProjectAndProblem lbprojectAndProblem) {
        try {
            lbprojectService.lbprojectPost(lbprojectAndProblem);
            return "增加成功";            
        } catch (Throwable e) {
            e.printStackTrace();
            return "增加失败"; 
        }
    }
    
    @ApiOperation("修改数据")
    @ApiResponse(code = 200, message = "返回’修改成功‘，或者’修改失败‘")
    @PutMapping("/lbproject")
    public String lbprojectPut(String oriTerm, String oriCname, String oriPname,  LbProjectAndProblem lbprojectAndProblem) {
        try {
            lbprojectService.lbprojectPut(oriTerm, oriCname, oriPname, lbprojectAndProblem);
            return "修改成功";            
        } catch (Throwable e) {
            e.printStackTrace();
            return "修改失败"; 
        }
    }

    @ApiOperation("通过主键删除数据")
    @ApiResponse(code = 200, message = "返回’删除成功‘，或者’删除失败‘")
    @DeleteMapping("/lbproject")
    public String lbprojectDelete(Lbproject lbproject) {
        try {
            lbprojectService.lbprojectDelete(lbproject);
            return "删除成功";
        } catch (Throwable e) {
            e.printStackTrace();
            return "删除失败";
        }
    }
}
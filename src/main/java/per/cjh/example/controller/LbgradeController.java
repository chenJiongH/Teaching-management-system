package per.cjh.example.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import per.cjh.example.domain.ExGrade;
import per.cjh.example.domain.Lbgrade;
import per.cjh.example.service.LbgradeService;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import per.cjh.example.utils.FileUtil;
import springfox.documentation.annotations.ApiIgnore;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * (Lbgrade)表控制层
 * @author cjh
 * @description: TODO
 * @date 2020-06-07 08:08:35
 */
@Api(tags = "理论课评分的相关 API")
@RestController
@Slf4j
@RequestMapping("/lbgrade")
public class LbgradeController {

    @Resource
    private LbgradeService lbgradeService;
    @Autowired
    private FileUtil fileUtil;

    @ApiOperation("教师评分页面查询某项目下的所有学生数据")
    @ApiResponse(code = 200, message = "成功返回实体类 Lbgrade 数组")
    @GetMapping()
    public List<Lbgrade> lbgradeGet(String term, String cname, String pname) {
        return lbgradeService.lbgradeGet(term, cname, pname);
    }

    @ApiOperation("学生查询自己的答案和分数。如果username为空，即没有携带cookie，则根据主键的查询会失败")
    @ApiResponse(code = 200, message = "成功返回实体类 Lbgrade 数组")
    @GetMapping("/stu")
    public Lbgrade lbstugradeGet(String term, String cname, String pname, @ApiIgnore HttpSession session) {
        String username = session.getAttribute("username") +"";
        try {
            return lbgradeService.lbstugradeGet(term, cname, pname, username);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.error(username + " 查分数或者答题查询出错");
            return null;
        }
    }
    @ApiOperation("学生提交自己的答案")
    @ApiResponse(code = 200, message = "成功返回实体类 Lbgrade 数组")
    @PutMapping("/stu")
    public String lbstugradePut(@RequestBody Lbgrade lbgrade) {
        try {
            lbgradeService.lbstugradePut(lbgrade);
            return "提交成功";
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return "提交失败";
        }
    }

    @ApiOperation("教师评分管理页面、管理员的数据维护页面，点击导出评分按钮，先上传json数据，生成文件，返回文件的绝对路径")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "grades", value = "新增的数据实体对象", required = true, dataType = "List<ExGrade>", paramType = "body")
    })
    @PostMapping("/makeFile")
    public String makeFile(@RequestBody List<Lbgrade> grades, @ApiIgnore HttpServletResponse response) {
        // 先把数据生成 Excel 文件，然后再把文件的绝对路径返回给客户端。（然后再把文件关联到 response 流中）
        try {
            return lbgradeService.generatExcel(grades);
        } catch (Throwable e) {
            e.printStackTrace();
            log.error("导出评分失败", e, grades);
            return "导出评分失败";
        }
    }
    
    @ApiOperation("教师评分，上传查询到的多个实体类中，正在评分的那个实体类")
    @ApiResponse(code = 200, message = "返回’修改成功‘，或者’修改失败‘")
    @PutMapping()
    public String lbgradePut(@RequestBody Lbgrade lbgrade) {
        try {
            lbgradeService.lbgradePut(lbgrade);
            return "评分成功";
        } catch (Throwable e) {
            e.printStackTrace();
            return "评分失败";
        }
    }

    @ApiOperation("教师的评分管理页面，点击删除评分按钮，上传查询到的json数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "grades", value = "新增的数据实体对象", required = true, dataType = "List<ExGrade>", paramType = "body")
    })
    @DeleteMapping("/lbgrade")
    public String del(@RequestBody List<Lbgrade> grades) {
        // 删除评分按钮，把上传的查询记录都删除
        try {
            lbgradeService.lbgradeDelete(grades);
            return "删除成功";
        } catch (Throwable e) {
            e.printStackTrace();
            log.error("删除评分", e, grades);
            return "";
        }
    }
}
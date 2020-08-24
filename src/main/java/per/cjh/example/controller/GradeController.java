package per.cjh.example.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import per.cjh.example.domain.ExGrade;
import per.cjh.example.domain.Objects;
import per.cjh.example.service.ExgradeService;
import per.cjh.example.utils.FileUtil;
import springfox.documentation.annotations.ApiIgnore;
import java.util.*;


import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.tools.JavaCompiler;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author cjh
 * @description: 和评分相关的操作。包括学生答题、教师评分、管理员导出评分
 *              学生答题页面：
 *              /grade/stu/get -学生获取某学期、某课程、某项目下的答题评分
 *              /grade/stu/post -学生答题
 *              教师评分页面：
 *              /grade/teacher/get -教师根据学期、课程、项目。开始评分
 *              /grade/conditions/get -教师在评分管理页面根据任意三条件进行评分查询。
 *              /grade/teacher/put -教师评分
 *              /grade/export/post -传入当前页面的评分数据列表，然后导出，即查询到什么，导出什么。
 *              管理员数据维护页面：
 *              /grade/conditions/get -管理员在数据维护根据任意三条件进行评分查询。
 *              /grade/export/post -传入当前页面的评分数据列表，然后导出，即查询到什么，导出什么。
 * @date 2020/5/8 21:11
 */
@Api(tags = "项目评分 的相关 API")
@RestController
@Slf4j
@RequestMapping("/grade")
public class GradeController {

    @Autowired
    private ExgradeService exgradeService;
    @Autowired
    private FileUtil fileUtil;

    @ApiOperation("通过项目、学期、课程查询单条答题数据")
    @ApiImplicitParams({
    })
    @GetMapping("/stu")
    public ExGrade queryById(ExGrade grade, @ApiIgnore HttpSession session) {
        grade.setSno((String) session.getAttribute("username"));
        return exgradeService.queryById(grade);
    }

    @ApiOperation("学生答题提交，修改该生的评分数据")
    @ApiImplicitParams({
    })
    @PostMapping("/stu")
    public String insert(ExGrade exgrade,@ApiIgnore HttpSession session) throws Throwable {
        try {

            exgrade.setSno((String) session.getAttribute("username"));
            exgrade.setPassword((String) session.getAttribute("password"));
            exgrade.setName((String) session.getAttribute("name"));
            exgradeService.
                    insert(exgrade);
            return "提交成功";
        } catch (Throwable e) {
            e.printStackTrace();
            log.error("增加评分，可能是重复提交", e, exgrade);
            return "提交失败";
        }
    }

    @ApiOperation("通过项目、学期、课程查询多条学生答题数据（教师评分页面）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "grade", value = "记录主键", required = true, dataType = "ExGrade", paramType = "query")
    })
    @GetMapping("/teacher")
    public List<ExGrade> teacherQueryById(ExGrade grade) {
        return exgradeService.queryListById(grade);
    }

    @ApiOperation("通过项目、学期、课程任意种条件查询学生的答题数据（教师评分管理页面、管理员数据维护页面）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "grade", value = "查询条件", required = true, dataType = "ExGrade", paramType = "query")
    })
    @GetMapping("/conditions")
    public List<ExGrade> queryByConditions(ExGrade grade) {
        return exgradeService.queryByConditions(grade);
    }

    @ApiOperation("教师提交评分，修改学生答题记录的分数字段")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "exgrade", value = "修改的数据实体对象", required = true, dataType = "ExGrade", paramType = "body")
    })
    @PutMapping("/teacher")
    public String teacherInsert(ExGrade exgrade) throws Throwable {
        try {
            exgradeService.
                    update(exgrade);
            return "提交成功";
        } catch (Throwable e) {
            e.printStackTrace();
            log.error("教师提交评分失败", e, exgrade);
            return "提交失败";
        }
    }

    @ApiOperation("教师评分管理页面、管理员的数据维护页面，点击导出评分按钮，先上传json数据，生成文件，返回文件的绝对路径")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "grades", value = "新增的数据实体对象", required = true, dataType = "List<ExGrade>", paramType = "body")
    })
    @PostMapping("/makeFile")
    public String makeFile(@RequestBody List<ExGrade> grades, @ApiIgnore HttpServletResponse response) {
        // 先把数据生成 Excel 文件，然后再把文件的绝对路径返回给客户端。（然后再把文件关联到 response 流中）
        try {
            return exgradeService.generatExcel(grades);
        } catch (Throwable e) {
            e.printStackTrace();
            log.error("导出评分失败", e, grades);
            return "导出评分失败";
        }
    }

    @ApiOperation("教师评分管理页面、管理员的数据维护页面，点击导出评分按钮")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "grades", value = "新增的数据实体对象", required = true, dataType = "List<ExGrade>", paramType = "body")
    })
    @PostMapping("/export")
    public void insert(String fileDest, @ApiIgnore HttpServletResponse response) {
        // 先把数据生成 Excel 文件，然后再把文件关联到 response 流中
        try {
            // 以二进制传送该文件
            response.addHeader("content-Type", "application/octet-stream");
            // 设置文件名编码，防止文件名乱码
            String str = "成绩表.xls";
            String filename =  new String(str.getBytes("UTF-8"), "iso-8859-1");
            // 是以什么方式下载，如attachment为以附件方式下载，这个信息头会告诉浏览器这个文件的名字和类型。
            response.addHeader("content-Disposition","attachment;filename=" + filename);

            fileUtil.download(fileDest, response);
        } catch (Throwable e) {
            e.printStackTrace();
            log.error("导出评分失败", e, fileDest);
        }
    }


    @ApiOperation("管理员的数据维护页面，点击删除评分按钮，上传查询到的json数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "grades", value = "新增的数据实体对象", required = true, dataType = "List<ExGrade>", paramType = "body")
    })
    @DeleteMapping("/grade")
    public String del(@RequestBody List<ExGrade> grades) {
        // 删除评分按钮，把上传的查询记录都删除
        try {
            exgradeService.del(grades);
            return "删除成功";
        } catch (Throwable e) {
            e.printStackTrace();
            log.error("删除评分", e, grades);
            return "";
        }
    }
}
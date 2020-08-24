package per.cjh.example.controller;

import io.swagger.annotations.*;
import org.springframework.web.multipart.MultipartFile;
import per.cjh.example.domain.Lbsc;
import per.cjh.example.service.LbscService;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import per.cjh.example.utils.FileUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * (Lbsc)表控制层
 * @author cjh
 * @description: TODO
 * @date 2020-06-03 06:32:09
 */
@Api(tags = "理论课学生课程 的相关 API")
@RestController
@Slf4j
@RequestMapping()
public class LbscController {

    @Resource
    private LbscService lbscService;
    @Autowired
    private FileUtil fileUtil;

    @ApiOperation("分页查询数据")
    @ApiResponse(code = 200, message = "成功返回实体类 Lbsc 数组")
    @GetMapping("/lbstu")
    public List<Lbsc> lbscGet(String term, String cname, int curpage) {
        return lbscService.lbscGet(term, cname, curpage);
    }

    @ApiOperation("学生主页获取在学的所有课程")
    @ApiResponse(code = 200, message = "成功返回实体类 Lbsc 数组")
    @GetMapping("/lbstu/course")
    public List<Lbsc> lbscCourseGet(HttpSession session) {
        String sno = session.getAttribute("username") + "";
        return lbscService.lbscCourseGet(sno);
    }
    @ApiOperation("学生主页点击方框跳转到方框内的学期、课程")
    @PostMapping("/lbstu/start")
    public String lbscStartPost(String cname, String term, HttpSession session) {
        session.setAttribute("cname", cname);
        session.setAttribute("term", term);
        return "ok";
    }
    @ApiOperation("学生答题或查分点击方框跳转到方框内的学期、课程")
    @GetMapping("/lbstu/start")
    public List<String> lbscStartGet(String cname, String term, HttpSession session) {
        List<String> courseAndTerm = new ArrayList<>();
        courseAndTerm.add(session.getAttribute("cname") +"");
        courseAndTerm.add(session.getAttribute("term") +"");
        return courseAndTerm;
    }

    @ApiOperation("新增数据")
    @ApiResponse(code = 200, message = "返回’增加成功‘，或者’增加失败‘")
    @PostMapping("/lbstuImport")
    public String lbscImport(MultipartFile file, String term, String cname) {
        try {
            // 保存文件，返回文件在服务器的绝对路径
            String dest = fileUtil.upload(file, "stuCourse");
            // 解析保存起来的文件
            lbscService.parseStuFile(dest, term, cname);
            return "增加成功";
        } catch (Throwable e) {
            e.printStackTrace();
            return "增加失败";
        }
    }

    @ApiOperation("新增数据")
    @ApiResponse(code = 200, message = "返回’增加成功‘，或者’增加失败‘")
    @PostMapping("/lbstu")
    public String lbscPost(Lbsc lbsc) {
        try {
            return lbscService.lbscPost(lbsc);
        } catch (Throwable e) {
            e.printStackTrace();
            return "增加失败"; 
        }
    }

    @ApiOperation("修改数据")
    @ApiResponse(code = 200, message = "返回’修改成功‘，或者’修改失败‘")
    @PutMapping("/lbstu")
    public String lbscPut(Lbsc lbsc) {
        try {
            lbscService.lbscPut(lbsc);
            return "修改成功";            
        } catch (Throwable e) {
            e.printStackTrace();
            return "修改失败"; 
        }
    }

    @ApiOperation("通过主键删除数据")
    @ApiResponse(code = 200, message = "返回’删除成功‘，或者’删除失败‘")
    @DeleteMapping("/lbstu")
    public String lbscDelete(Lbsc lbsc) {
        try {
            lbscService.lbscDelete(lbsc);
            return "删除成功";
        } catch (Throwable e) {
            e.printStackTrace();
            return "删除失败";
        }
    }
}
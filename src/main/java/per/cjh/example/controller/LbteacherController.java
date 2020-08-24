package per.cjh.example.controller;

import io.swagger.annotations.*;
import per.cjh.example.domain.Lbteacher;
import per.cjh.example.service.LbteacherService;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;

/**
 * (Lbteacher)表控制层
 * @author cjh
 * @description: TODO
 * @date 2020-06-02 15:38:12
 */
@Api(tags = "理论课教师的相关 API")
@RestController
@Slf4j
@RequestMapping("/lbteacher")
public class LbteacherController {

    @Resource
    private LbteacherService lbteacherService;

    @ApiOperation("分页查询数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curpage", value = "当前页码", required = true, dataType = "int", paramType = "query")
    })
    @ApiResponse(code = 200, message = "成功返回实体类 Lbteacher 数组")
    @GetMapping()
    public List<Lbteacher> lbteacherGet(String term, int curpage) {
        return lbteacherService.lbteacherGet(term, curpage);
    }

    @ApiOperation("新增数据")
    @ApiImplicitParams({
    })
    @ApiResponse(code = 200, message = "返回’增加成功‘，或者’增加失败‘")
    @PostMapping()
    public String lbteacherPost(Lbteacher lbteacher) {
        try {
            lbteacherService.lbteacherPost(lbteacher);
            return "上传成功";
        } catch (Throwable e) {
            e.printStackTrace();
            return "上传失败";
        }
    }
    
    @ApiOperation("修改数据")
    @ApiResponse(code = 200, message = "返回’修改成功‘，或者’修改失败‘")
    @PutMapping()
    public String lbteacherPut(Lbteacher lbteacher) {
        try {
            lbteacherService.lbteacherPut(lbteacher);
            return "修改成功";            
        } catch (Throwable e) {
            e.printStackTrace();
            return "修改失败"; 
        }
    }

    @ApiOperation("通过主键删除数据")
    @ApiResponse(code = 200, message = "返回’删除成功‘，或者’删除失败‘")
    @DeleteMapping()
    public String lbteacherDelete(Lbteacher lbteacher) {
        try {
            lbteacherService.lbteacherDelete(lbteacher);
            return "删除成功";
        } catch (Throwable e) {
            e.printStackTrace();
            return "删除失败";
        }
    }
}
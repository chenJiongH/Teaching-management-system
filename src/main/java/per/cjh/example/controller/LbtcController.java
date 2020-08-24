package per.cjh.example.controller;

import io.swagger.annotations.*;
import per.cjh.example.domain.Lbtc;
import per.cjh.example.service.LbtcService;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * (Lbtc)表控制层
 * @author cjh
 * @description: TODO
 * @date 2020-06-02 14:52:15
 */
@Api(tags = "理论课教师 的相关 API")
@RestController
@Slf4j
@RequestMapping()
public class LbtcController {

    @Autowired
    private LbtcService lbtcService;

    @ApiOperation("分页查询数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "curpage", value = "当前页码", required = true, dataType = "int", paramType = "query")
    })
    @ApiResponse(code = 200, message = "成功返回实体类 Lbtc 数组")
    @GetMapping("/lbcourse")
    public List<Lbtc> lbtcGet(int curpage, String term) {
        return lbtcService.lbtcGet(curpage, term);
    }

    @ApiOperation("新增数据")
    @ApiResponse(code = 200, message = "返回’增加成功‘，或者’增加失败‘")
    @PostMapping("/lbcourse")
    public String lbtcPost(Lbtc lbtc) {
        try {
            lbtcService.lbtcPost(lbtc);
            return "上传成功";
        } catch (Throwable e) {
            log.info(lbtc.toString());
            log.info(e.toString());
            return "当前学期无该教师记录！";
        }
    }
    
    @ApiOperation("修改数据")
    @ApiResponse(code = 200, message = "返回’修改成功‘，或者’修改失败‘")
    @PutMapping("/lbcourse")
    public String lbtcPut(Lbtc lbtc) {
        try {
            lbtcService.lbtcPut(lbtc);
            return "修改成功";
        } catch (Throwable e) {
            e.printStackTrace();
            return "修改失败"; 
        }
    }

    @ApiOperation("通过主键删除数据")
    @ApiResponse(code = 200, message = "返回’删除成功‘，或者’删除失败‘")
    @DeleteMapping("/lbcourse")
    public String lbtcDelete(Lbtc lbtc) {
        try {
            lbtcService.lbtcDelete(lbtc);
            return "删除成功";
        } catch (Throwable e) {
            e.printStackTrace();
            return "删除失败";
        }
    }


}
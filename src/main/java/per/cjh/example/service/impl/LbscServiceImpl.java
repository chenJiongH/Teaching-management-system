package per.cjh.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import per.cjh.example.domain.ExSC;
import per.cjh.example.domain.Lbgrade;
import per.cjh.example.domain.Lbproject;
import per.cjh.example.domain.Lbsc;
import per.cjh.example.mappers.LbgradeMapper;
import per.cjh.example.mappers.LbprojectMapper;
import per.cjh.example.mappers.LbscMapper;
import per.cjh.example.service.LbscService;
import org.springframework.stereotype.Service;
import per.cjh.example.utils.FileUtil;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * (Lbsc)表服务实现类
 * @author cjh
 * @description: TODO
 * @date 2020-06-03 06:32:09
 */
@Slf4j
@Service
public class LbscServiceImpl implements LbscService {
    @Resource
    private LbscMapper lbscMapper;
    @Resource
    private LbgradeMapper lbgradeMapper;
    @Resource
    LbprojectMapper lbprojectMapper;
    @Autowired
    private FileUtil fileUtil;


    /**
     * 查询多条数据
     * @param term
     * @param curpage 查询页码
     * @return 对象列表
     */
    @Override
    public List<Lbsc> lbscGet(String term, String cname, int curpage) {
        Example example = new Example(Lbsc.class);
        example.orderBy("sno");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("term", term)
                .andEqualTo("cname", cname);
        RowBounds rowBounds = new RowBounds((curpage - 1) * 20, 20);
        return lbscMapper.selectByExampleAndRowBounds(example, rowBounds);
    }

    @Override
    public List<Lbsc> lbscCourseGet(String sno) {
        Example example = new Example(Lbsc.class);
        example.orderBy("term").desc();
        Example.Criteria criteria = example.createCriteria();
        if (sno != null && !"null".equals(sno)) {
            criteria.andEqualTo("sno", sno);
        }
        return lbscMapper.selectByExample(example);
    }

    /**
     * 新增数据
     * @param lbsc 实例对象
     * @throws Throwable 新增异常
     */
    @Override
    public String lbscPost(Lbsc lbsc) throws Throwable {
        if (lbscMapper.selectByPrimaryKey(lbsc) != null) {
            this.lbscPut(lbsc);
            return "修改成功";
        }
        lbscMapper.insertSelective(lbsc);
        this.insertGradeRecord(lbsc);
        return "增加成功";
    }

    /**
     * 修改数据
     * @param lbsc 实例对象
     * @throws Throwable 修改异常
     */
    @Override
    public void lbscPut(Lbsc lbsc) throws Throwable {
    
        lbscMapper.updateByPrimaryKey(lbsc);
    }

    /**
     * 通过主键删除数据
     * @param lbsc 主键
     * @throws Throwable 删除异常
     */
    @Override
    public void lbscDelete(Lbsc lbsc) throws Throwable {
        lbscMapper.deleteByPrimaryKey(lbsc);
        // 删除该生的所有评分记录
        Example example = new Example(Lbgrade.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sno", lbsc.getSno());
        lbgradeMapper.deleteByExample(example);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void parseStuFile(String dest, String term, String cname) throws Throwable {
        Workbook workbook = fileUtil.getWorkbookByFilename(dest);
        if (workbook == null) {
            throw new Throwable("请上传excel文件！");
        }
        //获取第一个工作表，只有一个工作表
        Sheet sheet = workbook.getSheetAt(0);
        //获取该 sheet 有多少 row，并且遍历该行的所有单元格 cell
        // 页面下拉框选择 学年 课程
        // 导入的 Excel 表格头为：学号 姓名	密码 电话
        log.info("sheet.getLastRowNum()" + " " + sheet.getLastRowNum());
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            for (Cell cell : row) {
                cell.setCellType(CellType.STRING);
            }
            Lbsc exSC = new Lbsc();
            exSC.setTerm(term).setCname(cname);
            // 表格数据转成 javaBean
            exSC.setSno(row.getCell(0)+"").
                    setName(row.getCell(1)+"").
                    setPassword(row.getCell(2)+"").
                    setPhone( row.getCell(3)+"");
            // 学生已存在，修改。不存在，添加
            if (lbscMapper.selectByPrimaryKey(exSC) != null) {
                lbscMapper.updateByPrimaryKeySelective(exSC);
            } else {
                lbscMapper.insertSelective(exSC);
                // 给新增的学生插入该课程已有的评分记录
                this.insertGradeRecord(exSC);
            }
        }
    }

    public void insertGradeRecord(Lbsc exSC) {
        // 给新增的学生插入该课程已有的评分记录。1、先查找该课程的所有项目记录。2、
        Example example = new Example(Lbproject.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("term", exSC.getTerm())
                .andEqualTo("cname", exSC.getCname());
        List<Lbproject> lbprojects = lbprojectMapper.selectByExample(example);
        Lbgrade lbgrade = new Lbgrade();
        lbgrade.setTerm(exSC.getTerm()).
                setCname(exSC.getCname()).
                setName(exSC.getName()).
                setPassword(exSC.getPassword()).
                setSno(exSC.getSno());
        for (int i = 0; i < lbprojects.size(); i++) {
            lbgrade.setPname(lbprojects.get(i).getPname());
            if (lbgradeMapper.selectByPrimaryKey(lbgrade) == null) {
                lbgradeMapper.insert(lbgrade);
            }
        }
    }

}
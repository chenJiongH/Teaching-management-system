package per.cjh.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mysql.cj.util.StringUtils;
import per.cjh.example.domain.ExCource;
import per.cjh.example.domain.ExSC;
import per.cjh.example.domain.ExTerm;
import per.cjh.example.mappers.ExSCMapper;
import per.cjh.example.service.StuService;
import per.cjh.example.service.TermCourseService;
import per.cjh.example.utils.FileUtil;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author cjh
 * @description: TODO
 * @date 2020/5/5 19:59
 */
@Slf4j
@Service
public class StuServiceImpl implements StuService {

    @Resource
    private ExSCMapper scMapper;
    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private TermCourseService termCourseService;

    @Override
    public List<ExSC> stuGet(Integer curpage) {
        Example example = new Example(ExSC.class);
        example.orderBy("term").desc().orderBy("name").asc();
        RowBounds rowBounds = new RowBounds((curpage - 1) * 20, 20);
        return scMapper.selectByExampleAndRowBounds(example, rowBounds);
    }

    @Override
    public List<ExSC> parseStuFile(String dest, String term, String cname) throws Throwable {
        Workbook workbook = fileUtil.getWorkbookByFilename(dest);
        if (workbook == null) {
            throw new Throwable("请上传excel文件！");
        }
        //获取第一个工作表，只有一个工作表
        Sheet sheet = workbook.getSheetAt(0);
        //获取该 sheet 有多少 row，并且遍历该行的所有单元格 cell
        // 页面下拉框选择 学年 课程
        // 导入的 Excel 表格头为：学号 姓名	密码

        List<ExSC> exSCS = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            for (Cell cell : row) {
                cell.setCellType(CellType.STRING);
            }
            ExSC exSC = new ExSC();
            exSC.setTerm(term).setCname(cname);
            // 表格数据转成 javaBean
            exSC.setSno(row.getCell(0).getStringCellValue()).
                 setName(row.getCell(1).getStringCellValue()).
                 setPassword(row.getCell(2).getStringCellValue());
            log.info(i+" " + exSC);
            exSCS.add(exSC);
        }
        return exSCS;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void insertAll(List<ExSC> exSCS) throws Throwable {
        // 在插入的同时对学期和课程分别进行去重
        Set<ExTerm> terms = new HashSet<>();
        Set<ExCource> cources = new HashSet<>();
        for (ExSC exSC : exSCS) {
            scMapper.insert(exSC);
        }
    }

    @Override
    public ExSC selectOne(ExSC exSC) throws Throwable {
        Example example = new Example(ExSC.class);
        Example.Criteria criteria = example.createCriteria();
        // 对手机号、姓名、电话、密码进行去重，然后判断去重后的结果，防止该查询是匹配两个学生的，导致修改时出错
        example.setDistinct(true);
        example.selectProperties("sno", "name", "phone", "password");
        // 如果学号、姓名、手机 哪个字段不为空就作为查询条件
        if (!StringUtils.isEmptyOrWhitespaceOnly(exSC.getSno())) {
            criteria.andEqualTo("sno", exSC.getSno());
        }
        if (!StringUtils.isEmptyOrWhitespaceOnly(exSC.getName())) {
            criteria.andEqualTo("name", exSC.getName());
        }
        if (!StringUtils.isEmptyOrWhitespaceOnly(exSC.getPhone())) {
            criteria.andEqualTo("phone", exSC.getPhone());
        }
        List<ExSC> exSCS = scMapper.selectByExample(example);
        if (exSCS.size() != 1) {
            throw new Throwable("查询结果超过1");
        }
        return exSCS.get(0);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateOne(ExSC exSC, String oriSno, String oriName, String oriPhone) throws Throwable {
        // 可能连主键都一起修改，可以查询出此时所有匹配的记录，删除之，再添加新数据。或者需要新增单表方法
        Example example = new Example(ExSC.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sno", oriSno).
                andEqualTo("name", oriName).
                andEqualTo("phone", oriPhone);
        // 管理员可以修改的字段有 学号、姓名、密码、手机号，查询出所有该生的学生课程记录，然后删除并替换成新记录
        for (ExSC stuCourse : scMapper.selectByExample(example)) {
            // 删除原记录
            scMapper.deleteByPrimaryKey(stuCourse);
            // 插入新记录，先把主键替换成被修改的新值
            stuCourse.setSno(exSC.getSno()).
                    setPassword(exSC.getPassword()).
                    setName(exSC.getName()).
                    setPhone(exSC.getPhone());
            scMapper.insertSelective(stuCourse);
        }
    }


    @Override
    public ExSC stuSelfGet(String sno) throws Throwable {
        Example example = new Example(ExSC.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sno", sno);
        RowBounds rowBounds = new RowBounds(0, 1);
        return scMapper.selectByExampleAndRowBounds(example, rowBounds).get(0);
    }

}

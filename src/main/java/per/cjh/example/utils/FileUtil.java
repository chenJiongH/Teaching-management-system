package per.cjh.example.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import per.cjh.example.domain.ExGrade;
import per.cjh.example.domain.Lbgrade;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.UUID;

/**
 * @author cjh
 * @description: TODO
 * @date 2020/5/5 20:19
 */
@Slf4j
@Component
public class FileUtil {

    @Value("${file.save.location}")
    private String runClassPath;
    /**
     * 把文件存放在类路径下的 static/fileType(文件类型，如 vedio、schedule）/xxx.xxx
     * 并返回目的文件全路径类名
     * @param file
     * @param fileType
     * @return 文件全路径类名
     * @throws Throwable
     */
    public String upload(MultipartFile file, String fileType) throws Throwable {
        if (file.isEmpty()) {
            throw new Throwable("文件为空");
        }
        // static/fileType/xxx.xxx 文件存放位置有 fileType来区分开视频和进度表
        String filePath = runClassPath + File.separator + fileType + File.separator;
        File dest;
        // 导入学生文件名用uuid，其他用原来的名字
        if ("stuCourse".equals(fileType)) {
            // 求该文件的后缀
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));
            dest = new File(filePath + UUID.randomUUID() + suffix);
        } else {
            dest = new File(filePath + file.getOriginalFilename());
        }
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        file.transferTo(dest);
        return dest.getPath();
    }

    /**
     * 传入文件名，根据最后的文件后缀名，获取相应的 poi Workbook对象
     * @param dest
     * @return
     * @throws Throwable
     */
    public Workbook getWorkbookByFilename(String dest) throws Throwable {

        try (InputStream in = new FileInputStream(dest)) {
            Workbook book = null;
            String filetype = dest.substring(dest.lastIndexOf("."));

            if (".xls".equals(filetype)) {
                book = new HSSFWorkbook(in);
            } else if (".xlsx".equals(filetype)) {
                book = new XSSFWorkbook(in);
            } else {
                throw new Throwable("请上传excel文件！");
            }
            return book;
        } catch (Throwable e) {
            log.error(e.toString());
            return null;
        }
    }

    public void download(String fileDest, HttpServletResponse response) throws Throwable {

        byte[] buff = new byte[1024];
        log.info(fileDest);
        // 把数据流从磁盘文件搬移到 response 中
        try (OutputStream outputStream = response.getOutputStream();
         InputStream bis = new BufferedInputStream(new FileInputStream(new File(fileDest))))
       {
            // 读取filename
            int i = bis.read(buff);
            while (i != -1) {
                outputStream.write(buff, 0, buff.length);
                outputStream.flush();
                i = bis.read(buff);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Throwable("FileUtils 文件出错");
        }
    }

    /**
     * 创建 Excel 文件，根据表头{"", ""}和表内容数组 {{}, {}},
     * 返回文件路径 dest，文件名为filename。文件路径为 /static/excel/
     * @param title  excel 表头
     * @param values excel 表内容
     * @param filename 文件名
     * @return
     */
    public String generatExcel(List<String> title, List<ExGrade> values, String filename) throws IOException {
        //先生成存放数据的 Excel文件
        String filePath = runClassPath + File.separator + "static" + File.separator + "excel" + File.separator;
        File dest = new File(filePath + filename + ".xls");
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        if (!dest.exists()) {
            dest.createNewFile();
        }
        try (FileOutputStream fileOut = new FileOutputStream(dest)) {
            // 创建HSSFWorkbook对象
            HSSFWorkbook sheets = new HSSFWorkbook();
            // 建立sheet对象
            HSSFSheet sheet = sheets.createSheet("成绩表");
            // 在sheet里创建第一行，参数为行索引
            HSSFRow row = sheet.createRow(0);
            // 利用循环 放入表头值
            for (int i = 0; i < title.size(); i++) {
                row.createCell(i).setCellValue(title.get(i));
            }
            // 利用双重循环 放入表内容值
            for (int i = 0; i < values.size(); i++) {
                row = sheet.createRow(i + 1);
                // 根据实际传入的内容体 来修改强转实体类 和实体对象
                ExGrade value = values.get(i);
                row.createCell(0).setCellValue(value.getSno());
                row.createCell(1).setCellValue(value.getName());
                row.createCell(2).setCellValue(value.getCname());
                row.createCell(3).setCellValue(value.getPname());
                if (value.getScore() == null) {
                    row.createCell(4).setCellValue("");
                } else {
                    row.createCell(4).setCellValue(value.getScore());
                }
                row.createCell(5).setCellValue(value.getTerm());
            }
            sheets.write(fileOut);

        } catch (IOException e) {
            e.printStackTrace();
            log.error("生成 Excel 文件出错", e, values);
        }
        return dest.getPath();
    }
    /**
     * 创建 Excel 文件，根据表头{"", ""}和表内容数组 {{}, {}},
     * 返回文件路径 dest，文件名为filename。文件路径为 /static/excel/
     * @param title  excel 表头
     * @param values excel 表内容
     * @param filename 文件名
     * @return
     */
    public String generatlbExcel(List<String> title, List<Lbgrade> values, String filename) throws IOException {
        //先生成存放数据的 Excel文件
        String filePath = runClassPath + File.separator + "static" + File.separator + "excel" + File.separator;
        File dest = new File(filePath + filename + ".xls");
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        if (!dest.exists()) {
            dest.createNewFile();
        }
        try (FileOutputStream fileOut = new FileOutputStream(dest)) {
            // 创建HSSFWorkbook对象
            HSSFWorkbook sheets = new HSSFWorkbook();
            // 建立sheet对象
            HSSFSheet sheet = sheets.createSheet("成绩表");
            // 在sheet里创建第一行，参数为行索引
            HSSFRow row = sheet.createRow(0);
            // 利用循环 放入表头值
            for (int i = 0; i < title.size(); i++) {
                row.createCell(i).setCellValue(title.get(i));
            }
            // 利用双重循环 放入表内容值
            for (int i = 0; i < values.size(); i++) {
                row = sheet.createRow(i + 1);
                // 根据实际传入的内容体 来修改强转实体类 和实体对象
                Lbgrade value = values.get(i);
                row.createCell(0).setCellValue(value.getSno());
                row.createCell(1).setCellValue(value.getName());
                row.createCell(2).setCellValue(value.getCname());
                row.createCell(3).setCellValue(value.getPname());
                if (value.getScore() == null) {
                    row.createCell(4).setCellValue("");
                } else {
                    row.createCell(4).setCellValue(value.getScore());
                }
                row.createCell(5).setCellValue(value.getTerm());
            }
            sheets.write(fileOut);

        } catch (IOException e) {
            e.printStackTrace();
            log.error("生成 Excel 文件出错", e, values);
        }
        return dest.getPath();
    }
}

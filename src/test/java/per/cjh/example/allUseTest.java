package per.cjh.example;

import lombok.extern.slf4j.Slf4j;
import org.parboiled.support.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import per.cjh.example.utils.TransformOffice2Image;
import sun.util.resources.ga.LocaleNames_ga;

import java.util.List;

/**
 * @author cjh
 * @description: 用于通用的一些测试
 * @date 2020/5/12 17:03
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
class allUseTest {

    @Autowired
    private TransformOffice2Image office2Image;
    @Value("${file.save.location}")
    private String staticFileUrl;


//    @Test
    public void testOpenOffice() {
        String pdfPath;
        pdfPath = office2Image.transformOffice2Pdf("D:\\期末作业\\UML项目设计\\02班-3178907204-陈炯焕-书籍.docx", "D:\\期末作业\\UML项目设计\\transform");
        List<String> imgslocation = office2Image.transformPdf2Image(pdfPath, "D:\\期末作业\\UML项目设计\\transform\\img");
        StringBuilder strB = new StringBuilder();
        // 每个图片的路径都是绝对地址，要获取相对地址，而转换后的图片都是放在 transform\img 下
        int startIndex = imgslocation.get(0).indexOf("\\transform\\img");
        log.info(startIndex + ": " + imgslocation.get(0));
        startIndex += 14;
        for (int i = 0; i < imgslocation.size(); i++) {
            strB.append(imgslocation.get(i).substring(startIndex) + ",");
        }
        log.info(strB.toString());
    }
}

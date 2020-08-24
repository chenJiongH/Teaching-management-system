package per.cjh.example.utils;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class TransformOffice2Image {

    //日志打印，由于是非阻塞的，性能比<code>System.out.println();</code>要好
    private static final Logger logger = LoggerFactory.getLogger(TransformOffice2Image.class);

    /**
     * 将office文件转换为pdf文件
     *
     * @param officePath office文件的路径，
     * @param dirFolder 生成的pdf文件存放的文件夹
     * @return 生成的pdf文件路径
     */
    public String transformOffice2Pdf(String officePath, String dirFolder) {
        //创建一个目标文件夹来存放生成的文件
        String tempDirPath = dirFolder + "/";
        String pdfPath = null;
        Process process = null;
        OpenOfficeConnection connection = null;
        try {
            //强制创建文件夹，apache的io-commons工具类
            FileUtils.forceMkdir(new File(tempDirPath));
            pdfPath = tempDirPath + UUID.randomUUID() + ".pdf";
            //soffice.exe为OpenOffice的执行文件，端口8100最好不要改，需要提前启动 soffice
            String command = "C:/Program Files (x86)/OpenOffice 4/program/soffice.exe -headless -accept=\"socket,host=127.0.0.1,port=8100;urp;\"";
            //执行这条命令，相当于在Windows的cmd里面执行
            process = Runtime.getRuntime().exec(command);
            connection = new SocketOpenOfficeConnection("127.0.0.1", 8100);
            //建立连接
            connection.connect();
            DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
            logger.info("连接成功！");
            //开始转换
            logger.info(officePath +"\n" + pdfPath);
            converter.convert(new File(officePath), new File(pdfPath));
            logger.info("转换成功！");
        } catch (IOException e) {
            logger.error("Office转换异常", e);
        } finally {
            //最后别忘了关连接
            try {
                connection.disconnect();
            } catch (Throwable throwable) {
                logger.error("connection关闭异常", throwable);
            }
            try {
                process.destroy();
            } catch (Throwable throwable) {
                logger.error("process关闭异常", throwable);
            }
        }
        return pdfPath;
    }

    /**
     * @param pdfPath pdf文件路径
     * @param dirFolder 生成的图片存放文件夹
     * @return 各图片所在位置
     */
    public List<String> transformPdf2Image(String pdfPath, String dirFolder) {
        logger.info("开始pdf转图片！");
        // 创建一个文件夹来存放生成的文件
        String tempDirPath = dirFolder + "/" + UUID.randomUUID() + "/";
        PDDocument document = null;
        List<String> imgsLocation = new ArrayList<>();
        try {
            FileUtils.forceMkdir(new File(tempDirPath));
            //加载Pdf文件
            document= PDDocument.load(new File(pdfPath));
            PDFRenderer renderer = new PDFRenderer(document);
            if (document != null) {
                PDPageTree pages = document.getPages();
                int startPage = 0;
                int len = pages.getCount();
                if (startPage < len) {
                    for (int i = startPage; i < len; i++) {
                        StringBuilder strB = new StringBuilder();
                        //按照页码生成图片名
                        strB.append(tempDirPath).append("page-").append(i + 1).append(".png");
                        String imagePath = strB.toString();
                        // 存放每个图片的路径
                        imgsLocation.add(imagePath);
                        BufferedImage image = renderer.renderImage(i, 3f);
                        ImageIO.write(image, "png", new File(imagePath));
                    }
                }
            }
            return imgsLocation;
        } catch (IOException e) {
            logger.error("转换PDF异常",e);
            return null;
        } finally {
            try {
                //最后别忘了关文档
                document.close();
            } catch (IOException e) {
                logger.error("关闭PDF异常",e);
            }
        }
    }
}

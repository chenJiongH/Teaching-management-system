package per.cjh.example.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@ApiModel
@Table(name = "exnews")
@Data
@Accessors(chain = true)
public class ExNews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("新闻自增长 id")
    private Integer id;

    @ApiModelProperty("新闻标题")
    private String title;

    @ApiModelProperty("新闻内容")
    private String content;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty("新闻上传时间")
    private LocalDate uptime;

    @ApiModelProperty("新闻上传者，为当前管理员姓名")
    private String author;

    @ApiModelProperty("新闻250字概述，前端获取纯文本再发送")
    private String summary;

    private String category;

    @ApiModelProperty("新闻浏览次数")
    private Integer viewnum;

}
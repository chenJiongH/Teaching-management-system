package per.cjh.example.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.regex.Pattern;

@ApiModel
@Table(name = "exproject")
@Data
@Accessors(chain = true)
public class ExProject {
    @Id
    @ApiModelProperty("项目名")
    private String pname;

    @Id
    @ApiModelProperty("学期名")
    private String term;

    @Id
    @ApiModelProperty("课程名")
    private String cname;

    @ApiModelProperty("项目开始时间")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate start;

    @ApiModelProperty("项目结束时间")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate end;

}
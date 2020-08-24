package per.cjh.example.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@ApiModel
@Table(name = "exproblem")
@Data
@Accessors(chain = true)
public class ExProblem {
    @Id
    @ApiModelProperty("学期名")
    private String term;

    @Id
    @ApiModelProperty("课程名")
    private String cname;

    @Id
    @ApiModelProperty("项目名")
    private String pname;

    @Column(name = "onePro")
    @ApiModelProperty("第一个题目")
    private String onepro;

    @Column(name = "twoPro")
    @ApiModelProperty("第二个题目")
    private String twopro;

    @Column(name = "threePro")
    @ApiModelProperty("第三个题目")
    private String threepro;
}
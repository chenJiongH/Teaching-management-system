package per.cjh.example.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;

@ApiModel
@Table(name = "exsc")
@Data
@Accessors(chain = true)
public class ExSC {
    @Id
    @ApiModelProperty("学生学号")
    private String sno;

    @Id
    @ApiModelProperty("学生课程名")
    private String cname;

    @Id
    @ApiModelProperty("学期")
    private String term;

    @ApiModelProperty("学生密码")
    private String password;

    @ApiModelProperty("学生姓名")
    private String name;

    @ApiModelProperty("学生手机号")
    private String phone;
}
package per.cjh.example.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;

@ApiModel
@Table(name = "extc")
@Data
@Accessors(chain = true)
public class ExTC {
    @Id
    @ApiModelProperty("教师手机号")
    private String phone;

    @Id
    @ApiModelProperty("教师课程名")
    private String cname;

    @Id
    @ApiModelProperty("教师学期名")
    private String term;

    @ApiModelProperty("教师密码")
    private String password;

    @ApiModelProperty("教师姓名")
    private String name;

}
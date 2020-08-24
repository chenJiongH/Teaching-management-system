package per.cjh.example.domain;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "exgrade")
@Data
@Accessors(chain = true)
@ApiModel("(Grade)评分表，存放所有学生的项目答题答案和对应的分数")
public class ExGrade {
    @Id
    private String sno;

    @Id
    private String term;

    @Id
    private String cname;

    @Id
    private String pname;

    private String password;

    private String name;


    @Column(name = "oneAn")
    private String onean;

    @Column(name = "twoAn")
    private String twoan;

    @Column(name = "threeAn")
    private String threean;

    private Float score;

}
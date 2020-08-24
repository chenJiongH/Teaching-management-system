package per.cjh.example.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
import java.time.LocalDate;

/**
 * @author cjh
 * @description: 包括了理论课的项目 和项目下的题目
 * @date 2020/6/3 7:40
 */
@Slf4j
@Data
@Accessors(chain = true)
public class LbProjectAndProblem {

    @Id
    private String term;

    @Id
    private String pname;

    @Id
    private String cname;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate start;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate end;

    private Integer number;

    private Lbproblem lbproblem;

}

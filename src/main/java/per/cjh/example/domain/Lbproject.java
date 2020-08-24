package per.cjh.example.domain;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import javax.persistence.*;

@Table(name = "lbproject")
@Data
@Accessors(chain = true)
@Component
@ApiModel
public class Lbproject {
    @Id
    private String pname;

    @Id
    private String term;

    @Id
    private String cname;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate start;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate end;

    private Integer number;

}
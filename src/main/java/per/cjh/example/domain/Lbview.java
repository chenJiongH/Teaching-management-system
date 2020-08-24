package per.cjh.example.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Table(name = "lbview")
@Data
@Accessors(chain = true)
public class Lbview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vname;

    private String path;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate uptime;

    private String cname;

    private String term;

    private String author;

    private String title;

    private String summary;

    private Integer level;
}
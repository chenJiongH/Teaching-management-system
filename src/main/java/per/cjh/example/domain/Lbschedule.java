package per.cjh.example.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;
import javax.persistence.*;

@Table(name = "lbschedule")
@Data
@Accessors(chain = true)
public class Lbschedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sname;

    private String path;

    private String size;

    private String author;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate uptime;

    private String category;

    private String term;

    private String cname;

    private Integer level;
}
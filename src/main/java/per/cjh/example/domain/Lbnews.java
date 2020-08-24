package per.cjh.example.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;
import javax.persistence.*;

@Table(name = "lbnews")
@Data
@Accessors(chain = true)
public class Lbnews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate uptime;

    private String author;

    private String summary;

    private String term;

    private String cname;

    private Integer level;
}
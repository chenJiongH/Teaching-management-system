package per.cjh.example.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;
import javax.persistence.*;

@Table(name = "lbvideo")
@Data
@Accessors(chain = true)
public class Lbvideo {
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
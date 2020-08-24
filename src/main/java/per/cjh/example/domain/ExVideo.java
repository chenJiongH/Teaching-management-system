package per.cjh.example.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDate;

@Table(name = "exvideo")
@Data
@Accessors(chain = true)
@Component
public class ExVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String vname;

    private String path;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Column(name = "upTime")
    private LocalDate uptime;

    private String size;

    private String author;

    private String title;

    private String category;

    private String summary;

}
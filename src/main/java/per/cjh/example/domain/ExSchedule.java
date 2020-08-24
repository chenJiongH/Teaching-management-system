package per.cjh.example.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Table(name = "exSchedule")
@Data
@Accessors(chain = true)
@Component
public class ExSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String sname;

    private String path;

    private String size;

    private String author;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate uptime;

    private String category;
}
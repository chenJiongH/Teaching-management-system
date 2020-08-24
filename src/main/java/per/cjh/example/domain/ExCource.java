package per.cjh.example.domain;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "excource")
@Data
@Accessors(chain = true)
public class ExCource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String cname;

    @Id
    private String term;

}
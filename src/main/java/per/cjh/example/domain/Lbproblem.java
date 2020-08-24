package per.cjh.example.domain;

import io.swagger.models.auth.In;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Table(name = "lbproblem")
@Data
@Accessors(chain = true)
public class Lbproblem {
    @Id
    private String term;

    @Id
    private String pname;

    @Id
    private String cname;

    private Integer number;

    private String one;

    private String two;

    private String three;

    private String four;

    private String five;

    private String six;

    private String seven;

    private String eight;

    private String nine;

    private String ten;

    private String eleven;

    private String twelve;

    private String thirteen;

    private String fourteen;

    private String fifteen;

    private String plaintext;

}
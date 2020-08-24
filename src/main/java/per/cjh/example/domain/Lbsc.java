package per.cjh.example.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Table(name = "lbsc")
@Data
@Accessors(chain = true)
public class Lbsc {
    @Id
    private String sno;

    @Id
    private String cname;

    @Id
    private String term;

    private String password;

    private String name;

    private String phone;


}
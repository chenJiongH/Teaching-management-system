package per.cjh.example.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "lbteacher")
@Data
@Accessors(chain = true)
public class Lbteacher {
    @Id
    private String term;

    @Id
    private String phone;

    private String password;

    private String name;

}
package per.cjh.example.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Table(name = "lbtc")
@Data
@Accessors(chain = true)
public class Lbtc {
    @Id
    private String term;

    @Id
    private String cname;

    @Id
    private String phone;

    private String name;

}
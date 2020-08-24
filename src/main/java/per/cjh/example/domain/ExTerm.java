package per.cjh.example.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "exterm")
@Data
@Accessors(chain = true)
public class ExTerm {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private String term;

}
package per.cjh.example.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Table(name = "lbgrade")
@Data
@Accessors(chain = true)
public class Lbgrade {
    @Id
    private String sno;

    @Id
    private String term;

    @Id
    private String cname;

    @Id
    private String pname;

    private String password;

    private String name;

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

    private String anone;

    private String antwo;

    private String anthree;

    private String anfour;

    private String anfive;

    private String ansix;

    private String anseven;

    private String aneight;

    private String annine;

    private String anten;

    private String aneleven;

    private String antwelve;

    private String anthirteen;

    private String anfourteen;

    private String anfifteen;

    private String score;



}
package per.cjh.example.domain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author cjh
 * @description: TODO
 * @date 2020/5/16 9:42
 */
@Slf4j
@Data
public class Objects {
    List<ExGrade> grades;
}

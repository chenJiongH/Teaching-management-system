package per.cjh.example.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @description: TODO
 * @date 2020/5/4 14:54
 */
@Data
@Accessors(chain = true)
public class User {
    private String username;
    private String password;
    private String name;
    private String phone;
}

package per.cjh.example.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author cjh
 * @description: TODO
 * @date 2020/5/29 7:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CommonRet {
    String code;
    String message;
    Object json;

    CommonRet(String code, String message) {
        this.code = code;
        this.message = message;
        this.json = null;
    }
}

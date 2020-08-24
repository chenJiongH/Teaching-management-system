package per.cjh.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.*;

/**
 * @author cjh
 * @description: Swagger2 生成接口文档
 * @date 2020/5/7 7:04
 */
@Slf4j
@Configuration
@EnableSwagger2
public class Swagger2Config {
    @Bean
    public Docket createRestApi() {

        return new Docket(DocumentationType.SWAGGER_2).
                     // api 文档相关个性化信息，具体信息在 apiInfo函数内部指定
                     apiInfo(apiInfo()).
                     pathMapping("/").
                     select().
                     // 扫描 controller 层的所有包
                     apis(RequestHandlerSelectors.basePackage("per.cjh.example.controller")).
                     // 可以根据url路径设置哪些请求加入文档，忽略哪些请求
                     paths(PathSelectors.any()).
                     build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title("福州大学化学学院项目 API 开发文档")
                //创建人
                .contact(new Contact("Mr. Chen", "https://blog.csdn.net/fujuacm", "chenjionghuan@qq.com"))
                //描述
                .description("简单优雅的 RESTFul 风格")
                //版本号
                .version("1.0")
                .build();
    }

}

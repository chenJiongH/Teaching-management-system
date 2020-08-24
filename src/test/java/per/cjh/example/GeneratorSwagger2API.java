package per.cjh.example;

import io.github.swagger2markup.GroupBy;
import io.github.swagger2markup.Language;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;
import java.nio.file.Paths;


/**
 * @author cjh
 * @description: 用于生成各种格式的 API 接口文档
 * @date 2020/5/4 20:44
 */
@RunWith(SpringRunner.class)
@SpringBootTest
class GeneratorSwagger2API {

	/**
	 * 生成AsciiDocs格式文档
	 * @throws Exception
	 */
	@Test
	public void generateAsciiDocs() throws Exception {
		//    输出Ascii格式
		Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
				.withMarkupLanguage(MarkupLanguage.ASCIIDOC)
				.withOutputLanguage(Language.ZH)
				.withPathsGroupedBy(GroupBy.TAGS)
				.withGeneratedExamples()
				.withoutInlineSchema()
				.build();
		//从 http://localhost:8080/v2/api-docs 该 URL 中生成 API 文档，所以工程必须先运行起来。
		Swagger2MarkupConverter.from(new URL("http://localhost:8080/v2/api-docs"))
				.withConfig(config)
				.build()
				// 文档生成后所在的文件夹
				.toFolder(Paths.get("./docs/asciidoc/generated"));
	}

	/**
	 * 生成Markdown格式文档
	 * @throws Exception
	 */
	@Test
	public void generateMarkdownDocs() throws Exception {
		//    输出Markdown格式
		Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
				.withMarkupLanguage(MarkupLanguage.MARKDOWN)
				.withOutputLanguage(Language.ZH)
				.withPathsGroupedBy(GroupBy.TAGS)
				.withGeneratedExamples()
				.withoutInlineSchema()
				.build();

		Swagger2MarkupConverter.from(new URL("http://localhost:8080/v2/api-docs"))
				.withConfig(config)
				.build()
                // 生成指定目录、和文件
				.toFolder(Paths.get("./docs/markdown/generated"));
	}

	/**
	 * 生成Confluence格式文档
	 * @throws Exception
	 */
	@Test
	public void generateConfluenceDocs() throws Exception {
		//    输出Confluence使用的格式
		Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
				.withMarkupLanguage(MarkupLanguage.CONFLUENCE_MARKUP)
				.withOutputLanguage(Language.ZH)
				.withPathsGroupedBy(GroupBy.TAGS)
				.withGeneratedExamples()
				.withoutInlineSchema()
				.build();

		Swagger2MarkupConverter.from(new URL("http://localhost:8080/v2/api-docs"))
				.withConfig(config)
				.build()
				.toFolder(Paths.get("./docs/confluence/generated"));
	}

	/**
	 * 生成AsciiDocs格式文档,并汇总成一个文件
	 * @throws Exception
	 */
	@Test
	public void generateAsciiDocsToFile() throws Exception {
		//    输出Ascii到单文件
		Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
				.withMarkupLanguage(MarkupLanguage.ASCIIDOC)
				.withOutputLanguage(Language.ZH)
				.withPathsGroupedBy(GroupBy.TAGS)
				.withGeneratedExamples()
				.withoutInlineSchema()
				.build();

		Swagger2MarkupConverter.from(new URL("http://localhost:8080/v2/api-docs"))
				.withConfig(config)
				.build()
				.toFile(Paths.get("./docs/asciidoc/generated/all"));
	}

	/**
	 * 生成Markdown格式文档,并汇总成一个文件
	 * @throws Exception
	 */
	@Test
	public void generateMarkdownDocsToFile() throws Exception {
		//    输出Markdown到单文件
		Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
				.withMarkupLanguage(MarkupLanguage.MARKDOWN)
				.withOutputLanguage(Language.ZH)
				.withPathsGroupedBy(GroupBy.TAGS)
				.withGeneratedExamples()
				.withoutInlineSchema()
				.build();

		Swagger2MarkupConverter.from(new URL("http://localhost:8080/v2/api-docs"))
				.withConfig(config)
				.build()
				.toFile(Paths.get("./docs/markdown/generated/all"));
	}
}

package per.cjh.example;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author cjh
 * @description: TODO
 * @date 2020/5/5 9:01
 */
@Slf4j
public class LocalTime {
    @Test
    public void testSimpleDateFormat() throws Throwable {
        // 格式转换器
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        // 模拟多线程
        Callable<LocalDate> task = new Callable<LocalDate>() {
            @Override
            public LocalDate call() throws Exception {
                return LocalDate.parse("20161218", dtf);
            }
        };

        ExecutorService pool = Executors.newFixedThreadPool(10);

        List<Future<LocalDate>> results = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            results.add(pool.submit(task));
        }

        for (Future<LocalDate> future : results) {
            log.info(future.get().toString());
        }
    }

    @Test
    public void testLocalDateTime() {
        // 获取当前系统时间
        LocalDateTime ldt = LocalDateTime.now();
        log.info(ldt.toString());
        // 自己指定时间
        LocalDateTime ldt2 = LocalDateTime.of(2015, 10, 19, 13, 22, 33);
        log.info(ldt2.toString());
    }
}

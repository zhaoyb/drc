package com.ctrip.framework.drc.console;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;


/**
 * Created by mingdongli
 * 2019/10/30 上午10:59.
 */

/**
 * servlet注解， 可以扫描 @WebServlet、@WebFilter、@WebListener
 */
@ServletComponentScan
@SpringBootApplication
public class ConsoleApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ConsoleApplication.class);
    }

    /**
     * 控制台启动入口
     *
     * @param args
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder(ConsoleApplication.class).run(args);
    }
}

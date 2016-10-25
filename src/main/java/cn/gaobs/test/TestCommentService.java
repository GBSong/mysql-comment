package cn.gaobs.test;

import cn.gaobs.service.CommentService;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaobs on 2016/10/14.
 */
public class TestCommentService {
    private String filePath = "/conf/resourcemanage_dev.properties";
    //private String filePath = "/conf/resourcemanage_test.properties";
    //private String filePath = "/conf/resourceservice_dev.properties";
    //private String filePath = "/conf/resourceservice_test.properties";
    private String contextPath = "/mainCtx.xml";

    @Test
    public void testAddComment(){
        AbstractApplicationContext app = new ClassPathXmlApplicationContext (contextPath);
        CommentService commentService = (CommentService) app.getBean("commentService");
        System.out.println("容器初始化完毕");

        List<String> tableSchemas = new ArrayList<>();
        tableSchemas.add("resourcemanage_dev");
        tableSchemas.add("resourcemanage_test");
        tableSchemas.add("resourceservice_dev");
        tableSchemas.add("resourceservice_test");

        commentService.addComment(tableSchemas,filePath,"UTF-8");
        System.out.println("注释添加完毕");
        app.destroy();
    }
}

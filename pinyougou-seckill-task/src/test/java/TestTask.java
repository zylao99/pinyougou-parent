import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package PACKAGE_NAME *
 * @since 1.0
 */
@ContextConfiguration("classpath:spring/applicationContext-task.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class TestTask {

    @Test
    public void tesss(){
        System.out.println("afdsafa");
        try {
            Thread.sleep(100000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

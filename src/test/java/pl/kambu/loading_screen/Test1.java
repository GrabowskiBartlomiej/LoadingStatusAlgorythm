package pl.kambu.loading_screen;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.kambu.loading_screen.service.LoadingCoreServiceImpl;

@SpringBootTest
public class Test1 {

    @Autowired
    private LoadingCoreServiceImpl service;

    @BeforeEach
    public void prepare(){
        System.out.println("Zaczynamy nowy test...");
    }

    @Test
    void test() throws InterruptedException {
        Thread start = new Thread(new Runnable() {
            @Override
            public void run() {
                service.start();
            }
        });
        start.start();
        Thread.sleep(100000);

        Thread end = new Thread(new Runnable() {
            @Override
            public void run() {
                service.end();
            }
        });
        end.start();

        Assertions.assertEquals(5+2, 7);
    }

}

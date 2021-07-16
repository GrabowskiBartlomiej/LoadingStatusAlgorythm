package pl.kambu.loading_screen;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.kambu.loading_screen.commons.ServiceStatus;
import pl.kambu.loading_screen.commons.Status;
import pl.kambu.loading_screen.commons.UpdateStatus;
import pl.kambu.loading_screen.model.Update;
import pl.kambu.loading_screen.service.FileManager;
import pl.kambu.loading_screen.service.FileManagerImpl;
import pl.kambu.loading_screen.service.LoadingCoreServiceImpl;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@SpringBootTest
public class Test1 {

        private final LoadingCoreServiceImpl service = new LoadingCoreServiceImpl();

        private final FileManagerImpl fileManager= new FileManagerImpl();


    @BeforeEach
    public void prepare(){
        System.out.println("Zaczynamy nowy test...");

    }

    @Test
    void statusOnlineOnFinishedUpdate() throws InterruptedException {
        Thread start = new Thread(new Runnable() {
            @Override
            public void run() {
                service.start();
            }
        });
        start.start();

        Long time = ((service.getAverageUpdateTime(service.fileManager.getAllPreviousUpdatesFromFile())*100) / 2);
        Thread.sleep(time);

        Thread end = new Thread(new Runnable() {
            @Override
            public void run() {
                service.end();
            }
        });
        end.start();

        Thread.sleep(time/100);

        Assertions.assertEquals(ServiceStatus.ONLINE, service.updateStatus.getServiceStatus() );
    }


    @Test
    void areAllStatusesDoneAtTheEnd() throws InterruptedException {
        Thread start = new Thread(new Runnable() {
            @Override
            public void run() {
                service.start();
            }
        });
        start.start();

        Long time = ((service.getAverageUpdateTime(service.fileManager.getAllPreviousUpdatesFromFile())*100) / 2);
        Thread.sleep(time);

        Thread end = new Thread(new Runnable() {
            @Override
            public void run() {
                service.end();
            }
        });
        end.start();

        Thread.sleep(time/100);

        LinkedHashMap<UpdateStatus, Status> stepsStatus = service.updateStatus.getStepsStatus();
        Set<UpdateStatus> keys = stepsStatus.keySet();
        for(UpdateStatus entry : keys) {
            Assertions.assertEquals(Status.DONE, stepsStatus.get(entry));
        }

    }

    @Test
    void fileExists() {

        FileManager.LoadingConfiguration config = new FileManager.LoadingConfiguration();
        String path = config.getFilePath();
        String fileName = config.getFileName();
        File file = new File(path, fileName);
        Assertions.assertTrue(file.exists());
    }

    @Test
    void fileIsNotEmpty() {
        List<Update> allPreviousUpdatesFromFile = fileManager.getAllPreviousUpdatesFromFile();
        Assertions.assertTrue(allPreviousUpdatesFromFile.size() > 0);
    }


}


package pl.kambu.loading_screen.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kambu.loading_screen.commons.ServiceStatus;
import pl.kambu.loading_screen.dto.UpdateStatusDTO;
import pl.kambu.loading_screen.service.LoadingCoreServiceImpl;

@RestController
@RequiredArgsConstructor
public class Controller {

    private final LoadingCoreServiceImpl service = new LoadingCoreServiceImpl();

    @GetMapping("/hello")
    public String hello() throws InterruptedException {
        Thread start = new Thread(new Runnable() {
            @Override
            public void run() {
                service.start();
            }
        });
        start.start();
        Thread.sleep(10000);

        Thread end = new Thread(new Runnable() {
            @Override
            public void run() {
                service.end();
            }
        });
        start.interrupt();
        end.start();
        return "Hello";
    }
    @GetMapping("/update-status")
    public UpdateStatusDTO getStatus() {
        return service.updateStatus;
    }

}

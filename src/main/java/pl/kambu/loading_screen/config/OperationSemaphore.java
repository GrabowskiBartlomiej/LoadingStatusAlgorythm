package pl.kambu.loading_screen.config;

import java.util.concurrent.Semaphore;

public class OperationSemaphore {

    private Semaphore operationSemaphore = new Semaphore(1);

    public void end() {
        operationSemaphore.release();
    }

    public void acquire() throws InterruptedException {
        try {
            operationSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Boolean canAcquire() {
        return operationSemaphore.tryAcquire();
    }

}

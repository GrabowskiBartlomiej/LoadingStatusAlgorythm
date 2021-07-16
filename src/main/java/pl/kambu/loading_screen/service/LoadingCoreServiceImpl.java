package pl.kambu.loading_screen.service;

import lombok.extern.slf4j.Slf4j;
import pl.kambu.loading_screen.commons.ServiceStatus;
import pl.kambu.loading_screen.commons.Status;
import pl.kambu.loading_screen.config.OperationSemaphore;
import pl.kambu.loading_screen.model.Update;
import pl.kambu.loading_screen.dto.UpdateStatusDTO;
import pl.kambu.loading_screen.commons.UpdateStatus;

import java.text.SimpleDateFormat;
import java.util.*;


@Slf4j
public class LoadingCoreServiceImpl implements LoadingCoreService {

    private Long nextId;
    private boolean isUpdating = false;
    private Long updateStartTime;
    private Long updateFinishTime;
    public ServiceStatus currentServiceStatus = ServiceStatus.ONLINE;
    public UpdateStatusDTO updateStatus = new UpdateStatusDTO();
    public FileManagerImpl fileManager = new FileManagerImpl();
    private final OperationSemaphore semaphore = new OperationSemaphore();

    public LoadingCoreServiceImpl() {
    }

    @Override
    public void start() {
        isUpdating = true;
        currentServiceStatus = ServiceStatus.UPDATING;
        updateStatus.setServiceStatus(currentServiceStatus);
        log.info("Update has started...");
        List<Update> updates = fileManager.getAllPreviousUpdatesFromFile();
        List<UpdateStatus> statuses = Arrays.asList(UpdateStatus.values());
        int statusCounter = 0;
        long progressDelay = getAverageUpdateTime(updates) * 10;
        Long progressCounter = 0L;
        nextId = updates.get(0).getId() + 1;
        UpdateStatus currentStatus = statuses.get(statusCounter);
        UpdateStatus nextStatus = statuses.get(statusCounter + 1);
        updateStartTime = System.currentTimeMillis();
        LinkedHashMap<UpdateStatus, Status> stepStatus = new LinkedHashMap();
        for (UpdateStatus status : statuses) {
            stepStatus.put(status, Status.TODO);
        }
        stepStatus.put(UpdateStatus.READY, Status.ONGOING);
        updateStatus.setStepsStatus(stepStatus);
        while (isUpdating) {
            try {
                Thread.sleep(progressDelay);
                if (!(isUpdating)) {
                    break;
                }

                semaphore.acquire();
                if (progressCounter > 100) {
                    if (progressCounter >= nextStatus.getPercentage() && nextStatus.getPercentage() != -1) {
                        stepStatus.put(currentStatus, Status.DONE);
                        stepStatus.put(nextStatus, Status.ONGOING);
                        updateStatus.setStepsStatus(stepStatus);
                        statusCounter++;
                        currentStatus = nextStatus;
                        nextStatus = statuses.get(statusCounter + 1);
                    }
                    updateStatus.setPercentage(100L);
                    updateStatus.setDescription(UpdateStatus.LASTS_LONGER_THAN_EXPECTED.getDescription());
//                    log.info(String.format("Current Status : %s , Progress : %s%n", currentStatus.getDescription(), 100));
                } else {
                    updateStatus.setDescription(currentStatus.getDescription());
                    updateStatus.setPercentage(progressCounter);
//                    log.info(String.format("Current Status : %s , Progress : %s%n", currentStatus.getDescription(), progressCounter));
                }
                progressCounter++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.end();
            }
        }
    }

    @Override
    public void end() {

        updateFinishTime = System.currentTimeMillis();
        updateStatus.setDescription(UpdateStatus.FINISHED.getDescription());
        updateStatus.setPercentage(100L);
        currentServiceStatus = ServiceStatus.ONLINE;
        updateStatus.setServiceStatus(currentServiceStatus);
        LinkedHashMap<UpdateStatus, Status> stepStatus = updateStatus.getStepsStatus();
        Set<UpdateStatus> keys = stepStatus.keySet();
        for (UpdateStatus entry : keys) {
            stepStatus.put(entry, Status.DONE);
        }
        isUpdating = false;
        log.info(String.format("Current Status : %s , Progress : %s%n", UpdateStatus.FINISHED.getDescription(), 100));
        log.info("Update ended, saving data about it to the file...");
        fileManager.savingNewUpdateToFileOnTheTopOfIt(createNewUpdateObject());
    }

    @Override
    public boolean isUpdating() {
        return isUpdating;
    }

    public Long getAverageUpdateTime(List<Update> previousUpdates) {
        log.info("Getting the average update time...");
        Long avg = 0L;
        for (Update update : previousUpdates) {
            avg += update.getDuration();
        }
        return avg / previousUpdates.size();
    }

    public Update createNewUpdateObject() {
        log.info("Creating an Update object of the update just executed...");
        Update update = new Update();
        update.setId(nextId);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        update.setUpdateDate(sdf.format(new Date()));
        Long duration = (updateFinishTime - updateStartTime) / 1000;
        update.setDuration(duration);
        return update;
    }

}

package pl.kambu.loading_screen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.stereotype.Service;
import pl.kambu.loading_screen.model.ServiceStatusEnum;
import pl.kambu.loading_screen.model.Update;
import pl.kambu.loading_screen.model.UpdateStatus;
import pl.kambu.loading_screen.model.UpdateStatusEnum;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


@RequiredArgsConstructor
@Service
@Slf4j
public class LoadingCoreServiceImpl implements LoadingCoreService {

    private Long nextId;
    private boolean isUpdating = false;
    private Long updateStartTime;
    private Long updateFinishTime;
    public ServiceStatusEnum currentServiceStatus = ServiceStatusEnum.ONLINE;
    public UpdateStatus updateStatus = new UpdateStatus();

    @Override
    public void start() {
        isUpdating = true;
        currentServiceStatus = ServiceStatusEnum.UPDATING;
        log.info("Update has started...");
        List<Update> updates = getAllPreviousUpdatesFromFile();

        try {
            List<UpdateStatusEnum> statuses = Arrays.asList(UpdateStatusEnum.values());
            int statusCounter = 0;
            UpdateStatusEnum status = statuses.get(statusCounter);
            updateStartTime = System.currentTimeMillis();
            long progressDelay = getAverageUpdateTime(updates) * 10;
            for (int i = 0; i <= 999; i++) {
                if (!(isUpdating)) {
                    break;
                }
                Thread.sleep(progressDelay);
                if (i >= status.getPercentage()) {
                    statusCounter++;
                    status = statuses.get(statusCounter);
                }

                if (i > 100) {
                    updateStatus.setPercentage(100L);
                    updateStatus.setDescription(UpdateStatusEnum.LASTS_LONGER_THAN_EXPECTED.getDescription());
                    System.out.printf("Current Status : %s , Progress : %s%n", status.getDescription(), 100);
                } else {
                    updateStatus.setDescription(status.getDescription());
                    updateStatus.setPercentage((long) i);
                    System.out.printf("Current Status : %s , Progress : %s%n", status.getDescription(), i);
                }


            }
        } catch (InterruptedException e) {
            Thread.interrupted();
        }

    }

    @Override
    public void end() {
        updateFinishTime = System.currentTimeMillis();
        updateStatus.setDescription(UpdateStatusEnum.FINISHED.getDescription());
        updateStatus.setPercentage(100L);
        System.out.printf("Current Status : %s , Progress : %s%n", UpdateStatusEnum.FINISHED.getDescription(), 100);
        log.info("Update ended, saving data about it to the file...");
        currentServiceStatus = ServiceStatusEnum.ONLINE;
        isUpdating = false;
        savingNewUpdateToFileOnTheTopOfIt(createNewUpdateObject());
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


    public List<Update> getAllPreviousUpdatesFromFile() {
        log.info("Loading all previous updates from file...");
        String line;
        List<Update> updates = new ArrayList<>();
        try {
            File previousUpdatesFile = new File("src/main/resources/previous_updates_data.txt");
            if (previousUpdatesFile.createNewFile()) {
                // Na sztywno pierwsze dane od Kulczaka
            }
            BufferedReader reader = new BufferedReader(new FileReader(previousUpdatesFile));
            log.info("Transforming file data into previous Update objects...");
            while ((line = reader.readLine()) != null) {
                updates.add(transformFileLineIntoUpdateInstance(line));
            }

            nextId = updates.get(0).getId() + 1;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return updates;
    }


    public Update transformFileLineIntoUpdateInstance(String line) {
        List<String> data = Arrays.asList(line.split(";"));
        Update update = new Update();
        Long id = Long.parseLong(data.get(0).substring(data.get(0).lastIndexOf("=") + 1));
        String updateDate = (data.get(1).substring((data.get(1).lastIndexOf("=") + 1)));
        Long durationInSeconds = Long.parseLong(data.get(2).substring(data.get(2).lastIndexOf("=") + 1));

        update.setId(id);
        update.setUpdateDate(updateDate);
        update.setDuration(durationInSeconds);
        return update;
    }


    public void savingNewUpdateToFileOnTheTopOfIt(Update update) {
        log.info("Saving the just executed update to the file...");
        try {
            File previousUpdatesFile = new File("src/main/resources/", "previous_updates_data.txt");
            previousUpdatesFile.createNewFile();

            File tempFile = File.createTempFile("src/main/resources/", ".tmp");

            FileWriter fw = new FileWriter(tempFile, true);
            BufferedWriter writer = new BufferedWriter(fw);
            LineIterator li = FileUtils.lineIterator(previousUpdatesFile);

            try {
                writer.write(String.format("id=%s;date=%s;duration=%s", update.getId(), update.getUpdateDate(), update.getDuration()));
                writer.write("\n");
                while (li.hasNext()) {
                    writer.write(li.nextLine());
                    writer.write("\n");
                }
            } finally {
                IOUtils.closeQuietly(writer);
                LineIterator.closeQuietly(li);
            }
            FileUtils.deleteQuietly(previousUpdatesFile);
            FileUtils.moveFile(tempFile, previousUpdatesFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("Update has been saved successfully!");
    }


    private Update createNewUpdateObject() {
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

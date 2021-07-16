package pl.kambu.loading_screen.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import pl.kambu.loading_screen.model.Update;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Slf4j
public class FileManagerImpl implements FileManager {

    private final LoadingConfiguration config = new LoadingConfiguration();

    @Override
    public List<Update> getAllPreviousUpdatesFromFile() {
        log.info("Loading all previous updates from file...");
        String line;
        List<Update> updates = new ArrayList<>();
        try {
            File previousUpdatesFile = new File(config.filePath, config.fileName);
            if (previousUpdatesFile.createNewFile()) {
                savingNewUpdateToFileOnTheTopOfIt(transformFileLineIntoUpdateInstance(config.approximatelyKnownFirstUpdate));
            }
            BufferedReader reader = new BufferedReader(new FileReader(previousUpdatesFile));
            log.info("Transforming file data into previous Update objects...");
            while ((line = reader.readLine()) != null && updates.size() < 10) {
                updates.add(transformFileLineIntoUpdateInstance(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return updates;
    }

    @Override
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

    @Override
    public void savingNewUpdateToFileOnTheTopOfIt(Update update) {
        log.info("Saving the just executed update to the file...");
        try {
            File previousUpdatesFile = new File(config.filePath, config.fileName);
            previousUpdatesFile.createNewFile();

            File tempFile = File.createTempFile(config.filePath, ".tmp");

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

}

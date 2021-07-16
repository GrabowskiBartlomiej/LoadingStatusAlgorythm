package pl.kambu.loading_screen.service;

import lombok.Getter;
import pl.kambu.loading_screen.model.Update;

import java.util.List;

public interface FileManager {

    List<Update> getAllPreviousUpdatesFromFile();

    Update transformFileLineIntoUpdateInstance(String line);

    void savingNewUpdateToFileOnTheTopOfIt(Update update);

    @Getter
    class LoadingConfiguration {

        String filePath = "src/main/resources/";
        String fileName = "previous_updates_data.txt";
        String approximatelyKnownFirstUpdate = " "; //to info musze dostac od Kuby Kulczaka

    }

}

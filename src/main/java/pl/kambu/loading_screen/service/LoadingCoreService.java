package pl.kambu.loading_screen.service;

import lombok.Getter;

public interface LoadingCoreService {

    void start();

    void end();

    boolean isUpdating();

}

package pl.kambu.loading_screen.model;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum UpdateStatus {
    READY("Przygotowanie do Aktualizacji" , 0L),
    DB_MIGRATION("Migracja bazy danych", 16L),
    VERIFICATION_OF_MIGRATION("Weryfikacja migracji", 55L),
    TURNING_ON_SERVICES("Uruchamianie serwisów", 67L),
    FINALISING_CHANGES("Finalizacja zmian", 91L),
    LASTS_LONGER_THAN_EXPECTED("Proces trwa dłużej niż zwykle, jeszcze chwila!", 100L),
    FINISHED("Aktualizacja zakończona", -1L);

    private final String description;
    private final Long percentage;

}

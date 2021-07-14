package pl.kambu.loading_screen.model;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum UpdateStatusEnum {
    READY("Przygotowanie do Aktualizacji" , 16),
    DB_MIGRATION("Migracja bazy danych", 36),
    VERIFICATION_OF_MIGRATION("Weryfikacja migracji", 42),
    TURNING_ON_SERVICES("Uruchamianie serwisów", 92),
    FINALISING_CHANGES("Finalizacja zmian", 101),
    LASTS_LONGER_THAN_EXPECTED("Proces trwa dłużej niż zwykle, jeszcze chwila!", 999),
    FINISHED("Aktualizacja zakończona", null);

    private final String description;
    private final Integer percentage;

}

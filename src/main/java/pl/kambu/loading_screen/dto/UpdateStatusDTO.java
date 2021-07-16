package pl.kambu.loading_screen.dto;

import lombok.*;
import pl.kambu.loading_screen.commons.ServiceStatus;
import pl.kambu.loading_screen.commons.Status;
import pl.kambu.loading_screen.commons.UpdateStatus;
import pl.kambu.loading_screen.model.Update;

import java.util.LinkedHashMap;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class UpdateStatusDTO {

    private ServiceStatus serviceStatus;
    private Long percentage;
    private String description;
    private Long percentage;

}

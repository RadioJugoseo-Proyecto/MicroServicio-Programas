package cl.radiojugoseo.programas.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProgramaRequest(

        @NotBlank(message = "El nombre del programa es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
        String descripcion,

        Long locutorId,

        @NotNull(message = "El día de la semana es obligatorio")
        DayOfWeek diaSemana,

        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime horaInicio,

        @NotNull(message = "La hora de término es obligatoria")
        LocalTime horaFin,

        Boolean activo) {

    @AssertTrue(message = "La hora de término debe ser posterior a la hora de inicio")
    public boolean isHorarioValido() {
        return horaInicio == null || horaFin == null
                || horaFin.isAfter(horaInicio);
    }
}

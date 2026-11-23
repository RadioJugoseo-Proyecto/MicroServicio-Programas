package cl.radiojugoseo.programas.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

import cl.radiojugoseo.programas.entity.Programa;

public record ProgramaResponse(
        Long id,
        String nombre,
        String descripcion,
        Long locutorId,
        DayOfWeek diaSemana,
        LocalTime horaInicio,
        LocalTime horaFin,
        boolean activo) {

    public static ProgramaResponse desde(Programa p) {
        return new ProgramaResponse(
                p.getId(), p.getNombre(), p.getDescripcion(),
                p.getLocutorId(), p.getDiaSemana(),
                p.getHoraInicio(), p.getHoraFin(), p.isActivo());
    }
}

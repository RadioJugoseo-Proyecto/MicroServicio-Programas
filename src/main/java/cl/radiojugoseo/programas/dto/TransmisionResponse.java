package cl.radiojugoseo.programas.dto;

import java.time.Instant;

import cl.radiojugoseo.programas.entity.EstadoTransmision;

public record TransmisionResponse(
        Long id,
        Long programaId,
        String programaNombre,
        EstadoTransmision estado,
        Instant fechaInicio,
        Instant fechaFin,
        int oyentesActuales,
        int oyentesPico,
        int progresoPorcentaje,
        long minutoSimulado,
        long duracionProgramaMinutos) {
}

package cl.radiojugoseo.programas.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.radiojugoseo.programas.dto.TransmisionResponse;
import cl.radiojugoseo.programas.entity.EstadoTransmision;
import cl.radiojugoseo.programas.entity.Programa;
import cl.radiojugoseo.programas.entity.Transmision;
import cl.radiojugoseo.programas.exception.RecursoNoEncontradoException;
import cl.radiojugoseo.programas.exception.ReglaNegocioException;
import cl.radiojugoseo.programas.repository.ProgramaRepository;
import cl.radiojugoseo.programas.repository.TransmisionRepository;

/**
 * Simulación de la transmisión de un programa.
 *
 * - Al iniciar, la transmisión queda EN_VIVO con una audiencia inicial.
 * - Cada cierto intervalo (SimulacionScheduler) la audiencia varía al azar.
 * - El tiempo corre "acelerado": 1 segundo real = factorVelocidad segundos
 *   simulados, así un programa de 1 hora puede verse completo en 1 minuto.
 * - Cuando se completa la duración del programa, se finaliza sola.
 */
@Service
public class TransmisionService {

    private final TransmisionRepository transmisionRepository;
    private final ProgramaRepository programaRepository;
    private final long factorVelocidad;
    private final int oyentesBase;
    private final Random random = new Random();

    public TransmisionService(
            TransmisionRepository transmisionRepository,
            ProgramaRepository programaRepository,
            @Value("${jugoseo.simulacion.factor-velocidad:60}") long factorVelocidad,
            @Value("${jugoseo.simulacion.oyentes-base:50}") int oyentesBase) {
        this.transmisionRepository = transmisionRepository;
        this.programaRepository = programaRepository;
        this.factorVelocidad = Math.max(1, factorVelocidad);
        this.oyentesBase = Math.max(1, oyentesBase);
    }

    @Transactional
    public TransmisionResponse iniciar(
            Long programaId, String usuarioId, String rol) {

        Programa programa = programaRepository.findById(programaId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un programa con id " + programaId));

        Long idUsuario = Long.valueOf(usuarioId);

        // Un Locutor solo puede transmitir sus propios programas
        if ("Locutor".equals(rol)
                && programa.getLocutorId() != null
                && !programa.getLocutorId().equals(idUsuario)) {
            throw new AccessDeniedException(
                    "Solo el locutor asignado puede transmitir este programa.");
        }

        if (!programa.isActivo()) {
            throw new ReglaNegocioException("El programa está inactivo.");
        }

        if (transmisionRepository.existsByProgramaIdAndEstado(
                programaId, EstadoTransmision.EN_VIVO)) {
            throw new ReglaNegocioException(
                    "El programa ya se está transmitiendo en vivo.");
        }

        int inicial = oyentesBase + random.nextInt(oyentesBase);

        Transmision t = new Transmision();
        t.setPrograma(programa);
        t.setEstado(EstadoTransmision.EN_VIVO);
        t.setFechaInicio(Instant.now());
        t.setOyentesActuales(inicial);
        t.setOyentesPico(inicial);
        t.setIniciadaPor(idUsuario);

        return aResponse(transmisionRepository.save(t));
    }

    @Transactional
    public TransmisionResponse finalizar(Long id) {
        Transmision t = buscar(id);

        if (t.getEstado() != EstadoTransmision.EN_VIVO) {
            throw new ReglaNegocioException(
                    "La transmisión ya está finalizada.");
        }

        cerrar(t);
        return aResponse(t);
    }

    @Transactional(readOnly = true)
    public List<TransmisionResponse> listar(EstadoTransmision estado) {
        List<Transmision> lista = (estado == null)
                ? transmisionRepository.listarConPrograma()
                : transmisionRepository.listarPorEstadoConPrograma(estado);

        return lista.stream().map(this::aResponse).toList();
    }

    @Transactional(readOnly = true)
    public TransmisionResponse obtener(Long id) {
        return aResponse(buscar(id));
    }

    // Un "tick" de la simulación (lo invoca SimulacionScheduler)
    @Transactional
    public void avanzarSimulacion() {
        List<Transmision> enVivo = transmisionRepository
                .listarPorEstadoConPrograma(EstadoTransmision.EN_VIVO);

        for (Transmision t : enVivo) {
            if (progreso(t) >= 100) {
                cerrar(t);
                continue;
            }

            // Variación aleatoria de la audiencia
            int delta = (int) Math.round(random.nextGaussian() * 4);
            int nuevos = Math.max(0, t.getOyentesActuales() + delta);

            t.setOyentesActuales(nuevos);
            t.setOyentesPico(Math.max(t.getOyentesPico(), nuevos));
        }
        // Los cambios se guardan solos al cerrar la transacción
    }

    // ---------- auxiliares ----------

    private void cerrar(Transmision t) {
        t.setEstado(EstadoTransmision.FINALIZADA);
        t.setFechaFin(Instant.now());
        t.setOyentesActuales(0);
    }

    private Transmision buscar(Long id) {
        return transmisionRepository.buscarConPrograma(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una transmisión con id " + id));
    }

    private long duracionProgramaMs(Programa p) {
        long ms = Duration.between(p.getHoraInicio(), p.getHoraFin()).toMillis();
        return Math.max(1, ms);
    }

    private long transcurridoSimuladoMs(Transmision t) {
        Instant fin = (t.getFechaFin() != null) ? t.getFechaFin() : Instant.now();
        return Duration.between(t.getFechaInicio(), fin).toMillis() * factorVelocidad;
    }

    private int progreso(Transmision t) {
        long pct = transcurridoSimuladoMs(t) * 100
                / duracionProgramaMs(t.getPrograma());
        return (int) Math.min(100, pct);
    }

    private TransmisionResponse aResponse(Transmision t) {
        Programa p = t.getPrograma();
        long duracionMs = duracionProgramaMs(p);
        long simMs = Math.min(transcurridoSimuladoMs(t), duracionMs);

        return new TransmisionResponse(
                t.getId(),
                p.getId(),
                p.getNombre(),
                t.getEstado(),
                t.getFechaInicio(),
                t.getFechaFin(),
                t.getOyentesActuales(),
                t.getOyentesPico(),
                progreso(t),
                simMs / 60_000,
                duracionMs / 60_000);
    }
}

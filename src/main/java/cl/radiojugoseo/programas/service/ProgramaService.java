package cl.radiojugoseo.programas.service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.radiojugoseo.programas.dto.ProgramaRequest;
import cl.radiojugoseo.programas.dto.ProgramaResponse;
import cl.radiojugoseo.programas.entity.Programa;
import cl.radiojugoseo.programas.exception.RecursoNoEncontradoException;
import cl.radiojugoseo.programas.exception.ReglaNegocioException;
import cl.radiojugoseo.programas.repository.ProgramaRepository;
import cl.radiojugoseo.programas.repository.TransmisionRepository;

@Service
public class ProgramaService {

    private final ProgramaRepository programaRepository;
    private final TransmisionRepository transmisionRepository;
    private final ZoneId zona;

    public ProgramaService(
            ProgramaRepository programaRepository,
            TransmisionRepository transmisionRepository,
            @Value("${jugoseo.zona-horaria:America/Santiago}") String zona) {
        this.programaRepository = programaRepository;
        this.transmisionRepository = transmisionRepository;
        this.zona = ZoneId.of(zona);
    }

    @Transactional(readOnly = true)
    public List<ProgramaResponse> listar() {
        return programaRepository.findAll().stream()
                .sorted(Comparator
                        .comparing(Programa::getDiaSemana)
                        .thenComparing(Programa::getHoraInicio))
                .map(ProgramaResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProgramaResponse obtener(Long id) {
        return ProgramaResponse.desde(buscar(id));
    }

    // Programas activos cuyo horario incluye la hora actual
    @Transactional(readOnly = true)
    public List<ProgramaResponse> alAire() {
        ZonedDateTime ahora = ZonedDateTime.now(zona);
        LocalTime hora = ahora.toLocalTime();

        return programaRepository
                .findByDiaSemanaAndActivoTrue(ahora.getDayOfWeek())
                .stream()
                .filter(p -> !hora.isBefore(p.getHoraInicio())
                          && hora.isBefore(p.getHoraFin()))
                .map(ProgramaResponse::desde)
                .toList();
    }

    @Transactional
    public ProgramaResponse crear(ProgramaRequest request) {
        Programa p = new Programa();
        aplicar(p, request);
        return ProgramaResponse.desde(programaRepository.save(p));
    }

    @Transactional
    public ProgramaResponse actualizar(Long id, ProgramaRequest request) {
        Programa p = buscar(id);
        aplicar(p, request);
        return ProgramaResponse.desde(programaRepository.save(p));
    }

    @Transactional
    public void eliminar(Long id) {
        Programa p = buscar(id);

        if (transmisionRepository.existsByProgramaId(id)) {
            throw new ReglaNegocioException(
                    "El programa tiene transmisiones asociadas; "
                  + "desactívelo en lugar de eliminarlo.");
        }

        programaRepository.delete(p);
    }

    private void aplicar(Programa p, ProgramaRequest r) {
        p.setNombre(r.nombre().trim());
        p.setDescripcion(r.descripcion());
        p.setLocutorId(r.locutorId());
        p.setDiaSemana(r.diaSemana());
        p.setHoraInicio(r.horaInicio());
        p.setHoraFin(r.horaFin());
        if (r.activo() != null) {
            p.setActivo(r.activo());
        }
    }

    private Programa buscar(Long id) {
        return programaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un programa con id " + id));
    }
}

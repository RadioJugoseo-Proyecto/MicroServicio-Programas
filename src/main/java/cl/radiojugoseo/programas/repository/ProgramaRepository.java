package cl.radiojugoseo.programas.repository;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.radiojugoseo.programas.entity.Programa;

public interface ProgramaRepository extends JpaRepository<Programa, Long> {
    List<Programa> findByDiaSemanaAndActivoTrue(DayOfWeek diaSemana);
}

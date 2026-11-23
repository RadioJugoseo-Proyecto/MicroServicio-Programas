package cl.radiojugoseo.programas.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cl.radiojugoseo.programas.entity.EstadoTransmision;
import cl.radiojugoseo.programas.entity.Transmision;

public interface TransmisionRepository extends JpaRepository<Transmision, Long> {

    boolean existsByProgramaIdAndEstado(Long programaId, EstadoTransmision estado);

    boolean existsByProgramaId(Long programaId);

    // join fetch: open-in-view está desactivado, el programa se carga aquí
    @Query("select t from Transmision t join fetch t.programa where t.id = :id")
    Optional<Transmision> buscarConPrograma(@Param("id") Long id);

    @Query("select t from Transmision t join fetch t.programa order by t.fechaInicio desc")
    List<Transmision> listarConPrograma();

    @Query("select t from Transmision t join fetch t.programa "
         + "where t.estado = :estado order by t.fechaInicio desc")
    List<Transmision> listarPorEstadoConPrograma(
            @Param("estado") EstadoTransmision estado);
}

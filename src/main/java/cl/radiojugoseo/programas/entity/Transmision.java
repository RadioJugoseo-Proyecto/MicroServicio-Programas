package cl.radiojugoseo.programas.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transmisiones")
public class Transmision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "programa_id", nullable = false)
    private Programa programa;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 15)
    private EstadoTransmision estado;

    @Column(name = "fecha_inicio", nullable = false)
    private Instant fechaInicio;

    @Column(name = "fecha_fin")
    private Instant fechaFin;

    @Column(name = "oyentes_actuales", nullable = false)
    private int oyentesActuales;

    @Column(name = "oyentes_pico", nullable = false)
    private int oyentesPico;

    @Column(name = "iniciada_por")
    private Long iniciadaPor;

    public Transmision() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Programa getPrograma() { return programa; }
    public void setPrograma(Programa programa) { this.programa = programa; }

    public EstadoTransmision getEstado() { return estado; }
    public void setEstado(EstadoTransmision estado) { this.estado = estado; }

    public Instant getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Instant fechaInicio) { this.fechaInicio = fechaInicio; }

    public Instant getFechaFin() { return fechaFin; }
    public void setFechaFin(Instant fechaFin) { this.fechaFin = fechaFin; }

    public int getOyentesActuales() { return oyentesActuales; }
    public void setOyentesActuales(int oyentesActuales) { this.oyentesActuales = oyentesActuales; }

    public int getOyentesPico() { return oyentesPico; }
    public void setOyentesPico(int oyentesPico) { this.oyentesPico = oyentesPico; }

    public Long getIniciadaPor() { return iniciadaPor; }
    public void setIniciadaPor(Long iniciadaPor) { this.iniciadaPor = iniciadaPor; }
}

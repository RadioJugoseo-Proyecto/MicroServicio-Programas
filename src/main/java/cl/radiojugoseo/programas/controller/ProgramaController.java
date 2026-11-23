package cl.radiojugoseo.programas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.radiojugoseo.programas.dto.ProgramaRequest;
import cl.radiojugoseo.programas.dto.ProgramaResponse;
import cl.radiojugoseo.programas.service.ProgramaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/programas")
public class ProgramaController {

    private final ProgramaService programaService;

    public ProgramaController(ProgramaService programaService) {
        this.programaService = programaService;
    }

    // Parrilla completa (público)
    @GetMapping
    public List<ProgramaResponse> listar() {
        return programaService.listar();
    }

    // Programas que, según el horario, están al aire ahora (público)
    @GetMapping("/ahora")
    public List<ProgramaResponse> alAire() {
        return programaService.alAire();
    }

    @GetMapping("/{id}")
    public ProgramaResponse obtener(@PathVariable Long id) {
        return programaService.obtener(id);
    }

    // Solo Administrador
    @PostMapping
    public ResponseEntity<ProgramaResponse> crear(
            @Valid @RequestBody ProgramaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(programaService.crear(request));
    }

    // Solo Administrador
    @PutMapping("/{id}")
    public ProgramaResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProgramaRequest request) {
        return programaService.actualizar(id, request);
    }

    // Solo Administrador
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        programaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

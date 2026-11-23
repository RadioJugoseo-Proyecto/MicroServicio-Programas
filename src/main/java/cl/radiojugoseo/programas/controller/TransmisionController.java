package cl.radiojugoseo.programas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.radiojugoseo.programas.dto.TransmisionResponse;
import cl.radiojugoseo.programas.entity.EstadoTransmision;
import cl.radiojugoseo.programas.service.TransmisionService;

@RestController
@RequestMapping("/api/v1")
public class TransmisionController {

    private final TransmisionService transmisionService;

    public TransmisionController(TransmisionService transmisionService) {
        this.transmisionService = transmisionService;
    }

    // Iniciar la transmisión simulada de un programa (Locutor / Administrador)
    @PostMapping("/programas/{programaId}/transmisiones")
    public ResponseEntity<TransmisionResponse> iniciar(
            @PathVariable Long programaId,
            @AuthenticationPrincipal Jwt jwt) {

        TransmisionResponse respuesta = transmisionService.iniciar(
                programaId,
                jwt.getSubject(),
                jwt.getClaimAsString("rol"));

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    // Listar transmisiones; ?estado=EN_VIVO para ver solo las activas (público)
    @GetMapping("/transmisiones")
    public List<TransmisionResponse> listar(
            @RequestParam(required = false) EstadoTransmision estado) {
        return transmisionService.listar(estado);
    }

    // Estado actual de una transmisión (público)
    @GetMapping("/transmisiones/{id}")
    public TransmisionResponse obtener(@PathVariable Long id) {
        return transmisionService.obtener(id);
    }

    // Finalizar manualmente (Locutor / Administrador)
    @PostMapping("/transmisiones/{id}/finalizar")
    public TransmisionResponse finalizar(@PathVariable Long id) {
        return transmisionService.finalizar(id);
    }
}

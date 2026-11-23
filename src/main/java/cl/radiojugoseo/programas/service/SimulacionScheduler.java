package cl.radiojugoseo.programas.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SimulacionScheduler {

    private final TransmisionService transmisionService;

    public SimulacionScheduler(TransmisionService transmisionService) {
        this.transmisionService = transmisionService;
    }

    @Scheduled(fixedDelayString = "${jugoseo.simulacion.intervalo-ms:5000}")
    public void tick() {
        transmisionService.avanzarSimulacion();
    }
}

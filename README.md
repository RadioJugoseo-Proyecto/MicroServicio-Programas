# Radio Jugoseo - Microservicio de Programas

Administra la parrilla de programas y la **simulación de transmisión**.
Spring Boot 4.1.2, Java 17, MySQL. Puerto `8082`, BD `radiojugoseo_programas`.

## Configuración
1. Ejecutar `database/schema.sql` en MySQL.
2. Variables de entorno: `DB_PASSWORD` y `JWT_SECRET`
   (**la misma** `JWT_SECRET` del microservicio de usuarios, porque este
   servicio solo valida los tokens que ese emite).
3. `.\mvnw.cmd spring-boot:run`

## API
| Método | Ruta | Acceso |
|---|---|---|
| GET | `/api/v1/programas` | público |
| GET | `/api/v1/programas/ahora` | público (según horario actual) |
| GET | `/api/v1/programas/{id}` | público |
| POST / PUT / DELETE | `/api/v1/programas[/{id}]` | Administrador |
| POST | `/api/v1/programas/{id}/transmisiones` | Locutor (solo el asignado) / Administrador |
| GET | `/api/v1/transmisiones?estado=EN_VIVO` | público |
| GET | `/api/v1/transmisiones/{id}` | público |
| POST | `/api/v1/transmisiones/{id}/finalizar` | Locutor / Administrador |

Ejemplo de programa:
```json
{ "nombre": "Mañanas Jugoseo", "descripcion": "Música y contingencia",
  "locutorId": 2, "diaSemana": "MONDAY",
  "horaInicio": "08:00", "horaFin": "10:00", "activo": true }
```

## Simulación
- Iniciar crea una transmisión `EN_VIVO` con audiencia inicial aleatoria.
- Un `@Scheduled` (cada `jugoseo.simulacion.intervalo-ms`) varía los oyentes y actualiza el pico.
- Tiempo acelerado: `jugoseo.simulacion.factor-velocidad=60` => 1 s real = 60 s simulados
  (un programa de 2 h se completa en 2 min). Al completarse pasa a `FINALIZADA` sola.
- La respuesta incluye `progresoPorcentaje`, `minutoSimulado`, `oyentesActuales` y `oyentesPico`.

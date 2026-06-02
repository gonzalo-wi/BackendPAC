# PAC — Plataforma de Auditoría de Cobranzas

Sistema interno de conciliación diaria de cobranzas. Compara lo depositado en **Minibank** contra lo esperado en **Aguas**, detecta diferencias y gestiona el cierre de repartos.

---

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Backend | Java 21, Spring Boot 3, PostgreSQL |
| Integraciones | Spring Cloud OpenFeign, Resilience4j |
| Mapeo | MapStruct, Lombok |
| Frontend | React 18, TypeScript, TanStack Query v5, shadcn/ui + Tailwind |

---

## Arquitectura

El backend sigue una arquitectura hexagonal con separación clara entre capas:

```
controller/       → HTTP layer (REST endpoints)
*/application/    → Use cases y DTOs de respuesta
*/domain/         → Entidades y puertos del dominio
*/infrastructure/ → Adapters JPA, Feign clients, Mappers
config/           → Configuración Spring (Feign, Async, OpenAPI)
```

### Módulos principales

- **plants** — operaciones por planta: refresh masivo, process-all, close-all, estadísticas
- **reconciliation** — conciliación minibank vs aguas por reparto
- **deposits** — depósitos, ajustes, cheques y retenciones
- **expected** — montos esperados sincronizados desde Aguas
- **integrations** — health check de integraciones externas
- **audit** — log de todas las acciones del sistema

---

## Requisitos

- Java 21
- PostgreSQL 14+
- Maven 3.9+

---

## Variables de entorno

Crear un archivo `.env` en la raíz del proyecto con:

```properties
# Base de datos
DB_HOST=localhost
DB_PORT=5432
DB_NAME=pac
DB_USER=postgres
DB_PASSWORD=

# Aguas
URL_BASE_AGUAS=http://<host>/service1.asmx
URL_BASE_AGUAS_COBRANZA=http://<host>/jmobile/service/cobranza

# Minibank
URL_BASE_MINIBANK=http://<host>
USERNAME_MINIBANK=
PASSWORD_MINIBANK=

# Identificadores de cajeros en Minibank (por cajero externo)
BANK_CIUDADELA_1=
BANK_CIUDADELA_2=
BANK_LA_PLATA_3=
BANK_LOMAS_DE_ZAMORA_4=
```

---

## Cómo correr localmente

```bash
# 1. Clonar el repositorio
git clone <repo-url>
cd pac

# 2. Configurar el .env en la raíz del proyecto

# 3. Compilar
mvn clean package -DskipTests

# 4. Correr
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`.  
La documentación Swagger en `http://localhost:8080/swagger-ui.html`.

---

## Flujo operativo

El cajero de tesorería trabaja por planta. El flujo diario es:

```
1. POST /api/plants/{plantId}/refresh?date=YYYY-MM-DD
   └─ Sync Minibank + Sync Aguas + crea y procesa todas las reconciliaciones

2. Revisar el consolidado
   └─ GET /api/plants/{plantId}/stats?date=YYYY-MM-DD

3. Resolver repartos "Pendientes" (cheques/retenciones)
   └─ Cargar ítems en los depósitos correspondientes

4. POST /api/plants/{plantId}/close-all?date=YYYY-MM-DD
   └─ Cierra en Aguas todos los repartos procesados
```

### Estados de una reconciliación

| Estado | Descripción |
|---|---|
| `PENDING` | Recién creada |
| `AWAITING_MANUAL_ITEMS` | Aguas espera cheques/retenciones que el cajero no cargó aún |
| `PROCESSED_WITH_SURPLUS` | Calculada — hay sobrante |
| `PROCESSED_WITH_SHORTAGE` | Calculada — hay faltante |
| `PROCESSED_WITHOUT_DIFFERENCE` | Calculada — sin diferencia |
| `INTEGRATION_ERROR` | Error al comunicarse con Aguas |
| `CLOSED` | Cerrada en Aguas — estado final |

---

## Endpoints principales

### Plantas
```
GET  /api/plants                              Lista plantas (Ciudadela, La Plata, Lomas)
POST /api/plants/{id}/refresh?date=           Actualiza todo: sync + process
POST /api/plants/{id}/process-all?date=       Recalcula todas las reconciliaciones abiertas
POST /api/plants/{id}/close-all?date=         Cierra en Aguas todos los PROCESSED_*
GET  /api/plants/{id}/stats?date=             Estadísticas de la planta
GET  /api/plants/stats?date=                  Estadísticas de todas las plantas
```

### Reconciliaciones
```
GET    /api/reconciliations?plantId=&date=    Lista por planta y fecha
GET    /api/reconciliations/{id}              Detalle
POST   /api/reconciliations                   Crear
POST   /api/reconciliations/{id}/process      Calcular comparación
POST   /api/reconciliations/{id}/close        Cerrar en Aguas
GET    /api/reconciliations/{id}/deposits     Depósitos del reparto
GET    /api/reconciliations/{id}/aguas-detail Detalle de cobros desde Aguas
```

### Depósitos y cheques
```
GET    /api/deposits/{id}/checks              Cheques de un depósito
POST   /api/deposits/{id}/checks              Agregar cheque
DELETE /api/deposits/{id}/checks/{checkId}    Eliminar cheque
GET    /api/deposits/{id}/withholdings        Retenciones de un depósito
POST   /api/deposits/{id}/withholdings        Agregar retención
DELETE /api/deposits/{id}/withholdings/{wId}  Eliminar retención
GET    /api/deposits/conceptos/checks         Catálogo de conceptos de cheques
GET    /api/deposits/conceptos/withholdings   Catálogo de conceptos de retenciones
```

### Auditoría e integraciones
```
GET /api/audit                                Todos los logs (filtrable por entidad)
GET /api/integrations/status                  Estado de Minibank y Aguas por cajero/planta
```

---

## Integración con Aguas

| Endpoint Aguas | Uso |
|---|---|
| `/reparto_get_valores?idreparto=0&fecha=` | Trae todos los repartos con esperado (1 llamada para toda la planta) |
| `/reparto_get_valores?idreparto={n}&fecha=` | Esperado de un reparto específico |
| `/getcobranzadetalle?reparto={n}&fecha=` | Detalle de cobros: qué cliente pagó con qué forma de pago |
| `/close` (XML POST) | Cierra un reparto en Aguas |

> **Formatos de fecha**: el esperado usa `dd/MM/yyyy`; el detalle usa `YYYY-MM-DD`.

---

## Configuración notable

```properties
# Pool de conexiones (necesario para el refresh paralelo)
spring.datasource.hikari.maximum-pool-size=25

# Schedulers automáticos
pac.scheduler.minibank.fixed-delay-ms=300000   # 5 min
pac.scheduler.aguas.fixed-delay-ms=600000      # 10 min
pac.scheduler.health.fixed-delay-ms=120000     # 2 min

# Umbrales de diferencias
pac.difference.surplus-high-threshold=5000
pac.difference.shortage-critical-threshold=5000
```

---

## Nota sobre migraciones

El proyecto usa `spring.jpa.hibernate.ddl-auto=update`. Si se agregan valores a enums que tienen CHECK constraints en Postgres (como `audit_logs.action` o `reconciliations.status`), hay que ejecutar el ALTER manualmente.

---

## Header de autenticación

Todas las mutaciones aceptan el header `X-User-Id` con el nombre del usuario que realiza la acción. Se usa para trazabilidad en el log de auditoría. Si no se envía, se asume `"system"`.

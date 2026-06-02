#!/bin/sh
# Carga las variables del .env y arranca la aplicación
set -a
# shellcheck source=.env
. "$(dirname "$0")/.env"
set +a
./mvnw spring-boot:run "$@"

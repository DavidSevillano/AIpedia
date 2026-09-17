#!/usr/bin/env bash
# Compila el bundle firmado y lo sube a Google Play.
#
# Requisitos (ver README de la seccion "Publicar"):
#   - keystore.properties en la raiz, con la clave de subida
#   - play-service-account.json en la raiz, con acceso al paquete en Play Console
#
# Uso:
#   scripts/publish-play.sh                      # sube como borrador al track internal
#   scripts/publish-play.sh --track production   # sube como borrador a produccion
#   scripts/publish-play.sh --track production --rollout 1.0   # publica al 100%
#
# Sin --rollout la release queda en DRAFT: hay que darle a publicar en Play Console.

set -euo pipefail

PACKAGE_NAME="com.burixer85.aipedia"
TRACK="internal"
ROLLOUT=""
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SA_JSON="$ROOT/play-service-account.json"

while [[ $# -gt 0 ]]; do
    case "$1" in
        --track)   TRACK="$2"; shift 2 ;;
        --rollout) ROLLOUT="$2"; shift 2 ;;
        *) echo "Argumento desconocido: $1" >&2; exit 2 ;;
    esac
done

fail() { echo "ERROR: $*" >&2; exit 1; }

[[ -f "$ROOT/keystore.properties" ]] || fail "falta keystore.properties (copia keystore.properties.example)"
[[ -f "$SA_JSON" ]] || fail "falta play-service-account.json"
command -v gcloud >/dev/null || fail "gcloud no esta instalado"
command -v curl   >/dev/null || fail "curl no esta instalado"

# --- 1. Compilar el bundle firmado -----------------------------------------
echo "==> Compilando bundle de release..."
( cd "$ROOT" && ./gradlew --quiet bundleRelease )

AAB="$ROOT/app/build/outputs/bundle/release/app-release.aab"
[[ -f "$AAB" ]] || fail "no se genero el bundle en $AAB"

# Verificar que esta firmado: un AAB firmado lleva META-INF/*.RSA (o .EC)
if ! unzip -l "$AAB" | grep -qE 'META-INF/.*\.(RSA|EC|DSA)$'; then
    fail "el bundle NO esta firmado: revisa keystore.properties"
fi
echo "    bundle firmado: $AAB"

VERSION_CODE=$(
    cd "$ROOT" && sed -n 's/^[[:space:]]*versionCode[[:space:]]*=[[:space:]]*\([0-9]*\).*/\1/p' app/build.gradle.kts | head -1
)
echo "    versionCode: $VERSION_CODE"

# --- 2. Token de la service account ----------------------------------------
echo "==> Obteniendo token de Play..."
SA_EMAIL=$(sed -n 's/.*"client_email"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p' "$SA_JSON")
PREV_ACCOUNT=$(gcloud config get-value account 2>/dev/null || true)
restore_account() {
    if [[ -n "${PREV_ACCOUNT:-}" && "$PREV_ACCOUNT" != "(unset)" ]]; then
        gcloud config set account "$PREV_ACCOUNT" >/dev/null 2>&1 || true
    fi
}
trap restore_account EXIT

gcloud auth activate-service-account --key-file="$SA_JSON" >/dev/null 2>&1 \
    || fail "no se pudo activar la service account"
TOKEN=$(gcloud auth print-access-token \
    --account="$SA_EMAIL" \
    --scopes=https://www.googleapis.com/auth/androidpublisher 2>/dev/null) \
    || fail "no se pudo obtener el token con scope androidpublisher"

api() {
    local method="$1" path="$2"; shift 2
    curl -sS -X "$method" \
        "https://androidpublisher.googleapis.com/androidpublisher/v3/applications/$PACKAGE_NAME/$path" \
        -H "Authorization: Bearer $TOKEN" "$@"
}

# --- 3. Crear el edit -------------------------------------------------------
echo "==> Creando edit..."
EDIT_JSON=$(api POST "edits" -H "Content-Type: application/json" -d '{}')
EDIT_ID=$(echo "$EDIT_JSON" | sed -n 's/.*"id"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p')
[[ -n "$EDIT_ID" ]] || fail "no se pudo crear el edit: $EDIT_JSON"

abort_edit() {
    api DELETE "edits/$EDIT_ID" >/dev/null 2>&1 || true
    restore_account
}
trap abort_edit EXIT
echo "    edit: $EDIT_ID"

# --- 4. Subir el bundle -----------------------------------------------------
echo "==> Subiendo bundle (puede tardar)..."
UPLOAD=$(curl -sS -X POST \
    "https://androidpublisher.googleapis.com/upload/androidpublisher/v3/applications/$PACKAGE_NAME/edits/$EDIT_ID/bundles?uploadType=media" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/octet-stream" \
    --data-binary @"$AAB")
UPLOADED_VC=$(echo "$UPLOAD" | sed -n 's/.*"versionCode"[[:space:]]*:[[:space:]]*\([0-9]*\).*/\1/p')
[[ -n "$UPLOADED_VC" ]] || fail "fallo la subida: $UPLOAD"
echo "    subido versionCode $UPLOADED_VC"

# --- 5. Asignar al track ----------------------------------------------------
if [[ -n "$ROLLOUT" ]]; then
    if [[ "$ROLLOUT" == "1.0" || "$ROLLOUT" == "1" ]]; then
        RELEASE="{\"versionCodes\":[\"$UPLOADED_VC\"],\"status\":\"completed\"}"
        echo "==> Asignando a '$TRACK' con rollout COMPLETO (100%)"
    else
        RELEASE="{\"versionCodes\":[\"$UPLOADED_VC\"],\"status\":\"inProgress\",\"userFraction\":$ROLLOUT}"
        echo "==> Asignando a '$TRACK' con rollout parcial ($ROLLOUT)"
    fi
else
    RELEASE="{\"versionCodes\":[\"$UPLOADED_VC\"],\"status\":\"draft\"}"
    echo "==> Asignando a '$TRACK' como BORRADOR"
fi

TRACK_RESP=$(api PUT "edits/$EDIT_ID/tracks/$TRACK" \
    -H "Content-Type: application/json" \
    -d "{\"track\":\"$TRACK\",\"releases\":[$RELEASE]}")
echo "$TRACK_RESP" | grep -q '"releases"' || fail "fallo al asignar el track: $TRACK_RESP"

# --- 6. Commit --------------------------------------------------------------
echo "==> Confirmando edit..."
COMMIT=$(api POST "edits/$EDIT_ID:commit")
echo "$COMMIT" | grep -q '"id"' || fail "fallo el commit: $COMMIT"

trap restore_account EXIT
echo
echo "LISTO. versionCode $UPLOADED_VC en el track '$TRACK'."
if [[ -z "$ROLLOUT" ]]; then
    echo "Esta como BORRADOR: entra en Play Console y dale a publicar."
fi

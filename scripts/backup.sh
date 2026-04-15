#!/bin/bash
# ─── Backup do PostgreSQL para S3 ────────────────────────────────────────────
# Configurar no cron da EC2: 0 3 * * * /opt/clinikai/scripts/backup.sh >> /var/log/clinikai-backup.log 2>&1

set -euo pipefail

# ─── Variáveis (carregadas do .env.prod) ──────────────────────────────────────
source /opt/clinikai/.env.prod

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="clinikai_backup_${TIMESTAMP}.sql.gz"
BACKUP_DIR="/tmp/clinikai_backups"
RETENTION_DAYS=30

# ─── Criar diretório temporário ───────────────────────────────────────────────
mkdir -p "$BACKUP_DIR"

echo "[$(date)] Iniciando backup do banco clinikai..."

# ─── Dump comprimido ──────────────────────────────────────────────────────────
docker exec clinikai-postgres pg_dump \
  -U "$POSTGRES_USER" \
  -d "$POSTGRES_DB" \
  --no-owner \
  --no-acl \
  | gzip > "${BACKUP_DIR}/${BACKUP_FILE}"

echo "[$(date)] Dump gerado: ${BACKUP_FILE} ($(du -sh "${BACKUP_DIR}/${BACKUP_FILE}" | cut -f1))"

# ─── Upload para S3 ───────────────────────────────────────────────────────────
AWS_ACCESS_KEY_ID="$AWS_ACCESS_KEY_ID" \
AWS_SECRET_ACCESS_KEY="$AWS_SECRET_ACCESS_KEY" \
aws s3 cp \
  "${BACKUP_DIR}/${BACKUP_FILE}" \
  "s3://${S3_BACKUP_BUCKET}/postgres/${BACKUP_FILE}" \
  --region "$AWS_REGION" \
  --storage-class STANDARD_IA

echo "[$(date)] Upload para S3 concluído: s3://${S3_BACKUP_BUCKET}/postgres/${BACKUP_FILE}"

# ─── Limpar arquivo local ─────────────────────────────────────────────────────
rm -f "${BACKUP_DIR}/${BACKUP_FILE}"

# ─── Remover backups antigos do S3 (mais de RETENTION_DAYS dias) ──────────────
echo "[$(date)] Removendo backups com mais de ${RETENTION_DAYS} dias..."

AWS_ACCESS_KEY_ID="$AWS_ACCESS_KEY_ID" \
AWS_SECRET_ACCESS_KEY="$AWS_SECRET_ACCESS_KEY" \
aws s3 ls "s3://${S3_BACKUP_BUCKET}/postgres/" --region "$AWS_REGION" \
  | awk '{print $4}' \
  | while read -r file; do
      file_date=$(echo "$file" | grep -oP '\d{8}')
      if [[ -n "$file_date" ]]; then
        file_epoch=$(date -d "${file_date}" +%s 2>/dev/null || date -j -f "%Y%m%d" "${file_date}" +%s)
        cutoff_epoch=$(date -d "-${RETENTION_DAYS} days" +%s 2>/dev/null || date -v-${RETENTION_DAYS}d +%s)
        if [[ "$file_epoch" -lt "$cutoff_epoch" ]]; then
          AWS_ACCESS_KEY_ID="$AWS_ACCESS_KEY_ID" \
          AWS_SECRET_ACCESS_KEY="$AWS_SECRET_ACCESS_KEY" \
          aws s3 rm "s3://${S3_BACKUP_BUCKET}/postgres/${file}" --region "$AWS_REGION"
          echo "[$(date)] Removido backup antigo: ${file}"
        fi
      fi
    done

echo "[$(date)] Backup finalizado com sucesso!"

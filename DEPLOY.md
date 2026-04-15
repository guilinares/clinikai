# Deploy Clinikai na AWS

## Visão geral do fluxo

```
Você faz push para main
        │
        ▼
┌─────────────────────────────────────────────┐
│            GitHub Actions                    │
│                                             │
│  terraform.yml  →  Plan + Apply na AWS      │
│  deploy.yml     →  Test → Build → Deploy    │
└─────────────────────────────────────────────┘
        │
        ▼
  EC2 t4g.small (~$12/mês)
  ├── Nginx (HTTPS via Let's Encrypt)
  ├── Spring Boot (imagem do GHCR)
  └── PostgreSQL (backup diário → S3)
```

---

## Pré-requisitos

- Conta AWS com acesso ao console
- AWS CLI instalado localmente (`aws configure`)
- Terraform >= 1.7 instalado
- Domínio com DNS configurável

---

## Passo 1 — Gerar chave SSH para a EC2

```bash
ssh-keygen -t ed25519 -f ~/.ssh/clinikai-deployer -C "clinikai-deploy"
cat ~/.ssh/clinikai-deployer.pub  # copie o conteúdo para usar nos próximos passos
```

---

## Passo 2 — Bootstrap do estado remoto do Terraform

Este passo só é feito **uma vez** para criar o bucket S3 e tabela DynamoDB que guardam o estado do Terraform.

```bash
cd infra/terraform

# Comente temporariamente o bloco "backend" em main.tf
# (ou use o arquivo bootstrap.tf diretamente)

terraform init
terraform apply \
  -target=aws_s3_bucket.terraform_state \
  -target=aws_s3_bucket_versioning.terraform_state \
  -target=aws_s3_bucket_server_side_encryption_configuration.terraform_state \
  -target=aws_s3_bucket_public_access_block.terraform_state \
  -target=aws_dynamodb_table.terraform_locks \
  -var="ec2_public_key=$(cat ~/.ssh/clinikai-deployer.pub)"

# Descomente o bloco backend em main.tf e migre o estado
terraform init -migrate-state
```

---

## Passo 3 — Configurar GitHub Secrets

No repositório GitHub, vá em **Settings → Secrets and Variables → Actions** e crie:

| Secret | Valor |
|--------|-------|
| `AWS_ACCESS_KEY_ID` | Após o `terraform apply` inicial, rode `terraform output github_actions_access_key_id` |
| `AWS_SECRET_ACCESS_KEY` | `terraform output -raw github_actions_secret_access_key` |
| `EC2_PUBLIC_KEY` | Conteúdo do `~/.ssh/clinikai-deployer.pub` |
| `EC2_SSH_KEY` | Conteúdo do `~/.ssh/clinikai-deployer` (chave **privada**) |
| `EC2_HOST` | IP da EC2 — após o terraform apply: `terraform output ec2_public_ip` |
| `GHCR_TOKEN` | Personal Access Token do GitHub com permissão `read:packages` |

---

## Passo 4 — Primeiro deploy da infra via Terraform

```bash
cd infra/terraform
terraform apply -var="ec2_public_key=$(cat ~/.ssh/clinikai-deployer.pub)"
```

Anote o IP público que aparece no output (`ec2_public_ip`).

---

## Passo 5 — Configurar DNS

No painel do seu provedor de domínio:

```
Tipo: A
Nome: app  (resulta em app.clinikai.com.br)
Valor: IP_DA_EC2
TTL: 300
```

Teste: `nslookup app.clinikai.com.br`

---

## Passo 6 — Primeiro acesso à EC2 e configuração inicial

```bash
ssh -i ~/.ssh/clinikai-deployer ubuntu@IP_DA_EC2

# Clone o repositório
git clone https://github.com/seu-usuario/clinikai.git /opt/clinikai
cd /opt/clinikai

# Crie o .env.prod
cp .env.prod.example .env.prod
nano .env.prod   # preencha todos os valores
```

---

## Passo 7 — Emitir certificado SSL (primeira vez)

```bash
# Na EC2, dentro de /opt/clinikai
export $(grep -v '^#' .env.prod | xargs)

# Sobe só nginx e postgres para o Certbot funcionar
docker compose -f docker-compose.prod.yml up -d nginx postgres

# Emite o certificado
docker compose -f docker-compose.prod.yml run --rm certbot certonly \
  --webroot \
  --webroot-path=/var/www/certbot \
  --email seu@email.com \
  --agree-tos \
  --no-eff-email \
  -d app.clinikai.com.br
```

---

## Passo 8 — Adicionar ao .gitignore

```bash
echo ".env.prod" >> .gitignore
echo "*.pem" >> .gitignore
echo "infra/terraform/.terraform/" >> .gitignore
echo "infra/terraform/tfplan" >> .gitignore
```

---

## Fluxo do dia a dia (após setup)

A partir daqui, **todo push para `main` dispara o pipeline automaticamente**:

- Mudanças em `backend/` ou `clinikai-admin/` → `deploy.yml` roda
- Mudanças em `infra/terraform/` → `terraform.yml` roda
- Pull Requests → Terraform mostra o `plan` como comentário no PR, sem aplicar

---

## Configurar backup automático

```bash
# Na EC2
chmod +x /opt/clinikai/scripts/backup.sh

# Cron: backup todo dia às 3h da manhã
(crontab -l 2>/dev/null; echo "0 3 * * * /opt/clinikai/scripts/backup.sh >> /var/log/clinikai-backup.log 2>&1") | crontab -

# Teste manual
/opt/clinikai/scripts/backup.sh
```

---

## Comandos úteis na EC2

```bash
# Status dos containers
docker compose -f /opt/clinikai/docker-compose.prod.yml ps

# Logs do backend
docker compose -f /opt/clinikai/docker-compose.prod.yml logs -f backend

# Reiniciar um serviço
docker compose -f /opt/clinikai/docker-compose.prod.yml restart backend

# Acessar o banco
docker exec -it clinikai-postgres psql -U clinikai -d clinikai

# Ver IP público
curl ifconfig.me
```

---

## Estrutura de arquivos criados

```
clinikai/
├── .github/
│   └── workflows/
│       ├── deploy.yml          # CI/CD: testa, builda e faz deploy
│       └── terraform.yml       # Infra: plan no PR, apply no main
├── infra/
│   └── terraform/
│       ├── main.tf             # Provider + backend remoto
│       ├── bootstrap.tf        # Cria o bucket de estado (só uma vez)
│       ├── vpc.tf              # VPC, subnet, IGW, route table
│       ├── ec2.tf              # EC2 t4g.small + Elastic IP
│       ├── security_groups.tf  # Portas 22, 80, 443
│       ├── s3.tf               # Bucket de backups
│       ├── iam.tf              # Role EC2 + User GitHub Actions
│       ├── variables.tf        # Variáveis configuráveis
│       └── outputs.tf          # IP, SSH command, etc.
├── backend/
│   └── Dockerfile              # Multi-stage build JDK21 → JRE Alpine
├── nginx/
│   ├── nginx.conf
│   └── conf.d/clinikai.conf    # HTTPS + proxy + SPA routing
├── scripts/
│   └── backup.sh               # pg_dump diário para S3
├── docker-compose.prod.yml     # Stack completa de produção
└── .env.prod.example           # Template de variáveis (não commitar .env.prod)
```

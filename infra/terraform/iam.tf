# ─── IAM Role para a EC2 (acessa S3 de backups) ──────────────────────────────
resource "aws_iam_role" "ec2_role" {
  name = "clinikai-ec2-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action    = "sts:AssumeRole"
      Effect    = "Allow"
      Principal = { Service = "ec2.amazonaws.com" }
    }]
  })
}

resource "aws_iam_role_policy" "ec2_s3_backup" {
  name = "clinikai-ec2-s3-backup"
  role = aws_iam_role.ec2_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect = "Allow"
      Action = [
        "s3:PutObject",
        "s3:GetObject",
        "s3:DeleteObject",
        "s3:ListBucket"
      ]
      Resource = [
        aws_s3_bucket.backups.arn,
        "${aws_s3_bucket.backups.arn}/*"
      ]
    }]
  })
}

resource "aws_iam_instance_profile" "ec2_profile" {
  name = "clinikai-ec2-profile"
  role = aws_iam_role.ec2_role.name
}

# ─── IAM User para GitHub Actions (deploy via SSH + GHCR) ────────────────────
# O deploy é via SSH direto na EC2, então não precisa de permissões AWS complexas.
# Apenas acesso mínimo para verificar o estado da infra se necessário.
resource "aws_iam_user" "github_actions" {
  name = "clinikai-github-actions"
  tags = { Purpose = "GitHub Actions CI/CD" }
}

resource "aws_iam_access_key" "github_actions" {
  user = aws_iam_user.github_actions.name
}

# Permissão mínima: só leitura do estado da infra (EC2 describe)
resource "aws_iam_user_policy" "github_actions" {
  name = "clinikai-github-actions-policy"
  user = aws_iam_user.github_actions.name

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        # Necessário para o Terraform no pipeline
        Effect = "Allow"
        Action = [
          "ec2:Describe*",
          "s3:GetObject",
          "s3:PutObject",
          "s3:ListBucket",
          "dynamodb:GetItem",
          "dynamodb:PutItem",
          "dynamodb:DeleteItem"
        ]
        Resource = "*"
      }
    ]
  })
}

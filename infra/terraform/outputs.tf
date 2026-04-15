output "ec2_public_ip" {
  description = "IP público da EC2 (Elastic IP) — use para configurar o DNS"
  value       = aws_eip.app.public_ip
}

output "ec2_instance_id" {
  description = "ID da instância EC2"
  value       = aws_instance.app.id
}

output "backup_bucket_name" {
  description = "Nome do bucket S3 de backups"
  value       = aws_s3_bucket.backups.bucket
}

output "github_actions_access_key_id" {
  description = "Access Key ID para configurar no GitHub Secrets"
  value       = aws_iam_access_key.github_actions.id
  sensitive   = true
}

output "github_actions_secret_access_key" {
  description = "Secret Access Key para configurar no GitHub Secrets"
  value       = aws_iam_access_key.github_actions.secret
  sensitive   = true
}

output "ssh_command" {
  description = "Comando para conectar na EC2"
  value       = "ssh -i ~/.ssh/clinikai-deployer ubuntu@${aws_eip.app.public_ip}"
}

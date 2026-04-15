variable "aws_region" {
  description = "Região AWS"
  type        = string
  default     = "sa-east-1"
}

variable "environment" {
  description = "Ambiente (prod, staging)"
  type        = string
  default     = "prod"
}

variable "instance_type" {
  description = "Tipo da instância EC2 (ARM)"
  type        = string
  default     = "t4g.small"
}

variable "ec2_public_key" {
  description = "Chave SSH pública para acesso à EC2 (conteúdo do arquivo .pub)"
  type        = string
  sensitive   = true
}

variable "allowed_ssh_cidr" {
  description = "IP permitido para SSH (use seu IP: curl ifconfig.me)"
  type        = string
  default     = "0.0.0.0/0" # Restrinja para seu IP em produção!
}

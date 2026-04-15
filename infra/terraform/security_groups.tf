# ─── Security Group da EC2 ────────────────────────────────────────────────────
resource "aws_security_group" "ec2" {
  name        = "clinikai-ec2-sg"
  description = "Security group da EC2 Clinikai"
  vpc_id      = aws_vpc.main.id

  # SSH — apenas do seu IP (definido em var.allowed_ssh_cidr)
  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.allowed_ssh_cidr]
  }

  # HTTP — para o Certbot e redirect para HTTPS
  ingress {
    description = "HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # HTTPS
  ingress {
    description = "HTTPS"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Saída irrestrita
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "clinikai-ec2-sg" }
}

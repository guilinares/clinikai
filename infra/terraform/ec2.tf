# ─── Key Pair (gerado localmente, público importado pelo Terraform) ────────────
resource "aws_key_pair" "deployer" {
  key_name   = "clinikai-deployer"
  public_key = var.ec2_public_key
}

# ─── AMI Ubuntu 24.04 ARM (t4g) ───────────────────────────────────────────────
data "aws_ami" "ubuntu_arm" {
  most_recent = true
  owners      = ["099720109477"] # Canonical

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd-gp3/ubuntu-noble-24.04-arm64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

# ─── EC2 ──────────────────────────────────────────────────────────────────────
resource "aws_instance" "app" {
  ami                    = data.aws_ami.ubuntu_arm.id
  instance_type          = var.instance_type
  subnet_id              = aws_subnet.public.id
  vpc_security_group_ids = [aws_security_group.ec2.id]
  key_name               = aws_key_pair.deployer.key_name
  iam_instance_profile   = aws_iam_instance_profile.ec2_profile.name

  root_block_device {
    volume_size = 20
    volume_type = "gp3"
    encrypted   = true
  }

  # Instala Docker e dependências na inicialização
  user_data = <<-EOF
    #!/bin/bash
    set -e
    apt-get update -y
    apt-get install -y docker.io docker-compose-v2 awscli git curl nodejs npm
    systemctl enable docker
    systemctl start docker
    usermod -aG docker ubuntu

    # Cria diretório do app
    mkdir -p /opt/clinikai
    chown ubuntu:ubuntu /opt/clinikai
  EOF

  tags = { Name = "clinikai-app" }
}

# ─── Elastic IP (IP fixo) ─────────────────────────────────────────────────────
resource "aws_eip" "app" {
  instance = aws_instance.app.id
  domain   = "vpc"

  tags = { Name = "clinikai-eip" }
}

terraform {
  required_version = ">= 1.7"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  backend "s3" {
    bucket         = "clinikai-terraform-state"
    key            = "prod/terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "clinikai-terraform-locks"
    encrypt        = true
  }
}

# Provider principal — infraestrutura da aplicação (São Paulo)
provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = "clinikai"
      Environment = var.environment
      ManagedBy   = "terraform"
    }
  }
}

# Provider secundário — apenas para o bucket de estado e lock table (já existem em us-east-1)
provider "aws" {
  alias  = "us_east_1"
  region = "us-east-1"

  default_tags {
    tags = {
      Project     = "clinikai"
      Environment = var.environment
      ManagedBy   = "terraform"
    }
  }
}

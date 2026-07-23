# Input variables for the bidnest infrastructure. Supply db_password and ec2_key_pair_name
# at apply time (via -var flags, a tfvars file, or environment variables) — they have no
# defaults because they must be chosen per environment.

variable "aws_region" {
  description = "AWS region to deploy into"
  type        = string
  default     = "us-east-2"
}

variable "ec2_instance_type" {
  description = "EC2 instance type for the app server"
  type        = string
  default     = "t3.micro"
}

variable "ec2_key_pair_name" {
  description = "Name of an existing EC2 key pair for SSH access to the app server"
  type        = string
}

variable "db_instance_class" {
  description = "RDS instance class for the PostgreSQL database"
  type        = string
  default     = "db.t3.micro"
}

variable "db_password" {
  description = "Master password for the RDS PostgreSQL instance"
  type        = string
  sensitive   = true
}
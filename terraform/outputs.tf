# Outputs surfaced after `terraform apply`: the three values needed to deploy and connect
# to the running infrastructure — SSH/curl target, JDBC URL base, and Docker push destination.

output "ec2_public_ip" {
  description = "Public IP of the bidnest app server — use this to SSH in or hit the API"
  value       = aws_instance.app.public_ip
}

output "rds_endpoint" {
  description = "RDS PostgreSQL endpoint (host:port) — set as the JDBC URL base in app config"
  value       = aws_db_instance.main.endpoint
}

output "ecr_repository_url" {
  description = "ECR repository URL — tag and push the bidnest Docker image here"
  value       = aws_ecr_repository.app.repository_url
}
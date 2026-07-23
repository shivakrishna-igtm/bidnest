# RDS PostgreSQL instance: creates a subnet group across the default subnets, a security group
# that only allows port 5432 from the app server security group, and the database itself.
# skip_final_snapshot = true so the instance can be destroyed without a manual snapshot step.

resource "aws_security_group" "db" {
  name        = "bidnest-db-sg"
  description = "Allow PostgreSQL access only from the bidnest app security group"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description     = "PostgreSQL from app"
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.app.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "bidnest-db-sg"
  }
}

resource "aws_db_subnet_group" "main" {
  name       = "bidnest-db-subnet-group"
  subnet_ids = data.aws_subnets.default.ids

  tags = {
    Name = "bidnest-db-subnet-group"
  }
}

resource "aws_db_instance" "main" {
  identifier        = "bidnest-db"
  engine            = "postgres"
  engine_version    = "15"
  instance_class    = var.db_instance_class
  allocated_storage = 20

  db_name  = "bidnest"
  username = "bidnest"
  password = var.db_password

  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.db.id]

  skip_final_snapshot = true

  tags = {
    Name = "bidnest-db"
  }
}
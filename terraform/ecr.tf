# ECR repository: stores the bidnest Docker images built by CI. scan_on_push enables
# AWS's built-in vulnerability scanning on every image push at no extra cost.

resource "aws_ecr_repository" "app" {
  name                 = "bidnest"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name = "bidnest"
  }
}
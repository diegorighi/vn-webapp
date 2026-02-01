# =============================================================================
# AWS COGNITO - Authorization Server (PROD)
# =============================================================================

terraform {
  required_version = ">= 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# =============================================================================
# VARIABLES
# =============================================================================

variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "sa-east-1"
}

variable "environment" {
  description = "Environment name"
  type        = string
  default     = "prod"
}

variable "app_name" {
  description = "Application name"
  type        = string
  default     = "vanessa-viagem"
}

variable "callback_urls" {
  description = "Callback URLs for the app"
  type        = list(string)
  default     = ["https://vanessaviagem.com.br/callback", "https://app.vanessaviagem.com.br/callback"]
}

variable "logout_urls" {
  description = "Logout URLs for the app"
  type        = list(string)
  default     = ["https://vanessaviagem.com.br", "https://app.vanessaviagem.com.br"]
}

# =============================================================================
# COGNITO USER POOL
# =============================================================================

resource "aws_cognito_user_pool" "main" {
  name = "${var.app_name}-${var.environment}"

  # Username configuration
  username_attributes      = ["email"]
  auto_verified_attributes = ["email"]

  # Password policy
  password_policy {
    minimum_length                   = 8
    require_lowercase                = true
    require_numbers                  = true
    require_symbols                  = false
    require_uppercase                = true
    temporary_password_validity_days = 7
  }

  # MFA configuration (optional, can enable later)
  mfa_configuration = "OFF"

  # Account recovery
  account_recovery_setting {
    recovery_mechanism {
      name     = "verified_email"
      priority = 1
    }
  }

  # Email configuration (using Cognito default)
  email_configuration {
    email_sending_account = "COGNITO_DEFAULT"
  }

  # Schema - Custom attributes for multi-tenant
  schema {
    name                     = "tenant_id"
    attribute_data_type      = "String"
    developer_only_attribute = false
    mutable                  = true
    required                 = false

    string_attribute_constraints {
      min_length = 36
      max_length = 36
    }
  }

  schema {
    name                     = "user_id"
    attribute_data_type      = "String"
    developer_only_attribute = false
    mutable                  = true
    required                 = false

    string_attribute_constraints {
      min_length = 36
      max_length = 36
    }
  }

  schema {
    name                     = "role"
    attribute_data_type      = "String"
    developer_only_attribute = false
    mutable                  = true
    required                 = false

    string_attribute_constraints {
      min_length = 1
      max_length = 50
    }
  }

  schema {
    name                     = "can_approve"
    attribute_data_type      = "String"
    developer_only_attribute = false
    mutable                  = true
    required                 = false

    string_attribute_constraints {
      min_length = 4
      max_length = 5
    }
  }

  # User pool add-ons
  user_pool_add_ons {
    advanced_security_mode = "OFF" # Set to "ENFORCED" for advanced security ($0.05/MAU)
  }

  # Admin create user config
  admin_create_user_config {
    allow_admin_create_user_only = true # Only admins can create users

    invite_message_template {
      email_subject = "Bem-vindo ao Vanessa Viagem"
      email_message = "Seu usuario foi criado. Username: {username}, Senha temporaria: {####}"
      sms_message   = "Seu usuario: {username}, Senha: {####}"
    }
  }

  # Verification message
  verification_message_template {
    default_email_option = "CONFIRM_WITH_CODE"
    email_subject        = "Codigo de verificacao - Vanessa Viagem"
    email_message        = "Seu codigo de verificacao e: {####}"
  }

  tags = {
    Environment = var.environment
    Application = var.app_name
    ManagedBy   = "terraform"
  }
}

# =============================================================================
# COGNITO USER POOL DOMAIN
# =============================================================================

resource "aws_cognito_user_pool_domain" "main" {
  domain       = "${var.app_name}-${var.environment}"
  user_pool_id = aws_cognito_user_pool.main.id
}

# =============================================================================
# COGNITO USER POOL CLIENT - Web Application (SPA)
# =============================================================================

resource "aws_cognito_user_pool_client" "web" {
  name         = "${var.app_name}-web"
  user_pool_id = aws_cognito_user_pool.main.id

  # Client settings
  generate_secret                      = false # Public client for SPA
  prevent_user_existence_errors        = "ENABLED"
  enable_token_revocation              = true
  enable_propagate_additional_user_context_data = false

  # Auth flows
  explicit_auth_flows = [
    "ALLOW_USER_PASSWORD_AUTH",
    "ALLOW_REFRESH_TOKEN_AUTH",
    "ALLOW_USER_SRP_AUTH"
  ]

  # OAuth configuration
  allowed_oauth_flows                  = ["code"]
  allowed_oauth_flows_user_pool_client = true
  allowed_oauth_scopes                 = ["email", "openid", "profile"]
  supported_identity_providers         = ["COGNITO"]

  # URLs
  callback_urls = var.callback_urls
  logout_urls   = var.logout_urls

  # Token validity
  access_token_validity  = 1  # hours
  id_token_validity      = 1  # hours
  refresh_token_validity = 30 # days

  token_validity_units {
    access_token  = "hours"
    id_token      = "hours"
    refresh_token = "days"
  }

  # Read/Write attributes
  read_attributes = [
    "email",
    "email_verified",
    "name",
    "custom:tenant_id",
    "custom:user_id",
    "custom:role",
    "custom:can_approve"
  ]

  write_attributes = [
    "email",
    "name"
  ]
}

# =============================================================================
# COGNITO USER POOL CLIENT - API (Backend service-to-service)
# =============================================================================

resource "aws_cognito_user_pool_client" "api" {
  name         = "${var.app_name}-api"
  user_pool_id = aws_cognito_user_pool.main.id

  # Client settings
  generate_secret               = true # Confidential client
  prevent_user_existence_errors = "ENABLED"

  # Auth flows - client credentials for service-to-service
  explicit_auth_flows = [
    "ALLOW_ADMIN_USER_PASSWORD_AUTH",
    "ALLOW_REFRESH_TOKEN_AUTH"
  ]

  # Token validity
  access_token_validity  = 1  # hours
  id_token_validity      = 1  # hours
  refresh_token_validity = 30 # days

  token_validity_units {
    access_token  = "hours"
    id_token      = "hours"
    refresh_token = "days"
  }
}

# =============================================================================
# COGNITO GROUPS (Roles)
# =============================================================================

resource "aws_cognito_user_group" "root" {
  name         = "ROOT"
  description  = "Super administrador com controle total"
  user_pool_id = aws_cognito_user_pool.main.id
  precedence   = 1
}

resource "aws_cognito_user_group" "admin" {
  name         = "ADMIN"
  description  = "Administrador que pode gerenciar usuarios e aprovar alteracoes"
  user_pool_id = aws_cognito_user_pool.main.id
  precedence   = 2
}

resource "aws_cognito_user_group" "manager" {
  name         = "MANAGER"
  description  = "Gerente que pode inserir e atualizar dados com aprovacao"
  user_pool_id = aws_cognito_user_pool.main.id
  precedence   = 3
}

resource "aws_cognito_user_group" "operator" {
  name         = "OPERATOR"
  description  = "Operador que pode apenas inserir dados com aprovacao"
  user_pool_id = aws_cognito_user_pool.main.id
  precedence   = 4
}

resource "aws_cognito_user_group" "viewer" {
  name         = "VIEWER"
  description  = "Visualizador com acesso somente leitura"
  user_pool_id = aws_cognito_user_pool.main.id
  precedence   = 5
}

# =============================================================================
# OUTPUTS
# =============================================================================

output "user_pool_id" {
  description = "Cognito User Pool ID"
  value       = aws_cognito_user_pool.main.id
}

output "user_pool_arn" {
  description = "Cognito User Pool ARN"
  value       = aws_cognito_user_pool.main.arn
}

output "user_pool_endpoint" {
  description = "Cognito User Pool Endpoint"
  value       = aws_cognito_user_pool.main.endpoint
}

output "user_pool_domain" {
  description = "Cognito User Pool Domain"
  value       = "https://${aws_cognito_user_pool_domain.main.domain}.auth.${var.aws_region}.amazoncognito.com"
}

output "web_client_id" {
  description = "Web Client ID"
  value       = aws_cognito_user_pool_client.web.id
}

output "api_client_id" {
  description = "API Client ID"
  value       = aws_cognito_user_pool_client.api.id
}

output "api_client_secret" {
  description = "API Client Secret"
  value       = aws_cognito_user_pool_client.api.client_secret
  sensitive   = true
}

output "jwk_uri" {
  description = "JWK Set URI for JWT validation"
  value       = "https://cognito-idp.${var.aws_region}.amazonaws.com/${aws_cognito_user_pool.main.id}/.well-known/jwks.json"
}

output "issuer_uri" {
  description = "Issuer URI for JWT validation"
  value       = "https://cognito-idp.${var.aws_region}.amazonaws.com/${aws_cognito_user_pool.main.id}"
}

# =============================================================================
# SPRING BOOT CONFIG OUTPUT
# =============================================================================

output "spring_config" {
  description = "Spring Boot configuration for application-prod.yml"
  value       = <<-EOT
    # Add this to application-prod.yml
    spring:
      security:
        oauth2:
          resourceserver:
            jwt:
              issuer-uri: https://cognito-idp.${var.aws_region}.amazonaws.com/${aws_cognito_user_pool.main.id}
              jwk-set-uri: https://cognito-idp.${var.aws_region}.amazonaws.com/${aws_cognito_user_pool.main.id}/.well-known/jwks.json

    cognito:
      user-pool-id: ${aws_cognito_user_pool.main.id}
      web-client-id: ${aws_cognito_user_pool_client.web.id}
      region: ${var.aws_region}
  EOT
}

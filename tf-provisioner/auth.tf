resource "vault_auth_backend" "approle" {
  type = "approle"
}

resource "vault_approle_auth_backend_role" "contact-management" {
  backend        = vault_auth_backend.approle.path
  role_name      = "contact-management"
  token_policies = ["contact-management-readonly"]
}

resource "vault_auth_backend" "approle" {
  type = "approle"
}

resource "vault_approle_auth_backend_role" "jawasundapadangbetawi" {
  backend        = vault_auth_backend.approle.path
  role_name      = "jawasundapadangbetawi"
  token_policies = ["contact-management-readonly"]
}

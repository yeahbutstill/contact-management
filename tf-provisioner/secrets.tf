resource "vault_generic_secret" "aplikasi-contact-management" {
  path = "secret/aplikasi-contact-management"
  data_json = <<EOF
  {
    "spring.datasource.url": "jdbc:postgresql://localhost:5432/contact_management_db",
    "spring.datasource.username": "dani-k8s",
    "spring.datasource.password": "dani-k8s",
    "database_username": "dani-k8s",
    "database_password": "dani-k8s"
  }
  EOF
}

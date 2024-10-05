resource "vault_generic_secret" "aplikasi-contact-management" {
  path = "secret/aplikasi/contact-management"
  data_json = <<EOT
  {
    "spring.datasource.url": "jdbc:postgresql://localhost:5432/contact_management_db",
    "spring.datasource.username": "dani",
    "spring.datasource.password": "dani"
  }
  EOT
}

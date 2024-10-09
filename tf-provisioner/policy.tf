#---------------------
# Create policies
#---------------------

# Create 'contact-management' policy
resource "vault_policy" "applikasi-contact-management-readonly" {
  name   = "applikasi-contact-management-readonly"
  policy = file("policies/aplikasi-contact-management-readonly.hcl")
}

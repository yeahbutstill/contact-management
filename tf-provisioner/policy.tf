#---------------------
# Create policies
#---------------------

# Create 'contact-management' policy
resource "vault_policy" "contact-management-readonly" {
  name   = "contact-management-readonly"
  policy = file("policies/contact-management-readonly.hcl")
}

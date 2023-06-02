package com.yeahbutstill.restful.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAddressRequest {

    @JsonIgnore // biar ini tidak di kirim menggunakan body
    @NotBlank
    @NotEmpty
    private String contactId;

    @JsonIgnore
    @NotBlank
    @NotEmpty
    private String addressId;

    @Size(max = 200)
    private String street;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String province;

    @NotBlank
    @NotEmpty
    @Size(max = 100)
    private String country;

    @Size(max = 10)
    private String postalCode;

}

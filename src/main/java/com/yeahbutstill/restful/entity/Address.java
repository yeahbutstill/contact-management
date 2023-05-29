package com.yeahbutstill.restful.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

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

    @Column(name = "postal_code")
    @Size(max = 10)
    private String postalCode;

    @ManyToOne
    @JoinColumn(name = "contact_id", referencedColumnName = "id")
    private Contact contact;

}

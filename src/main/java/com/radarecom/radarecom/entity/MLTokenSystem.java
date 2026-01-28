package com.radarecom.radarecom.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "ML_TOKEN_SYSTEM")
@Getter
@Setter
public class MLTokenSystem {

    @Id
    @Column(name = "IDENTITY")
    private String identity;

    @Column(name = "TOKEN")
    private String token;

    @Column(name = "REFRESH_TOKEN")
    private String refreshToken;

}

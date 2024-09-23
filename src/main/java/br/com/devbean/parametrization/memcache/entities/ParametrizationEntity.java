package br.com.devbean.parametrization.memcache.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "PARAMETRIZATION")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParametrizationEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false, unique = true)
    private Long id;

    @Column(name = "KEY_REF", nullable = false, unique = true)
    private String key;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled;

    @Column(name = "CREATED_AT", nullable = false)
    private Date createdAt;

}

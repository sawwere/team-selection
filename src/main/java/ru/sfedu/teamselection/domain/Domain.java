package ru.sfedu.teamselection.domain;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.Id;

//TODO настроить id или вообще удалить
@MappedSuperclass
public abstract class Domain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false, updatable = false, insertable = false)
    Long id;
}

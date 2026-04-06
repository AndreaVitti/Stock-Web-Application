package com.project.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "favourites")
public class FavStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String symbol;
    private String name;
    private String currency;
    @OneToMany(mappedBy = "favStock", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<History> histories;
}

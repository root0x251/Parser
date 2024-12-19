package com.example.demo.entity.tour;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "link")
public class LinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "link", columnDefinition = "TEXT")
    private String link;

    @ManyToOne
    @JoinColumn(name = "selector_id", nullable = false)
    private SelectorEntity selectorEntity;

    @OneToOne(mappedBy = "link", cascade = CascadeType.ALL)
    private TourEntity tourEntity;

    public LinkEntity(String link, SelectorEntity selectorEntity) {
        this.link = link;
        this.selectorEntity = selectorEntity;
    }


}

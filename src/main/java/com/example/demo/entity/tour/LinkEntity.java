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
    @Column(name = "is_archive")
    private boolean isArchive;
    @Column(name = "parsing_error_count")
    private int errorCount;

    @ManyToOne
    @JoinColumn(name = "selector_id", nullable = false)
    private SelectorEntity selectorEntity;

    @OneToOne(mappedBy = "link", cascade = CascadeType.ALL)
    private TourEntity tourEntity;

    public LinkEntity(String link, boolean isArchive, int errorCount, SelectorEntity selectorEntity) {
        this.link = link;
        this.isArchive = isArchive;
        this.errorCount = errorCount;
        this.selectorEntity = selectorEntity;
    }
}

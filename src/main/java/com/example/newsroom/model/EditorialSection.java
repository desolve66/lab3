
package com.example.newsroom.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "editorial_sections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditorialSection extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean isAvailableForPublication = true;
}

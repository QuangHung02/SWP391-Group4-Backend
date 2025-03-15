package com.example.pregnancy_tracking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class PregnancyStandardId implements Serializable {
    @Column(name = "week")
    private Integer week;
    
    @Column(name = "fetus_number")
    private Integer fetusNumber;

    public PregnancyStandardId() {}

    public PregnancyStandardId(Integer week, Integer fetusNumber) {
        this.week = week;
        this.fetusNumber = fetusNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PregnancyStandardId that = (PregnancyStandardId) o;
        return Objects.equals(week, that.week) && Objects.equals(fetusNumber, that.fetusNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(week, fetusNumber);
    }
}

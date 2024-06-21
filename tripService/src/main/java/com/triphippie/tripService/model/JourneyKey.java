package com.triphippie.tripService.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class JourneyKey implements Serializable {
    private Long trip_id;

    private Long id;

    public JourneyKey(Long trip_id, Long id) {
        this.trip_id = trip_id;
        this.id = id;
    }
}

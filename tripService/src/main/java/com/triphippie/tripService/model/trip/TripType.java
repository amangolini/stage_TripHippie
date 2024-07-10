package com.triphippie.tripService.model.trip;

public enum TripType {
    FAMIGLIE(1),
    CULTURALE(2),
    ESCURSIONE(3),
    RELAX(4),
    DEGUSTAZIONE(5);

    private Integer id;

    private TripType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static TripType valueOf(Integer id) {
        for (TripType value : values())
            if (value.getId().equals(id))
                return value;
        return null;
    }
}

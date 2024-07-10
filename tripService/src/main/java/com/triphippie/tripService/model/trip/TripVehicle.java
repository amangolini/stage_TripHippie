package com.triphippie.tripService.model.trip;

public enum TripVehicle {
    AUTO(1),
    AEREO(2),
    BICICLETTA(3),
    TRENO(4),
    MOTOCICLO(5);

    private Integer id;

    private TripVehicle(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static TripVehicle valueOf(Integer id) {
        for (TripVehicle value : values())
            if (value.getId().equals(id))
                return value;
        return null;
    }
}

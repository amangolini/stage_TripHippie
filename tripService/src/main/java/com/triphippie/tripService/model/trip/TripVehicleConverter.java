package com.triphippie.tripService.model.trip;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TripVehicleConverter implements AttributeConverter<TripVehicle, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TripVehicle attribute) {
        return attribute.getId();
    }

    @Override
    public TripVehicle convertToEntityAttribute(Integer dbData) {
        return TripVehicle.valueOf(dbData);
    }
}

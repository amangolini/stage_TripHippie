package com.triphippie.tripService.model.trip;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TripTypeConverter implements AttributeConverter<TripType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(TripType attribute) {
        return attribute.getId();
    }

    @Override
    public TripType convertToEntityAttribute(Integer dbData) {
        return TripType.valueOf(dbData);
    }
}

package um.backend.selection.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import um.backend.selection.SelectedSeat;

import java.util.Collections;
import java.util.List;

@Converter
public class SelectedSeatListConverter implements AttributeConverter<List<SelectedSeat>, String> {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<List<SelectedSeat>> TYPE = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(List<SelectedSeat> attribute) {
        try {
            return attribute == null ? "[]" : MAPPER.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error serializing seats", e);
        }
    }

    @Override
    public List<SelectedSeat> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isBlank()) return Collections.emptyList();
            return MAPPER.readValue(dbData, TYPE);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
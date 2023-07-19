package com.despegar.javatemplate.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecordNamingStrategyPatchModuleTest {
    public record SampleItem(int itemId) { }

    /* Si este test empieza a fallar (o sea que NO falla al deserializar) es porque
     * se fixeo el bug en jackson y ya se puede eliminar el módulo RecordNamingStrategyPatchModule*/
    @Test
    void testNamingStrategyDeserializeRecordBug() {
        JsonMapper STANDARD_MAPPER = JsonMapper.builder()
                .addModule(new ParameterNamesModule())
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .build();

        assertThrows(JsonMappingException.class, () ->
                STANDARD_MAPPER.readValue(""" 
                        {"item_id": 5}
                        """, SampleItem.class), "https://github.com/FasterXML/jackson-databind/issues/2992 has been fixed!");
    }

    @Test
    void testNamingStrategyDeserializeRecordLocallyFixed() throws JsonProcessingException {
        JsonMapper PATCHED_MAPPER = JsonMapper.builder()
                .addModule(new ParameterNamesModule())
                .addModule(new RecordNamingStrategyPatchModule())
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .build();

        assertEquals(new SampleItem(5),
                PATCHED_MAPPER.readValue("""
                        {"item_id": 5}
                        """, SampleItem.class));
    }
}

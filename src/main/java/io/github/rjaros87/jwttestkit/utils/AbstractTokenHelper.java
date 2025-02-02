package io.github.rjaros87.jwttestkit.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import net.datafaker.Faker;

import java.util.Map;

@Getter
public abstract class AbstractTokenHelper {
    protected static final ObjectMapper objectMapper = new ObjectMapper();
    protected static final Faker FAKER = new Faker();

    public Map<String, Object> toMap() {
        return objectMapper.convertValue(this, Map.class);
    }
}

package io.github.rjaros87.jwttestkit.model;

import java.util.Map;

public interface Claims {
    Map<String, Object> toMap();
    Long getExp();
}

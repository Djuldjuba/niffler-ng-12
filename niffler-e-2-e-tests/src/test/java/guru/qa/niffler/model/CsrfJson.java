package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CsrfJson(
        @JsonProperty("token") String token
) {
}
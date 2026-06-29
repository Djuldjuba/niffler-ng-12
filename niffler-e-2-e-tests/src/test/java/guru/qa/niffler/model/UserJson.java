package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import java.util.UUID;

public record UserJson(
        @JsonProperty("id") UUID id,
        @JsonProperty("username") String username,
        @JsonProperty("password") String password,
        @JsonProperty("enabled") Boolean enabled,
        @JsonProperty("account_non_expired") Boolean accountNonExpired,
        @JsonProperty("account_non_locked") Boolean accountNonLocked,
        @JsonProperty("credentials_non_expired") Boolean credentialsNonExpired,
        TestData testData
) {

    public static UserJson fromEntity(AuthUserEntity entity) {
        return new UserJson(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getEnabled(),
                entity.getAccountNonExpired(),
                entity.getAccountNonLocked(),
                entity.getCredentialsNonExpired(),
                null
        );
    }

    public UserJson(String username, String password) {
        this(null, username, password, true, true, true, true, null);
    }

    public UserJson(String username, String password, TestData testData) {
        this(null, username, password, true, true, true, true, testData);
    }

    public UserJson addTestData(TestData testData) {
        return new UserJson(
                this.id,
                this.username,
                this.password,
                this.enabled,
                this.accountNonExpired,
                this.accountNonLocked,
                this.credentialsNonExpired,
                testData
        );
    }
}
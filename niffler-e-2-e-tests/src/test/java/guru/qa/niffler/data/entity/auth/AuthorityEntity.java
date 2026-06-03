package guru.qa.niffler.data.entity.auth;

import guru.qa.niffler.model.AuthorityJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class AuthorityEntity implements Serializable {
    private UUID id;
    private Authority authority;
    private AuthUserEntity user;

    public static AuthorityEntity fromJson(AuthorityJson json) {
        AuthorityEntity ae = new AuthorityEntity();
        ae.setId(json.id());
        ae.setAuthority(json.authority());
        AuthUserEntity userEntity = new AuthUserEntity();
        userEntity.setId(json.user());
        ae.setUser(userEntity);
        return ae;
    }
}

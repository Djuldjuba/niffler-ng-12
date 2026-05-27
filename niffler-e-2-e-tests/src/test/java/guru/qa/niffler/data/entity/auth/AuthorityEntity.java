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
    private UserEntity user;

    public static AuthorityEntity fromJson(AuthorityJson json) {
        AuthorityEntity ae = new AuthorityEntity();
        ae.setId(json.id());
        ae.setAuthority(json.authority());
        UserEntity userEntity = new UserEntity();
        userEntity.setId(json.user());
        ae.setUser(userEntity);
        return ae;
    }
}

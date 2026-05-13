package guru.qa.niffler.data.entity.userdata;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserdataUserJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class UserEntity implements Serializable {
    private UUID id;
    private String username;
    private CurrencyValues currency;
    private String fullname;
    private String firstname;
    private String surname;
    private byte[] photo;
    private byte[] photoSmall;

    public static UserEntity fromJson(UserdataUserJson json) {
        UserEntity ue = new UserEntity();
        ue.setId(json.id());
        ue.setUsername(json.username());
        ue.setFirstname(json.firstname());
        ue.setSurname(json.surname());
        ue.setFullname(json.fullname());
        ue.setCurrency(json.currency());

        if (json.photo() != null) {
            ue.setPhoto(json.photo().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }

        if (json.photoSmall() != null) {
            ue.setPhotoSmall(json.photoSmall().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }

        return ue;
    }
}

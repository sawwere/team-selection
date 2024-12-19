package ru.sfedu.teamselection.service.security;

import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import ru.sfedu.teamselection.domain.User;

@Getter
@Setter
public class OidcUserImpl extends DefaultOidcUser {
    private final User user;

    public OidcUserImpl(Collection<? extends GrantedAuthority> authorities,
                        OidcIdToken idToken,
                        OidcUserInfo userInfo,
                        User user) {
        super(authorities, idToken, userInfo);
        this.user = user;
    }
}

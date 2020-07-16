package com.ppdai.auth.idp.local;

import com.ppdai.auth.dao.ClientRepository;
import com.ppdai.auth.dao.UserRepository;
import com.ppdai.auth.idp.Authentication;
import com.ppdai.auth.idp.AuthenticationProvider;
import com.ppdai.auth.common.identity.DefaultIdentity;
import com.ppdai.auth.common.identity.Identity;
import com.ppdai.auth.po.ClientEntity;
import com.ppdai.auth.po.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * local authentication provider
 *
 */
public class LocalAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ClientRepository clientRepo;

    public Identity loadClientIdentity(String clientId) {
        DefaultIdentity identity = null;

        ClientEntity client = clientRepo.findByIdEx(clientId);
        if (client != null) {
            identity = new DefaultIdentity(clientId);
            UserEntity user = client.getOwner();
            if (user != null) {
                identity.setEmail(user.getEmail());
                identity.setRole(user.getRoles());
                identity.setOrganzation(null);
            }
        }

        return identity;
    }

    @Override
    public Identity load(String userName) {
        DefaultIdentity identity = null;

        UserEntity user = userRepo.findOneByName(userName);
        if (user != null) {
            identity = new DefaultIdentity(userName);
            identity.setEmail(user.getEmail());
            identity.setRole(user.getRoles());
            identity.setOrganzation(null);
        }

        return identity;
    }

    @Override
    public Identity load(Authentication authentication) {
        return load(authentication.getPrincipal().toString());
    }

    @Override
    public Class<? extends AuthenticationProvider> support() {
        return this.getClass();
    }

}

package com.ppdai.auth.idp.atlas;

import com.ppdai.atlas.client.api.UserControllerApi;
import com.ppdai.atlas.client.model.OrgDto;
import com.ppdai.atlas.client.model.ResponseUserDto;
import com.ppdai.atlas.client.model.RoleDto;
import com.ppdai.atlas.client.model.UserDto;
import com.ppdai.auth.idp.Authentication;
import com.ppdai.auth.idp.AuthenticationProvider;
import com.ppdai.auth.common.identity.DefaultIdentity;
import com.ppdai.auth.common.identity.Identity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * atlas authentication provider
 *
 */
@Slf4j
public class AtlasAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserControllerApi atlasUserControllerApi;

    @Override
    public Identity load(String userName) {
        DefaultIdentity identity = null;

        try {
            String userRoles = null;
            String userOrg = null;
            String userEmail = null;
            UserDto userDto = null;

            ResponseUserDto response = atlasUserControllerApi.findUserByUserNameUsingGET(userName);
            if (response.getCode() >= 0) {
                userDto = response.getDetail();
                if (userDto != null) {
                    List<RoleDto> roleDtoList = userDto.getRoles();
                    if (roleDtoList != null) {
                        List<String> roleList = roleDtoList.stream().map(m -> m.getName()).collect(Collectors.toList());
                        if (!roleList.isEmpty()) {
                            userRoles = StringUtils.join(roleList.toArray(), ",");
                        }
                    }

                    OrgDto orgDto = userDto.getOrgDto();
                    if (orgDto != null) {
                        userOrg = orgDto.getName();
                    }

                    userEmail = userDto.getEmail();
                }
            }

            if (userDto != null) {
                identity = new DefaultIdentity(userName);
                identity.setRole(userRoles);
                identity.setOrganzation(userOrg);
                identity.setEmail(userEmail);
            }

        } catch (Exception exception) {
            log.error("failed to fetch user role/organization by atlas.", exception);
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

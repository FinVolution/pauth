import jwt_decode from 'jwt-decode';

export default {

    save(token) {
        localStorage.setItem("jwt-token", token);
    },

    read() {
        return localStorage.getItem("jwt-token");
    },

    clear() {
        localStorage.removeItem("jwt-token");
    },

    getUserRoles() {
        let userRoles = null;
        let jwt = localStorage.getItem("jwt-token");
        if (jwt != null) {
            let jwtInfo = jwt_decode(jwt);
            if (jwtInfo != null) {
                userRoles = (jwtInfo.user_role != null) ? jwtInfo.user_role : null;
                if (userRoles != null) {
                    userRoles = userRoles.split(',');
                }
            }
        }
        return userRoles;
    }

}
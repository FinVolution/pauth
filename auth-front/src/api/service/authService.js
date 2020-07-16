import restApi from '../restApi'

export default {

    doAuth(token){
        let url = '/api/auth?code=' + token;
        return restApi.doGetRequest(url);
    },
    doLogin(request = {}){
        let url = '/api/auth/login';
        return restApi.doPostRequest(url, request.token);
    },
    saveAccountUserInfo(request = {}){
        let url = "api/auth/setUserInfo?email=" + request.usermail + "&username=" + request.username;
        return restApi.doPostRequest(url);
    },
    saveAccountPassword(request = {}){
        let url = "api/auth/setPassword";
        return restApi.doPostRequest(url, request.token);
    }

}

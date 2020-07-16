import restApi from '../restApi'

export default {

    fetchToken(request = {}){
        let url = 'api/token?code=' + request.code;
        return restApi.doGetRequest(url);
    },
    refreshToken(request = {}){
        let url = 'api/refresh?refresh_token=' + request.refresh_token;
        return restApi.doGetRequest(url);
    }

}

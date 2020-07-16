import restApi from '../restApi'

export default {
    fetchToken(request = {}) {
        let url = 'api/token/fetch?code=' + request.code;
        return restApi.doGetRequest(url);
    },
    refreshToken(request = {}) {
        let url = 'api/token/refresh?refresh_token=' + request.refresh_token;
        return restApi.doGetRequest(url);
    },
    revokeToken(request = {}) {
        let url = 'api/token/revoke?token=' + request.token;
        return restApi.doGetRequest(url);
    },
    fetchLoginUrl(request = {}) {
        let url = null;
        if (request != null && request.redirect_url != null) {
            url = 'api/login/url?redirect_url=' + encodeURIComponent(request.redirect_url);
        } else {
            url = 'api/login/url';
        }
        return restApi.doGetRequest(url);
    },
    fetchTestData(request = {}) {
        let url = 'api/test/fetch';
        return restApi.doGetRequest(url);
    },
    updateTestData(request = {}) {
        let url = 'api/test/update?newData=' + request.newData;
        return restApi.doPostRequest(url);
    }
}

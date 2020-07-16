import restApi from '../restApi'

export default {


    // admin
    getClientList(request = {}) {
        let url = '/api/clients/all';
        return restApi.doGetRequest(url);
    },
    getClientsByPage(request = {}) {
        let url = '/api/clients?clientId=' + request.clientId + '&ownerId=' + request.ownerId + '&page=' + request.page + '&size=' + request.size;
        return restApi.doGetRequest(url);
    },
    updateClient(request = {}) {
        let url = '/api/clients/' + request.id;
        return restApi.doPutRequest(url, request);
    },
    deleteClient(request = {}) {
        let url = '/api/clients/' + request.clientId;
        return restApi.doDeleteRequest(url);
    },
    getUserList(request = {}) {
        let url = '/api/users/all';
        return restApi.doGetRequest(url);
    },
    getUsersByPage(request = {}) {
        let url = '/api/users?name=' + request.name + '&page=' + request.page + '&size=' + request.size;
        return restApi.doGetRequest(url);
    },
    getIssuedAccessTokens(request = {}) {
        let url = '/api/access_tokens/all';
        return restApi.doGetRequest(url);
    },
    getAccessTokensByPage(request = {}) {
        let url = '/api/access_tokens?userId=' + request.userId + '&clientId=' + request.clientId + '&page=' + request.page + '&size=' + request.size;
        return restApi.doGetRequest(url);
    },
    getIssuedRefreshTokens(request = {}) {
        let url = '/api/refresh_tokens/all';
        return restApi.doGetRequest(url);
    },
    getRefreshTokensByPage(request = {}) {
        let url = '/api/refresh_tokens?userId=' + request.userId + '&clientId=' + request.clientId + '&page=' + request.page + '&size=' + request.size;
        return restApi.doGetRequest(url);
    },
    revokeAccessToken(request = {}) {
        let url = '/api/access_tokens/' + request.tokenId;
        return restApi.doDeleteRequest(url);
    },
    revokeRefreshToken(request = {}) {
        let url = '/api/refresh_tokens/' + request.tokenId;
        return restApi.doDeleteRequest(url);
    },
    getAuditLogsByPage(request = {}) {
        let url = '/api/audit?page=' + request.page + '&size=' + request.size;
        return restApi.doGetRequest(url);
    },
    issueLongTimeToken(request = {}) {
        let url = '/api/longtimeToken';
        return restApi.doPostRequest(url, request);
    },
    extendTokenExpiration(request = {}) {
        let url = '/api/access_tokens/extend';
        return restApi.doPostRequest(url, request);
    },

    // user
    getMySessions(request = {}) {
        let url = '/api/sessions/all?username=' + request.username;
        return restApi.doGetRequest(url);
    },
    getSessionsByPage(request = {}) {
        let url = '/api/sessions?username=' + request.username + '&clientId=' + request.clientId + '&page=' + request.page + '&size=' + request.size;
        return restApi.doGetRequest(url);
    },
    revokeSession(request = {}) {
        let url = '/api/sessions/' + request.sessionId;
        return restApi.doDeleteRequest(url);
    },
    getAllAuthCodes(request = {}) {
        let url = '/api/auth_codes/all';
        return restApi.doGetRequest(url);
    },
    getAuthCodesByPage(request = {}) {
        let url = '/api/auth_codes?userId=' + request.userId + '&clientId=' + request.clientId + '&page=' + request.page + '&size=' + request.size;
        return restApi.doGetRequest(url);
    },
    getMyAuthCodes(request = {}) {
        let url = '/api/auth_codes/all?username=' + request.username;
        return restApi.doGetRequest(url);
    },
    revokeAuthCode(request = {}) {
        let url = '/api/auth_codes/' + request.codeId;
        return restApi.doDeleteRequest(url);
    },
    getMyAccessTokens(request = {}) {
        let url = '/api/access_tokens/all?username=' + request.username;
        return restApi.doGetRequest(url);
    },
    getMyRefreshTokens(request = {}) {
        let url = '/api/refresh_tokens/all?username=' + request.username;
        return restApi.doGetRequest(url);
    },

    // client development
    registerClient(request = {}) {
        let url = '/api/clients';
        return restApi.doPostRequest(url, request);
    },
    getMyClients(request = {}) {
        let url = '/api/clients/all?username=' + request.username;
        return restApi.doGetRequest(url);
    },

    // oauth2 - authorization endpoint
    introspectClient(client = {}) {
        let url = '/api/clients/introspect';
        return restApi.doPostRequest(url, client);
    },
    generateAuthCode(client = {}) {
        let url = '/oauth2/authorize';
        return restApi.doPostRequest(url, client);
    }
}

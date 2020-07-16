import * as types from '../mutation-types'
import {api} from '../../api'

/**
 *
 * pAuth store
 *
 */

// initial state
const state = {
    clients: [],
    clientList: [],
    clientCount: 0,
    userList: [],
    userCount: 0,
    sessionList: [],
    sessionCount: 0,
    auditLogList: [],
    auditLogCount: 0,
    issuedToken: {
        accessTokenList: [],
        refreshTokenList: [],
        authCodeList: [],
        tokenCount: 0
    },
    userOwned: {
        mySessions: [],
        myAuthCodes: [],
        myRefreshTokens: [],
        myAccessTokens: [],
        myClients: []
    },
    authApproved: {
        isClientValid: null,
        approveDirectly: false,
        isApproved: false,
        clientId: null,
        redirectUrl: null,
        authCode: null,
    }
};

// getters
const getters = {
    getAllClients: state => state.clients,
    getClientList: state => state.clientList,
    getClientCount: state => state.clientCount,
    getUserList: state => state.userList,
    getUserCount: state => state.userCount,
    getSessionList: state => state.sessionList,
    getSessionCount: state => state.sessionCount,
    getAuditLogList: state => state.auditLogList,
    getAuditLogCount: state => state.auditLogCount,
    getAccessTokens: state => state.issuedToken.accessTokenList,
    getRefreshTokens: state => state.issuedToken.refreshTokenList,
    getAuthCodes: state => state.issuedToken.authCodeList,
    getTokenCount: state => state.issuedToken.tokenCount,
    getMyClients: state => state.userOwned.myClients,
    getMySessions: state => state.userOwned.mySessions,
    getMyAuthCodes: state => state.userOwned.myAuthCodes,
    getMyAccessTokens: state => state.userOwned.myAccessTokens,
    getMyRefreshTokens: state => state.userOwned.myRefreshTokens,
    isClientValid: state => state.authApproved.isClientValid,
    isClientApproveDirect: state => state.authApproved.approveDirectly,
    isClientApproved: state => state.authApproved.isApproved,
    getAuthResult: state => {
        return {
            'clientId': state.authApproved.clientId,
            'redirectUrl': state.authApproved.redirectUrl,
            'authCode': state.authApproved.authCode
        }
    },
};

// actions
const actions = {

    /**
     * 发送请求到后端服务，获取client列表
     * @param commit    store state更新提交者
     * @param dispatch  store action分发者
     */
    fetchClientList({commit, dispatch}) {
        api.pauthService.getClientList().then(function (resp) {
            if (resp.data != null && resp.data.code != null && resp.data.code >= 0) {
                commit(types.REFRESH_CLIENT_LIST, resp.data.details);
            }
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，获取分页client列表
     * @param commit    store state更新提交者
     * @param dispatch  store action分发者
     * @param data      附带client信息的数据对象，data格式为 {clientId: a, ownerId: b, page: c, size: d}
     */
    fetchClientsByPage({commit, dispatch}, data) {
        api.pauthService.getClientsByPage(data).then(function (resp) {
            if (resp.data != null && resp.data.code != null && resp.data.code >= 0) {
                commit(types.REFRESH_CLIENTS_BY_PAGE, resp.data.details);
            }
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，修改指定client
     * @param dispatch  store action分发者
     * @param data      附带client信息的数据对象，data格式为 {clientInfo: x, queryParam: y}
     */
    updateClient({dispatch}, data) {
        api.pauthService.updateClient(data.clientInfo).then(function (resp) {
            dispatch("fetchClientsByPage", data.queryParam);
            dispatch("displayPromptByResponseMsg", resp);
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，删除指定client
     * @param dispatch  store action分发者
     * @param data      附带client信息的数据对象，data格式为 {clientId: x, queryParam: y}
     */
    deleteClient({dispatch}, data) {
        api.pauthService.deleteClient(data).then(function (resp) {
            dispatch("fetchClientsByPage", data.queryParam);
            dispatch("displayPromptByResponseMsg", resp);
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，注册新的Client
     * @param dispatch  store action分发者
     * @param data      附带client信息的数据对象，data格式为 {clientId: x, redirectUrl: y, description: z}
     */
    registerClient({dispatch}, data){
        api.pauthService.registerClient(data).then(function (resp) {
            dispatch("displayPromptByResponseMsg", resp);
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，获取用户列表
     * @param commit     store state更新提交者
     * @param dispatch  store action分发者
     */
    fetchUserList ({commit, dispatch}) {
        api.pauthService.getUserList().then(function (resp) {
            if (resp.data != null && resp.data.code != null && resp.data.code >= 0) {
                commit(types.REFRESH_USER_LIST, resp.data.details);
            }
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，获取分页用户列表
     * @param commit    store state更新提交者
     * @param dispatch  store action分发者
     * @param data      附带user信息的数据对象，data格式为 {name: x, page: y, size: z}
     */
    fetchUsersByPage({commit, dispatch}, data) {
        api.pauthService.getUsersByPage(data).then(function (resp) {
            if (resp.data != null && resp.data.code != null && resp.data.code >= 0) {
                commit(types.REFRESH_USERS_BY_PAGE, resp.data.details);
            }
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，获取Access Token列表
     * @param commit    store state更新提交者
     * @param dispatch  store action分发者
     */
    fetchAccessTokenList({commit, dispatch}) {
        api.pauthService.getIssuedAccessTokens().then(function (resp) {
            if (resp.data != null && resp.data.code != null && resp.data.code >= 0) {
                commit(types.REFRESH_ACCESS_TOKEN_LIST, resp.data.details);
            }
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，获取Access Token分页列表
     * @param commit    store state更新提交者
     * @param dispatch  store action分发者
     * @param data      附带token信息的数据对象，data格式为 {userId: a, clientId: b, page: c, size: d}
     */
    fetchAccessTokensByPage({commit, dispatch}, data) {
        api.pauthService.getAccessTokensByPage(data).then(function (resp) {
            if (resp.data != null && resp.data.code != null && resp.data.code >= 0) {
                commit(types.REFRESH_ACCESS_TOKENS_BY_PAGE, resp.data.details);
            }
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，注销Access Token
     * @param dispatch  store action分发者
     * @param data      附带token信息的数据对象，data格式为 {tokenId: x, queryParam: y}
     */
    revokeAccessToken({dispatch}, data) {
        api.pauthService.revokeAccessToken(data).then(function (resp) {
            dispatch("fetchAccessTokensByPage", data.queryParam);
            dispatch("displayPromptByResponseMsg", resp);
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，获取Refresh Token列表
     * @param commit    store state更新提交者
     * @param dispatch  store action分发者
     */
    fetchRefreshTokenList ({commit, dispatch}) {
        api.pauthService.getIssuedRefreshTokens().then(function (resp) {
            if (resp.data != null && resp.data.code != null && resp.data.code >= 0) {
                commit(types.REFRESH_REFRESH_TOKEN_LIST, resp.data.details);
            }
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，获取Refresh Token分页列表
     * @param commit    store state更新提交者
     * @param dispatch  store action分发者
     * @param data      附带token信息的数据对象，data格式为 {userId: a, clientId: b, page: c, size: d}
     */
    fetchRefreshTokensByPage ({commit, dispatch}, data) {
        api.pauthService.getRefreshTokensByPage(data).then(function (resp) {
            if (resp.data != null && resp.data.code != null && resp.data.code >= 0) {
                commit(types.REFRESH_REFRESH_TOKENS_BY_PAGE, resp.data.details);
            }
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，注销Refresh Token
     * @param dispatch  store action分发者
     * @param data      附带token信息的数据对象，data格式为 {tokenId: x, queryParam: y}
     */
    revokeRefreshToken({dispatch}, data) {
        api.pauthService.revokeRefreshToken(data).then(function (resp) {
            dispatch("fetchRefreshTokensByPage", data.queryParam);
            dispatch("displayPromptByResponseMsg", resp);
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，获取当前用户注册的client列表
     * @param commit    store state更新提交者
     * @param dispatch  store action分发者
     * @param data      附带client信息的数据对象，data格式为 {username: x}
     */
    fetchMyClients ({commit, dispatch}, data) {
        api.pauthService.getMyClients(data).then(function (resp) {
            if (resp.data != null && resp.data.code != null && resp.data.code >= 0) {
                commit(types.REFRESH_MY_CLIENTS, resp.data.details);
            }
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，修改当前用户注册的client
     * @param dispatch  store action分发者
     * @param data      附带client信息的数据对象，data格式为 {client: x, username: y}
     */
    updateMyClient({dispatch}, data) {
        api.pauthService.updateClient(data.client).then(function (resp) {
            dispatch("fetchMyClients", data);
            dispatch("displayPromptByResponseMsg", resp);
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，删除当前用户注册的client
     * @param dispatch  store action分发者
     * @param data      附带client信息的数据对象，data格式为 {clientId: x, username: y}
     */
    deleteMyClient({dispatch}, data) {
        api.pauthService.deleteClient(data).then(function (resp) {
            dispatch("fetchMyClients", data);
            dispatch("displayPromptByResponseMsg", resp);
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，获取当前用户验证通过的登录sessions
     * @param commit    store state更新提交者
     * @param dispatch  store action分发者
     * @param data      附带session信息的数据对象，data格式为 {username: x}
     */
    fetchMySessions({commit, dispatch}, data) {
        api.pauthService.getMySessions(data).then(function (resp) {
            if (resp.data != null && resp.data.code != null && resp.data.code >= 0) {
                commit(types.REFRESH_MY_SESSIONS, resp.data.details);
            }
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，获取验证通过的登录session分页列表
     * @param commit    store state更新提交者
     * @param dispatch  store action分发者
     * @param data      附带session信息的数据对象，data格式为 {username: a, clientId: b, page: c, size: d}
     */
    fetchSessionsByPage({commit, dispatch}, data) {
        api.pauthService.getSessionsByPage(data).then(function (resp) {
            if (resp.data != null && resp.data.code != null && resp.data.code >= 0) {
                commit(types.REFRESH_SESSIONS_BY_PAGE, resp.data.details);
            }
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，注销登录session
     * @param dispatch  store action分发者
     * @param data      附带session信息的数据对象，data格式为 {sessionId: x, queryParam: y}
     */
    revokeSession({dispatch}, data) {
        api.pauthService.revokeSession(data).then(function (resp) {
            dispatch("fetchSessionsByPage", data.queryParam);
            dispatch("displayPromptByResponseMsg", resp);
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，注销当前用户的登录session
     * @param dispatch  store action分发者
     * @param data      附带session信息的数据对象，data格式为 {sessionId: x, username: y}
     */
    revokeMySession({dispatch}, data) {
        api.pauthService.revokeSession(data).then(function (resp) {
            dispatch("fetchMySessions", data);
            dispatch("displayPromptByResponseMsg", resp);
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，获取所有颁发的授权码
     * @param commit    store state更新提交者
     * @param dispatch  store action分发者
     * @param data      附带code信息的数据对象，data格式为 {username: x}
     */
    fetchAuthCodeList({commit, dispatch}) {
        api.pauthService.getAllAuthCodes().then(function (resp) {
            if (resp.data != null && resp.data.code != null && resp.data.code >= 0) {
                commit(types.REFRESH_AUTH_CODE_LIST, resp.data.details);
            }
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，获取授权码分页列表
     * @param commit    store state更新提交者
     * @param dispatch  store action分发者
     * @param data      附带code信息的数据对象，data格式为 {userId: a, clientId: b, page: c, size: d}
     */
    fetchAuthCodesByPage({commit, dispatch}, data) {
        api.pauthService.getAuthCodesByPage(data).then(function (resp) {
            if (resp.data != null && resp.data.code != null && resp.data.code >= 0) {
                commit(types.REFRESH_AUTH_CODES_BY_PAGE, resp.data.details);
            }
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，获取当前用户颁发的授权码
     * @param commit    store state更新提交者
     * @param dispatch  store action分发者
     * @param data      附带code信息的数据对象，data格式为 {username: x}
     */
    fetchMyAuthCodes({commit, dispatch}, data) {
        api.pauthService.getMyAuthCodes(data).then(function (resp) {
            if (resp.data != null && resp.data.code != null && resp.data.code >= 0) {
                commit(types.REFRESH_MY_AUTH_CODES, resp.data.details);
            }
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，注销授权码
     * @param dispatch  store action分发者
     * @param data      附带code信息的数据对象，data格式为 {codeId: x, queryParam: y}
     */
    revokeAuthCode({dispatch}, data) {
        api.pauthService.revokeAuthCode(data).then(function (resp) {
            dispatch("fetchAuthCodesByPage", data.queryParam);
            dispatch("displayPromptByResponseMsg", resp);
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，注销当前用户颁发的授权码
     * @param dispatch  store action分发者
     * @param data      附带code信息的数据对象，data格式为 {codeId: x, username: y}
     */
    revokeMyAuthCode({dispatch}, data) {
        api.pauthService.revokeAuthCode(data).then(function (resp) {
            dispatch("fetchMyAuthCodes", data);
            dispatch("displayPromptByResponseMsg", resp);
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，获取当前用户颁发的Access Tokens
     * @param commit    store state更新提交者
     * @param dispatch  store action分发者
     * @param data      附带token信息的数据对象，data格式为 {username: x}
     */
    fetchMyAccessTokens ({commit, dispatch}, data) {
        api.pauthService.getMyAccessTokens(data).then(function (resp) {
            if (resp.data != null && resp.data.code != null && resp.data.code >= 0) {
                commit(types.REFRESH_MY_ACCESS_TOKENS, resp.data.details);
            }
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，注销当前用户颁发的Access Token
     * @param dispatch  store action分发者
     * @param data      附带token信息的数据对象，data格式为 {tokenId: x, username: y}
     */
    revokeMyAccessToken({dispatch}, data) {
        api.pauthService.revokeAccessToken(data).then(function (resp) {
            dispatch("fetchMyAccessTokens", data);
            dispatch("displayPromptByResponseMsg", resp);
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，获取当前用户颁发的Refresh Tokens
     * @param commit    store state更新提交者
     * @param dispatch  store action分发者
     * @param data      附带token信息的数据对象，data格式为 {username: x}
     */
    fetchMyRefreshTokens ({commit, dispatch}, data) {
        api.pauthService.getMyRefreshTokens(data).then(function (resp) {
            if (resp.data != null && resp.data.code != null && resp.data.code >= 0) {
                commit(types.REFRESH_MY_REFRESH_TOKENS, resp.data.details);
            }
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 发送请求到后端服务，注销当前用户颁发的Refresh Token
     * @param dispatch  store action分发者
     * @param data      附带token信息的数据对象，data格式为 {tokenId: x, username: y}
     */
    revokeMyRefreshToken({dispatch}, data) {
        api.pauthService.revokeRefreshToken(data).then(function (resp) {
            dispatch("fetchMyRefreshTokens", data);
            dispatch("displayPromptByResponseMsg", resp);
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 检查client的合法性
     * @param commit    store state更新提交者
     * @param dispatch  store action分发者
     * @param data      附带client信息的数据对象，data格式为 {clientId: x, redirectUrl: y, scopes: [admin, dev]}
     */
    verifyClient({commit, dispatch}, data) {
        api.pauthService.introspectClient(data).then(function (resp) {
            if (resp.data != null && resp.data.code != null && resp.data.code >= 0) {
                commit(types.REFRESH_CLIENT_VALID_STATUS, resp.data.details);
            } else {
                commit(types.REFRESH_CLIENT_VALID_STATUS, {isValid: false})
                dispatch("displayPromptByResponseMsg", resp);
            }
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 在用户同意之后，为client申请授权码
     * @param commit    store state更新提交者
     * @param dispatch  store action分发者
     * @param data      附带client信息的数据对象，data格式为 {clientId: x, redirectUrl: y, scopes: z}
     */
    fetchAuthCode({commit, dispatch}, data) {
        api.pauthService.generateAuthCode(data).then(function (resp) {
            if (resp.data != null && resp.data.code != null) {
                commit(types.REFRESH_AUTH_CODE, resp.data);
            } else {
                dispatch("displayPromptByResponseMsg", resp);
            }
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    fetchAuditLogsByPage({commit, dispatch}, data) {
        api.pauthService.getAuditLogsByPage(data).then(function (resp) {
            if (resp.data != null && resp.data.code != null && resp.data.code >= 0) {
                commit(types.REFRESH_AUDIT_LOGS_BY_PAGE, resp.data.details);
            }
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    issueLongTimeToken({commit, dispatch}, data) {
        api.pauthService.issueLongTimeToken(data.token).then(function (resp) {
            dispatch("fetchAccessTokensByPage", data.queryParam);
            dispatch("displayPromptByResponseMsg", resp);
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    extendTokenExpiration({commit, dispatch}, data) {
        api.pauthService.extendTokenExpiration(data.token).then(function (resp) {
            dispatch("fetchAccessTokensByPage", data.queryParam);
            dispatch("displayPromptByResponseMsg", resp);
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    }

};

// mutations
const mutations = {
    [types.REFRESH_CLIENT_LIST] (state, data) {
        state.clients = data;
    },
    [types.REFRESH_CLIENTS_BY_PAGE] (state, data) {
        state.clientList = data.content;
        state.clientCount = data.totalElements;
    },
    [types.REFRESH_USER_LIST] (state, data) {
        state.userList = data;
    },
    [types.REFRESH_USERS_BY_PAGE] (state, data) {
        state.userList = data.content;
        state.userCount = data.totalElements;
    },
    [types.REFRESH_ACCESS_TOKEN_LIST] (state, data) {
        state.issuedToken.accessTokenList = data;
    },
    [types.REFRESH_ACCESS_TOKENS_BY_PAGE] (state, data) {
        state.issuedToken.accessTokenList = data.content;
        state.issuedToken.tokenCount = data.totalElements;
    },
    [types.REFRESH_REFRESH_TOKEN_LIST] (state, data) {
        state.issuedToken.refreshTokenList = data;
    },
    [types.REFRESH_REFRESH_TOKENS_BY_PAGE] (state, data) {
        state.issuedToken.refreshTokenList = data.content;
        state.issuedToken.tokenCount = data.totalElements;
    },
    [types.REFRESH_MY_CLIENTS] (state, data) {
        state.userOwned.myClients = data;
    },
    [types.REFRESH_MY_SESSIONS] (state, data) {
        state.userOwned.mySessions = data;
    },
    [types.REFRESH_SESSIONS_BY_PAGE] (state, data) {
        state.sessionList = data.content;
        state.sessionCount = data.totalElements;
    },
    [types.REFRESH_AUTH_CODE_LIST] (state, data) {
        state.issuedToken.authCodeList = data;
    },
    [types.REFRESH_AUTH_CODES_BY_PAGE] (state, data) {
        state.issuedToken.authCodeList = data.content;
        state.issuedToken.tokenCount = data.totalElements;
    },
    [types.REFRESH_MY_AUTH_CODES] (state, data) {
        state.userOwned.myAuthCodes = data;
    },
    [types.REFRESH_MY_ACCESS_TOKENS] (state, data) {
        state.userOwned.myAccessTokens = data;
    },
    [types.REFRESH_MY_REFRESH_TOKENS] (state, data) {
        state.userOwned.myRefreshTokens = data;
    },
    [types.REFRESH_CLIENT_VALID_STATUS] (state, data) {
        state.authApproved.isClientValid = data.isValid;
        if (data.directApprove != null) {
            state.authApproved.approveDirectly = data.directApprove;
        }
    },
    [types.REFRESH_AUTH_CODE] (state, data) {
        if (data != null) {
            state.authApproved.isApproved = true;
            state.authApproved.authCode = data.code;
            state.authApproved.redirectUrl = data.redirectUrl;
        } else {
            state.authApproved.isApproved = false;
        }
    },
    [types.REFRESH_AUDIT_LOGS_BY_PAGE] (state, data) {
        state.auditLogList = data.content;
        state.auditLogCount = data.totalElements;
    }
};

export default {
    state,
    getters,
    actions,
    mutations
}
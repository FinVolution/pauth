import {api} from '../../api';
import jwt_decode from 'jwt-decode';
import jwtTokenUtil from '../../utils/jwtTokenUtil'
import lastVisitUtil from '../../utils/lastVisitUtil'

// initial state
const state = {
    login: null,
    expired: null,
    usermail: null,
    username: null,
    userRoles: null,
    userOrg: null,
    invalidLogin: null,
    testData: null,
    lastVisitUrl: null,
    promptMessage: {
        code: null,
        details: null
    }
};

// getters
const getters = {
    getLoginState: state => state.login,
    getExpireState: state => state.expired,
    getUserMail: state => state.usermail,
    getUserName: state => state.username,
    getUserRoles: state => state.userRoles,
    getUserOrg: state => state.userOrg,
    getPromptMessage: state => state.promptMessage,
    getInvalidLoginState: state => state.invalidLogin,
    getTestData: state => state.testData,
    getLastVisit: state => state.lastVisitUrl
};

// actions
const actions = {
    /**
     * 获取当前应用的登录信息，从local storage获取登录的用户名
     * 使用场合：在主应用页面加载时调用该方法
     * @param commit    store state更新提交者
     */
    readLoginInfo({commit}) {
        let usermail = null;
        let username = null;
        let userRoles = null;
        let userOrg = null;
        try {
            let jwt = jwtTokenUtil.readAccess();
            let jwtInfo = jwt_decode(jwt);
            if (jwtInfo != null) {
                usermail = (jwtInfo.user_mail != null) ? jwtInfo.user_mail : null;
                username = (jwtInfo.user_name != null) ? jwtInfo.user_name : null;
                userRoles = (jwtInfo.user_role != null) ? jwtInfo.user_role : null;
                userOrg = (jwtInfo.user_org != null) ? jwtInfo.user_org : null;
            }
        } catch (e) {
            //console.log(e);
        }

        commit("refreshUserInfo", {usermail, username, userRoles, userOrg});
        commit("refreshLoginState", (usermail != null));

    },

    /**
     * 检查当前登录状态是否过期
     * 使用场合：在主应用页面中定时查看，并监测Expired状态，一旦发现过期，则提示用户重新登录
     * @param commit    store state更新提交者
     */
    checkExpired({commit}) {
        let expired = true;

        try {
            let jwt = jwtTokenUtil.readAccess();
            let jwtInfo = jwt_decode(jwt);
            if (jwtInfo != null) {
                let now = new Date();
                expired = ( jwtInfo.exp != null) ? (jwtInfo.exp * 1000 < now.getTime()) : true;
            }
        } catch (e) {
            //console.log(e);
        }

        commit("refreshExpireInfo", expired);
    },

    /**
     * 使用授权码来获取access/refresh token
     * 使用场合：在跳转到登录页面，拿到颁发的授权码，获取access/refresh token
     * @param commit    store state更新提交者
     * @param dispatch  store action分发者
     * @param data 附带应用信息的数据对象，data格式为 {code: x}，其中x为用户的授权码
     */
    fetchToken({commit, dispatch}, data) {
        api.authService.fetchToken(data).then(function (resp) {
            if (resp.data != null && resp.data.code != null && resp.data.code >=0) {
                jwtTokenUtil.saveAccess(resp.data.details.accessToken);
                jwtTokenUtil.saveRefresh(resp.data.details.refreshToken);
                dispatch("readLoginInfo");
            } else {
                commit("refreshInvalidLoginState", true);
                dispatch("displayPromptByResponseMsg", resp);
            }
        }.bind(this)).catch(function (err) {
            commit("refreshInvalidLoginState", true);
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));

    },

    /**
     * 通过refresh token去获取新的access token
     * 使用场合：在主应用页面中定时刷新access token，保证在每一个access token过期前获取新颁发的access token
     * @param dispatch  store action分发者
     */
    refreshToken({commit, dispatch}) {
        let refreshToken = jwtTokenUtil.readRefresh();
        if (refreshToken != null) {
            api.authService.refreshToken({'refresh_token': refreshToken}).then(function (resp) {
                if (resp.data != null && resp.data.code != null && resp.data.code >= 0) {
                    jwtTokenUtil.saveAccess(resp.data.details.accessToken);
                    dispatch("readLoginInfo");
                } else {
                    dispatch("displayPromptByResponseMsg", resp);
                }
            }.bind(this)).catch(function (err) {
                dispatch("displayPromptByResponseMsg", err.response);
            }.bind(this));
        }

    },

    /**
     * 注销当前的access token和refresh token
     * 使用场合：用户登出时注销access token同时注销refresh token
     * @param dispatch  store action分发者
     */
    revokeToken({dispatch}) {
        let accessToken = jwtTokenUtil.readAccess();
        if (accessToken != null) {
            api.authService.revokeToken({token: accessToken}).then(function (resp) {
                dispatch("clearToken");
            }.bind(this)).catch(function (err) {
                dispatch("displayPromptByResponseMsg", err.response);
            }.bind(this));
        }
    },

    login({dispatch}) {
        api.authService.fetchLoginUrl().then(function (resp) {
            if (resp.data != null && resp.data.code != null && resp.data.code >= 0) {
                window.location.href = resp.data.details;
            } else {
                dispatch("displayPromptByResponseMsg", resp);
            }
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    fetchTestData({commit, dispatch}) {
        api.authService.fetchTestData().then(function (resp) {
            commit("saveTestData", resp.data);
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    updateTestData({commit, dispatch}, data) {
        api.authService.updateTestData(data).then(function (resp) {
            commit("saveTestData", resp.data);
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 清除用户的token信息
     * 使用场合：当用户登出时，需要调用该方法清除所有token
     * @param commit    store state更新提交者
     */
    clearToken({dispatch}){
        jwtTokenUtil.clear();
        dispatch("readLoginInfo");
    },

    /**
     * 保存最近一次用户访问的地址url，用于登录后跳转
     * @param commit    store state更新提交者
     * @param url       用户访问的地址url
     */
    saveLastVisit({commit}, url){
        lastVisitUtil.save(url);
        commit("saveLastVisit", url);
    },

    fetchLastVisit({commit}){
        let url = lastVisitUtil.read();
        commit("saveLastVisit", url);
    },

    /**
     * 根据后端服务返回的响应消息，更新提示消息，使得在UI上显示
     * @param commit     store state更新提交者
     * @param response  后端服务返回的响应消息
     */
    displayPromptByResponseMsg({commit}, response){
        if (response != null && response.status != null && response.status == 200) {
            commit("refreshPromptMessage", {code: response.data.code, details: response.data.details});
        } else {
            let errorMsg = "请求失败，";
            if (response == null) {
                errorMsg += "访问后端服务返回异常。";
            } else if (response.status != null && response.data.details != null) {
                // 发生后端处理过的错误
                errorMsg += "返回码：" + response.status + "，返回消息：" + response.data.details;
            } else if (response.status != null && response.status >= 400 && response.status < 500) {
                // 发生4XX错误
                errorMsg += "返回码：" + response.status + "，返回消息：" + response.statusText;
            } else if (response.status != null && response.status >= 500 && response.status < 600) {
                // 发生5XX错误
                errorMsg += "请检查后端服务是否工作正常。";
                errorMsg += "返回码：" + response.status + "，返回消息：" + response.statusText;
            } else if (response.status != null) {
                errorMsg += "返回码：" + response.status + "，返回消息：" + response.statusText;
            } else {
                errorMsg += "请检查后端服务是否工作正常。";
                errorMsg += "消息：" + response;
            }
            commit("refreshPromptMessage", {code: -1, details: errorMsg});
        }
    },

    /**
     * 清空提示消息
     * @param commit     store state更新提交者
     */
    clearPrompt({commit}){
        commit("refreshPromptMessage", {code: null, details: null});
    }
};

// mutations
const mutations = {
    refreshUserInfo(state, {usermail, username, userRoles, userOrg}) {
        state.usermail = usermail;
        state.username = username;
        state.userRoles = (userRoles != null) ? userRoles.split(",") : userRoles;
        state.userOrg = userOrg;
    },
    refreshExpireInfo(state, data) {
        state.expired = data;
    },
    refreshPromptMessage(state, data) {
        state.promptMessage = data;
    },
    refreshLoginState(state, data) {
        state.login = data;
    },
    refreshInvalidLoginState(state, data) {
        state.invalidLogin = data;
    },
    saveLastVisit(state, data) {
        state.lastVisitUrl = data;
    },
    saveTestData(state, data) {
        state.testData = data;
    }
};

export default {
    state,
    getters,
    actions,
    mutations
}
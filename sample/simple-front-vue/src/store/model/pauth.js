import * as types from '../mutation-types';
import {api} from '../../api';
import jwt_decode from 'jwt-decode';
import jwtTokenUtil from '../../utils/jwtTokenUtil'


/**
 *
 * 存储app登录所需要的数据，包括登录的用户名、IP、应用标题等信息，对登录token的获取和刷新、清理
 *
 */

// initial state
const state = {
    login: null,
    expired: false,
    usermail: null,
    username: null,
    promptMessage: {
        code: null,
        details: null
    },
};

// getters
const getters = {
    getLoginState: state => state.login,
    getExpireState: state => state.expired,
    getUserMail: state => state.usermail,
    getUserName: state => state.username,
    getPromptMessage: state => state.promptMessage,
};

// actions
const actions = {

    /**
     * 获取当前应用的登录信息，从local storage获取登录的用户名
     * 使用场合：在主应用页面加载时调用该方法
     * @param commit    store state更新提交者
     */
    readLoginInfo({commit}){
        let usermail = null;
        let username = null;

        try {
            let jwt = jwtTokenUtil.readAccess();
            let jwtInfo = jwt_decode(jwt);
            if (jwtInfo != null) {
                username = jwtInfo.sub;
                usermail = jwtInfo.user_mail;
            }
        } catch (e) {
            //console.log(e);
        }

        commit(types.REFRESH_LOGIN_STATE, (username != null));
        commit(types.REFRESH_USER_INFO, {usermail, username});

    },

    /**
     * 检查当前登录状态是否过期
     * 使用场合：在主应用页面中定时查看，并监测Expired状态，一旦发现过期，则提示用户重新登录
     * @param commit    store state更新提交者
     */
    checkExpired({commit}){
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

        commit(types.REFRESH_EXPIRE_INFO, {expired});
    },

    /**
     * 使用授权码来获取access/refresh token
     * 使用场合：在跳转到登录页面，拿到颁发的授权码，获取access/refresh token
     * @param commit    store state更新提交者
     * @param dispatch  store action分发者
     * @param data 附带应用信息的数据对象，data格式为 {code: x}，其中x为用户的授权码
     */
    fetchToken({commit, dispatch}, data){

        api.tokenService.fetchToken(data).then(function (resp) {
            if (resp.data != null && resp.data.access_token != null && resp.data.refresh_token != null) {
                let details = resp.data;
                jwtTokenUtil.saveAccess(details.access_token);
                jwtTokenUtil.saveRefresh(details.refresh_token);
                dispatch("readLoginInfo");
            } else {
                commit(types.REFRESH_LOGIN_STATE, false);
                dispatch("displayPromptByResponseMsg", resp);
            }
        }.bind(this)).catch(function (err) {
            commit(types.REFRESH_LOGIN_STATE, false);
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));

    },

    /**
     * 通过refresh token去获取新的access token
     * 使用场合：在主应用页面中定时刷新access token，保证在每一个access token过期前获取新颁发的access token
     * @param dispatch  store action分发者
     */
    refreshToken({commit, dispatch}){

        let refreshToken = jwtTokenUtil.readRefresh();
        if (refreshToken != null) {
            api.tokenService.refreshToken({'refresh_token': refreshToken}).then(function (resp) {
                if (resp.data != null && resp.data.access_token != null) {
                    let details = resp.data;
                    jwtTokenUtil.saveAccess(details.access_token);
                    dispatch("readLoginInfo");
                } else {
                    commit(types.REFRESH_LOGIN_STATE, false);
                    //dispatch("displayPromptByResponseMsg", resp);
                }
            }.bind(this)).catch(function (err) {
                commit(types.REFRESH_LOGIN_STATE, false);
                //dispatch("displayPromptByResponseMsg", err.response);
            }.bind(this));
        }

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
     * 根据后端服务返回的响应消息，更新提示消息，使得在UI上显示
     * @param commit     store state更新提交者
     * @param response  后端服务返回的响应消息
     */
    displayPromptByResponseMsg({commit}, response){
        //console.log(response);
        if (response != null && response.status != null && response.status == 200) {
            let data = response.data;
            commit(types.REFRESH_PROMPT_MESSAGE, {code: data.code, details: data.message + data.details});
        } else {
            let errorMsg = "请求失败，";
            if (response == null) {
                errorMsg += "访问后端服务返回异常。";
            } else if (response.status != null && response.status >= 400 && response.status < 500) {
                // 发生4XX错误
                errorMsg += "返回码：" + response.status + "，返回消息：" + response.data.message + response.data.details;
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

            commit(types.REFRESH_PROMPT_MESSAGE, {code: -100000, details: errorMsg});
        }
    },

    /**
     * 清空提示消息
     * @param commit     store state更新提交者
     */
    clearPrompt({commit}){
        commit(types.REFRESH_PROMPT_MESSAGE, {code: null, details: null});
    },

};

// mutations
const mutations = {

    [types.REFRESH_USER_INFO] (state, {usermail, username}) {
        state.usermail = usermail;
        state.username = username;
    },
    [types.REFRESH_EXPIRE_INFO](state, {expired}){
        state.expired = expired;
    },
    [types.REFRESH_PROMPT_MESSAGE] (state, data){
        state.promptMessage = data;
    },
    [types.REFRESH_LOGIN_STATE] (state, data){
        state.login = data;
    }

};

export default {
    state,
    getters,
    actions,
    mutations
}
import * as types from '../mutation-types';
import jwt_decode from 'jwt-decode';
import {api} from '../../api';
import encryptor from '../../utils/encryptUtil'
import jwtTokenUtil from '../../utils/jwtTokenUtil'
import lastVisitedUtil from '../../utils/lastVisitedUtil'


/**
 *
 * 存储全局vue app的数据，包括登录的用户名，IP，应用标题等信息
 *
 */


// initial state
const state = {
    login: false,
    expired: false,
    usermail: null,
    username: null,
    userRoles: null,
    promptMessage: {
        code: null,
        details: null
    },
    lastVisitUrl: null
};

// getters
const getters = {
    getLoginState: state => state.login,
    getExpireState: state => state.expired,
    getUserMail: state => state.usermail,
    getUserName: state => state.username,
    getUserRoles: state => state.userRoles,
    getPromptMessage: state => state.promptMessage,
    getLastVisit: state => state.lastVisitUrl
};

// actions
const actions = {

    /**
     * 初始化当前应用的登录信息，从local storage获取登录的用户名
     * 使用场合：在页面加载时调用该方法
     * @param commit    store state更新提交者
     */
    initLoginInfo({commit}){
        let usermail = null;
        let username = null;
        let userRoles = null;

        try {
            let jwt = jwtTokenUtil.read();
            let jwtInfo = jwt_decode(jwt);
            if (jwtInfo != null) {
                usermail = (jwtInfo.user_mail != null) ? jwtInfo.user_mail : null;
                username = (jwtInfo.sub != null) ? jwtInfo.sub : null;
                userRoles = (jwtInfo.user_role != null) ? jwtInfo.user_role : null;
            }
        } catch (e) {
        }

        commit(types.REFRESH_USER_INFO, {usermail, username, userRoles});
    },

    /**
     * 检查当前登录状态是否过期
     *
     * @param commit    store state更新提交者
     */
    checkExpired({commit}){
        let expired = false;

        try {
            let jwt = jwtTokenUtil.read();
            let jwtInfo = jwt_decode(jwt);
            if (jwtInfo != null) {
                let now = new Date();
                expired = ( jwtInfo.exp != null) ? (jwtInfo.exp * 1000 < now.getTime()) : true;
            }
        } catch (e) {
        }

        commit(types.REFRESH_EXPIRE_INFO, {expired});
    },

    /**
     * 用户登出，清除相关登录信息
     * @param commit store state更新提交者
     */
    logout({commit, dispatch}){
        jwtTokenUtil.clear();
        dispatch("initLoginInfo");
    },

    /**
     * 用户登入，获取jwt-token并保存
     * @param commit    store state更新提交者
     * @param dispatch  store action分发者
     * @param data 附带应用信息的数据对象，data格式为 {username: x, userpwd: y}，其中x为用户的邮箱地址或者用户名，y为用户新密码
     */
    login({commit, dispatch}, data){
        let token = encryptor.generateToken(data);
        api.authService.doLogin({"token": token}).then(function (resp) {
            if (resp.data != null && resp.data.code != null && resp.data.code >= 0) {
                let token = resp.data.details;
                jwtTokenUtil.save(token);
                dispatch("initLoginInfo");
            } else {
                dispatch("displayPromptByResponseMsg", resp);
            }
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this));
    },

    /**
     * 设置账号邮箱和用户名
     * @param commit     store state更新提交者
     * @param dispatch  store action分发者
     * @param data 附带应用信息的数据对象，data格式为 {usermail: x, username: y}，其中x为用户的邮箱地址，y为用户名
     */
    setUserInfo({commit, dispatch}, data){
        api.authService.saveAccountUserInfo(data).then(function (resp) {
            if (resp.data != null && resp.data.code != null && resp.data.code >= 0) {
                let token = resp.data.details;
                jwtTokenUtil.save(token);
                dispatch("initLoginInfo");
                let response = {status: 200, data: {code: resp.data.code, message: resp.data.message, details: "用户信息设置成功。"}};
                dispatch("displayPromptByResponseMsg", response);
            } else {
                dispatch("displayPromptByResponseMsg", resp);
            }
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this))
    },

    /**
     * 设置账号用户密码
     * @param commit     store state更新提交者
     * @param dispatch  store action分发者
     * @param data 附带应用信息的数据对象，data格式为 {usermail: x, newpwd: y}，其中x为用户的邮箱地址，y为用户新密码
     */
    setUserPassword({commit, dispatch}, data){
        let token = encryptor.generateToken(data);
        api.authService.saveAccountPassword({"token": token}).then(function (resp) {
            dispatch("displayPromptByResponseMsg", resp);
        }.bind(this)).catch(function (err) {
            dispatch("displayPromptByResponseMsg", err.response);
        }.bind(this))
    },

    /**
     * 保存最近一次用户访问的地址url，用于登录后跳转
     * @param commit    store state更新提交者
     * @param url       用户访问的地址url
     */
    saveLastVisit({commit}, url){
        lastVisitedUtil.save(url);
        commit(types.SAVE_LAST_VISIT, url);
    },

    fetchLastVisit({commit}){
        let url = lastVisitedUtil.read();
        commit(types.SAVE_LAST_VISIT, url);
    },

    clearLastVisit({commit}){
        lastVisitedUtil.clear();
        commit(types.SAVE_LAST_VISIT, null);
    },

    /**
     * 根据后端服务返回的响应消息，更新提示消息，使得在UI上显示
     * @param commit     store state更新提交者
     * @param response  后端服务返回的响应消息
     */
    displayPromptByResponseMsg({commit}, response){
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
    [types.REFRESH_USER_INFO] (state, {usermail, username, userRoles}) {
        state.usermail = usermail;
        state.username = username;
        state.userRoles = (userRoles != null) ? userRoles.split(',') : userRoles;
        state.login = (usermail != null);
    },
    [types.REFRESH_EXPIRE_INFO](state, {expired}){
        state.expired = expired;
    },
    [types.REFRESH_PROMPT_MESSAGE] (state, data){
        state.promptMessage = data;
    },
    [types.SAVE_LAST_VISIT] (state, data){
        state.lastVisitUrl = data;
    }
};

export default {
    state,
    getters,
    actions,
    mutations
}
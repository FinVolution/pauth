import Vue from 'vue'

import VueRouter from 'vue-router'
Vue.use(VueRouter);

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
Vue.use(ElementUI);

import axios from 'axios'
import router from './router'
import store from './store'
import jwtTokenUtil from "./utils/jwtTokenUtil";


/**
 * enable axios ajax call in the vue component
 * please see the usage example in the ./pages/pages/demo/Ajax.vue
 * @type {AxiosStatic}
 */
Vue.prototype.$http = axios;

/**
 * enable the development mode
 * @type {boolean}
 */
Vue.config.devtools = process.env.NODE_ENV === 'development';

// http request 拦截器
axios.interceptors.request.use(
    config => {
        let jwtToken = jwtTokenUtil.readAccess();
        // 给http请求的header加上jwt-token
        config.headers['jwt-token'] = jwtToken;
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

/**
 * initialize the vue app with vuex store and vue router
 */
new Vue({
    store,
    router,
}).$mount('#app');


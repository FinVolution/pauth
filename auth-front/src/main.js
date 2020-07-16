import Vue from 'vue'
import Vuex from 'vuex'
import VueRouter from 'vue-router'
Vue.use(VueRouter);
Vue.use(Vuex);
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
Vue.use(ElementUI);

import VueParticles from 'vue-particles'
Vue.use(VueParticles);

import axios from 'axios'
import router from './router'
import store from './store'
import jwtTokenUtil from "./utils/jwtTokenUtil";
import jwt_decode from 'jwt-decode';


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

/**
 * 为admin用户和local用户分配路由权限
 */
router.beforeEach ((to, from, next) => {
    // console.log(store.state.app.userRoles)
    let userRoles = jwtTokenUtil.getUserRoles();
    if (to.name == 'admin' || to.name == 'clients' || to.name == 'users' || to.name == 'approvedsites' || to.name == 'tokens' || to.name == 'auditlogs') {
        let canGo = (userRoles != null) && userRoles.includes('admin');
        if (canGo) {
            next();
        } else {
            next({name: 'base'});
        }
    } else if (to.name == 'useraccount' || to.name == 'changeinfo' || to.name == 'changepwd') {
        let canGo = (userRoles != null) && userRoles.includes('local');
        if (canGo) {
            next();
        } else {
            next({name: 'base'});
        }
    } else {
        next();
    }
})

/**
 * initialize the vue app with vuex store and vue router
 */
new Vue({
    store,
    router,
}).$mount('#app');


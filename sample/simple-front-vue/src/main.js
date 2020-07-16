import Vue from 'vue'

import VueRouter from 'vue-router'
Vue.use(VueRouter);

import ElementUI from 'element-ui'
import "./assets/theme/lightblue/index.css";
Vue.use(ElementUI);

import axios from 'axios'
import router from './router'
import store from './store'


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
 * initialize the vue app with vuex store and vue router
 */
new Vue({
    store,
    router,
}).$mount('#app');


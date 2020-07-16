import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);

import Layout from '../pages/Layout.vue'
import BlankPage from '../pages/Blank.vue'
import Login from '../pages/Login.vue'

export default new Router({
    mode: 'hash', // mode option: 1. hash (default), 2. history
    routes: [{
        path: '',
        name: 'base',
        component: Layout,
        children: [{
            path: '',
            name: 'blank',
            component: BlankPage
        }]
    }, {
        path: '/login',
        name: 'Login',
        component: Login,
    }],
    linkActiveClass: 'active'
})

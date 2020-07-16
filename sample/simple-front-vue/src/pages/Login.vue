<template>
    <div class="login-wrapper">
        <div v-if="this.loading">
            正在登陆中，请稍后...
        </div>
        <div v-if="this.invalidLogin">
            <div><img src="../assets/img/fail_login.jpg"/></div>
            <div>当前登录失败，请回到首页重新尝试登录。</div>
            <el-button @click="returnHome">回到首页</el-button>
        </div>
    </div>
</template>

<script>
    import {mapGetters, mapActions} from 'vuex'

    export default {
        data: function () {
            return {
                loading: true,
                invalidLogin: false
            }
        },
        computed: {
            ...mapGetters({
                isLogin: 'getLoginState',
            })
        },
        methods: {
            ...mapActions(['fetchToken', 'readLoginInfo']),
            returnHome() {
                this.$router.push('/');
            },
        },
        mounted: function () {
            let query = this.$route.query;

            if (!(query.error == null)) {
                this.loading = false;
                this.invalidLogin = true;
            } else if (!(query.code == null)) {
                this.loading = true;
                this.invalidLogin = false;
                // fetch access/refresh token by auth code
                this.$store.dispatch('fetchToken', {'code': query.code});
            } else {
                this.loading = false;
                this.invalidLogin = true;
                this.$store.dispatch('readLoginInfo');
            }


        },
        watch: {
            isLogin: function () {

                if (this.isLogin != null) {

                    // 当获取用户登录信息完成，则跳转到应用首页
                    if (this.isLogin) {
                        this.loading = true;
                        this.invalidLogin = false;
                        this.returnHome();
                    } else {
                        this.loading = false;
                        this.invalidLogin = true;
                    }
                }

            }
        }
    }
</script>

<style scoped>

    .login-wrapper {
        position: relative;
        height: 100%;
        width: 100%;
        /*background-color: #8a8a8a;*/
        text-align: center;
        padding-top: 100px;
    }

    .login-btn button {
        width: 100%;
        height: 36px;
    }


</style>
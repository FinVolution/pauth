<template>
    <div class="header">
        <div class="title" @click="returnHome"><i class="el-icon-star-off logo"></i>{{title}}</div>
        <div class="user-info">
            <el-dropdown trigger="click" @command="handleCommand">
                <span class="el-dropdown-link">
                    <img class="user-logo" src="../assets/img/dog.jpg">
                    {{(username != null) ? username : "hello"}}
                </span>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item command="logout" v-if='isLogin'>退出</el-dropdown-item>
                    <el-dropdown-item command="login" v-else>登录</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
            <div v-if="isPromptExpire" class="expire-prompt">
                <div>您的当前登录会话已过期，请重新登录。</div>
                <div>
                    <el-button type="primary" @click="login">重新登录</el-button>
                </div>
            </div>
        </div>

    </div>
</template>

<script>
    import {mapGetters, mapActions} from 'vuex'

    export default {
        props: {
            title: {
                type: String,
                required: true,
                value: "标题"
            }
        },
        data: function () {
            return {
                isPromptExpire: false
            }
        },
        computed: {
            ...mapGetters({
                isLogin: 'getLoginState',
                username: 'getUserName',
                isExpired: 'getExpireState',
            })
        },
        watch: {
            isExpired: function () {
                // 当用户处于登录中的状态，但是登录令牌过期，则提示用户重新登录
                this.isPromptExpire = this.isExpired && this.isLogin;
            }
        },
        methods: {
            ...mapActions(['logout', 'checkExpired', 'refreshToken']),
            handleCommand(command) {
                if (command == 'logout') {
                    this.logout();
                } else if (command == 'login') {
                    this.login();
                }
            },
            returnHome() {
                // 回到首页
                this.$router.push('/');
            },
            logout(){
                // 登出时清空所有token
                this.$store.dispatch('clearToken');
            },
            login(){
                // 跳转到pauth进行单点登录，需提供client id/call back url/scope等等
                let pauthUrl = "http://localhost/#/authorize?";
                let redirectUrl = "http%3a%2f%2flocalhost%3a8400%2f%23%2flogin";
                let query = "response_type=code&client_id=demo&redirect_uri=" + redirectUrl + "&scope=user_role&state=1";
                window.location.href = pauthUrl + query;
            },
            onInterval: function () {
                if (!this.isLogin) {
                    // 一旦用户登出，则可以关闭定时器
                    clearInterval(this.internalTimer);
                    clearInterval(this.refreshTokenTimer);
                } else {
                    // 定期检查登录状态是否过期
                    this.$store.dispatch('checkExpired');
                }
            },
            refreshToken: function () {
                this.$store.dispatch('refreshToken');
            }
        },
        created () {
            this.refreshToken();
        },
        mounted: function () {
            // 每隔500ms触发一次定期检查
            this.internalTimer = setInterval(this.onInterval.bind(this), 500);
            // 每隔4分钟触发一次access token刷新
            this.refreshTokenTimer = setInterval(this.refreshToken.bind(this), 4 * 60 * 1000);
        },
        beforeDestroy: function () {
            // 清理定时器
            clearInterval(this.internalTimer);
            clearInterval(this.refreshTokenTimer);
        }
    };
</script>

<style scoped>

    .user-info {
        float: right;
        padding-right: 50px;
        font-size: 16px;
        color: #fff;
    }

    .user-info .el-dropdown-link {
        position: relative;
        display: inline-block;
        padding-left: 50px;
        color: #fff;
        cursor: pointer;
        vertical-align: middle;
    }

    .user-info .user-logo {
        position: absolute;
        left: 0;
        top: 15px;
        width: 40px;
        height: 40px;
        border-radius: 50%;
    }

    .el-dropdown-menu__item {
        text-align: center;
    }

    .expire-prompt {
        position: absolute;
        right: 0px;
        background: #fff;
        color: black;
        min-width: 150px;
        border-radius: 2px;
        border: 1px solid rgb(209, 219, 229);
        padding: 20px;
        z-index: 2000;
    }

    .expire-prompt div {
        text-align: center;
        margin: 0;
        line-height: 4em;
    }

</style>
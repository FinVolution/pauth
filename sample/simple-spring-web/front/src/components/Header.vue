<template>
    <div class="header">
        <div class="title" @click="returnHome"><i class="el-icon-menu logo"></i>{{title}}</div>
        <div class="user-info">
            <el-dropdown @command="handleCommand">
                <div class="el-dropdown-link">
                    <div v-if="username != null" class="user-logo"></div>
                    {{(username != null) ? username : "您好，请登录"}}
                </div>
                <el-dropdown-menu slot="dropdown" class="dropdown-menu">
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
                isLogoutOnStartup: false
            }
        },
        computed: {
            ...mapGetters({
                isLogin: 'getLoginState',
                username: 'getUserName',
                isExpired: 'getExpireState',
                promptMessage: 'getPromptMessage'
            }),
            isPromptExpire: function () {
                return this.isExpired && this.isLogin;
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
                this.$router.push('/');
            },
            onInterval: function () {
                if (!this.isLogin) {
                    // 一旦用户登出，则可以关闭定时器
                    clearInterval(this.internalTimer);
                    clearInterval(this.refreshTokenTimer);

                    // 若用户手动点击登出按钮，提示用户已经成功登出
                    if (!this.isLogoutOnStartup) {
                        this.$message.success("登出成功");
                    }
                } else {
                    // 定期检查登录状态是否过期
                    this.$store.dispatch('checkExpired');
                }
            },
            refreshToken: function () {
                this.$store.dispatch('refreshToken');
            },
            logout(){
                // 登出时注销并清空所有token
                this.$store.dispatch('revokeToken');
            },
            login(){
                this.$store.dispatch('saveLastVisit', this.$route.fullPath);
                this.$store.dispatch('login');
            }
        },
        watch: {
            promptMessage: function (newMessage) {
                if (newMessage.code != null) {
                    if (newMessage.code >= 0) {
                        this.$message.success(newMessage.details);
                    } else {
                        this.$message.error(newMessage.details);
                    }
                }
            }
        },
        created () {
            this.$store.dispatch('readLoginInfo');
            this.refreshToken();
        },
        beforeMount: function () {
            //若发现没有登录，提示用户登录
            if (!this.isLogin) {
                this.$message.warning("您好，请先登录。");
                this.isLogoutOnStartup = true;
            }
        },
        mounted: function () {
            // 定时器，每隔500ms触发一次定期检查
            this.internalTimer = setInterval(this.onInterval.bind(this), 500);
            // 每隔5小时触发一次access token刷新
            this.refreshTokenTimer = setInterval(this.refreshToken.bind(this), 5 * 60 * 60 * 1000);
        },
        beforeDestroy: function () {
            //清理定时器
            clearInterval(this.internalTimer);
            clearInterval(this.refreshTokenTimer);
        }
    };
</script>

<style scoped>

    .header {
        background-color: rgb(32, 160, 255);
        position: relative;
        box-sizing: border-box;
        width: 100%;
        height: 70px;
        font-size: 22px;
        line-height: 70px;
        color: #fff;
    }

    .header .title {
        display: table;
        cursor: pointer;
        margin-left: 20px;
        float: left;
        font-size: 24px;
    }

    .header .title:hover {
        color: #337ab7;
    }

    .header .title i {
        font-size: 28px;
    }

    .header .logo {
        margin-right: 5px;
    }

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
        background-size: 100% 100%;
        background-repeat: no-repeat;
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

    .dropdown-menu {
        min-width: 100px;
    }

</style>
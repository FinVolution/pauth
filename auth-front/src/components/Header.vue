<template>
    <div class="header">
        <div class="title" @click="returnHome"><i class="el-icon-star-on logo"></i>{{title}}</div>
        <div class="user-info">
            <el-dropdown @command="handleCommand">
                <div class="el-dropdown-link">
                    <img v-if="username != null" :src="imgUrl" class="user-logo">
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
                    <el-button type="primary" @click="logout">重新登录</el-button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import {mapGetters, mapActions} from 'vuex'
    import crypto from 'crypto'
    import Identicon from 'identicon.js'

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
            }
        },
        computed: {
            ...mapGetters({
                isLogin: 'getLoginState',
                isExpired: 'getExpireState',
                username: 'getUserName'
            }),
            isPromptExpire: function () {
                return this.isExpired && this.isLogin;
            },
            imgUrl: function () {
                let hash = crypto.createHash('md5');
                hash.update(this.username);
                let imgData = new Identicon(hash.digest('hex'), 40).toString();
                let imgUrl = 'data:image/png;base64,' + imgData;
                return imgUrl;
            }
        },
        methods: {
            ...mapActions(['initLoginInfo', 'logout', 'checkExpired']),
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
            onInterval: function() {
                // 检查登录状态是否过期
                this.$store.dispatch('checkExpired');
            },
            logout() {
                this.$store.dispatch('logout');
                this.$store.dispatch('saveLastVisit', this.$route.fullPath);
                this.$router.push('/login');
            },
            login() {
                this.$store.dispatch('saveLastVisit', this.$route.fullPath);
                this.$router.push('/login');
            }
        },
        created () {
            this.$store.dispatch('initLoginInfo');
        },
        beforeMount: function () {
            //若发现没有登录，提示用户登录
            if (!this.isLogin) {
                this.$message.warning("您好，请先登录");
            }
        },
        mounted: function () {
            this.internalTimer = setInterval(this.onInterval.bind(this), 500);
        },
        beforeDestroy: function () {
            clearInterval(this.internalTimer);
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

    .user-info .el-dropdown {
        display: block;
    }

    .user-info .el-dropdown-link {
        position: relative;
        display: inline-block;
        padding-left: 50px;
        color: #fff;
        cursor: pointer;
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

    .dropdown-menu {
        min-width: 100px;
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
<template>
    <div class="login-wrapper">
        <div class="login-form">
            <div class="login-form-title">Pauth登录</div>
            <div class="login-form-input">

                <el-form :model="loginForm" :rules="rules" ref="loginForm"
                         label-width="0px"
                         class="demo-ruleForm">
                    <el-form-item prop="username">
                        <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
                    </el-form-item>
                    <el-form-item prop="password">
                        <el-input type="password" v-model="loginForm.password" placeholder="请输入密码"
                                  @keyup.enter.native="submitForm('loginForm')"></el-input>
                    </el-form-item>
                    <el-button type="primary" @click="submitForm('loginForm')" class="login-btn">登录</el-button>
                </el-form>

            </div>
        </div>
        <vue-particles
                id="particles-js"
                color="#dedede"
                :particleOpacity="0.1"
                :particlesNumber="80"
                shapeType="circle"
                :particleSize="4"
                linesColor="#dedede"
                :linesWidth="1"
                :lineLinked="true"
                :lineOpacity="0.05"
                :linesDistance="150"
                :moveSpeed="2"
                :hoverEffect="true"
                hoverMode="grab"
                :clickEffect="false"
                clickMode="push"
                style="display: flex"
        >
        </vue-particles>
    </div>
</template>

<script>
    import {mapGetters, mapActions} from 'vuex'

    export default {
        data: function () {
            return {
                loginForm: {
                    username: '',
                    password: ''
                },
                rules: {
                    username: [
                        {required: true, message: '请输入用户名', trigger: 'blur'}
                    ],
                    password: [
                        {required: true, message: '请输入密码', trigger: 'blur'}
                    ]
                }
            }
        },
        computed: {
            ...mapGetters({
                isLogin: 'getLoginState',
                lastVisit: 'getLastVisit',
                promptMessage: 'getPromptMessage'
            })
        },
        methods: {
            submitForm(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        let data = {
                            "username": this.loginForm.username,
                            "userpwd": this.loginForm.password
                        };
                        this.$store.dispatch('login', data);
                    } else {
                        return false;
                    }
                });
            }
        },
        created () {
            this.$store.dispatch('initLoginInfo');
            this.$store.dispatch('fetchLastVisit');
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
            },
            isLogin: function (newLoginState) {
                if (newLoginState) {
                    this.$message.success("登录成功");

                    // 跳转回用户最近一次访问的地址
                    let lastVisit = this.lastVisit;
                    if (lastVisit != null && lastVisit != "null") {
                        this.$store.dispatch('saveLastVisit', null);
                        this.$router.push(lastVisit);
                    } else {
                        this.$router.push('/');
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
        background-color: #353535;
    }

    #particles-js {
        background-size: cover;
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
    }

    .login-form {
        position: relative;
        top: 250px;
        width: 500px;
        margin: 0 auto;
        text-align: center;
        z-index: 999;
    }

    .login-form-title {
        color: white;
        font-size: 30px;
        padding-bottom: 30px;
    }

    .login-form-input {
        position: absolute;
        width: 400px;
        padding: 50px;
        border-radius: 5px;
    }

    .login-btn {
        width: 100%;
        height: 36px;
    }

</style>
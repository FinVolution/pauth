<template>
    <div>

        <div>
            <el-row class="nav-bar">
                <el-col>
                    <el-breadcrumb separator="/">
                        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                        <el-breadcrumb-item>用户信息</el-breadcrumb-item>
                        <el-breadcrumb-item>修改密码</el-breadcrumb-item>
                    </el-breadcrumb>
                </el-col>
            </el-row>
        </div>

        <div class="embeded_form">
            <el-form :model="userInfo" ref="userInfo" label-width="100px" label-position="top">
                <el-form-item label="旧密码" prop="pass">
                    <el-input type="password" v-model="userInfo.originalPwd" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="新密码" prop="pass">
                    <el-input type="password" v-model="userInfo.newPwd" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item label="确认密码" prop="checkPass">
                    <el-input type="password" v-model="userInfo.confirmPwd" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="onSubmit()">修改</el-button>
                </el-form-item>
            </el-form>
        </div>


    </div>
</template>

<script>
    import {mapGetters, mapActions} from 'vuex'

    export default{
        data: function () {
            return {
                userInfo: {
                    originalPwd: "",
                    newPwd: "",
                    confirmPwd: ""
                }
            }
        },
        computed: {
            ...mapGetters({
                umail: 'getUserMail'
            })
        },
        methods: {
            onSubmit() {
                if (this.userInfo.originalPwd == "") {
                    this.$message.error("请输入旧密码");
                } else if (this.userInfo.newPwd == "") {
                    this.$message.error("请输入新密码");
                } else if (this.userInfo.newPwd != this.userInfo.confirmPwd) {
                    this.$message.error("确认密码不一致");
                } else {
                    let data = {
                        usermail: this.umail,
                        originalPwd: this.userInfo.originalPwd,
                        newpwd: this.userInfo.newPwd
                    };
                    this.$store.dispatch("setUserPassword", data);
                    this.userInfo = {
                        originalPwd: "",
                        newPwd: "",
                        confirmPwd: ""
                    };
                }

            }
        }
    }
</script>

<!--<style>-->
    <!--.change_pwd_form {-->
        <!--margin-top: 20px;-->
        <!--padding-top: 20px;-->
        <!--padding-right: 30px;-->
        <!--border: 1px solid #eaeefb;-->
        <!--border-radius: 4px;-->
        <!--transition: .2s;-->
    <!--}-->
<!--</style>-->
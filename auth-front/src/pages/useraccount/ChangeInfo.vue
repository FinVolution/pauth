<template>
    <div>
        <div>
            <el-row class="nav-bar">
                <el-col>
                    <el-breadcrumb separator="/">
                        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                        <el-breadcrumb-item>用户信息</el-breadcrumb-item>
                        <el-breadcrumb-item>修改信息</el-breadcrumb-item>
                    </el-breadcrumb>
                </el-col>
            </el-row>
        </div>

        <div class="embeded_form">
            <el-form :model="userInfo" ref="userInfo" label-width="100px" label-position="top">
                <el-form-item label="邮箱">
                    <el-input v-model="userInfo.mail"></el-input>
                </el-form-item>
                <el-form-item label="用户名">
                    <el-input v-model="userInfo.name"></el-input>
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
                    mail: "",
                    name: ""
                }
            }
        },
        computed: {
            ...mapGetters({
                umail: 'getUserMail',
                uname: 'getUserName'
            })
        },
        created: function () {
            this.userInfo.mail = this.umail;
            this.userInfo.name = this.uname;
        },
        methods: {
            onSubmit() {
                if (this.userInfo.mail == "") {
                    this.$message.error("邮箱不能为空，请重新输入。");
                } else if (this.userInfo.name == "") {
                    this.$message.error("用户名不能为空，请重新输入。");
                } else {
                    let data = {
                        usermail: this.userInfo.mail,
                        username: this.userInfo.name
                    };
                    this.$store.dispatch("setUserInfo", data);
                }
            }
        }
    }
</script>
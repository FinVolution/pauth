<template>
    <div>

        <div>
            <el-row class="nav-bar">
                <el-col>
                    <el-breadcrumb separator="/">
                        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                        <el-breadcrumb-item>应用开发</el-breadcrumb-item>
                        <el-breadcrumb-item>注册Client</el-breadcrumb-item>
                    </el-breadcrumb>
                </el-col>
            </el-row>
        </div>

        <div class="embeded_form">
            <el-form :model="clientInfo" ref="clientInfo" label-width="130px" label-position="top">
                <el-form-item label="Client Id">
                    <el-input v-model="clientInfo.clientId"></el-input>
                </el-form-item>
                <el-form-item label="重定向返回地址（校验用，支持正则表达式）">
                    <el-input v-model="clientInfo.redirectUrl"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input v-model="clientInfo.description"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="onSubmit()">注册</el-button>
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
                clientInfo: {
                    clientId: "",
                    redirectUrl: "",
                    description: ""
                }
            }
        },
        computed: {
            ...mapGetters({
                username: 'getUserName'
            })
        },
        methods: {
            onSubmit() {
                if (this.clientInfo.clientId == "") {
                    this.$message.error("请输入Client Id");
                } else if (this.clientInfo.redirectUrl == "") {
                    this.$message.error("请输入重定向返回地址");
                } else {
                    let data = {
                        clientId: this.clientInfo.clientId,
                        redirectUrl: this.clientInfo.redirectUrl,
                        description: this.clientInfo.description,
                        ownerName: this.username
                    };
                    this.$store.dispatch("registerClient", data);
                }

            }
        }
    }
</script>
<template>
    <div class="auth-form-wrapper">

        <div>请查看授权的应用和权限域</div>

        <el-card class="box-card">
            <el-form label-position="left" label-width="110px" :model="form">
                <el-form-item label="待授权应用">
                    <el-input v-model="query.clientId" readonly>Test</el-input>
                </el-form-item>
                <el-form-item label="申请权限域">
                    <el-checkbox-group v-model="query.scopes" :min="10">
                        <el-checkbox v-for="scope in query.scopes"
                                     :label="scope"
                                     :name="type">
                        </el-checkbox>
                    </el-checkbox-group>
                </el-form-item>
                <el-form-item label="应用回调地址">
                    <el-input v-model="query.redirectUrl" readonly></el-input>
                </el-form-item>
            </el-form>
        </el-card>


        <div style="text-align: center">
            <el-button type="success" @click="onAgree()" v-if="this.isClientValid && this.isLogin && !this.isExpired">同意</el-button>
            <el-button type="primary" @click="onLogin()" v-if="this.isClientValid && this.isLogin && !this.isExpired">切换用户</el-button>
            <el-button type="warning" @click="onRefuse()" v-if="this.isClientValid && this.isLogin && !this.isExpired">拒绝</el-button>
            <el-button type="warning" @click="onReturn()" v-if="this.formDisabled">返回</el-button>
            <el-button type="success" @click="onLogin()" v-if="this.isClientValid && (!this.isLogin || this.isExpired)">点击登录</el-button>
        </div>

        <div v-if="this.formDisabled">
            <el-alert :closable="false"
                      type="error"
                      title="应用的申请信息不合法，请选择直接返回。可能原因：该应用名未注册，或者注册的信息（可访问权限域，回调地址等）有误。">
            </el-alert>
        </div>

        <div class="auth-form-rem-me" v-if="this.isClientValid && this.isLogin && !this.isExpired">
            <el-form label-position="top" label-width="150px" :model="form">
                <el-form-item label="记住此次授权选择？">
                    <el-radio-group v-model="query.rememberChoice">
                        <el-radio label="yes">是，记住此次授权选择，直到我注销</el-radio>
                        <el-radio label="no">否，下一次登录授权，请继续提示我</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
        </div>


    </div>
</template>

<script>
    import {mapGetters, mapActions} from 'vuex'

    export default {

        data() {
            return {
                query: {
                    respType: '',
                    clientId: '',
                    scopes: [],
                    redirectUrl: '',
                    rememberChoice: 'no',
                    state: ''
                }
            }
        },
        computed: {
            ...mapGetters({
                isLogin: 'getLoginState',
                isExpired: 'getExpireState',
                username: 'getUserName',
                isClientValid: 'isClientValid',
                isClientApproveDirect: 'isClientApproveDirect',
                isClientApproved: 'isClientApproved',
                authResult: 'getAuthResult',
            }),
            formDisabled() {
                return !(this.isClientValid == null || this.isClientValid);
            }
        },
        mounted: function () {
            let query = this.$route.query;
            this.query.respType = query.response_type;
            this.query.clientId = query.client_id;
            this.query.redirectUrl = query.redirect_uri;
            this.query.state = query.state;
            if (Array.isArray(query.scope)) {
                this.query.scopes = query.scope
            } else if (query.scope != null) {
                this.query.scopes = [query.scope];
            }

            // verify the client's query and fetch its info
            this.$store.dispatch('verifyClient', {username: this.username, clientVO: this.query});
        },
        methods: {
            onAgree() {
                this.$store.dispatch('fetchAuthCode', this.query);
            },
            onRefuse() {
                let q = "?error=access_denied&error_description=User%20denied%20access&state=" + this.query.state;
                this.redirectTo(this.query.redirectUrl, q);
            },
            onReturn(){
                window.history.back(-1);
            },
            redirectTo(hostUrl, query){
                window.location.href = hostUrl + query;
            },
            onLogin(){
                this.$store.dispatch('logout');
                this.$store.dispatch('saveLastVisit', this.$route.fullPath);
                this.$router.push('/login');
            }
        },
        watch: {
            isClientApproveDirect: function () {
                // 当用户上次选择了记住登录授权，则此次直接进行授权
                if (this.isClientValid && this.isClientApproveDirect) {
                    this.onAgree();
                }
            },
            isClientApproved: function () {
                // 当Client通过了授权时，检查是否正常获得授权码
                if (this.isClientApproved != null) {
                    if (this.query.respType == "token") {

                        if (this.isClientApproved) {
                            this.$message.success("已获取到授权令牌，跳转到应用页面中...");
                            let q = "?token=" + this.authResult.authCode + "&state=" + this.query.state;
                            this.redirectTo(this.authResult.redirectUrl, q);
                        } else {
                            this.$message.error("授权令牌获取失败，请检查应用信息，然后重新尝试。");
                        }

                    } else if (this.query.respType == "code") {

                        if (this.isClientApproved) {
                            this.$message.success("已获取到授权码，跳转到应用页面中...");
                            let q = "?code=" + this.authResult.authCode + "&state=" + this.query.state;
                            this.redirectTo(this.authResult.redirectUrl, q);
                        } else {
                            this.$message.error("授权码获取失败，请检查应用信息，然后重新尝试。");
                        }

                    } else {
                        this.$message.error("未知的授权响应类别，response_type必须指定为token或code，请选择两者之一，然后重新尝试。");
                    }


                }
            }
        }

    }

</script>

<style>

    .auth-form-wrapper {
        width: 500px;
        margin: 0 auto;
    }

    .auth-form-wrapper > div {
        margin: 20px 0px;
    }

    .auth-form-rem-me .el-form-item .el-radio {
        margin-top: 5px;
        margin-left: 30px;
    }

</style>
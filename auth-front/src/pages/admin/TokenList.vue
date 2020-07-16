<template>
    <div class="content-panel">
        <div>
            <el-row class="nav-bar">
                <el-col>
                    <el-breadcrumb separator="/">
                        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                        <el-breadcrumb-item>管理员</el-breadcrumb-item>
                        <el-breadcrumb-item>Token列表</el-breadcrumb-item>
                    </el-breadcrumb>
                </el-col>
            </el-row>

            <el-row style="margin-bottom: 7px">
                <el-select v-model="query.userId"
                           placeholder="输入用户名"
                           filterable
                           remote
                           :remote-method="handleUserAutoPrompt"
                           class="query-input">
                    <el-option v-for="item in users"
                               :key="item.id"
                               :label="item.name"
                               :value="item.id">
                    </el-option>
                </el-select>
                <el-select v-model="query.clientId"
                           placeholder="输入Client Id"
                           filterable
                           remote
                           :remote-method="handleClientAutoPrompt"
                           class="query-input">
                    <el-option v-for="item in clients"
                               :key="item.id"
                               :label="item.clientId"
                               :value="item.clientId">
                    </el-option>
                </el-select>
                <el-button type="primary" @click="refreshTokenList">查询</el-button>
                <el-button @click="resetQueryForm">重置</el-button>
                <el-button @click="issueToken" type="primary" style="float: right">颁发Token</el-button>
            </el-row>
        </div>

        <div>
            <el-tabs v-model="activeTab" :value="activeTab" @tab-click="handleTabClick">
                <el-tab-pane label="Access Token" name="AccessToken"></el-tab-pane>
                <el-tab-pane label="Refresh Token" name="RefreshToken"></el-tab-pane>
                <el-tab-pane label="Auth Code" name="AuthCode"></el-tab-pane>
            </el-tabs>
            <el-table :data="tokens" style="width: 100%" border fit>
                <el-table-column label="用户名" prop="userName" align="center"></el-table-column>
                <el-table-column label="Client Id" prop="clientName" align="center"></el-table-column>
                <el-table-column v-if="this.activeTab == 'AuthCode'" label="Code" prop="code"
                                 align="center" width="650"></el-table-column>
                <el-table-column v-else label="Token" prop="value" align="center" width="650"></el-table-column>
                <el-table-column label="生成时间" prop="insertTime" align="center"
                                 :formatter="dateFormatter"></el-table-column>
                <el-table-column label="失效时间" prop="expiration" align="center"
                                 :formatter="dateFormatter"></el-table-column>
                <el-table-column label="操作" align="center" width="200">
                    <template scope="props">
                        <el-button @click="extendToken(props.row)" size="small"
                                   v-if="activeTab == 'AccessToken'">延长</el-button>
                        <el-button @click="handleDelete(props.row)" type="danger" size="small">注销</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>

        <div align='center' style="margin-top: 10px">
            <el-pagination
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page="currentPage"
                    :page-sizes="[10, 20, 30, 50]"
                    :page-size="pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="total">
            </el-pagination>
        </div>

        <el-dialog title="颁发长期Token" :visible.sync="dialogVisible" width="500px" :before-close="onClose">
            <el-form label-width="110px" label-position="left" :model="inEditToken" ref="inEditTokenForm" :rules="inEditTokenRules">
                <el-form-item label="用户名" prop="userName">
                    <el-select v-model="inEditToken.userName" placeholder="输入用户名" filterable allow-create default-first-option style="width: 100%"
                               remote :remote-method="handleUserAutoPrompt">
                        <el-option v-for="item in users" :key="item.id"
                                   :label="item.name" :value="item.name">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="Client" prop="clientId">
                    <el-select v-model="inEditToken.clientId" placeholder="选择Client" filterable style="width: 100%">
                        <el-option v-for="item in allClients" :key="item.id" :value="item.clientId"
                                   :label="item.clientId"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="有效时间(天)" prop="lifeCycle">
                    <el-input v-model.number="inEditToken.lifeCycle"></el-input>
                </el-form-item>
                <el-form-item style="margin-bottom: 0">
                    <el-button @click="onClose" style="float: right">关闭</el-button>
                    <el-button type="primary" @click="onSubmit()" style="float:right;margin:0 10px 0 0;">颁发</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>

        <el-dialog title="延长Token失效时间" :visible.sync="extendDialogVisible" width="500px" :before-close="onCloseExtend">
            <el-form label-width="110px" label-position="left" :model="inExtendToken" ref="inExtendTokenForm" :rules="inExtendTokenRules">
                <el-form-item label="延长时间(天)" prop="overtime">
                    <el-input v-model.number="inExtendToken.overtime"></el-input>
                </el-form-item>
                <el-form-item style="margin-bottom: 0">
                    <el-button @click="onCloseExtend" style="float: right">关闭</el-button>
                    <el-button type="primary" @click="onSubmitExtend()" style="float:right;margin:0 10px 0 0;">提交</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>
    </div>
</template>
<script>

    import {mapGetters, mapActions} from 'vuex';
    import dateUtil from '../../utils/dateUtil';

    export default {
        data: function () {
            return {
                query: {
                    userId: '',
                    clientId: ''
                },
                currentPage: 1,
                pageSize: 10,
                activeTab: "AccessToken",
                dialogVisible: false,
                extendDialogVisible: false,
                inEditToken: {
                    userName: '',
                    clientId: '',
                    lifeCycle: 365
                },
                inEditTokenRules: {
                    userName: [
                        {required: true, message: '请输入用户名', trigger: 'blur'},
                    ],
                    clientId: [
                        {required: true, message: '请选择Client', trigger: 'change'},
                    ],
                    lifeCycle: [
                        {required: true, message: '请输入有效时间(天)', trigger: 'blur'},
                        {type: 'number', min: 1, max: 5000, message: '请输入1-5000之间的数字'}
                    ]
                },
                inExtendToken: {
                    tokenId: null,
                    overtime: 365
                },
                inExtendTokenRules: {
                    overtime: [
                        {required: true, message: '请输入延长时间(天)', trigger: 'blur'},
                        {type: 'number', min: 1, max: 5000, message: '请输入1-5000之间的数字'}
                    ]
                },
            }
        },
        created: function () {
            this.refreshTokenList();
            this.$store.dispatch('fetchUsersByPage', {
                page: 0,
                size: 10,
                name: ''
            });
            this.$store.dispatch('fetchClientsByPage', {
                clientId: '',
                ownerId: '',
                page: 0,
                size: 10
            });
        },
        computed: {
            ...mapGetters({
                accessTokens: 'getAccessTokens',
                refreshTokens: 'getRefreshTokens',
                authCodes: 'getAuthCodes',
                total: 'getTokenCount',
                users: 'getUserList',
                clients: 'getClientList',
                allClients: 'getAllClients'
            }),
            tokens: function () {
                // 根据当前不同tab选择，返回相应的Tokens
                let tokens = [];
                if (this.activeTab == "AccessToken") {
                    tokens = this.accessTokens;
                } else if (this.activeTab == "RefreshToken") {
                    tokens = this.refreshTokens;
                } else if (this.activeTab == "AuthCode") {
                    tokens = this.authCodes;
                }
                return tokens;
            }
        },
        methods: {
            dateFormatter(row, column, cellValue) {
                return dateUtil.formatDate(cellValue);
            },
            handleTabClick(tab, event) {
                this.refreshTokenList();
            },
            handleDelete(data) {
                let queryParam = {
                    userId: this.query.userId,
                    clientId: this.query.clientId,
                    page: this.currentPage - 1,
                    size: this.pageSize
                };
                this.$confirm('确认注销？').then(() => {
                    if (this.activeTab == "AccessToken") {
                        this.$store.dispatch('revokeAccessToken', {tokenId: data.id, queryParam: queryParam});
                    } else if (this.activeTab == "RefreshToken") {
                        this.$store.dispatch('revokeRefreshToken', {tokenId: data.id, queryParam: queryParam});
                    } else if (this.activeTab == "AuthCode") {
                        this.$store.dispatch('revokeAuthCode', {codeId: data.id, queryParam: queryParam});
                    }
                }).catch(() => {
                });
            },
            handleSizeChange(data) {
                this.pageSize = data;
                this.refreshTokenList();
            },
            handleCurrentChange(data) {
                this.currentPage = data;
                this.refreshTokenList();
            },
            refreshTokenList() {
                let data = {
                    userId: this.query.userId,
                    clientId: this.query.clientId,
                    page: this.currentPage - 1,
                    size: this.pageSize
                };
                if (this.activeTab == "AccessToken") {
                    this.$store.dispatch('fetchAccessTokensByPage', data);
                } else if (this.activeTab == "RefreshToken") {
                    this.$store.dispatch('fetchRefreshTokensByPage', data);
                } else if (this.activeTab == "AuthCode") {
                    this.$store.dispatch('fetchAuthCodesByPage', data);
                }
            },
            handleUserAutoPrompt(data) {
                this.$store.dispatch('fetchUsersByPage', {
                    name: data,
                    page: 0,
                    size: 10
                })
            },
            handleClientAutoPrompt(data) {
                this.$store.dispatch('fetchClientsByPage', {
                    clientId: data,
                    ownerId: '',
                    page: 0,
                    size: 10
                })
            },
            resetQueryForm() {
                this.query.userId = '';
                this.query.clientId = '';
                this.currentPage = 1;
                this.refreshTokenList();
            },
            issueToken() {
                this.$store.dispatch('fetchClientList');
                this.dialogVisible = true;
            },
            onClose() {
                this.dialogVisible = false;
                this.$refs["inEditTokenForm"].resetFields();
            },
            onSubmit() {
                this.$refs["inEditTokenForm"].validate((valid) => {
                    if (valid) {
                        let data = {
                            userId: this.query.userId,
                            clientId: this.query.clientId,
                            page: this.currentPage - 1,
                            size: this.pageSize
                        };
                        this.inEditToken.lifeCycle = this.inEditToken.lifeCycle * 24 * 60 * 60 * 1000;
                        this.$store.dispatch('issueLongTimeToken', {queryParam: data, token: this.inEditToken});
                        this.activeTab = "AccessToken";
                        this.onClose();
                    }
                });
            },
            extendToken(token) {
                this.inExtendToken.tokenId = token.id;
                this.extendDialogVisible = true;
            },
            onCloseExtend() {
                this.extendDialogVisible = false;
                this.$refs["inExtendTokenForm"].resetFields();
            },
            onSubmitExtend() {
                this.$refs["inExtendTokenForm"].validate((valid) => {
                    if (valid) {
                        let data = {
                            userId: this.query.userId,
                            clientId: this.query.clientId,
                            page: this.currentPage - 1,
                            size: this.pageSize
                        };
                        this.inExtendToken.overtime = this.inExtendToken.overtime * 24 * 60 * 60 * 1000;
                        this.$store.dispatch('extendTokenExpiration', {queryParam: data, token: this.inExtendToken});
                        this.onCloseExtend();
                    }
                });
            }
        }
    }

</script>

<style>

</style>
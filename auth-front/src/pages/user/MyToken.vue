<template>
    <div class="content-panel">
        <el-row class="nav-bar">
            <el-col>
                <el-breadcrumb separator="/">
                    <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                    <el-breadcrumb-item>用户</el-breadcrumb-item>
                    <el-breadcrumb-item>我的Token</el-breadcrumb-item>
                </el-breadcrumb>
            </el-col>
        </el-row>

        <div>
            <div>
                <el-tabs v-model="activeTab" @tab-click="handleTabClick">
                    <el-tab-pane label="Access Token" name="AccessToken"></el-tab-pane>
                    <el-tab-pane label="Refresh Token" name="RefreshToken"></el-tab-pane>
                    <el-tab-pane label="Auth Code" name="AuthCode"></el-tab-pane>
                </el-tabs>
                <el-table :data="tokens" style="width: 100%" border fit>
                    <el-table-column label="用户名" prop="userName" align="center"></el-table-column>
                    <el-table-column label="Client Id" prop="clientName" align="center"></el-table-column>
                    <el-table-column v-if="this.activeTab == 'AuthCode'" label="Code" prop="code"
                                     align="center" width="700"></el-table-column>
                    <el-table-column v-else label="Token" prop="value" align="center" width="700"></el-table-column>
                    <el-table-column label="生成时间" prop="insertTime" align="center"
                                     :formatter="dateFormatter"></el-table-column>
                    <el-table-column label="失效时间" prop="expiration" align="center"
                                     :formatter="dateFormatter"></el-table-column>
                    <el-table-column label="操作" align="center" width="140">
                        <template scope="props">
                            <el-button @click="handleDelete(props.row)" type="danger" size="small">注销
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
        </div>
    </div>


</template>
<script>

    import {mapGetters, mapActions} from 'vuex';
    import dateUtil from '../../utils/dateUtil';

    export default {
        data: function () {
            return {
                query: {
                    fieldList: [{
                        label: 'Client ID',
                        value: 'clientId'
                    }],
                    currentField: 'clientId',
                    currentValue: null
                },
                activeTab: "AccessToken"
            }
        },
        created: function () {
            this.$store.dispatch('fetchMyAccessTokens', {username: this.username});
        },
        computed: {
            ...mapGetters({
                username: 'getUserName',
                myAuthCodes: 'getMyAuthCodes',
                myAccessTokens: 'getMyAccessTokens',
                myRefreshTokens: 'getMyRefreshTokens',
            }),
            tokens: function () {
                // 根据当前不同tab选择，返回相应的Tokens
                let tokens = [];
                if (this.activeTab == "AccessToken") {
                    tokens = this.myAccessTokens;
                } else if (this.activeTab == "RefreshToken") {
                    tokens = this.myRefreshTokens;
                } else if (this.activeTab == "AuthCode") {
                    tokens = this.myAuthCodes;
                }
                return tokens;
            }
        },
        mounted: function () {

        },
        methods: {
            dateFormatter(row, column, cellValue) {
                return dateUtil.formatDate(cellValue);
            },
            onSearch(){

            },
            handleEdit(){

            },
            handleDelete(data){
                this.$confirm('确认注销？').then(() => {
                    if (this.activeTab == "AuthCode") {
                        this.$store.dispatch('revokeMyAuthCode', {codeId: data.id, username: this.username});
                    } else if (this.activeTab == "AccessToken") {
                        this.$store.dispatch('revokeMyAccessToken', {tokenId: data.id, username: this.username});
                    } else if (this.activeTab == "RefreshToken") {
                        this.$store.dispatch('revokeMyRefreshToken', {tokenId: data.id, username: this.username});
                    }
                }).catch(() => {
                });
            },
            handleTabClick(tab, event) {
                if (tab.name == "AccessToken") {
                    this.$store.dispatch('fetchMyAccessTokens', {username: this.username});
                } else if (tab.name == "RefreshToken") {
                    this.$store.dispatch('fetchMyRefreshTokens', {username: this.username});
                } else if (tab.name == "AuthCode") {
                    this.$store.dispatch('fetchMyAuthCodes', {username: this.username});
                }
            },
        }
    }

</script>

<style>

</style>
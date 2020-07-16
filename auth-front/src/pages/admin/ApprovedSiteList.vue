<template>
    <div class="content-panel">
        <div>
            <el-row class="nav-bar">
                <el-col>
                    <el-breadcrumb separator="/">
                        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                        <el-breadcrumb-item>管理员</el-breadcrumb-item>
                        <el-breadcrumb-item>登录授权列表</el-breadcrumb-item>
                    </el-breadcrumb>
                </el-col>
            </el-row>

            <el-row>
                <el-select v-model="query.username"
                           placeholder="输入用户名"
                           filterable
                           remote
                           :remote-method="handleUserAutoPrompt"
                           class="query-input">
                    <el-option v-for="item in users"
                               :key="item.id"
                               :label="item.name"
                               :value="item.name">
                    </el-option>
                </el-select>
                <el-select v-model="query.clientId"
                           placeholder="输入Client"
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
                <el-button type="primary" @click="refreshSessionList">查询</el-button>
                <el-button @click="resetQueryForm">重置</el-button>
            </el-row>
        </div>

        <br/>

        <div>
            <el-table :data="sessions" style="width: 100%" border fit>
                <el-table-column label="用户名" prop="userName" align="center"></el-table-column>
                <el-table-column label="授权的Client" prop="clientId" align="center"></el-table-column>
                <el-table-column label="授权的权限域" prop="scopes" :formatter="scopeFormatter"
                                 align="center"></el-table-column>
                <el-table-column label="授权时间" prop="insertTime" align="center"
                                 :formatter="dateFormatter"></el-table-column>
                <el-table-column label="操作" align="center" width="140">
                    <template scope="props">
                        <el-button @click="handleDelete(props.row)" type="danger" size="small">注销
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>

        <div align='center' style="margin-top: 10px">
            <el-pagination
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                    :current-page="currentPage"
                    :page-sizes="[10, 30, 50, 100]"
                    :page-size="pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="total">
            </el-pagination>
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
                    username: '',
                    clientId: ''
                },
                currentPage: 1,
                pageSize: 10
            }
        },
        created: function () {
            this.refreshSessionList();
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
                sessions: 'getSessionList',
                total: 'getSessionCount',
                users: 'getUserList',
                clients: 'getClientList'
            })
        },
        mounted: function () {

        },
        methods: {
            dateFormatter(row, column, cellValue) {
                return dateUtil.formatDate(cellValue);
            },
            scopeFormatter(row, column, scopes){
                let scopeNames = "";
                scopes.forEach(function (scope) {
                    scopeNames = scope.scopeName + " " + scopeNames;
                });
                return scopeNames;
            },
            handleDelete(session) {
                let queryParam = {
                    username: this.query.username,
                    clientId: this.query.clientId,
                    page: this.currentPage - 1,
                    size: this.pageSize
                };
                this.$confirm('确认注销？').then(() => {
                    this.$store.dispatch('revokeSession', {sessionId: session.id, queryParam: queryParam});
                }).catch(() => {
                });
            },
            handleSizeChange(data) {
                this.pageSize = data;
                this.refreshSessionList();
            },
            handleCurrentChange(data) {
                this.currentPage = data;
                this.refreshSessionList();
            },
            refreshSessionList() {
                this.$store.dispatch('fetchSessionsByPage', {
                    username: this.query.username,
                    clientId: this.query.clientId,
                    page: this.currentPage - 1,
                    size: this.pageSize
                })
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
                this.query.username = '';
                this.query.clientId = '';
                this.currentPage = 1;
                this.refreshSessionList();
            }
        }
    }

</script>


<style>

</style>
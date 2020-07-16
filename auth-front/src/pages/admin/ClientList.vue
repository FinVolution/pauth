<template>
    <div class="content-panel">
        <div>
            <el-row class="nav-bar">
                <el-col>
                    <el-breadcrumb separator="/">
                        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                        <el-breadcrumb-item>管理员</el-breadcrumb-item>
                        <el-breadcrumb-item>Client列表</el-breadcrumb-item>
                    </el-breadcrumb>
                </el-col>
            </el-row>

            <el-row>
                <el-input v-model="query.clientId" placeholder="输入Client Id" class="query-input"></el-input>
                <el-select v-model="query.ownerId"
                           placeholder="输入注册人"
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
                <el-button type="primary" @click="refreshClients">查询</el-button>
                <el-button @click="resetQueryForm">重置</el-button>
            </el-row>
        </div>

        <br/>

        <div>
            <el-table :data="clients" style="width: 100%" border fit>
                <el-table-column label="Client Id" prop="clientId" align="center"></el-table-column>
                <el-table-column label="Client Secret" prop="clientSecret" align="center"></el-table-column>
                <el-table-column label="Authorization" prop="basicAuth" align="center"></el-table-column>
                <el-table-column label="重定向返回地址" prop="redirectUrl" align="center"></el-table-column>
                <el-table-column label="描述" prop="description" align="center"></el-table-column>
                <el-table-column label="注册人" prop="owner.name" align="center"></el-table-column>
                <el-table-column label="注册时间" prop="insertTime" align="center"
                                 :formatter="dateFormatter"></el-table-column>
                <el-table-column label="操作" align="center" width="200">
                    <template scope="props">
                        <el-button @click="handleEdit(props.row)" size="small">编辑</el-button>
                        <el-button @click="handleDelete(props.row)" type="danger" size="small">删除
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

        <el-dialog title="编辑Client" :visible.sync="dialogVisible" class="client-dialog" width="500px">
            <el-form label-position="top" :model="inEditClient" ref="inEditClientForm">
                <el-form-item label="重定向返回地址（校验用，支持正则表达式）" prop="redirectUrl">
                    <el-input v-model="inEditClient.redirectUrl"></el-input>
                </el-form-item>
                <el-form-item label="描述" prop="description" style="margin-bottom: 22px">
                    <el-input v-model="inEditClient.description"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button style="float: right" @click="onCloseDialog">关闭</el-button>
                    <el-button type="primary" @click="onSubmit('inEditClientForm')" style="float:right;margin:0 10px 0 0;">保存</el-button>
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
                dialogVisible: false,
                query: {
                    clientId: '',
                    ownerId: ''
                },
                currentPage: 1,
                pageSize: 10,
                inEditClient: {
                    id: null,
                    redirectUrl: '',
                    description: ''
                }
            }
        },
        created: function () {
            this.refreshClients();
            this.$store.dispatch('fetchUsersByPage', {
                page: 0,
                size: 10,
                name: ''
            });
        },
        computed: {
            ...mapGetters({
                clients: 'getClientList',
                total: 'getClientCount',
                users: 'getUserList'
            })
        },
        mounted: function () {

        },
        methods: {
            dateFormatter(row, column, cellValue) {
                return dateUtil.formatDate(cellValue);
            },
            onCloseDialog() {
                this.dialogVisible = false;
            },
            handleEdit(client) {
                this.inEditClient = {
                    id: client.id,
                    redirectUrl: client.redirectUrl,
                    description: client.description
                };
                this.dialogVisible = true;
            },
            onSubmit(formName) {
                let queryParam = {
                    clientId: this.query.clientId,
                    ownerId: this.query.ownerId,
                    page: this.currentPage - 1,
                    size: this.pageSize
                };
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        this.dialogVisible = false;
                        this.$store.dispatch('updateClient', {clientInfo: this.inEditClient, queryParam: queryParam});
                    } else {
                        return false;
                    }
                });
            },
            handleDelete(client) {
                let queryParam = {
                    clientId: this.query.clientId,
                    ownerId: this.query.ownerId,
                    page: this.currentPage - 1,
                    size: this.pageSize
                };
                this.$confirm('确认删除？').then(() => {
                    this.$store.dispatch('deleteClient', {clientId: client.id, queryParam: queryParam});
                }).catch(() => {
                });
            },
            handleSizeChange(data) {
                this.pageSize = data;
                this.refreshClients();
            },
            handleCurrentChange(data) {
                this.currentPage = data;
                this.refreshClients();
            },
            refreshClients() {
                this.$store.dispatch('fetchClientsByPage', {
                    clientId: this.query.clientId,
                    ownerId: this.query.ownerId,
                    page: this.currentPage - 1,
                    size: this.pageSize
                })
            },
            resetQueryForm() {
                this.query.clientId = '';
                this.query.ownerId = '';
                this.currentPage = 1;
                this.refreshClients();
            },
            handleUserAutoPrompt(data) {
                this.$store.dispatch('fetchUsersByPage', {
                    name: data,
                    page: 0,
                    size: 10
                })
            }
        }
    }

</script>

<style>
    .el-form--label-top .el-form-item__label {
        padding: 0;
    }
</style>
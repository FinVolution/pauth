<template>
    <div class="content-panel">

        <div>
            <el-row class="nav-bar">
                <el-col>
                    <el-breadcrumb separator="/">
                        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                        <el-breadcrumb-item>应用开发</el-breadcrumb-item>
                        <el-breadcrumb-item>我的Client</el-breadcrumb-item>
                    </el-breadcrumb>
                </el-col>
            </el-row>
        </div>

        <br/>

        <div>
            <el-table :data="myClients" style="width: 100%" border fit>
                <el-table-column label="Client Id" prop="clientId" align="center"></el-table-column>
                <el-table-column label="Client Secret" prop="clientSecret" align="center"></el-table-column>
                <el-table-column label="Authorization" prop="basicAuth" align="center"></el-table-column>
                <el-table-column label="重定向返回地址" prop="redirectUrl" align="center"></el-table-column>
                <el-table-column label="描述" prop="description" align="center"></el-table-column>
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
                    fieldList: [{
                        label: 'Client ID',
                        value: 'clientId'
                    }],
                    currentField: 'clientId',
                    currentValue: null
                },
                inEditClient: {
                    id: null,
                    redirectUrl: '',
                    description: ''
                }
            }
        },
        created: function () {
            this.$store.dispatch('fetchMyClients', {username: this.username});
        },
        computed: {
            ...mapGetters({
                username: 'getUserName',
                myClients: 'getMyClients'
            })
        },
        mounted: function () {

        },
        methods: {
            dateFormatter(row, column, cellValue) {
                return dateUtil.formatDate(cellValue);
            },
            onSearch(){

            },
            onCloseDialog() {
                this.dialogVisible = false;
            },
            handleEdit(client){
                this.inEditClient = {
                    id: client.id,
                    redirectUrl: client.redirectUrl,
                    description: client.description
                };
                this.dialogVisible = true;
            },
            onSubmit(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        this.dialogVisible = false;
                        let data = {
                            client: this.inEditClient,
                            username: this.username
                        }
                        this.$store.dispatch('updateMyClient', data);
                    } else {
                        return false;
                    }
                });
            },
            handleDelete(client){
                this.$confirm('确认删除？').then(() => {
                    this.$store.dispatch('deleteMyClient', {clientId: client.id, username: this.username});
                }).catch(() => {
                });
            }
        }
    }

</script>

<style>

</style>
<template>
    <div class="content-panel">

        <div>
            <el-row class="nav-bar">
                <el-col>
                    <el-breadcrumb separator="/">
                        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                        <el-breadcrumb-item>管理员</el-breadcrumb-item>
                        <el-breadcrumb-item>用户列表</el-breadcrumb-item>
                    </el-breadcrumb>
                </el-col>
            </el-row>
            <el-row>
                <el-input v-model="query.name" placeholder="输入用户名" class="query-input"></el-input>
                <el-button type="primary" @click="refreshUsers">查询</el-button>
                <el-button @click="resetQueryForm">重置</el-button>
            </el-row>
        </div>

        <br/>

        <div>
            <el-table :data="users" style="width: 100%" border fit>
                <el-table-column label="用户名" prop="name" align="center"></el-table-column>
                <el-table-column label="邮箱" prop="email" align="center"></el-table-column>
                <el-table-column label="上次登录时间" prop="lastVisitAt" align="center"
                                 :formatter="dateFormatter"></el-table-column>
                <el-table-column label="首次登录时间" prop="insertTime" align="center"
                                 :formatter="dateFormatter"></el-table-column>
            </el-table>
        </div>
        <div align="center" style="margin-top: 10px">
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
                    name: ''
                },
                currentPage: 1,
                pageSize: 10
            }
        },
        created: function () {
            this.refreshUsers();
        },
        computed: {
            ...mapGetters({
                users: 'getUserList',
                total: 'getUserCount'
            })
        },
        mounted: function () {

        },
        methods: {
            dateFormatter(row, column, cellValue) {
                return dateUtil.formatDate(cellValue);
            },
            handleSizeChange(data) {
                this.pageSize = data;
                this.refreshUsers();
            },
            handleCurrentChange(data) {
                this.currentPage = data;
                this.refreshUsers();
            },
            refreshUsers() {
                this.$store.dispatch('fetchUsersByPage', {
                    name: this.query.name,
                    page: this.currentPage - 1,
                    size: this.pageSize
                })
            },
            resetQueryForm() {
                this.query.name = '';
                this.currentPage = 1;
                this.refreshUsers();
            }
        }
    }

</script>

<style>

</style>
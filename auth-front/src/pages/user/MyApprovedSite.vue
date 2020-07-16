<template>
    <div class="content-panel">

        <div>
            <el-row class="nav-bar">
                <el-col>
                    <el-breadcrumb separator="/">
                        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                        <el-breadcrumb-item>用户</el-breadcrumb-item>
                        <el-breadcrumb-item>我的登录授权</el-breadcrumb-item>
                    </el-breadcrumb>
                </el-col>
            </el-row>
        </div>

        <br/>
        <div>
            <el-table :data="mySessions" style="width: 100%" border fit>
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
    </div>


</template>
<script>

    import {mapGetters, mapActions} from 'vuex';
    import dateUtil from '../../utils/dateUtil';

    export default {
        data: function () {
            return {}
        },
        created: function () {
            this.$store.dispatch('fetchMySessions', {username: this.username});
        },
        computed: {
            ...mapGetters({
                username: 'getUserName',
                mySessions: 'getMySessions'
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
                this.$confirm('确认注销？').then(() => {
                    this.$store.dispatch('revokeMySession', {sessionId: session.id, username: this.username});
                }).catch(() => {
                });
            }
        }
    }

</script>


<style>

</style>
<template>
    <div class="content-panel">
        <div>
            <el-row class="nav-bar">
                <el-col>
                    <el-breadcrumb separator="/">
                        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
                        <el-breadcrumb-item>管理员</el-breadcrumb-item>
                        <el-breadcrumb-item>操作日志列表</el-breadcrumb-item>
                    </el-breadcrumb>
                </el-col>
            </el-row>
        </div>

        <br/>

        <div>
            <el-table :data="auditLogs" style="width: 100%" border fit>
                <el-table-column type="expand">
                    <template slot-scope="props">
                        <el-form label-position="top" inline class="table-expand">
                            <el-form-item label="请求参数">
                                <span>{{ props.row.classMethodArgs }}</span>
                            </el-form-item>
                            <el-form-item label="返回值">
                                <span>{{ props.row.classMethodReturn }}</span>
                            </el-form-item>
                        </el-form>
                    </template>
                </el-table-column>
                <el-table-column label="用户名" prop="userName" align="center" width="120"></el-table-column>
                <el-table-column label="请求类型" prop="httpMethod" align="center" width="120"></el-table-column>
                <el-table-column label="请求路径" prop="httpUri" align="center"></el-table-column>
                <el-table-column label="请求方法" prop="classMethod" align="center" min-width="150"></el-table-column>
                <el-table-column label="请求时间" prop="insertTime" align="center" width="200"
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
            this.refreshAuditLogs();
        },
        computed: {
            ...mapGetters({
                auditLogs: 'getAuditLogList',
                total: 'getAuditLogCount'
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
                this.refreshAuditLogs();
            },
            handleCurrentChange(data) {
                this.currentPage = data;
                this.refreshAuditLogs();
            },
            refreshAuditLogs() {
                this.$store.dispatch('fetchAuditLogsByPage', {
                    page: this.currentPage - 1,
                    size: this.pageSize
                })
            }
        }
    }

</script>

<style>
    .table-expand label {
        color: #99a9bf;
    }
    .table-expand .el-form-item {
        margin-right: 0;
        margin-bottom: 0;
        width: 100%;
        word-break: break-all;
    }
    .table-expand .el-form-item .el-form-item__content {
        line-height: normal;
    }
</style>
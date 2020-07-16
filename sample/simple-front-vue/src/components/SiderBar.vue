<template>
    <div class="sidebar">
        <el-menu :default-openeds="['admin','menu']" class="el-menu-vertical-demo" theme="light" router>
            <el-submenu index="menu">
                <template slot="title"><i class="el-icon-menu"></i>一级菜单</template>
                <el-menu-item index="/">二级菜单</el-menu-item>
                <el-menu-item index="/">二级菜单</el-menu-item>
            </el-submenu>
        </el-menu>
    </div>
</template>
<script>
    export default {}
</script>
<style scoped>

    .sidebar {
        display: block;
        position: absolute;
        width: 250px;
        left: 0;
        top: 70px;
        bottom: 0;
        background: #2E363F;
    }

    .sidebar > ul {
        height: 100%;
    }

    .sidebar li i {
        font-size: 16px;
    }

</style>

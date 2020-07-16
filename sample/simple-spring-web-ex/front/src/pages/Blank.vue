<template>
    <div>
        <el-button @click="getTestData">GET</el-button>
        <el-button @click="updateTestData">POST</el-button>
        <p>{{testData}}</p>
    </div>
</template>

<script>
    import {mapGetters} from "vuex";
    export default {
        computed: {
            ...mapGetters({
                testData: 'getTestData'
            })
        },
        methods: {
            getTestData() {
                this.$store.dispatch('fetchTestData');
            },
            updateTestData() {
                this.$store.dispatch('updateTestData', {newData: "TEST"});
            }
        }
    }
</script>

<style>

</style>
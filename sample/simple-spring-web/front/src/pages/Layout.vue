<template>
    <div class="layout-wrapper">
        <vheader title="演示"></vheader>
        <vsiderbar></vsiderbar>
        <div class="main-content">
            <div class="content-wrapper">
                <transition name="move" mode="out-in">
                    <router-view></router-view>
                </transition>
            </div>
            <vfooter></vfooter>
        </div>
    </div>
</template>

<script>
    import vheader from '../components/Header.vue';
    import vsiderbar from '../components/SiderBar.vue';
    import vfooter from '../components/Footer.vue';
    import {mapGetters} from 'vuex'

    export default {
        components: {
            vheader, vsiderbar, vfooter
        },
        computed: {
            ...mapGetters({
                promptMessage: 'getPromptMessage'
            })
        },
        watch: {
            promptMessage: function (newMessage) {
                if (newMessage.code != null) {
                    if (newMessage.code >= 0) {
                        this.$message.success(newMessage.details);
                    } else {
                        this.$message.error(newMessage.details);
                    }
                }
            }
        }
    }

</script>

<style>

    @import '../assets/common.css';

    .layout-wrapper {

    }

    .main-content {
        background: none repeat scroll 0 0 #fff;
        position: absolute;
        left: 250px;
        right: 0;
        top: 70px;
        bottom: 0;
        width: auto;
        padding: 40px;
        box-sizing: border-box;
        overflow-y: scroll;
    }

    .content-wrapper {
        min-height: 500px;
    }

</style>
<template>
    <div id="authorize" class="layout-wrapper">
        <vheader title="Pauth登录授权"></vheader>
        <div class="main-content-auth">
            <transition name="move" mode="out-in">
                <router-view></router-view>
            </transition>
        </div>
    </div>
</template>

<script>
    import vheader from '../components/Header.vue';
    import {mapGetters} from 'vuex'

    export default {
        components: {
            vheader
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
        },
        methods: {
            returnHome() {
                this.$router.push('/');
            }
        }
    }

</script>

<style>

    @import '../assets/common.css';

    .main-content-auth {
        background: none repeat scroll 0 0 #fff;
        position: absolute;
        left: 0px;
        right: 0;
        top: 70px;
        bottom: 0;
        width: auto;
        padding: 20px;
        box-sizing: border-box;
        overflow-y: scroll;
    }

</style>
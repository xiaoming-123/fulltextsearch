<template>
    <a-layout class="layout">
        <a-layout-header>
            <div class="logo" />
            <a-menu v-model="selectedKeys" @click="handleSelect" theme="dark" mode="horizontal" :style="{ lineHeight: '64px' }">
                <a-menu-item v-for="item in $GLOBAL_CONFIG.menu" :key="item.key">{{ item.name }}</a-menu-item>
            </a-menu>
        </a-layout-header>
        <a-layout-content style="padding: 0 50px">
            <a-breadcrumb style="margin: 16px 0">
                <a-breadcrumb-item>{{ current }}</a-breadcrumb-item>
            </a-breadcrumb>
            <div :style="{ background: '#fff', padding: '24px', height: 'calc(100vh - 64px - 100px - 5px)' }">
                <router-view></router-view>
            </div>
        </a-layout-content>
        <a-layout-footer style="text-align: center; height: 1px"> @2021 </a-layout-footer>
    </a-layout>
</template>
<script>
export default {
    name: "MainPage",
    data() {
        return {
            selectedKeys: ["home"],
            current: {}
        };
    },
    methods: {
        handleSelect({ key }) {
            if (key) {
                this.$router.push({ path: key });
            }
        },
        getCurrent(key) {
            this.$GLOBAL_CONFIG.menu.forEach((item) => {
                if (item.key === key) {
                    this.current = item.name;
                }
            });
        }
    },
    watch: {
        $route: {
            handler(to) {
                // 设置当前菜单
                this.selectedKeys = [to.fullPath];
                this.getCurrent(to.fullPath);
            },
            immediate: true,
            deep: true
        }
    }
};
</script>
<style>
.site-layout-content {
    min-height: 280px;
    padding: 24px;
    background: #fff;
}
#components-layout-demo-top .logo {
    float: left;
    width: 120px;
    height: 31px;
    margin: 16px 24px 16px 0;
    background: rgba(255, 255, 255, 0.3);
}
.ant-row-rtl #components-layout-demo-top .logo {
    float: right;
    margin: 16px 0 16px 24px;
}

[data-theme="dark"] .site-layout-content {
    background: #141414;
}
</style>

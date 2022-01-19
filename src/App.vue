<template>
    <a-config-provider :locale="locale">
        <div id="app">
            <router-view />
        </div>
    </a-config-provider>
</template>
<script>
// ant-design国际化
import zh_CN from "ant-design-vue/es/locale/zh_CN";
// ant-design本身已经引用了moment不需要在项目中再引用，它也需要国际化
import moment from "moment";
import "moment/locale/zh-cn";
moment.locale("zh-cn");

export default {
    data() {
        return {
            locale: zh_CN
        };
    }
};
</script>

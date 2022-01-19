import { createApp } from "vue";

import antd from "ant-design-vue";
import "ant-design-vue/dist/antd.css";

import router from "./router";
import GLOBAL_CONFIG from "./mixin";
import utils from "./utils";

import "xe-utils";
import VXETable from "vxe-table";
import "vxe-table/lib/style.css";

import App from "./App.vue";

const app = createApp(App);
app.config.productionTip = false;
app.use(router);
app.use(antd);
app.use(VXETable);
// 为自定义的选项 'myOption' 注入一个处理器。
app.config.globalProperties.$utils = utils;
app.config.globalProperties.$GLOBAL_CONFIG = GLOBAL_CONFIG;

app.mount("#app");

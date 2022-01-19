import { createRouter, createWebHashHistory } from "vue-router";
import Main from "../view/Main.vue";
import Home from "../view/home/Home.vue";
import IndexManage from "../view/manager/IndexManage.vue";
import DataSourceManage from "../view/manager/DataSourceManage.vue";

const routes = [
    {
        path: "/",
        redirect: "/main"
    },
    {
        path: "/main",
        name: "Main",
        redirect: "/main/home",
        component: Main,
        children: [
            {
                path: "home",
                component: Home
            },
            {
                path: "indexManage",
                component: IndexManage
            },
            {
                path: "dataSourceManage",
                component: DataSourceManage
            }
        ]
    }
];

const router = createRouter({
    history: createWebHashHistory(),
    routes
});

export default router;

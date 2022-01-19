<template>
    <div>
        <div class="search">
            <vxe-input style="width: 400px" v-model="homeData.queryString" placeholder="关键词"></vxe-input>
            <vxe-button @click="handleSearch">查询</vxe-button>
        </div>

        <div class="result-content">
            <a-list item-layout="vertical" size="mini" :pagination="pagination" :data-source="homeData.result">
                <template #renderItem="{ item }">
                    <a-list-item key="item.id">
                        <a-list-item-meta :description="item.time">
                            <template #title>
                                <a :href="item.url" v-html="item.title"></a>
                                <span style=""> - {{ item.type }}</span>
                            </template>
                        </a-list-item-meta>
                        <div v-html="item.content"></div>
                    </a-list-item>
                </template>
            </a-list>
        </div>
    </div>
</template>

<script>
import { reactive, getCurrentInstance } from "vue";
export default {
    name: "HomePage",
    setup() {
        const homeData = reactive({
            dataSource: [],
            queryString: "",
            result: "",
            types: {
                "1_": "mysql",
                "2_": "文件"
            }
        });

        const pagination = {
            onChange: (page) => {
                console.log(page);
            },
            pageSize: 5
        };

        const { proxy } = getCurrentInstance();

        const handleChange = (value) => {};

        const handleSearch = () => {
            if (!homeData.queryString) {
                return;
            }
            proxy.$utils.http.post("./api/search/fulltextsearch/search", { queryString: homeData.queryString }).then(({ data }) => {
                homeData.result = data.data.map((item) => {
                    item.time = proxy.$utils.util.formatDate(item.time);
                    item.type = homeData.types[item.type + "_"];
                    return item;
                });
            });
        };

        return { homeData, pagination, handleChange, handleSearch };
    }
};
</script>
<style scoped>
.search {
    display: flex;
    width: 500px;
    justify-content: space-between;
}
.result-content {
    height: calc(100vh - 250px);
    width: 100%;
    overflow: auto;
}
</style>

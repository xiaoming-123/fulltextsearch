<template>
    <div>
        <vxe-table :loading="table.loading" :align="table.align" :data="table.tableData" :size="table.size">
            <vxe-column type="seq" width="60"></vxe-column>
            <vxe-column field="uuid" title="UUID"></vxe-column>
            <vxe-column field="index" title="索引名"></vxe-column>
            <vxe-column field="docs.count" title="文档数"></vxe-column>
            <vxe-column field="docs.deleted" title="已删除文档数"></vxe-column>
            <vxe-column field="pri" title="主分片"></vxe-column>
            <vxe-column field="pri.store.size" title="主分片总容量"></vxe-column>
            <vxe-column field="rep" title="rep"></vxe-column>
            <vxe-column field="status" title="状态"></vxe-column>
            <vxe-column field="store.size" title="索引存储总容量"></vxe-column>
        </vxe-table>
    </div>
</template>
<script>
import { defineComponent, onMounted, reactive, getCurrentInstance } from "vue";
export default defineComponent({
    name: "SourceManager",
    setup() {
        const table = reactive({
            loading: false,
            size: "mini",
            align: "center",
            tableData: []
        });

        const { proxy } = getCurrentInstance();

        const loadData = () => {
            table.loading = true;
            proxy.$utils.http.post("./api/search/index/manage/getIndex").then(({ data }) => {
                table.loading = false;
                table.tableData = data.data;
            });
        };

        onMounted(() => {
            loadData();
        });

        return {
            table
        };
    }
});
</script>
<style scoped>
.source-btn {
    display: flex;
    width: 200px;
    margin-bottom: 10px;
    justify-content: space-between;
}
</style>

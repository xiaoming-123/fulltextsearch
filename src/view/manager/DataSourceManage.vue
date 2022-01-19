<template>
    <div>
        <div class="source-btn"></div>
        <vxe-toolbar ref="xToolbar" custom>
            <template #buttons>
                <vxe-button icon="vxe-icon--plus" content="新增" @click="handleAdd(false, null)"></vxe-button>
                <a-upload
                    :file-list="fileData.fileList"
                    action="./api/search/source/manage/uploadFile"
                    multiple
                    name="file"
                    accept=".txt,.doc,.docx,.pdf"
                    @change="uploadChange"
                >
                    <vxe-button icon="vxe-icon--plus" content="上传文件"></vxe-button>
                </a-upload>
            </template>
            <template #tools>
                <vxe-button type="text" icon="vxe-icon--refresh" class="tool-btn" @click="loadData"></vxe-button>
            </template>
        </vxe-toolbar>
        <vxe-table ref="xTable" :loading="table.loading" :align="table.align" :data="table.tableData" :size="table.size">
            <vxe-column type="seq" width="60"></vxe-column>
            <vxe-column field="name" title="名称"></vxe-column>
            <vxe-column field="type" title="类型">
                <template #default="{row}">
                    {{ table.types[row.type] }}
                </template>
            </vxe-column>
            <vxe-column field="schema" title="数据库"></vxe-column>
            <vxe-column field="driverClassName" title="驱动"></vxe-column>
            <vxe-column field="url" title="地址"></vxe-column>
            <vxe-column field="username" title="用户名"></vxe-column>
            <vxe-column field="password" title="密码"></vxe-column>
            <vxe-column title="操作" width="250" show-overflow>
                <template #default="{ row }">
                    <vxe-button type="text" icon="vxe-icon--edit-outline" content="测试连接" @click="testConnect(row)"></vxe-button>
                    <vxe-button type="text" icon="vxe-icon--edit-outline" content="设置" @click="showConfig(row)"></vxe-button>
                    <vxe-button type="text" icon="vxe-icon--edit-outline" content="编辑" @click="handleAdd(true, row)"></vxe-button>
                </template>
            </vxe-column>
        </vxe-table>
        <vxe-modal title="新增" v-model="edit.show" width="600" show-footer @confirm="saveIndex">
            <template #default>
                <div class="form-content">
                    <div class="form-item">
                        <vxe-input v-model="edit.editData.name" placeholder="名称" size="small"></vxe-input>
                    </div>
                    <div class="form-item">
                        <vxe-select v-model="edit.editData.type" placeholder="类型" size="small">
                            <vxe-option :value="1" label="mysql"></vxe-option>
                        </vxe-select>
                    </div>
                    <div class="form-item">
                        <vxe-input v-model="edit.editData.schema" placeholder="数据库" size="small"></vxe-input>
                    </div>
                    <div class="form-item">
                        <vxe-input v-model="edit.editData.driverClassName" placeholder="驱动" size="small"></vxe-input>
                    </div>
                    <div class="form-item">
                        <vxe-input v-model="edit.editData.url" placeholder="地址" size="small"></vxe-input>
                    </div>
                    <div class="form-item">
                        <vxe-input v-model="edit.editData.username" placeholder="用户名" size="small"></vxe-input>
                    </div>
                    <div class="form-item">
                        <vxe-input v-model="edit.editData.password" placeholder="密码" size="small"></vxe-input>
                    </div>
                </div>
            </template>
        </vxe-modal>
        <vxe-modal title="配置" v-model="config.show" width="1300" height="500" show-footer @confirm="saveConfig">
            <template #default>
                <div class="form-content">
                    <div class="config-tree-content">
                        <vxe-table
                            size="mini"
                            resizable
                            show-overflow
                            border="inner"
                            ref="xTree"
                            height="380"
                            highlight-current-row
                            :loading="config.tableTreeDataLoading"
                            :tree-config="{ transform: true, rowField: 'id', parentField: 'parentId' }"
                            :data="config.tableTreeData"
                            @cell-click="handCellClick"
                        >
                            <vxe-column field="name" title="名称" tree-node></vxe-column>
                        </vxe-table>
                    </div>
                    <div class="config-table-content">
                        <vxe-table
                            ref="configTable"
                            border
                            resizable
                            show-overflow
                            :data="config.tableData"
                            height="380"
                            :edit-config="{ trigger: 'click', mode: 'cell' }"
                        >
                            <vxe-column type="seq" width="60"></vxe-column>
                            <vxe-column field="schema" title="数据库"></vxe-column>
                            <vxe-column field="tableName" title="表名"></vxe-column>
                            <vxe-column field="fieldName" title="字段名"></vxe-column>
                            <vxe-column field="time" title="时间"></vxe-column>
                            <vxe-column field="title" title="标题" :edit-render="{}">
                                <template #edit="{ row }">
                                    <vxe-input v-model="row.title" placeholder="请输入标题"></vxe-input>
                                </template>
                            </vxe-column>
                            <vxe-column width="100" title="操作">
                                <template #default="{ row }">
                                    <vxe-button type="text" icon="vxe-icon--edit-outline" content="删除" @click="deleteTableData(row)"></vxe-button>
                                </template>
                            </vxe-column>
                        </vxe-table>
                    </div>
                </div>
            </template>
        </vxe-modal>
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
            tableData: [],
            types: {
                "1": "mysql"
            }
        });

        const { proxy } = getCurrentInstance();

        const edit = reactive({
            show: false,
            editData: {}
        });

        const config = reactive({
            show: false,
            editData: {},
            tableTreeDataLoading: false,
            tableTreeData: [],
            tableData: []
        });

        const loadData = () => {
            table.loading = true;
            proxy.$utils.http.post("./api/search/source/manage/getSource").then(({ data }) => {
                table.loading = false;
                table.tableData = data.data;
            });
        };

        const handleAdd = (isEdit, row) => {
            edit.editData = isEdit ? row : {};
            edit.show = true;
        };

        const saveIndex = () => {
            let keys = Object.keys(edit.editData);
            let haveValue = true;
            keys.forEach((item) => {
                haveValue = !!edit.editData[item];
            });
            if (keys.length < 7 || !haveValue) {
                alert("参数不完整");
                return;
            }
            proxy.$utils.http.post("./api/search/source/manage/addSource", { id: edit.editData.id, sourceData: edit.editData }).then(() => {
                setTimeout(() => {
                    loadData();
                }, 1000);
            });
        };

        const testConnect = (row) => {
            proxy.$utils.http.post("./api/search/source/manage/testConnect", { sourceData: row }).then((data) => {
                if (data.success) {
                    alert("连接成功");
                } else {
                    alert("连接失败");
                }
            });
        };

        const showConfig = (row) => {
            config.show = true;
            config.editData = row;
            getTableAndField();
            getConfigListByDatabaseId();
        };

        const getTableAndField = () => {
            config.tableTreeDataLoading = true;
            proxy.$utils.http.post("./api/search/source/manage/getTableAndField", { sourceData: config.editData }).then(({ data }) => {
                config.tableTreeDataLoading = false;
                if (data.success) {
                    config.tableTreeData = data.data;
                }
            });
        };

        const getConfigListByDatabaseId = () => {
            proxy.$utils.http.post("./api/search/source/manage/getConfigListByDatabaseId", { databaseId: config.editData.id }).then(({ data }) => {
                if (data.success && data.data) {
                    config.tableData = data.data;
                }
            });
        };

        const handCellClick = ({ row }) => {
            if (!row.parentId) {
                return;
            }
            config.tableData.push({
                databaseId: config.editData.id,
                schema: config.editData.schema,
                tableName: row.parentId,
                fieldName: row.name,
                time: new Date().getTime(),
                type: config.editData.type,
                primaryKey: row.primaryKey
            });
            proxy.$refs.configTable.loadData(config.tableData);
        };

        const deleteTableData = (row) => {
            config.tableData.splice(config.tableData.indexOf(row), 1);
            proxy.$refs.configTable.loadData(config.tableData);
        };

        const saveConfig = () => {
            proxy.$utils.http.post("./api/search/source/manage/saveConfig", { configData: config.tableData }).then(({ data }) => {
                if (data.success) {
                    alert("操作成功！");
                    config.show = false;
                }
            });
        };

        const fileData = reactive({
            fileList: []
        });

        const uploadChange = ({ file, fileList, event }) => {
            console.log(file);
            console.log(fileList);
            console.log(event);
        };

        onMounted(() => {
            // 将表格和工具栏进行关联
            proxy.$nextTick(() => {
                // 将表格和工具栏进行关联
                const $table = proxy.$refs.xTable;
                $table.connect(proxy.$refs.xToolbar);
            });
            loadData();
        });

        return {
            table,
            edit,
            config,
            loadData,
            handleAdd,
            showConfig,
            handCellClick,
            deleteTableData,
            saveConfig,
            saveIndex,
            testConnect,
            fileData,
            uploadChange
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
.form-content {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: space-between;
}
.form-item {
    width: 45%;
    padding: 5px;
}
.config-tree-content {
    width: 20%;
    overflow: auto;
}
.config-table-content {
    width: 78%;
}
</style>

<template>
  <div class="mod-sys-area">
    <div class="search-bar">
      <el-input
        v-model="areaNameEn"
        class="area-search-input"
        clearable
        :placeholder="$t('sys.regionalKeyword')"
      />
      <div
        class="default-btn primary-btn area-add-btn"
        @click="onAddOrUpdate()"
      >
        {{ $t('sysManagement.add') }}
      </div>
    </div>

    <el-tree
      ref="tree2Ref"
      :data="areaTreeData"
      node-key="areaId"
      :filter-node-method="filterNode"
      :props="props"
      :expand-on-click-node="false"
    >
      <template #default="{ node, data }">
        <div class="custom-tree-node">
          <span>{{ node.label }}</span>
          <div>
            <div
              class="default-btn text-btn"
              @click="() => update(data)"
            >
              {{ $t('coupon.edit') }}
            </div>
            <div
              class="default-btn text-btn"
              @click="() => remove(data)"
            >
              {{ $t('coupon.delete') }}
            </div>
          </div>
        </div>
      </template>
    </el-tree>

    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdateRef"
      @refresh-data-list="getDataList"
    />
  </div>
</template>

<script setup>
import AddOrUpdate from './add-or-update.vue'
import { treeDataTranslate } from '@/utils/index.js'
import { ElMessage, ElMessageBox } from 'element-plus'

const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
const addOrUpdateVisible = ref(false)
const areaNameEn = ref('')
const areaTreeData = ref([])
const props = {
  id: 'areaId',
  label: 'areaNameEn',
  children: 'children'
}
const tree2Ref = ref(null)
watch(
  () => areaNameEn.value,
  (val) => {
    tree2Ref.value?.filter(val)
  }
)
onMounted(() => {
  getDataList(page)
})
const getDataList = (pageParam, params) => {
  http({
    url: http.adornUrl('/admin/area/list'),
    method: 'get',
    params: http.adornParams(Object.assign({
      current: pageParam == null ? page.currentPage : pageParam.currentPage,
      size: pageParam == null ? page.pageSize : pageParam.pageSize
    }, params))
  }).then(({ data }) => {
    areaTreeData.value = treeDataTranslate(data, 'areaId', 'parentId')
  })
}
const addOrUpdateRef = ref(null)
// 新增 / 修改
const onAddOrUpdate = (id) => {
  addOrUpdateVisible.value = true
  nextTick(() => {
    addOrUpdateRef.value?.init(id)
  })
}

// 删除
const onDelete = (areaId) => {
  ElMessageBox.confirm($t('seckill.isDeleOper'), $t('text.tips'), {
    confirmButtonText: $t('coupon.confirm'),
    cancelButtonText: $t('coupon.cancel'),
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl('/admin/area/' + areaId),
      method: 'delete',
      data: http.adornData({})
    }).then(() => {
      ElMessage({
        message: $t('publics.operation'),
        type: 'success',
        duration: 1500,
        onClose: () => {
          getDataList(page)
        }
      })
    })
  }).catch(() => { })
}
const update = data => {
  onAddOrUpdate(data.areaId)
}

const remove = data => {
  onDelete(data.areaId)
}
const filterNode = (value, data) => {
  if (!value) return true
  return data.areaNameEn.indexOf(value) !== -1
}

</script>

<style lang="scss" scoped>
.mod-sys-area {
  .search-bar {
    background: transparent;
    margin-bottom: 15px;
    padding: 0;
  }
  .custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right: 8px;
  }
  .area-search-input {
    width: 30%;
  }
  .area-add-btn {
    float: right;
  }
}
</style>

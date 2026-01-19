<template>
  <div class="mod-menu main-container">
    <!-- 操作栏 -->
    <div class="operation-bar">
      <div
        v-if="isAuth('sys:menu:save')"
        class="default-btn primary-btn"
        @click="onAddOrUpdate()"
      >
        {{ $t("sysManagement.add") }}
      </div>
    </div>

    <!-- 表格 -->
    <div class="table-con sys-menu-table">
      <el-table
        :data="dataList"
        header-cell-class-name="table-header"
        style="width: 100%;"
        row-key="menuId"
        lazy
        :load="load"
        :tree-props="{ children: 'childrenList', hasChildren: 'hasChildren' }"
      >
        <el-table-column
          prop="name"
          tree-key="menuId"
          width="260"
          :label="$t('publics.name')"
        />
        <el-table-column
          :label="$t('sys.icon')"
        >
          <template #default="scope">
            <svg-icon
              v-if="scope.row.icon"
              style="stroke: #8A979E !important;color: #8A979E"
              :name="scope.row.icon || ''"
              :icon-class="'icon-' + scope.row.icon"
            />
            <span
              v-else
              style="margin-left:8px"
            >-</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="type"
          :label="$t('sysManagement.type')"
        >
          <template #default="scope">
            <span v-if="scope.row.type === 0">{{ $t('sys.catalog') }}</span>
            <span v-if="scope.row.type === 1">{{ $t('sys.menu') }}</span>
            <span v-if="scope.row.type === 2">{{ $t('sys.button') }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="hidden"
          :label="$t('sys.isHidden')"
        >
          <template #default="scope">
            <span v-if="scope.row.hidden">{{ $t('sys.yes') }}</span>
            <span v-else>{{ $t('sys.no') }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="orderNum"
          :label="$t('hotSearch.seq')"
        />
        <el-table-column
          prop="url"
          width="150"
          :show-overflow-tooltip="true"
          :label="$t('sys.menuUrl')"
        >
          <template #default="scope">
            <span>{{ scope.row.url || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="perms"
          width="150"
          :show-overflow-tooltip="true"
          :label="$t('sys.authorization')"
        >
          <template #default="scope">
            <span>{{ scope.row.perms || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          align="center"
          width="150"
          :label="$t('sys.operation')"
        >
          <template #default="scope">
            <div class="text-btn-con">
              <div
                v-if="isAuth('sys:menu:update')"
                class="default-btn text-btn"
                @click="onAddOrUpdate(scope.row.menuId)"
              >
                {{ $t('sysManagement.modify') }}
              </div>
              <div
                v-if="isAuth('sys:menu:delete')"
                class="default-btn text-btn"
                @click="onDelete(scope.row)"
              >
                {{ $t('coupon.delete') }}
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdateRef"
      @refresh-data-list="getDataList"
      @close="addOrUpdateClose"
    />
  </div>
</template>

<script setup>
import AddOrUpdate from './add-or-update.vue'
import { treeDataTranslate, isAuth } from '@/utils/index.js'
import { ElMessage, ElMessageBox } from 'element-plus'

const dataList = ref([])
const dataListLoading = ref(false)

onMounted(() => {
  getDataList()
})
// 获取数据列表
const getDataList = () => {
  dataListLoading.value = true
  http({
    url: http.adornUrl('/sys/menu/table'),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    dataList.value = treeDataTranslate(data, 'menuId')
    dataListLoading.value = false
  })
}
// 新增 / 修改
const addOrUpdateRef = ref(null)
const addOrUpdateVisible = ref(false)
const onAddOrUpdate = (id) => {
  addOrUpdateVisible.value = true
  nextTick(() => {
    addOrUpdateRef.value?.init(id)
  })
}
const router = useRouter()
// 删除
const onDelete = (row) => {
  ElMessageBox.confirm($t('seckill.isDeleOper'), $t('text.tips'), {
    confirmButtonText: $t('coupon.confirm'),
    cancelButtonText: $t('coupon.cancel'),
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl(`/sys/menu/${row.menuId}`),
      method: 'delete',
      data: http.adornData()
    }).then(() => {
      if (row.type === 1) {
        router.go(0)
      }
      ElMessage({
        message: $t('publics.operation'),
        type: 'success',
        duration: 1500,
        onClose: () => {
          getDataList()
        }
      })
    })
  })
}

const load = (row, treeNode, resolve) => {
  setTimeout(() => {
    resolve(row.children)
  }, 300)
}

/**
 * 关闭销毁编辑弹框
 */
const addOrUpdateClose = () => {
  addOrUpdateVisible.value = false
}
</script>

<style lang="scss" scoped>
</style>

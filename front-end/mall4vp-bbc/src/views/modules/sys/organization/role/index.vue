<template>
  <div class="mod-role">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form
        ref="searchFormRef"
        :inline="true"
        class="search-form"
        :model="searchForm"
        @submit.prevent
      >
        <div class="input-row">
          <el-form-item
            :label="$t('sys.roleName') + ':'"
            class="search-form-item"
            prop="roleName"
          >
            <el-input
              v-model="searchForm.roleName"
              type="text"
              clearable
              :placeholder="$t('sys.roleName')"
            />
          </el-form-item>
          <el-form-item>
            <div
              class="default-btn primary-btn"
              @click="onSearch(true)"
            >
              {{ $t('crud.searchBtn') }}
            </div>
            <div
              class="default-btn"
              @click="onResetSearch()"
            >
              {{ $t('product.reset') }}
            </div>
          </el-form-item>
        </div>
      </el-form>
    </div>

    <div class="main-container">
      <!-- 操作栏 -->
      <div class="operation-bar">
        <el-checkbox
          v-if="dataList.length"
          v-model="selectAll"
          :disabled="!dataList.length"
          class="all-check-btn"
          @change="onSelectAll"
        >
          {{ $t('publics.selectAll') }}
        </el-checkbox>
        <!-- <span v-if="dataListSelections.length" class="had-selected">{{$t('dataAnalysis.selected')}} {{dataListSelections.length}}</span> -->
        <div
          v-if="isAuth('sys:role:save')"
          class="default-btn primary-btn"
          @click.stop="onAddOrUpdate()"
        >
          {{ $t("sysManagement.add") }}
        </div>
        <div
          v-if="isAuth('sys:role:delete')"
          :class="[dataListSelections.length <= 0 ? 'disabled-btn' : '', 'default-btn']"
          :disabled="dataListSelections.length <= 0"
          @click="onDelete()"
        >
          {{ $t('sys.batchDelete') }}
        </div>
      </div>

      <!-- 表格 -->
      <div class="table-con sys-role-table">
        <el-table
          ref="sysRoleTableRef"
          :empty-text="$t('order.noData')"
          :data="dataList"
          header-cell-class-name="table-header"
          row-class-name="table-row-low"
          style="width: 100%"
          @selection-change="selectionChange"
        >
          <el-table-column
            v-if="dataList.length"
            type="selection"
            width="55"
          />
          <!-- 用户名 -->
          <el-table-column
            prop="roleName"
            :label="$t('sys.roleName')"
          >
            <template #default="scope">
              {{ scope.row.roleName }}
            </template>
          </el-table-column>
          <!-- 备注 -->
          <el-table-column
            prop="remark"
            :label="$t('publics.remark')"
          >
            <template #default="scope">
              <span class="table-cell-text">{{ scope.row.remark || '-' }}</span>
            </template>
          </el-table-column>
          <!-- 创建时间 -->
          <el-table-column
            prop="createTime"
            :label="$t('sys.creationTime')"
          >
            <template #default="scope">
              {{ scope.row.createTime }}
            </template>
          </el-table-column>
          <!-- 操作 -->
          <el-table-column
            fixed="right"
            align="center"
            :label="$t('publics.operating')"
            width="150"
          >
            <template #default="scope">
              <div class="text-btn-con">
                <div
                  v-if="isAuth('sys:role:update')"
                  class="default-btn text-btn"
                  @click.stop="onAddOrUpdate(scope.row.roleId)"
                >
                  {{ $t('coupon.edit') }}
                </div>
                <div
                  v-if="isAuth('sys:role:delete')"
                  class="default-btn text-btn"
                  @click.stop="onDelete(scope.row.roleId)"
                >
                  {{ $t('coupon.delete') }}
                </div>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <el-pagination
        v-if="dataList.length"
        :current-page="page.currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="page.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="page.total"
        @size-change="onPageSizeChange"
        @current-change="onPageChange"
      />
    </div>

    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdateRef"
      @refresh-data-list="refreshChange"
    />
  </div>
</template>

<script setup>
import AddOrUpdate from './add-or-update.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { isAuth } from '@/utils/index.js'

let tempSearchForm = null // 保存上次点击查询的请求条件

const dataList = ref([])
const searchFormRef = ref(null)
const sysRoleTableRef = ref(null)
const dataListLoading = ref(false)
const dataListSelections = ref([])
const addOrUpdateRef = ref(null)
const addOrUpdateVisible = ref(false)
const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
const searchForm = reactive({})
const selectAll = ref(false)
onMounted(() => {
  getDataList()
})

// 获取数据列表
const getDataList = (pageParam, newData = false) => {
  dataListLoading.value = true
  if (newData || !tempSearchForm) {
    tempSearchForm = JSON.parse(JSON.stringify(searchForm))
  }
  http({
    url: http.adornUrl('/sys/role/page'),
    method: 'get',
    params: http.adornParams(
      Object.assign(
        {
          current: pageParam == null ? page.currentPage : pageParam.currentPage,
          size: pageParam == null ? page.pageSize : pageParam.pageSize
        },
        tempSearchForm
      )
    )
  }).then(({ data }) => {
    dataList.value = data.records
    page.total = data.total
    dataListLoading.value = false
  })
}
// 条件查询
const onSearch = (newData = false) => {
  page.currentPage = 1
  getDataList(page, newData)
}
// 刷新回调用
const refreshChange = () => {
  page.currentPage = 1
  getDataList(page)
}
// 多选变化
const selectionChange = (val) => {
  dataListSelections.value = val
  selectAll.value = val.length === dataList.value.length
}
/**
 * 全选按钮
 */
const onSelectAll = () => {
  selectAll.value = sysRoleTableRef.value.selection?.length < dataList.value.length
  sysRoleTableRef.value?.toggleAllSelection()
}
// 新增 / 修改
const onAddOrUpdate = (id) => {
  addOrUpdateVisible.value = true
  nextTick(() => {
    addOrUpdateRef.value?.init(id)
  })
}
// 删除
const onDelete = (id) => {
  const ids = id ? [id] : dataListSelections.value.map(item => {
    return item.roleId
  })
  if (!id && !dataListSelections.value.length) {
    return
  }
  ElMessageBox.confirm(`${$t('sysManagement.confirmSure')}${id ? $t('coupon.delete') : $t('sys.batchDelete')}${$t('sysManagement.operation')}`, $t('text.tips'), {
    confirmButtonText: $t('coupon.confirm'),
    cancelButtonText: $t('coupon.cancel'),
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl('/sys/role'),
      method: 'delete',
      data: http.adornData(ids, false)
    }).then(() => {
      ElMessage({
        message: $t('publics.operation'),
        type: 'success',
        duration: 1500,
        onClose: () => {
          refreshChange()
        }
      })
    })
  }).catch(() => { })
}

/**
 * 重置表单
 */
const onResetSearch = () => {
  searchFormRef.value?.resetFields()
}

const onPageSizeChange = (val) => {
  page.pageSize = val
  getDataList(page)
}
const onPageChange = (val) => {
  page.currentPage = val
  getDataList(page)
}

</script>

<style lang="scss" scoped>

</style>

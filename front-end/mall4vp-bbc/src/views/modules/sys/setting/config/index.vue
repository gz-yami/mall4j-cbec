<template>
  <div class="mod-config">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-form
        ref="searchFormRef"
        :inline="true"
        class="search-form"
        :model="searchForm"
        @submit.prevent
      >
        <div class="input-row margin-bottom-none">
          <el-form-item
            :label="$t('sys.parameteName') + ':'"
            class="search-form-item"
          >
            <el-input
              v-model="searchForm.paramKey"
              type="text"
              clearable
              :placeholder="$t('sys.parameteName')"
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
              @click="onResetSearch('searchForm')"
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
          v-model="selectAll"
          :disabled="!dataList.length"
          class="all-check-btn"
          @change="onSelectAll"
        >
          {{ $t('publics.selectAll') }}
        </el-checkbox>
        <div
          class="default-btn primary-btn"
          @click.stop="onAddOrUpdate()"
        >
          {{ $t("sysManagement.add") }}
        </div>
        <div
          :class="[dataListSelections.length <= 0 ? 'disabled-btn' : '', 'default-btn']"
          :disabled="dataListSelections.length <= 0"
          @click="onDelete()"
        >
          {{ $t('publics.batchDelete') }}
        </div>
      </div>

      <!-- 表格 -->
      <div class="table-con config-table">
        <el-table
          ref="configTableRef"
          :empty-text="$t('order.noData')"
          :data="dataList"
          header-cell-class-name="table-header"
          style="width: 100%"
          :row-key="getRowKeys"
          @selection-change="selectionChange"
        >
          <el-table-column
            type="selection"
            :reserve-selection="true"
            width="55"
          />
          <!-- 参数名 -->
          <el-table-column
            prop="paramKey"
            :label="$t('sys.parameteName')"
          >
            <template #default="scope">
              {{ scope.row.paramKey }}
            </template>
          </el-table-column>
          <!-- 参数值 -->
          <el-table-column
            prop="paramValue"
            :label="$t('sys.parameterValue')"
          >
            <template #default="scope">
              {{ scope.row.paramValue }}
            </template>
          </el-table-column>
          <!-- 备注 -->
          <el-table-column
            prop="remark"
            :label="$t('publics.remark')"
          >
            <template #default="scope">
              {{ scope.row.remark }}
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
                  class="default-btn text-btn"
                  @click.stop="onAddOrUpdate(scope.row.id)"
                >
                  {{ $t('coupon.edit') }}
                </div>
                <div
                  class="default-btn text-btn"
                  @click.stop="onDelete(scope.row.id)"
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

let tempSearchForm = null // 保存上次点击查询的请求条件

const dataList = ref([])
const dataListLoading = ref(false)
const dataListSelections = ref([])
const addOrUpdateVisible = ref(false)
const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
const searchForm = reactive({})
const selectAll = ref(false)
onBeforeUnmount(() => {
  configTableRef.value?.clearSelection()
})
onMounted(() => {
  getDataList(page)
})

// 获取数据列表
const getDataList = (pageParam, newData = false) => {
  dataListLoading.value = true
  if (newData || !tempSearchForm) {
    tempSearchForm = JSON.parse(JSON.stringify(searchForm))
  }
  http({
    url: http.adornUrl('/sys/config/page'),
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
const getRowKeys = (row) => {
  return row.id
}
/**
 * 全选按钮
 */
const configTableRef = ref(null)
const onSelectAll = () => {
  selectAll.value = configTableRef.value.selection?.length < dataList.value.length
  configTableRef.value?.toggleAllSelection()
}
// 新增 / 修改
const addOrUpdateRef = ref(null)
const onAddOrUpdate = (id) => {
  addOrUpdateVisible.value = true
  nextTick(() => {
    addOrUpdateRef.value?.init(id)
  })
}
// 删除
const onDelete = (id) => {
  if (!id && !dataListSelections.value.length) {
    return
  }
  const ids = id ? [id] : dataListSelections.value.map(item => {
    return item.id
  })
  ElMessageBox.confirm(`${$t('sysManagement.confirmSure')}${id ? $t('coupon.delete') : $t('sys.batchDelete')}${$t('sysManagement.operation')}`, $t('text.tips'), {
    confirmButtonText: $t('coupon.confirm'),
    cancelButtonText: $t('coupon.cancel'),
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl('/sys/config'),
      method: 'delete',
      data: http.adornData(ids, false)
    }).then(() => {
      configTableRef.value?.clearSelection()
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
 * @param {String} formName 表单名称
 */
const searchFormRef = ref(null)
const onResetSearch = () => {
  searchFormRef.value.resetFields()
  for (const key in searchForm) {
    delete searchForm[key]
  }
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

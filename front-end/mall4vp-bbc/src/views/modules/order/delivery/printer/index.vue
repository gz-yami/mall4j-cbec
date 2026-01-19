<template>
  <div class="page-delivery-printer">
    <!-- 搜索相关区域 -->
    <div class="search-bar">
      <el-form
        ref="searchFormRef"
        :inline="true"
        class="search-form"
        :model="searchForm"
        label-width="auto"
        @submit.prevent
      >
        <div class="input-row">
          <el-form-item
            prop="printerName"
            :label="$t('printer.printerName')+':'"
          >
            <el-input
              v-model.trim="searchForm.printerName"
              type="text"
              :placeholder="$t('printer.printerNameTips')"
              clearable
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
              @click="onResetSearch"
            >
              {{ $t('shop.resetMap') }}
            </div>
          </el-form-item>
        </div>
      </el-form>
    </div>
    <!-- 列表相关区域 -->
    <div class="main-container">
      <div class="operation-bar print-info">
        <button
          v-if="isAuth('delivery:printer:save')"
          class="default-btn primary-btn"
          @click="onAddOrUpdate()"
        >
          {{ $t("crud.addTitle") }}
        </button>
        <div class="printer-head">
          <div
            class="text"
            @click="onGetInfo()"
          >
            {{ $t('printer.deviceInfo') }}
          </div>
        </div>
      </div>
      <div class="table-con printer-table">
        <el-table
          ref="printerTable"
          :empty-text="$t('order.noData')"
          :data="dataList"
          header-cell-class-name="table-header"
          row-class-name="table-row-low"
          style="width: 100%"
        >
          <!-- 打印机名称 -->
          <el-table-column
            :label="$t('printer.printerName')"
            prop="printerName"
            align="center"
          >
            <template #default="scope">
              <span>{{ scope.row.printerName }}</span>
            </template>
          </el-table-column>
          <!-- 设备码 -->
          <el-table-column
            :label="$t('printer.siid')"
            prop="siid"
            align="center"
          >
            <template #default="scope">
              <span>{{ scope.row.siid }}</span>
            </template>
          </el-table-column>
          <!-- 打印机备注 -->
          <el-table-column
            :label="$t('printer.printerRemark')"
            prop="printerRemark"
            align="center"
          >
            <template #default="scope">
              <span>{{ scope.row.printerRemark || '-' }}</span>
            </template>
          </el-table-column>
          <!-- 是否默认 0否1是 -->
          <el-table-column
            :label="$t('printer.isDefault')"
            prop="isDefault"
            align="center"
          >
            <template #default="scope">
              <span>{{ scope.row.isDefault === 1 ? $t('publics.yes'):$t('publics.no') }}</span>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            fixed="right"
            :label="$t('publics.operating')"
            width="auto"
          >
            <template #default="scope">
              <div class="text-btn-con">
                <div
                  v-if="isAuth('delivery:printer:update')"
                  class="default-btn text-btn"
                  @click="onAddOrUpdate(scope.row.printerId)"
                >
                  {{ $t("crud.updateBtn") }}
                </div>
                <div
                  v-if="isAuth('delivery:printer:delete')"
                  class="default-btn text-btn"
                  @click.stop="onDelete(scope.row.printerId)"
                >
                  {{ $t("text.delBtn") }}
                </div>
                <div
                  v-if="isAuth('delivery:printer:setDefault')"
                  class="default-btn text-btn"
                  @click="onSetDefault(scope.row.printerId)"
                >
                  {{ scope.row.isDefault === 1 ? $t('common.undefault') : $t('common.setAsDefault') }}
                </div>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-pagination
        v-if="dataList.length"
        :current-page="page.currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="page.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="page.total"
        @update:page-size="onPageSizeChange"
        @update:current-page="onPageChange"
      />
    </div>
    <AddOrUpdate
      v-if="addOrUpdateVisible"
      ref="addOrUpdateRef"
      @refresh-data-list="refreshChange"
    />
  </div>
</template>

<script setup>
import AddOrUpdate from './add-or-update.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { isAuth } from '@/utils/index'

let tempSearchForm = null // 保存上次点击查询的请求条件

const dataList = ref([])

const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})

// 头部搜索表单
const searchForm = reactive({
  printerName: ''
})

onMounted(() => {
  onGetQuick100()
  getDataList()
})

const getDataList = (pageParam, newData = false) => {
  if (newData || !tempSearchForm) {
    tempSearchForm = JSON.parse(JSON.stringify(searchForm))
  }
  http({
    url: http.adornUrl('/platform/printer/page'),
    method: 'get',
    params: http.adornParams(Object.assign(tempSearchForm, {
      current: pageParam == null ? page.currentPage : pageParam.currentPage,
      size: pageParam == null ? page.pageSize : pageParam.pageSize
    }), false)
  }).then(({ data }) => {
    dataList.value = data.records
    page.total = data.total
    page.currentPage = data.current
  })
}

// 新增 / 修改
const addOrUpdateRef = ref(null)
const addOrUpdateVisible = ref(false)
const onAddOrUpdate = (printerId) => {
  if (isPrint.value === 0) {
    return ElMessage({
      message: $t('printer.defaultTips'),
      type: 'error',
      duration: 1500
    })
  }
  addOrUpdateVisible.value = true
  nextTick(() => {
    addOrUpdateRef.value?.init(printerId)
  })
}

// 删除
const onDelete = (id) => {
  ElMessageBox.confirm($t('admin.isDeleOper'), $t('text.tips'), {
    confirmButtonText: $t('crud.filter.submitBtn'),
    cancelButtonText: $t('crud.filter.cancelBtn'),
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl('/platform/printer/' + id),
      method: 'delete',
      data: http.adornData({})
    }).then(() => {
      ElMessage({
        message: $t('publics.operation'),
        type: 'success',
        duration: 1500,
        onClose: () => {
          refreshChange()
        }
      })
    }).catch(() => {})
  })
}

// 条件查询
const onSearch = (newData = false) => {
  page.currentPage = 1
  getDataList(page, newData)
}

// 刷新回调
const refreshChange = () => {
  addOrUpdateVisible.value = false
  getDataList(page)
}

// 每页数量变更
const onPageSizeChange = val => {
  page.pageSize = val
  getDataList()
}

// 页数变更
const onPageChange = val => {
  page.currentPage = val
  getDataList()
}

// 重置表单
const searchFormRef = ref()
const onResetSearch = () => {
  searchFormRef.value.resetFields()
}
// 设为默认
const onSetDefault = (printerId) => {
  http({
    url: http.adornUrl('/platform/printer/setDefault/' + printerId),
    method: 'put',
    data: http.adornData({})
  }).then(() => {
    ElMessage({
      message: $t('user.succeeded'),
      type: 'success',
      duration: 1500,
      onClose: () => getDataList()
    })
  })
}
// 设备说明
const onGetInfo = () => {
  ElMessageBox.alert($t('printer.specialDevices1') + $t('printer.specialDevices2') + $t('printer.specialDevices3'), $t('printer.deviceInfo'), {
    dangerouslyUseHTMLString: true,
    confirmButtonText: $t('crud.filter.submitBtn')
  })
}
// 获取快递100配置
const isPrint = ref(0)
const onGetQuick100 = () => {
  http({
    url: http.adornUrl('/sys/pconfig/quick100'),
    method: 'get',
    params: http.adornParams()
  }).then((res) => {
    isPrint.value = res.data ? 1 : 0
  })
}
</script>
<style lang="scss" scoped>
.page-delivery-printer {
  .print-info {
    display: flex;
    justify-content: space-between;
  }
  .printer-head {
    height: 40px;
    line-height: 40px;
    display: flex;
    .text {
      color: #155bdc;
      font-size: 14px;
      margin-left: 7px;
      cursor:pointer;
    }
  }
}
</style>

<template>
  <div class="page-outlet-config app-container">
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
            prop="deliveryCompanyType"
            :label="$t('order.courierCompany')+':'"
          >
            <el-select
              v-model="searchForm.deliveryCompanyType"
              clearable
              :placeholder="$t('order.seleCouCom')"
            >
              <el-option
                v-for="item in deliveryCompanyType"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
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
              @click="onResetSearch(searchFormRef)"
            >
              {{ $t('shop.resetMap') }}
            </div>
          </el-form-item>
        </div>
      </el-form>
    </div>
    <!-- 列表相关区域 -->
    <div class="main-container">
      <div class="operation-bar">
        <button
          v-if="isAuth('delivery:outletConfig:save')"
          class="default-btn primary-btn"
          @click="onAddOrUpdate()"
        >
          {{ $t('crud.addTitle') }}
        </button>
      </div>
      <div class="table-con outlet-config-table">
        <el-table
          v-loading="pageLoading"
          :empty-text="$t('order.noData')"
          :data="dataList"
          header-cell-class-name="table-header"
          row-class-name="table-row-low"
          style="width: 100%"
        >
          <!-- 快递公司类型 -->
          <el-table-column
            :label="$t('outletConfig.deliveryCompanyType')"
            prop="deliveryCompanyType"
            align="center"
          >
            <template #default="scope">
              <span>{{ deliveryCompanyType[scope.row.deliveryCompanyType - 1].label }}</span>
            </template>
          </el-table-column>
          <!-- 发货人 -->
          <el-table-column
            :label="$t('outletConfig.shipper')"
            prop="shipper"
            align="center"
          >
            <template #default="scope">
              <span>{{ scope.row.shipper }}</span>
            </template>
          </el-table-column>
          <!-- 发货人电话 -->
          <el-table-column
            :label="$t('outletConfig.mobile')"
            prop="mobile"
            align="center"
          >
            <template #default="scope">
              <span>{{ scope.row.mobile }}</span>
            </template>
          </el-table-column>
          <!-- 发货地址 -->
          <el-table-column
            :label="$t('order.deliveryAddress')"
            align="center"
          >
            <template #default="scope">
              <span class="addr-name">{{ scope.row.printAddr }}</span>
            </template>
          </el-table-column>
          <!-- 是否默认 0否1是 -->
          <el-table-column
            :label="$t('outletConfig.isDefault')"
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
                  v-if="isAuth('delivery:outletConfig:paperSize')"
                  class="default-btn text-btn"
                  @click="onGetPaperSize(scope.row)"
                >
                  {{ $t("outletConfig.paperSize") }}
                </div>
                <div
                  v-if="isAuth('delivery:outletConfig:update')"
                  class="default-btn text-btn"
                  @click="onAddOrUpdate(scope.row.outletConfigId)"
                >
                  {{ $t("crud.updateBtn") }}
                </div>
                <div
                  v-if="isAuth('delivery:outletConfig:delete')"
                  class="default-btn text-btn"
                  @click="onDelete(scope.row.outletConfigId)"
                >
                  {{ $t("text.delBtn") }}
                </div>
                <div
                  v-if="isAuth('delivery:outletConfig:setDefault')"
                  class="default-btn text-btn"
                  @click="onSetDefault(scope.row.outletConfigId)"
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
        @current-change="onPageChange"
      />
    </div>
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdateRef"
      @refresh-data-list="refreshChange"
    />
    <PaperSize
      ref="paperSizeRef"
      @refresh-data-list="refreshChange"
    />
  </div>
</template>

<script setup>
import { isAuth } from '@/utils/index'
import AddOrUpdate from './add-or-update.vue'
import PaperSize from './components/paper-size.vue'
import { ElMessage, ElMessageBox } from 'element-plus'

let tempSearchForm = null // 保存上次点击查询的请求条件
const dataList = ref([])
// 返回参数
const page = ref({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
const deliveryCompanyType = [{
  value: 1,
  label: $t('express.sf')
}, {
  value: 2,
  label: $t('express.jd')
}, {
  value: 3,
  label: $t('express.yto')
}, {
  value: 4,
  label: $t('express.yunDa')
}, {
  value: 5,
  label: $t('express.zto')
}, {
  value: 6,
  label: $t('express.sto')
}, {
  value: 7,
  label: $t('express.best')
}, {
  value: 8,
  label: $t('express.ems')
}]

// 头部搜索表单
const searchForm = reactive({
  deliveryCompanyType: null
})
const pageLoading = ref(true)

onMounted(() => {
  onGetQuick100()
  getPageData()
})

const getPageData = (pageParam, newData = false) => {
  if (newData || !tempSearchForm) {
    tempSearchForm = JSON.parse(JSON.stringify(searchForm))
  }
  pageLoading.value = true
  http({
    url: http.adornUrl('/platform/outletConfig/page'),
    method: 'get',
    params: http.adornParams(
      Object.assign(
        {
          current: pageParam == null ? page.value.currentPage : pageParam.currentPage,
          size: pageParam == null ? page.value.pageSize : pageParam.pageSize
        },
        tempSearchForm
      )
    )
  }).then(({ data }) => {
    dataList.value = data.records
    page.value.total = data.total
    pageLoading.value = false
  }).catch(() => {
    pageLoading.value = false
  })
}

// 新增 / 修改
const addOrUpdateRef = ref(null)
const addOrUpdateVisible = ref(false)
const onAddOrUpdate = (outletConfigId) => {
  if (isPrint.value === 0) {
    return ElMessage({
      message: $t('printer.defaultTips'),
      type: 'error',
      duration: 1500
    })
  }
  addOrUpdateVisible.value = true
  nextTick(() => {
    addOrUpdateRef.value?.init(outletConfigId)
  })
}

// 删除
const onDelete = (outletConfigId) => {
  ElMessageBox.confirm($t('admin.isDeleOper'), $t('text.tips'), {
    confirmButtonText: $t('crud.filter.submitBtn'),
    cancelButtonText: $t('crud.filter.cancelBtn'),
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl('/platform/outletConfig/' + outletConfigId),
      method: 'delete',
      data: http.adornData({})
    }).then(() => {
      ElMessage({
        message: $t('publics.operation'),
        type: 'success',
        duration: 1500,
        onClose: () => refreshChange()
      })
    })
  })
}
// 条件查询
const onSearch = (newData = false) => {
  page.value.currentPage = 1
  getPageData(page.value, newData)
}

const onPageSizeChange = (val) => {
  page.value.pageSize = val
  getPageData(page.value)
}
const onPageChange = (val) => {
  page.value.currentPage = val
  getPageData(page.value)
}

// 刷新回调
const refreshChange = () => {
  page.value.currentPage = 1
  getPageData()
}

// 重置表单
const searchFormRef = ref(null)
const onResetSearch = (formEl) => {
  formEl.resetFields()
}

// 获取纸张规格
const paperSizeRef = ref(null)
const onGetPaperSize = (item) => {
  paperSizeRef.value.init(JSON.parse(JSON.stringify(item)))
}

// 设为默认
const onSetDefault = (outletConfigId) => {
  http({
    url: http.adornUrl('/platform/outletConfig/setDefault/' + outletConfigId),
    method: 'put',
    data: http.adornData({})
  }).then(() => {
    ElMessage({
      message: $t('user.succeeded'),
      type: 'success',
      duration: 1500,
      onClose: () => getPageData()
    })
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
.page-outlet-config {
  .addr-name {
    text-overflow: ellipsis;
    -o-text-overflow: ellipsis;
    -webkit-text-overflow: ellipsis;
    -moz-text-overflow: ellipsis;
    word-break: break-word;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  .text-btn-con{
    flex-wrap: wrap;
  }
}
</style>

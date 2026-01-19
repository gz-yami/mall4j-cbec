<template>
  <div class="mod-p-delivery">
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
            prop="dvyName"
            :label="$t('order.logisticsCompanyName') + ':'"
            class="search-form-item"
          >
            <el-input
              v-model="searchForm.dvyName"
              type="text"
              clearable
              class="select-input-width"
              :placeholder="$t('order.logisticsCompanyName')"
            />
          </el-form-item>
          <el-form-item
            prop="dvyNoTrack"
            :label="`${$t('platform.logisticsCompanyNumber')}(17TRACK):`"
            class="search-form-item"
          >
            <el-input
              v-model="searchForm.dvyNoTrack"
              type="text"
              class="select-input-width"
              clearable
              :placeholder="`${$t('platform.logisticsCompanyNumber')}(17TRACK)`"
            />
          </el-form-item>
          <el-form-item
            prop="dvyNoHd"
            :label="`${$t('platform.logisticsCompanyNumber')}(${$t('sysManagement.quick100')}):`"
            class="search-form-item"
          >
            <el-input
              v-model="searchForm.dvyNoHd"
              class="select-input-width"
              type="text"
              clearable
              :placeholder="`${$t('platform.logisticsCompanyNumber')}(${$t('sysManagement.quick100')})`"
            />
          </el-form-item>
          <el-form-item>
            <div
              class="default-btn primary-btn"
              @click="onSearch(true)"
            >
              {{ $t('product.search') }}
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
      <div class="operation-bar">
        <div
          v-if="isAuth('platform:delivery:save')"
          class="default-btn primary-btn"
          @click="onAddOrUpdate()"
        >
          {{ $t('sysManagement.add') }}
        </div>
      </div>
      <div class="table-con delivery-table">
        <el-table
          ref="deliveryListTableRef"
          :empty-text="$t('order.noData')"
          :data="dataList"
          header-cell-class-name="table-header"
          row-class-name="table-row-low"
          style="width: 100%"
        >
          <el-table-column
            fixed
            type="index"
            :label="$t('brand.serialNumber')"
            width="100"
          />

          <el-table-column
            fixed
            prop="dvyName"
            :label="$t('order.logisticsCompanyName')"
            min-width="300"
          >
            <template #default="scope">
              <span class="table-cell-text">{{ scope.row.dvyName }}</span>
            </template>
          </el-table-column>

          <el-table-column
            prop="companyHomeUrl"
            :label="$t('platform.companyHomepage')"
            min-width="300"
          >
            <template #default="scope">
              <span
                v-if="scope.row.companyHomeUrl"
                class="table-cell-text"
              >{{ scope.row.companyHomeUrl }}</span>
              <span
                v-if="!scope.row.companyHomeUrl"
                class="table-cell-text"
              >-</span>
            </template>
          </el-table-column>
          <el-table-column
            prop="dvyNoTrack"
            :label="`${$t('platform.logisticsCompanyNumber')}（17TRACK）`"
            min-width="300"
          >
            <template #default="scope">
              <span
                v-if="scope.row.dvyNoTrack"
                class="table-cell-text"
              >{{ scope.row.dvyNoTrack }}</span>
              <span
                v-if="!scope.row.dvyNoTrack"
                class="table-cell-text"
              >-</span>
            </template>
          </el-table-column>
          <el-table-column
            prop="dvyNoHd"
            :label="`${$t('platform.logisticsCompanyNumber')}（${$t('sysManagement.quick100')}）`"
            min-width="300"
          >
            <template #default="scope">
              <span
                v-if="scope.row.dvyNoHd"
                class="table-cell-text"
              >{{ scope.row.dvyNoHd }}</span>
              <span
                v-if="!scope.row.dvyNoHd"
                class="table-cell-text"
              >-</span>
            </template>
          </el-table-column>
          <el-table-column
            fixed="right"
            align="center"
            :label="$t('publics.operating')"
            width="180"
          >
            <template #default="scope">
              <div class="text-btn-con">
                <div
                  v-if="isAuth('platform:delivery:update')"
                  class="default-btn text-btn"
                  @click="onAddOrUpdate(scope.row.dvyId)"
                >
                  {{ $t('user.editBtn') }}
                </div>
                <div
                  v-if="isAuth('platform:delivery:delete')"
                  class="default-btn text-btn"
                  @click="onDelete(scope.row.dvyId)"
                >
                  {{ $t('remindPop.delete') }}
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
        @size-change="onPageSizeChange"
        @current-change="onPageChange"
      />
    </div>
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
import { isAuth } from '@/utils'

const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
const searchForm = reactive({
  dvyName: '',
  dvyNoTrack: '',
  dvyNoHd: ''
}) // 搜索

onMounted(() => {
  getDataList()
})

let tempSearchForm = null // 保存上次点击查询的请求条件
const dataList = ref([])
const getDataList = (pageParam, newData = false) => {
  if (page) {
    const size = Math.ceil(page.total / page.pageSize)
    page.currentPage = (page.currentPage > size ? size : page.currentPage) || 1
  }
  if (newData || !tempSearchForm) {
    tempSearchForm = JSON.parse(JSON.stringify(searchForm))
  }
  http({
    url: http.adornUrl('/platform/delivery/page'),
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
  })
}

const addOrUpdateVisible = ref(false)
const addOrUpdateRef = ref(null)
// 新增 / 修改
const onAddOrUpdate = (id) => {
  addOrUpdateVisible.value = true
  nextTick(() => {
    addOrUpdateRef.value?.init(id)
  })
}
const onDelete = (id) => {
  const ids = id ? [id] : []
  ElMessageBox.confirm($t('seckill.isDeleOper'), $t('remindPop.remind'), {
    confirmButtonText: $t('remindPop.confirm'),
    cancelButtonText: $t('remindPop.cancel'),
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl('/platform/delivery/' + id),
      method: 'delete',
      data: http.adornData({})
    }).then(() => {
      page.total = page.total - ids.length
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
 * 刷新回调
 */
const refreshChange = () => {
  getDataList(page)
}

const onSearch = (newData = false) => {
  getDataList(page, newData)
}

const searchFormRef = ref(null)
/**
 * 重置表单
 */
const onResetSearch = () => {
  searchFormRef.value.resetFields()
}

const onPageSizeChange = (val) => {
  page.pageSize = val
  getDataList()
}

const onPageChange = (val) => {
  page.currentPage = val
  getDataList()
}

</script>
<style lang="scss" scoped>
.mod-p-delivery {
  .search-bar {
    .select-input-width {
      min-width: 280px;
    }
    .input-row {
      :deep(.el-form-item__label-wrap:first-child) {
        margin-left: 0 !important;
      }
    }
  }
}

</style>

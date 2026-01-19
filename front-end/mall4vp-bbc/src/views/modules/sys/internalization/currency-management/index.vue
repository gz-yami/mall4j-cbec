<template>
  <div class="page-currency-management">
    <div class="search-bar">
      <el-form
        ref="searchFormRef"
        :inline="true"
        :model="searchForm"
        @submit.prevent
      >
        <div class="input-row">
          <!--货币名称-->
          <el-form-item
            prop="currencyName"
            :label="$t('internalization.currencyName') + ':'"
          >
            <el-input
              v-model="searchForm.currencyName"
              clearable
              :placeholder="$t('internalization.currencyName')"
            />
          </el-form-item>
          <!--货币编码-->
          <el-form-item
            prop="currencyCode"
            :label="$t('internalization.currencyCode') + ':'"
          >
            <el-input
              v-model="searchForm.currencyCode"
              clearable
              :placeholder="$t('internalization.currencyCode')"
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
          v-if="isAuth('platform:currency:update')"
          class="default-btn primary-btn"
          @click="onAddOrUpdate()"
        >
          {{ $t('internalization.changeDefault') }}
        </div>
      </div>
      <div class="table-con hot-search-table">
        <el-table
          ref="hotSearchListTableRef"
          :empty-text="$t('order.noData')"
          :data="dataList"
          header-cell-class-name="table-header"
          row-class-name="table-row-low"
          style="width: 100%"
        >
          <!--货币名称-->
          <el-table-column
            align="left"
            prop="currencyName"
            :label="$t('internalization.currencyName')"
            min-width="180"
          >
            <template #default="scope">
              <span class="table-cell-text">
                <span>{{ scope.row.currencyName }}</span>
                <el-tag
                  v-if="scope.row.defaultCurrency === 1"
                  effect="dark"
                  style="margin-left: 10px;"
                >
                  {{ $t('internalization.defaultCurrency') }}
                </el-tag>
              </span>
            </template>
          </el-table-column>
          <!--货币编码-->
          <el-table-column
            align="center"
            prop="currencyCode"
            :label="$t('internalization.currencyCode')"
            min-width="150"
          >
            <template #default="scope">
              <span class="table-cell-text">{{ scope.row.currencyCode }}</span>
            </template>
          </el-table-column>
          <!--货币符号-->
          <el-table-column
            align="center"
            prop="symbol"
            :label="$t('internalization.currencySymbol')"
            min-width="150"
          />
          <!--汇率值-->
          <el-table-column
            align="center"
            prop="exchangeRate"
            :label="$t('internalization.exchangeRate')"
            min-width="120"
          />
          <!--状态-->
          <el-table-column
            align="center"
            prop="status"
            :label="$t('internalization.status')"
            min-width="100"
          >
            <template #default="scope">
              <span
                v-if="scope.row.status === 1"
                class="tag-text"
              >{{ $t('internalization.enable') }}</span>
              <span
                v-else
                class="tag-text"
              >{{ $t('internalization.disable') }}</span>
            </template>
          </el-table-column>
          <el-table-column
            fixed="right"
            align="center"
            :label="$t('publics.operating')"
            width="240"
          >
            <template #default="scope">
              <div class="text-btn-con">
                <div
                  v-if="isAuth('platform:currency:update')"
                  class="default-btn text-btn"
                  @click="handlerStatus(scope.row)"
                >
                  {{ scope.row.status ? $t('internalization.disableCurrency') : $t('internalization.enableCurrency') }}
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
    <!-- 弹窗, 新增 / 修改 -->
    <addOrUpdate
      v-if="addOrUpdateVisible"
      ref="addOrUpdateRef"
      @refresh-data-list="refreshChange"
    />
  </div>
</template>

<script setup>
import { Debounce } from '@/utils/debounce.js'
import addOrUpdate from './components/add-or-update.vue'
import { ElMessage } from 'element-plus'
import { isAuth } from '@/utils'

const searchForm = reactive({
  currencyCode: '',
  currencyName: ''
})
onMounted(() => {
  getDataList()
})

let tempSearchForm = null // 保存上次点击查询的请求条件
const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
const dataList = ref([])
/**
 * 获取数据列表
 * @param pageParam
 * @param newData
 */
const getDataList = (pageParam, newData = false) => {
  if (newData || !tempSearchForm) {
    tempSearchForm = JSON.parse(JSON.stringify(searchForm))
  }
  http({
    url: http.adornUrl('/platform/currency/page'),
    method: 'get',
    params: http.adornParams(Object.assign({
      current: pageParam == null ? page.currentPage : pageParam.currentPage,
      size: pageParam == null ? page.pageSize : pageParam.pageSize
    }, tempSearchForm))
  })
    .then(({ data }) => {
      if (!data) return
      page.currentPage = data.current
      dataList.value = data.records
      page.pageSize = data.size
      page.total = data.total
    })
}

const addOrUpdateVisible = ref(false)
const addOrUpdateRef = ref(null)
/**
 * 新增 / 修改
 * @param id
 */
const onAddOrUpdate = (id) => {
  addOrUpdateVisible.value = true
  nextTick(() => {
    addOrUpdateRef.value?.init(id)
  })
}
/**
 * 启用/禁用货币
 */
const handlerStatus = Debounce((row) => {
  if (!row) return
  const currencyForm = Object.assign({}, row)
  // 默认货币不支持禁用
  if (currencyForm.defaultCurrency === 1) {
    ElMessage({
      message: $t('internalization.defaulDisabledTip'),
      type: 'error',
      duration: 1500
    })
    return
  }
  currencyForm.status = currencyForm.status === 1 ? 0 : 1
  http({
    url: http.adornUrl('/platform/currency'),
    method: 'put',
    data: http.adornData(currencyForm)
  })
    .then(() => {
      ElMessage({
        message: $t('remindPop.succeeded'),
        type: 'success',
        duration: 1500
      })
      refreshChange()
    })
}, 1000)

/**
 * 点击查询
 * @param newData
 */
const onSearch = (newData = false) => {
  page.currentPage = 1
  getDataList(page, newData)
}

/**
 * 刷新回调用
 */
const refreshChange = () => {
  getDataList(page, false)
}

const hotSearchListTableRef = ref(null)

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

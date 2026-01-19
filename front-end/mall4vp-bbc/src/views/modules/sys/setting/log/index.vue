<template>
  <div class="mod-log">
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
            :label="$t('sys.userName') + ':'"
            class="search-form-item"
          >
            <el-input
              v-model="searchForm.username"
              type="text"
              clearable
              :placeholder="$t('sys.userName')"
            />
          </el-form-item>
          <el-form-item
            :label="$t('sys.userAction')"
            class="search-form-item"
          >
            <el-input
              v-model="searchForm.operation"
              type="text"
              clearable
              :placeholder="$t('sys.userAction')"
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
      <!-- 表格 -->
      <div class="table-con sys-log-table">
        <el-table
          ref="sysLogTableRef"
          :empty-text="$t('order.noData')"
          :data="dataList"
          header-cell-class-name="table-header"
          style="width: 100%"
        >
          <el-table-column
            fixed
            type="index"
            :label="$t('sysManagement.serialNumber')"
            :width="$t('language') !== 'zh_CN' ? '120' : '80'"
          />
          <!-- 用户名 -->
          <el-table-column
            min-width="120"
            prop="username"
            :label="$t('sys.userName')"
          >
            <template #default="scope">
              {{ scope.row.username }}
            </template>
          </el-table-column>
          <!-- 用户操作 -->
          <el-table-column
            min-width="120"
            prop="operation"
            :label="$t('sys.userAction')"
          >
            <template #default="scope">
              {{ scope.row.operation }}
            </template>
          </el-table-column>
          <!-- 请求方法 -->
          <el-table-column
            min-width="160"
            prop="method"
            :label="$t('sys.requestMerthod')"
          >
            <template #default="scope">
              {{ scope.row.method }}
            </template>
          </el-table-column>
          <!-- 请求参数 -->
          <el-table-column
            min-width="160"
            prop="params"
            :label="$t('sys.requestParameter')"
          >
            <template #default="scope">
              {{ scope.row.params }}
            </template>
          </el-table-column>
          <!-- 执行时长 -->
          <el-table-column
            min-width="220"
            prop="time"
            :label="$t('sys.executionTime')"
          >
            <template #default="scope">
              {{ scope.row.time }}
            </template>
          </el-table-column>
          <!-- IP地址 -->
          <el-table-column
            min-width="160"
            prop="ip"
            :label="$t('sys.ipAddress')"
          >
            <template #default="scope">
              {{ scope.row.ip }}
            </template>
          </el-table-column>
          <!-- 创建时间 -->
          <el-table-column
            min-width="160"
            prop="createDate"
            :label="$t('sys.creationTime')"
          >
            <template #default="scope">
              {{ scope.row.createDate }}
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
  </div>
</template>

<script setup>

let tempSearchForm = null // 保存上次点击查询的请求条件

const dataList = ref([])
const dataListLoading = ref(false)
const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
const searchForm = reactive({})
onMounted(() => {
  getDataList()
})

// 刷新回调
const refreshChange = () => {
  page.currentPage = 1
  getDataList(page)
}
// 获取数据列表
const getDataList = (pageParam, newData = false) => {
  dataListLoading.value = true
  if (newData || !tempSearchForm) {
    tempSearchForm = JSON.parse(JSON.stringify(searchForm))
  }
  http({
    url: http.adornUrl('/sys/log/page'),
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

defineExpose({
  refreshChange
})

</script>

<style lang="scss" scoped>

</style>

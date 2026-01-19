<template>
  <div class="page-shop-transport">
    <!-- 搜索栏 -->
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
            :label="$t('transport.name') +':'"
            class="search-form-item"
          >
            <el-input
              v-model="searchForm.transName"
              type="text"
              clearable
              :placeholder="$t('transport.name')"
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
              {{ $t('shop.resetMap') }}
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
          v-if="isAuth('platform:transport:save')"
          class="default-btn primary-btn"
          @click="onAddOrUpdate(0,1)"
        >
          {{ $t("crud.addTitle") }}
        </div>
        <div
          v-if="isAuth('platform:transport:delete')"
          :class="[dataListSelections.length <= 0 ? 'disabled-btn' : '', 'default-btn']"
          @click="onDelete()"
        >
          {{ $t('publics.batchDelete') }}
        </div>
      </div>

      <!-- 表格 -->
      <div class="table-con transport-table">
        <el-table
          ref="transportTableRef"
          :empty-text="$t('order.noData')"
          :data="dataList"
          header-cell-class-name="table-header"
          row-class-name="table-row-low"
          style="width: 100%"
          @selection-change="selectionChange"
        >
          <el-table-column
            type="selection"
            width="55"
          />
          <el-table-column
            align="left"
            prop="imgType"
            :label="$t('transport.name')"
          >
            <template #default="scope">
              <span class="table-cell-text">{{ scope.row.transName }}</span>
            </template>
          </el-table-column>

          <el-table-column
            align="left"
            prop="imgType"
            :label="$t('transport.chargePrice')"
          >
            <template #default="scope">
              <span>{{ [$t('transport.byCount'),$t('transport.byWeight'),$t('transport.byVolume')][scope.row.chargeType] }}</span>
            </template>
          </el-table-column>

          <el-table-column
            align="center"
            :label="$t('crud.menu')"
            width="150"
          >
            <template #default="scope">
              <div class="text-btn-con">
                <div
                  v-if="isAuth('platform:transport:update')"
                  class="default-btn text-btn"
                  @click="onAddOrUpdate(scope.row.transportId)"
                >
                  {{ $t('crud.editBtn') }}
                </div>
                <div
                  v-if="isAuth('platform:transport:delete')"
                  class="default-btn text-btn"
                  @click="onDelete(scope.row.transportId)"
                >
                  {{ $t('crud.delBtn') }}
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
      @close="addOrUpdateClose"
      @refresh-data-list="refreshChange"
    />
  </div>
</template>

<script setup>
import { isAuth } from '@/utils/index.js'
import { ElMessage, ElMessageBox } from 'element-plus'
import AddOrUpdate from '@/components/transport-add-or-update/index.vue'

onMounted(() => {
  getDataList()
})

const addOrUpdateVisible = ref(false)
const addOrUpdateClose = () => {
  addOrUpdateVisible.value = false
}

const searchForm = reactive({
  transName: ''
})
const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
const dataListLoading = ref(false)
const dataList = ref([])
let tempSearchForm = null // 保存上次点击查询的请求条件
/**
 * 获取数据列表
 * @param pageParam
 * @param newData
 */
const getDataList = (pageParam, newData = false) => {
  dataListLoading.value = true
  if (newData || !tempSearchForm) {
    tempSearchForm = JSON.parse(JSON.stringify(searchForm))
  }
  http({
    url: http.adornUrl('/platform/transport/page'),
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
  })
    .then(({ data }) => {
      dataList.value = data.records
      page.total = data.total
      dataListLoading.value = false
    })
}

const addOrUpdateRef = ref(null)
/**
 *  新增 / 修改
 * @param id
 */
const onAddOrUpdate = (id) => {
  addOrUpdateVisible.value = true
  nextTick(() => {
    addOrUpdateRef.value?.init(id)
  })
}

const selectAll = ref(false)
const transportTableRef = ref(null)
/**
 * 全选按钮
 */
const onSelectAll = () => {
  transportTableRef.value?.toggleAllSelection()
}

const dataListSelections = ref([])
/**
 * 删除
 * @param id
 */
const onDelete = (id) => {
  const ids = id ? [id] : dataListSelections.value.map(item => {
    return item.transportId
  })
  if (!id && !dataListSelections.value.length) {
    return
  }
  ElMessageBox.confirm(
    `${$t('sys.makeSure')}[${id ? $t('text.delBtn') : $t('sys.batchDelete')}]${$t('text.menu')}?`,
    $t('text.tips'),
    {
      confirmButtonText: $t('crud.filter.submitBtn'),
      cancelButtonText: $t('crud.filter.cancelBtn'),
      type: 'warning'
    }
  )
    .then(() => {
      http({
        url: http.adornUrl('/platform/transport'),
        method: 'delete',
        data: http.adornData(ids, false)
      })
        .then(() => {
          ElMessage({
            message: $t('publics.operation'),
            type: 'success',
            duration: 1500,
            onClose: () => {
              refreshChange()
            }
          })
        })
    })
}

/**
 * 条件查询
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
  getDataList(page)
}
/**
 * 多选变化
 * @param val
 */
const selectionChange = (val) => {
  dataListSelections.value = val
  selectAll.value = val.length === dataList.value.length
}

const searchFormRef = ref(null)
/**
 * 重置表单
 */
const onResetSearch = () => {
  searchFormRef.value?.resetFields()
  searchForm.transName = ''
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

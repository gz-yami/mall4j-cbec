<template>
  <div class="mod-hotSearcch">
    <div class="search-bar">
      <el-form
        ref="searchFormRef"
        :inline="true"
        :model="searchForm"
        @submit.prevent
      >
        <div class="input-row">
          <el-form-item
            prop="title"
            :label="$t('shop.hotTitle') + ':'"
          >
            <el-input
              v-model="searchForm.title"
              clearable
              :placeholder="$t('shop.hotTitle')"
            />
          </el-form-item>
          <el-form-item
            prop="content"
            :label="$t('shop.hotContent') + ':'"
          >
            <el-input
              v-model="searchForm.content"
              clearable
              :placeholder="$t('shop.hotContent')"
            />
          </el-form-item>
          <el-form-item
            prop="status"
            :label="$t('brand.status') + ':'"
          >
            <el-select
              v-model="searchForm.status"
              clearable
              :placeholder="$t('brand.status')"
            >
              <el-option
                :label="$t('shop.notEna')"
                :value="0"
              />
              <el-option
                :label="$t('shop.ena')"
                :value="1"
              />
            </el-select>
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
        <el-checkbox
          v-if="dataList.length"
          v-model="selectAll"
          class="all-check-btn"
          @change="onSelectAll"
        >
          {{ $t('publics.selectAll') }}
        </el-checkbox>
        <div
          v-if="isAuth('platform:hotSearch:save')"
          class="default-btn primary-btn"
          @click="onAddOrUpdate()"
        >
          {{ $t('crud.addTitle') }}
        </div>
        <div
          v-if="isAuth('platform:indexImg:delete')"
          :class="[dataListSelections.length <= 0 ? 'disabled-btn' : '', 'default-btn']"
          @click.stop="onDelete"
        >
          {{ $t('sys.batchDelete') }}
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
          @selection-change="onSelectSome"
        >
          <el-table-column
            v-if="dataList.length"
            type="selection"
            width="55"
          />
          <el-table-column
            align="left"
            prop="title"
            :label="$t('shop.hotTitle')"
            min-width="150"
          >
            <template #default="scope">
              <span class="table-cell-text">{{ scope.row.title }}</span>
            </template>
          </el-table-column>
          <el-table-column
            align="left"
            prop="content"
            :label="$t('shop.hotContent')"
            min-width="180"
          >
            <template #default="scope">
              <span class="table-cell-text">{{ scope.row.content }}</span>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="recDate"
            :label="$t('shop.recDate')"
            min-width="200"
            sortable
          />
          <el-table-column
            align="center"
            prop="seq"
            :label="$t('brand.sequence')"
            min-width="120"
            sortable
          />
          <el-table-column
            align="center"
            prop="status"
            :label="$t('brand.status')"
            width="auto"
          >
            <template #default="scope">
              <span
                v-if="scope.row.status === 0"
                class="tag-text"
              >{{ $t('groups.notEnabled') }}</span>
              <span
                v-else
                class="tag-text"
              >{{ $t('brand.enAble') }}</span>
            </template>
          </el-table-column>
          <el-table-column
            :label="$t('groups.hotSearchType')"
            prop="content"
            align="center"
            min-width="150"
          >
            <template #default="{row}">
              <span>{{ row.type === 1 ? $t('home.goods') : $t('formData.shop') }}</span>
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
                  v-if="isAuth('platform:hotSearch:update')"
                  class="default-btn text-btn"
                  @click="onAddOrUpdate(scope.row)"
                >
                  {{ $t('user.editBtn') }}
                </div>
                <div
                  v-if="isAuth('platform:hotSearch:delete')"
                  class="default-btn text-btn"
                  @click.stop="onDelete(scope.row)"
                >
                  {{ $t('user.deleteBtn') }}
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

onMounted(() => {
  getDataList()
})

let tempSearchForm = null // 保存上次点击查询的请求条件
const searchForm = reactive({
  content: '',
  status: '',
  title: ''
})
const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
const dataList = ref([])
// 获取数据列表
const getDataList = (pageParam, newData = false) => {
  if (newData || !tempSearchForm) {
    tempSearchForm = JSON.parse(JSON.stringify(searchForm))
  }
  http({
    url: http.adornUrl('/platform/hotSearch/page'),
    method: 'get',
    params: http.adornParams(Object.assign({
      current: pageParam == null ? page.currentPage : pageParam.currentPage,
      size: pageParam == null ? page.pageSize : pageParam.pageSize
    }, tempSearchForm))
  }).then(({ data }) => {
    page.total = data.total
    page.pageSize = data.size
    page.currentPage = data.current
    dataList.value = data.records
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

// 点击查询
const onSearch = (newData = false) => {
  page.currentPage = 1
  getDataList(page, newData)
}

const dataListSelections = ref([])
const selectAll = ref(false)
// 删除
const onDelete = (row) => {
  const ids = row.hotSearchId ? [row.hotSearchId] : dataListSelections.value.map(item => {
    return item.hotSearchId
  })
  if (!ids.length && !dataListSelections.value.length) {
    return
  }
  // `确定进行[${row.hotSearchId ? '删除' : '批量删除'}]操作?`
  let tip = $t('remindPop.makeSure') + '[' + (row.hotSearchId ? $t('remindPop.delete') : $t('remindPop.batchDeletion'))
  tip = tip + ']' + $t('remindPop.operation') + '?'
  ElMessageBox.confirm(tip, $t('remindPop.remind'), {
    confirmButtonText: $t('remindPop.confirm'),
    cancelButtonText: $t('remindPop.cancel'),
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl('/platform/hotSearch'),
      method: 'delete',
      data: http.adornData(ids, false)
    }).then(() => {
      if (!row.hotSearchId) {
        selectAll.value = false
      }
      if (dataList.value.length === 1) {
        page.currentPage = page.currentPage === 1 ? 1 : page.currentPage - 1
      }
      refreshChange()
      ElMessage({
        message: $t('publics.operation'),
        type: 'success',
        duration: 1500
      })
    })
  }).catch(() => { })
}

// 刷新回调用
const refreshChange = () => {
  getDataList(page)
}
// 多选变化
const onSelectSome = (val) => {
  dataListSelections.value = val
  selectAll.value = val.length === dataList.value.length
  if (!val.length) {
    selectAll.value = false
  }
}

const hotSearchListTableRef = ref(null)
/**
 * 全选按钮
 */
const onSelectAll = () => {
  selectAll.value = hotSearchListTableRef.value?.getSelectionRows().length < dataList.value.length
  hotSearchListTableRef.value?.toggleAllSelection()
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

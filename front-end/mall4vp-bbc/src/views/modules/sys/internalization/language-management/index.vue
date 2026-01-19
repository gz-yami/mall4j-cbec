<template>
  <div class="page-language-management">
    <div class="search-bar">
      <el-form
        ref="searchFormRef"
        :inline="true"
        :model="searchForm"
        @submit.prevent
      >
        <div class="input-row">
          <!--语言名称-->
          <el-form-item
            prop="name"
            :label="$t('internalization.langName') + ':'"
          >
            <el-input
              v-model="searchForm.name"
              clearable
              :placeholder="$t('internalization.langName')"
            />
          </el-form-item>
          <!--语言代码-->
          <el-form-item
            prop="language"
            :label="$t('internalization.langCode') + ':'"
          >
            <el-input
              v-model="searchForm.language"
              clearable
              :placeholder="$t('internalization.langCode')"
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
          v-if="isAuth('platform:languageManagement:save')"
          class="default-btn primary-btn"
          @click="onAddOrUpdate()"
        >
          {{ $t('crud.addTitle') }}
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
          <!--语言名称-->
          <el-table-column
            align="left"
            prop="name"
            :label="$t('internalization.langName')"
            min-width="150"
          >
            <template #default="scope">
              <span class="table-cell-text">{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <!--语言代码-->
          <el-table-column
            align="center"
            prop="language"
            :label="$t('internalization.langCode')"
            min-width="150"
          >
            <template #default="scope">
              <span class="table-cell-text">{{ scope.row.language }}</span>
            </template>
          </el-table-column>
          <!--语言编号-->
          <el-table-column
            align="center"
            prop="lang"
            :label="$t('internalization.langNumber')"
            width="160"
          />
          <!--默认语言-->
          <el-table-column
            align="center"
            prop="master"
            :label="$t('internalization.langdel')"
            width="200"
          >
            <template #default="scope">
              <span
                v-if="scope.row.master === 0"
                class="tag-text"
              >{{ $t('publics.no') }}</span>
              <span
                v-else
                class="tag-text"
              >{{ $t('publics.yes') }}</span>
            </template>
          </el-table-column>
          <!--是否启用-->
          <el-table-column
            align="center"
            prop="hide"
            :label="$t('internalization.langEnable')"
            width="120"
          >
            <template #default="scope">
              <span
                v-if="scope.row.hide === 0"
                class="tag-text"
              >{{ $t('publics.yes') }}</span>
              <span
                v-else
                class="tag-text"
              >{{ $t('publics.no') }}</span>
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
                  v-if="isAuth('platform:languageManagement:update')"
                  class="default-btn text-btn"
                  @click="onAddOrUpdate(scope.row.langId)"
                >
                  {{ $t('user.editBtn') }}
                </div>
                <div
                  v-if="isAuth('platform:languageManagement:delete')"
                  class="default-btn text-btn"
                  @click.stop="onDelete(scope.row,scope.index)"
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
// import { useLangStore } from '@/stores/lang.js'
import AddOrUpdate from './add-or-update.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { isAuth } from '@/utils'

const searchForm = reactive({
  language: '',
  name: ''
})
onMounted(() => {
  getDataList()
})

// const langStore = useLangStore()
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
 * @param isUpdateLang 更新语言缓存标识，默认不更新
 */
const getDataList = (pageParam, newData = false, isUpdateLang = false) => {
  if (newData || !tempSearchForm) {
    tempSearchForm = JSON.parse(JSON.stringify(searchForm))
  }
  http({
    url: http.adornUrl('/platform/lang/page'),
    method: 'get',
    params: http.adornParams(Object.assign({
      current: pageParam == null ? page.currentPage : pageParam.currentPage,
      size: pageParam == null ? page.pageSize : pageParam.pageSize
    }, tempSearchForm))
  })
    .then(({ data }) => {
      if (!data) return
      page.total = data.total
      page.pageSize = data.size
      page.currentPage = data.current
      dataList.value = data.records
      // if (isUpdateLang) langStore.updateLangSetUp()
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
 * 点击查询
 * @param newData
 */
const onSearch = (newData = false) => {
  page.currentPage = 1
  getDataList(page, newData)
}

const dataListSelections = ref([])
const selectAll = ref(false)
/**
 * 删除
 * @param row
 */
const onDelete = (row) => {
  const ids = row.langId ? [row.langId] : dataListSelections.value.map(item => {
    return item.langId
  })
  if (!ids.length && !dataListSelections.value.length) {
    return
  }
  // `确定进行[${row.langId ? '删除' : '批量删除'}]操作?`
  let tip = $t('remindPop.makeSure') + '[' + (row.langId ? $t('remindPop.delete') : $t('remindPop.batchDeletion'))
  tip = tip + ']' + $t('remindPop.operation') + '?'
  ElMessageBox.confirm(tip, $t('remindPop.remind'), {
    confirmButtonText: $t('remindPop.confirm'),
    cancelButtonText: $t('remindPop.cancel'),
    type: 'warning'
  })
    .then(() => {
      http({
        url: http.adornUrl('/platform/lang/' + ids),
        method: 'delete'
      })
        .then(() => {
          ElMessage({
            message: $t('publics.operation'),
            type: 'success',
            duration: 1500,
            onClose: () => {
              if (!row.langId) {
                selectAll.value = false
              }
              if (dataList.value.length === 1) {
                page.currentPage = page.currentPage === 1 ? 1 : page.currentPage - 1
              }
              refreshChange()
            }
          })
        })
    })
    .catch(() => { })
}

/**
 * 刷新回调用
 */
const refreshChange = () => {
  getDataList(page, false, true)
}

/**
 * 多选变化
 */
const onSelectSome = (val) => {
  dataListSelections.value = val
  selectAll.value = val.length === dataList.value.length
  if (!val.length) {
    selectAll.value = false
  }
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

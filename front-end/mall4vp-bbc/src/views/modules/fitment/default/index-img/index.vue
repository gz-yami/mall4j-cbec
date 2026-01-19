<template>
  <div class="mod-prod-swiper">
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
            prop="status"
            :label="$t('publics.status') + ':'"
            class="search-form-item"
          >
            <el-select
              v-model="searchForm.status"
              :placeholder="$t('publics.status')"
              clearable
            >
              <el-option
                :label="$t('publics.disable')"
                :value="0"
              />
              <el-option
                :label="$t('publics.normal')"
                :value="1"
              />
            </el-select>
          </el-form-item>
          <el-form-item
            prop="imgType"
            :label="$t('platform.platform') + ':'"
            class="search-form-item"
          >
            <el-select
              v-model="searchForm.imgType"
              :placeholder="$t('platform.platform')"
              clearable
            >
              <el-option
                :label="$t('platform.mobile')"
                :value="0"
              />
              <el-option
                :label="$t('platform.PCTerminal')"
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
          v-if="isAuth('platform:indexImg:save')"
          class="default-btn primary-btn"
          @click.stop="onAddOrUpdate()"
        >
          {{ $t('crud.addTitle') }}
          <el-tooltip
            class="item"
            effect="dark"
            :content="$t('publics.maxIndexImgNumOfPlatform')"
            placement="top"
          >
            <el-icon
              style="top: 1px"
            >
              <WarningFilled />
            </el-icon>
          </el-tooltip>
        </div>
        <div
          v-if="isAuth('platform:indexImg:delete')"
          :class="[dataListSelections.length <= 0 ? 'disabled-btn' : '', 'default-btn']"
          @click="onDelete()"
        >
          {{ $t('sys.batchDelete') }}
        </div>
      </div>
      <div class="table-con banner-table">
        <el-table
          ref="bannerListTableRef"
          :empty-text="$t('order.noData')"
          :data="dataList"
          header-cell-class-name="table-header"
          row-class-name="table-row"
          style="width: 100%"
          @selection-change="onSelectSome"
        >
          <el-table-column
            v-if="dataList.length"
            type="selection"
            width="55"
          />
          <el-table-column
            prop="imgUrl"
            :label="$t('platform.carouselPicture')"
            width="440"
          >
            <template #default="scope">
              <img
                :src="checkFileUrl(scope.row.imgUrl)"
                height="100"
                alt
                @error="handlePicError"
              >
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="seq"
            :label="$t('brand.sequence')"
            min-width="100"
          />
          <el-table-column
            align="center"
            prop="status"
            :label="$t('product.status')"
            width="auto"
          >
            <template #default="scope">
              <span class="tag-text">{{ [$t('brand.disable'), $t('brand.normal')][scope.row.status] }}</span>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            prop="imgType"
            :label="$t('platform.platform')"
            min-width="100"
          >
            <template #default="scope">
              <span class="tag-text">{{ scope.row.imgType === 0 ? $t('platform.mobile') : $t('platform.PCTerminal') }}</span>
            </template>
          </el-table-column>
          <el-table-column
            fixed="right"
            align="center"
            :label="$t('publics.operating')"
            width="250"
          >
            <template #default="scope">
              <div class="text-btn-con">
                <div
                  v-if="isAuth('platform:indexImg:update')"
                  class="default-btn text-btn"
                  @click="onAddOrUpdate(scope.row.imgId)"
                >
                  {{ $t('user.editBtn') }}
                </div>
                <div
                  v-if="isAuth('platform:indexImg:delete')"
                  class="default-btn text-btn"
                  @click="onDelete(scope.row.imgId)"
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
import defImg from '@/assets/img/def.png'
import AddOrUpdate from './add-or-update.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { isAuth } from '@/utils/index.js'

const searchForm = reactive({
  status: '',
  imgType: ''
})
onMounted(() => {
  getDataList()
})

const handlePicError = (event) => {
  event.target.src = defImg
}

let tempSearchForm = {} // 保存上次点击查询的请求条件
const dataList = ref([])
// 修改
const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
// 获取数据列表
const getDataList = (pageParam, newData = false) => {
  if (page) {
    const size = Math.ceil(page.total / page.pageSize)
    page.currentPage = (page.currentPage > size ? size : page.currentPage) || 1
  }
  if (newData || !tempSearchForm) {
    tempSearchForm = JSON.parse(JSON.stringify(searchForm))
  }

  http({
    url: http.adornUrl('/platform/indexImg/page'),
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

const dataListSelections = ref([])
// 删除
const onDelete = (id) => {
  const ids = id ? [id] : dataListSelections.value.map(item => {
    return item.imgId
  })
  if (!id && !dataListSelections.value.length) {
    return
  }
  let tip = $t('remindPop.makeSure') + '[' + (id ? $t('remindPop.delete') : $t('remindPop.batchDeletion'))
  tip = tip + ']' + $t('remindPop.operation') + '?'
  ElMessageBox.confirm(tip, $t('remindPop.remind'), {
    confirmButtonText: $t('remindPop.confirm'),
    cancelButtonText: $t('remindPop.cancel'),
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl('/platform/indexImg'),
      method: 'delete',
      data: http.adornData(ids, false)
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
  })
}

// 条件查询
const onSearch = (newData = false) => {
  page.currentPage = 1
  getDataList(page, newData)
}

// 刷新回调
const refreshChange = () => {
  getDataList(page)
}

const selectAll = ref(false)
// 多选变化
const onSelectSome = (val) => {
  dataListSelections.value = val
  selectAll.value = val.length === dataList.value.length
}

const bannerListTableRef = ref(null)
/**
 * 全选按钮
 */
const onSelectAll = () => {
  selectAll.value = bannerListTableRef.value?.getSelectionRows().length < dataList.value.length
  bannerListTableRef.value?.toggleAllSelection()
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
:deep(.el-table__fixed-right) {
  height: 98% !important
}
:deep(.el-table__fixed-right::before) {
  background-color: transparent;
}
:deep(.el-table::before) {
  z-index: 6
}
</style>

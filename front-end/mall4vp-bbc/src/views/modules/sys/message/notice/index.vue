<template>
  <div class="mod-shop-notice">
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
            prop="title"
            :label="$t('shop.announcementTitle') + ':'"
            class="search-form-item"
          >
            <el-input
              v-model="searchForm.title"
              type="text"
              clearable
              :placeholder="$t('shop.announcementTitle')"
            />
          </el-form-item>
          <el-form-item
            prop="status"
            :label="$t('brand.status') + ':'"
            class="search-form-item"
          >
            <el-select
              v-model="searchForm.status"
              clearable
              :placeholder="$t('brand.status')"
            >
              <el-option
                :label="$t('publics.cancel')"
                :value="0"
              />
              <el-option
                :label="$t('publics.publicar')"
                :value="1"
              />
            </el-select>
          </el-form-item>
          <el-form-item
            prop="isTop"
            :label="$t('publics.isTop') + ':'"
            class="search-form-item"
          >
            <el-select
              v-model="searchForm.isTop"
              clearable
              :placeholder="$t('publics.isTop')"
            >
              <el-option
                :label="$t('publics.no')"
                :value="0"
              />
              <el-option
                :label="$t('publics.yes')"
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
              @click="onResetSearch(searchFormRef)"
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
          v-if="isAuth('platform:notice:save')"
          class="default-btn primary-btn"
          @click="onAddOrUpdate()"
        >
          {{ $t('sysManagement.add') }}
        </div>
      </div>
      <div class="table-con notice-table">
        <el-table
          ref="noticeListTableRef"
          :data="dataList"
          header-cell-class-name="table-header"
          row-class-name="table-row-low"
          style="width: 100%"
        >
          <el-table-column
            fixed
            type="index"
            :width="$t('language') !== 'zh_CN' ? '120' : '80'"
            :label="$t('brand.serialNumber')"
          />

          <el-table-column
            fixed
            align="left"
            prop="title"
            :label="$t('shop.announcementTitle')"
            width="480"
          >
            <template #default="scope">
              <span class="table-cell-text">{{ scope.row.title }}</span>
            </template>
          </el-table-column>

          <el-table-column
            prop="status"
            :label="$t('publics.status')"
            width="auto"
          >
            <template #default="scope">
              <span
                v-if="scope.row.status === 1"
                class="tag-text"
              >{{ $t('publics.publicar') }}</span>
              <span
                v-if="scope.row.status === 0"
                class="tag-text"
              >{{ $t('publics.cancel') }}</span>
            </template>
          </el-table-column>

          <el-table-column
            prop="isTop"
            :label="$t('publics.isTop')"
            width="auto"
          >
            <template #default="scope">
              <span
                v-if="scope.row.isTop === 0"
                class="tag-text"
              >{{ $t('publics.no') }}</span>
              <span
                v-else-if="scope.row.isTop === 1"
                class="tag-text"
              >{{ $t('publics.yes') }}</span>
            </template>
          </el-table-column>

          <el-table-column
            prop="isTop"
            :label="$t('publics.releaseTime')"
            width="150"
          >
            <template #default="scope">
              <span
                class="tag-text"
              >{{ scope.row.publishTime }}</span>
            </template>
          </el-table-column>

          <el-table-column
            fixed="right"
            align="center"
            :label="$t('publics.operating')"
            width="230"
          >
            <template #default="scope">
              <div class="text-btn-con">
                <div
                  v-if="isAuth('platform:notice:update')"
                  class="default-btn text-btn"
                  @click="onAddOrUpdate(scope.row.id)"
                >
                  {{ $t('user.editBtn') }}
                </div>
                <div
                  v-if="isAuth('platform:notice:delete')"
                  class="default-btn text-btn"
                  @click.stop="onDelete(scope.row.id)"
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
      @on-close="addOrUpdateVisible = false"
    />
  </div>
</template>

<script setup>
import AddOrUpdate from './add-or-update.vue'
import { isAuth } from '@/utils/index.js'
import { ElMessage, ElMessageBox } from 'element-plus'

let tempSearchForm = null // 保存上次点击查询的请求条件

const dataList = ref([])
const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
const searchForm = reactive({
  title: '',
  isTop: '',
  status: '',
  type: ''
})
const dataListLoading = ref(false)
const addOrUpdateVisible = ref(false)
const addOrUpdateRef = ref(null)

onMounted(() => {
  getDataList()
})
const getDataList = (pageParam, newData = false) => {
  if (newData || !tempSearchForm) {
    tempSearchForm = JSON.parse(JSON.stringify(searchForm))
  }
  dataListLoading.value = true
  http({
    url: http.adornUrl('/platform/notice/page'),
    method: 'get',
    params: http.adornParams(Object.assign({
      current: pageParam == null ? page.currentPage : pageParam.currentPage,
      size: pageParam == null ? page.pageSize : pageParam.pageSize
    }, tempSearchForm))
  }).then(({ data }) => {
    dataList.value = data.records
    dataList.value.forEach(item => {
      const lang = localStorage.getItem('cbecB2cLang') || 'en'
      if (lang === 'zh_CN') {
        const arr = item.types.split(',').map(el => ['', '', '用户端'][el])
        item.exhibition = arr.length ? `在${arr.join('/')}展示` : '-'
      } else {
        const arr = item.types.split(',').map(el => ['', 'shop side', 'user side', 'supplier side'][el])
        item.exhibition = arr.length ? `Show on the ${arr.join('/')}` : '-'
      }
    })
    page.total = data.total
    dataListLoading.value = false
  })
}
// 新增 / 修改
const onAddOrUpdate = (id) => {
  addOrUpdateVisible.value = true
  nextTick(() => {
    addOrUpdateRef.value?.init(id)
  })
}
const onDelete = (id) => {
  ElMessageBox.confirm($t('seckill.isDeleOper'), $t('remindPop.remind'), {
    confirmButtonText: $t('remindPop.confirm'),
    cancelButtonText: $t('remindPop.cancel'),
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl('/platform/notice/' + id),
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
    })
  }).catch(() => { })
}
// 刷新回调用
const refreshChange = () => {
  getDataList(page)
}
const onSearch = (newData = false) => {
  page.currentPage = 1
  getDataList(page, newData)
}

const searchFormRef = ref()
/**
 * 重置表单
 * @param {String} formName 表单名称
 */
const onResetSearch = formName => {
  formName.resetFields()
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
</style>

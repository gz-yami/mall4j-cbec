<template>
  <div class="mod-user">
    <!-- 搜索栏 -->
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
            :label="$t('sys.userName') + ':'"
            class="search-form-item"
            prop="username"
          >
            <el-input
              v-model="searchForm.username"
              type="text"
              clearable
              :placeholder="$t('sys.userName')"
            />
          </el-form-item>
          <el-form-item
            :label="$t('sys.nickName') + ':'"
            class="search-form-item"
            prop="nickName"
          >
            <el-input
              v-model="searchForm.nickName"
              type="text"
              clearable
              :placeholder="$t('sys.nickName')"
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
              @click="onResetSearch()"
            >
              {{ $t('product.reset') }}
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
        <!-- <span v-if="dataListSelections.length" class="had-selected">{{$t('dataAnalysis.selected')}} {{dataListSelections.length}}</span> -->
        <div
          v-if="isAuth('sys:user:save')"
          class="default-btn primary-btn"
          @click.stop="onAddOrUpdate()"
        >
          {{ $t("sysManagement.add") }}
        </div>
        <div
          v-if="isAuth('sys:user:delete')"
          :class="[dataListSelections.length <= 0 ? 'disabled-btn' : '', 'default-btn']"
          :disabled="dataListSelections.length <= 0"
          @click="onDelete()"
        >
          {{ $t('sys.batchDelete') }}
        </div>
      </div>

      <!-- 表格 -->
      <div class="table-con sys-user-table">
        <el-table
          ref="sysUserTableRef"
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
          <!-- 用户名 -->
          <el-table-column
            min-width="120"
            prop="username"
            :label="$t('sys.userName')"
          >
            <template #default="scope">
              <span>{{ scope.row.username }}</span>
              <span
                v-if="scope.row.userId === 1"
                class="super-admin"
              >{{ $t('sys.superAdmin') }}</span>
            </template>
          </el-table-column>
          <!-- 用户昵称 -->
          <el-table-column
            min-width="140"
            prop="nickName"
            :label="$t('sys.nickName')"
          >
            <template #default="scope">
              {{ scope.row.nickName }}
            </template>
          </el-table-column>
          <!-- 邮箱 -->
          <el-table-column
            min-width="140"
            prop="email"
            :label="$t('sys.email')"
          >
            <template #default="scope">
              {{ scope.row.email }}
            </template>
          </el-table-column>
          <!-- 联系电话 -->
          <el-table-column
            min-width="140"
            prop="mobile"
            :label="$t('publics.phoneNumber')"
          >
            <template #default="scope">
              {{ scope.row.mobile }}
            </template>
          </el-table-column>
          <!-- 创建时间 -->
          <el-table-column
            min-width="160"
            prop="createTime"
            :label="$t('sys.creationTime')"
          >
            <template #default="scope">
              {{ scope.row.createTime }}
            </template>
          </el-table-column>
          <!-- 状态 -->
          <el-table-column
            min-width="100"
            prop="status"
            :label="$t('product.status')"
          >
            <template #default="scope">
              <div v-if="scope.row.status === 0">
                {{ $t('publics.disable') }}
              </div>
              <div v-if="scope.row.status === 1">
                {{ $t('publics.normal') }}
              </div>
            </template>
          </el-table-column>
          <!-- 操作 -->
          <el-table-column
            fixed="right"
            align="center"
            :label="$t('publics.operating')"
            width="150"
          >
            <template #default="scope">
              <div class="text-btn-con">
                <div
                  v-if="isAuth('sys:user:update')"
                  class="default-btn text-btn"
                  @click.stop="onAddOrUpdate(scope.row.userId)"
                >
                  {{ $t('coupon.edit') }}
                </div>
                <div
                  v-if="isAuth('sys:user:delete') && scope.row.userId !== 1"
                  class="default-btn text-btn"
                  @click.stop="onDelete(scope.row.userId)"
                >
                  {{ $t('coupon.delete') }}
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
      @refresh-data-list="refreshChange"
    />
  </div>
</template>

<script setup>
import AddOrUpdate from './add-or-update.vue'
import { isAuth } from '@/utils/index.js'
import { ElMessage, ElMessageBox } from 'element-plus'

let tempSearchForm = null // 保存上次点击查询的请求条件
const searchFormRef = ref(null)
const dataList = ref([])
const sysUserTableRef = ref(null)
const dataListLoading = ref(false)
const dataListSelections = ref([])
const addOrUpdateVisible = ref(false)
const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
const searchForm = reactive({
  userName: null,
  nickName: null
})
const selectAll = ref(false)
onMounted(() => {
  getDataList()
})

// 获取数据列表
const getDataList = (pageParam, newData = false) => {
  if (newData || !tempSearchForm) {
    tempSearchForm = JSON.parse(JSON.stringify(searchForm))
  }
  dataListLoading.value = true
  http({
    url: http.adornUrl('/sys/user/page'),
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
// 刷新回调用
const refreshChange = () => {
  page.currentPage = 1
  getDataList(page)
}
// 多选变化
const selectionChange = (val) => {
  dataListSelections.value = val
  selectAll.value = val.length === dataList.value.length
}
/**
 * 全选按钮
 */
const onSelectAll = () => {
  selectAll.value = sysUserTableRef.value.selection?.length < dataList.value?.length
  sysUserTableRef.value?.toggleAllSelection()
}
const addOrUpdateRef = ref(null)
// 新增 / 修改
const onAddOrUpdate = (id) => {
  addOrUpdateVisible.value = true
  nextTick(() => {
    addOrUpdateRef.value?.init(id)
  })
}
// 删除
const onDelete = (id) => {
  const userIds = id ? [id] : dataListSelections.value.map(item => {
    return item.userId
  })
  if (!id && !dataListSelections.value.length) {
    return
  }
  ElMessageBox.confirm(`${$t('sysManagement.confirmSure')}${id ? $t('coupon.delete') : $t('sys.batchDelete')}${$t('sysManagement.operation')}`, $t('text.tips'), {
    confirmButtonText: $t('coupon.confirm'),
    cancelButtonText: $t('coupon.cancel'),
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl('/sys/user'),
      method: 'delete',
      data: http.adornData(userIds, false)
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

/**
 * 重置表单
 */
const onResetSearch = () => {
  searchFormRef.value.resetFields()
}

const onPageSizeChange = (val) => {
  page.pageSize = val
  getDataList(page)
}
const onPageChange = (val) => {
  page.currentPage = val
  getDataList(page)
}

</script>

<style lang="scss" scoped>
.mod-user {
  .super-admin {
    color: #e43130;
  }
}
</style>

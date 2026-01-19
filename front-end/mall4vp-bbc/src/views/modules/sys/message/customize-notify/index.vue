<template>
  <div class="mod-service-notifyTemplate">
    <div class="main-container">
      <div class="operation-bar">
        <div
          v-if="isAuth('platform:notifyTemplate:save')"
          class="default-btn primary-btn"
          @click="onAddOrUpdate()"
        >
          {{ $t('crud.addTitle') }}
        </div>
      </div>
    </div>
  </div>
  <div class="table-con notice-table">
    <el-table
      ref="customizeNotifyTableRef"
      :empty-text="$t('order.noData')"
      :data="dataList"
      header-cell-class-name="table-header"
      row-class-name="table-row-low"
      style="width: 100%"
    >
      <el-table-column
        :label="$t('formData.serialNumber')"
        type="index"
        width="80"
      />
      <el-table-column
        :label="$t('platform.notifyConten')"
        prop="message"
      />
      <el-table-column
        :label="$t('platform.canUseNotice')"
        prop="templateTypes"
      >
        <template #default="scope">
          <span
            v-if="scope.row.sub "
          >{{ $t('shop.officialAccount') }}</span>
          <span
            v-if="scope.row.sms "
          >{{ $t('sysManagement.shortMessage') }}</span>
          <span
            v-if="scope.row.app "
          >{{ $t('platform.stationMsg') }}</span>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('platform.tagList')"
        prop="templateCode"
      />
      <el-table-column
        :label="$t('sys.creationTime')"
        prop="createTime"
      />
      <el-table-column
        align="center"
        :label="$t('publics.operating')"
        fixed="right"
        width="320"
      >
        <template #default="scope">
          <div class="text-btn-con">
            <div
              v-if="isAuth('platform:notifyTemplate:update')"
              class="default-btn text-btn"
              @click="onAddOrUpdate(scope.row.templateId)"
            >
              {{ $t('crud.updateBtn') }}
            </div>
            <div
              v-if="isAuth('platform:notifyTemplate:update')"
              class="default-btn text-btn"
              @click="sendTagMsg(scope.row.templateId)"
            >
              {{ $t('platform.sendMsg') }}
            </div>
            <div
              class="default-btn text-btn"
              @click="onDelete(scope.row.templateId)"
            >
              {{ $t('crud.delBtn') }}
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
  <div>
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

const dataList = ref([])
const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
let searchForm = reactive({}) // 搜索
const dataListLoading = ref(false)
const addOrUpdateVisible = ref(false)
const addOrUpdateRef = ref(null)

onMounted(() => {
  getDataList()
})
const getDataList = (pageParam) => {
  dataListLoading.value = true
  http({
    url: http.adornUrl('/platform/sendTagNotify/page'),
    method: 'get',
    params: http.adornParams(
      Object.assign(
        {
          current: pageParam == null ? page.currentPage : pageParam.currentPage,
          size: pageParam == null ? page.pageSize : pageParam.pageSize,
          sendType: 0
        },
        searchForm
      )
    )
  }).then(({ data }) => {
    dataList.value = data.records
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
  let tip = $t('remindPop.makeSure') + '[' + $t('remindPop.delete')
  tip = tip + ']' + $t('remindPop.operation') + '?'
  ElMessageBox.confirm(tip, $t('remindPop.remind'), {
    confirmButtonText: $t('remindPop.confirm'),
    cancelButtonText: $t('remindPop.cancel'),
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl('/platform/sendTagNotify/' + id),
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
const sendTagMsg = (id) => {
  // `确定进行[${row.status === 0 ? '启用' : '禁用'}]操作?`
  const tip = $t('sys.pushTagMsg')
  ElMessageBox.confirm(tip, $t('remindPop.remind'), {
    confirmButtonText: $t('remindPop.confirm'),
    cancelButtonText: $t('remindPop.cancel'),
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl('/user/userTag/sendMsg/' + id),
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
/**
 * 刷新回调
 */
const refreshChange = () => {
  getDataList(page)
}
const onSearch = (params) => {
  searchForm = params
  getDataList(page)
}
const onPageSizeChange = (val) => {
  page.pageSize = val
  getDataList()
}
const onPageChange = (val) => {
  page.currentPage = val
  getDataList()
}

defineExpose({
  onSearch
})

</script>
<style lang="scss" scoped>
.notice-table {
  margin: 20px auto 30px;
}
</style>

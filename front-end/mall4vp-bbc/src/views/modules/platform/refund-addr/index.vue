<template>
  <div class="page-refund-addr">
    <div class="main-container">
      <div class="operation-bar">
        <div
          v-if="isAuth('shop:refundAddr:save')"
          class="default-btn primary-btn"
          @click="onAddOrUpdate()"
        >
          {{ $t('crud.addBtn') }}
        </div>
      </div>
      <div class="table-con">
        <el-table
          :data="dataList"
          header-cell-class-name="table-header"
          row-class-name="table-row-low"
          style="width: 100%"
        >
          <el-table-column
            :label="$t('publics.addressee')"
            prop="receiverName"
          >
            <template #default="scope">
              <span class="table-cell-text">
                {{ scope.row.receiverName }}
              </span>
            </template>
          </el-table-column>
          <el-table-column
            align="left"
            prop="receiverMobile"
            :label="$t('publics.mobilePhone')"
          />
          <el-table-column
            align="left"
            prop="receiverTelephone"
            :label="$t('address.receiverTelephone')"
          >
            <template #default="scope">
              <span class="table-cell-text">
                {{ scope.row.receiverTelephone || '-' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column
            align="left"
            prop="postCode"
            :label="$t('address.postalCode')"
          >
            <template #default="scope">
              <span class="table-cell-text">
                {{ scope.row.postCode || '-' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column
            align="left"
            prop="province"
            :label="$t('shop.nations')"
          />
          <el-table-column
            align="left"
            prop="city"
            :label="$t('address.province')"
          />
          <el-table-column
            align="left"
            prop="area"
            :label="$t('address.city')"
          />
          <el-table-column
            align="left"
            prop="addr"
            :label="$t('address.detailed')"
          >
            <template #default="scope">
              <span class="table-cell-text">
                {{ scope.row.addr }}
              </span>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            :label="$t('crud.menu')"
            width="160"
          >
            <template #default="scope">
              <div class="text-btn-con">
                <div
                  v-if="isAuth('shop:refundAddr:update')"
                  class="default-btn text-btn"
                  @click="onAddOrUpdate(scope.row.refundAddrId)"
                >
                  {{ $t('text.updateBtn') }}
                </div>
                <div
                  v-if="isAuth('shop:refundAddr:delete')"
                  class="default-btn text-btn"
                  @click="onDelete(scope.row.refundAddrId)"
                >
                  {{ $t('text.delBtn') }}
                </div>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-pagination
        :current-page="page.currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="page.pageSize"
        :total="page.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="onPageSizeChange"
        @current-change="onPageChange"
      />
    </div>
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdateRef"
      @refresh-data-list="onResetSearch"
    />
  </div>
</template>

<script setup>
import { ElMessageBox, ElMessage } from 'element-plus'
import AddOrUpdate from './add-or-update.vue'
import { isAuth } from '@/utils/index.js'

onMounted(() => {
  onGetDataList(page)
})

const dataList = ref([])
const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
const searchForm = reactive({})
const onGetDataList = (pageParam) => {
  http({
    url: http.adornUrl('/shop/refundAddr/page'),
    method: 'get',
    params: http.adornParams(Object.assign({
      current: pageParam.currentPage,
      size: pageParam.pageSize
    }, searchForm))
  }).then(({ data }) => {
    dataList.value = data.records
    page.total = data.total
  })
}
// 新增 / 修改
const addOrUpdateVisible = ref(false)
const addOrUpdateRef = ref(null)
const onAddOrUpdate = (id) => {
  addOrUpdateVisible.value = true
  nextTick(() => {
    addOrUpdateRef.value?.init(id)
  })
}
const onDelete = (id) => {
  ElMessageBox.confirm($t('admin.isDeleOper') + '?', $t('text.tips'), {
    confirmButtonText: $t('crud.filter.submitBtn'),
    cancelButtonText: $t('crud.filter.cancelBtn'),
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl('/shop/refundAddr/' + id),
      method: 'delete',
      data: http.adornData({})
    }).then(() => {
      ElMessage({
        message: $t('publics.operation'),
        type: 'success',
        duration: 1500,
        onClose: () => {
          onResetSearch()
        }
      })
    })
  }).catch(() => { })
}
// 刷新回调用
const onResetSearch = () => {
  page.currentPage = 1
  onGetDataList(page)
}
const onPageSizeChange = (val) => {
  page.pageSize = val
  onGetDataList(page)
}
const onPageChange = (val) => {
  page.currentPage = val
  onGetDataList(page)
}

</script>
<style lang="scss" scoped>

.page-refund-addr {
  .main-container {
    margin: 0;
    padding: 0;
  }
}
</style>

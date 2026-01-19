<template>
  <div class="page-category">
    <el-form @submit.prevent>
      <el-form-item>
        <el-alert
          :title="$t('publics.alertMessage1')"
          type="warning"
          show-icon
          :closable="false"
        />
      </el-form-item>
    </el-form>
    <el-form
      :inline="true"
      :model="dataForm"
      @submit.prevent
    >
      <el-form-item>
        <div
          v-if="isAuth('prod:category:save')"
          class="default-btn primary-btn"
          @click="addOrUpdateHandle()"
        >
          {{ $t("brand.add") }}
        </div>
      </el-form-item>
      <el-form-item>
        <div
          v-if="isAuth('prod:category:export')"
          class="default-btn primary-btn"
          @click="exportCategory()"
        >
          {{ $t("order.export") }}
        </div>
      </el-form-item>
    </el-form>
    <el-table
      ref="tableRef"
      :empty-text="$t('order.noData')"
      :data="dataList"
      row-key="categoryId"
      style="width: 100%;"
      header-cell-class-name="table-header"
      row-class-name="table-row"
    >
      <el-table-column
        prop="categoryName"
        tree-key="categoryId"
        min-width="120"
        :label="$t('publics.category')"
      />
      <el-table-column
        prop="pic"
        width="400px"
        :label="$t('publics.image')"
        align="center"
      >
        <template #default="scope">
          <ImgShow
            v-if="scope.row.grade !== 1"
            :src="scope.row.pic"
            :img-style="{width:scope.row.grade===0?'':'100px',height:'100px'}"
          />
        </template>
      </el-table-column>
      <el-table-column
        prop="status"
        width="110px"
        :label="$t('publics.status')"
      >
        <template #default="scope">
          <span
            v-if="scope.row.status === 0"
            class="tag-text"
          >{{ $t("brand.offline") }}</span>
          <span
            v-else
            class="tag-text"
          >{{ $t("brand.normal") }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="seq"
        width="140px"
        align="center"
        :label="$t('hotSearch.seq')"
      />
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="200px"
        :label="$t('crud.menu')"
      >
        <template #default="scope">
          <div class="text-btn-con">
            <div
              v-if="isAuth('prod:category:update')"
              class="default-btn text-btn"
              @click="addOrUpdateHandle(scope.row.categoryId)"
            >
              {{ $t("brand.revise") }}
            </div>
            <div
              v-if="isAuth('prod:category:delete')"
              class="default-btn text-btn"
              @click="deleteHandle(scope.row.categoryId)"
            >
              {{ $t("brand.delete") }}
            </div>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdateRef"
      @refresh-data-list="getDataList"
    />
  </div>
</template>

<script setup>
import { isAuth } from '@/utils'
import addOrUpdate from './components/add-or-update.vue'

import { ElLoading, ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive } from 'vue'

const Data = reactive({
  dataForm: {},
  dataList: [],
  dataListLoading: false,
  addOrUpdateVisible: false
})

const { dataForm, dataList, addOrUpdateVisible } = toRefs(Data)

onMounted(() => {
  getDataList()
})

const tableRef = ref()
// 获取数据列表
const getDataList = () => {
  Data.dataListLoading = true
  http({
    url: http.adornUrl('/prod/category/listCategory'),
    method: 'get',
    params: http.adornParams({
      grade: 2
    })
  }).then(({ data }) => {
    Data.dataList = treeDataTranslate(data, 'categoryId', 'parentId', false)
    nextTick(() => {
      tableRef.value.doLayout() // 解决表格错位
    })
    Data.dataListLoading = false
  })
}

const addOrUpdateRef = ref()
// 新增 / 修改
const addOrUpdateHandle = (id) => {
  Data.addOrUpdateVisible = true
  nextTick(() => {
    addOrUpdateRef.value.init(id)
  })
}

// 导出
const exportCategory = () => {
  // let params = {}
  const loading = ElLoading.service({
    lock: true,
    target: '.mod-category',
    customClass: 'export-load',
    background: 'transparent',
    text: $t('shop.exportIng')
  })
  http({
    url: http.adornUrl('/prod/category/export'),
    method: 'get',
    responseType: 'blob'
  }).then(({ data }) => {
    loading.close()
    const blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8' })
    const fileName = $t('category.exportXls')
    const elink = document.createElement('a')
    if ('download' in elink) {
      elink.download = fileName
      elink.style.display = 'none'
      elink.href = URL.createObjectURL(blob)
      document.body.appendChild(elink)
      elink.click()
    } else {
      navigator.msSaveBlob(blob, fileName)
    }
    ElMessage({
      message: $t('shop.exportSuccess'),
      type: 'success',
      duration: 1500
    })
  }).catch((e) => {
    loading.close()
  })
}

// 删除
const deleteHandle = (id) => {
  ElMessageBox.confirm($t('remindPop.makeSure') + ' ' + `[${$t('remindPop.delete')}]` + ' ' + $t('remindPop.operation') + '？', $t('remindPop.remind'), {
    confirmButtonText: $t('remindPop.confirm'),
    cancelButtonText: $t('remindPop.cancel'),
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl(`/prod/category/${id}`),
      method: 'delete',
      data: http.adornData()
    }).then(() => {
      ElMessage({
        message: $t('remindPop.success'),
        type: 'success',
        duration: 1500,
        onClose: () => {
          getDataList()
        }
      })
    })
  })
}

</script>

<style lang="scss" scoped>
.page-category{
  :deep(.el-form-item__content){
    line-height: 24px;
  }
  :deep(.el-alert__content) {
    padding: 6px 8px;
  }
  :deep(.export-load) {
    top: -50% !important;
    left: -10% !important;
  }
}
</style>

<template>
  <div class="mod-prod">
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
            prop="propName"
            :label="$t('product.attributeName')"
            class="search-form-item"
          >
            <el-input
              v-model="searchForm.propName"
              type="text"
              clearable
              :placeholder="$t('product.attributeName')"
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
              @click="onResetSearch"
            >
              {{ $t('shop.resetMap') }}
            </div>
          </el-form-item>
        </div>
      </el-form>
    </div>
    <div class="main-container">
      <div class="operation-bar">
        <div
          v-if="isAuth('prod:spec:save')"
          class="default-btn primary-btn"
          @click="onAddOrUpdate()"
        >
          {{ $t("crud.addTitle") }}
        </div>
      </div>
      <div class="table-con spec-table">
        <el-table
          ref="specListTableRef"
          :empty-text="$t('order.noData')"
          :data="dataList"
          header-cell-class-name="table-header"
          row-class-name="table-row-low"
          style="width: 100%"
        >
          <el-table-column
            type="index"
            align="left"
            :label="$t('retailProd.number')"
            width="200"
          />
          <el-table-column
            align="left"
            prop="propName"
            :label="$t('product.attributeName')"
          >
            <template #default="scope">
              <div class="table-cell-text">
                {{ scope.row.propName }}
              </div>
            </template>
          </el-table-column>

          <el-table-column
            align="left"
            prop="prodPropValues"
            :label="$t('product.attributeValue')"
          >
            <template #default="scope">
              <div class="table-cell-text">
                {{ scope.row.propValueList.join('/') }}
              </div>
            </template>
          </el-table-column>

          <el-table-column
            align="center"
            fixed="right"
            width="200"
            :label="$t('publics.operating')"
          >
            <template #default="scope">
              <div class="text-btn-con">
                <div
                  v-if="isAuth('prod:spec:update')"
                  class="default-btn text-btn"
                  @click.stop="onAddOrUpdate(scope.row)"
                >
                  {{ $t('groups.edit') }}
                </div>
                <div
                  v-if="isAuth('prod:spec:delete')"
                  class="default-btn text-btn"
                  @click.stop="onDelete(scope.row.propId)"
                >
                  {{ $t('text.delBtn') }}
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
import { isAuth } from '@/utils/index.js'
import { ElMessage, ElMessageBox } from 'element-plus'
import AddOrUpdate from './add-or-update.vue'

const dataListSelections = ref([])

const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})

onMounted(() => {
  getDataList()
})

// 获取数据列表
const searchForm = reactive({
  propName: ''
})
let tempSearchForm = null // 保存上次点击查询的请求条件
const dataList = ref([])
const getDataList = (pageParam, newData = false) => {
  if (page) {
    const size = Math.ceil(page.total / page.pageSize)
    page.currentPage = (page.currentPage > size ? size : page.currentPage) || 1
  }
  if (newData || !tempSearchForm) {
    tempSearchForm = JSON.parse(JSON.stringify(searchForm))
  }
  http({
    url: http.adornUrl('/prod/spec/page'),
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
    for (let i = 0; i < data.records.length; i++) {
      const item = data.records[i]
      item.propValueList = []
      item.propValueEnList = []
      for (let j = 0; j < item.prodPropValues.length; j++) {
        const element = item.prodPropValues[j]
        item.propValueList.push(element.propValue)
        item.propValueEnList.push(element.propValueEn)
      }
    }
    dataList.value = data.records
    page.total = data.total
  })
}
// 新增 / 修改
const addOrUpdateRef = ref(null)
const addOrUpdateVisible = ref(false)
const onAddOrUpdate = (val) => {
  addOrUpdateVisible.value = true
  nextTick(() => {
    addOrUpdateRef.value?.init(val)
  })
}
// 删除
const onDelete = (id) => {
  const ids = id ? [id] : dataListSelections.value.map(item => {
    return item.propId
  })
  ElMessageBox.confirm(`${$t('sys.makeSure')}[${id ? $t('text.delBtn') : $t('sys.batchDelete')}]${$t('text.menu')}?`, $t('text.tips'), {
    confirmButtonText: $t('crud.filter.submitBtn'),
    cancelButtonText: $t('crud.filter.cancelBtn'),
    type: 'warning'
  })
    .then(() => {
      http({
        url: http.adornUrl(`/prod/spec/${ids}`),
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
    }).catch(() => { })
}
// 刷新回调用
const refreshChange = () => {
  getDataList(page)
}
const onSearch = (newData = false) => {
  page.currentPage = 1
  page.pageSize = 10
  getDataList(page, newData)
}

// 重置表单
const searchFormRef = ref(null)
const onResetSearch = () => {
  searchFormRef.value.resetFields()
  getDataList()
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

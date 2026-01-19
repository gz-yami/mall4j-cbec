<template>
  <div class="select-account-range">
    <el-form-item
      label=""
      prop="type"
    >
      <div
        class="default-btn"
        @click="dialogVisible = true"
      >
        {{ $t('weixin.select') }}{{ name }}
      </div>
    </el-form-item>
    <el-form-item
      v-if="tableData && tableData.length > 0"
      label=""
      prop="type"
    >
      <el-table
        :empty-text="$t('order.noData')"
        :data="tableData"
        header-cell-class-name="table-header"
        row-class-name="table-row-low"
      >
        <el-table-column
          :prop="propList[0]"
          :label="`${name}${$t('publics.name')}`"
        />
        <el-table-column
          :prop="propList[1]"
          :label="$t('shopProcess.email')"
        >
          <template #default="scope">
            {{ scope.row[propList[1]] ? scope.row[propList[1]] : '-' }}
          </template>
        </el-table-column>
        <el-table-column
          header-align="center"
          align="center"
          :label="$t('crud.menu')"
        >
          <template #default="scope">
            <div class="text-btn-con">
              <div
                class="default-btn text-btn"
                @click="onDelete(scope.row)"
              >
                {{ $t("brand.delete") }}
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-form-item>
    <el-dialog
      v-model="dialogVisible"
      :title="`${$t('weixin.select')}${name}`"
      append-to-body
    >
      <el-form
        :inline="true"
        :model="searchParam"
        class="demo-form-inline"
        @submit.prevent
      >
        <el-form-item :label="`${name}${$t('publics.name')}`">
          <el-input
            v-model="searchParam[propList[0]]"
            :placeholder="`${name}${$t('publics.name')}`"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <div
            class="default-btn primary-btn"
            @click="searchInfo()"
          >
            {{ $t('product.search') }}
          </div>
          <div
            class="default-btn"
            @click="clearSearchInfo()"
          >
            {{ $t('product.reset') }}
          </div>
        </el-form-item>
      </el-form>
      <el-table
        ref="multipleTableRef"
        v-loading="pageLoading"
        :data="pageVO.records"
        header-cell-class-name="table-header"
        row-class-name="table-row-low"
        :row-key="getRowKeys"
        @selection-change="onSelectSome"
      >
        <el-table-column
          v-if="props.singleElection === 1"
          width="50"
          header-align="center"
          align="center"
        >
          <template #default="scope">
            <div>
              <el-radio
                v-model="singleShopId"
                :label="scope.row.shopId"
                @change="getSelectShopRow(scope.$index,scope.row)"
              >
                &nbsp;
              </el-radio>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          v-if="props.singleElection === 0"
          type="selection"
          :reserve-selection="true"
          width="55"
        />
        <el-table-column
          :prop="propList[0]"
          :label="`${name}${$t('publics.name')}`"
        />
        <el-table-column
          :prop="propList[1]"
          :label="$t('shopProcess.email')"
        >
          <template #default="scope">
            {{ scope.row[propList[1]] ? scope.row[propList[1]] : '-' }}
          </template>
        </el-table-column>
        <el-table-column
          :prop="propList[2]"
          :label="$t('publics.status')"
        >
          <template #default="scope">
            <span v-if="scope.row[propList[2]] === -1">{{ $t('shop.deleted') }}</span>
            <span v-else-if="scope.row[propList[2]] === 0">{{ type == 2 ? $t('publics.disable') : $t('shop.stop') }}</span>
            <span v-else-if="scope.row[propList[2]] === 1">{{ type == 2 ? $t('publics.normal') : $t('shop.inOperation') }}</span>
            <span v-else-if="scope.row[propList[2]] === 4">{{ $t('distributionMsg.applying') }}</span>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页条 -->
      <el-pagination
        v-if="pageVO.total>0"
        :current-page="searchParam.current"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="searchParam.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pageVO.total"
        style="padding-top: 18px;"
        @size-change="onPageSizeChange"
        @current-change="onPageChange"
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button
            type="primary"
            @click="onSubmit"
          >确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
const emit = defineEmits(['getIds'])
const props = defineProps({
  type: {
    type: Number,
    default: 0
  },
  singleElection: {
    type: Number,
    default: 0
  },
  shopId: {
    type: Number,
    default: 0
  },
  tableList: {
    type: Array,
    default: () => []
  }
})

const tableData = ref([])
const singleShopId = ref(null)
const dialogVisible = ref(false)
const multipleSelection = ref([])
const searchParam = reactive({
  size: 10,
  current: 1
})
// 返回参数
const pageVO = reactive({
  records: [], // 返回的列表
  total: 0, // 一共多少条数据
  pages: 0 // 一共多少页
})
// loading
const pageLoading = ref(true)
const name = computed(() => {
  return props.type === 1 ? $t('publics.business') : $t('publics.user')
})

const propList = computed(() => {
  const shopProp = ['shopName', 'mail', 'shopStatus']
  const userProp = ['nickName', 'userMail', 'status']
  return props.type === 1 ? shopProp : userProp
})
const api = computed(() => {
  return props.type === 1 ? '/shop/shopAuditing/page' : '/admin/user/userPage'
})

const idKey = computed(() => {
  return props.type === 1 ? 'shopId' : 'userId'
})
const multipleTableRef = ref(null)
watch(() => dialogVisible, (data) => {
  if (data) {
    nextTick(() => {
      multipleTableRef.value?.clearSelection()
      multipleSelection.value = tableData.value.map(item => item[idKey.value])
      tableData.value.forEach(row => {
        const index = pageVO.records.findIndex(item => item[idKey.value] === row[idKey.value])
        if (index >= 0) {
          multipleTableRef.value.toggleRowSelection(pageVO.records[index], true)
        }
      })
    })
  }
})
onMounted(() => {
  getPage()
  tableData.value = props.tableList
  singleShopId.value = props.shopId
})

const getPage = () => {
  pageLoading.value = true
  if (props.type === 1) {
    searchParam.shopStatusList = [0, 1, 2, 3]
  }
  http({
    url: http.adornUrl(api.value),
    method: 'get',
    params: http.adornParams(searchParam)
  }).then(({ data }) => {
    pageVO.records = data.records
    pageVO.total = data.total
    pageVO.pages = data.pages
    pageLoading.value = false
  })
}
const searchInfo = () => {
  searchParam.current = 1
  getPage()
}
const onSelectSome = (val) => {
  multipleSelection.value = val
}
const onSubmit = () => {
  dialogVisible.value = false
  tableData.value = multipleSelection.value
  if (props.singleElection === 1) {
    emit('getIds', singleShopId.value)
    return
  }
  const ids = tableData.value.map(item => props.type === 1 ? item.shopId : item.userId)
  emit('getIds', ids)
}
// 清空搜索内容
const clearSearchInfo = () => {
  searchParam[propList.value[0]] = null
  searchParam.current = 1
  getPage()
}
const onPageSizeChange = (val) => {
  searchParam.size = val
  getPage()
}
const onPageChange = (val) => {
  searchParam.current = val
  getPage()
}
// 记录选中状态
const getRowKeys = (row) => {
  return row[idKey.value]
}
const onDelete = (row) => {
  tableData.value = tableData.value.filter(item => item[idKey.value] !== row[idKey.value])
  multipleSelection.value = tableData.value
  const ids = tableData.value.map(item => item[idKey.value])
  emit('getIds', ids)
}
/**
 * 获取当选的行
 * @param index
 * @param row
 */
// eslint-disable-next-line no-unused-vars
const getSelectShopRow = (index, row) => {
  tableData.value = []
  tableData.value.push(row)
  singleShopId.value = row.shopId
}
</script>

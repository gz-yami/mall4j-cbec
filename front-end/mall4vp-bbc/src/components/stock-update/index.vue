<template>
  <div class="component-stock-upload">
    <el-dialog
      v-model="visible"
      :show-close="false"
      width="650"
      @closed="onCloseDialog"
    >
      <div class="title">
        <el-tabs
          v-model="activeName"
          class="tabs"
          @tab-change="onActiveChange"
        >
          <el-tab-pane
            :label="$t('stock.warehouseInventory')"
            name="warehouse"
          />
          <el-tab-pane
            v-if="!isSpuMold && (!isNeedVerifyDelivery || deliveryMode.hasUserPickUp) && !isHideStation"
            :label="$t('stock.storeInventory')"
            name="station"
          />
        </el-tabs>
        <div
          class="close-btn"
          @click="visible = false"
        >
          <el-icon><Close /></el-icon>
        </div>
      </div>
      <div class="content">
        <el-table
          :key="activeName"
          v-loading="pageLoading"
          :data="dataList"
          header-cell-class-name="table-header"
          row-class-name="table-row-low"
          style="width: 100%"
        >
          <el-table-column
            :prop="activeName==='warehouse'?'warehouseName':'stationName'"
            :label="activeName==='warehouse'?$t('stock.warehouseName'):$t('stock.stationNames')"
          />
          <el-table-column
            v-if="activeName==='station'"
            prop="status"
            label="状态"
          >
            <template #default="scope">
              <div v-show="!pageLoading">
                <div v-if="scope.row.status === 4">
                  {{ $t("station.auditFailure") }}
                </div>
                <div v-else-if="scope.row.status === 3">
                  {{ $t("station.underReview") }}
                </div>
                <div v-else-if="scope.row.status === 2">
                  {{ $t("components.platformOffline") }}
                </div>
                <div v-else-if="scope.row.status === 1">
                  {{ $t("station.business") }}
                </div>
                <div v-else>
                  {{ $t("station.close") }}
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="stock"
            :label="$t('stock.merchandiseInventory')"
          >
            <template #default="scope">
              <el-input
                v-model.number="scope.row.stock"
                :min="0"
                maxlength="7"
                class="stock-number"
                :disabled="isView"
                @input="onStockInput(scope.row)"
                @change="onPushStockPointList(scope.row)"
              />
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- 分页 -->
      <el-pagination
        v-show="pageData.total > 0"
        style="text-align: right;padding-right: 20px;"
        :total="pageData.total"
        :current-page="searchForm.current"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="searchForm.size"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="onSizeChangeHandle"
        @current-change="onCurrentChangeHandle"
      />
      <template #footer>
        <div class="dialog-footer">
          <el-button
            @click="visible = false"
          >
            {{ $t('crud.filter.cancelBtn') }}
          </el-button>
          <el-button
            type="primary"
            @click="onSubmit()"
          >
            {{ $t('crud.filter.submitBtn') }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
const props = defineProps({
  isView: {
    default: false,
    type: Boolean
  },
  // 是否需要校验配送方式（未选择自提的商品不显示门店列表）
  isNeedVerifyDelivery: {
    default: false,
    type: Boolean
  },
  // 配送方式
  deliveryMode: {
    type: Object,
    default: () => {
      return {}
    }
  },
  isHideStation: {
    default: false,
    type: Boolean
  },
  // 是否要查询所有仓库，包含已删除
  isShowAll: {
    default: false,
    type: Boolean
  }
})

const emit = defineEmits(['setStockCallback', 'close'])

let stockPointList = [] // 库存点及库存信息列表（提交时使用）
let index = null // 修改的索引
const visible = ref(false)
const activeName = ref('warehouse')
const isSpuMold = ref(false)
/**
 * 初始化
 * @param {object} sku sku信息
 * @param {number} indexValue 索引值（回调中返回给父组件修改数据用,-1则表示无）
 * @param {boolean} mold 1虚拟商品 0实物商品
 */
const init = (sku, indexValue = -1, mold = 0) => {
  activeName.value = 'warehouse'
  searchForm.current = 1
  searchForm.size = 10
  visible.value = true
  index = indexValue
  isSpuMold.value = mold
  stockPointList = sku.stockPointList ? JSON.parse(JSON.stringify(sku.stockPointList)) : []
  onGetPageData(true)
}

let tempSearchForm = null // 保存上次点击查询的请求条件
// 头部搜索表单
const searchForm = reactive({
  size: 10,
  current: 1,
  stockMode: 2
})
const pageData = ref({
  total: 0,
  pages: 0
})
const dataList = ref([])
const pageLoading = ref(true)
let typeIdStr = '' // 当前请求的字段键名
// 获取数据列表
const onGetPageData = (newData = false) => {
  if (newData || !tempSearchForm) {
    tempSearchForm = JSON.parse(JSON.stringify(searchForm))
  } else {
    tempSearchForm.current = searchForm.current
    tempSearchForm.size = searchForm.size
  }
  tempSearchForm.showAll = props.isView && props.isShowAll ? 1 : undefined
  let url
  if (activeName.value === 'warehouse') {
    url = '/m/warehouse/page'
    typeIdStr = 'warehouseId'
  } else {
    typeIdStr = 'stationId'
    url = '/admin/station/page'
  }
  http({
    url: http.adornUrl(url),
    method: 'get',
    params: http.adornParams(tempSearchForm)
  }).then(({ data }) => {
    pageData.value.pages = data.pages
    pageData.value.total = data.total
    dataList.value = cidyArray(data.records, stockPointList)
    if (pageLoading.value) {
      pageLoading.value = false
    }
  }).catch(() => {
    pageLoading.value = false
  })
}

/**
 * 整理回显库存数量
 * @param {Array} pointList 请求来的仓库/门店列表（id为[typeIdStr]字段）
 * @param {Array} stockList 存储点库存数列表（id为'stockPointType'字段）
 */
const cidyArray = (pointList, stockList) => {
  // 如果未有存储点设置库存，则直接返回整理的数据
  const nowStockPointType = activeName.value === 'warehouse' ? 1 : 2
  if (!(stockList && stockList.length)) {
    return pointList.map((item) => {
      return {
        ...item,
        stock: 0, // 库存
        stockPointId: item[typeIdStr], // 存储点ID
        stockPointType: nowStockPointType, // 存储点类型（当前请求的列表为仓库则类型为1，请求的列表为门店则类型为2）
        type: activeName.value === 'warehouse' ? item.type : 1, // 仓库需要返回仓库类型（0 默认仓库 1 非默认仓库）
        stockMode: activeName.value === 'station' ? item.stockMode : undefined
      }
    })
  }
  const list = []
  for (let i = 0; i < pointList.length; i++) {
    let flag = false
    for (let j = 0; j < stockList.length; j++) {
      if (pointList[i][typeIdStr] === stockList[j].stockPointId && stockList[j].stockPointType === nowStockPointType) {
        list.push({
          ...pointList[i],
          stock: stockList[j].stock, // 库存
          stockPointId: pointList[i][typeIdStr], // 存储点ID
          stockPointType: nowStockPointType, // 存储点类型（当前请求的列表为仓库则类型为1，请求的列表为门店则类型为2）
          type: activeName.value === 'warehouse' ? pointList[i].type : 1, // 仓库需要返回仓库类型（0 默认仓库 1 非默认仓库）
          stockMode: activeName.value === 'station' ? pointList[i].stockMode : undefined,
          skuId: stockList[j].skuId
        })
        flag = true
        break
      }
    }
    // 当前循环的仓库/门店未设置库存
    if (!flag) {
      list.push({
        ...pointList[i],
        stock: 0, // 库存
        stockPointId: pointList[i][typeIdStr], // 存储点ID
        stockPointType: nowStockPointType, // 存储点类型（当前请求的列表为仓库则类型为1，请求的列表为门店则类型为2）
        type: activeName.value === 'warehouse' ? pointList[i].type : 1, // 仓库需要返回仓库类型（0 默认仓库 1 非默认仓库）
        stockMode: activeName.value === 'station' ? pointList[i].stockMode : undefined
      })
    }
  }
  return list
}
// 将设置的库存数存储起来
const onPushStockPointList = (row) => {
  const nowStockPointType = activeName.value === 'warehouse' ? 1 : 2
  // if (!row.stock && row.type === 1) {
  //   // 设置库存为0则将记录剔除
  //   row.stock = 0
  //   if (stockPointList && stockPointList.length) {
  //     for (let i = 0; i < stockPointList.length; i++) {
  //       // 判断当前修改的库存是否存在原库存
  //       if (stockPointList[i].stockPointId === row[typeIdStr] && stockPointList[i].stockPointType === nowStockPointType) {
  //         stockPointList.splice(i, 1)
  //         return
  //       }
  //     }
  //   }
  //   return
  // }
  if (!(stockPointList && stockPointList.length)) {
    stockPointList.push({
      stock: row.stock, // 库存
      stockPointId: row.stockPointId, // 存储点ID
      stockPointType: row.stockPointType, // 存储点类型（当前请求的列表为仓库则类型为1，请求的列表为门店则类型为2）
      type: activeName.value === 'warehouse' ? row.type : 1, // 仓库需要返回仓库类型（0 默认仓库 1 非默认仓库）
      stockMode: activeName.value === 'station' ? row.stockMode : undefined
    })
    return
  }
  let flag = false
  for (let i = 0; i < stockPointList.length; i++) {
    if (stockPointList[i].stockPointId === row[typeIdStr] && stockPointList[i].stockPointType === nowStockPointType) {
      // 当前修改的库存存在原库存，则直接修改
      if (row.skuId) {
        // 记录库存的改变
        if (stockPointList[i].changeStock) {
          // 如果存在库存改变（非首次修改），则计算 原库存（stockPointList[i].stock） 与 修改库存（row.stock） 的差值再与记录的changeStock相加
          const diff = row.stock - (stockPointList[i].stock || 0)
          stockPointList[i].changeStock += diff
        } else {
          // 如果不存在库存改变（首次修改）且oldStock不为空，则修改的库存为 原库存（stockPointList[i].stock） 与 修改库存（row.stock） 的差值
          stockPointList[i].changeStock = row.stock - (stockPointList[i].stock || 0)
        }
      } else if (!row.stock) {
        // 不存在库存的新增库存点则移除
        stockPointList.splice(i, 1)
        break
      }
      stockPointList[i].stock = row.stock || 0
      flag = true
      break
    }
  }
  if (!flag && row.stock > 0) {
    // 当前修改的库存未有记录，则新建一条
    stockPointList.push({
      stock: row.stock,
      stockPointId: row.stockPointId,
      stockPointType: row.stockPointType,
      type: activeName.value === 'warehouse' ? row.type : 1, // 仓库需要返回仓库类型（0 默认仓库 1 非默认仓库）
      stockMode: activeName.value === 'station' ? row.stockMode : undefined
    })
  }
}

const onStockInput = (row) => {
  if (row.stock) {
    row.stock = row.stock.toString().replace(/[^\d]/g, '')
    row.stock = row.stock > 9999999 ? 9999999 : Number(row.stock)
  }
}

// 每页数
const onSizeChangeHandle = (val) => {
  searchForm.size = val
  searchForm.current = 1
  onGetPageData()
}

// 当前页
const onCurrentChangeHandle = (val) => {
  searchForm.current = val
  onGetPageData()
}

const onActiveChange = () => {
  searchForm.current = 1
  pageLoading.value = true
  onGetPageData(true)
}

const onSubmit = () => {
  if (props.isView) {
    // 查看时点击确认直接关闭页面
    visible.value = false
    emit('close')
  }
  const sum = stockPointList.reduce((total, item) => {
    return total + Number(item.stock)
  }, 0)
  visible.value = false
  emit('setStockCallback', stockPointList, sum, index)
}

const onCloseDialog = () => {
  emit('close')
}

defineExpose({
  init,
  visible
})
</script>

<style lang="scss" scoped>
.component-stock-upload {
  :deep(.el-dialog__header){
    display: none;
  }
  :deep(.el-dialog__body) {
    padding: 0 0 20px;
  }
  .title {
    position: relative;
    :deep(.el-tabs__nav-scroll) {
      padding: 10px 0 0 20px;
    }
    .close-btn {
      font-size: 18px;
      color: #909399;
      position: absolute;
      top: 10px;
      right: 10px;
      &:hover {
        color: #155bd4;
        cursor: pointer;
      }
    }
  }
  .content {
    padding: 20px;
    .stock-number {
      width: 100px;
    }
  }
  .dialog-footer {
    text-align: center;
  }
}
</style>

<template>
  <el-dialog
    v-model="dialogTableVisible"
    :title="$t('groups.relatedProducts')"
    width="65%"
    class="component-association-goods"
    :close-on-click-modal="false"
  >
    <el-button
      type="primary"
      class="filter-item"
      @click="onSelectGoods"
    >
      {{ $t('popupAd.selectProd') }}
    </el-button>
    <div
      v-if="!comData.isCommonWarehouse"
      class="warning-tip"
    >
      <el-icon><WarningFilled /></el-icon> {{ $t('prod.stockpilesTips') }}
    </div>
    <el-table
      :empty-text="$t('order.noData')"
      :data="comData.list"
      header-row-class-name="header-row"
      style="margin-top: 10px"
    >
      <el-table-column
        width="160"
        fixed="left"
        property="prodName"
        :label="$t('product.prodName')"
        align="center"
      >
        <template #default="scope">
          <div class="prod-name">
            {{ scope.row.prodName }}
          </div>
        </template>
      </el-table-column>
      <el-table-column
        min-width="170"
        property="skuName"
        :label="$t('product.productSpecifi')"
        align="center"
      >
        <template #default="scope">
          {{ scope.row.skuName || '-' }}
        </template>
      </el-table-column>
      <el-table-column
        min-width="190"
        property="comboCount"
        :label="$t('product.quantityInCom')"
        align="center"
      >
        <template #default="scope">
          <el-input-number
            v-model="scope.row.comboCount"
            :min="1"
            :max="9999999"
            controls-position="right"
            :step="1"
            step-strictly
            :value-on-clear="1"
            @change="getTotalStock"
          />
        </template>
      </el-table-column>
      <el-table-column
        min-width="180"
        property="price"
        :label="$t('prod.OriginalSalesPrice') + '(' + useCurrencyStore().defMark + ')'"
        align="center"
      >
        <template #default="scope">
          {{ scope.row.price?.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column
        min-width="140"
        property="stock"
        :label="$t('product.totalStocks')"
        align="center"
      >
        <template #default="scope">
          {{ scope.row.stock || 0 }}
        </template>
      </el-table-column>
      <el-table-column
        width="120"
        fixed="right"
        :label="$t('shopFeature.list.oper')"
      >
        <template #default="scope">
          <el-button
            link
            type="primary"
            size="small"
            @click="onDelete(scope)"
          >
            {{ $t('crud.delBtn') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="total-price">
      {{ $t('prod.stockpilesTotal') }}: <span class="price">{{ totalStock }}</span>
    </div>

    <!--搭配商品表格-->
    <select-goods
      v-if="addProdVisible"
      ref="skuSelectRef"
      :chosen-check-items="comData.list"
      :limit="10"
      :status="1"
      :sku-status="1"
      :can-deposit="0"
      @refresh-select-prods="selectProdItem"
    />

    <template #footer>
      <span class="dialog-footer">
        <el-button
          @click="dialogTableVisible = false"
        >
          {{ $t('crud.cancelBtn') }}
        </el-button>
        <el-button
          type="primary"
          @click="onSubmit"
        >
          {{ $t('crud.filter.submitBtn') }}
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import SelectGoods from './components/select-goods/index.vue'

const emit = defineEmits(['onSelectSuccess'])

const dialogTableVisible = ref(false)
const isEdit = ref(false)

/**
 * 打开关联商品弹窗
 * @param data 已经关联的商品数据
 * @param isUpdate 是否为修改
 */
const openDialog = (data = [], isUpdate = false) => {
  isEdit.value = isUpdate
  resetData()
  comData.list = JSON.parse(JSON.stringify(data))
  getTotalStock()
  dialogTableVisible.value = true
}

const comData = reactive({
  list: [],
  totalStock: 0,
  isCommonWarehouse: true
})

const totalStock = ref(0)
const getTotalStock = () => {
  const skuComboList = comData.list.map(item => {
    return {
      skuId: item.comboSkuId,
      comboCount: item.comboCount || 1
    }
  })
  if (skuComboList.length > 0) {
    http({
      url: http.adornUrl('/sku/calculateStockBySkuIds'),
      method: 'post',
      data: http.adornData({
        skuComboList
      })
    }).then(({ data }) => {
      totalStock.value = data.stocks || 0
      comData.isCommonWarehouse = data.hasCommonStock
    })
  } else {
    totalStock.value = 0
    comData.isCommonWarehouse = true
  }
}

const skuSelectRef = ref(null)
const addProdVisible = ref(false)
/**
 * 选择需要关联的商品
 */
const onSelectGoods = () => {
  addProdVisible.value = true
  nextTick(() => {
    skuSelectRef.value?.init()
  })
}

/**
 * 选择完成回调
 */
const selectProdItem = (list) => {
  const arr = []
  // 获取回调数据，展开sku
  if (list.length > 0) {
    for (const item of list) {
      const obj = {
        prodName: item.prodName,
        comboCount: 1,
        comboSkuId: item.comboSkuId ? item.comboSkuId : item.skuId,
        comboProdId: item.comboProdId ? item.comboProdId : item.prodId,
        skuName: item.skuName || '-',
        price: item.price,
        stock: item.stock || item.stocks,
        skuId: null,
        prodId: null
      }
      arr.push(obj)
    }
  }
  comData.list = arr
  getTotalStock()
}

const onDelete = (scope) => {
  const { $index } = scope
  comData.list.splice($index, 1)
  getTotalStock()
}

const onSubmit = () => {
  dialogTableVisible.value = false
  comData.list.forEach((item) => {
    if (!item.comboCount || item.comboCount < 1) item.comboCount = 1
  })
  comData.totalStock = totalStock.value
  const data = JSON.parse(JSON.stringify(comData))
  emit('onSelectSuccess', data)
}

// 初始化数据
const resetData = () => {
  comData.list = []
  comData.totalStock = 0
}

defineExpose({
  openDialog
})

</script>

<style scoped lang="scss">
// 定义表头样式
:deep(.header-row) {
  .el-table__cell {
    background-color: #F7F8FA;
    height: 57px;
    font-weight: bold;
  }
  th.el-table-fixed-column--left,
  th.el-table-fixed-column--right {
    background-color: #F7F8FA;
  }
}

.total-price {
  margin: 20px 0 0 20px;

  &:deep(.price) {
    text-decoration: underline;
    margin-right: 30px;
  }
}

.warning-tip {
  width: 100%;
  height: 30px;
  line-height: 30px;
  background-color: #fdf6ec;
  border-radius: 3px;
  padding-left: 10px;
  margin: 5px 0;
  color: #e6a23c;
  display: flex;
  align-items: center;
}

.prod-name {
  flex: 1;
  font-size: 14px;
  color: #333333;
  word-break: break-all;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

</style>

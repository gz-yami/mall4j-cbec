<template>
  <div class="mod-prod-sku-table">
    <el-form-item
      :label="$t('product.priceAndInventory')"
      :label-width="$t('language') !== 'zh_CN' ? 'auto' : '130px'"
    >
      <div class="sku-table-con">
        <!--批量设置-->
        <div
          v-if="lists.length > 1"
          class="batch-settings-box"
        >
          <div class="set-txt">
            <span>{{
              $t("groups.batchSettings")
            }}</span>
            <span class="weak-txt">{{ $t("product.postProductTips14") }}</span>
          </div>
          <div
            class="batch-settings-tb"
          >
            <el-form
              :inline="true"
              class="demo-form-inline"
              @submit.prevent
            >
              <div class="batch-settings-con">
                <div
                  v-for="(item, i) in batchList"
                  :key="i"
                  class="item"
                >
                  <el-select
                    v-model="item.value"
                    class="bat-set-item"
                    placeholder="请选择"
                  >
                    <el-option
                      :key="-1"
                      :label="$t('home.all')"
                      :value="-1"
                    />
                    <el-option
                      v-for="(el,index) in item.tagItems"
                      :key="index"
                      :label="el.propValue"
                      :value="el.propValue"
                    />
                  </el-select>
                </div>

                <div class="item">
                  <input
                    v-model.number="dataFrom.price"
                    :placeholder="$t('product.sellingPrice') + '(' + useCurrencyStore().defMark + ')'"
                    type="number"
                    :max="100000000"
                    :min="0.01"
                    :step="0.01"
                    class="tag-input-width"
                    style="min-width: 105px;"
                    @blur="
                      handleInputValue(
                        dataFrom.price,
                        null,
                        'price',
                        0.01,
                        100000000
                      )
                    "
                  >
                </div>
                <div class="item">
                  <div class="stock-number">
                    <input
                      v-model.number="dataFrom.stocks"
                      :placeholder="$t('coupon.stock')"
                      type="number"
                      :disabled="mold === 2"
                      :max="9999999"
                      :min="0"
                      :step="1"
                      style="min-width: 70px;"
                      class="tag-input-width"
                      @keyup=" dataFrom.stocks = String(dataFrom.stocks).match( /[^0-9]{1,7}/ ) ? 0 : dataFrom.stocks "
                      @blur=" handleInputValue( dataFrom.stocks, null, 'stocks', 0, 9999999 ) "
                    >
                  </div>
                </div>
                <div class="item">
                  <input
                    v-model.number="dataFrom.oriPrice"
                    :placeholder="$t('product.marketPrice') + '(' + useCurrencyStore().defMark + ')'"
                    type="number"
                    :max="100000000"
                    :min="0"
                    :step="0.01"
                    style="min-width: 125px;"
                    class="tag-input-width"
                    @blur="
                      handleInputValue(
                        dataFrom.oriPrice,
                        null,
                        'oriPrice',
                        0,
                        100000000
                      )
                    "
                  >
                </div>
                <div
                  v-if="(mold === 0 || mold === 2)"
                  class="item"
                >
                  <input
                    v-model.number="dataFrom.volume"
                    :placeholder="$t('product.commodityVolume')"
                    type="number"
                    :max="100000000"
                    :min="0"
                    :step="0.01"
                    class="tag-input-width"
                    style="min-width: 160px;"
                    @blur="
                      handleInputValue(
                        dataFrom.volume,
                        null,
                        'volume',
                        0,
                        100000000
                      )
                    "
                  >
                </div>
                <div
                  v-if="(mold === 0 || mold === 2)"
                  class="item"
                >
                  <input
                    v-model.number="dataFrom.weight"
                    :placeholder="$t('product.commodityWeight')"
                    type="number"
                    :max="100000000"
                    :min="0"
                    :step="0.01"
                    class="tag-input-width"
                    style="min-width: 155px;"
                    @blur="
                      handleInputValue(
                        dataFrom.weight,
                        null,
                        'weight',
                        0,
                        100000000
                      )
                    "
                  >
                </div>
                <div
                  v-if="mold !== 2"
                  class="item"
                >
                  <input
                    v-model.number="dataFrom.stockWarning"
                    :placeholder="$t('product.stockWarning')"
                    type="number"
                    :max="9999999"
                    :min="0"
                    :step="1"
                    style="min-width: 135px;"
                    class="tag-input-width"
                    @keyup="
                      dataFrom.stockWarning = String(dataFrom.stockWarning).match(
                        /[^0-9]{1,7}/
                      )
                        ? 0
                        : dataFrom.stockWarning
                    "
                    @blur="
                      handleInputValue(
                        dataFrom.stockWarning,
                        null,
                        'stockWarning',
                        0,
                        9999999
                      )
                    "
                  >
                </div>
                <div class="item" />
                <div class="btn-row">
                  <div
                    class="default-btn primary-btn"
                    @click="batchSet"
                  >
                    {{ $t("order.save") }}{{ $t("publics.batchSetting") }}
                  </div>
                </div>
              </div>
            </el-form>
          </div>
        </div>
        <!--sku列表-->
        <div class="table-con">
          <el-table
            :empty-text="$t('shop.noData')"
            :data="lists"
            header-cell-class-name="table-header"
            :span-method="tableSpanMethod"
            row-class-name="table-row"
            border
          >
            <el-table-column
              v-for="(leftTitle, index) in tableLeftTitles"
              :key="index"
              min-width="100"
              :label="leftTitle.tagName"
            >
              <template #default="scope">
                <div v-if="scope.row.properties">
                  {{
                    scope.row.properties.split(";")[index].substring(scope.row.properties.split(";")[index].indexOf(':') + 1)
                  }}
                </div>
              </template>
            </el-table-column>
            <!-- 售价 -->
            <el-table-column
              prop="price"
              :label="$t('product.sellingPrice') + '(' + useCurrencyStore().defMark + ')'"
            >
              <template #default="scope">
                <input
                  v-model.number="scope.row.price"
                  type="number"
                  :precision="2"
                  :max="100000000"
                  :min="0.01"
                  :step="0.01"
                  :disabled="!scope.row.status"
                  class="tag-input-width"
                  @blur="
                    handleInputValue(
                      scope.row.price,
                      scope.$index,
                      'price',
                      0.01,
                      100000000
                    )
                  "
                >
              </template>
            </el-table-column>
            <!-- 库存 -->
            <el-table-column
              :width="$t('language') !== 'zh_CN'?110:''"
              prop="stocks"
              :label="$t('coupon.stock')"
            >
              <template #header>
                <span>
                  {{ $t('marketing.inventory') }}
                  <el-tooltip
                    v-if="mold === 2"
                    class="box-item"
                    effect="dark"
                    :content="$t('prod.stocksEditContent')"
                    placement="top"
                  >
                    <el-icon style="top: 2px"><QuestionFilled /></el-icon>
                  </el-tooltip>
                </span>
              </template>

              <template #default="scope">
                <div class="stock-number">
                  <input
                    v-model.number="scope.row.stocks"
                    type="number"
                    :max="9999999"
                    :min="0"
                    :step="1"
                    :disabled="!scope.row.status || (mold === 0 && isHaveStockPoint) || mold === 2"
                    :class="['tag-input-width', mold===0 && scope.row.status!==0?'shade-input':null]"
                    @keyup="
                      scope.row.stocks = String(scope.row.stocks).match(/[^0-9]/)
                        ? ''
                        : scope.row.stocks
                    "
                    @blur=" handleInputValue( scope.row.stocks, scope.$index, 'stocks', 0, 9999999 ) "
                  >
                  <div
                    v-if="mold !== 1 && mold !== 2 && scope.row.status!==0 && isHaveStockPoint"
                    class="shade"
                    @click="onSetStock(scope)"
                  />
                </div>
              </template>
            </el-table-column>
            <!-- 市场价 -->
            <el-table-column
              prop="oriPrice"
              :label="$t('product.marketPrice') + '(' + useCurrencyStore().defMark + ')'"
            >
              <template #default="scope">
                <input
                  v-model.number="scope.row.oriPrice"
                  type="number"
                  :precision="2"
                  :max="100000000"
                  :min="0"
                  :step="0.01"
                  :disabled="!scope.row.status"
                  class="tag-input-width"
                  @blur="
                    handleInputValue(
                      scope.row.oriPrice,
                      scope.$index,
                      'oriPrice',
                      0,
                      100000000
                    )
                  "
                >
              </template>
            </el-table-column>
            <!-- 体积 -->
            <el-table-column
              v-if="(mold === 0 || mold === 2)"
              :width="$t('language') !== 'zh_CN'?170:''"
              prop="volume"
              :label="$t('product.commodityVolume')"
            >
              <template #default="scope">
                <input
                  v-model.number="scope.row.volume"
                  type="number"
                  :max="100000000"
                  :min="0"
                  :step="0.01"
                  :disabled="!scope.row.status"
                  class="tag-input-width"
                  @blur="
                    handleInputValue(
                      scope.row.volume,
                      scope.$index,
                      'volume',
                      0,
                      100000000
                    )
                  "
                >
              </template>
            </el-table-column>
            <!-- 重量 -->
            <el-table-column
              v-if="(mold === 0 || mold === 2)"
              :width="$t('language') !== 'zh_CN'?170:''"
              prop="weight"
              :label="$t('product.commodityWeight')"
            >
              <template #default="scope">
                <input
                  v-model.number="scope.row.weight"
                  type="number"
                  :max="100000000"
                  :min="0"
                  :step="0.01"
                  :disabled="!scope.row.status"
                  class="tag-input-width"
                  @blur="
                    handleInputValue(
                      scope.row.weight,
                      scope.$index,
                      'weight',
                      0,
                      100000000
                    )
                  "
                >
              </template>
            </el-table-column>

            <!-- 库存预警 -->
            <el-table-column
              v-if="mold !== 2"
              :width="$t('language') !== 'zh_CN'?170:''"
              prop="stockWarning"
              :label="$t('product.stockWarning')"
            >
              <template #default="scope">
                <input
                  v-model.number="scope.row.stockWarning"
                  type="number"
                  :max="9999999"
                  :min="0"
                  :step="1"
                  :disabled="!scope.row.status"
                  class="tag-input-width"
                  @keyup="
                    scope.row.stockWarning = String(scope.row.stockWarning).match(/[^0-9]/)
                      ? ''
                      : scope.row.stockWarning
                  "
                  @blur="
                    handleInputValue(
                      scope.row.stockWarning,
                      scope.$index,
                      'stockWarning',
                      0,
                      9999999
                    )
                  "
                >
              </template>
            </el-table-column>

            <!-- 编码 -->
            <el-table-column
              prop="partyCode"
              :label="$t('product.commodityCode')"
              :width="$t('language') !== 'zh_CN' ? '410px' : '220px'"
            >
              <template #default="scope">
                <input
                  ref="partyCodeIntRef"
                  v-model="scope.row.partyCode"
                  type="text"
                  maxlength="36"
                  :disabled="!scope.row.status"
                  class="tag-input-width text-input party-code"
                  :placeholder="$t('product.postProductTips15')"
                  @blur="validatePartyCode(scope)"
                >
              </template>
            </el-table-column>

            <el-table-column
              width="150"
              fixed="right"
              :label="$t('text.menu')"
              align="center"
            >
              <template #default="scope">
                <div
                  v-if="scope.row.status"
                  class="default-btn text-btn"
                  @click="changeSkuStatus(`${scope.$index}`)"
                >
                  {{ $t("publics.disable") }}
                </div>
                <div
                  v-else
                  class="default-btn text-btn"
                  @click="changeSkuStatus(`${scope.$index}`)"
                >
                  {{ $t("shop.ena") }}
                </div>
                <div
                  v-if="mold === 2"
                  class="default-btn text-btn"
                  @click="onAssociationGoods(scope)"
                >
                  {{ $t("product.prod") }}({{ scope.row.skuComboList?.length || 0 }})
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-form-item>

    <association-goods
      v-if="associationGoodsVisible"
      ref="associationGoodsRef"
      @on-select-success="onSelectSuccess"
    />
    <!-- 库存设置 -->
    <stock-update
      v-if="stockUpdateVisible"
      ref="stockUpdateRef"
      :is-need-verify-delivery="true"
      :delivery-mode="deliveryMode"
      :is-hide-station="stockPointInfo.stationSize === 0"
      @set-stock-callback="onSetStockCallback"
    />
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { validNoEmptySpace } from '@/utils/validate'
import { flatten as genFlatten } from '@/utils'
import { useProdStore } from '@/stores/prod.js'
import AssociationGoods from './association-goods/index.vue'

const prodStore = useProdStore()

const emit = defineEmits(['update:modelValue', 'on-change-data'])

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => ([])
  },
  prodNameCn: {
    type: String,
    default: ''
  },
  prodNameEn: {
    type: String,
    default: ''
  },
  // 0-普通商品;1-虚拟商品;2-组合商品
  mold: {
    type: Number,
    default: 0
  },
  // 配送方式
  deliveryMode: {
    type: Object,
    default: () => {
      return {}
    }
  },
  stockPointInfo: {
    type: Object,
    default: () => {
      return {}
    }
  }
})

watch(() => props.deliveryMode.hasUserPickUp, (val) => {
  let flag = false
  lists.value.forEach((skuItem) => {
    // 监听配送方式中的用户自提
    // 如果取消，则将设置的门店相关库存移除并存储；在重新勾选时，将存储起来的门店相关设置重新添加
    if (!val) {
      skuItem.filterList = []
      skuItem.filterStock = 0 // 过滤出来的库存数量
      skuItem.stockPointList = skuItem.stockPointList?.filter(stockItem => {
        if (stockItem.stockPointType === 2) {
          // 存储门店相关库存
          skuItem.filterList.push(stockItem)
          skuItem.filterStock += Number(stockItem.stock)
          flag = true
        }
        return stockItem.stockPointType === 1
      })
      skuItem.stocks -= skuItem.filterStock
    } else {
      if (skuItem.filterList && skuItem.stockPointList?.length) {
        skuItem.stockPointList = skuItem.stockPointList.concat(skuItem.filterList)
        skuItem.filterList = []
        flag = true
      }
      if (skuItem.filterStock) {
        skuItem.stocks += skuItem.filterStock
        skuItem.filterStock = 0
        flag = true
      }
    }
  })
  if (flag) {
    // 仓库信息有更改，就执行更新
    emit('update:modelValue', lists.value)
  }
})

/**
 * 是否存在库存点，存在多余库存点时，修改库存才进行弹窗
 * @type {ComputedRef<unknown>}
 */
const isHaveStockPoint = computed(() => {
  if (props.stockPointInfo.warehouseSize > 1) {
    // 仓库数量大于1（除默认仓库外还存在仓库），则直接返回true
    return true
  } else if (props.deliveryMode.hasUserPickUp && props.stockPointInfo.stationSize > 0) {
    // 开启了自提，且门店数量大于0返回true
    return true
  }
  return false
})

// 表格数据
const lists = ref([])
let rowspan = []
let initing = true

watch(() => lists.value, (data) => {
  emit('on-change-data', data)
}, {
  deep: true
})

const dataFrom = reactive({
  oriPrice: null,
  price: null,
  stocks: null,
  weight: null,
  volume: null,
  stockWarning: null
})

const batchList = ref([])
const tableLeftTitles = computed(() => {
  return skuTags.value
})

const skuTags = computed(() => {
  return prodStore.skuTags.filter((item) => {
    return !!(
      item.tagItems &&
      item.tagItems.length &&
      !item.tagItems[0].creating
    )
  })
})
const defalutSku = computed(() => {
  return prodStore.defalutSku
})

watch(() => skuTags.value, () => {
  batchList.value = JSON.parse(JSON.stringify(skuTags.value))
  batchList.value.forEach(el => {
    if (!el.value) {
      el.value = -1
    }
  })
  lists.value = genFlatten(skuTags.value, lists.value, defalutSku.value)
  computeRowspan()
}, { deep: true })

watch(() => [props.prodNameCn, props.prodNameEn], () => {
  skuAddProdName()
})

const isEdit = ref(false)
onMounted(() => {
  isEdit.value = false
  if (!lists.value.length) {
    lists.value.push({
      oriPrice: 0.01,
      partyCode: '',
      price: 0.01,
      prodName: '',
      status: 1,
      stocks: 0,
      volume: 0,
      weight: 0,
      stockWarning: 0,
      skuComboList: [] // 组合单品列表
    })
  }
})

const init = (skuList) => {
  initing = true
  lists.value = genFlatten(skuTags.value, skuList, defalutSku.value)
  computeRowspan()
}

const changeSkuImg = (propValue, img) => {
  // 把对应的sku图片修改
  for (let i = 0; i < lists.value.length; i++) {
    if (!lists.value[i].properties) {
      continue
    }
    const pVal = lists.value[i].properties.split(';')[0]
    const properties = pVal.substring(pVal.indexOf(':') + 1)
    if (properties === propValue) {
      lists.value[i].pic = img
    }
  }
}
const clearSkuImg = () => {
  for (let i = 0; i < lists.value.length; i++) {
    lists.value[i].pic = ''
  }
}
const computeRowspan = () => {
  rowspan = []
  const tempRowspan = (index) => {
    const span = []
    let dot = 0
    lists.value.forEach((item, idx) => {
      if (idx === 0) {
        span.push(1)
      } else {
        if (
          checkIsEqualByIndex(
            item.properties,
            lists.value[idx - 1].properties,
            index
          )
        ) {
          span[dot] += 1
          span.push(0)
        } else {
          dot = idx
          span.push(1)
        }
      }
    })

    rowspan.push(span)
  }
  // eslint-disable-next-line no-unused-vars
  skuTags.value.map((item, index) => tempRowspan(index))
}
const checkIsEqualByIndex = (str1, str2, index) => {
  const strArr1 = str1.split(';')
  const strArr2 = str2.split(';')
  const temp1 = [strArr1[index].slice(0, strArr1[index].indexOf(':')), strArr1[index].substring(strArr1[index].indexOf(':') + 1)]
  const temp2 = [strArr2[index].slice(0, strArr2[index].indexOf(':')), strArr2[index].substring(strArr2[index].indexOf(':') + 1)]
  return temp1[1] === temp2[1]
}
// eslint-disable-next-line no-unused-vars
const tableSpanMethod = ({ row, column, rowIndex, columnIndex }) => {
  for (let i = 0; i < skuTags.value.length; i++) {
    if (columnIndex === i) {
      if (rowspan[i] && rowspan[i][rowIndex]) {
        return {
          rowspan: rowspan[i][rowIndex],
          colspan: 1
        }
      } else {
        return {
          rowspan: 0,
          colspan: 0
        }
      }
    }
  }
}
const changeSkuStatus = (tagIndex) => {
  lists.value[tagIndex].status = lists.value[tagIndex].status ? 0 : 1
  emit('update:modelValue', lists.value)
}
const skuAddProdName = () => {
  if (initing) {
    return
  }
  const skuList = []
  emit('update:modelValue', lists.value)
  for (let i = 0; i < lists.value.length; i++) {
    const sku = Object.assign({}, lists.value[i])
    if (!sku.properties) {
      return
    }
    sku.skuName = ''
    sku.skuNameEn = ''
    const properties = sku.properties.split(';')
    for (const propertiesKey in properties) {
      sku.skuName += properties[propertiesKey].substring(properties[propertiesKey].indexOf(':') + 1) + ' '
    }
    sku.prodNameCn = props.prodNameCn + ' ' + sku.skuName
    sku.prodName = props.prodNameCn + ' ' + sku.skuName
    sku.prodNameEn = props.prodNameEn + ' ' + sku.skuNameEn
    skuList.push(sku)
  }
}

const batchSet = () => {
  lists.value.forEach((sku) => {
    let isBatch = true
    sku.properties.split(';').forEach((el, index) => {
      // -1为全部
      if (batchList.value.length > 0 && batchList.value[index].value !== -1 && el !== batchList.value[index].tagName + ':' + batchList.value[index].value) {
        isBatch = false
      }
    })
    if (isBatch) {
      if (dataFrom.oriPrice || dataFrom.oriPrice === 0) {
        sku.oriPrice = dataFrom.oriPrice
      }
      if (dataFrom.price) {
        sku.price = dataFrom.price
      }
      if (dataFrom.stocks || dataFrom.stocks === 0) {
        let isHaveDefaultWarehouse = false // 库存中是否存在默认仓库
        for (let i = 0; i < sku.stockPointList.length; i++) {
          if (sku.stockPointList[i].skuId) {
            // 存在skuId，则计算changeStock，并将默认仓库库存设置为stockIntVal
            if (sku.stockPointList[i].type !== 0) {
              // 非默认仓库，库存改为0
              sku.stockPointList[i].changeStock = -(sku.stockPointList[i].stock - (sku.stockPointList[i].changeStock || 0))
              sku.stockPointList[i].stock = 0
            } else {
              sku.stockPointList[i].changeStock = dataFrom.stocks - sku.stockPointList[i].stock + (sku.stockPointList[i].changeStock || 0)
              sku.stockPointList[i].stock = dataFrom.stocks
              isHaveDefaultWarehouse = true
            }
          } else {
            if (sku.stockPointList[i].type === 0) {
              isHaveDefaultWarehouse = true
              sku.stockPointList[i].stock = dataFrom.stocks // 无skuId的默认仓库（此次修改新增的仓库库存，无需传changeStock）
              continue
            }
            sku.stockPointList.splice(i, 1) // 不存在skuId，则删除当前项
            i--
          }
        }
        if (!isHaveDefaultWarehouse) {
          // 不存在默认仓库，则存储一条默认仓库信息
          sku.stockPointList.push({
            stock: dataFrom.stocks,
            stockPointId: props.stockPointInfo.defaultWarehouseId,
            stockPointType: 1,
            type: 0
          })
        }
        sku.stocks = dataFrom.stocks
        initing = false
      }
      if (dataFrom.weight || dataFrom.weight === 0) {
        sku.weight = dataFrom.weight
      }
      if (dataFrom.volume || dataFrom.volume === 0) {
        sku.volume = dataFrom.volume
      }
      if (dataFrom.stockWarning || dataFrom.stockWarning === 0) {
        sku.stockWarning = dataFrom.stockWarning
      }
    }
  })
  skuAddProdName()
}

/**
 * 处理输入框数据
 * @param data
 * @param index
 * @param dataFields
 * @param min 最小值
 * @param max 最大值
 */
const handleInputValue = (data, index, dataFields, min, max) => {
  if (index !== undefined && index !== null) {
    if (dataFields === 'stocks' && (props.stockPointInfo.warehouseSize === 1 || props.mold === 1)) {
      const stockPoint = lists.value[index].stockPointList[0]
      const stock = (+data > max) ? max : (data || 0)
      const changeStock = stockPoint.skuId ? stock - stockPoint.stock + (stockPoint.changeStock || 0) : undefined
      // 仅有一个仓库（默认仓库）时，直接修改库存时同步修改库存点列表
      lists.value[index].stockPointList = [{
        ...stockPoint,
        changeStock,
        stock
      }]
    }
    // 表格
    if (+data > max) {
      lists.value[index][dataFields] = max
      if (dataFields === 'stocks') {
        emit('update:modelValue', lists.value)
      }
      return
    }
    if (+data <= min || !data) {
      lists.value[index][dataFields] = min
      if (dataFields === 'stocks') {
        emit('update:modelValue', lists.value)
      }
      return
    }
    if (
      dataFields === 'price' ||
      dataFields === 'oriPrice' ||
      dataFields === 'volume' ||
      dataFields === 'weight' ||
      dataFields === 'stockWarning'
    ) {
      lists.value[index][dataFields] = checkInput(data)
    }
  } else {
    // 批量
    if (data === null || data === '') {
      return
    }
    if (+data > max) {
      dataFrom[dataFields] = max
      return
    }
    if (+data <= min) {
      dataFrom[dataFields] = min
      return
    }
    if (
      dataFields === 'price' ||
      dataFields === 'oriPrice' ||
      dataFields === 'volume' ||
      dataFields === 'weight' ||
      dataFields === 'stockWarning'
    ) {
      dataFrom[dataFields] = checkInput(data)
    }
  }
  if (dataFields === 'stocks') {
    emit('update:modelValue', lists.value)
  }
}

/**
 * 只允许输入正数和小数(保留小数点后两位)
 */
const checkInput = (num) => {
  if (num) {
    let tmpVal = String(num).replace(/[^\d^\\.]/g, '')
    const reg = /^(0|([1-9]\d*))(\.\d{1,2})?$/ // 最多允许后输入两位小数
    if (!reg.test(tmpVal)) {
      tmpVal = tmpVal + ''
      tmpVal = tmpVal.substring(0, tmpVal.indexOf('.') + 3)
      const n = tmpVal.split('.').length - 1
      if (n > 1) {
        tmpVal = tmpVal.substring(0, tmpVal.indexOf('.'))
      }
    }
    return tmpVal
  } else {
    return ''
  }
}

/**
 * 编码输入框校验
 */
const validatePartyCode = (scope) => {
  const { row, $index } = scope
  // 纯空格校验
  if (validNoEmptySpace(row.partyCode)) {
    lists.value[$index].partyCode = ''
    return
  }
  // 商品编码重复校验
  check(row, $index)
}

const check = (row, $index) => {
  if (
    row.partyCode &&
    lists.value.find(
      (el, index) =>
        el.partyCode && index !== $index && el.partyCode === row.partyCode
    )
  ) {
    ElMessage({
      message: $t('product.postProductTips16'),
      type: 'error',
      duration: 1500
    })
  }
}
const getDataList = () => {
  return lists.value
}
const stockUpdateVisible = ref(false)
const stockUpdateRef = ref(null)
const onSetStock = (scope) => {
  if (props.mode === 1) {
    // 虚拟商品库存为默认仓库
    return
  }
  const { $index, row } = scope
  stockUpdateVisible.value = true
  nextTick(() => {
    stockUpdateRef.value.init(row, $index)
  })
}

// 修改库存弹窗回调
const onSetStockCallback = (res, sum, index) => {
  lists.value[index].stockPointList = res
  lists.value[index].stocks = sum
  emit('update:modelValue', lists.value)
}

/** start 关联商品相关 */

// 当前选中的列表序号
const newListIndex = ref(null)
const associationGoodsRef = ref(null)
const associationGoodsVisible = ref(false)
/**
 * 打开商品列表弹窗,选择需要关联的商品
 */
const onAssociationGoods = (scope) => {
  newListIndex.value = Number(scope.$index)
  associationGoodsVisible.value = true
  nextTick(() => {
    associationGoodsRef.value.openDialog(lists.value[newListIndex.value]?.skuComboList || [])
  })
}

/**
 * 选择商品完成后回调
 */
const onSelectSuccess = (data) => {
  lists.value[newListIndex.value].skuComboList = data.list
  lists.value[newListIndex.value].priceFee = 0.01
  lists.value[newListIndex.value].stocks = data.totalStock
}

/** end 关联商品相关 */

defineExpose({
  init,
  changeSkuImg,
  clearSkuImg,
  getDataList
})

</script>

<style lang="scss" scoped>
.mod-prod-sku-table {
  // eslint-disable-next-line vue-scoped-css/no-unused-selector
  .pic-uploader-component .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    // eslint-disable-next-line vue-scoped-css/no-unused-selector
    .pic-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 120px;
      height: 120px;
      line-height: 120px;
      text-align: center;
    }
  }
  // eslint-disable-next-line vue-scoped-css/no-unused-selector
  .pic-uploader-component .el-upload:hover {
    border-color: #409eff;
  }
  // eslint-disable-next-line vue-scoped-css/no-unused-selector
  .tag-input-width.el-input-number--small {
    width: 100%;
  }
  .tag-input-width.party-code::placeholder {
    color: #999;
  }
  // eslint-disable-next-line vue-scoped-css/no-unused-selector
  .tag-input-width.party-code.err-tips {
    border-color: #d40000;
  }

  // 表格输入框
  .tag-input-width {
    width: 100%;
    padding-left: 5px;
    padding-right: 0;
    border: 1px solid #dcdcdc;
    border-radius: 2px;
    height: 32px;
    line-height: 32px;
    box-sizing: border-box;
    &:focus {
      outline: 0;
    }
  }
  .tag-input-width.text-input {
    padding-right: 5px;
  }
  // 表格+批量设置
  .sku-table-con {
    display: block;
    padding: 10px;
    width: 100%;
    border: 1px solid #dcdcdc;
    // eslint-disable-next-line vue-scoped-css/no-unused-selector
    .table-header {
      background: #f8f8f8;
    }

    // 批量设置
    .batch-settings-box {
      .set-txt {
        padding-top: 10px;
        .weak-txt {
          color: #999;
          font-size: 12px;
          margin-left: 5px;
        }
      }
      .batch-settings-tb {
        margin: 10px 0;

        .batch-settings-con {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 0 0 15px 0;
          .item {
            flex: 1;
            padding: 0 10px;
            & :deep(.el-form-item),
            & :deep(.el-form-item__content) {
              width: 100%;
              margin-right: 0;
            }
          }
          .btn-row{
            text-align: right;
            width: 220px;
          }
        }
      }
    }

    .stock-number {
      position: relative;
      .shade {
        position: absolute;
        top: 0;
        bottom: 0;
        left: 0;
        right: 0;
        cursor: pointer;
      }
      :deep(.shade-input) {
        background-color: #ffffff;
      }
    }
  }
  .bat-set-item {
    min-width: 100px;
  }
  // 保障表格中输入框的最小宽度
  .table-con {
    :deep(.el-table) {
      col {
        min-width: 150px;
      }
    }
  }
}
.filter-submitBtn span {
  color: #fff !important;
}
div :deep(.el-table tbody tr:hover > td) {
  background-color: #ffffff;
}

</style>

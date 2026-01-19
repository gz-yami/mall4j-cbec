<template>
  <el-dialog
    v-model="visible"
    :title="$t('product.select')"
    :modal="true"
    top="100px"
    class="component-sku-select"
    :close-on-click-modal="false"
  >
    <el-form
      :inline="true"
      :model="dataForm"
      class="demo-form-inline search-bar"
      @submit.prevent
    >
      <div class="input-row">
        <el-form-item :label="$t('product.prodName')">
          <el-input
            v-model.trim="prodName"
            :placeholder="$t('product.prodName')"
            clearable
          />
        </el-form-item>
        <el-form-item :label="$t('product.shopCategories')">
          <el-cascader
            v-model="selectedCategory"
            expand-trigger="hover"
            :options="categoryList"
            :props="categoryTreeProps"
            :clearable="true"
            style="width: 180px"
            @change="handleChange"
          />
        </el-form-item>
        <el-form-item>
          <div
            class="default-btn primary-btn"
            @click="searchProd"
          >
            {{
              $t("order.query")
            }}
          </div>
        </el-form-item>
        <el-form-item>
          <div
            class="default-btn"
            @click="clean"
          >
            {{
              $t("shop.resetMap")
            }}
          </div>
        </el-form-item>
      </div>
    </el-form>
    <div class="chosenCountTip">
      <span>{{ $t("dataAnaly.chosen") }}{{ limitType === 1 ? chosenProdCount : chosenSkuCount }}{{ limitType === 1 ? $t("combo.countProd") : $t("giveaway.countSku") }},</span>
      <span v-if="limit !== -1">{{ $t("combo.maxChose") }}{{ limit }}{{ limitType === 1 ? $t("combo.countProd") : $t("giveaway.countSku") }}</span>
    </div>
    <div class="main sku-select-main">
      <el-table
        row-class-name="table-row"
        header-cell-class-name="table-header"
        :data="dataList"
      >
        <el-table-column
          :label="$t('product.prodName')"
          fixed="left"
          align="center"
          width="380"
        >
          <template #default="scope">
            <div class="prod-item">
              <div class="select-dor">
                <el-radio
                  v-if="isRadio && limit === 1"
                  v-model="checkProdId"
                  :label="scope.row.prodId"
                  :disabled="checkMainProdIds(scope.row.prodId)"
                  @change="radioCheckProd(scope.$index)"
                >
                  &nbsp;
                </el-radio>
                <el-checkbox
                  v-else
                  v-model="scope.row.check"
                  :indeterminate="scope.row.indeterminate"
                  :disabled="checkProdIsLimit(scope.row.prodId) || checkMainProdIds(scope.row.prodId)"
                  @change="checkProd(scope.$index)"
                />
              </div>
              <div class="prod-info">
                <div class="prod-image">
                  <ImgShow
                    :src="scope.row.pic"
                    :class-list="['prod-img']"
                  />
                </div>
                <div class="prod-name">
                  {{ scope.row.prodName }}
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('product.productSpecifi')"
          align="center"
          min-width="180"
        >
          <template #default="scope">
            <div
              v-for="(sku, skuIndex) in scope.row.skuList"
              :key="sku.skuId"
              class="items name"
            >
              <span class="sku-container">
                <div class="select-dor">
                  <el-checkbox
                    v-if="scope.row.skuList.length > 1"
                    v-model="sku.check"
                    :disabled="checkSkuIsLimit(scope.row.prodId, sku.skuId) || checkMainProdIds(scope.row.prodId)"
                    @change="checkSku(scope.$index, skuIndex)"
                  />
                </div>
                <div class="name">
                  {{ sku.skuName || '-' }}
                </div>
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('components.commodityPrice')"
          align="center"
          min-width="140"
          class="prodPrice"
        >
          <template #default="scope">
            <div
              v-for="(sku) in scope.row.skuList"
              :key="sku.skuId"
              class="price-container"
            >
              {{ sku.price }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('coupon.stock')"
          align="center"
        >
          <template #default="scope">
            <div
              v-for="(sku) in scope.row.skuList"
              :key="sku.skuId"
              class="stocks-container"
            >
              {{ sku.stocks }}
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-if="dataList.length"
        :current-page="pageParam.pageIndex"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageParam.pageSize"
        :total="pageParam.totalPage"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px"
        @size-change="sizeChangeHandle"
        @current-change="currentChangeHandle"
      />
    </div>
    <template #footer>
      <span>
        <div
          class="default-btn"
          @click="visible = false"
        >{{
          $t("crud.filter.cancelBtn")
        }}</div>
        <div
          class="default-btn primary-btn"
          type="primary"
          @click="submitProds()"
        >{{
          $t("crud.filter.submitBtn")
        }}</div>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ElMessage } from 'element-plus'

import { onMounted, reactive, toRefs } from 'vue'
const emit = defineEmits(['refreshSelectProds'])
const props = defineProps({
  prodType: {
    default: null,
    type: Number // 商品类型
  },
  mainProdId: {
    default: () => [],
    type: Array // 主商品id
  },
  chosenCheckItems: {
    default: () => [],
    type: Array // 已选择的数据项
  },
  limit: {
    default: -1,
    type: Number // 可选择的spu数量限制, -1代表没有限制
  },
  limitType: {
    default: 2,
    type: Number // 限制数量类型 1：按商品 2：按sku
  },
  banSkuIdList: {
    default: () => [],
    type: Array // 禁止选择的skuId列表
  },
  status: {
    default: null,
    type: Number // 商品状态
  },
  skuStatus: {
    default: null,
    type: Number // sku状态
  },
  isActive: {
    default: null,
    type: Number // null: 普通商品 1：普通商品 + 活动商品
  },
  isRadio: {
    default: false, // 选取商品样式 true:单选框 false:复选框
    type: Boolean
  },
  // 是否包含预售商品
  isContainPreSell: {
    default: false, // true：包含 false：不包含
    type: Boolean
  },
  notProdTypes: {
    default: () => [3],
    type: Array
  }
})

const Data = reactive({
  visible: false,
  dataForm: {
    prodName: ''
  },
  dataList: [],
  prodName: '',
  categoryId: null,
  pageParam: {
    pageIndex: 1,
    pageSize: 10,
    totalPage: 0
  },
  dataListLoading: false,
  chosenProdCount: 0, // 已选择的商品数量，用于限制数量
  chosenSkuCount: 0, // 已选择的sku数量，用于限制数量
  chosenSkuMap: {}, // 已选择的结点对象map
  chosenProdMap: {}, // 已选择的商品结点对象map
  currentCheckProdMap: {}, // 当前选择的结点对象map
  banSkuMap: {}, // 禁止选择的sku对象map
  categoryList: [],
  selectedCategory: [],
  categoryTreeProps: {
    value: 'categoryId',
    label: 'categoryName'
  },
  // 单选框
  checkProdId: 0,
  lastCheckProd: null // 上一次所选的商品
})

const { visible, dataForm, dataList, prodName, pageParam, chosenProdCount, chosenSkuCount, categoryList, selectedCategory, categoryTreeProps, checkProdId } = toRefs(Data)

onMounted(() => {
  init()
})

// 获取数据列表
const init = () => {
  Data.checkProdId = 0
  Data.dataListLoading = true
  Data.prodName = ''
  Data.selectedCategory = []
  Data.chosenProdCount = 0
  Data.chosenSkuCount = 0
  Data.categoryId = null
  Data.chosenSkuMap = {}
  Data.chosenProdMap = {}
  Data.currentCheckProdMap = {}
  initDefaultCheckedKeys()
  getDataList()
  getCategoryList()
  Data.visible = true
}

// 判断是否选为主商品
const checkMainProdIds = (id) => {
  return props.mainProdId.some(item => {
    return item === id
  })
}

// 初始化默认选择的商品项记录
const initDefaultCheckedKeys = () => {
  if (props.chosenCheckItems && props.chosenCheckItems.length > 0) {
    props.chosenCheckItems.forEach(sku => {
      sku = sku || {}
      ++Data.chosenSkuCount
      if (Data.currentCheckProdMap[sku.comboProdId]) {
        Data.currentCheckProdMap[sku.comboProdId].currentCheckSkuMap[sku.comboSkuId] = sku
      } else {
        ++Data.chosenProdCount
        const prodObj = {}
        prodObj.prodId = sku.comboProdId
        prodObj.prodName = sku.prodName
        prodObj.pic = sku.pic
        prodObj.currentCheckSkuMap = {}
        prodObj.currentCheckSkuMap[sku.comboSkuId] = sku
        Data.currentCheckProdMap[sku.comboProdId] = prodObj
      }
    })
  }
}

// 处理商品选择
const handleProdCheck = (prod) => {
  // 商品选择事件
  if (props.limit !== -1 && Data.chosenProdCount >= props.limit) {
    // 数量超过限制
    prod.check = false
    ElMessage({
      message: $t('components.maxChoice') + props.limit + $t('combo.countProd'),
      type: 'error',
      duration: 1500,
      onClose: () => {
      }
    })
    return
  }
  // 把该商品的可选择的sku置为选择状态并保存选择的商品信息
  if (!Data.currentCheckProdMap[prod.prodId]) {
    ++Data.chosenProdCount
    Data.currentCheckProdMap[prod.prodId] = prod
  }
  const skuList = prod.skuList
  const currentCheckSkuMap = {}
  if (props.limit !== -1 && props.limitType === 2) {
    for (let i = 0; i < skuList.length && Data.chosenSkuCount < props.limit; i++) {
      if (!skuList[i].check) {
        ++Data.chosenSkuCount
        skuList[i].check = true
      }
      currentCheckSkuMap[skuList[i].skuId] = skuList[i]
    }
  } else {
    skuList.forEach(sku => {
      if (!sku.check) {
        ++Data.chosenSkuCount
        sku.check = true
      }
      currentCheckSkuMap[sku.skuId] = sku
    })
  }
  prod.currentCheckSkuMap = currentCheckSkuMap
  prod.indeterminate = false
}

const radioCheckProd = (index) => {
  const prod = Data.dataList[index]
  prod.check = true
  // 对上次所选择的商品进行处理
  if (Data.lastCheckProd) {
    // 商品取消选择事件，删除对应的商品结点
    delete Data.currentCheckProdMap[Data.lastCheckProd.prodId]
    --Data.chosenProdCount
    Data.lastCheckProd.skuList.forEach(sku => {
      if (sku.check) {
        --Data.chosenSkuCount
        sku.check = false
      }
    })
    Data.lastCheckProd.check = false
  }

  // 保存为上次操作
  Data.lastCheckProd = prod

  // 处理商品选择
  handleProdCheck(prod)
}

// 选择或取消选择商品
const checkProd = (index) => {
  const prod = Data.dataList[index]
  if (prod.check) {
    // 商品选择事件
    handleProdCheck(prod)
  } else {
    // 商品取消选择事件，删除对应的商品结点
    delete Data.currentCheckProdMap[prod.prodId]
    --Data.chosenProdCount
    prod.skuList.forEach(sku => {
      if (sku.check) {
        --Data.chosenSkuCount
        sku.check = false
      }
    })
    prod.check = false
    prod.indeterminate = false
  }
}

// 检查商品是否已达到限制
const checkProdIsLimit = (prodId) => {
  if (props.limit === -1) {
    return false
  }
  const checkCount = props.limitType === 1 ? Data.chosenProdCount : Data.chosenSkuCount
  if (checkCount >= props.limit && !Data.currentCheckProdMap[prodId]) {
    return true
  } else {
    return false
  }
}

// 检查sku是否已达到限制
const checkSkuIsLimit = (prodId, skuId) => {
  if (props.limit === -1) {
    return false
  }
  const checkCount = props.limitType === 1 ? Data.chosenProdCount : Data.chosenSkuCount
  if (checkCount >= props.limit) {
    if (props.limitType === 1) {
      return Data.currentCheckProdMap[prodId] === undefined || Data.currentCheckProdMap[prodId] === null
    }
    if (props.limitType === 2) {
      if (Data.currentCheckProdMap[prodId] === undefined || Data.currentCheckProdMap[prodId] === null) {
        return true
      }
      if (Data.currentCheckProdMap[prodId].currentCheckSkuMap[skuId] === undefined || Data.currentCheckProdMap[prodId].currentCheckSkuMap[skuId] === null) {
        return true
      }
      return false
    }
  }
  return false
}

// 选择或取消选择sku
const checkSku = (prodIndex, skuIndex) => {
  const prod = Data.dataList[prodIndex]
  const sku = prod.skuList[skuIndex]
  if (sku.check) {
    // sku选择事件
    if (checkSkuIsLimit(prod.prodId, sku.skuId)) {
      // 数量超过限制
      sku.check = false
      ElMessage({
        message: $t('components.maxChoice') + props.limit + $t('combo.countProd'),
        type: 'error',
        duration: 1500,
        onClose: () => {
        }
      })
      return
    }
    if (Data.currentCheckProdMap[prod.prodId]) {
      // 该商品存在已选择的sku
      Data.currentCheckProdMap[prod.prodId].currentCheckSkuMap[sku.skuId] = sku
    } else {
      // 该商品没有已选择的sku
      Data.currentCheckProdMap[prod.prodId] = prod
      ++Data.chosenProdCount
      const currentCheckSkuMap = {}
      currentCheckSkuMap[sku.skuId] = sku
      Data.currentCheckProdMap[prod.prodId].currentCheckSkuMap = currentCheckSkuMap
    }
    ++Data.chosenSkuCount
    const choseCount = Object.keys(Data.currentCheckProdMap[prod.prodId].currentCheckSkuMap).length
    prod.check = true
    prod.indeterminate = !(choseCount === prod.skuList.length)
    // 当选择商品为单选按钮时
    if (props.isRadio) {
      Data.checkProdId = prod.prodId
      Data.lastCheckProd = prod
    }
  } else {
    // sku取消选择
    --Data.chosenSkuCount
    sku.check = false
    const choseCount = Object.keys(Data.currentCheckProdMap[prod.prodId].currentCheckSkuMap).length
    if (choseCount === 1) {
      --Data.chosenProdCount
      prod.check = false
      prod.indeterminate = false
      // 当选择商品为单选按钮时
      if (props.isRadio) {
        Data.checkProdId = 0
        Data.lastCheckProd = null
      }
      delete Data.currentCheckProdMap[prod.prodId]
    } else {
      prod.indeterminate = true
      delete Data.currentCheckProdMap[prod.prodId].currentCheckSkuMap[sku.skuId]
    }
  }
}

// 获取店铺分类列表
const getCategoryList = () => {
  http({
    url: http.adornUrl('/prod/category/listCategory'),
    method: 'get',
    params: http.adornParams({
      status: 1
    })
  }).then(({ data }) => {
    Data.categoryList = treeDataTranslate(data, 'categoryId', 'parentId')
  })
}

// 分页获取商品数据
const getDataList = () => {
  http({
    url: http.adornUrl('/prod/prod/prodSkuPage'),
    method: 'get',
    params: http.adornParams(
      Object.assign(
        {
          current: Data.pageParam.pageIndex,
          size: Data.pageParam.pageSize,
          prodName: Data.prodName ? Data.prodName : null,
          prodType: props.prodType,
          categoryId: Data.categoryId,
          mold: 0,
          status: props.status,
          skuStatus: props.skuStatus,
          // 选择主商品时需要过滤掉活动商品
          isActive: props.isActive === 1 ? 1 : null,
          preSellStatus: props.isContainPreSell ? null : 0,
          notProdTypes: props.notProdTypes
        }
      )
    )
  }).then(({ data }) => {
    Data.pageParam.totalPage = data.total
    setProdCheckStatus(data.records)
    Data.dataList = data.records
    Data.dataListLoading = false
  }).catch(() => {
    Data.dataListLoading = false
  })
}

// 根据已选择的商品项设置商品选择状态
const setProdCheckStatus = (prodList) => {
  prodList.forEach(prod => {
    const skuList = prod.skuList
    if (!Data.currentCheckProdMap[prod.prodId]) {
      skuList.forEach(sku => {
        sku.check = false
      })
      prod.check = false
      prod.indeterminate = false
    } else {
      // 当前商品已选择的sku数量
      let selectCount = 0
      const checkSkuMap = Data.currentCheckProdMap[prod.prodId].currentCheckSkuMap || {}
      skuList.forEach(sku => {
        if (checkSkuMap[sku.skuId]) {
          ++selectCount
          sku.check = true
        } else {
          sku.check = false
        }
      })
      prod.check = selectCount > 0
      prod.indeterminate = selectCount > 0 && selectCount < skuList.length
    }
  })
}

// 每页数
const sizeChangeHandle = (val) => {
  Data.pageParam.pageSize = val
  Data.pageParam.pageIndex = 1
  getDataList()
}

// 当前页
const currentChangeHandle = (val) => {
  Data.pageParam.pageIndex = val
  getDataList()
}

// 获取分类id
const handleChange = (val) => {
  Data.categoryId = val && val[val.length - 1]
}

// 根据条件搜索商品
const searchProd = () => {
  Data.pageParam.pageIndex = 1
  getDataList()
}

// 清空搜索条件
const clean = () => {
  Data.prodName = ''
  Data.categoryId = null
  Data.selectedCategory = idList(Data.categoryList, Data.categoryId, 'categoryId', 'children').reverse()
}

// 确定事件
const submitProds = () => {
  const selectList = []
  for (const prodId in Data.currentCheckProdMap) {
    const prodObj = Data.currentCheckProdMap[prodId]
    const choseProdMap = Data.chosenProdMap[prodId] || {}
    for (const skuId in prodObj.currentCheckSkuMap) {
      const skuObj = (choseProdMap.chosenSkuMap !== undefined && choseProdMap.chosenSkuMap[skuId]) ? choseProdMap.chosenSkuMap[skuId] : prodObj.currentCheckSkuMap[skuId]
      skuObj.prodId = prodObj.prodId
      skuObj.prodName = prodObj.prodName
      skuObj.pic = prodObj.pic
      selectList.push(skuObj)
    }
  }
  emit('refreshSelectProds', selectList)
  Data.visible = false
}

defineExpose({
  init
})
</script>

<style lang="scss" scoped>
.component-sku-select {
  .prod-item {
    display: flex;
    align-items: center;
    align-content: center;
    flex-direction: row;
    .select-dor {
      width: 10px;
      padding: 2px;
    }
    .prod-info {
      display: flex;
      align-items: center;
    }
    .prod-image {
      width: 100px;
      height: 100px;
      padding: 10px;
    }
    .prod-name {
      // width: 240px;
      flex:1;
      font-size: 14px;
      color: #333333;
      word-break: break-all;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 2;
      overflow: hidden;
    }
  }
  .sku-container {
    width: 120px;
    display: flex;
    flex-direction: row;
    min-height: 32px;
    .select-dor {
      line-height: 32px;
      height: 32px;
      box-sizing: border-box;
      width: 10px;
      padding: 2px;
      margin-right: 10px;
    }
    .name {
      height: 32px;
      line-height: 32px;
      width: 100px;
      font-size: 14px;
      color: #333333;
      display: inline-block;
      white-space: nowrap;
      overflow: hidden;
      text-overflow:ellipsis;
    }
  }
  .price-container {
    height: 32px;
    line-height: 32px;
    align-content: center;
  }
  .stocks-container {
    height: 32px;
    line-height: 32px;
    align-content: center;
  }
}
</style>

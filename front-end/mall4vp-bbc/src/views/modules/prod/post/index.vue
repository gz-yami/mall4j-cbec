<template>
  <!-- 发布商品页面 -->
  <div class="post-product-page">
    <div class="prod-main-containers">
      <!-- 步骤 -->
      <div class="post-step">
        <div
          class="step-item"
          :class="{'active': postingSteps === 1 || postingSteps === 2 || postingSteps === 3}"
        >
          <!-- 01、选择商品类目 -->
          <div class="step-txt">
            01、{{ $t("product.selectProductCategory") }}
          </div>
        </div>
        <div
          class="step-item"
          :class="{'active': postingSteps === 2 || postingSteps === 3}"
        >
          <!-- 02、编辑商品信息 -->
          <div class="step-txt">
            02、{{ $t("product.editProductInfo") }}
          </div>
        </div>
        <div
          class="step-item"
          :class="{'active': postingSteps === 3}"
        >
          <!-- 03、编辑商品详情 -->
          <div class="step-txt">
            03、{{ $t("product.editProductDetails") }}
          </div>
        </div>
      </div>

      <!-- 内容 -->
      <div class="prod-content">
        <!-- 01、选择商品类目 -->
        <div
          v-if="postingSteps === 1 || postingSteps === 2"
          v-show="postingSteps === 1"
          class="prod-content-item"
        >
          <selectCategory
            v-model="dataForm"
            @selected-category-name="selectedCategoryName"
            @clear-stock="onClearStock"
          />
        </div>

        <!-- 02、编辑商品信息 -->
        <div
          v-if="postingSteps === 2"
          class="prod-content-item"
        >
          <editProduct
            ref="editProductRef"
            v-model="dataForm"
            :is-compose="isCompose"
            :plat-category-name="platCategoryName"
            :stock-point-info="stockPointInfo"
            @change-category="changeCategory"
            @updata-prod-data-form="updataProdDataForm"
            @change-write-off-time="changeWriteOffTime"
          />
        </div>

        <!-- 03、编辑商品详情 -->
        <div
          v-if="postingSteps === 3"
          class="prod-content-item"
        >
          <productDetails
            v-model="dataForm"
            @update-content="updateContent"
          />
        </div>
      </div>
    </div>

    <!-- 底部固定操作栏 -->
    <div :class="['prod-footer',{'prod-footer-cls':sidebarFold}]">
      <div class="foot">
        <div class="inner">
          <div
            v-if="postingSteps === 1"
            class="default-btn primary-btn"
            @click="nextStep"
          >
            {{ $t("product.nextStep1") }}
          </div>
          <div v-if="postingSteps === 2">
            <div
              v-if="!dataForm.prodId"
              class="default-btn"
              @click="prevStep"
            >
              {{ $t("product.prevStep") }}
            </div>
            <div
              class="default-btn save-btn"
              @click="saveStep"
            >
              {{ $t("product.saveBtn1") }}
            </div>
            <div
              class="default-btn primary-btn"
              @click="nextStep"
            >
              {{ $t("product.nextStep2") }}
            </div>
          </div>
          <div v-if="postingSteps === 3">
            <div
              class="default-btn"
              @click="prevStep"
            >
              {{ $t("product.prevStep") }}
            </div>
            <div
              class="default-btn primary-btn"
              @click="saveStep"
            >
              {{ $t("product.saveBtn1") }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import selectCategory from './components/posting-select-category.vue'
import editProduct from './components/posting-edit-product.vue'
import productDetails from './components/posting-product-details.vue'
import { useProdStore } from '@/stores/prod.js'

const router = useRouter()
const route = useRoute()

// 获取默认仓库id并传入编辑商品信息页面
const stockPointInfo = ref({
  warehouseSize: 0 // 用于判断是否请求完成（请求完成后，因为存在默认仓库，所以最底为1）
})

const commonStore = useCommonStore()
// 底部操作栏左偏移大小
const sidebarFold = computed(() => commonStore.sidebarFold)

// 发布步骤：1选择商品类目 2编辑商品信息 3编辑商品详情
const postingSteps = ref(null)
// 商品详情信息
const dataForm = ref({
  tableRate: -1, // 0 包邮 -1 固定运费 大于0运费模板id
  // 商品类型(0普通商品 1拼团 2秒杀 3积分 5活动商品)
  prodType: 0,
  mold: 0,
  video: '',
  pic: '',
  imgs: '',
  seq: 0,
  isCompose: 0,
  preSellStatus: 0,
  preSellTime: null,
  categoryId: route.query.categoryId ? parseInt(route.query.categoryId) : null,
  prodId: 0,
  brandId: 0,
  skuList: [],
  prodLangList: [],
  brandName: '',
  deliveryMode: {
    hasShopDelivery: true,
    hasUserPickUp: false,
    hasCityDelivery: false
  },
  // 运费模板id
  deliveryAmount: 0.01, // 统一运费的金额
  deliveryTemplateId: null,
  // 库存总和
  totalStocks: 0,
  /* 其他设置(虚拟商品时出现) */
  virtualRemark: [], // 留言
  // 核销次数 -1.多次核销 0.无需核销 1.单次核销
  writeOffNum: 0,
  // 核销有效期 -1.长期有效 0.自定义 1.当天24点前 x.x天内有效
  writeOffTime: -1,
  writeOffMultipleCount: -1,
  writeOffStart: null,
  writeOffEnd: null,
  // 售后服务 0不支持卖家申请退款 1支持卖家申请退款
  isRefund: 0,
  // 参数
  prodParameterList: []
})

const isCompose = ref(0)
let page = null

onMounted(() => {
  dataForm.value.prodId = route.query.prodId
  page = route.query.page
  if (!dataForm.value.prodId) {
    postingSteps.value = 1
  }
  // 获取产品详情数据
  getDataList()
  // 获取规格列表
  getSpecList()
})

const platCategoryName = ref('') // 平台选定分类名称
/**
 * 获取产品详情数据
 */
const getDataList = () => {
  if (dataForm.value.prodId) {
    http({
      url: http.adornUrl(`/prod/prod/info/${dataForm.value.prodId}`),
      method: 'get',
      params: http.adornParams()
    }).then(({ data }) => {
      dataForm.value = data
      dataForm.value.deliveryMode = JSON.parse(data.deliveryMode)
      dataForm.value.deliveryMode.hasShopDelivery = true

      if (!dataForm.value.prodLangList) {
        dataForm.value.prodLangList = []
      }

      // 运费选项处理
      if (dataForm.value.deliveryTemplateId < 1) {
        dataForm.value.tableRate = dataForm.value.deliveryTemplateId
        dataForm.value.deliveryTemplateId = null
      } else {
        dataForm.value.tableRate = 1
      }
      if (data.brand) {
        dataForm.value.brandId = data.brand.brandId
        dataForm.value.brandName = data.brand.name
      }
      dataForm.value.skuList.forEach(sku => {
        sku.skuComboList = sku.skuComboList || []
        sku.changeStock = parseInt(sku.stocks)
        sku.oriStock = parseInt(sku.stocks)
        // 非自提商品，则将门店相关库存过滤并存储（提交时携带回去）
        if (!dataForm.value?.deliveryMode?.hasUserPickUp) {
          sku.filterList = []
          sku.filterStock = 0 // 过滤出来的库存数量（库存数量变化，用于之后重新勾选后添加回去）
          sku.stockPointList = sku.stockPointList?.filter(stockItem => {
            if (stockItem.stockPointType === 2) {
              // 存储门店相关库存
              sku.filterList.push(stockItem)
              sku.filterStock += Number(stockItem.stock)
            }
            return stockItem.stockPointType === 1
          })
        }
      })
      if (data.mold === 1 && data.writeOffTime !== -1 && data.writeOffTime !== 0 && data.writeOffTime !== 1) {
        changeWriteOffTime(data.writeOffTime)
      }
      if (dataForm.value.categoryVO) {
        let firstCat
        let secondCat
        const threeCat = dataForm.value.categoryVO
        if (dataForm.value.categoryVO.categories[0].categoryId === dataForm.value.categoryVO.categories[1].parentId) {
          firstCat = dataForm.value.categoryVO.categories[0]
          secondCat = dataForm.value.categoryVO.categories[1]
        } else {
          firstCat = dataForm.value.categoryVO.categories[1]
          secondCat = dataForm.value.categoryVO.categories[0]
        }
        platCategoryName.value = firstCat.categoryName + ' > ' + secondCat.categoryName + ' > ' + dataForm.value.categoryVO.categoryName
        // 获取第一分类id
        prodStore.updatePlatProdCategory({
          platProdCategory: {
            firstCat: {
              id: firstCat.categoryId,
              categoryName: firstCat.categoryName
            },
            secondCat: {
              id: secondCat.categoryId,
              categoryName: secondCat.categoryName
            },
            threeCat: {
              id: threeCat.categoryId,
              categoryName: threeCat.categoryName
            }
          },
          selectUpdate: true
        })
      }
      postingSteps.value = 2
    }).catch(() => {
      postingSteps.value = 1
      // 出错，返回列表
      setTimeout(() => {
        router.push('/prod/manage/index')
      }, 1000)
    })
  } else {
    nextTick(() => {
      postingSteps.value = 1
      dataForm.value.pic = ''
      dataForm.value.imgs = ''
      dataForm.value.video = ''
    })
  }
}
// 获取规格列表
let specList = []
const getSpecList = () => {
  http({
    url: http.adornUrl('/prod/spec/list'),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    specList = data
  })
}

/**
 * 子组件更新数据
 */
const updataProdDataForm = (value) => {
  dataForm.value = Object.assign(dataForm.value, value)
}

/**
 * 获取选定的分类名称
 */
const selectedCategoryName = (categoryName) => {
  platCategoryName.value = categoryName
}

/**
 * 修改分类
 */
// eslint-disable-next-line no-undef
const prodStore = useProdStore()
const changeCategory = () => {
  const platProdCategory = prodStore.platProdCategory
  platCategoryName.value = platProdCategory.firstCat.categoryName +
    ' > ' +
    platProdCategory.secondCat.categoryName +
    ' > ' +
    platProdCategory.threeCat.categoryName
  dataForm.value.categoryId = platProdCategory.threeCat?.id
}

/**
 * 虚拟商品-获取自定义核销有效天数
 */
// 自定义核销有效天数
const validDays = ref(0)
const changeWriteOffTime = (validDaysPar) => {
  validDays.value = validDaysPar
}

/**
 * 获取详情
 */
const updateContent = (list) => {
  list.forEach((item, index) => {
    dataForm.value.prodLangList[index].content = item.content
  })
}

/**
 * 下一步
 */
const editProductRef = ref(null)
const nextStep = async () => {
  if (postingSteps.value === 1) {
    if (!dataForm.value.categoryId) {
      // 请选择商品分类
      ElMessage({
        message: $t('product.thisProduCategroy'),
        type: 'error',
        duration: 1000
      })
      return
    }
  }
  if (postingSteps.value === 2) {
    const result = await editProductRef.value?.dataFormValidation()
    if (!result) {
      return
    }
    if (!dataForm.value.skuList.find(el => el.status)) {
      // 至少要启用一种商品规格
      ElMessage({
        message: $t('product.enableSpec'),
        type: 'error',
        duration: 1500
      })
      return
    }

    if (dataForm.value.mold === 2 && dataForm.value.skuList.some(item => (item.skuComboList?.length === 0 && item.status !== 0))) {
      ElMessage({
        message: $t('prod.correlationTip'),
        duration: 1000
      })
      return false
    }
  }
  if (dataForm.value.mold === 1) {
    dataForm.value.preSellTime = ''
    dataForm.value.preSellStatus = 0
  }
  postingSteps.value += 1
}
/**
 * 上一步
 */
const prevStep = () => {
  if (postingSteps.value === 2) {
    // 返回第一步之前先更新已填写的商品信息
    editProductRef.value?.upadteProdInfo()
  }
  postingSteps.value -= 1
}
/**
 * 保存
 */
const saveStep = async () => {
  const result = await editProductRef.value?.dataFormValidation()
  if (postingSteps.value === 2 && !result) {
    return
  }
  onSubmit()
}

// 切换商品类型（虚拟/实物）清除库存
const onClearStock = () => {
  dataForm.value.skuList?.forEach((sku) => {
    if (sku.stocks) {
      sku.stocks = 0
      sku.stockPointList = [{
        stock: 0,
        stockPointId: stockPointInfo.value.defaultWarehouseId,
        stockPointType: 1,
        type: 0
      }]
    }
    dataForm.value.totalStock = 0 // 总库存
  })
}

const paramSetPriceAndStocks = (param) => {
  // 获取规格属性信息
  // param.skuList = prodSpecRef.value?.getTableSpecData()
  // 商品库存
  param.totalStocks = 0
  // 商品价格
  param.price = 0
  // 商品原价
  param.oriPrice = 0
  // 商品实际库存
  for (let i = 0; i < param.skuList.length; i++) {
    const element = param.skuList[i]
    if (element.status !== 1) {
      continue
    }
    if (param.price === 0) {
      param.price = element.price ? Number.parseFloat(element.price) : 0
    }
    // 商品价格为最低价的那件商品的价格
    param.price = Math.min(param.price, element.price)
    if ((param.price === element.price) || (Number.parseFloat(param.price) === Number.parseFloat(element.price))) {
      param.oriPrice = element.oriPrice ? Number.parseFloat(element.oriPrice) : 0
    }
    param.totalStocks += element.stocks ? Number.parseInt(element.stocks) : 0
  }
  // 如果sku没有商品名称，则使用商品的商品名称
  if (param.skuList.length === 1 && !param.skuList[0].skuName) {
    param.skuList[0].prodName = dataForm.value.prodName
  }
}

// 表单提交
const onSubmit = () => {
  if (!dataForm.value.categoryId) {
    // 请选择商品分类
    ElMessage({
      message: $t('product.thisProduCategroy'),
      type: 'error',
      duration: 1000
    })
    return
  }
  if (!dataForm.value.skuList.find(el => el.status)) {
    // 至少要启用一种商品规格
    ElMessage({
      message: $t('product.enableSpec'),
      type: 'error',
      duration: 1000
    })
    return
  }
  if (dataForm.value.mold === 2 && dataForm.value.skuList.some(item => (item.skuComboList?.length === 0 && item.status !== 0))) {
    ElMessage({
      message: $t('prod.correlationTip'),
      duration: 1000
    })
    return false
  }
  const param = Object.assign({}, dataForm.value)
  // 运费处理
  param.deliveryTemplateId = param.tableRate < 1 ? param.tableRate : param.deliveryTemplateId
  if (param.deliveryTemplateId !== -1) {
    param.deliveryAmount = null
  }
  // 设置价格和库存
  paramSetPriceAndStocks(param)
  if (!param.prodId && param.totalStocks === 0) {
    ElMessageBox.confirm($t('product.saveTip'), $t('resource.tips'), {
      confirmButtonText: $t('resource.confirm'),
      cancelButtonText: $t('resource.cancel'),
      type: 'warning'
    }).then(() => {
      confirmMethod(param)
    })
  } else {
    confirmMethod(param)
  }
}

let isSubmit = false
const confirmMethod = (param) => {
  param.content = null
  param.prodParameterList.forEach(item => {
    item.parameterKey = item.prodParameterLangList[0].parameterKey
    item.parameterValue = item.prodParameterLangList[0].parameterValue
  })

  // 取主语言的名称
  const masterProd = dataForm.value.prodLangList[0]
  param.prodName = masterProd.prodName
  const curLang = []

  // 判断不超过两万字
  let flag = false
  dataForm.value.prodLangList.forEach(item => {
    if (item.content) {
      const contentStr = item.content
        .replace(/<[^<>]+>/g, '')
        .replace(/&nbsp;/gi, '')
        .replace(/(\s)+/gi, '')
      // 限制不超过两万字
      if (contentStr.length >= 20000) {
        flag = true
      }
    }
    curLang.push(item.lang)
  })

  if (flag) {
    ElMessage({
      message: $t('product.productDetailsContentLimitTxt'),
      type: 'error',
      duration: 1500
    })
    return
  }

  param.skuList = onHandleSkuList(JSON.parse(JSON.stringify(param.skuList)), curLang, masterProd) // 处理skuList列表

  param.deliveryMode = undefined
  param.deliveryModeVo = dataForm.value.deliveryMode
  // 商品主图
  param.pic = dataForm.value.imgs.split(',')[0]
  // 虚拟商品
  if (dataForm.value.mold === 1) {
    // 选择"无需核销"，没有核销有效期
    if (dataForm.value.writeOffNum === 0) param.writeOffTime = null
    // 自定义核销有效天数
    if (dataForm.value.writeOffNum !== 0 && dataForm.value.writeOffTime === 2) {
      param.writeOffTime = parseInt(validDays.value)
    }
    param.virtualRemark = JSON.stringify(dataForm.value.virtualRemark)
    // 发布商品时，清除活动商品设置；编辑商品时，保留活动商品设置
    param.prodType = param.prodId ? param.prodType : 0
  }
  if (dataForm.value.mold !== 1) {
    // 普通商品，清空虚拟商品信息
    param = {
      ...param,
      virtualRemark: null,
      writeOffNum: null,
      writeOffTime: null,
      writeOffStart: null,
      writeOffEnd: null,
      isRefund: null
    }
  }
  if (!(param.mold !== 1 && param.preSellStatus === 1 && param.prodType !== 5)) {
    param.preSellTime = ''
  }

  if (isSubmit) {
    return
  }
  isSubmit = true

  http({
    url: http.adornUrl('/prod/prod'),
    method: param.prodId ? 'put' : 'post',
    data: http.adornData(param)
  }).then(() => {
    ElMessage({
      message: $t('publics.operation'),
      type: 'success',
      duration: 1500,
      onClose: () => {
        isSubmit = false
        useCommonStore().removeMainActiveTab()
        if (!isAuth('prod:prod:manage:view')) {
          router.push({ path: '/home' })
          return
        }
        router.replace({
          path: '/prod/manage/index',
          query: { page }
        })
      }
    })
  }).catch(() => {
    isSubmit = false
  })
}

const onHandleSkuList = (skuList, curLang, masterProd) => {
  skuList.forEach(sku => {
    sku.changeStock = parseInt(sku.stocks) - parseInt(sku.oriStock)
    // 设置不同语言的skuName和properties
    const skuLangList = []
    if (sku.properties) {
      for (const langItem of curLang) {
        const properties = sku.properties.split(';')
        let langProperties = ''
        let langSkuName = ''
        // 找出当前语言的商品信息
        const curLangProdInfo = dataForm.value.prodLangList.find(f => f.lang === langItem)
        const curProdName = curLangProdInfo ? curLangProdInfo.prodName : masterProd.prodName
        for (const propItem of properties) {
          const propKey = propItem.substring(0, propItem.indexOf(':'))
          const propVal = propItem.substring(propItem.indexOf(':') + 1)
          // 找出被选的规格名信息
          const fdDbKey = specList.find(it => it.propName === propKey)
          // 当前所选的规格名是否存在
          if (fdDbKey) {
            // 找出相应语言的规格名信息
            const fdPropKey = fdDbKey.prodPropLangList.find(it => it.lang === langItem)
            langProperties += fdPropKey ? fdPropKey.propName + ':' : propKey + ':'
            // 找出被选的规格值信息
            const fdDbVal = fdDbKey.prodPropValues.find(it => it.propValue === propVal)
            // 当前所选的规格值是否存在
            if (fdDbVal) {
              // 找出相应语言的规格值信息
              const fdPropVal = fdDbVal.prodPropValueLangList.find(it => it.lang === langItem)
              langProperties += fdPropVal ? fdPropVal.propValue + ';' : propVal + ';'
              langSkuName += fdPropVal ? fdPropVal.propValue + ' ' : propVal + ' '
            } else {
              langProperties += propVal + ';'
              langSkuName += propVal + ' '
            }
          } else {
            langProperties += propItem + ';'
            langSkuName += propVal + ' '
          }
        }
        // 去除';'
        langProperties = langProperties.substring(0, langProperties.length - 1)
        skuLangList.push({
          lang: langItem,
          prodName: curProdName + ' ' + langSkuName,
          properties: langProperties,
          skuId: sku.skuId || 0,
          skuName: langSkuName
        })
      }
    }
    if (skuList.length === 1 && !sku.properties) {
      for (const langItem of curLang) {
        skuLangList.push({
          lang: langItem,
          prodName: masterProd.prodName,
          properties: '',
          skuId: sku.skuId || 0,
          skuName: ''
        })
      }
    }
    sku.skuLangList = skuLangList
    // 修改时，将移除的门店库存相关列表合并回去
    if (sku.skuId && !dataForm.value.deliveryMode?.hasUserPickUp) {
      if (sku.filterList && sku.stockPointList?.length) {
        sku.stockPointList = sku.stockPointList.concat(sku.filterList)
        sku.filterList = []
      }
    }
    if (!(sku.stockPointList && sku.stockPointList.length)) {
      sku.stockPointList = [{
        stock: 0,
        stockPointId: stockPointInfo.value.defaultWarehouseId,
        stockPointType: 1,
        type: 0
      }]
    }
  })
  return skuList
}

</script>

<style lang="scss" scoped>
.post-product-page {
  color: #333;
  .prod-main-containers {
    display: block;
    width: 100%;
    padding: 15px;
    box-sizing: border-box;
    background: #fff;
    margin-bottom: 50px;

    // 步骤
    .post-step {
      display: flex;
      align-content: center;
      justify-content: space-between;
      .step-item {
        position: relative;
        flex: 1;
        .step-txt {
          display: block;
          font-size: 14px;
          text-align: center;
          box-sizing: border-box;
          background: #f9f9f9;
          padding: 12px 0;
        }
      }
      .step-item.active {
        .step-txt {
          color: #fff;
          background: #155bd4;
        }
      }
      // 右箭头
      .step-item:not(:last-child) {
        .step-txt {
          margin-right: 10px;
        }
        &::after {
          position: absolute;
          top: 0;
          right: 0;
          content: '';
          width: 0;
          height: 0;
          border-top: 20px solid transparent;
          border-left: 10px solid #f9f9f9;
          border-bottom: 20px solid transparent;
        }
      }
      .step-item.active:not(:last-child) {
        &::after {
          border-left: 10px solid #155bd4;
        }
      }
      // 左箭头
      .step-item:not(:first-child) {
        &::before {
          position: absolute;
          top: 0;
          left: 0;
          content: '';
          width: 0;
          height: 0;
          border-top: 20px solid transparent;
          border-left: 10px solid #fff;
          border-bottom: 20px solid transparent;
        }
      }
    }

    // 内容
    .prod-content {
      display: block;
      width: 100%;
      margin-top: 20px;
      // 公共
      & :deep(.prod-title-row) {
        display: block;
        width: 100%;
        background: #f9f9f9;
        font-size: 14px;
        color: #333;
        font-weight: bold;
        padding: 10px;
        margin-bottom: 20px;
      }
    }
  }

  // 底部固定操作栏
  .prod-footer {
    position: fixed;
    bottom: 0;
    width: calc(100vw - 250px);
    left: 250px;
    box-shadow: 0 -3px 5px #eee;
    z-index: 3;
    margin-top: 20px;
    margin-right: 20px;
    &.prod-footer-cls{
      width: calc(100vw - 120px);
      left: 120px;
    }
    .foot {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 10px 0;
      background: #fff;
      box-sizing: border-box;
      .inner {
        .default-btn.save-btn {
          border-color: #155bd4;
          color: #155bd4;
        }
      }
    }
  }
}

:deep(.el-form-item.is-error .el-input__inner) {
  border-color: #e4e7ed !important;
}
:deep(.el-form-item.is-error .el-input__validateIcon) {
  display: none;
}
</style>

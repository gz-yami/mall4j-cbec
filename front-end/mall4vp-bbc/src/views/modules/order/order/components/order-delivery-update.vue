<template>
  <el-dialog
    v-model="visible"
    :title="$t('order.orderShipping')"
    :close-on-click-modal="false"
    width="1045px"
  >
    <div class="mod-order-orderInfo">
      <el-table
        ref="multipleTableRef"
        :empty-text="$t('shop.noData')"
        :data="dataList"
        tooltip-effect="dark"
        style="width: 100%"
        header-cell-class-name="table-header"
        row-class-name="table-row-low"
        @selection-change="onSelectSome"
      >
        <el-table-column
          type="selection"
          width="55"
          :selectable="checkSelectable"
        />
        <el-table-column
          align="left"
          label-class-name="text-left"
          :label="$t('order.product')"
          fit
        >
          <template #default="scope">
            <div class="prod-name-con">
              <span class="prod-name">
                <span
                  v-if="scope.row.activityType===5"
                  class="gift-icon"
                >{{ $t("order.giveawayPord") }}</span>
                {{ scope.row.prodName }}
                <span
                  v-if="scope.row.skuName"
                  class="prod-sku"
                >{{ scope.row.skuName }}</span>
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="prodCount"
          :label="$t('order.canShipQuantity')"
          width="100"
        />
        <el-table-column
          align="center"
          prop="status"
          :label="$t('product.status')"
          show-overflow-tooltip
          width="100"
        >
          <template #default="scope">
            <template v-if="scope.row.platformInterventionStatus && scope.row.platformInterventionStatus!==-1">
              <span>
                {{ [
                  "",
                  $t('order.requestARefund'),
                  $t('order.requestARefund'),
                  $t('order.refundClosed'),
                  '',
                  $t('order.refundsuccessfully')
                ][scope.row.platformInterventionStatus] }}
              </span>
            </template>
            <template v-else>
              <span
                v-if="
                  scope.row.returnMoneySts !== null &&
                    scope.row.returnMoneySts < 6 &&
                    scope.row.returnMoneySts > 0
                "
                class="tag-text"
              >
                {{ scope.row.returnMoneySts === 5 ? $t("order.refundsuccessfully") : $t("order.refundApplicationInProgress") }}
              </span>
              <span
                v-else-if="scope.row.status !== 0 && scope.row.status !== -1"
                class="tag-text"
              >{{ $t("order.partialDelivery") }}</span>
              <span
                v-else-if="scope.row.status === 0"
                class="tag-text"
              >{{
                $t("order.shipmentCompleted")
              }}</span>
              <span
                v-else-if="scope.row.status === -1"
                class="tag-text"
              >{{ $t("order.toBeShipped") }}</span>
            </template>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="changeNum"
          :label="$t('order.numberOfShipments')"
          fit
        >
          <template #default="scope">
            <el-input-number
              v-model="scope.row.changeNum"
              :disabled="scope.row.prodCount <= 0"
              :precision="0"
              :min="0"
              :max="scope.row.prodCount"
              style="width:150px"
            />
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-form
      ref="dataFormRef"
      :model="dataForm"
      :inline="true"
      label-width="auto"
      @submit.prevent
      @keyup.enter="onSubmit()"
    >
      <div class="detail-cont">
        <div class="detail01">
          <div class="text-width">
            <div class="text-width-item">
              <div class="title">
                {{ $t("order.delType") }}：
              </div>
              <div class="text">
                {{
                  [$t("order.expressDelivery"),
                   $t("order.selfMention"),
                   $t("order.noNeedRequired"),
                   $t("order.sameCityDelivery")
                  ][initDeliveryType - 1] }}
              </div>
            </div>
            <div class="text-width-item text-width-item-addressee">
              <div class="title">
                {{ $t("publics.addressee") }}：
              </div>
              <div class="text">
                {{ dataForm.userAddrOrder.receiver }}
              </div>
            </div>
            <div class="text-width-item">
              <div class="title">
                {{ $t("distribution.phoneNum") }}：
              </div>
              <div class="text">
                {{ dataForm.userAddrOrder.mobile }}
              </div>
            </div>
            <div class="text-width-item addr-info">
              <div class="title">
                {{ $t("publics.deliveryAddr") }}：
              </div>
              <div
                class="text"
                :class="{'zh-text': $t('language') === 'zh_CN'}"
              >
                {{
                  handlerAddress(
                    dataForm.userAddrOrder.province,
                    dataForm.userAddrOrder.city,
                    dataForm.userAddrOrder.area,
                    dataForm.userAddrOrder.addr
                  )
                }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <el-form-item
        :label="$t('order.deliveryMethod') + '：'"
        style="margin-left: 20px"
      >
        <el-radio-group
          v-model="dataForm.tempDeliveryType"
          @change="clear()"
        >
          <el-radio
            :label="1"
            :disabled="dataForm.deliveryType === 4"
          >
            {{
              $t("order.selfConOrd")
            }}
          </el-radio>
          <el-radio
            :label="3"
            :disabled="dataForm.deliveryType === 4 || isDisableNoCourier"
          >
            {{
              $t("order.noNeedRequired")
            }}
          </el-radio>
          <!-- 同城配送 -->
          <!--          <el-radio-->
          <!--            :label="4"-->
          <!--            :disabled="dataForm.deliveryType !== 4"-->
          <!--          >-->
          <!--            {{-->
          <!--              $t("order.sameCityDelivery")-->
          <!--            }}-->
          <!--          </el-radio>-->
        </el-radio-group>
      </el-form-item>
      <div
        v-if="dataForm.tempDeliveryType === 1"
        class="number-info"
      >
        <!--配送方式 线上 线下-->
        <el-form-item
          v-if="dataForm.tempDeliveryType === 1"
          :label="$t('order.delType') + '：'"
          style="text-align: left;"
        >
          <el-radio-group v-model="dataForm.deliveryType">
            <!-- 线上发货 -->
            <!--            <el-radio-->
            <!--              :label="0"-->
            <!--              :disabled="isPrint === 0"-->
            <!--            >-->
            <!--              {{-->
            <!--                $t("order.onlineDelivery")-->
            <!--              }}-->
            <!--            </el-radio>-->
            <el-radio
              :label="1"
            >
              {{
                $t("order.selfDelivery")
              }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <!--自行联系快递-->
        <div v-if="dataForm.deliveryType === 1">
          <el-form-item
            :label="$t('order.courierCompany') + '：'"
            class="height"
          >
            <select-lazy
              v-model="dataForm.dvyId"
              :sel-style="{width:'300px'}"
              :place-tips="$t('tip.select')"
            />
          </el-form-item>
          <el-form-item
            :label="$t('order.trackingNumber') + '：'"
            prop="dvyFlowId"
          >
            <el-input
              v-model="dataForm.dvyFlowId"
              width="80"
              style="width: 300px"
              maxlength="20"
              show-word-limit
              :placeholder="$t('order.trackingNumber')"
            />
          </el-form-item>
        </div>
        <!--线上发货-->
        <!--        <div v-if="dataForm.deliveryType === 0">-->
        <!--          <el-form-item-->
        <!--            :label="$t('order.courierCompany') + '：'"-->
        <!--            prop="deliveryCompanyType"-->
        <!--          >-->
        <!--            <div class="part-form-delivery">-->
        <!--              <el-select-->
        <!--                v-model="dataForm.deliveryCompanyType"-->
        <!--                :placeholder="$t('tip.select')"-->
        <!--                @change="onDeliveryCompanyTypeChange"-->
        <!--              >-->
        <!--                <el-option-->
        <!--                  v-for="item in outletConfigList"-->
        <!--                  :key="item.deliveryCompanyType"-->
        <!--                  :label="item.deliveryCompanyName"-->
        <!--                  :value="item.deliveryCompanyType"-->
        <!--                />-->
        <!--              </el-select>-->
        <!--              <div-->
        <!--                v-if="deliveryCompanyType.isConfig === 0"-->
        <!--                class="lb"-->
        <!--              >-->
        <!--                <el-button-->
        <!--                  type="primary"-->
        <!--                  link-->
        <!--                  @click="onToConfigOutlet"-->
        <!--                >-->
        <!--                  {{ $t('order.toOpen') }}-->
        <!--                </el-button>-->
        <!--                <span-->
        <!--                  class="vertical"-->
        <!--                />-->
        <!--                <el-button-->
        <!--                  type="primary"-->
        <!--                  link-->
        <!--                  @click="onGetOutletConfigList"-->
        <!--                >-->
        <!--                  {{ $t('admin.refresh') }}-->
        <!--                </el-button>-->
        <!--              </div>-->
        <!--            </div>-->
        <!--          </el-form-item>-->
        <!--          <el-form-item-->
        <!--            v-if="deliveryCompanyType.isConfig === 1"-->
        <!--            :label="$t('order.deliveryAddress') + '：'"-->
        <!--            prop="outletConfigId"-->
        <!--          >-->
        <!--            <el-select-->
        <!--              v-model="dataForm.outletConfigId"-->
        <!--              :placeholder="$t('tip.select')"-->
        <!--              style="width: 330px"-->
        <!--            >-->
        <!--              <el-option-->
        <!--                v-for="item in deliveryCompanyType.shopAddrList"-->
        <!--                :key="item.outletConfigId"-->
        <!--                :label="item.printAddr"-->
        <!--                :value="item.outletConfigId"-->
        <!--              />-->
        <!--            </el-select>-->
        <!--          </el-form-item>-->
        <!--          <el-form-item-->
        <!--            v-if="deliveryCompanyType.isConfig === 1"-->
        <!--            :label="$t('order.deliveryPrintType') + '：'"-->
        <!--          >-->
        <!--            <el-radio-group v-model="isPrint">-->
        <!--              <el-radio-->
        <!--                :label="1"-->
        <!--              >-->
        <!--                {{-->
        <!--                  $t("order.printInfo")-->
        <!--                }}-->
        <!--              </el-radio>-->
        <!--            </el-radio-group>-->
        <!--          </el-form-item>-->
        <!--          <el-form-item-->
        <!--            v-if="deliveryCompanyType.isConfig === 1"-->
        <!--            :label="$t('order.printer') + '：'"-->
        <!--            prop="printerId"-->
        <!--          >-->
        <!--            <div class="part-form-delivery">-->
        <!--              <el-select-->
        <!--                v-model="dataForm.printerId"-->
        <!--                :placeholder="$t('tip.select')"-->
        <!--              >-->
        <!--                <el-option-->
        <!--                  v-for="item in printerList"-->
        <!--                  :key="item.printerId"-->
        <!--                  :label="item.printerName"-->
        <!--                  :value="item.printerId"-->
        <!--                />-->
        <!--              </el-select>-->
        <!--              <div-->
        <!--                class="lb"-->
        <!--              >-->
        <!--                <el-button-->
        <!--                  type="primary"-->
        <!--                  link-->
        <!--                  @click="onToConfigPrinter"-->
        <!--                >-->
        <!--                  {{ $t('admin.newConstruction') }}-->
        <!--                </el-button>-->
        <!--                <span-->
        <!--                  class="vertical"-->
        <!--                />-->
        <!--                <el-button-->
        <!--                  type="primary"-->
        <!--                  link-->
        <!--                  @click="onGetPrinterList"-->
        <!--                >-->
        <!--                  {{ $t('admin.refresh') }}-->
        <!--                </el-button>-->
        <!--              </div>-->
        <!--            </div>-->
        <!--          </el-form-item>-->
        <!--        </div>-->
      </div>
    </el-form>
    <template #footer>
      <div>
        <div
          class="default-btn"
          @click="visible = false"
        >
          {{ $t("crud.filter.cancelBtn") }}
        </div>
        <div
          class="default-btn primary-btn"
          @click="submitProds()"
        >
          {{ $t("crud.filter.submitBtn") }}
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { handlerAddress } from '@/utils/index.js'
import { ElMessage } from 'element-plus'
const emit = defineEmits(['refreshOrderDeliveryUpdate'])
const visible = ref(false)
const dataForm = reactive({
  dvyId: '',
  userAddrOrder: {},
  dvyFlowId: null,
  dvyNames: [],
  orderNumber: 0,
  deliveryType: 1,
  tempDeliveryType: 1,
  outletConfigId: null,
  printerId: null,
  deliveryCompanyType: null
})
const deliveryCompanyType = ref({
  deliveryCompanyType: null,
  deliveryCompanyName: null,
  isConfig: null,
  isDefault: null,
  shopAddrList: []
})
const initDeliveryType = ref(1)
const dataList = ref([])
const selectOrderItems = ref([])
const isSubmit = ref(false)
const isTrue = ref(false)
const errorInfo = ref('')
const orderNumber = ref(0)
const freightAmount = ref(0)
// 是否禁用"无需快递"选项
const isDisableNoCourier = ref(false)

watch(
  () => dataList.value,
  () => {
    getNoDeliverySts()
  },
  { deep: true }
)

/**
 * 获取数据列表
 */
const init = (order) => {
  dataForm.deliveryCompanyType = null
  dataForm.printerId = null
  deliveryCompanyType.value.deliveryCompanyType = null
  deliveryCompanyType.value.deliveryCompanyName = null
  deliveryCompanyType.value.isConfig = null
  deliveryCompanyType.value.isDefault = null
  deliveryCompanyType.value.shopAddrList = []
  isSubmit.value = false
  dataForm.userAddrOrder = {}
  visible.value = true
  orderNumber.value = order.orderNumber
  clear()
  // onGetQuick100()
  // onGetOutletConfigList()
  // onGetPrinterList()
  dataForm.dvyId = ''
  // 修改
  http({
    url: http.adornUrl(`/order/delivery/getOrderItemUnDelivery/${order.orderNumber}`),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    dataForm.userAddrOrder = data.userAddrOrder
    // 过滤掉组合商品的主商品
    data.orderItems = data.orderItems.filter(i => i.mold !== 2)
    dataList.value = data.orderItems
    dataForm.deliveryType = parseInt(data.dvyType)
    if (data.dvyType === 2 || data.dvyType === 4) {
      dataForm.deliveryType = parseInt(data.dvyType)
    } else {
      dataForm.deliveryType = isPrint.value === 1 ? 0 : parseInt(data.dvyType)
    }
    dataForm.tempDeliveryType = dataForm.deliveryType === 0 || dataForm.deliveryType === 1 ? 1 : dataForm.deliveryType
    initDeliveryType.value = dataForm.tempDeliveryType
    dataList.value?.forEach(element => {
      element.changeNum = element.prodCount
    })
  })
}
// const onDeliveryCompanyTypeChange = (val) => {
//   outletConfigList.value.forEach(el => {
//     if (el.deliveryCompanyType === val) {
//       deliveryCompanyType.value = el
//     }
//   })
//   dataForm.outletConfigId = null
// }
defineExpose({ init })

/**
 * 修改状态
 */
const onSelectSome = (val) => {
  selectOrderItems.value = val
  getNoDeliverySts()
}

/**
 * 设置无需快递按钮状态
 */
const getNoDeliverySts = () => {
  let flag = false
  const tempList = dataList.value.filter(el => (el.returnMoneySts && el.returnMoneySts != 5) || !el.returnMoneySts)
  if (tempList.length !== selectOrderItems.value.length) {
    flag = true
  }
  for (let i = 0; i < tempList.length; i++) {
    const item = tempList[i]
    if (item.status != -1 || item.changeNum != item.prodCount) {
      flag = true
      break
    }
  }
  if (flag && dataForm.deliveryType === 3) {
    dataForm.deliveryType = isPrint.value === 1 ? 0 : parseInt(dataForm.deliveryType)
    dataForm.tempDeliveryType = dataForm.deliveryType === 0 || dataForm.deliveryType === 1 ? 1 : dataForm.deliveryType
  }
  isDisableNoCourier.value = flag
}

/**
 * 禁用多选按钮
 */
const checkSelectable = (row) => {
  return (row.status !== 0 && (!row.returnMoneySts || row.returnMoneySts < 0 || row.returnMoneySts > 5)) && ![1, 2, 5].includes(row.platformInterventionStatus)
}

const clear = () => {
  if (dataForm.tempDeliveryType === 3) {
    dataForm.deliveryType = 3
  } else if (dataForm.tempDeliveryType === 4) {
    dataForm.deliveryType = 4
  } else {
    if (isPrint.value === 1) {
      dataForm.deliveryType = 0
    } else {
      dataForm.deliveryType = 1
    }
  }
  dataForm.dvyFlowId = null
  dataForm.dvyId = ''
}

/**
 * 校验数量
 */
const check = () => {
  isTrue.value = false
  if (selectOrderItems.value.length <= 0) {
    isTrue.value = true
    errorInfo.value = $t('order.seleOrd')
    return true
  }
  if (dataForm.deliveryType === 0) {
    if (dataForm.deliveryCompanyType && deliveryCompanyType.value.isConfig === 0) {
      isTrue.value = true
      errorInfo.value = $t('order.notLogisticsTips')
      return true
    }
    if (!dataForm.deliveryCompanyType) {
      isTrue.value = true
      errorInfo.value = $t('order.seleCouCom')
      return true
    }
    if (!dataForm.outletConfigId) {
      isTrue.value = true
      errorInfo.value = $t('order.selectAddrTips')
      return true
    }
    if (!dataForm.printerId) {
      isTrue.value = true
      errorInfo.value = $t('order.selectPrinterTips')
      return true
    }
  }
  selectOrderItems.value?.forEach(element => {
    if (element.prodCount <= 0 || element.prodCount < element.changeNum || element.changeNum <= 0) {
      isTrue.value = true
      errorInfo.value = element.prodName + $t('order.numNotZero') + '！'
      return true
    }
    if (element.changeNum <= 0) {
      isTrue.value = true
      errorInfo.value = element.prodName + $t('order.numNotZero') + '！'
      return true
    }
    if (element.changeNum == null) {
      isTrue.value = true
      errorInfo.value = element.prodName + $t('order.fhNumNotZero') + '！'
      return true
    }
  })
  if (dataForm.dvyId === '' && dataForm.deliveryType === 1) {
    isTrue.value = true
    errorInfo.value = $t('order.seleCouCom')
    return true
  }
  if ((dataForm.dvyFlowId === 0 || !dataForm.dvyFlowId) && dataForm.deliveryType === 1) {
    isTrue.value = true
    errorInfo.value = $t('order.entCouNum')
    return true
  }
  const reg = /^[a-zA-Z0-9]{4,30}$/
  if (!reg.test(dataForm.dvyFlowId) && dataForm.deliveryType === 1) {
    isTrue.value = true
    errorInfo.value = $t('order.checkDvyId')
    return true
  }
}

/**
 * 确定事件
 */
const submitProds = () => {
  if (!dataForm.deliveryType && dataForm.deliveryType !== 0) {
    ElMessage.error($t('order.pledSeleMet'))
    return
  }
  check()
  if (isTrue.value) {
    errorMsg(errorInfo.value)
    return
  }
  if (isSubmit.value) {
    return
  }
  isSubmit.value = true
  http({
    url: http.adornUrl('/order/delivery/orderItemsDelivery'),
    method: 'put',
    data: http.adornData({
      orderNumber: orderNumber.value,
      dvyId: dataForm.dvyId,
      dvyFlowId: dataForm.dvyFlowId,
      freightAmount: freightAmount.value,
      deliveryType: dataForm.tempDeliveryType !== 1 ? dataForm.tempDeliveryType : dataForm.deliveryType,
      outletConfigId: dataForm.outletConfigId,
      printerId: dataForm.printerId,
      deliveryCompanyType: dataForm.deliveryCompanyType,
      selectOrderItems: selectOrderItems.value
    })
  }).then(() => {
    ElMessage({
      message: $t('publics.operation'),
      type: 'success',
      duration: 1500,
      onClose: () => {
        visible.value = false
        emit('refreshOrderDeliveryUpdate')
      }
    })
  }).catch(() => {
    isSubmit.value = false
  })
}
// 获取网点列表
// const outletConfigList = ref([])
// const onGetOutletConfigList = () => {
//   http({
//     url: http.adornUrl('/shop/outletConfig/listOutletConfig'),
//     method: 'get',
//     params: http.adornParams()
//   }).then(({ data }) => {
//     outletConfigList.value = data
//     if (deliveryCompanyType.value.deliveryCompanyType !== null) {
//       data.forEach(el => {
//         if (el.deliveryCompanyType === deliveryCompanyType.value.deliveryCompanyType) {
//           deliveryCompanyType.value = el
//           dataForm.deliveryCompanyType = el.deliveryCompanyType
//           el.shopAddrList?.forEach(val => {
//             if (val.isDefault === 1) {
//               dataForm.outletConfigId = val.outletConfigId
//             }
//           })
//         }
//       })
//       return
//     }
//     data.forEach(el => {
//       if (el.isDefault === 1) {
//         // 设置选中默认配置
//         deliveryCompanyType.value = el
//         dataForm.deliveryCompanyType = el.deliveryCompanyType
//         el.shopAddrList.forEach(val => {
//           if (val.isDefault === 1) {
//             dataForm.outletConfigId = val.outletConfigId
//           }
//         })
//       }
//     })
//   })
// }

// const printerList = ref([])
// // 获取打印机列表
// const onGetPrinterList = () => {
//   http({
//     url: http.adornUrl('/shop/printer/listPrinter'),
//     method: 'get',
//     params: http.adornParams()
//   }).then(({ data }) => {
//     printerList.value = data
//     data.forEach(el => {
//       if (el.isDefault === 1) {
//         // 设置选中默认配置
//         dataForm.printerId = el.printerId
//       }
//     })
//   })
// }

// 获取快递100配置
const isPrint = ref(0)
// const onGetQuick100 = () => {
//   http({
//     url: http.adornUrl('/sys/shopConfig/quick100'),
//     method: 'get',
//     params: http.adornParams()
//   }).then((res) => {
//     isPrint.value = res.data ? 1 : 0
//   })
// }

// 去开通网点
// const onToConfigOutlet = () => {
//   window.open('/order/delivery/outlet-config/index', '_blank', 'noopener,noreferrer')
// }

// 去配置打印机
// const onToConfigPrinter = () => {
//   window.open('/order/delivery/printer/index', '_blank', 'noopener,noreferrer')
// }

const errorMsg = (message) => {
  ElMessage({
    message,
    type: 'error',
    duration: 1500
  })
}
</script>

<style lang="scss" scoped>
.mod-order-orderInfo {
  height: 100%;
  width: 100%;
  font: 14px Arial, "PingFang SC", "Hiragino Sans GB", STHeiti,
  "Microsoft YaHei", "WenQuanYi Micro Hei", sans-serif;
  color: #495060;
}
.prod-name-con {
  display: flex;
  max-width: 100%;
  align-items: center;
}
/* 赠品标签 */
.prod-name-con .gift-icon {
  display: inline-block;
  font-size: 12px;
  line-height: 1em;
  color: #fff;
  background: #e43130;
  padding: 3px;
  border-radius: 3px;
  box-sizing: border-box;
  margin-right: 5px;
}
.prod-name-con .prod-name {
  flex: 1;
  max-width: 80%;
  text-overflow: ellipsis;
  -o-text-overflow: ellipsis;
  word-break: break-word;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.prod-name-con .prod-sku {
  color: #999;
  margin-left: 2%;
}
.detail01 {
  min-height: 80px;
  width: 100%;
  background: #F7F8FA;
  margin-top: 15px;
  padding: 20px 20px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
}
.addr-info {
  line-height: 17px;
}
.text-width {
  width: 100%;
  display: flex;
  justify-content: flex-start;
  align-items: center;
}
.text-width .text-width-item {
  display: flex;
  margin-right: 60px;
}
.text-width .text-width-item .title {
  white-space: nowrap;
  color: #999999;
  display: flex;
  align-items: center;
}
.text-width .text-width-item .text {
  flex: 1 0 auto;
  color: #333333;
  max-width: 95px;
  word-break: break-word;
}

.text-width .text-width-item-addressee .text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.addr-info .zh-text {
  max-width: 260px !important;
}
//.part-form-delivery {
//  display:flex;
//  .lb {
//    display: flex;
//    align-items: center;
//
//    margin-left: 20px;
//
//    .vertical {
//      display: inline-block;
//      width: 1px;
//      height: 10px;
//      background: #000;
//      margin: 0 6px;
//    }
//  }
//}
.number-info:deep(.el-form-item) {
  display: flex;
  margin-bottom: 20px;
  margin-left: 20px;
}

</style>

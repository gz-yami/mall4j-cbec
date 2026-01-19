<template>
  <el-dialog
    v-model="visible"
    :title="$t('order.modifyOrderAmount')"
    :close-on-click-modal="false"
    width="60%"
  >
    <div class="mod-order-orderInfo">
      <el-alert
        :title="$t('order.modificatioAmount')"
        type="warning"
        show-icon
      />
      <div class="main">
        <div class="content">
          <div class="prod-tit prod-tit-item">
            <el-row
              style="width:100%"
              justify="center"
            >
              <el-col :span="19">
                <el-col
                  :span="5"
                  header-align="center"
                >
                  <span class="item">{{ $t("order.product") }}</span>
                </el-col>
                <el-col
                  :span="3"
                  header-align="center"
                >
                  <span class="item">{{ $t("order.unitPrice") }}</span>
                </el-col>
                <el-col
                  :span="3"
                  header-align="center"
                >
                  <span class="item">{{ $t("order.quantity") }}</span>
                </el-col>
                <el-col
                  :span="3"
                  header-align="center"
                >
                  <span class="item">{{ $t("order.totalPrice") }}</span>
                </el-col>
                <el-col
                  :span="3"
                  header-align="center"
                >
                  <span class="item">{{ $t("order.totalDiscount") }}</span>
                </el-col>
                <el-col
                  :span="6"
                  header-align="center"
                >
                  <span class="item">{{ $t("order.userPayunt") }}</span>
                </el-col>
              </el-col>
              <el-col :span="5">
                <el-col
                  header-align="center"
                >
                  <span class="item product">{{ $t("order.shippingAmunt") }}</span>
                </el-col>
              </el-col>
            </el-row>
          </div>
          <div class="prod">
            <div class="prod-cont prod-bottom">
              <el-row style="width:100%;align-items: center;">
                <el-col :span="19">
                  <el-col
                    v-for="(orderItem,index) in dataList"
                    :key="index"
                    class="prod"
                  >
                    <el-col class="prod-cont-item">
                      <el-row
                        class="prod-cont-row"
                        style="width:100%"
                      >
                        <el-col
                          :span="5"
                          style="height: 100%;"
                        >
                          <div class="item">
                            <span class="prod-name">{{ orderItem.prodName }}</span>
                          </div>
                        </el-col>
                        <el-col
                          :span="3"
                          style="height: 100%;"
                        >
                          <div class="item line">
                            <span>{{ orderItem.price }}</span>
                          </div>
                        </el-col>
                        <el-col
                          :span="3"
                          style="height: 100%;"
                        >
                          <div class="item line">
                            <span>{{ orderItem.prodCount }}</span>
                          </div>
                        </el-col>
                        <el-col
                          :span="3"
                          style="height: 100%;"
                        >
                          <div class="item line">
                            <span>{{ orderItem.productTotalAmount }}</span>
                          </div>
                        </el-col>
                        <el-col
                          :span="3"
                          style="height: 100%;"
                        >
                          <div class="item line">
                            <span>{{ orderItem.shareReduceAmount }}</span>
                          </div>
                        </el-col>
                        <el-col
                          :span="6"
                          style="height: 100%;"
                        >
                          <div class="item line">
                            <el-input-number
                              v-model="orderItem.actualTotalAmount"
                              :precision="2"
                              :min="0"
                              :disabled="orderItem.scoreOrder"
                              @change="setChageAmount(index)"
                              @blur="setChageAmount(index)"
                            />
                          </div>
                        </el-col>
                      </el-row>
                    </el-col>
                  </el-col>
                </el-col>
                <el-col
                  class="prod-cont-ca"
                  :span="5"
                  style="height: 100%;"
                >
                  <div class="item line">
                    <el-input-number
                      v-model="freightAmount"
                      :disabled="orderMold === 1 || dvyType === 2 || platformFreeFreightAmount"
                      :precision="2"
                      :min="0"
                      @blur="freightAmountInput"
                    />
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
        </div>
      </div>
    </div>
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
import { ElMessage } from 'element-plus'
import Big from 'big.js'

const emit = defineEmits(['refreshOrderAmountUpdate'])
const visible = ref(false)
const dataList = ref([])
const isTrue = ref(false)
const errorInfo = ref('')
const orderId = ref(0)
const orderNumber = ref(0)
const freightAmount = ref(0)
const freightDisabled = ref(false)
const lossAmount = ref(0)
const dvyType = ref(1)
const orderMold = ref(0)
const freeTransfee = ref(0)
const dataListLoading = ref(false)
const isOnce = ref(true)
const platformFreeFreightAmount = ref(0)

/**
 * 获取数据列表
 */
const init = (order) => {
  const orderInfo = Object.assign({}, order)
  visible.value = true
  dataListLoading.value = true
  dataList.value = orderInfo.orderItems
  orderId.value = orderInfo.orderId
  orderNumber.value = orderInfo.orderNumber
  freightAmount.value = orderInfo.freightAmount - orderInfo.platformFreeFreightAmount
  platformFreeFreightAmount.value = orderInfo.platformFreeFreightAmount
  freightDisabled.value = orderInfo.freightAmount && orderInfo.freightAmount + orderInfo.platformFreeFreightAmount === 0
  orderMold.value = orderInfo.orderMold
  freeTransfee.value = orderInfo.freeTransfee
  dvyType.value = orderInfo.dvyType
  lossAmount.value = 0
  dataList.value?.forEach(element => {
    element.chageAmount = 0.00
    element.lossAmount = 0.00
    const shareReduceAmount = element.shareReduce
    const actualTotalAmount = element.actualTotal
    element.shareReduceAmount = shareReduceAmount
    element.actualTotalAmount = actualTotalAmount
    const actual = new Big(element.actualTotal)
    element.amount = actual.plus(element.shareReduce).round(2)
    element.scoreOrder = element.actualTotalAmount < 0.01 && element.useScore > 0
  })
  dataListLoading.value = false
}
defineExpose({ init })

/**
 * 修改实际金额，改变订单金额
 */
const setChageAmount = (index) => {
  const item = dataList.value[index]
  if (!item.actualTotalAmount) {
    item.actualTotalAmount = 0
  }
  const actualTotalAmount = new Big(item.actualTotalAmount)
  const actualTotal = new Big(item.actualTotal)
  // 变化金额 = 原实际金额 - 实际金额
  const chageAmount = actualTotal.minus(actualTotalAmount)
  item.chageAmount = chageAmount.round(2)
  // 总优惠金额 = 变化金额 + 原总优惠金额
  item.shareReduceAmount = chageAmount.plus(item.shareReduce)
  // 实际金额 = 原实际金额 - 变化金额
  item.actualTotalAmount = actualTotal.minus(item.chageAmount)
  // 店铺预计收入金额 = 实际金额 + 平台优惠金额
  item.amount = item.actualTotalAmount
  dataList.value[index] = Object.assign({}, item)
}

/**
 * 校验金额
 */
const check = () => {
  isTrue.value = false
  dataList.value?.forEach(element => {
    // 如果金额小于0.01 支付积分大于0则为纯积分支付 此时不进行金额判断
    if (element.actualTotalAmount < 0.01 && element.useScore > 0) {
      return false
    }
    if (element.actualTotalAmount <= 0) {
      isTrue.value = true
      errorInfo.value = $t('order.theOraterThan0')
      return true
    }
  })
}

/**
 * 确定事件
 */
const submitProds = () => {
  if (!isOnce.value) {
    return
  }
  isOnce.value = false
  check()
  if (isTrue.value) {
    errorMsg(errorInfo.value)
    isOnce.value = true
    return
  }
  http({
    url: http.adornUrl('/order/order/changeAmount'),
    method: 'put',
    data: http.adornData({
      orderId: orderId.value,
      orderItems: dataList.value,
      orderNumber: orderNumber.value,
      freightAmount: freightAmount.value
    })
  }).then(() => {
    ElMessage({
      message: $t('order.modifyAmountSuccess'),
      type: 'success',
      duration: 1500,
      onClose: () => {
        emit('refreshOrderAmountUpdate')
        visible.value = false
        setTimeout(() => {
          isOnce.value = true
        }, 200)
      }
    })
  }).catch(() => {
    isOnce.value = true
  })
}

/**
 * 金额输入事件
 */
const freightAmountInput = () => {
  if (freightAmount.value === null || freightAmount.value === undefined) {
    freightAmount.value = 0
  }
}

const errorMsg = (message) => {
  ElMessage({
    message,
    type: 'error',
    duration: 1500
  })
}
</script>

<style scoped lang="scss">
.mod-order-orderInfo {
  height: 100%;
  width: 100%;
  font: 14px Arial, "PingFang SC", "Hiragino Sans GB", STHeiti,
  "Microsoft YaHei", "WenQuanYi Micro Hei", sans-serif;
  color: #495060;
}

// 列表头部
.prod-tit {
  :deep(.el-row){
    display: flex;
    flex-wrap: nowrap;
    background: #f7f8fa;
    height: 50px;
    line-height: 50px;
    border: 1px solid #ebedf0;
    .el-col {
      color: #495060;
      display: flex;
      flex-wrap: nowrap;
      .el-col {
        display: flex;
        justify-content: center;
        &:first-child {
          width: 20%;
        }
      }
    }
  }
}

// 列表内容区域
.prod-cont {
  & > :deep(.el-row) {
    border: 1px solid #ebedf0;
    border-top: 1px solid transparent;
    .el-row{
      border-right: 1px solid #ebedf0;
    }
  }
  :deep(.el-row){
    .el-col {
      .item {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 10px;
        white-space: pre-wrap;
        word-wrap: break-word;
      }
    }
  }
  & > .el-row:first-child .el-row > :deep(.el-col:last-child),
  & > .el-row > :deep(.el-col:last-child) {
    .el-input-number {
      width: 100%;
      max-width: 150px ;
    }
  }
  .prod-cont-row {
    align-items: center;
    .prod-name {
      width: 100%;
      text-align: center;
    }
  }
}
</style>

<template>
  <el-dialog
    v-model="visible"
    :title="order.orderMold === 1 ? $t('order.orderWriteOff'):$t('order.orderPickupS')"
    :close-on-click-modal="false"
    width="60%"
  >
    <div class="mod-order-orderInfo">
      <el-table
        ref="multipleTableRef"
        :empty-text="$t('shop.noData')"
        :data="dataList.filter(item => item.mold !== 2)"
        tooltip-effect="dark"
        style="width: 100%"
      >
        <el-table-column
          prop="changeNum"
          :label="$t('product.pic')"
          fit
          width="100"
        >
          <template #default="scope">
            <ImgShow
              :src="scope.row.pic"
              :img-style="{width:'50px'}"
            />
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('product.prodName')"
          fit
        >
          <template #default="scope">
            {{ scope.row.prodName }}
            <div
              v-if="
                scope.row.returnMoneySts !== null &&
                  scope.row.returnMoneySts > 0 &&
                  scope.row.returnMoneySts < 6
              "
              class="order-status"
            >
              {{ $t("order.reimburse") }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="prodCount"
          :label="$t('order.amountOfGoods')"
          width="100"
        />
        <el-table-column
          prop="status"
          :label="$t('product.status')"
          show-overflow-tooltip
          width="250"
        >
          <template #default="scope">
            <div v-if="order.orderMold !== 1">
              <span
                v-if="
                  scope.row.returnMoneySts !== null &&
                    scope.row.returnMoneySts < 6 &&
                    scope.row.returnMoneySts > 0
                "
                class="tag-text"
              >
                {{
                  [
                    "",
                    $t("order.buyerApplication"),
                    $t("order.sellerAccepts"),
                    $t("order.buyShipment"),
                    $t("order.sellerReceipt"),
                    $t("order.refundsuccessfully"),
                  ][scope.row.returnMoneySts]
                }}
              </span>
              <span
                v-else-if="scope.row.status !== 0 && scope.row.status !== -1"
                class="tag-text"
              >{{ $t("order.pendingPickUp") }}
              </span>
              <span
                v-else-if="scope.row.status === 0"
                class="tag-text"
              >{{
                $t("order.shipmentCompleted")
              }}</span>
              <span
                v-else-if="scope.row.status === -1"
                class="tag-text"
              >{{ $t("order.toBeShipped") }}
              </span>
            </div>
            <div v-else>
              <span
                v-if="order.status === 3"
                class="tag-text"
              >{{
                $t("order.waitWriteOff")
              }}</span>
              <span
                v-else
                class="tag-text"
              >{{
                $t("order.commoditful")
              }}</span>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      label-width="100px"
      @submit.prevent
      @keyup.enter="onSubmit()"
    >
      <div class="detail-cont">
        <div class="detail01">
          <div
            v-if="order.orderMold !== 1"
            class="text-width"
          >
            <el-form-item :label="$t('order.shippingInformation') + ':'">
              <span>{{ $t("order.delType") }}：{{
                $t("order.selfMention")
              }}</span>
            </el-form-item>
            <el-form-item>
              <span>{{ $t("publics.addressee") }}：{{
                dataForm.stationUserName
              }}</span>
            </el-form-item>
            <el-form-item>
              <span>{{ $t("publics.mobilePhone") }}：{{
                dataForm.stationUserMobile
              }}</span>
            </el-form-item>
            <el-form-item>
              <span>{{ $t("station.stationAddr") }}：{{
                dataForm.stationAddress
              }}</span>
            </el-form-item>
          </div>
        </div>
      </div>
    </el-form>
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      label-width="auto"
      class="code-input-box"
      @submit.prevent
      @keyup.enter="submitProds()"
    >
      <div class="detail-cont">
        <el-form-item
          :label="$t('order.selfMentionCode') + '：'"
          prop="code"
        >
          <el-input
            v-model="code"
            :placeholder="$t('order.pleEntPickupCode')"
            class="code-int"
            @change="checkStationCode"
          />
          <el-alert
            :title="order.orderMold === 1 ?
              $t('order.doNotEntPickupCode2')
              :$t('order.doNotEntPickupCode1')"
            type="warning"
            :closable="false"
            class="code-int-tips"
          />
        </el-form-item>
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
          {{ order.orderMold === 1 ? $t("order.Writeoffs") : $t("order.pickUp") }}
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ElMessage } from 'element-plus'

const emit = defineEmits(['refreshOrderStationDataList', 'refreshOrderStationDataList'])
const visible = ref(false)
const dataForm = ref({})
const code = ref('')
const order = ref({})
const isStationCode = ref(0)
const dataList = ref([])
let isSubmit = null

/**
 * 获取数据列表
 */
const init = (orderParam) => {
  isSubmit = false
  visible.value = true
  code.value = ''
  isStationCode.value = 0
  order.value = orderParam
  clear()
  if (orderParam.orderMold !== 1) {
    // 修改
    http({
      url: http.adornUrl('/admin/station/getOrderItemAndStationInfo'),
      method: 'get',
      params: http.adornParams({
        orderNumber: orderParam.orderNumber
      })
    }).then(({ data }) => {
      dataForm.value = data
      dataList.value = data.orderItems
    })
  } else {
    // 修改
    http({
      url: http.adornUrl('/order/order/orderInfo/' + orderParam.orderNumber),
      method: 'get'
    }).then(({ data }) => {
      dataForm.value = data
      dataList.value = data.orderItems
    })
  }
}
defineExpose({ init })

const clear = () => {
  dataForm.value.dvyFlowId = 0
  dataForm.value.dvyId = ''
}
/**
 * 校验自提码
 */
const checkStationCode = () => {
  if (code.value === dataForm.value.stationCode || !code.value) {
    isStationCode.value = 1
  } else {
    isStationCode.value = 2
  }
}
/**
 * 提货事件
 */
const submitProds = () => {
  code.value = code.value.trim()
  if (isStationCode.value === 2 && order.value.orderMold !== 1) {
    errorMsg($t('order.pickupCodeError'))
    return
  }
  if (isSubmit) {
    return
  }
  isSubmit = true
  if (order.value.orderMold !== 1) {
    http({
      url: http.adornUrl('/admin/station/changeStatusAndOrderStation'),
      method: 'put',
      data: http.adornData({
        orderNumber: dataForm.value.orderNumber,
        stationId: dataForm.value.stationId
      })
    }).then(() => {
      ElMessage({
        message: $t('publics.operation'),
        type: 'success',
        duration: 1500,
        onClose: () => {
          visible.value = false
          emit('refreshOrderStationDataList')
        }
      })
    }).catch(() => {
      isSubmit = false
    })
  } else {
    virtualProdWriteOff()
  }
}
const virtualProdWriteOff = () => {
  http({
    url: http.adornUrl('/admin/station/orderWriteOffByOrderNumber'),
    method: 'put',
    data: http.adornData({
      orderNumber: dataForm.value.orderNumber,
      writeOffCode: code.value
    })
  }).then(() => {
    ElMessage({
      message: $t('publics.operation'),
      type: 'success',
      duration: 1500,
      onClose: () => {
        visible.value = false
        emit('refreshOrderStationDataList')
      }
    })
  }).catch(() => {
    isSubmit = false
  })
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
.code-input-box {
  margin-top: 20px;
}
.code-input-box .code-int {
  max-width: 60%;
}
.code-input-box .code-int-tips {
  max-width: 60%;
  line-height: 1em;
  margin-top: 5px;
}
.code-input-box .code-int-tips :deep(.el-alert__content) {
  padding: 0;
}
</style>

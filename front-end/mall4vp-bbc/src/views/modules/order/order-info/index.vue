<template>
  <div class="page-order-info">
    <div class="new-page-title">
      <div class="line" />
      <div class="text">
        {{
          !dataForm.orderNumber
            ? $t('crud.addTitle')
            : $t('order.orderDetail')
        }}
      </div>
    </div>
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      label-width="auto"
      @submit.prevent
      @keyup.enter="onSubmit()"
    >
      <div class="mod-order-orderInfo">
        <div class="content">
          <div class="order-number">
            <div class="number">
              <span class="text">{{ $t("order.number") }}:</span>
              {{ dataForm.orderNumber }}
            </div>
            <div class="time">
              <span class="text">{{ $t("order.createTime") }}:</span>
              {{ dataForm.createTime }}
            </div>
            <div class="type">
              {{
                [
                  $t("order.normalOrder"),
                  $t("order.groupPurchaseOrder"),
                  $t("order.spikeOrder"),
                ][dataForm.orderType]
              }}
            </div>
          </div>
          <div class="order-state">
            <div class="state-box">
              <div class="state">
                {{
                  [
                    $t("order.waitingFosPayment"),
                    $t("order.waitiooShip"),
                    $t("order.waitingFeGoods"),
                    $t("order.waitingtion"),
                    $t("order.commoditful"),
                    $t("order.commodityFailed"),
                    $t("order.commodited"),
                  ][dataForm.status - 1]
                }}
              </div>
              <div class="state-des">
                <div v-if="dataForm.status === 1">
                  {{ $t("order.buyerDidNTime") }}
                </div>
                <div v-if="dataForm.status === 2">
                  {{ $t("order.buyPleF") }}
                </div>
                <div v-if="dataForm.status === 3 && dataForm.dvyType !== 2">
                  {{ $t("order.shelF") }}
                </div>
                <div v-if="dataForm.status === 3 && dataForm.dvyType === 2">
                  {{ $t("order.buyA") }}
                </div>
                <!-- <div v-if="dataForm.status === 4">订单已完成，等待买家发表评价。</div> -->
                <div v-if="dataForm.status === 5">
                  {{ $t("order.buyB") }}
                </div>
                <div v-if="dataForm.status === 6">
                  {{ $t("order.orderCanc") }}
                </div>
                <div v-if="dataForm.status === 7">
                  {{ $t("order.outTimeCanOrd") }}
                </div>
              </div>
              <div class="actions">
                <div
                  v-if="dataForm.status <= 2 && dataForm.dvyType !== 2 && dataForm.orderMold !== 1"
                  class="item-btn default-btn"
                  @click="changeUserAddrOrder(dataForm.userAddrOrder)"
                >
                  {{ $t("components.modifyUseAddress") }}
                </div>
              </div>
            </div>
            <div class="state-steps">
              <el-form-item>
                <el-steps
                  style="flex: 1;"
                  :active="stepsStatus"
                  align-center
                  :process-status="dataForm.status == 6 ? 'error' : 'wait'"
                >
                  <el-step
                    :title="$t('order.submitOrders')"
                    :description="dataForm.createTime"
                  />
                  <el-step
                    :title="$t('order.theBuyerHasPaid')"
                    :description="dataForm.payTime"
                  />
                  <el-step
                    :title="
                      dataForm.dvyType === 2
                        ? $t('order.buyerHasMentioned')
                        : $t('order.shippedBySeller')
                    "
                    :description="dataForm.dvyTime"
                  />
                  <el-step
                    :title="$t('order.buyerHasReceived')"
                    :description="dataForm.finallyTime"
                  />
                </el-steps>
              </el-form-item>
            </div>
          </div>
          <div
            v-if="dataForm.deliveryExpresses && dataForm.deliveryExpresses.length"
            class="packages"
          >
            <div
              v-if="dataForm.deliveryExpresses.length > 1"
              class="p-tab"
            >
              <div
                v-for="(
                  item, keys
                ) in dataForm.deliveryExpresses"
                :key="keys"
                :class="indexs === keys ? 'item active' : 'item'"
                @click="onClickListDelivery(item, keys)"
              >
                {{ $t("order.package") }}{{ keys + 1 }}
              </div>
            </div>
            <div
              v-if="deliveryExpresse"
              class="p-con"
            >
              <div class="deliver-msg">
                <div class="d-item">
                  <div
                    class="text"
                    :style="$t('language') !== 'zh_CN' ? 'width:210px;' : 'width:80px;'"
                  >
                    {{ $t("order.delType") }}：
                  </div>
                  <div class="res">
                    {{
                      [
                        $t('order.ordinaryExpress'),
                        $t('order.ordinaryExpress'),
                        $t("order.selfMention"),
                        $t("order.noNeedRequired"),
                        $t("order.sameCityDelivery")
                      ][deliveryExpresse.deliveryType] || [
                        $t('order.ordinaryExpress'),
                        $t('order.ordinaryExpress'),
                        $t("order.selfMention"),
                        $t("order.noNeedRequired"),
                        $t("order.sameCityDelivery")
                      ][dataForm.dvyType]
                    }}
                  </div>
                </div>
                <div class="d-item">
                  <div
                    class="text"
                    :style="$t('language') !== 'zh_CN' ? 'width:210px;' : 'width:80px;'"
                  >
                    {{ $t("order.deliveryTime") }}：
                  </div>
                  <div class="res">
                    {{ deliveryExpresse.createTime }}
                  </div>
                </div>
                <div
                  v-if="deliveryExpresse.deliveryType !== 3&&deliveryExpresse.deliveryType !== 4 && deliveryExpresse.deliveryDto"
                  class="d-item"
                >
                  <div
                    class="text"
                    :style="$t('language') !== 'zh_CN' ? 'width:210px;' : 'width:80px;'"
                  >
                    {{ $t("order.courierCompany") }}：
                  </div>
                  <div class="res">
                    {{ deliveryExpresse.deliveryDto.companyName }}
                  </div>
                </div>
                <div
                  v-if="deliveryExpresse.deliveryType !== 3&&deliveryExpresse.deliveryType !== 4 && deliveryExpresse.deliveryDto"
                  class="d-item"
                >
                  <div
                    class="text"
                    :style="$t('language') !== 'zh_CN' ? 'width:210px;' : 'width:80px;'"
                  >
                    {{ $t("order.waybillNumber") }}：
                  </div>
                  <div class="res">
                    {{ deliveryExpresse.deliveryDto.dvyFlowId }}
                  </div>
                </div>
                <div
                  class="d-goods"
                  :class="{ over: deliveryExpresse.orderItems.length > 5 }"
                >
                  <div
                    class="arrow prev"
                    :class="{
                      disable:
                        deliveryExpresse.orderItems.length - 5 <= 0 ||
                        offsetCount < 1,
                    }"
                    @click="prevItem"
                  />
                  <div
                    class="arrow next"
                    :class="{
                      disable:
                        deliveryExpresse.orderItems.length - 5 <= 0 ||
                        offsetCount >=
                        deliveryExpresse.orderItems.length - 5,
                    }"
                    @click="nextItem"
                  />
                  <div class="item-goods">
                    <div
                      ref="carouserRef"
                      class="goods-box"
                    >
                      <div
                        v-for="(
                          orderItem, index
                        ) in deliveryExpresse.orderItems"
                        :key="index"
                        class="item"
                      >
                        <div class="img">
                          <prod-pic
                            :pic="orderItem.pic"
                            height="60px"
                            width="60px"
                          />
                          <div class="number">
                            ×{{ orderItem.prodCount }}
                          </div>
                        </div>
                        <div class="name">
                          {{ orderItem.prodName }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="logistics">
                <div
                  v-if="deliveryExpresse && deliveryExpresse.deliveryDto"
                  class="l-tit"
                >
                  <span class="text">{{ $t("order.logisticsStatus") }}：</span>
                  <span
                    v-if="deliveryExpresse.deliveryDto.state === 0"
                    class="l-state"
                  >{{ $t("order.noRecord") }}</span>
                  <span
                    v-if="deliveryExpresse.deliveryDto.state === 1"
                    class="l-state"
                  >{{ $t("order.collected") }}</span>
                  <span
                    v-if="deliveryExpresse.deliveryDto.state === 2"
                    class="l-state"
                  >{{ $t("order.delivering") }}</span>
                  <span
                    v-if="deliveryExpresse.deliveryDto.state === 3"
                    class="l-state"
                  >{{ $t("order.haveBeenReceived") }}</span>
                  <span
                    v-if="deliveryExpresse.deliveryDto.state === 201"
                    class="l-state"
                  >{{ $t("order.reachTheDestinationCity") }}</span>
                  <span
                    v-if="deliveryExpresse.deliveryDto.state === 4"
                    class="l-state"
                  >{{ $t("order.problemPiece") }}</span>
                </div>
                <div
                  v-if="deliveryExpresse && deliveryExpresse.deliveryDto"
                  class="logistics-box"
                >
                  <div
                    v-if="
                      deliveryExpresse.deliveryDto.state === 0 &&
                        dataForm.status == 5 &&
                        dataForm.finallyTime !== null
                    "
                    class="item"
                  >
                    <div class="time">
                      {{ dataForm.finallyTime }}
                    </div>
                    <div class="text">
                      {{ $t("order.receivedGoods") }}
                    </div>
                  </div>
                  <div
                    v-for="(trace, index) in deliveryExpresse.deliveryDto
                      .traces"
                    :key="index"
                    class="item"
                  >
                    <div class="time">
                      {{ trace.acceptTime }}
                    </div>
                    <div class="text">
                      {{ trace.acceptStation }}
                    </div>
                  </div>
                  <div
                    v-if="
                      deliveryExpresse.deliveryDto.traces &&
                        deliveryExpresse.deliveryDto.traces.length < 1
                    "
                    class="item"
                  >
                    {{ $t("order.noLogisticsInformation") }}
                  </div>
                  <div
                    v-if="dataForm.status >= 3 && dataForm.dvyTime !== null"
                    class="item"
                  >
                    <div class="time">
                      {{ dataForm.dvyTime }}
                    </div>
                    <div class="text">
                      {{ $t("order.merchantHasShippedWa") }}
                    </div>
                  </div>
                  <div
                    v-if="dataForm.status >= 2 && dataForm.payTime !== null"
                    class="item"
                  >
                    <div class="time">
                      {{ dataForm.payTime }}
                    </div>
                    <div class="text">
                      {{ $t("order.buyerHasPaidWa") }}
                    </div>
                  </div>
                  <div
                    v-if="dataForm.status >= 1"
                    :class="['item', dataForm.status >= 1?'left-line':'']"
                  >
                    <div class="time">
                      {{ dataForm.createTime }}
                    </div>
                    <div class="text">
                      {{ $t("order.buyerSubmittedAnOrder") }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div :class="['order-info',$t('language') !== 'zh_CN'?'flex-wrap':'']">
            <div
              v-if="dataForm.userAddrOrder && dataForm.userAddrOrder !== null"
              :class="['info-item',$t('language') !== 'zh_CN'?'small-width':'']"
            >
              <div class="item-tit">
                {{ $t("order.recipientInformation") }}
              </div>
              <div class="item">
                <div class="text">
                  {{
                    dataForm.dvyType === 2
                      ? $t("order.deliveryPerson")
                      : $t("publics.addressee")
                  }}：
                </div>
                <div class="res">
                  {{ dataForm.userAddrOrder.receiver }}
                </div>
              </div>
              <div class="item">
                <div class="text">
                  {{ $t("shop.contactTel") }}：
                </div>
                <div class="res">
                  {{ dataForm.userAddrOrder.mobile }}
                </div>
              </div>
              <div
                v-if="dataForm.dvyType !== 2"
                class="item"
              >
                <div class="text">
                  {{ $t("publics.deliveryAddr") }}：
                </div>
                <div class="res">
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
            <div :class="['info-item',$t('language') !== 'zh_CN'?'small-width':'']">
              <div class="item-tit">
                {{ $t("order.shippingInformation") }}
              </div>
              <div class="item">
                <div class="text">
                  {{ $t("order.delType") }}：
                </div>
                <div class="res">
                  {{
                    [
                      $t('order.ordinaryExpress'),
                      $t('order.ordinaryExpress'),
                      $t("order.buyerMention"),
                      $t("order.noNeedRequired"),
                      $t("order.sameCityDelivery"),
                    ][dataForm.dvyType]
                  }}
                </div>
                <div
                  v-if="dataForm.dvyType === null"
                  class="res"
                >
                  {{ $t("order.no") }}
                </div>
              </div>
              <div class="item">
                <div class="text">
                  {{ $t("order.deliveryTime") }}：
                </div>
                <div class="res">
                  {{ dataForm.dvyTime }}
                </div>
                <div
                  v-if="dataForm.dvyTime === null"
                  class="res"
                >
                  {{ $t("order.no") }}
                </div>
              </div>
              <div
                v-if="dataForm.dvyType === 2"
                class="item"
              >
                <div class="text">
                  {{ $t("station.stationNames") }}：
                </div>
                <div class="res">
                  {{ dataForm.stationName }}
                </div>
              </div>
            </div>
            <div
              v-if="dataForm.orderMold === 1"
              :class="['info-item',$t('language') !== 'zh_CN'?'small-width':'']"
            >
              <div class="item-tit">
                {{ $t("order.virtualInfo") }}
              </div>
              <div class="item">
                <div class="text">
                  {{ $t("order.virtualMsg") }}：
                </div>
                <div class="res">
                  <div
                    v-for="(virtualRemark,index) in virtualRemarkList"
                    :key="index"
                  >
                    {{
                      virtualRemark.name
                    }}：{{
                      virtualRemark.value
                    }}
                  </div>
                </div>
              </div>
            </div>
            <div :class="['info-item',$t('language') !== 'zh_CN'?'small-width':'']">
              <div class="item-tit">
                {{ $t("order.paymentInformation") }}
              </div>
              <div class="item">
                <div class="text">
                  {{ $t("order.actualAmount") }}：
                </div>
                {{ useCurrencyStore().defMark + dataForm.actualTotal + " + " + dataForm.score + $t("order.integral") }}
              </div>
              <div class="item">
                <div class="text">
                  {{ $t("order.paymentMethod") }}：
                </div>
                <span v-if="dataForm.payType === null || dataForm.status === 1">{{
                  $t("order.unpaid")
                }}</span>
                <div
                  v-else
                  class="res"
                >
                  {{
                    [
                      $t("order.pointsPayment"),
                      $t("order.wecProPay"),
                      $t("order.alipayPCPayment"),
                      $t("order.wechatScanCodePayment"),
                      $t("order.wechatH5Payment"),
                      $t("order.weclAccountPay"),
                      $t("order.alipayH5Payment"),
                      $t("order.alipayAPPPayment"),
                      $t("order.wechatAPPPayment"),
                      $t("order.balancePayment"),
                      $t("order.payPalPayment"),
                      '',
                      $t("order.payPalPayCardment")
                    ][dataForm.payType]
                  }}
                </div>
              </div>
              <div class="item">
                <div class="text">
                  {{ $t("order.paymentTime") }}：
                </div>
                <div class="res">
                  {{ dataForm.payTime }}
                </div>
                <div
                  v-if="dataForm.payTime === null"
                  class="res"
                >
                  {{ $t("order.no") }}
                </div>
              </div>
            </div>
            <div :class="['info-item',$t('language') !== 'zh_CN'?'small-width':'']">
              <div class="item-tit">
                {{ $t("order.buyerInformation") }}
              </div>
              <div class="item">
                <div class="text">
                  {{ $t("order.buyerSNickname") }}：
                </div>
                <div class="res">
                  {{ dataForm.nickName }}
                </div>
                <div
                  v-if="dataForm.nickName === null"
                  class="res"
                >
                  {{ $t("order.no") }}
                </div>
              </div>
              <div class="item">
                <div class="text">
                  {{ $t("order.orderRemarks") }}：
                </div>
                <div class="res">
                  {{ dataForm.remarks }}
                </div>
                <div
                  v-if="dataForm.remarks === null || dataForm.remarks === ''"
                  class="res"
                >
                  {{ $t("order.no") }}
                </div>
              </div>
            </div>
          </div>
          <div class="goods-list">
            <el-table
              :empty-text="$t('shop.noData')"
              :data="prodList"
              header-cell-class-name="table-header"
              row-class-name="table-row"
              scrollbar-always-on
            >
              <el-table-column
                prop=""
                :label="$t('home.product')"
                min-width="400px"
              >
                <template #default="scope">
                  <!-- 商品信息 -->
                  <div class="df">
                    <prod-pic
                      width="60px"
                      height="60px"
                      :pic="scope.row.pic"
                      class="prod-pic"
                    />
                    <div class="name">
                      <div>
                        <span
                          v-if="scope.row.activityType===5"
                          class="gift-icon"
                        >{{ $t("order.giveawayPord") }}</span>
                        <span>{{ scope.row.prodName }}</span>
                        <span class="sku">{{ scope.row.skuName }}</span>
                      </div>
                      <div
                        v-if="
                          scope.row.returnMoneySts &&
                            scope.row.returnMoneySts < 5 &&
                            scope.row.returnMoneySts > 0
                        "
                        class="order-status"
                      >
                        {{
                          $t("order.refundApplicationInProgress")
                        }}
                      </div>
                      <div
                        v-if="
                          scope.row.returnMoneySts &&
                            scope.row.returnMoneySts === 5
                        "
                        class="order-status"
                      >
                        {{
                          $t("order.refundsuccessfully")
                        }}
                      </div>
                    </div>
                  </div>
                  <!-- / 商品信息 -->
                  <!-- 组合 -->
                  <div
                    v-if="scope.row.comboList"
                    class="gift-prod"
                  >
                    <combo-list
                      :combo-list="scope.row.comboList"
                      :tag-name="'【' + $t('order.combo') + '】'"
                    />
                  </div>
                  <!-- / 组合 -->

                  <!-- 赠品 -->
                  <div
                    v-if="scope.row.giveawayList"
                    class="gift-prod"
                  >
                    <combo-list
                      :combo-list="scope.row.giveawayList"
                      :tag-name="'【' + $t('order.giveawayPord') + '】'"
                    />
                  </div>
                  <!-- / 赠品 -->
                </template>
              </el-table-column>
              <el-table-column
                prop="price"
                :label="$t('order.unitPrice')"
                width="180"
                align="center"
              >
                <template #default="scope">
                  <span>{{ scope.row.activityType===5 ? '-' : scope.row.price }}</span>
                </template>
              </el-table-column>
              <el-table-column
                prop="count"
                :label="$t('order.quantity')"
                width="180"
                align="center"
              >
                <template #default="scope">
                  <span>{{ scope.row.prodCount }}</span>
                </template>
              </el-table-column>
              <el-table-column
                prop="count"
                :label="$t('order.shopPreferentialAmount')"
                :width="$t('language') !== 'zh_CN'?250:180"
                align="center"
              >
                <template #default="scope">
                  <span>{{ scope.row.activityType===5 ? '-' : scope.row.shopShareReduce }}</span>
                </template>
              </el-table-column>
              <el-table-column
                prop="totalPrice"
                :label="$t('order.totalPrice')"
                width="180"
                align="center"
                fixed="right"
              >
                <template #default="scope">
                  <span>{{ scope.row.activityType===5 ? '-' : scope.row.productTotalAmount }}</span>
                </template>
              </el-table-column>
            </el-table>
            <div class="goods-total">
              <div class="text-box">
                <div class="item">
                  <div class="text">
                    {{ $t("order.prodTotalPrice") }}:
                  </div>
                  <div class="number">
                    {{ useCurrencyStore().defMark }}{{ dataForm.total }}
                  </div>
                </div>
                <div
                  v-if="dataForm.reduceAmount && dataForm.platformFreeFreightAmount && dataForm.reduceAmount - dataForm.platformFreeFreightAmount"
                  class="item"
                >
                  <div class="text">
                    {{ $t("marketing.discountedPrice") }}:
                  </div>
                  <div class="number">
                    {{ useCurrencyStore().defMark }}{{ (dataForm.reduceAmount - dataForm.platformFreeFreightAmount).toFixed(2) }}
                  </div>
                </div>
                <div
                  v-if="dataForm.freeFreightAmount"
                  class="item"
                >
                  <div class="text">
                    {{ $t("order.freeFreight") }}:
                  </div>
                  <div class="number">
                    {{ useCurrencyStore().defMark }}{{ dataForm.freeFreightAmount }}
                  </div>
                </div>
                <div
                  v-if="dataForm.freightAmount"
                  class="item"
                >
                  <div class="text">
                    {{ $t("order.shippingFees") }}:
                  </div>
                  <div class="number">
                    {{ useCurrencyStore().defMark }}{{ dataForm.freightAmount }}
                  </div>
                </div>
                <div
                  v-if="dataForm.platformFreeFreightAmount"
                  class="item"
                >
                  <div class="text">
                    {{ $t("order.platformFreeFreightAmount") }}:
                  </div>
                  <div class="number">
                    {{ useCurrencyStore().defMark }}{{ dataForm.platformFreeFreightAmount }}
                  </div>
                </div>

                <div class="item act-price">
                  <div class="text">
                    {{ $t("order.actualAmount") }}:
                  </div>
                  <div class="number">
                    {{ useCurrencyStore().defMark }}{{ dataForm.actualTotal }}{{ dataForm.score?" + "+ dataForm.score +$t("order.integral"):'' }}
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="order-log">
            <div class="log-title">
              {{ $t("order.logs") }}
            </div>
            <div
              v-if="dataForm.createTime"
              class="log-cont"
            >
              {{ dataForm.createTime }} {{ dataForm.nickName }}
              {{ $t("order.createOrder") }}
            </div>
            <div
              v-if="dataForm.payTime"
              class="log-cont"
            >
              {{ dataForm.payTime }} {{ dataForm.nickName }}
              {{ $t("order.payment") }}
            </div>
            <div
              v-if="dataForm.dvyTime"
              class="log-cont"
            >
              {{ dataForm.dvyTime }}
              {{
                dataForm.dvyType === 2
                  ? dataForm.nickName + $t("order.orderPickup")
                  : $t("order.deliverys")
              }}
            </div>
            <div
              v-if="dataForm.finallyTime"
              class="log-cont"
            >
              {{ dataForm.finallyTime }} {{ dataForm.nickName
              }}{{ $t("order.completed") }}
            </div>
            <div
              v-if="dataForm.cancelTime"
              class="log-cont"
            >
              {{ dataForm.cancelTime }} {{ dataForm.nickName }}
              {{ $t("order.cancelOrder") }}
            </div>
            <div
              v-if="dataForm.updateTime"
              class="log-cont"
            >
              {{ dataForm.updateTime }} {{ dataForm.nickName }}
              {{ $t("order.orderUpdate") }}
            </div>
          </div>
        </div>
      </div>
    </el-form>
    <!-- 弹窗, 新增 / 修改 -->
    <order-addr-update
      v-if="orderAddrUpdateVisible"
      ref="orderAddrUpdateRef"
      @refresh-user-addr-order="getDataList"
    />
    <order-remark-update
      v-if="orderRemarkUpdateVisible"
      ref="orderRemarkUpdateRef"
      @refresh-user-remark-order="getDataList"
    />
  </div>
</template>

<script setup>
import { handlerAddress } from '@/utils/index.js'

const dataForm = ref({
  orderId: 0,
  virtualRemark: '',
  orderNumber: '',
  remarks: '',
  shopRemarks: '',
  total: 0,
  deliveryExpresses: [],
  actualTotal: 0,
  dvyType: '',
  status: 1,
  addrOrderId: 0,
  nickName: '',
  orderItems: [],
  orderTime: '',
  updateTime: '',
  payTime: '',
  dvyTime: '',
  finallyTime: '',
  cancelTime: '',
  orderType: '',
  userAddrOrder: {}
})
const stepsStatus = computed(() => {
  return dataForm.value.finallyTime ? 4 : dataForm.value.dvyTime ? 3 : dataForm.value.payTime ? 2 : 1
})

const visible = ref(false)
const orderAddrUpdateVisible = ref(false)
const orderRemarkUpdateVisible = ref(false)
watch(() => visible.value,
  () => {
    if (!visible.value) {
      orderRemarkUpdateVisible.value = false
      orderAddrUpdateVisible.value = false
    }
  }
)

const deliveryExpresse = ref({
  deliveryDto: {}
})
const offsetCount = ref(0) // 偏移量
const carouserRef = ref(null)
watch(() => deliveryExpresse.value, () => {
  nextTick(() => {
    offsetCount.value = 0 // 初始化变量
    if (deliveryExpresse.value && carouserRef.value) {
      carouserRef.value.style.left = '0px'
    }
  })
})

const { query } = useRoute()
onMounted(() => {
  dataForm.value.orderNumber = query.orderNumber || 0
  init()
})

const indexs = ref(0)
const virtualRemarkList = ref([])
const dataFormRef = ref(null)
// 商品列表
const prodList = ref([])
const init = () => {
  visible.value = true
  nextTick(() => {
    dataFormRef.value?.resetFields()
  })
  if (dataForm.value.orderNumber) {
    indexs.value = 0
    // 修改
    http({
      url: http.adornUrl(`/order/delivery/orderInfo/${dataForm.value.orderNumber}`),
      method: 'get',
      params: http.adornParams()
    }).then(({ data }) => {
      data.deliveryExpresses.forEach(item => {
        if (item.deliveryDto) {
          item.deliveryDto.traces = item.deliveryDto.traces ? item.deliveryDto.traces.reverse() : item.deliveryDto.traces
        }
      })
      dataForm.value = data
      deliveryExpresse.value = dataForm.value.deliveryExpresses[0]
      virtualRemarkList.value = JSON.parse(dataForm.value.virtualRemark)

      // 处理商品列表
      prodList.value = data.orderItems
    })
  }
}

const getDataList = () => {
  http({
    url: http.adornUrl(`/order/delivery/orderInfo/${dataForm.value.orderNumber}`),
    method: 'get',
    params: http.adornParams()
  })
    .then(({ data }) => {
      dataForm.value = data
      deliveryExpresse.value = dataForm.value.deliveryExpresses[0]
    })
}

/**
 * 物流事件
 */
const onClickListDelivery = (delivery, index) => {
  http({
    url: http.adornUrl(`/order/delivery/deliveryOrder/${delivery.orderDeliveryId}`),
    method: 'get'
  }).then(({ data }) => {
    if (data.deliveryDto) {
      data.deliveryDto.traces = data.deliveryDto.traces ? data.deliveryDto.traces.reverse() : data.deliveryDto.traces
    }
    deliveryExpresse.value = data
  })
  indexs.value = index
}

// 商品切换
const prevItem = () => {
  const len = deliveryExpresse.value?.orderItems.length
  if (len - 5 > 0) {
    if (offsetCount.value > 0) {
      offsetCount.value--
      carouserRef.value.style.left = '-' + (70 * offsetCount.value) + 'px'
    } else {
      return false
    }
  } else if (len - 5 <= 0) {
    return false
  } else {
    return false
  }
}

const nextItem = () => {
  const len = deliveryExpresse.value?.orderItems.length
  if (len - 5 > 0) {
    if (offsetCount.value < len - 5) {
      offsetCount.value++
      carouserRef.value.style.left = '-' + (70 * offsetCount.value) + 'px'
    } else if (len - 5 <= 0) {
      return false
    } else {
      return false
    }
  } else {
    return false
  }
}

const orderAddrUpdateRef = ref(null)
/**
 * 修改地址
 */
const changeUserAddrOrder = () => {
  orderAddrUpdateVisible.value = true
  nextTick(() => {
    const From = JSON.parse(JSON.stringify(dataForm.value))
    orderAddrUpdateRef.value?.init(From)
  })
}
</script>

<style scoped lang="scss">
@use './index.scss';
</style>

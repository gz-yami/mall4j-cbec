<template>
  <div class="page-order-order">
    <div class="search-bar">
      <el-form
        :inline="true"
        :model="dataForm"
        @submit.prevent
        @keyup.enter="getDataList(page)"
      >
        <div class="input-row">
          <el-form-item
            :label="$t('order.number') + ':'"
            :label-width="lang !== 'zh_CN' ? 'auto' : '85px'"
          >
            <el-input
              v-model="dataForm.orderNumber"
              :placeholder="$t('order.number')"
              clearable
            />
          </el-form-item>

          <el-form-item
            :label="$t('order.orderMold') + ':'"
            :label-width="lang !== 'zh_CN' ? 'auto' : '85px'"
          >
            <el-select
              v-model="dataForm.orderMold"
              clearable
              :placeholder="$t('order.orderMold')"
            >
              <el-option
                v-for="item in orderMold"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item
            :label="$t('group.orderStatus') + ':'"
            :label-width="lang !== 'zh_CN' ? 'auto' : '85px'"
          >
            <el-select
              v-model="status"
              clearable
              :placeholder="$t('order.statusMsg')"
              @change="orderStatus"
            >
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item
            :label="$t('order.theRecipientSName') + ':'"
            :label-width="lang !== 'zh_CN' ? 'auto' : '100px'"
          >
            <el-input
              v-model="dataForm.receiver"
              :placeholder="$t('order.pleaseEnRecipName')"
              clearable
            />
          </el-form-item>
          <el-form-item
            :label="$t('shop.contactTel') + ':'"
            :label-width="lang !== 'zh_CN' ? 'auto' : '85px'"
          >
            <el-input
              v-model="dataForm.mobile"
              :placeholder="$t('order.pleaseEnterNumber')"
              clearable
            />
          </el-form-item>

          <el-form-item
            :label="$t('order.logisticsType') + ':'"
            :label-width="lang !== 'zh_CN' ? 'auto' : '85px'"
          >
            <el-select
              v-model="dataForm.dvyType"
              clearable
              :placeholder="$t('order.logisticsType')"
            >
              <el-option
                v-for="item in dvyType"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <!-- 门店名称 -->
          <!--          <el-form-item-->
          <!--            :label="$t('station.stationNames') + ':'"-->
          <!--            :label-width="lang !== 'zh_CN' ? 'auto' : '100px'"-->
          <!--          >-->
          <!--            <el-input-->
          <!--              v-model="dataForm.stationName"-->
          <!--              :placeholder="$t('station.stationNames')"-->
          <!--              clearable-->
          <!--            />-->
          <!--          </el-form-item>-->
          <el-form-item
            :label="$t('order.createTime') + ':' "
            :label-width="lang !== 'zh_CN' ? 'auto' : '85px'"
          >
            <el-date-picker
              v-model="dateRange"
              type="datetimerange"
              clearable
              :range-separator="$t('time.tip')"
              value-format="YYYY-MM-DD HH:mm:ss"
              :default-time="[new Date(2000, 1, 1, 0, 0, 0),
                              new Date(2000, 2, 1, 23, 59, 59)]"
              :start-placeholder="$t('time.start')"
              :end-placeholder="$t('time.end')"
            />
            <div
              class="default-btn"
              style="margin-left: 20px;"
              :class="{ 'is-active': timeActive === 1 }"
              @click="setDateRange(1)"
            >
              {{
                $t("time.t")
              }}
            </div>
            <div
              class="default-btn"
              :class="{ 'is-active': timeActive === 2 }"
              @click="setDateRange(2)"
            >
              {{
                $t("time.y")
              }}
            </div>
            <div
              class="default-btn"
              :class="{ 'is-active': timeActive === 3 }"
              @click="setDateRange(3)"
            >
              {{
                $t("time.n")
              }}
            </div>
            <div
              class="default-btn"
              :class="{ 'is-active': timeActive === 4 }"
              @click="setDateRange(4)"
            >
              {{
                $t("temp.m")
              }}
            </div>
          </el-form-item>
          <el-form-item>
            <div
              class="default-btn primary-btn"
              @click="onSearch(true)"
            >
              {{ $t("order.query") }}
            </div>
            <div
              class="default-btn"
              @click="getSoldExcel()"
            >
              {{ $t("formData.export") }}
            </div>
            <div
              class="default-btn"
              @click="clear()"
            >
              {{ $t("shop.resetMap") }}
            </div>
            <div
              v-if="isAuth('order:order:exportunorder')"
              class="default-btn"
              @click="uploadSpu"
            >
              {{ $t("order.BulkShipping") }}
            </div>
          </el-form-item>
        </div>
      </el-form>
    </div>
    <div class="main">
      <div class="content">
        <!-- 导航 -->
        <div class="order-status-nav clearfix">
          <ul class="nav-list clearfix">
            <li
              :class="['nav-item', activeName == 0 ? 'selected' : '']"
              data-sts="0"
              @click="selectNav($event)"
            >
              {{ $t('time.a') }}
            </li>
            <li
              :class="['nav-item', activeName == 1 ? 'selected' : '']"
              data-sts="1"
              @click="selectNav($event)"
            >
              {{ $t('order.pendingPayment') }}
            </li>
            <li
              :class="['nav-item', activeName == 2 ? 'selected' : '']"
              data-sts="2"
              @click="selectNav($event)"
            >
              {{ $t('order.toBeShipped') }}
            </li>
            <li
              :class="['nav-item', activeName == 3 ? 'selected' : '']"
              data-sts="3"
              @click="selectNav($event)"
            >
              {{ $t("order.pendingReceipt") }}
            </li>
            <li
              :class="['nav-item', activeName == 5 ? 'selected' : '']"
              data-sts="5"
              @click="selectNav($event)"
            >
              {{ $t("order.successfulTransaction") }}
            </li>
            <li
              :class="['nav-item', activeName == 6 ? 'selected' : '']"
              data-sts="6"
              @click="selectNav($event)"
            >
              {{ $t("order.transactionFailed") }}
            </li>
            <li
              :class="['nav-item', activeName == 7 ? 'selected' : '']"
              data-sts="7"
              @click="selectNav($event)"
            >
              {{ $t("group.waitGroup") }}
            </li>
          </ul>
          <ul class="nav-right" />
        </div>

        <!-- 列标题 -->
        <div :class="['tit']">
          <el-row style="width: 100%">
            <el-col
              id="prod-info-title"
              :span="6"
            >
              <span class="item product">{{ $t("temp.prodInfo") }}</span>
            </el-col>
            <el-col
              id="price-title"
              :span="4"
              class="transaction-price"
            >
              <span class="item">{{ $t("order.transaQuantity") }}</span>
            </el-col>
            <el-col
              :span="3"
              class="column-title"
            >
              <span class="item">{{ $t("order.actualPaymentAmount") }}({{ useCurrencyStore().defMark }})</span>
            </el-col>
            <el-col
              :span="2"
              class="column-title"
            >
              <span class="item">{{ $t("order.paymentMethod") }}</span>
            </el-col>
            <el-col
              :span="$t('language') !== 'zh_CN'?2:3"
              class="column-title"
            >
              <span class="item">{{ $t("order.buyerConsignee") }}</span>
            </el-col>
            <el-col
              :span="2"
              class="column-title"
            >
              <span class="item">{{ $t("group.orderStatus") }}</span>
            </el-col>
            <el-col
              :span="$t('language') === 'zh_CN'?2:3"
              class="column-title"
            >
              <span class="item">{{ $t("order.afterSalesStatus") }}</span>
            </el-col>
            <el-col
              :span="2"
              class="column-title"
            >
              <span class="item">{{ $t("publics.operating") }}</span>
            </el-col>
          </el-row>
        </div>
        <div
          v-show="showHeadScroll"
          :class="['tit', $t('language') !== 'zh_CN'? 'fixed-top-en' : 'fixed-top',sidebarFold?'fold-fixed-top':'']"
        >
          <el-row style="width: 100%">
            <el-col
              id="prod-info-title"
              :span="6"
            >
              <span class="item product">{{ $t("temp.prodInfo") }}</span>
            </el-col>
            <el-col
              id="price-title"
              :span="4"
              class="transaction-price"
            >
              <span class="item">{{ $t("order.transaQuantity") }}</span>
            </el-col>
            <el-col
              :span="3"
              class="column-title"
            >
              <span class="item">{{ $t("order.actualPaymentAmount") }}({{ useCurrencyStore().defMark }})</span>
            </el-col>
            <el-col
              :span="2"
              class="column-title"
            >
              <span class="item">{{ $t("order.paymentMethod") }}</span>
            </el-col>
            <el-col
              :span="$t('language') !== 'zh_CN'?2:3"
              class="column-title"
            >
              <span class="item">{{ $t("order.buyerConsignee") }}</span>
            </el-col>
            <el-col
              :span="2"
              class="column-title"
            >
              <span class="item">{{ $t("group.orderStatus") }}</span>
            </el-col>
            <el-col
              :span="$t('language') === 'zh_CN'?2:3"
              class="column-title"
            >
              <span class="item">{{ $t("order.afterSalesStatus") }}</span>
            </el-col>
            <el-col
              :span="2"
              class="column-title"
            >
              <span class="item">{{ $t("publics.operating") }}</span>
            </el-col>
          </el-row>
        </div>

        <div
          v-for="order in dataList"
          :key="order.orderId"
          class="prod"
        >
          <div class="prod-tit">
            <span class="order-number">{{ $t("order.number") }}:{{ order.orderNumber }}</span>
            <span class="order-time">{{ $t("order.createTime") }}:{{ order.createTime }}</span>
          </div>
          <div class="prod-cont">
            <el-row style="width: 100%">
              <el-col
                :span="10"
                style="height: 100%"
              >
                <div class="item prod-item">
                  <div
                    v-for="orderItem in order.orderItems"
                    :key="orderItem.orderItemId"
                    class="items name"
                  >
                    <!-- 商品信息 -->
                    <div class="order-prod-item-info">
                      <div class="prod-image">
                        <prod-pic
                          width="60px"
                          height="60px"
                          :pic="orderItem.pic"
                        />
                      </div>
                      <div class="prod-name">
                        <div class="prod-con">
                          <div class="prod-name-txt">
                            {{ orderItem.prodName }}
                          </div>
                          <div
                            v-if="orderItem.skuName"
                            class="prod-name-sku"
                          >
                            {{ orderItem.skuName }}
                          </div>
                          <div
                            v-if="order.orderType === 1 || order.orderType === 2"
                            class="order-status"
                          >
                            {{
                              order.orderType === 1
                                ? $t("order.groupPurchaseOrder")
                                : order.orderType === 2
                                  ? $t("order.spikeOrder")
                                  : ""
                            }}
                          </div>
                          <div
                            v-if="order.orderMold === 1"
                            class="order-status"
                          >
                            {{ $t("order.virtualOrder") }}
                          </div>
                          <div
                            v-if="
                              !orderItem.returnMoneySts ||
                                orderItem.returnMoneySts < 0 ||
                                orderItem.returnMoneySts > 5
                            "
                            class="order-status"
                          >
                            {{
                              orderItem.status === 0 && order.status === 2
                                ? $t("order.pendingReceipt")
                                : [
                                  "",
                                  $t("order.pendingPayment"),
                                  $t("order.toBeShipped"),
                                  $t("order.pendingReceipt"),
                                  "",
                                  $t("order.successfulTransaction"),
                                  $t("order.transactionFailed"),
                                  $t("group.waitGroup"),
                                ][order.status]
                            }}
                          </div>
                          <div
                            v-else
                            class="order-status"
                          >
                            {{ orderItem.returnMoneySts === 5 ? $t("order.refundsuccessfully") : $t("order.refundApplicationInProgress") }}
                          </div>
                          <div
                            v-if="order.dvyType === 2 || order.dvyType === 4"
                            class="order-status"
                          >
                            {{
                              order.dvyType === 2
                                ? $t("order.selfMention")
                                : order.dvyType === 4
                                  ? $t("order.sameCityDelivery")
                                  : ""
                            }}
                          </div>
                          <div
                            v-if="orderItem.preSellTime && orderItem.preSellTime!== null"
                            class="order-status"
                          >
                            {{ $t('order.EstimatedDeliveryTime') }}{{ preSellTimeShowHandler(orderItem.preSellTime) }}
                          </div>
                        </div>
                      </div>
                      <div class="prod-price">
                        <span>{{ orderItem.price.toFixed(2) }}</span>
                        <span>{{ orderItem.prodCount }}{{ $t("marketing.item") }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </el-col>
              <el-col
                :span="3"
                style="height: 100%"
              >
                <div class="item">
                  <div>
                    <span class="totalprice">{{ order.actualTotal.toFixed(2) }}
                    </span>
                    <span
                      v-if="order.score && order.score > 0"
                      class="totalprice"
                    >+ {{ order.score }}{{ $t("order.integral") }}</span>
                    <span
                      v-if="order.freightAmount && order.freightAmount + order.platformFreeFreightAmount > 0 && order.freightAmount - order.platformFreeFreightAmount"
                      class="totalprice"
                    >（{{ $t("order.includingFreight") }}：{{
                      (order.freightAmount - order.platformFreeFreightAmount).toFixed(2)
                    }}）</span>
                    <br>
                    <span>{{ $t("order.total") }}{{ order.productNums
                    }}{{ $t("marketing.item") }}</span>
                    <br>
                    <div
                      v-if="order.status === 1"
                      class="default-btn text-btn"
                      @click="editAmount(order)"
                    >
                      {{ $t("order.modifyTheAmount") }}
                    </div>
                  </div>
                </div>
              </el-col>
              <!-- 支付方式 -->
              <el-col
                :span="2"
                style="height: 100%"
              >
                <div class="item">
                  线上支付
                </div>
              </el-col>
              <!-- 买家信息 -->
              <el-col
                :span="$t('language') !== 'zh_CN'?2:3"
                style="height: 100%"
              >
                <div class="item">
                  <div class="buyer-info">
                    <div class="buyer-name">
                      {{ order.receiverName }}
                    </div>
                    <div class="buyer-phone">
                      {{ order.receiverMobile }}
                    </div>
                  </div>
                </div>
              </el-col>
              <el-col
                :span="2"
                style="height: 100%"
              >
                <div class="item">
                  <span>
                    <span
                      v-if="order.status === 1"
                      type="danger"
                    >{{ $t("order.pendingPayment") }}</span>
                    <span
                      v-else-if="order.status === 2"
                      type="danger"
                    >{{ $t("order.toBeShipped") }}</span>
                    <span
                      v-else-if="order.status === 3"

                      type="danger"
                    >{{ $t("order.pendingReceipt") }}</span>
                    <span
                      v-else-if="order.status === 7"
                      type="danger"
                    >{{ $t("group.waitGroup") }}</span>
                    <span
                      v-else-if="order.status === 5"

                      type="danger"
                    >{{ $t("order.successfulTransaction") }}</span>
                    <span
                      v-else-if="order.status === 6"
                    >{{
                      $t("order.transactionFailed")
                    }}</span>
                  </span>
                </div>
              </el-col>
              <el-col
                :span="$t('language') === 'zh_CN'?2:3"
              >
                <div class="item">
                  <span v-if="order.refundStatus === 1">{{ $t("order.requestARefund") }}</span>
                  <span v-else-if="order.refundStatus === 2">{{ $t("order.refundsuccessfully") }}</span>
                  <span v-else-if="order.refundStatus === 3">{{ $t("order.partialRefundSucc") }}</span>
                  <span v-else-if="order.refundStatus === 4">{{ $t("order.refundFailed") }}</span>
                  <span v-else>{{ $t("order.noAfterSales") }}</span>
                </div>
              </el-col>
              <el-col
                :span="2"
                style="height: 100%"
              >
                <div class="item">
                  <div class="operate">
                    <!-- <button onclick="">打印订单</button><br> -->
                    <div
                      v-if="isAuth('order:get:info')"
                      class="default-btn text-btn operate-btn"
                      @click="onAddOrUpdate(order.orderNumber)"
                    >
                      {{ $t("order.seeDetails") }}
                    </div>
                    <div
                      v-if="
                        isAuth('order:delivery:orderItemsDelivery') &&
                          order.status == 3 &&
                          (!order.refundStatus || order.refundStatus > 2) &&
                          order.isOnlineDelivery === 0
                      "
                      class="default-btn text-btn operate-btn"
                      @click="reviseLogistics(order.orderNumber)"
                    >
                      {{ $t("order.modifyLogistics") }}
                    </div>
                    <div
                      v-if="isAuth('order:delivery:orderItemsDelivery') && order.status === 2 && order.dvyType !== 2 && order.orderMold !== 1"
                      class="default-btn text-btn operate-btn"
                      @click="changeOrder(order)"
                    >
                      {{ $t("order.delivery") }}
                    </div>
                    <div
                      v-if="isAuth('admin:station:orderItemsDelivery') && order.status === 2 && order.dvyType === 2"
                      class="default-btn text-btn operate-btn"
                      @click="stationOrder(order)"
                    >
                      {{ $t("order.pickUp") }}
                    </div>
                    <div
                      v-if="isAuth('order:virtual:update') && (!order.refundStatus || order.refundStatus > 2)
                        && order.orderMold === 1 && order.writeOffNum !== 0 && (order.status === 3 ||order.status === 5)
                        && order.writeOffCodes && order.writeOffCodes.length > 0 && order.writeOffStatus !== 1 "
                      class="default-btn text-btn operate-btn"
                      @click="stationOrder(order)"
                    >
                      {{ $t("order.Writeoffs") }}
                    </div>
                    <div
                      v-if="isAuth('admin:orderRefund:update') && order.refundStatus"
                      class="default-btn text-btn operate-btn"
                      @click="refundRoute(order.orderNumber)"
                    >
                      {{ $t("order.refundInformation") }}
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
        <div
          v-if="!dataList.length"
          class="empty"
        >
          {{ $t("order.noData") }}
        </div>
      </div>
    </div>
    <el-pagination
      v-if="dataList.length"
      :current-page="page.currentPage"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="page.pageSize"
      :total="page.total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="sizeChangeHandle"
      @current-change="currentChangeHandle"
    />
    <!-- 弹窗, 新增 / 修改 -->
    <order-station
      v-if="orderStationVisible"
      ref="orderStationRef"
      @refresh-order-station-data-list="getDataList"
    />
    <consignment-info
      v-if="consignmentInfoVisible"
      ref="consignmentInfoRef"
      @input-callback="getWaitingConsignmentExcel"
    />
    <orderAmount-update
      v-if="orderAmountUpdateVisible"
      ref="orderAmountUpdateRef"
      @refresh-order-amount-update="getDataList"
    />
    <order-delivery-update
      v-if="devyVisible"
      ref="orderDeliveryUpdateRef"
      @refresh-order-delivery-update="getDataList"
    />
    <!-- 修改物流弹窗 -->
    <el-dialog
      v-model="logVisible"
      :title="$t('order.modifyLogistics')"
      :close-on-click-modal="false"
      width="50%"
      @closed="handleClose"
    >
      <!-- 修改 -->
      <div
        v-if="isReviseLog"
        class="revise-log"
      >
        <div class="change-logistics">
          <div class="warning">
            {{ $t("order.ifModifyTheLog") }}
          </div>
          <div class="log-list">
            <div
              v-for="(logItem, index) in logisticsInfo"
              :key="index"
              class="item"
            >
              <div class="i-tit">
                <div class="big">
                  {{ $t("order.package") }}{{ index + 1 }}
                </div>
                <div class="text">
                  {{ $t("order.total") }} {{ logItem.productNums }}
                  {{ $t("marketing.item") }}{{ $t("home.product") }}
                </div>
              </div>
              <el-form
                ref="logDataFormRef"
                :model="logDataForm"
                :label-width="lang !== 'zh_CN' ? '200px' : '95px'"
                @submit.prevent
                @keyup.enter="logDataFormSubmit()"
              >
                <div class="item-goods">
                  <div
                    ref="carouserRef"
                    class="goods-box"
                  >
                    <div
                      v-for="( orderItem, indexs ) in logItem.orderItems"
                      :key="indexs"
                      class="item"
                    >
                      <div class="img">
                        <prod-pic
                          height="60"
                          width="60"
                          :pic="orderItem.pic"
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
                <el-form-item
                  :label="$t('order.delType') + '：'"
                >
                  <el-radio-group v-model="logItem.tempDeliveryType">
                    <el-radio
                      v-model="radio"
                      :label="1"
                      disabled
                    >
                      {{
                        $t("order.distribution")
                      }}
                    </el-radio>
                    <el-radio
                      v-model="radio"
                      :label="3"
                      disabled
                    >
                      {{
                        $t("order.noNeedRequired")
                      }}
                    </el-radio>
                  </el-radio-group>
                </el-form-item>
                <!--配送方式 线上 线下-->
                <el-form-item
                  v-if="logItem.tempDeliveryType === 1"
                  :label="$t('order.delType') + '：'"
                >
                  <el-radio-group v-model="logItem.deliveryType">
                    <!--                    <el-radio-->
                    <!--                      :label="0"-->
                    <!--                      disabled-->
                    <!--                    >-->
                    <!--                      {{-->
                    <!--                        $t("order.onlineDelivery")-->
                    <!--                      }}-->
                    <!--                    </el-radio>-->
                    <el-radio
                      :label="1"
                      disabled
                    >
                      {{
                        $t("order.selfDelivery")
                      }}
                    </el-radio>
                  </el-radio-group>
                </el-form-item>
                <div
                  v-if="logItem.deliveryType !== 3 &&logItem.deliveryType !== 0"
                  class="info-int"
                >
                  <el-form-item
                    :label="$t('order.courierCompany') + ':'"
                    class="form-item"
                  >
                    <div
                      v-if="logVisible"
                      class="con"
                    >
                      <select-lazy v-model="logItem.dvyId" />
                    </div>
                  </el-form-item>
                  <el-form-item
                    :label="$t('order.trackingNumber') + ':'"
                    class="form-item"
                  >
                    <el-input
                      v-model="logItem.dvyFlowId"
                      controls-position="right"
                      :min="0"
                      maxlength="20"
                    />
                  </el-form-item>
                </div>
                <div
                  v-if="logItem.deliveryType === 0"
                >
                  <el-form-item
                    :label="$t('order.courierCompany') + '：'"
                    class="form-item"
                  >
                    <div
                      v-if="logVisible"
                      class="con"
                    >
                      {{ deliveryCompanyType[logItem.dvyId - 1]?.label }}
                    </div>
                  </el-form-item>
                  <el-form-item
                    :label="$t('order.trackingNumber') + '：'"
                    class="form-item"
                  >
                    {{ logItem.dvyFlowId }}
                  </el-form-item>
                </div>
              </el-form>
            </div>
          </div>
        </div>
      </div>
      <!-- 确认 -->
      <div
        v-if="!isReviseLog"
        class="change-logistics"
      >
        <div class="warning">
          {{ $t("order.ifModifyTheLog") }}
        </div>
        <el-table
          class="log-info-table"
          :data="confirmList"
        >
          <el-table-column
            property="dvyIdName"
            :label="$t('order.packageName')"
            width="220"
            style="text-align: left"
          />
          <el-table-column
            property="productNums"
            :label="$t('order.amountOfGoods')"
            width="140"
          />
          <el-table-column
            property="delMethod"
            :label="$t('order.deliveryMethod')"
            width="140"
          />
          <el-table-column
            property="dvyName"
            :label="$t('order.logisticsCompany')"
            width="170"
          />
          <el-table-column
            property="dvyFlowId"
            :label="$t('order.trackingNumber')"
          />
        </el-table>
      </div>

      <template #footer>
        <div
          v-if="isReviseLog"
          class="dialog-footer"
        >
          <div
            class="default-btn"
            @click="logVisible = false"
          >
            {{
              $t("crud.filter.cancelBtn")
            }}
          </div>
          <div
            class="default-btn primary-btn"
            @click="logDataFormSubmit()"
          >
            {{
              $t("order.save")
            }}
          </div>
        </div>

        <div
          v-if="!isReviseLog"
          class="dialog-footer"
        >
          <div
            class="default-btn"
            @click="backToRevise()"
          >
            {{
              $t("order.backToModify")
            }}
          </div>
          <div
            class="default-btn primary-btn"
            @click="confirmRevise()"
          >
            {{
              $t("order.confirmTheChanges")
            }}
          </div>
        </div>
      </template>
    </el-dialog>
    <!-- /修改物流弹窗 -->
    <order-upload
      v-if="uploadVisible"
      ref="spuUploadRef"
      :param="dataForm"
      @refresh-data-list1="getWaitingConsignmentExcel"
    />
  </div>
</template>

<script setup>
import OrderStation from './components/order-station.vue'
import ConsignmentInfo from './components/consignment-info.vue'
import OrderAmountUpdate from './components/orderAmount-update.vue'
import OrderDeliveryUpdate from './components/order-delivery-update.vue'
import OrderUpload from './components/order-upload.vue'
import moment from 'moment'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { isAuth } from '@/utils'

const commonStore = useCommonStore()
const routerStore = useRouterStore()
const router = useRouter()
const radio = ref('1')
const options = [
  {
    value: 1,
    label: $t('order.pendingPayment')
  },
  {
    value: 2,
    label: $t('order.toBeShipped')
  },
  {
    value: 3,
    label: $t('order.pendingReceipt')
  },
  {
    value: 5,
    label: $t('order.successfulTransaction')
  },
  {
    value: 6,
    label: $t('order.transactionFailed')
  }
]
const orderMold = [
  {
    value: 0,
    label: $t('order.physicalOrder')
  },
  {
    value: 1,
    label: $t('order.virtualOrder')
  }
]
const dvyType = [
  {
    value: 1,
    label: $t('order.expressDelivery')
  },
  {
    value: 3,
    label: $t('order.noNeedRequired')
  }
]
const deliveryCompanyType = [{
  value: 1,
  label: $t('express.sf')
}, {
  value: 2,
  label: $t('express.jd')
}, {
  value: 3,
  label: $t('express.yto')
}, {
  value: 4,
  label: $t('express.yunDa')
}, {
  value: 5,
  label: $t('express.zto')
}, {
  value: 6,
  label: $t('express.sto')
}, {
  value: 7,
  label: $t('express.best')
}, {
  value: 8,
  label: $t('express.ems')
}]

const payType = computed(() => {
  const typeArr = [{
    value: -1,
    label: $t('admin.unpay')
  }, {
    value: 0,
    label: $t('order.pointsPayment')
  },
  // {
  //   value: 1,
  //   label: $t('admin.weChatPay')
  // }, {
  //   value: 2,
  //   label: $t('admin.aliPay')
  // },
  {
    value: 3,
    label: $t('admin.balancePay')
  }, {
    value: 4,
    label: $t('order.payPalPayment')
  }, {
    value: 5,
    label: $t('order.payPalPayCardment')
  }]
  return typeArr
})

const consignmentInfoVisible = ref(false)
const sidebarFold = computed({
  get () { return commonStore.sidebarFold },
  set (val) { commonStore.updateSidebarFold(val) }
})

const page = ref({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})
const { query } = useRoute()
onActivated(() => {
  // 携带参数查询
  if (Object.keys(query).length > 0) {
    getDataList(page, query)
  }
})

const activeName = ref('0')
const status = ref(null)
const sts = ref(0)
const resizeProportion = ref(1)
onMounted(() => {
  // 首页跳转状态参数
  activeName.value = query.status ? String(query.status) : '0'
  sts.value = query.status || 0
  status.value = sts.value === 0 ? null : Number(sts.value)
  // 携带参数查询
  getDataList(page.value, query)
  // 请求物流公司
  getLogisticsList()
  // 监听页面滚动
  window.addEventListener('scroll', scrollToTop)
  resizeProportion.value = window.outerHeight / window.innerHeight
  window.addEventListener('resize', () => {
    resizeProportion.value = window.outerHeight / window.innerHeight
  })
})

onUnmounted(() => {
  // 页面销毁时移除监听
  window.removeEventListener('scroll', scrollToTop)
})

const isReviseLog = ref(true) // 是否正在修改物流信息
/**
 * 关闭 修改物流弹窗
 */
const handleClose = () => {
  isReviseLog.value = true
}

const showHeadScroll = ref(false) // 修改物流相关
/**
 * 页面滚动事件
 */
const scrollToTop = () => {
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop
  showHeadScroll.value = scrollTop > (400 * resizeProportion.value)
}

const confirmList = ref([]) // 确认修改信息
const errorNum = ref(0)
const logisticsInfo = ref([]) // 包裹列表
const logVisible = ref(false)
/**
 * 获取包裹物流信息
 */
const reviseLogistics = (orderNumber) => {
  logVisible.value = !logVisible.value
  http({
    url: http.adornUrl('/order/delivery/getOrderDeliveries/' + orderNumber),
    method: 'put'
  }).then(({ data }) => {
    logisticsInfo.value = data
    logisticsInfo.value?.forEach((el, index) => {
      el.tempDeliveryType = el.deliveryType === 1 || el.deliveryType === 0 ? 1 : el.deliveryType
      el.newDvyFlowId = el.dvyFlowId
      el.newDvyId = el.dvyId
      el.dvyIdName = $t('order.package') + Number(index + 1)
      // 修改弹窗
      confirmList.value = [] // 清空确认列表
    })
  })
  errorNum.value = 0
}

const logDataForm = ref({
  dvyId: '', // 当前物流公司id
  dvyFlowId: '', // 物流单号
  dvyNames: [] // 物流公司列表
})
/**
 * 请求物流公司列表
 */
const getLogisticsList = () => {
  http({
    url: http.adornUrl('/platform/delivery/list'),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    logDataForm.value.dvyNames = data
  })
}

/**
 * 点击保存
 */
const logDataFormSubmit = () => {
  let flag = true
  const logisticsInfoCope = logisticsInfo.value
  // eslint-disable-next-line array-callback-return
  logisticsInfoCope.map((el) => {
    // eslint-disable-next-line array-callback-return
    if (el.deliveryType === 3 || el.deliveryType === 0) return
    if (!flag) return false
    const reg = /^[a-zA-Z0-9]{4,30}$/
    if (el.dvyFlowId.length === 0) {
      ElMessage({
        message: $t('order.logEmpty'),
        type: 'warning'
      })
      flag = false
      errorNum.value = 1
    } else if (!reg.test(el.dvyFlowId)) {
      ElMessage({
        message: $t('order.checkDvyId'),
        type: 'warning'
      })
      flag = false
      errorNum.value = 1
    } else {
      errorNum.value = 0
      const confirmListCope = confirmList.value
      if ((el.newDvyFlowId.length !== 0 && el.newDvyFlowId !== el.dvyFlowId) || (el.newDvyId !== el.dvyId)) {
        confirmListCope.push(el)
        confirmListCope.forEach((el) => {
          el.delMethod = $t('order.logistics')
          logDataForm.value?.dvyNames.forEach((dvyName) => {
            if (el.dvyId === dvyName.dvyId) {
              el.dvyName = dvyName.dvyName
            }
          })
        })
        confirmList.value = confirmListCope
      }
    }
  })
  if (errorNum.value === 1) {
    return
  } else if (confirmList.value.length === 0) {
    ElMessage({
      message: $t('order.noLogIsChanged'),
      type: 'warning'
    })
    flag = false
  }
  if (flag) {
    isReviseLog.value = false // 修改弹窗
  }
}

/**
 * 确认修改物流信息
 */
const confirmRevise = () => {
  http({
    url: http.adornUrl('/order/delivery/updateOrderDeliveries'),
    method: 'put',
    data: http.adornData({
      deliveryOrders: confirmList.value
    }),
    params: http.adornParams()
  }).then(() => {
    ElMessage({
      message: $t('publics.operation'),
      type: 'success'
    })
    isReviseLog.value = true
    logVisible.value = false
  })
}

/**
 * 返回修改（物流信息）
 */
const backToRevise = () => {
  isReviseLog.value = true
  // 修改弹窗
  confirmList.value = [] // 清空确认列表
}

const dataListLoading = ref(false)
const dateRange = ref([])
const dataForm = ref({})
let tempSearchForm = ''
const dataList = ref([])
/**
 * 获取数据列表
 */
const getDataList = (pageParam, params, newData = false) => {
  pageParam = (pageParam === undefined ? page.value : pageParam)
  dataListLoading.value = true
  if (newData || !tempSearchForm) {
    tempSearchForm = {
      current: pageParam == null ? page.value.currentPage : pageParam.currentPage,
      size: pageParam == null ? page.value.pageSize : pageParam.pageSize,
      orderNumber: dataForm.value.orderNumber,
      orderType: dataForm.value.orderType,
      orderMold: dataForm.value.orderMold,
      payType: dataForm.value.payType,
      receiver: dataForm.value.receiver,
      mobile: dataForm.value.mobile,
      status: status.value,
      dvyType: dataForm.value.dvyType,
      stationName: dataForm.value.stationName,
      refundStatus: dataForm.value.refundStatus,
      startTime: dateRange.value === null ? null : dateRange.value[0], // 开始时间
      endTime: dateRange.value === null ? null : dateRange.value[1] // 结束时间
    }
  } else {
    tempSearchForm.current = pageParam == null ? page.value.currentPage : pageParam.currentPage
    tempSearchForm.size = pageParam == null ? page.value.pageSize : pageParam.pageSize
    tempSearchForm.status = status.value
  }
  http({
    url: http.adornUrl('/order/order/page'),
    method: 'get',
    params: http.adornParams(
      Object.assign(tempSearchForm, params
      ), false
    )
  }).then(({ data }) => {
    dataList.value = data.records
    onHandleDataList()
    page.value.total = data.total
    sts.value = !status.value ? 0 : status.value
    dataListLoading.value = false
    activeName.value = !status.value ? '0' : status.value + ''
  })
}

const onHandleDataList = () => {
  dataList.value.forEach(order => {
    let isOnlineDelivery = 1
    for (let i = 0; i < order.orderItems.length; i++) {
      if (order.orderItems[i].dvyType === 1) {
        isOnlineDelivery = 0
        break
      }
    }
    order.isOnlineDelivery = isOnlineDelivery
  })
}

/**
 * 每页数
 * @param val
 */
const sizeChangeHandle = (val) => {
  page.value.pageSize = val
  page.value.currentPage = 1
  getDataList(page.value)
}

/**
 * 当前页
 * @param val
 */
const currentChangeHandle = (val) => {
  page.value.currentPage = val
  getDataList(page.value)
}

/**
 * 导航选择状态
 */
const selectNav = (e) => {
  const sts = e.currentTarget.dataset.sts
  activeName.value = parseInt(sts)
  status.value = activeName.value === 0 ? null : parseInt(sts)
  page.value.currentPage = 1
  getDataList(page.value)
}

const orderStatus = (val) => {
  status.value = val
  activeName.value = val + ''
  getDataList(page.value)
}

const timeActive = ref(null)
/**
 * 根据选项设置时间
 * 1:今天 2:昨天 3: 近七天 4:近30天 5:近60天
 */
const setDateRange = (val) => {
  timeActive.value = val
  let startDay = null
  let endDay = null
  if (val === 1) {
    startDay = 0
    endDay = 0
  } else if (val === 2) {
    startDay = -1
    endDay = -1
  } else if (val === 3) {
    startDay = -7
    endDay = -1
  } else if (val === 4) {
    startDay = -30
    endDay = -1
  } else {
    return
  }
  // 开始时间
  const startTime = moment().add(startDay, 'days').startOf('days').format('LL')
  // 结束时间
  const endTime = moment().add(endDay, 'days').endOf('days').format('LL')
  dateRange.value = [startTime, endTime]
}

/**
 * 查看订单
 * @param val
 */
const onAddOrUpdate = (val) => {
  router.push({
    path: '/order/order-info/index',
    query: {
      orderNumber: val
    }
  })
}

/**
 * 退款路由跳转
 * @param val
 */
const refundRoute = (val) => {
  routerStore.updateIncludePageList('order-orderRefund')
  routerStore.pushHisPageList('order-orderRefund')
  router.push({
    path: '/order/order-refund/index',
    query: { orderNumber: val }
  })
}

const getWaitingConsignmentExcel = () => {
  getDataList(page.value)
}

const uploadVisible = ref(false)
const spuUploadRef = ref(null)
const uploadSpu = () => {
  dataForm.value.startTime = dateRange.value === null ? null : dateRange.value[0]
  dataForm.value.endTime = dateRange.value === null ? null : dateRange.value[1]
  uploadVisible.value = true
  nextTick(() => {
    spuUploadRef.value?.init()
  })
}

/**
 * 清空按钮
 */
const clear = () => {
  dataForm.value = {}
  dateRange.value = []
  status.value = null
  timeActive.value = null
}

/**
 * 搜索查询
 * @param newData
 */
const onSearch = (newData = false) => {
  page.value.currentPage = 1
  getDataList(page.value, null, newData)
}

/**
 * 更改订单金额
 */
const orderAmountUpdateVisible = ref(false)
const orderAmountUpdateRef = ref(null)
const editAmount = (order) => {
  orderAmountUpdateVisible.value = true
  nextTick(() => {
    orderAmountUpdateRef.value?.init(order)
  })
}

/**
 * 发货
 */
const orderDeliveryUpdateRef = ref(null)
const devyVisible = ref(false)
const changeOrder = (order) => {
  devyVisible.value = true
  nextTick(() => {
    orderDeliveryUpdateRef.value?.init(order)
  })
}

/**
 * 提货
 */
const orderStationVisible = ref(false)
const orderStationRef = ref(null)
const stationOrder = (order) => {
  orderStationVisible.value = true
  nextTick(() => {
    orderStationRef.value?.init(order)
  })
}

const lang = ref(localStorage.getItem('cbecB2cLang'))
const getSoldExcel = () => {
  if (!dateRange.value || dateRange.value.length < 2) {
    ElMessage.error($t('order.pleExpOrderFirst'))
    return
  }
  ElMessageBox.confirm(`${$t('order.exportReport')}`, $t('text.tips'), {
    confirmButtonText: $t('crud.filter.submitBtn'),
    cancelButtonText: $t('crud.filter.cancelBtn'),
    type: 'warning'
  }).then(() => {
    const loading = ElLoading.service({
      lock: true,
      target: '.page-order-order',
      customClass: 'export-load',
      background: 'transparent',
      text: $t('formData.exportIng')
    })
    http({
      url: http.adornUrl('/order/order/soldExcel'),
      method: 'get',
      params: http.adornParams({
        orderNumber: dataForm.value.orderNumber,
        orderType: dataForm.value.orderType,
        payType: dataForm.value.payType,
        lang: lang.value !== 'zh_CN' ? 1 : 0,
        receiver: dataForm.value.receiver,
        mobile: dataForm.value.mobile,
        status: status.value,
        dvyType: dataForm.value.dvyType,
        stationName: dataForm.value.stationName,
        refundStatus: dataForm.value.refundStatus,
        startTime: dateRange.value === null ? null : dateRange.value[0], // 开始时间
        endTime: dateRange.value === null ? null : dateRange.value[1] // 结束时间
      }),
      responseType: 'blob' // 解决文件下载乱码问题
    }).then(({ data }) => {
      loading.close()
      const blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8' })
      const fileName = $t('order.orderInfCollationXls')
      const elink = document.createElement('a')
      if ('download' in elink) { // 非IE下载
        elink.download = fileName
        elink.style.display = 'none'
        elink.href = URL.createObjectURL(blob)
        document.body.appendChild(elink)
        elink.click()
        URL.revokeObjectURL(elink.href) // 释放URL 对象
        document.body.removeChild(elink)
      } else { // IE10+下载
        navigator.msSaveBlob(blob, fileName)
      }
    }).catch(() => {
      loading.close()
    })
  })
}

/**
 * 预售时间显示为 ’YYYY-MM-DD‘
 */
const preSellTimeShowHandler = (val) => {
  if (!val) return val
  return val.slice(0, -8).trim()
}
</script>

<style lang="scss" scoped>
  @use './index.scss';
</style>

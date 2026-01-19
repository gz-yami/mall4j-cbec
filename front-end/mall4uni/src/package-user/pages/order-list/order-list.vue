<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <z-paging
    ref="pagingRef"
    v-model="listPar"
    :paging-style="{'top': '0', 'background': '#f4f4f4'}"
    :empty-view-text="$t('noOrderTips')"
    :empty-view-img="orderEmptyImg"
    :empty-view-img-style="{ height: '500rpx',width: '550rpx' }"
    :auto-show-back-to-top="false"
    @query="loadOrderData"
    @list-change="pagingChange"
  >
    <view class="Mall4j container page-order-list">
      <!-- 头部菜单 -->
      <view class="header">
        <view class="search-bar">
          <!-- #ifdef H5-->
          <image
            class="back-icon"
            src="@/static/images/icon/back.png"
            @tap="backToPre"
          />
          <!-- #endif -->
          <view class="search-box">
            <input
              v-model="prodName"
              :placeholder="$t('enterKeywordSearch')"
              class="sear-input"
              confirm-type="search"
              @confirm="Search"
            >
            <image
              src="@/static/images/icon/search.png"
              class="search-img"
            />
          </view>
        </view>
        <view class="order-tit">
          <text
            data-sts="0"
            :class="sts===0?'on':''"
            @tap="onStsTap"
          >
            {{ $t('all') }}
          </text>
          <text
            data-sts="1"
            :class="sts===1?'on':''"
            @tap="onStsTap"
          >
            {{ $t('toBePaid') }}
          </text>
          <text
            data-sts="2"
            :class="sts===2?'on':''"
            @tap="onStsTap"
          >
            {{ $t('toBeDelivered') }}
          </text>
          <text
            data-sts="3"
            :class="sts===3?'on':''"
            @tap="onStsTap"
          >
            {{ $t('toBeReceived') }}
          </text>

          <!-- 待使用状态暂时未添加 -->
          <!-- <text data-sts="4" :class="sts==4?'on':''" @tap="onStsTap">{{ $t('toBeUsed') }}</text> -->

          <text
            data-sts="5"
            :class="sts===5?'on':''"
            @tap="onStsTap"
          >
            {{ $t('completed') }}
          </text>
        </view>
      </view>
      <!-- 头部菜单 end -->

      <!-- 订单列表 -->
      <view class="main">
        <block
          v-for="(item, orderNumber) in list"
          :key="orderNumber"
        >
          <view class="prod-item">
            <!-- 订单编号 -->
            <view class="order-num">
              <view class="shop-box">
                <view class="shop-icon">
                  <image
                    src="@/static/images/icon/icon_shop.png"
                    class="icon"
                  />
                </view>
                <view class="shop-name">
                  {{ item.shopName }}
                </view>
              </view>
              <view class="order-state">
                <text :class="['order-sts' , {red: (item.status < 5) }]">
                  {{
                    ['', $t('toBePaid'), $t('toBeDelivered'), $t('toBeReceived'), $t('toBeEvaluated'), $t('completed'), $t('cancelled'), $t('inAGroup')][item.status]
                  }}
                </text>
                <view class="red">
                  <!-- returnMoneySts 退款状态 1.买家申请 2.卖家接受 3.买家发货 4.卖家收货 5.退款成功 6.买家撤回申请(无) 7.商家拒绝(无) -1.退款关闭 -->
                  <!-- refundStatus 订单退款状态参考（1:申请退款 2:退款成功 3:部分退款成功 4:退款失败 -->
                  <text v-if="item.returnMoneySts===0" />
                  <text
                    v-if="item.returnMoneySts===1||item.returnMoneySts===2||item.returnMoneySts===3||item.returnMoneySts===4"
                  >
                    ({{ $t('refunding') }}
                    <block v-if="item.status <= 2 && item.deliveryCount && item.productNums > item.deliveryCount">
                      , {{ $t('partialDelivery') }}
                    </block>
                    )
                  </text>
                  <!-- 部分退款关闭（退款中）item.refundStatus === 1 && item.returnMoneySts === -1 -->
                  <text v-if="item.refundStatus === 1 && item.returnMoneySts === -1">
                    ({{ $t('refunding') }}
                    <block v-if="item.status <= 2 && item.deliveryCount && item.productNums > item.deliveryCount">
                      , {{ $t('partialDelivery') }}
                    </block>
                    )
                  </text>
                  <!-- 退款完成 -->
                  <text v-if="item.returnMoneySts === 5 && item.refundStatus !== 3">
                    ({{ $t('refundComplete') }})
                  </text>
                  <!-- 部分退款完成refundStatus === 3 && returnMoneySts === 5 -->
                  <text
                    v-if="item.returnMoneySts === 5 && item.refundStatus === 3 && (item.status > 2 || (item.status === 2 && !item.deliveryCount))"
                  >
                    ({{ $t('partialRefundCompleted') }})
                  </text>
                  <!-- 部分发货 && 部分退款 -->
                  <text
                    v-if="item.status <= 2 && item.deliveryCount && item.productNums > item.deliveryCount && item.returnMoneySts === 5 && item.refundStatus === 3"
                  >
                    ({{ $t('partialRefund') }}, {{ $t('partialDelivery') }})
                  </text>
                  <!-- 部分发货 -->
                  <text
                    v-if="!item.refundStatus && item.status <= 2 && item.deliveryCount && item.productNums > item.deliveryCount"
                  >
                    ({{ $t('partialDelivery') }})
                  </text>
                  <!-- 退款关闭 -->
                  <text
                    v-if="item.refundStatus !== 1 && item.returnMoneySts===-1 && (item.status > 2 || (item.status === 2 && !item.deliveryCount))"
                  >
                    ({{ $t('refundClosed') }})
                  </text>
                  <!-- 退款关闭, 部分发货 -->
                  <text
                    v-if="item.refundStatus !== 1 && item.returnMoneySts===-1 && item.status <= 2 && item.deliveryCount && item.productNums > item.deliveryCount"
                  >
                    ({{ $t('refundClosed') }}, {{ $t('partialDelivery') }})
                  </text>
                  <text v-if="item.refundStatus===1">
                    ({{ $t('refunding') }})
                  </text>
                </view>
              </view>
            </view>
            <!-- 商品列表 -->
            <!-- 一个订单单个商品的显示 -->
            <block v-if="item.orderItemDtos">
              <view class="prod-list">
                <block
                  v-for="(prod, prodId) in item.orderItemDtos"
                  :key="prodId"
                >
                  <view class="item-cont-box">
                    <!-- 预售 icon -->
                    <view
                      v-if="item.preSaleTime && item.preSaleTime!=='null'"
                      class="pre-sale"
                    >
                      <text class="pre-sale-icon">
                        {{ $t('preSale') }}
                      </text>
                      <text class="preSaleTime">
                        {{ $t('expect') }}&nbsp;{{ tsToDate(item.preSaleTime) }}&nbsp;{{ $t('startDelivery') }}
                      </text>
                    </view>
                    <view
                      class="item-cont"
                      :data-ordernum="item.orderNumber"
                      @tap="toOrderDetailPage(item)"
                    >
                      <view class="prod-pic">
                        <image
                          v-if="prod.pic && !prod.isPicError"
                          mode="aspectFill"
                          :src="util.checkFileUrl(prod.pic)"
                          @error="handlePicError(prod)"
                        />
                        <image
                          v-else
                          src="@/static/images/icon/def.png"
                          mode="aspectFill"
                        />
                      </view>
                      <view class="prod-info-box">
                        <view class="prod-info">
                          <view class="prodname">
                            {{ prod.prodName }}
                          </view>
                        </view>
                        <view class="prod-info-cont-sku">
                          <text class="quantity">
                            {{ $t('quantity') + ':' + prod.prodCount }}
                          </text>
                          {{ (prod.properties && prod.properties.split(';').join('；')) || '' }}
                        </view>
                        <view class="prod-info">
                          <view class="sku-box">
                            <!-- 拼团icon -->
                            <text
                              v-if="item.orderType"
                              class="spell-group-icon"
                            >
                              <!-- orderType 订单类型(0普通订单 1团购订单 2秒杀订单) -->
                              {{ ['', $t('aGroup'), $t('spike'), $t('integral')][item.orderType] }}
                            </text>
                            <!-- 拼团icon end -->
                            <!-- 配送类型 1:快递 2:自提 3：无需快递)-->
                            <text
                              v-if="item.dvyType === 2"
                              class="spell-group-icon"
                            >
                              {{ $t('pickitUp') }}
                            </text>
                          </view>
                        </view>
                        <view class="price-nums">
                          <view class="prodprice">
                            <text
                              v-if="prod.useScore > 0&&item.orderType===3"
                              class="big-num score-price"
                              :decode="true"
                            >
                              {{ '&nbsp;' + prod.useScore + '&nbsp;' }}
                              <text class="">
                                {{ $t('integral') }}
                              </text>
                            </text>
                            <text v-if="(prod.useScore>0 && prod.price>=0)&&item.orderType===3">
                              +
                            </text>
                            <text
                              v-if="prod.price>=0"
                              class="big-num"
                              :class="{'score-price':prod.useScore > 0&&item.orderType===3}"
                            >
                              <text class="price-symbol fs-24">
                                {{ useCurrencyStore().defMark }}
                              </text>
                              {{ wxs.parsePrice(prod.price)[0] }}
                              <text class="fs-24">
                                .{{ wxs.parsePrice(prod.price)[1] }}
                              </text>
                            </text>
                          </view>
                        </view>
                      </view>
                    </view>
                  </view>
                </block>
              </view>
            </block>
            <!-- 一个订单多个商品时的显示 -->
            <block v-else>
              <view
                class="item-cont"
                :data-ordernum="item.orderNumber"
                @tap="toOrderDetailPage(item)"
              >
                <scroll-view
                  :scroll-x="true"
                  scroll-left="0"
                  :scroll-with-animation="false"
                  class="categories"
                >
                  <block
                    v-for="(prod, prodId) in item.orderItemDtos"
                    :key="prodId"
                  >
                    <view class="prod-pic">
                      <image
                        v-if="prod.pic && !prod.isPicError"
                        mode="aspectFill"
                        :src="util.checkFileUrl(prod.pic)"
                        @error="handlePicError(prod)"
                      />
                      <image
                        v-else
                        src="@/static/images/icon/def.png"
                        mode="aspectFill"
                      />
                    </view>
                  </block>
                </scroll-view>
              </view>
            </block>
            <!-- 总计 -->
            <view class="total-num">
              <view
                class="prodprice"
                :class="{'score-price': item.userScore > 0}"
              >
                {{ $t('total') }}
                <lh-tooltip
                  v-if="useCurrencyStore().currency !== useCurrencyStore().defCurrency"
                  width="350"
                  :content="$t('ratePriceTip')"
                >
                  <icon
                    type="warn"
                    color="#999999"
                    size="14"
                    class="warn-tip"
                  />
                </lh-tooltip>：

                <show-price
                  :type="1"
                  :price="item.actualTotal"
                  :score="item.userScore"
                  custom-class="price-red"
                  :is-by-side="false"
                  :is-align-center="false"
                />
              </view>
            </view>
            <view
              v-if="item.btns&&item.btns.length"
              class="prod-foot"
            >
              <view
                v-if="item.btns"
                class="btn"
              >
                <view
                  v-if="item.btns.length>3"
                  class="btn-more"
                >
                  <btns
                    :btn-list="getbtns(item.btns,true)"
                    @select="selectBtn($event,getbtns(item.btns,true))"
                  >
                    {{ $t('more') }}
                  </btns>
                </view>
                <view
                  v-for="(btn,idx) in getbtns(item.btns)"
                  :key="idx"
                  :class="btn.class"
                  class="ellipsis"
                  @tap="btnsTap(btn.tap,btn.params)"
                >
                  {{ btn.text }}
                </view>
              </view>
            </view>
          </view>
        </block>
      </view>
      <!-- 订单列表 end -->

      <!-- 删除弹窗  -->
      <confirm-pop
        v-model:show-pop="showPop"
        :pop-content="popContent"
        :show-title="false"
        :show-cancel="showCancel"
        @confirm="confirm"
      />
    </view>
  </z-paging>
</template>

<script setup>
import util from '@/utils/util.js'
import btns from './components/btns.vue'
import temOrderEmptyImg from '@/package-user/static/images/empty-img/empty_order.png'
import { useCurrencyStore } from '@/stores/modules/currency'

let orderEmptyImg = ''

// #ifndef MP-WEIXIN
orderEmptyImg = temOrderEmptyImg
// #endif

const wxs = number()

const sts = ref(0)
/**
 * 生命周期函数--监听页面加载
 */
onLoad((options) => {
  if (options.sts) {
    sts.value = Number(options.sts)
  }
})

/**
 * 生命周期函数--监听页面显示
 */
onShow(() => {
  if (uni.getStorageSync('cbecB2cOrderListLoad')) {
    uni.removeStorageSync('cbecB2cOrderListLoad')
    pagingRef.value.reload()
  }
  setTimeout(() => {
    // 加载导航标题
    uni.setNavigationBarTitle({
      title: $t('orderList')
    })
  }, 100)
  uni.pageScrollTo({
    scrollTop: 0,
    duration: 0
  })
})

/**
 * 用户点击右上角分享 (勿删)
 */
onShareAppMessage(() => {})

const isLoaded = ref(false)
const prodName = ref('')
const list = ref([])
const listPar = ref([])
const pagingRef = ref(null)
/**
 * 加载订单数据
 */
const loadOrderData = (pageNum, pageSize) => {
  isLoaded.value = false
  const params = {
    url: '/p/myOrder/myOrder',
    method: 'get',
    data: {
      current: pageNum,
      size: pageSize,
      status: sts.value,
      prodName: prodName.value
    }
  }
  http.request(params).then(({ data }) => {
    isLoaded.value = true
    data.records.forEach(orderItem => {
      orderItem.totalCounts = 0
      if (orderItem.returnMoneySts === null) {
        orderItem.returnMoneySts = 0
      }
      orderItem.orderItemDtos.forEach(prod => {
        orderItem.totalCounts += prod.prodCount
      })
    })
    pagingRef.value.complete(data.records)
  })
}

const pagingChange = (listData) => {
  getBtnCount(listData)
  list.value = listData
}

/**
 * 状态点击事件
 */
const onStsTap = (e) => {
  sts.value = Number(e.currentTarget.dataset.sts)
  prodName.value = ''
  uni.pageScrollTo({
    scrollTop: 0,
    duration: 0
  })
  pagingRef.value.reload()
}

/**
 * 查看物流
 */
const toDeliveryPage = (orderNumber) => {
  uni.navigateTo({
    url: '/package-user/pages/logistics-info/logistics-info?orderNumber=' + orderNumber
  })
}

const popContent = ref({})
let confirmPopFun = ''
let confirmPopParam = null
const showPop = ref(false) // ConfirmPops 显示
/**
 * 取消订单
 */
const onCancelOrder = (orderNumber) => {
  popContent.value = {
    content: $t('cancelOrderTips'),
    cancelText: $t('no'),
    confirmText: $t('yes')
  }
  confirmPopFun = cancelOrderFun
  confirmPopParam = orderNumber
  showPop.value = true
}

const cancelOrderFun = () => {
  const params = {
    url: '/p/myOrder/cancel/' + confirmPopParam,
    method: 'PUT'
  }
  http.request(params).then(() => {
    confirmPopParam = null
    pagingRef.value.reload()
  })
}

const showCancel = ref(true)
/**
 * 付款
 * @param orderNumber 订单编号
 * @param orderType 订单类型
 * @param dvyType 物流方式
 */
const onPayAgain = (orderNumber, orderType, dvyType) => {
  const params = {
    url: '/p/order/getOrderPayInfoByOrderNumber',
    method: 'GET',
    data: {
      orderNumbers: orderNumber
    }
  }
  http.request(params).then(({ data }) => {
    // 订单过期时间
    let endTime = data.endTime.replace(/-/g, '/')
    const nowTime = new Date().getTime() // 现在时间（时间戳）
    endTime = new Date(endTime).getTime() // 结束时间（时间戳）
    const time = (endTime - nowTime) / 1000 // 距离结束的毫秒数
    if (time > 0) {
      uni.navigateTo({
        url: `/pages/pay-way/pay-way?orderNumbers=${orderNumber}&orderType=${orderType}&dvyType=${dvyType}`
      })
    } else {
      popContent.value = {
        content: $t('orderExpired'),
        cancelText: $t('cancel'),
        confirmText: $t('confirm')
      }
      confirmPopFun = resetOrderList
      showCancel.value = false
      showPop.value = true
    }
  })
}

const resetOrderList = () => {
  pagingRef.value.reload()
  showCancel.value = true
}

/**
 * 查看订单详情
 */
const toOrderDetailPage = (order) => {
  uni.navigateTo({
    url: '/package-user/pages/order-detail/order-detail?orderNum=' + order.orderNumber
  })
}

/**
 * 确认收货
 */
const onConfirmReceive = async (orderNumber) => {
  popContent.value = {
    content: $t('haveRceivedGoods'),
    confirmText: $t('confirm')
  }
  confirmPopFun = confirmReceiptFun
  confirmPopParam = orderNumber
  showPop.value = true
}

// 确认收货
const confirmReceiptFun = () => {
  const params = {
    url: '/p/myOrder/receipt/' + confirmPopParam,
    method: 'PUT'
  }
  http.request(params).then(() => {
    confirmPopParam = null
    pagingRef.value.reload()
  })
}

let delOrderNumber = null
// 删除已完成||已取消的订单
const delOrderList = (orderNumber) => {
  delOrderNumber = orderNumber
  popContent.value = {
    content: $t('sureDeleteOrder'),
    confirmText: $t('delete')
  }
  showPop.value = true
  confirmPopFun = delMyOrderFun
}

// 删除订单
const delMyOrderFun = () => {
  const params = {
    url: '/p/myOrder/' + delOrderNumber,
    method: 'DELETE'
  }
  http.request(params).then(({ data }) => {
    uni.showToast({
      icon: 'none',
      mask: true,
      title: data,
      complete: () => {
        setTimeout(() => {
          pagingRef.value.reload()
        }, 1500)
      }
    })
  })
}

/**
 * 图片加载失败时显示默认图片
 */
const handlePicError = (prod) => {
  prod.isPicError = true
}

// 格式化时间
const tsToDate = (value) => {
  const arr = value.split(' ')
  return arr[0]
}

const Search = () => {
  pagingRef.value.reload()
}
/**
 * 返回个人中心
 */
const backToPre = () => {
  uni.switchTab({
    url: '/pages/user/user'
  })
}

// 获取按键
const getBtnCount = (list) => {
  if (!list) return
  return list.forEach(item => {
    const count = 0
    const btns = []
    if (item.status == 1 || item.status == 3 || item.status == 5 || (item.status == 5 && item.orderItemDtos[0].commSts == 0) || (item.status == 6 && item.returnMoneySts == 5 && item.refundStatus !== 3)) {
      if ((item.status == 5 || item.status == 6) && (item.returnMoneySts > 4 || item.returnMoneySts == -1 || item.returnMoneySts == null || item.returnMoneySts == '')) {
        btns.push({
          text: $t('delOrder'),
          tap: delOrderList,
          params: [item.orderNumber],
          class: 'button'
        })
      }
      if (item.status == 1) {
        btns.push({
          text: $t('cancelOrder'),
          tap: onCancelOrder,
          params: [item.orderNumber],
          class: 'button'
        })
        btns.push({
          text: $t('continuePayment'),
          tap: onPayAgain,
          params: [item.orderNumber, item.orderType, item.dvyType],
          class: 'button red'
        })
      }
      if (item.status == 3 && item.refundStatus != 1) {
        btns.push({
          text: $t('confirmRceipt'),
          tap: onConfirmReceive,
          params: [item.orderNumber],
          class: 'button warn'
        })
      }
    } else {
      if ((item.status == 5 || item.status == 6) && (item.returnMoneySts > 4 || item.returnMoneySts == -1 || item.returnMoneySts == null || item.returnMoneySts == '')) {
        btns.push({
          text: $t('delOrder'),
          tap: delOrderList,
          params: [item.orderNumber],
          class: 'button'
        })
      }
      if (item.status === 2 && item.deliveryCount && item.productNums > item.deliveryCount) {
        btns.push({
          text: $t('viewLogistics'),
          tap: toDeliveryPage,
          params: [item.orderNumber],
          class: 'button'
        })
      }
    }
    item.btns = btns
    item.btnCount = count
  })
}

// 按键数大于三分割
const getbtns = (btns, isMore = false) => {
  let list = []
  try {
    if (isMore) {
      list = btns.slice(0, -3)
    } else {
      if (btns.length > 3) {
        list = btns.slice(-3)
      } else {
        list = btns
      }
    }
  } catch (e) {}
  return list
}

// 按键事件
const btnsTap = (tap, params) => {
  tap(...params)
}

// 更多  按键  事件
const selectBtn = (e, btns) => {
  const { tap, params } = btns[e]
  tap(...params)
}

// ConfirmPop 确定事件
const confirm = () => {
  showPop.value = false
  confirmPopFun()
}

</script>

<style lang="scss" scoped>
@use "order-list";
</style>

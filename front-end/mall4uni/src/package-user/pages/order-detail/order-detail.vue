<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <view
    v-show="isDataLoad"
    class="Mall4j container page-order-detail"
    @click.stop="onCloseTips"
  >
    <view class="order-detail">
      <!-- 订单状态 -->
      <view class="order-status">
        <view class="status-bg">
          <image src="@/package-user/static/images/icon/status-bg.png" />
        </view>
        <view class="status-text">
          <image
            v-if="orderDetail.status===5||orderDetail.status===4"
            class="icon-clock"
            src="@/package-user/static/images/icon/finish.png"
          />
          <image
            v-else-if="orderDetail.status ===6"
            class="icon-clock"
            src="@/package-user/static/images/icon/clock.png"
          />
          <image
            v-else
            class="icon-clock"
            src="@/package-user/static/images/icon/icon_clock.png"
          />
          {{
            ['',
             $t('waitingBuyerPay'),
             $t('waitingForDelivery1'),
             $t('waitingForReceipt'),
             $t('orderCompleted'),
             $t('orderCompleted'),
             $t('orderCancelled'),
             $t('toShare')][orderDetail.status]
          }}
        </view>
        <view class="sratus-tip">
          <template v-if="orderDetail.status===1 && entTimeObj">
            {{ $t('remaining') }}{{
              entTimeObj.hou ? entTimeObj.hou + $t('time') : ""
            }}{{
              entTimeObj.min ? entTimeObj.min + $t('minute') : ''
            }}{{ entTimeObj.sec ? entTimeObj.sec + $t('second') : '' }}，
          </template><!-- 剩余支付时间 -->
          {{
            ['',
             $t('waitingBuyerPay'),
             deliveryInfo?.deliveryDto? $t('sratusTip2'): $t('sratusTip1'),
             $t('waitingForReceiptTip'),
             $t('goodsReceived'),
             $t('goodsReceived'),
             $t('orderCancelledTips'),
             $t('groupJoinTips1')][orderDetail.status]
          }}
        </view>
      </view>

      <!-- 配送地址(虚拟商品不展示) -->
      <view
        v-if="orderDetail.dvyType !== 2 && orderDetail.userAddrDto && orderDetail.orderMold !== 1"
        class="address-box"
      >
        <!-- 快递信息 status订单状态  deliveryInfo:物流信息    dvyType：配送类型 1:快递 2:自提 3：无需快递 -->
        <view
          v-if="(orderDetail.status > 2||(orderDetail.status===2&&deliveryInfo?.deliveryDto)) && orderDetail.status <6 && (orderDetail.dvyType===1 || orderDetail.dvyType===0)"
          class="address-box-item"
          @tap="toDeliveryPage"
        >
          <view class="img">
            <image src="@/package-user/static/images/icon/icon_logistics.png" />
          </view>
          <view class="address-box-info">
            <view class="user-info">
              <view class="logistics name">
                {{ $t('logisticsStatus') }}
              </view>
              <view class="logisticsStatus phone">
                {{
                  ['',
                   $t('waitingBuyerPay'),
                   deliveryInfo?.deliveryDto ? $t('partialDelivery') :$t('waitingForDelivery1'),
                   $t('shipped'),
                   $t('orderCompleted'),
                   $t('orderCompleted'),
                   $t('orderCancelled'),
                   $t('toShare')][orderDetail.status]
                }}
              </view>
            </view>
            <!-- 已支付  待商家发货 -->
            <view
              v-if="orderDetail.status === 2 &&orderDetail.payTime !== null"
              class="address-detail"
            >
              <!-- 部分发货 -->
              <template v-if="deliveryInfo?.deliveryDto">
                <template v-if="deliveryInfo.deliveryDto?.traces?.[0]">
                  {{
                    deliveryInfo.deliveryDto.traces[0].acceptTime + ""
                  }}&nbsp;&nbsp;{{ deliveryInfo.deliveryDto.traces[0].acceptStation }}
                </template>
                <template v-else>
                  {{ tsToDate(orderDetail.dvyTime) }}&nbsp;&nbsp;{{ $t('merchantHasShippedWa') }}
                </template>
              </template>
              <!--  待商家发货 -->
              <template v-else>
                {{ tsToDate(orderDetail.payTime) }}&nbsp;&nbsp;{{ $t('sratusTip1') }}
              </template>
            </view>
            <!-- 已收货 -->
            <view
              v-else-if="
                orderDetail.status === 5 &&
                  orderDetail.finallyTime !== null"
              class="address-detail"
            >
              {{ tsToDate(orderDetail.finallyTime) }}&nbsp;&nbsp;{{ $t('signedInReceived') }}
            </view>
            <!-- 物流信息 -->
            <view
              v-else-if="deliveryInfo && deliveryInfo.deliveryDto &&deliveryInfo.deliveryDto.traces&&deliveryInfo.deliveryDto.traces[0]"
              class="address-detail"
            >
              {{
                deliveryInfo.deliveryDto.traces[0].acceptTime + ""
              }}&nbsp;&nbsp;{{ deliveryInfo.deliveryDto.traces[0].acceptStation }}
            </view>
            <!-- 待揽件 -->
            <view
              v-else-if="orderDetail.status === 3 && orderDetail.dvyTime !== null"
              class="address-detail"
            >
              {{ tsToDate(orderDetail.dvyTime) }}&nbsp;&nbsp;{{ $t('merchantHasShippedWa') }}
            </view>
          </view>
          <image
            class="arrow-right-icon"
            src="@/package-user/static/images/icon/arrow-right.png"
          />
        </view>
        <view class="address-box-item address-box-item-info">
          <view class="box-item">
            <view class="box-label img">
              <image src="@/package-user/static/images/icon/icon_address.png" />
            </view>
            <view class="box-info">
              <view class="user-info">
                <view class="name">
                  {{ orderDetail.userAddrDto.receiver }}
                </view>
                <view class="phone">
                  {{ phoneDesensitization(orderDetail.userAddrDto.mobile) }}
                </view>
              </view>
            </view>
          </view>
          <view class="box-item">
            <view class="box-label img visibility">
              <image src="@/package-user/static/images/icon/icon_address.png" />
            </view>
            <view class="box-info">
              <view class="address-detail">
                {{ $t('address') }}：
                {{ orderDetail.userAddrDto.province }}
                <span v-if="orderDetail.userAddrDto.city">
                  / {{ orderDetail.userAddrDto.city }}
                </span>
                <span v-if="orderDetail.userAddrDto.area">
                  / {{ orderDetail.userAddrDto.area }}
                </span>
                / {{ orderDetail.userAddrDto.addr }}
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 商品信息 -->
      <view class="prod-item">
        <view
          v-for="(item, prodIndex) in orderDetail.orderItemDtos"
          :key="prodIndex"
          class="prod-box"
        >
          <view
            v-if="item.preSellTime"
            class="pre-sale"
          >
            <!-- 预售-->
            <view class="a-icon preSaleTime-icon">
              {{ $t('preSale') }}
            </view>
            <view class="sku-name preSaleTime">
              {{ $t('expect') }}&nbsp;{{ tsToDate(item.preSellTime) }}&nbsp;{{ $t('startDelivery') }}
            </view>
          </view>
          <view
            class="item-cont"
            @tap="toProdDetail(item.prodId)"
          >
            <view class="prod-pic">
              <image
                v-if="item.pic && !item.isPicError"
                mode="aspectFill"
                :src="util.checkFileUrl(item.pic)"
                @error="handlePicError(item)"
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
                  <view class="text">
                    {{ item.prodName }}
                  </view>
                </view>
              </view>
              <view class="prod-info">
                <view class="suk-con sku-info">
                  <view class="sku-name">
                    <text class="quantity">
                      {{ $t('quantity') + ':' + item.prodCount }}
                    </text>
                    {{ item.properties || '' }}
                  </view>
                </view>
              </view>
              <view
                class="suk-con sku-info"
                style="margin-left: 20rpx"
              >
                <view
                  v-if="orderDetail.orderType && orderDetail.orderMold !== 1"
                  class="a-icon"
                >
                  {{ ['', $t('aGroup'), $t('spike'), $t('integral')][orderDetail.orderType] }}
                </view>
              </view>

              <!-- 价格 -->
              <view class="price-nums">
                <view
                  class="prodprice"
                  :class="{'score-price':orderDetail.orderType===3}"
                >
                  <text
                    v-if="orderDetail.orderType===3"
                    class="big-num"
                  >
                    {{ item.useScore / item.prodCount }} {{ $t('integral') }}
                  </text>
                  <text
                    v-if="item.price && orderDetail.orderType===3"
                    class="big-num"
                  >
                    {{ `&nbsp;+&nbsp;` }}
                  </text>
                  <text
                    v-if="item.price >0"
                    class="big-num"
                  >
                    <text
                      class="price-symbol fs-24"
                    >
                      {{ useCurrencyStore().defMark }}
                    </text>
                    {{ wxs.parsePrice(item.price)[0] }}
                    <text class="fs-24">
                      .{{ wxs.parsePrice(item.price)[1] }}
                    </text>
                  </text>
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 商品总额与优惠明细 -->
      <view class="order-msg payment">
        <view class="msg-item">
          <view class="item price">
            <view class="item-tit">
              {{ $t('actualPayment') }}
            </view>
            <view class="item-txt">
              <!-- 暂时  无法获取   -->
              <text class="gray goodsNumber">
                {{ $t('inTotal') }}&nbsp;{{ prodCount }}&nbsp;{{ $t('items') }}&nbsp;
              </text>
              <text
                v-if="orderDetail.actualTotal >= 0"
                class="big-num"
              >
                <text class="fs-24">
                  {{ useCurrencyStore().defMark }}
                </text>
                {{ wxs.parsePrice(orderDetail.actualTotal)[0] }}
                <text class="fs-24">
                  .{{ wxs.parsePrice(orderDetail.actualTotal)[1] }}
                </text>
              </text>
            </view>
          </view>
          <!-- 商品总额 -->
          <view class="item">
            <view class="item-tit">
              {{ $t('comTotal') }}
            </view>
            <view class="item-txt">
              {{ useCurrencyStore().defMark }}{{ wxs.parsePrice(orderDetail.total)[0] }}.{{ wxs.parsePrice(orderDetail.total)[1] }}
            </view>
          </view>
          <!-- 商品运费(虚拟商品不展示) -->
          <view
            v-if="orderDetail.orderMold !== 1"
            class="item"
          >
            <view class="item-tit">
              {{ $t('orderFreight') }}
              <view
                v-if="orderDetail.orderType !== 3 && commodityFreight > 0"
                class="icon-tip"
                :class="{'is-show-tips':isShowTips}"
                :data-text-tips="$t('freightTips')"
              >
                <icon
                  type="warn"
                  size="15"
                  color="grey"
                  @tap.stop="onShowTips"
                />
              </view>
            </view>
            <view
              class="transfee-tips"
              style="display: flex;"
            >
              <view class="item-txt">
                <view
                  v-if="orderDetail.transfee && orderDetail.status === 2 && orderDetail.isRefunding"
                  class="item-txt gray-tips-con"
                  @tap="showTransfeeTips=true"
                >
                  <icon
                    type="info"
                    color="#666"
                    size="14"
                    class="info-tips"
                  />
                  <text
                    v-if="showTransfeeTips"
                    class="mask"
                    @tap.stop="showTransfeeTips=false"
                  />
                  <text
                    v-if="showTransfeeTips"
                    class="tips"
                  >
                    {{ $t('refundBeforeShippingTips') }}
                  </text>
                </view>
                <view class="align-items">
                  {{ useCurrencyStore().defMark }}{{ wxs.parsePrice(Math.abs(commodityFreight))[0] }}.{{ wxs.parsePrice(Math.abs(commodityFreight))[1] }}
                </view>
              </view>
            </view>
          </view>
          <!-- 运费减免 -->
          <view
            v-if="orderDetail.freeTransfee || orderDetail.platformFreeFreightAmount"
            class="item"
          >
            <view class="item-tit">
              {{ $t('shippingDiscount') }}
            </view>
            <view class="item-txt">
              -{{ useCurrencyStore().defMark }}{{ wxs.parsePrice(Math.abs(orderDetail.freeTransfee||orderDetail.platformFreeFreightAmount))[0] }}.{{ wxs.parsePrice(Math.abs(orderDetail.freeTransfee||orderDetail.platformFreeFreightAmount))[1] }}
            </view>
          </view>
        </view>
      </view>
      <!-- 商品总额与优惠明细 end -->

      <!-- 订单信息 -->
      <view class="order-msg">
        <view class="msg-item">
          <view class="item orderNumber">
            <text class="item-tit">
              {{ $t('orderNumber') }}
            </text>
            <text class="item-txt">
              {{ orderNumber }}
            </text>
            <text
              class="copy-btn"
              @tap="copyText()"
            >
              {{ $t('copy') }}
            </text>
          </view>
          <view class="item">
            <text class="item-tit">
              {{ $t('orderTime') }}
            </text>
            <text class="item-txt">
              {{ orderDetail.createTime }}
            </text>
          </view>
        </view>
        <view
          v-if="(orderDetail.status>1&&orderDetail.status!==6) || orderDetail.orderMold !== 1 || (orderDetail.orderMold === 1 && virtualRemarkList)"
          class="msg-item"
        >
          <!-- 支付方式 -->
          <view
            v-if="orderDetail.status>1&&orderDetail.status!==6"
            class="item"
          >
            <text class="item-tit">
              {{ $t('paymentMethod') }}
            </text>
            <text class="item-txt">
              {{
                [
                  $t('integralPayment'),
                  $t('payWithWeChat'),
                  $t('PayWithAli'),
                  $t('payWithWeChat'),
                  $t('payWithWeChat'),
                  $t('payWithWeChat'),
                  $t('PayWithAli'),
                  $t('PayWithAli'),
                  $t('payWithWeChat'),
                  $t('balancePay'),
                  $t('paypalPay'),
                  '',
                  $t('payPalPayCardment')
                ][orderDetail.payType]
              }}
            </text>
          </view>
          <!-- 支付时间 -->
          <view
            v-if="orderDetail.status>1&&orderDetail.status!==6&&orderDetail.payTime"
            class="item"
          >
            <text class="item-tit">
              {{ $t('paymentTiem') }}
            </text>
            <text class="item-txt">
              {{ orderDetail.payTime }}
            </text>
          </view>
          <!-- 配送方式（虚拟商品不展示） -->
          <view
            v-if="orderDetail.orderMold !== 1"
            class="item"
          >
            <text class="item-tit">
              {{ $t('deliveryMethod') }}
            </text>
            <!-- 1卖家配送 2到店自提 3无需快递 4同城配送 -->
            <text class="item-txt">
              {{ [$t('sellerDelivery'), $t('sellerDelivery'), $t('sellerDelivery'), $t('noNeedDelivery'), $t('sameDelivery')][orderDetail.dvyType] }}
            </text>
          </view>
          <view
            v-if="orderDetail.orderType!==3"
            class="item"
          >
            <text class="item-tit">
              {{ $t('invoice.invoiceType') }}
            </text>
            <text class="item-txt">
              {{ orderDetail.orderInvoiceId ? $t('invoice.generalInvoice') : $t('invoice.noInvoice2') }}
            </text>
          </view>
          <!-- 全部留言：虚拟商品 && 留言不为空时显示 -->
          <view
            v-if="orderDetail.orderMold === 1 && virtualRemarkList.length > 0"
            class="item all-msg"
            @tap="showViewMsgPopup"
          >
            <view class="item-tit">
              {{ $t('allMsg') }}
            </view>
            <view
              class="item-txt"
              :class="{'pd': isExtraLong}"
            >
              <span
                v-for="(item, index) in virtualRemarkList"
                :key="index"
              >{{ item[0] }}&nbsp;&nbsp;&nbsp;&nbsp;{{ item[1] }}</span>
            </view>
            <view
              v-if="isExtraLong"
              class="more-msg"
            />
          </view>
        </view>
        <!-- 订单备注 -->
        <view
          v-if="orderDetail.remarks"
          class="msg-item"
        >
          <view class="item">
            <text class="item-tit">
              {{ $t('OrderNotes') }}
            </text>
            <text class="item-txt">
              {{ orderDetail.remarks ? orderDetail.remarks : '' }}
            </text>
          </view>
        </view>
      </view>
    </view>
    <!-- 查看留言弹窗（虚拟商品） -->
    <view
      v-if="showViewMsg"
      class="popup-hide"
    >
      <view class="popup-box virtual-goods-msg-pop">
        <view class="con-tit">
          <view class="tit-text">
            {{ $t('viewMsg') }}
          </view>
          <view
            class="close"
            @tap="closeMsgPopup"
          />
        </view>
        <view class="msg-pop-con">
          <view class="msg-list">
            <view
              v-for="(item, index) in orderDetail.virtualRemark"
              :key="index"
              class="msg-item"
            >
              <view class="item-con weak">
                {{ item.name }}
              </view>
              <view class="item-con">
                {{ item.value }}
              </view>
            </view>
          </view>
          <view class="pop-foot">
            <view
              class="foot-btn"
              @tap="closeMsgPopup"
            >
              {{ $t('gotIt') }}
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import util from '@/utils/util.js'
import uniCopy from '@/js_sdk/copy/uni-copy.js'
import Big from 'big.js'
import { useCurrencyStore } from '@/stores/modules/currency'

const wxs = number()

const phoneDesensitization = (val) => {
  if (!val) return val
  return val.replace(/(\d{3})\d*(\d{4})/, '$1****$2')
}

// 查看全部留言弹窗
const showViewMsg = ref(false)
const showTransfeeTips = ref(false)
const deliveryInfo = ref({})
// 订单详情
const orderDetail = ref({})

const prodCount = computed(() => {
  let count = 0
  if (orderDetail.value.orderItemDtos && orderDetail.value.orderItemDtos.length) {
    orderDetail.value.orderItemDtos.forEach(val => {
      count += val.prodCount
    })
  }
  return count
})

let endTime = null
const entTimeObj = computed(() => {
  // 支付倒计时
  if (endTime && endTime > 0) {
    let hou = parseInt(endTime % (60 * 60 * 24) / 3600)
    let min = parseInt(endTime % (60 * 60 * 24) % 3600 / 60)
    let sec = parseInt(endTime % (60 * 60 * 24) % 3600 % 60)
    hou = timeFormin(hou)
    min = timeFormin(min)
    sec = timeFormin(sec)
    return {
      hou,
      min,
      sec
    }
  }
  return null
})
const commodityFreight = computed(() => {
  if (!orderDetail.value.transfee && !orderDetail.value.freeTransfee) {
    return ''
  }
  return parseFloat(new Big(orderDetail.value.transfee || 0).plus(orderDetail.value.freeTransfee || 0))
})

const orderNumber = ref('')
/**
 * 生命周期函数--监听页面加载
 */
onLoad((options) => {
  if (options.orderNum) {
    orderNumber.value = options.orderNum
  }
})

const isDataLoad = ref(false)
/**
 * 生命周期函数--监听页面显示
 */
onShow(() => {
  // 加载导航标题
  uni.setNavigationBarTitle({
    title: $t('orderDetails')
  })
  uni.setNavigationBarColor({
    frontColor: '#ffffff',
    backgroundColor: '#F81A1A'
  })
  loadOrderDetail() // 请求订单详情数据
})

/**
 * 生命周期函数--监听页面隐藏
 */
onHide(() => {
  closeInterval()
})

/**
 * 生命周期函数--监听页面卸载
 */
onUnload(() => {
  closeInterval()
})

// 跳转商品详情页
const toProdDetail = (prodIdPar) => {
  uni.navigateTo({
    url: '/package-prod/pages/prod/prod?prodId=' + prodIdPar
  })
}

const unusedCount = ref(0) // 待使用的核销券
/**
 * 加载订单数据
 */
const loadOrderDetail = () => {
  const params = {
    hasCatch: true,
    url: '/p/myOrder/orderDetail',
    method: 'GET',
    data: {
      orderNumber: orderNumber.value
    }
  }
  return http.request(params).then(({ data }) => {
    orderDetail.value = data
    orderDetail.value.isRefunding = data.orderItemDtos.findIndex(el => el.refundSn && el.returnMoneySts && el.returnMoneySts !== 5) > -1
    orderDetail.value.orderType = Number(data.orderType)
    // 优惠明细
    orderDetail.value.freeTransfee = Math.abs(data.freeTransfee) // 运费减免
    orderDetail.value.virtualRemark = data.virtualRemark ? JSON.parse(data.virtualRemark) : [] // 留言
    isDataLoad.value = true
    // 虚拟商品-待使用的券总数
    handleUnusedVirtualCode(data.virtualInfoList)

    // 虚拟商品留言处理
    handlevirtualRemark()
  }).catch(err => {
    uni.showModal({
      content: err.msg,
      confirmText: $t('confirm'),
      confirmColor: '#eb2444',
      showCancel: false,
      success: res => {
        if (res.confirm) {
          uni.redirectTo({
            url: '/package-user/pages/order-list/order-list'
          })
        }
      }
    })
  })
}

// 虚拟商品-待使用的券总数
const handleUnusedVirtualCode = (list) => {
  if (list && list.length) {
    let unusedCountPar = 0
    list.forEach(el => {
      if (el.isWriteOff === 0) {
        unusedCountPar = unusedCountPar + 1
      }
    })
    unusedCount.value = unusedCountPar
  }
}

// 虚拟商品留言是否超长
const isExtraLong = ref(false)
const virtualRemarkList = ref([])
// 虚拟商品-留言列表处理
const handlevirtualRemark = () => {
  if (orderDetail.value.virtualRemark && orderDetail.value.virtualRemark.length) {
    // 过滤掉没有填写的留言
    orderDetail.value.virtualRemark = orderDetail.value.virtualRemark.filter(el => el.value)
    const virtualRemarkListPar = []
    let virtualRemarks
    // 如果留言总数大于3条，先展示前一条（完整留言在弹窗中展示）
    if (orderDetail.value.virtualRemark.length > 3) {
      const list = JSON.parse(JSON.stringify(orderDetail.value.virtualRemark))
      virtualRemarks = list.splice(0, 1)
      isExtraLong.value = true
    } else {
      virtualRemarks = orderDetail.value.virtualRemark
      isExtraLong.value = false
    }
    // 全部留言拼接
    virtualRemarks.forEach((el) => {
      virtualRemarkListPar.push([el.name, el.value])
    })
    if (isExtraLong.value) {
      virtualRemarkList.value = virtualRemarkListPar.push('......')
    } else {
      virtualRemarkList.value = virtualRemarkListPar
    }
  }
}

// 查看留言弹窗
const showViewMsgPopup = () => {
  if (isExtraLong.value) {
    showViewMsg.value = true
  }
}

const closeMsgPopup = () => {
  showViewMsg.value = false
}

/**
 * 一键复制事件
 */
const copyText = () => {
  uniCopy({
    content: orderNumber.value,
    success: (res) => {
      uni.showToast({
        title: res,
        icon: 'none'
      })
    },
    error: (e) => {
      uni.showToast({
        title: e,
        icon: 'none',
        duration: 3000
      })
    }
  })
}

/**
 * 图片加载失败时显示默认图片
 */
const handlePicError = (item) => {
  item.isPicError = true
}

// 格式化时间
const tsToDate = (data) => {
  if (!data) return data
  return util.tsToDate(data.replace(/-/g, '/'), 'Y-M-D', 1)
}

// 查看物流信息
const toDeliveryPage = () => {
  uni.navigateTo({
    url: '/package-user/pages/logistics-info/logistics-info?orderNumber=' + orderNumber.value
  })
}

let interval = null

// 小于0的格式化函数（不会出现负数）
const timeFormin = (num) => {
  return num < 0 ? 0 : num
}

// 关闭倒计时
const closeInterval = () => {
  interval && clearInterval(interval)
  interval = null
  endTime = null
}

const isShowTips = ref(false)
const onShowTips = () => {
  isShowTips.value = !isShowTips.value
}

const onCloseTips = () => {
  if (!isShowTips.value) return
  isShowTips.value = false
}

/**
 * 用户点击右上角分享 (勿删)
 */
onShareAppMessage(() => {})

</script>

<style lang="scss" scoped>
@use "order-detail";
</style>

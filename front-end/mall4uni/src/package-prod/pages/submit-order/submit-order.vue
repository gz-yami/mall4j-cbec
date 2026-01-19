<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <view
    class="Mall4j page-submit-order"
    @click.stop="onCloseTips"
  >
    <view
      class="container"
    >
      <view
        class="submit-order"
      >
        <!-- 邮寄到家 -->
        <view
          v-if="mold !== 1 || (filterShopItems && filterShopItems.length > 0)"
          class="address-box"
        >
          <view
            v-if="userAddr && !isEditAddr && isDistribution"
            class="current-address"
            @tap="addressListPop"
          >
            <image
              class="img"
              src="/static/images/icon/address.png"
            />
            <view class="user">
              <view class="c-address">
                {{ userAddr.province }}
                <span v-if="userAddr.city">
                  / {{ userAddr.city }}
                </span>
                <span v-if="userAddr.area">
                  / {{ userAddr.area }}
                </span>
                / {{ userAddr.addr }}
              </view>
              <view class="c-user">
                {{ userAddr.receiver }} {{ userAddr.mobile }}
              </view>
            </view>
            <text class="cmt-more" />
          </view>

          <view
            v-else
            class="current-address no-addr"
            @tap="addressPop"
          >
            <image
              class="img"
              src="/static/images/icon/address.png"
            />
            <view class="tips">
              {{ $t('fillInformation') }}
            </view>
            <text class="cmt-more" />
          </view>
        </view>
        <!-- /邮寄到家 -->
        <view
          v-for="(shopCart, index) in shopCartOrders"
          :key="index"
          class="shop-item"
        >
          <!-- 店铺信息 -->
          <view class="shop-box">
            <view class="shop-icon">
              <image src="/static/images/icon/icon_shop.png" />
            </view>
            <view class="shop-name">
              {{ shopCart.shopName }}
            </view>
          </view>
          <!-- /店铺信息 -->

          <!-- 店铺商品明细 -->
          <view class="prod-item">
            <view
              v-for="(shopCartItem, index2) in shopCart.shopCartItemDiscounts"
              :key="index2"
            >
              <view
                :class="[ 'prod-block', (shopCartItem.chooseDiscountItemDto && shopCartItem.chooseDiscountItemDto.reduceAmount) || shopCartItem.chooseComboItemDto ? 'discount' : '', ]"
              >
                <!-- 商品信息 -->
                <view class="item-box">
                  <view
                    v-for="(item, prodIndex) in shopCartItem.shopCartItems"
                    :key="prodIndex"
                    class="item-cont"
                  >
                    <view class="info-row">
                      <view class="prod-pic">
                        <image
                          v-if="item.pic && !item.isPicError"
                          mode="aspectFill"
                          :src="util.checkFileUrl(item.pic)"
                          @error="handlePicError(item)"
                        />
                        <image
                          v-else
                          src="/static/images/icon/def.png"
                          mode="aspectFill"
                        />
                      </view>
                      <view class="prod-info">
                        <view :class="['prodname']">
                          {{ item.prodName }}
                        </view>
                        <view class="prod-info-cont-sku">
                          <text class="quantity">
                            {{ $t('quantity') + ':' + item.prodCount }}
                          </text>
                          {{ (item.properties && item.properties.split(';').join('；')) || '' }}
                        </view>
                        <view
                          v-if="item.preSellTime&&item.preSellStatus===1||orderType"
                          class="prod-info-cont"
                        >
                          <view
                            v-if="orderType"
                            class="icon activity-icon"
                          >
                            {{ ['', $t('aGroup'), $t('spike'), $t('integral')][orderType] }}
                          </view>
                        </view>
                        <view class="price-box">
                          <view class="price-nums">
                            <view
                              v-if="orderType===3"
                              class="prodprice"
                            >
                              <text
                                v-if="item.scorePrice"
                                class="price-symbol"
                              >
                                {{ item.scorePrice }} {{ $t('integral') }} + {{ useCurrencyStore().defMark }}{{ item.price.toFixed(2) }}
                              </text>
                            </view>
                            <view
                              v-else
                              class="prodprice"
                            >
                              <text class="price-symbol">
                                {{ useCurrencyStore().defMark }}
                              </text>
                              {{ item.price.toFixed(2) }}
                            </view>
                          </view>
                        </view>
                      </view>
                    </view>
                    <!-- 虚拟商品留言 start -->
                    <view
                      v-if="item.virtualRemarkList && item.virtualRemarkList.length"
                      class="msg-item"
                    >
                      <view
                        v-for="(msgItem, msgIndex) in item.virtualRemarkList"
                        :key="msgIndex"
                        class="item"
                      >
                        <view class="tit">
                          {{ msgItem.name }}<span
                            v-if="msgItem.isRequired"
                            style="color: red"
                          >*</span>
                        </view>
                        <view class="free-box">
                          <input
                            v-model="msgItem.value"
                            class="input"
                            maxlength="100"
                            :placeholder="$t('pleaseEnter') + ' ' + msgItem.name"
                            placeholder-class="ph-class"
                            @blur="handleInputBlur(msgItem.value, msgIndex, prodIndex, index2, index)"
                          >
                        </view>
                      </view>
                    </view>
                    <!-- 虚拟商品留言 end -->
                  </view>
                </view>
              </view>
            </view>
          </view>
          <!-- /店铺商品明细 -->

          <!-- 店铺优惠券和买家留言 -->
          <view class="msg-item">
            <view
              v-if="shopCart.dvyType===4"
              class="item"
            >
              <view class="tit">
                {{ $t('startDeliveryFee') }}
              </view>
              <view class="free-box">
                <view class="price black">
                  <text class="symbol">
                    {{ useCurrencyStore().defMark }}
                  </text>
                  <text class="small-num">
                    {{ wxs.parsePrice(shopCart.startDeliveryFee)[0] }}
                  </text>
                  <text class="small-num">
                    .{{ wxs.parsePrice(shopCart.startDeliveryFee)[1] }}
                  </text>
                </view>
              </view>
            </view>
            <view
              v-if="mold !== 1 && isDistribution && orderType !== 3 && shopCart.dvyType !== 3"
              class="item item-distribution"
            >
              <view class="tit">
                {{ $t('distribution') }}
              </view>
              <view class="free-box">
                <icon
                  v-if="shopCart.dvyType===4"
                  class="free-box-icon"
                  type="warn"
                  size="15"
                  color="grey"
                  @tap.stop="onShowTips"
                />
                <text class="text">
                  {{
                    shopCart.dvyType === 1 ? $t('expressDelivery') : shopCart.dvyType === 4 ? $t('sameDelivery') : ''
                  }}
                </text>
              </view>
            </view>
            <!-- 配送方式 -->
            <view
              v-if="mold !== 1 && isDistribution && orderType!==3 && shopCart.dvyType !== 3"
              class="item"
            >
              <view class="tit">
                {{ $t('shippingCosts') }}
              </view>
              <view class="free-box">
                <view class="price black">
                  <text class="symbol">
                    {{ useCurrencyStore().defMark }}
                  </text>
                  <text class="small-num">
                    {{ wxs.parsePrice(new Big(shopCart.transFee || 0).plus(shopCart.freeTransFee || 0))[0] }}
                  </text>
                  <text class="small-num">
                    .{{ wxs.parsePrice(new Big(shopCart.transFee || 0).plus(shopCart.freeTransFee || 0))[1] }}
                  </text>
                </view>
              </view>
            </view>
            <view :class="['item',orderType === 3?'bottom0':'']">
              <view class="tit">
                {{ $t('OrderNotes') }}
              </view>
              <input
                class="input order-notes-input"
                maxlength="100"
                :placeholder="$t('storeNotesTips')"
                :value="shopCart.remarks"
                :data-index="index"
                @input="onRemarkIpt"
                @click="hideTabbar"
                @focus="hideTabbar"
                @blur="showTabbar"
              >
              <text class="cmt-more" />
            </view>
          </view>
          <!-- /店铺优惠券和买家留言 -->
        </view>

        <!-- 不满足当前配送方式的商品 -->
        <view
          v-if="filterShopItems && filterShopItems.length > 0"
          class="useless"
        >
          <view class="tit">
            {{ $t('followingProducts') }}
            （{{ $t('inTotal') }} {{ filterShopItems.length }} {{ $t('piece') }}）
            {{ dvyType === 2 ? $t('productNotSupportedStop') : $t('notSupportDistribution') }}
          </view>
          <scroll-view
            class="scroll-view_H"
            scroll-x
          >
            <view class="content">
              <view class="le">
                <view
                  v-for="(item,index) in filterShopItems"
                  :key="index"
                  class="le-img"
                >
                  <img-show
                    class="img"
                    :src="item.pic"
                  />
                  <view class="tips ellipsis">
                    {{ $t('cannotBuy') }}
                  </view>
                </view>
              </view>
            </view>
          </scroll-view>
          <!--（自提/同城相关 --- 勿动） -->
          <!--          <view-->
          <!--            v-if="mold !== 1 && !shopCartOrders.length && orderType!==3&&isSingleShop()"-->
          <!--            class="item"-->
          <!--            @tap="showDistributionPopup()"-->
          <!--          >-->
          <!--            <view class="item-tit">-->
          <!--              {{ $t('distribution') }}-->
          <!--            </view>-->
          <!--            <view class="free-box">-->
          <!--              <text class="text">-->
          <!--                {{ filterDvyType === 1 ? $t('expressDelivery') : $t('sameDelivery') }}-->
          <!--              </text>-->
          <!--              <text class="cmt-more" />-->
          <!--            </view>-->
          <!--          </view>-->
        </view>

        <!-- 总金额计算 -->
        <view class="order-msg">
          <view class="order-msg-tit">
            {{ $t('priceDetail') }}
          </view>
          <!-- 商品总额 -->
          <view class="item">
            <view class="item-tit">
              {{ $t('comTotal') }}
            </view>
            <view class="price black">
              <text class="symbol">
                {{ useCurrencyStore().defMark }}
              </text>
              <text class="big-num">
                {{ wxs.parsePrice(total)[0] }}
              </text>
              <text class="small-num">
                .{{ wxs.parsePrice(total)[1] }}
              </text>
            </view>
          </view>
          <!-- 商品总额 -->
          <view
            v-if="scorePrice"
            class="item"
          >
            <view class="item-tit">
              {{ $t('memberPoints') }}
            </view>
            <view class="price black">
              {{ scorePrice }}{{ $t('integral') }}
            </view>
          </view>
          <!-- 应付运费 -->
          <view
            v-if="mold !== 1 && dvyType !== 2"
            class="item"
          >
            <view class="item-tit">
              {{ $t('freightPayable') }}
            </view>
            <view class="price black">
              <text class="symbol">
                {{ useCurrencyStore().defMark }}
              </text>
              <text class="big-num">
                {{ wxs.parsePrice(transFee + shopFreeTransFee)[0] }}
              </text>
              <text class="small-num">
                .{{ wxs.parsePrice(transFee + shopFreeTransFee)[1] }}
              </text>
            </view>
          </view>
          <!-- 店铺运费减免(运费减免) -->
          <view
            v-if="mold !== 1 && shopFreeTransFee"
            class="item"
          >
            <view class="item-tit">
              {{ $t('shippingDiscount') }}
            </view>
            <view class="price">
              <text class="symbol">
                -{{ useCurrencyStore().defMark }}
              </text>
              <text class="big-num">
                {{ wxs.parsePrice(shopFreeTransFee)[0] }}
              </text>
              <text class="small-num">
                .{{ wxs.parsePrice(shopFreeTransFee)[1] }}
              </text>
            </view>
          </view>
          <!-- 平台开启会员包邮(运费减免) -->
          <view
            v-if="freeTransFee"
            class="item"
          >
            <view class="item-tit">
              {{ $t('memberPackage') }}
            </view>
            <view class="price">
              <text class="symbol">
                -{{ useCurrencyStore().defMark }}
              </text>
              <text class="big-num">
                {{ wxs.parsePrice(freeTransFee)[0] }}
              </text>
              <text class="small-num">
                .{{ wxs.parsePrice(freeTransFee)[1] }}
              </text>
            </view>
          </view>
          <!-- 会员等级折扣 -->
          <view
            v-if="totalLevelAmount"
            class="item"
          >
            <view class="item-tit">
              {{ $t('memberDiscountAmount') }}
            </view>
            <view class="price">
              <text class="symbol">
                -{{ useCurrencyStore().defMark }}
              </text>
              <text class="big-num">
                {{ wxs.parsePrice(totalLevelAmount)[0] }}
              </text>
              <text
                class="small-num"
              >
                .{{ wxs.parsePrice(totalLevelAmount)[1] }}
              </text>
            </view>
          </view>
        </view>
        <!-- /总金额计算 -->
      </view>
      <!-- 底部栏 -->
      <view
        v-if="tabbar"
        class="submit-order-footer"
      >
        <view class="sub-order">
          <view class="item-txt">
            <view class="txt-box">
              <text
                v-if="totalCount"
                class="num"
              >
                {{ $t('inTotal') }}&nbsp;{{ totalCount }}&nbsp;{{ $t('piece') }}
              </text>
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
              </lh-tooltip>
              ：
            </view>
            <view class="price">
              <show-price
                :type="1"
                :price="actualTotal"
                :is-by-side="false"
              />
            </view>
          </view>
          <view
            v-if="orderReduce>0"
            class="coupon"
          >
            {{ $t('totalDiscounts') }} {{ useCurrencyStore().defMark }}{{ wxs.parsePrice(orderReduce)[0] }}.{{ wxs.parsePrice(orderReduce)[1] }}
          </view>
        </view>
        <view
          class="footer-box"
          :style="filterShopItems && filterShopItems.length > 0 && shopCartOrders.length === 0 ? 'background: #909399;' : 'background: #F81A1A;' "
          @tap.stop="toPay"
        >
          {{ $t('submitOrders') }}
        </view>
      </view>
    </view>

    <!-- 新建收货地址弹窗 -->
    <view
      v-if="showAddressPop||isEditAddr"
      class="popup-hide"
    >
      <view class="popup-box add-addr-box">
        <view class="con-tit">
          <view class="tit-text">
            {{ $t('newShippingAddress') }}
          </view>
          <view
            class="close"
            @tap="closePopup"
          />
        </view>
        <view class="pop-con add-addr-con">
          <view class="list">
            <view class="tit">
              {{ $t('recipient') }}
            </view>
            <view class="con">
              <view :class="['input-box', isClickedSaveAddr && !receiver.trim() ? 'not-filled' : '']">
                <input
                  v-model="receiver"
                  class="input"
                  maxlength="15"
                  :placeholder="$t('consigneeTips')"
                  type="text"
                  @click="hideTabbar"
                  @focus="hideTabbar"
                  @blur="showTabbar"
                >
              </view>
              <view
                v-if="isClickedSaveAddr && !receiver.trim()"
                class="tips"
              >
                {{ $t('recipientNameTips') }}
              </view>
            </view>
          </view>
          <view class="list">
            <view class="tit">
              {{ $t('phoneNumber') }}
            </view>
            <view class="con">
              <view :class="['input-box', isClickedSaveAddr && !mobile.trim() ? 'not-filled' : '']">
                <input
                  v-model="mobile"
                  class="number"
                  :placeholder="$t('enterContactNumber')"
                  maxlength="11"
                  type="text"
                  @click="hideTabbar"
                  @focus="hideTabbar"
                  @blur="showTabbar"
                >
              </view>
              <view
                v-if="isClickedSaveAddr && !mobile.trim()"
                class="tips"
              >
                {{ $t('enterMobileNumber') }}
              </view>
            </view>
          </view>
          <view class="list">
            <view class="tit">
              {{ $t('area') }}
            </view>
            <view
              class="con"
              @tap="openSelectAddrPup"
            >
              <view
                :class="['input-box']"
                @click="hideTabbar"
                @focus="hideTabbar"
                @blur="showTabbar"
              >
                <view
                  v-if="province"
                  class="area-content"
                >
                  {{ province }}
                  <span v-if="city">
                    / {{ city }}
                  </span>
                  <span v-if="area">
                    / {{ area }}
                  </span>
                </view>
                <view
                  v-else
                  class="placeholder-text"
                >
                  {{ $t('selectProvinceCity') }}
                </view>
                <view class="icon-right">
                  <image src="../../static/images/icon/arrow-right.png" />
                </view>
              </view>
            </view>
          </view>
          <view class="list">
            <view class="tit">
              {{ $t('detailedAddress') }}
            </view>
            <view class="con">
              <view :class="['input-box', 'addres-input-box', isClickedSaveAddr && !addr.trim() ? 'not-filled' : '']">
                <textarea
                  v-model="addr"
                  class="textarea"
                  auto-height
                  :placeholder="$t('addressHint')"
                  @click="hideTabbar"
                  @focus="hideTabbar"
                  @blur="showTabbar"
                />
              </view>
              <view
                v-if="isClickedSaveAddr && !addr.trim()"
                class="tips"
              >
                {{ $t('selectDetailedAddress') }}
              </view>
            </view>
          </view>

          <view class="list">
            <view class="tit">
              {{ $t('postalCode') }}
            </view>
            <view class="con">
              <view :class="['input-box',isClickedSaveAddr && !postCode.trim() ?'not-filled':'']">
                <input
                  v-model="postCode"
                  class="number"
                  :placeholder="$t('enterCorrectPostalCode')"
                  maxlength="15"
                  type="text"
                  @click="hideTabbar"
                  @focus="hideTabbar"
                  @blur="showTabbar"
                >
              </view>
              <view
                v-if="isClickedSaveAddr && !postCode.trim()"
                class="tips"
              >
                {{ $t('enterCorrectPostalCode') }}
              </view>
            </view>
          </view>
        </view>
        <view
          class="confirm"
          @tap="onSaveAddr"
        >
          {{ $t('confirm') }}
        </view>
      </view>
    </view>

    <!-- 查看留言（虚拟商品） -->
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
            @tap="closePopup"
          />
        </view>
        <view class="msg-pop-con">
          <view class="pop-foot">
            <view
              class="foot-btn"
              @tap="closePopup"
            >
              {{ $t('gotIt') }}
            </view>
          </view>
        </view>
      </view>
    </view>

    <AddressSelect
      v-model="isShowSelectAddrPopup"
      :select-ids="[provinceId,cityId,areaId]"
      @select-addr="selectAddr"
    />
    <confirm-pop
      v-model:show-pop="showPop"
      :pop-content="popContent"
      :show-title="true"
      :show-cancel="false"
      :is-cancel="false"
      @confirm="confirm"
    />
  </view>
</template>

<script setup>
import AddressSelect from '../../components/address-select/index.vue'
import Big from 'big.js'
import util from '@/utils/util'
import { useCurrencyStore } from '@/stores/modules/currency'

const wxs = number()
const dvyType = ref(1) // 配送类型 1:快递 2:自提 3：无需快递 4:同城配送
const dvyTypes = ref([]) // 每个店铺的配送类型 1:快递 2:自提 3：无需快递 4:同城配送
const userAddr = ref(null)
const addrId = ref(0)
const couponIds = ref([])
const orderEntry = ref('0') // 订单入口 0购物车 1立即购买
const orderType = ref(0) // 订单类型(0普通商品 1拼团 2秒杀 3积分)
// 1虚拟商品
const mold = ref(0)
const pickUpPointSelected = ref(false) // 是否已选自提点
const orderPath = ref('')
const rawDvyType = ref(1) // 配送类型（页面原始传参）
const shopId = ref('')
const selStationItem = ref({}) // 选中的自提点数据
const stationUserName = ref('') // 提货人
const stationUserMobile = ref('') // 提货人手机号
const isDistribution = ref(true) // 配送方式tab状态
const provArray = ref([])
const addressList = ref([]) // 地址列表
const isToPay = ref(false)
const module = ref('')

/**
 * 生命周期函数--监听页面加载
 */
onLoad((options) => {
  // 获取省市区列表数据
  uni.removeStorageSync('cbecB2cInvoiceDataFrom')
  dvyType.value = Number(options.dvyType) || 1
  if (uni.getStorageSync('orderData')) {
    const orderData = uni.getStorageSync('orderData')
    dvyType.value = orderData.dvyType
    dvyTypes.value = orderData.dvyTypes
    userAddr.value = orderData.userAddr
    addrId.value = orderData.userAddr.addrId

    couponIds.value = orderData.couponIds
    uni.removeStorageSync('orderData')
  }
  dvyType.value = 1
  if (dvyTypes.value?.length) {
    dvyTypes.value = dvyTypes.value.map(item => ({
      ...item,
      dvyType: item.dvyType === 2 ? 1 : item.dvyType
    }))
  }
  orderEntry.value = options.orderEntry || uni.getStorageSync('cbecB2cOrderEntry') || 0
  module.value = options.module || ''
  orderType.value = Number(options.orderType)
  mold.value = Number(options.mold)
  pickUpPointSelected.value = options.pickUpPointSelected || false
  orderPath.value = options.orderPath
  rawDvyType.value = options.dvyType
  shopId.value = options.shopId || ''
  isDistribution.value = true
  provArray.value = provArray.value.unshift({})
  loadAddressList().then(res => {
    addressList.value = res
  }) // 加载地址列表
  setTimeout(() => {
    isToPay.value = true
  }, 100)

  if (dvyType.value !== 2) {
    let addrIdPar
    // 从购物车过来以购物车所选地址为主
    if (+orderEntry.value === 0) {
      addrIdPar = uni.getStorageSync('cbecB2cCurCartSelAddr')?.addrId
      uni.removeStorageSync('cbecB2cCurCartSelAddr')
    } else {
      addrIdPar = uni.getStorageSync('cbecB2cCartSelectAddrInfo')?.addrId || 0
    }
    addrId.value = addrIdPar
    loadOrderData()
  }
})

const animation = ref('')

/**
 * 生命周期函数--监听页面初次渲染完成
 */
onReady(() => {
  animation.value = uni.createAnimation({
    transformOrigin: '50% 50%',
    duration: 0,
    timingFunction: 'ease',
    delay: 0
  })
  animation.value.translateY(200 + 'vh').step()

  animation.value = animation.value.export()
})

/**
 * 生命周期函数--监听页面显示
 */
onShow(() => {
  uni.removeStorageSync('orderData')

  // 切换收货地址
  const addrInfo = uni.getStorageSync('stationSaddress')
  if (addrInfo) {
    addrId.value = addrInfo.addrId
    userAddr.value = addrInfo
    // 切换收货地址后初始化配送商品类型(订单中可能存在无法配送的普通商品)
    mold.value = 0
    loadOrderData()
    uni.removeStorageSync('stationSaddress')
  } else {
    let addrIdPar = 0
    // 从购物车过来以购物车所选地址为主
    if (+orderEntry.value === 0) {
      addrIdPar = uni.getStorageSync('cbecB2cCurCartSelAddr')?.addrId
      uni.removeStorageSync('cbecB2cCurCartSelAddr')
    } else {
      addrIdPar = uni.getStorageSync('cbecB2cCartSelectAddrInfo')?.addrId || 0
    }
    addrId.value = addrIdPar
  }

  // 设置头部导航标题
  uni.setNavigationBarTitle({
    title: $t('submitOrders')
  })
  uni.removeStorageSync('cbecB2cMsgList')
})

/**
 * 生命周期函数--监听页面卸载
 */
onUnload(() => {
  if (mold.value === 1) {
    // 清除虚拟商品留言缓存
    uni.removeStorageSync('cbecB2cVirtualRemark')
    uni.removeStorageSync('cbecB2cMsgList')
  }
  uni.removeStorageSync('cbecB2cSelStationItem')
  uni.removeStorageSync('cbecB2cOrderEntry')
  uni.removeStorageSync('cbecB2cDvyType')
  uni.removeStorageSync('cbecB2cAddrInfo')
  uni.removeStorageSync('cbecB2cSelectedPickupinfors')
})

const showPop = ref(false)
const confirm = () => {
  showPop.value = false
  uni.navigateBack()
}

const isShowSelectAddrPopup = ref(false)
// 选择地址前的处理
const openSelectAddrPup = () => {
  isShowSelectAddrPopup.value = true
}

const province = ref('')
const city = ref('')
const area = ref('')
const provinceId = ref(0)
const cityId = ref(0)
const areaId = ref(0)
// 选择地址确认
const selectAddr = (selInfo) => {
  province.value = selInfo.province
  city.value = selInfo.city
  area.value = selInfo.area
  provinceId.value = selInfo.provinceId
  cityId.value = selInfo.cityId
  areaId.value = selInfo.areaId
}

const mobile = ref('')
const postCode = ref('')
const receiver = ref('')
const addr = ref('')
const lat = ref('')
const lng = ref('')

const tabbar = ref(true)

const disabled = ref(false) // 是否禁止输入框输入
// 提交订单浮框的显隐
const showTabbar = () => {
  if (disabled.value == false) {
    tabbar.value = true
  }
}

const hideTabbar = () => {
  if (disabled.value == false) {
    tabbar.value = true
  }
}

const shopCartOrders = ref([])
const helensPopup = ref(true)
const isEditAddr = ref(false) // 编辑地址
const showRaisingUserList = ref(false) // 选择历史提货人弹窗
const filterShopItems = ref([]) // 过滤掉的商品项
const showDistributionPop = ref(false) // 选择物流方式弹窗
const distributionPopType = ref(1) // 1:普通店铺选择物流 2:不支持配送商品选择物流
const filterDvyType = ref(1) // 不支持配送商品的配送方式

// 加载地址列表
const loadAddressList = () => {
  return new Promise((resolve) => {
    http.request({
      url: '/p/address/list',
      method: 'GET',
      data: {}
    }).then(({ data }) => {
      resolve(data)
    })
  })
}

/**
 * 加载订单数据
 */
const loadOrderData = () => {
  const orderParam = uni.getStorageSync('cbecB2cOrderItem') || {}
  const shopIds = uni.getStorageSync('cbecB2cShopIds') || [] // 购物车选择的shopId
  if (shopCartOrders.value && shopCartOrders.value.length > 0) { // 非首次获取数据
    dvyTypes.value = []
    if (dvyType.value === 2) { // 自提
      shopCartOrders.value.forEach(el => {
        dvyTypes.value.push({ shopId: el.shopId, dvyType: 2 })
      })
    } else { // 非自提
      shopCartOrders.value.forEach(el => {
        dvyTypes.value.push({ shopId: el.shopId, dvyType: mold.value === 1 ? 3 : (el.dvyType === 3 ? 1 : el.dvyType) }) // 虚拟商品为无需快递
      })
    }
  } else { // 首次获取数据
    if (orderEntry.value === '1') { // 立即购买进入
      dvyTypes.value = [{ shopId: orderParam.shopId, dvyType: mold.value === 1 ? 3 : dvyType.value - 0 || 1 }] // 虚拟商品为无需快递
      shopId.value = orderParam.shopId
    } else { // 购物车进入
      dvyTypes.value = []
      shopIds?.forEach(el => {
        dvyTypes.value.push({ shopId: el, dvyType: dvyType.value - 0 || 1 })
      })
      if (shopIds?.length === 0) {
        shopId.value = shopIds[0]
      }
    }
  }

  if (filterShopItems.value && filterShopItems.value.length > 0) {
    filterShopItems.value.forEach(el => {
      if (!(dvyTypes.value.some(item => item.shopId === el.shopId))) {
        dvyTypes.value.push({ shopId: el.shopId, dvyType: 1 })
      }
    })
  }

  dvyTypes.value = dvyTypes.value.map(item => {
    const obj = item
    obj.lat = item.dvyType === 2 ? lat.value : null
    obj.lng = item.dvyType === 2 ? lng.value : null
    return obj
  })
  const url = orderType.value === 3 ? '/p/score/confirm' : orderType.value === 2 ? `/p/seckill/${orderPath.value}/confirm` : orderType.value === 1 ? '/p/group/order/confirm' : '/p/order/confirm'
  http.request({
    url,
    method: 'POST',
    data: {
      addrId: addrId.value || 0,
      couponIds: couponIds.value,
      orderItem: orderEntry.value === '1' ? uni.getStorageSync('cbecB2cOrderItem') : undefined,
      dvyTypes: dvyTypes.value, // 配送类型 1:快递 2:自提 3：无需快递 4:同城配送
      groupSkuId: orderParam.groupSkuId,
      groupTeamId: orderParam.groupTeamId,
      seckillSkuId: orderParam.seckillSkuId,
      prodCount: orderParam.prodCount
    }
  }).then((res) => {
    if (['00000', 'A00002'].includes(res.code)) {
      const data = res.data
      loadOrderRes(data)
    }
  }).catch(errMsg => {
    if (errMsg.code === 'A03001' || errMsg.code === 'A03002') {
      uni.showModal({
        title: $t('tips'),
        content: errMsg.code === 'A03001' ? $t('deliveryNotSupported') : $t('duplicateErrorTips'),
        showCancel: false,
        success: (res) => {
          if (res.confirm) {
            uni.navigateBack()
          }
        }
      })
    }
  })
}

const totalLevelAmount = ref(0) // 等级抵扣金额
const selectDistributionWay = ref(false) // 选择订单发货方式
const actualTotal = ref(0)
const total = ref(0)
const totalCount = ref(0)
const transFee = ref(0)
const orderReduce = ref(0)
const freeTransFee = ref(0) // 用户等级免运费金额
const isFirst = ref(true)
const scorePrice = ref(0)
const startDeliveryFee = ref(0)
const shopFreeTransFee = ref(0) // 店铺免运费金额

// 订单请求成功数据处理
const loadOrderRes = (data) => {
  distributionPopType.value = 1
  if (!data.shopCartOrders.length && data.dvyTypes.length === 1) {
    filterDvyType.value = data.dvyTypes[0].dvyType
  }

  if (!data.shopCartOrders) {
    data.shopCartOrders = []
  }

  // 获取上次输入的备注及留言
  echoRemarks(data)

  switch (data.orderType) {
    case 'ORDINARY':
      orderType.value = 0
      break
    default:
      break
  }

  shopCartOrders.value = []
  const shopCartOrdersPar = data.shopCartOrders // 购物车店铺商品信息列表

  // 店铺添加配送方式字段
  shopCartOrdersPar.forEach((el) => {
    el.dvyType = data.dvyTypes.filter(item => item.shopId === el.shopId)[0].dvyType
  })

  const couponIdsPar = []
  let selectDistributionWayPar = false

  // 判断不支持配送商品是否支持自提
  if (shopCartOrdersPar.length < 2 && data.filterShopItems?.length > 0) {
    // eslint-disable-next-line no-mixed-operators
    let shopId = shopCartOrdersPar.length && shopCartOrdersPar[0].shopId || null

    selectDistributionWayPar = data.filterShopItems?.some(prod => {
      if (shopId === null) {
        shopId = prod.shopId
      }
      return prod.deliveryModeVO.hasUserPickUp
    })
  }

  // 运费
  const transFeePar = Math.abs(data.totalTransFee)
  selectDistributionWay.value = selectDistributionWayPar
  shopCartOrdersPar.forEach((order) => {
    if (order.dvyType === 2) {
      order.dvyType = 1
    }
  })
  if (data.dvyTypes?.length) {
    data.dvyTypes = data.dvyTypes.map(item => ({
      ...item,
      dvyType: item.dvyType === 2 ? 1 : item.dvyType
    }))
  }
  if (dvyType.value === 2) {
    dvyType.value = 1
  }
  shopCartOrders.value = shopCartOrdersPar // 所有的店铺的购物车信息
  transFee.value = transFeePar // 总运费
  couponIds.value = couponIdsPar

  orderDataValuation(data)

  getOrderSkuList()

  // 虚拟商品留言
  handleAllVirtualMsg()

  disabledInput() // 是否禁止提货人输入框输入
}

/**
 * 获取订单规格列表
 */
const getOrderSkuList = () => {
  const stationProdList = []
  if (shopCartOrders.value?.length > 0) {
    shopCartOrders.value.forEach(order => {
      if (order.shopCartItemDiscounts?.length > 0) {
        order.shopCartItemDiscounts.forEach(discount => {
          if (discount.shopCartItems?.length > 0) {
            discount.shopCartItems.forEach(item => {
              if (item.mold === 2) {
                if (item.comboShopCartItems?.length > 0) {
                  item.comboShopCartItems.forEach(combo => {
                    const obj = {
                      skuId: combo.skuId,
                      count: combo.prodCount
                    }
                    stationProdList.push(obj)
                  })
                }
              } else if (item.mold === 0) {
                const obj = {
                  skuId: item.skuId,
                  count: item.prodCount
                }
                stationProdList.push(obj)
              }
            })
          }
        })
      }
    })
    // 将订单中所选的商品规格存入缓存
    uni.setStorageSync('cbecB2cOrderSkuList', stationProdList)
  }
}

const echoRemarks = (data) => {
  // 获取上次输入的备注及留言
  if (shopCartOrders.value && shopCartOrders.value.length > 0) {
    shopCartOrders.value.forEach(el => {
      data.shopCartOrders.forEach(item => {
        if (el.shopId === item.shopId) {
          item.remarks = el.remarks

          el.shopCartItemDiscounts.forEach(el1 => {
            el1.shopCartItems.forEach(el2 => {
              item.shopCartItemDiscounts.forEach(item1 => {
                item1.shopCartItems.forEach(item2 => {
                  if (el2.prodId === item2.prodId && el2.basketId === item2.basketId) {
                    item2.virtualRemarkList = el2.virtualRemarkList
                  }
                })
              })
            })
          })
        }
      })
    })
  }
}

const orderDataValuation = (data) => {
  dvyType.value = data.dvyTypes[0].dvyType
  actualTotal.value = data.actualTotal // 实际总值
  total.value = data.total // 商品总值
  totalCount.value = data.totalCount // 商品总数
  userAddr.value = data.userAddr // 地址Dto
  orderReduce.value = data.orderReduce // 订单优惠金额(所有店铺优惠金额和使用积分抵现相加)
  totalLevelAmount.value = data.totalLevelAmount // 等级抵扣金额
  freeTransFee.value = data.freeTransFee // 用户等级免运费金额
  filterShopItems.value = data.filterShopItems // 过滤掉的商品项
  isFirst.value = false
  // 起送费 (同城配送)
  startDeliveryFee.value = data.startDeliveryFee
  shopFreeTransFee.value = data.shopFreeTransFee
}

const allVirtualMsg = ref([])
/**
 * 虚拟商品留言
 */
const handleAllVirtualMsg = () => {
  allVirtualMsg.value = getValueOfArr(shopCartOrders.value)
  if (uni.getStorageSync('cbecB2cMsgList')) {
    const list = JSON.parse(uni.getStorageSync('cbecB2cMsgList'))
    allVirtualMsg.value.forEach((msgItem, index) => {
      if (list[index].id === msgItem.id && !msgItem.value) {
        msgItem.value = list[index].value
      }
    })
  }
}

const getValueOfArr = (data) => {
  const layers = ['shopCartItemDiscounts', 'shopCartItems', 'virtualRemarkList']
  const deepGet = (item, index) => {
    return item.map(la => {
      const children = la[layers[index]]
      if (Array.isArray(children)) {
        if (layers.length === index + 1) {
          return children.map((ch, idx) => {
            ch.skuId = la.skuId
            ch.id = la.prodId + '_' + idx + '_' + la.basketId
            ch.basketId = la.basketId
            return ch
          })
        }
        return deepGet(children, index + 1)
      }
      return []
    }).flat(Infinity)
  }
  return deepGet(data, 0)
}

/**
 * 虚拟商品留言输入框失焦
 */
const handleInputBlur = (value, msgIndex, prodIndex, discIndex, shopIndex) => {
  if (!value) {
    return
  }
  const currentItem = shopCartOrders.value[shopIndex].shopCartItemDiscounts[discIndex].shopCartItems[prodIndex].virtualRemarkList[msgIndex]
  allVirtualMsg.value.forEach(msgItem => {
    if (currentItem.id === msgItem.id) {
      msgItem.value = value
    }
  })
  uni.setStorageSync('cbecB2cMsgList', JSON.stringify(allVirtualMsg.value))
}

/**
 * 提交订单校验
 */
const toPay = () => {
  if (!isToPay.value) return
  if (dvyType.value !== 2) {
    if (!userAddr.value && mold.value !== 1) {
      uni.showToast({
        title: $t('pleaseSelectSddress'),
        icon: 'none'
      })
      return
    }
    if (!shopCartOrders.value.length) {
      const title = dvyType.value === 1 ? $t('expressDelivery') : dvyType.value === 4 ? $t('sameDelivery') : ''
      uni.showToast({
        title: $t('productNotSupported') + title,
        icon: 'none'
      })
      return
    }
    if (shopCartOrders.value.some(el => el.dvyType === 4) && (!userAddr.value.lat || !userAddr.value.lng)) {
      uni.showModal({
        title: $t('tips'),
        content: $t('cityAddressPrompt'),
        showCancel: false,
        confirmText: $t('confirm'),
        success: (res) => {
          if (res.confirm) {
            uni.setStorageSync('orderData', {
              shopCartOrders: shopCartOrders.value,
              dvyType: dvyType.value,
              dvyTypes: dvyTypes.value,
              userAddr: userAddr.value
            })
            uni.setStorageSync('cbecB2cAddrInfo', userAddr.value)
            uni.navigateTo({
              url: '/package-prod/pages/edit-address/edit-address?selectLocation=1&addrId=' + userAddr.value.addrId,
              success (res) {
                uni.setStorageSync('stationSaddress', userAddr.value)
                res.eventChannel.emit('goLocate')
              }
            })
          }
        }
      })
      return
    }
  } else if (dvyType.value === 2) {
    const reg = /^\s+$/g
    if (!shopCartOrders.value.length) {
      uni.showToast({
        title: $t('productNotSupportedStop'),
        icon: 'none'
      })
      return
    }
    if (!(selStationItem.value && selStationItem.value.stationId)) {
      uni.showToast({
        title: $t('selectPickPoint'),
        icon: 'none'
      })
      return
    }
    if (reg.test(stationUserName.value) || reg.test(stationUserMobile.value)) {
      uni.showToast({
        title: $t('inputAllSpace'),
        icon: 'none'
      })
      return
    }
    if (!stationUserName.value || !stationUserMobile.value) {
      uni.showToast({
        title: $t('fillDeliveryPersonInformation'),
        icon: 'none'
      })
      return
    }
  }
  // 点击地址判断
  if (!userAddr.value && dvyType.value !== 2 && mold.value !== 1) {
    uni.showToast({
      title: $t('saveAndUseTips'),
      icon: 'none'
    })
    return
  }
  // 虚拟商品留言校验
  if (allVirtualMsg.value.length &&
    allVirtualMsg.value.find(el => el.value && !el.value.trim())) {
    uni.showToast({
      title: $t('msgCannotBeAllSpaces'),
      icon: 'none'
    })
    return
  }
  if (allVirtualMsg.value.length &&
    allVirtualMsg.value.find(el => el.isRequired && !el.value)) {
    uni.showToast({
      title: $t('requiredMessage'),
      icon: 'none'
    })
    return
  }

  submitOrder()
}

const dateContent = ref('') // 选中的日期
const timeContent = ref('') // 选中的时间
const popContent = ref({})
// 提交订单
const submitOrder = () => {
  const shopCartOrdersPar = shopCartOrders.value
  const reg = /^\s+$/g
  const orderShopParam = []
  shopCartOrdersPar.forEach((shopCart) => {
    orderShopParam.push({
      remarks: shopCart.remarks && shopCart.remarks.trim() ? shopCart.remarks : '',
      shopId: shopCart.shopId
    })
  })

  const remarksFlag = orderShopParam.some((item) => {
    return reg.test(item.remarks)
  })

  if (remarksFlag) {
    uni.showToast({
      title: $t('inputAllSpace'),
      icon: 'none'
    })
    return
  }
  const orderSelfStationDto = {
    stationId: selStationItem.value && selStationItem.value.stationId,
    stationTime: dateContent.value + ' ' + timeContent.value,
    stationUserMobile: stationUserMobile.value,
    stationUserName: stationUserName.value
  }
  let orderInvoiceList = [] // invoiceDataFrom
  shopCartOrdersPar.forEach((item) => {
    if (item.invoiceDataFrom && item.invoiceDataFrom.invoiceType === 1 && item.actualTotal) {
      orderInvoiceList.push(item.invoiceDataFrom)
    }
  })
  if (orderInvoiceList.length === 0) {
    orderInvoiceList = null
  }

  allVirtualMsg.value.forEach(el => {
    el.id = undefined
  })
  const obj = {
    orderSelfStationDto,
    orderInvoiceList,
    virtualRemarkList: allVirtualMsg.value,
    groupTeamId: uni.getStorageSync('cbecB2cOrderItem').groupTeamId
  }

  obj.orderShopParams = orderShopParam
  // }
  uni.removeStorageSync('cbecB2cMsgList')
  http.request({
    url: orderType.value === 1 ? '/p/group/order/submit' : orderType.value === 2 ? `/p/seckill/${orderPath.value}/submit` : '/p/order/submit',
    method: 'POST',
    selfLoading: orderType.value === 2,
    hasCatch: true,
    data: obj
  }).then(({ data }) => {
    if (data.duplicateError != null && data.duplicateError == 1) {
      uni.showModal({
        title: $t('tips'),
        content: $t('duplicateErrorTips'),
        confirmText: $t('confirm'),
        showCancel: false,
        success: (res) => {
          if (res.confirm) {
            uni.navigateBack()
          }
        }
      })
    } else {
      // 请求支付
      http.request({
        url: '/p/order/pay',
        data: {
          orderNumbers: data.orderNumbers
        }
      }).then(() => {
        // 直接跳转支付成功
        uni.redirectTo({
          url: `/pages/pay-result/pay-result?orderNumbers=${data.orderNumbers}`
        })
      })
    }
  }).catch(errMsg => {
    if (errMsg.code === 'A00001' || errMsg.code === 'A03002' || errMsg.code === 'A07001') {
      if (errMsg.code === 'A07001') {
        shopCartOrders.value.forEach((shopCart) => {
          shopCart.shopCartItemDiscounts.forEach((shopCartItemDiscount) => {
            shopCartItemDiscount.shopCartItems.forEach((prodItem) => {
              if (!prodItem.isDelivery) {
                errMsg.msg += prodItem.prodName + '、'
              }
            })
          })
        })
        errMsg.msg = errMsg.msg.substring(0, errMsg.msg.length - 1)
      }

      popContent.value = {
        content: errMsg.msg,
        confirmText: $t('confirm')
      }
      showPop.value = true
    }
  })
}

// 查看留言弹窗显隐
const showViewMsg = ref(false)

const showRaisingTimePop = ref(false) // 选择提货时间弹窗
const showAddressPop = ref(false) // 新建地址弹窗
const closePopup = () => {
  showDistributionPop.value = false
  showRaisingUserList.value = false
  showRaisingTimePop.value = false
  showAddressPop.value = false
  isEditAddr.value = false
  showViewMsg.value = false
  helensPopup.value = true
}
/**
 * 输入备注
 */
const onRemarkIpt = (e) => {
  const index = e.currentTarget.dataset.index
  shopCartOrders.value[index].remarks = e.detail.value
}

/**
 * 可用地址弹窗显示
 */
const addressListPop = () => {
  uni.navigateTo({
    url: '/package-user/pages/delivery-address/delivery-address?selectAddress=1'
  })
}
const addressPop = () => {
  showAddressPop.value = true
  isEditAddr.value = true
}

/**
 * 禁止输入提货人信息
 */
const disabledInput = () => {
  disabled.value = dvyType.value === 2 && !shopCartOrders.value.length
}

// 收货地址提示
const isClickedSaveAddr = ref(false)
/**
 * 保存地址
 */
const onSaveAddr = () => {
  isClickedSaveAddr.value = true
  if (addressList.value.length === 10) {
    uni.showToast({
      title: $t('newAddressesLimit'),
      icon: 'none',
      duration: 1500
    })
    return
  }

  if (!receiver.value.trim() || !mobile.value.trim() || !province.value || !addr.value || !postCode.value.trim()) {
    return
  }
  if (addr.value.length < 5) {
    uni.showToast({
      title: $t('selectDetailedAddress'),
      icon: 'none'
    })
    return
  }

  if (dvyType.value === 4) {
    // 同城配送
    if (!lat.value || !lng.value || lat.value == '' || lng.value == '') {
      uni.showModal({
        title: $t('tips'),
        content: $t('selectCoordinates'),
        showCancel: false,
        success: () => {
          uni.navigateTo({
            url:
              '/package-prod/pages/edit-address/edit-address?addrId=' +
              userAddr.value.addrId
          })
        }
      })
    }
  }
  const url = '/p/address/addAddr'
  const method = 'POST'
  // 添加或修改地址
  http.request({
    url,
    method,
    data: {
      receiver: receiver.value,
      mobile: mobile.value,
      addr: addr.value,
      postCode: postCode.value,
      province: province.value,
      provinceId: provinceId.value,
      city: city.value,
      cityId: cityId.value,
      areaId: areaId.value,
      area: area.value,
      userType: 0,
      lat: lat.value, // 纬度
      lng: lng.value // 经度
    }
  }).then(() => {
    setTimeout(() => {
      closePopup()
      loadOrderData()
    }, 200)
  })
}

/**
 * 图片加载失败时显示默认图片
 */
const handlePicError = (prod) => {
  prod.isPicError = true
}

const isShowTips = ref(false)
const onShowTips = () => {
  isShowTips.value = !isShowTips.value
}

const onCloseTips = () => {
  if (!isShowTips.value) return
  isShowTips.value = false
}

</script>
<style scoped lang="scss">
@use "submit-order";
</style>

<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <view
    id="basket"
    class="Mall4j page-basket basket-box"
  >
    <view class="top-manage-box">
      <view class="left-box">
        <view class="icon">
          <image src="/static/images/icon/icon_addr.png" />
        </view>
        <template v-if="addrLoaded">
          <view
            v-if="addrInfo"
            class="text"
            @tap="onSelectAddr"
          >
            {{ addrInfo.province }}
            <span v-if="addrInfo.city">
              / {{ addrInfo.city }}
            </span>
            <span v-if="addrInfo.area">
              / {{ addrInfo.area }}
            </span>
          </view>
          <view
            v-else
            class="text"
            @tap="onAddAddr"
          >
            {{ $t('addShopAddress') }}
          </view>
        </template>
      </view>
      <view
        v-if="isExistProds"
        class="right-box"
        @tap="onHandleManage"
      >
        {{ isEditCarts ? $t('accomplish') : $t('manage') }}
      </view>
    </view>
    <view
      v-if="isExistProds"
      id="prod-list"
      :style="prodListStyle"
    >
      <view class="prod-list">
        <view
          v-for="(shopCart, shopIndex) in shopCarts"
          :key="shopIndex"
          class="shop-item"
        >
          <view
            v-for="(shopCartItem, shopCartItemIndex) in shopCart.shopCartItemDiscounts"
            :key="shopCartItemIndex"
            class="shop-item-detail"
          >
            <view class="prod-block">
              <view
                v-if="shopCartItem.shopCartItems.length"
                class="goods-box"
              >
                <view class="act-box">
                  <block
                    v-for="(prod, prodIndex) in shopCartItem.shopCartItems"
                    :key="prod.prodId"
                  >
                    <view :class="['goods-item',prodIndex!==0?'pd-top':'']">
                      <view class="left-box">
                        <view :class="['btn', prod.comboId && prod.parentBasketId !==0 ? 'hide' : '']">
                          <label>
                            <checkbox
                              :value="prod.prodId+''"
                              :checked="prod.isChecked"
                              color="#fff"
                              class="checkbox"
                              :disabled="(shopCartItem.unDelivery) && !isEditCarts"
                              @tap.stop="onHandleCheckProdItem(shopIndex, shopCartItemIndex, prodIndex)"
                            />
                          </label>
                        </view>
                        <view
                          class="pic"
                          @tap="onToProdPage(prod.prodId)"
                        >
                          <img-show :src="prod.pic" />
                          <text
                            v-if="shopCartItem.unDelivery"
                            class="tips"
                          >
                            {{ $t('undelivered') }}
                          </text>
                        </view>
                      </view>
                      <view class="right-box">
                        <view :class="['txt-ell-1',!shopCartItem.unDelivery ?'goods-name':'gray-goods-name']">
                          <text @tap.stop="onToProdPage(prod.prodId)">
                            {{ prod.prodName }}
                          </text>
                        </view>
                        <view class="sku-act-box">
                          <!-- 所选的sku -->
                          <view
                            v-if="prod.skuName && prod.skuName.trim()"
                            class="sku-act-item sku-itm"
                            @tap.stop="onGetSkuListByProdId(prod, shopCartItem)"
                          >
                            {{ prod.skuName || '' }}
                          </view>
                        </view>
                        <view class="price-num-info">
                          <view class="price-info">
                            <view class="price">
                              <text class="small">
                                {{ useCurrencyStore().defMark }}
                              </text>
                              <text class="big">
                                {{ parsePrice(prod.comboId ? prod.comboPrice : prod.price)[0] }}
                              </text>
                              <text class="small">
                                .{{ parsePrice(prod.comboId ? prod.comboPrice : prod.price)[1] }}
                              </text>
                            </view>
                          </view>
                          <!-- 普通商品 -->
                          <view
                            v-if="!prod.comboId"
                            class="m-numSelector"
                          >
                            <view
                              :class="['minus',{'m-p-btn':prod.prodCount===1}]"
                              @tap.stop="onUpdateBasket(shopCart.shopId, prod, -1)"
                            />
                            <input
                              :key="prod.prodCountKey"
                              type="number"
                              :value="prod.prodCount"
                              @blur="onUpdateProdNumber($event,shopCart.shopId, prod)"
                            >
                            <view
                              class="plus"
                              @tap.stop="onUpdateBasket(shopCart.shopId, prod, 1)"
                            />
                          </view>
                        </view>
                        <!-- 预估到手价 -->
                        <view
                          v-if="prod.isChecked && estimatedPriceFormat(prod.actualTotal,prod.prodCount)!=prod.price"
                          class="evaluate price"
                        >
                          <text class="evaluate-label">
                            {{ $t('estimatedPri') }}
                          </text>
                          <text class="evaluate-val">
                            {{ useCurrencyStore().defMark }}{{ estimatedPriceFormat(prod.actualTotal,prod.prodCount) }}
                          </text>
                        </view>
                      </view>
                    </view>
                  </block>
                  <view
                    v-if="shopCartItemIndex!==shopCart.shopCartItemDiscounts.length-1"
                    class="act-box-line"
                  />
                </view>
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部按钮 -->
    <!-- 英文时候购物车底部字体出现遮蔽 -->
    <view
      v-if="isExistProds"
      class="cart-footer"
    >
      <view class="btn all">
        <label @tap="onHandleCheckAll">
          <checkbox
            :checked="allChecked"
            color="#fff"
            class="checkbox"
            @tap.stop="onHandleCheckAll"
          />
          <text>{{ $t('selectAll') }}</text>
        </label>
      </view>
      <block v-if="!isEditCarts">
        <view class="btn total">
          <view
            class="total-con"
            @tap.stop="onShowPriDet"
          >
            <view class="finally">
              <view class="blod">
                {{ $t('total') }}
                <lh-tooltip
                  v-if="useCurrencyStore().currency !== useCurrencyStore().defCurrency"
                  width="350"
                  :content="$t('ratePriceTip')"
                >
                  <icon
                    type="warn"
                    color="#999999"
                    size="13"
                    class="warn-tip"
                  />
                </lh-tooltip>:
              </view>
              <view class="price">
                <show-price
                  :price="finalMoney"
                  :is-align-center="false"
                  :is-by-side="false"
                  big-font-size="32rpx"
                  small-font-size="24rpx"
                />
              </view>
            </view>
            <!-- 优惠总额 -->
            <view
              v-if="totalReduceMoney>0"
              class="total-msg"
            >
              <view class="total-msg-price">
                {{ $t('totalDiscounts') }}:{{ useCurrencyStore().defMark }}{{ toFixxed(totalReduceMoney) }}
              </view>
              <view>，{{ $t('discountDetails') }}</view>
              <view :class="['arrow-sty',hidePriModal?'bottom':'top']" />
            </view>
          </view>
        </view>

        <view
          class="btn settle"
          @tap="onToFirmOrder"
        >
          <text>{{ $t('settlement') }}</text>
          <text class="cart-check-num">
            ({{ cartsCount }}
          </text>)
        </view>
      </block>
      <view
        v-else
        class="btn-box"
      >
        <view
          class="btn collect"
          @tap="onHandleEditProd(1)"
        >
          <text>{{ $t('moveToFavorites') }}</text>
        </view>
        <view
          class="btn del"
          @tap="onHandleEditProd(-1)"
        >
          <text>{{ $t('delete') }}</text>
        </view>
      </view>
    </view>
    <!-- end 底部按钮 -->

    <!-- 底部金额明细弹框 -->
    <view
      v-if="!hidePriModal"
      class="pri-modal modals-bottom-dialog"
    >
      <view
        class="modals-cancel"
        @tap="onHidePriModalFun"
      />
      <view
        class="bottom-dialog-box bottom-pos radius"
        :animation="animationData"
      >
        <view class="discount-title radius">
          <text>{{ $t('amountDetails') }}</text>
          <view
            class="close"
            @tap="onHidePriModalFun"
          >
            <image src="/static/images/icon/close_pop.png" />
          </view>
        </view>
        <view class="pri-tips">
          {{ $t('amountDetailsTips') }}
        </view>
        <view class="pri-box">
          <view :class="['goods-list',!priGoodsShow?'jc_sb':'']">
            <view
              v-if="!priGoodsShow"
              class="goods-info"
            >
              <view
                v-for="(goodItem,goodsInx) in priceDetailGoods"
                :key="goodsInx"
                class="goods-item-img"
              >
                <img-show :src="goodItem.pic" />
              </view>
            </view>
            <block v-if="priGoodsShow">
              <view
                v-for="(goodItem,goodsInx) in priceDetailGoods"
                :key="goodsInx"
                class="goods-item-img"
              >
                <img-show :src="goodItem.pic" />
              </view>
            </block>
            <view
              :class="['goods-num ',priGoodsShow?'unfold':'']"
              @tap.stop="onHandlePriGoodsShow"
            >
              {{ $t('selected') }}&nbsp;{{ selectProdsList.length }}&nbsp;{{ $t('piece') }}
            </view>
          </view>
          <view class="price-detail">
            <view class="price-detail-item">
              <view class="det-tit">
                {{ $t('comTotal') }}
              </view>
              <view class="det-num">
                {{ useCurrencyStore().defMark }}{{ toPrice(totalMoney) }}
              </view>
            </view>
            <!-- 合计 -->
            <view
              v-if="finalMoney > 0"
              class="price-detail-item"
            >
              <view class="det-tit">
                {{ $t('total') }}
              </view>
              <view class="total-pri">
                {{ useCurrencyStore().defMark }}{{ toFixxed(finalMoney) }}
              </view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 空列表或加载全部提示 -->
    <empty-all-tips
      v-if="isLoaded"
      :empty-img="emptyImg"
      :img-sty="{width:'500rpx',height:'400rpx','margin-top': '106rpx','margin-bottom': '32rpx'}"
      :is-empty="!isExistProds"
      :empty-tips="$t('shoppingTips')"
    />

    <!-- sku弹窗组件 -->
    <prod-sku-select
      v-if="showSkuPop && currentProdItem.skuList"
      :pic="currentProdItem.pic"
      :least-num="currentProdItem.leastNum"
      :sku-id="currentProdItem.skuId"
      :sku-name="currentProdItem.skuName"
      :activity-info="currentProdItem"
      :sku-list="currentProdItem.skuList"
      :prod-num="currentProdItem.prodCount"
      :prod-id="currentProdItem.prodId"
      :is-main="Boolean(currentProdItem.comboId && currentProdItem.parentBasketId===0)"
      @set-sku="onHandleSetSku"
      @close-sku-pop="onHandleCloseSkuPop"
    />

    <!-- 确认弹窗 -->
    <confirm-pop
      v-model:show-pop="showPop"
      :pop-content="popContent"
      :show-cancel="true"
      @confirm="onConfirmPopConfirm"
    />
  </view>
</template>

<script setup>
import Big from 'big.js'
import emptyImg from '@/static/images/empty-img/empty_cart.png'
import { useCurrencyStore } from '@/stores/modules/currency'

/**
 * 函数节流
 * @param fn
 * @param wait
 * @returns {Function}
 * @constructor
 */
const throttle = (fn, wait) => {
  let timer = null
  return function () {
    const context = this
    const args = arguments
    if (!timer) {
      timer = setTimeout(function () {
        fn.apply(context, args)
        timer = null
      }, wait)
    }
  }
}

const { toPrice, toFixxed, parsePrice } = number()

let shopId = null // 选择的第一个店铺id
let addrId = ''
const shopCartsLoaded = ref(false) // 用户购物车列表是否加载完成
// 头部信息
const isEditCarts = ref(false)
const addrInfo = ref(null)
const addrLoaded = ref(false)
// 所有店铺的数据
const shopCarts = ref([])
let isCartsOp = false // 编辑状态是否有对购物车进行操作

const allChecked = ref(false)
const hideModal = ref(true) // 模态框的状态  true-隐藏  false-显示
const cartsCount = ref(0)

// 金额明细
const hidePriModal = ref(true) // 金额明细模态框的状态  true-隐藏  false-显示
const priGoodsShow = ref(false) // 弹窗商品是否展开

const animationData = ref({})

const hasShopDeliveryList = ref([]) // 店铺配送
const totalSdPri = ref(0) // 店铺配送总额
const hasShopDeliveryProdCount = ref(0) // 店铺配送商品总数

let basketReqData = [] // 请求购物车的数据
// 切换sku弹窗显隐
const showSkuPop = ref(false)
// 当前操作的项商品
const currentProdItem = ref({})

// 选择的满减id
const switchDiscountId = ref('')
const prodListStyle = ref({}) // 页面样式

const popContent = ref({}) // 弹窗内容
const showPop = ref(false)// 弹窗显示

const animation = ref(null)

// 购物车商品数量
const isExistProds = computed(() => {
  return shopCarts.value.length
})

// 已勾选的商品列表
const selectProdsList = computed(() => {
  const list = []
  shopCarts.value.forEach(shopCart => {
    const shopCartItemDiscounts = shopCart.shopCartItemDiscounts
    for (let i = 0; i < shopCartItemDiscounts.length; i++) {
      const cItems = shopCartItemDiscounts[i].shopCartItems
      for (let j = 0; j < cItems.length; j++) {
        if (cItems[j].isChecked) {
          list.push(cItems[j])
        }
      }
    }
  })
  return list
})

// 金额明细商品列表
const priceDetailGoods = computed(() => {
  const list = JSON.parse(JSON.stringify(selectProdsList.value))
  return priGoodsShow.value ? list : list.splice(0, 3)
})

// 列表是否加载完毕
const isLoaded = computed(() => {
  return shopCartsLoaded.value
})

/**
 * 生命周期函数--监听页面加载
 */
onLoad(() => {
  util.transTabbar()
})

/**
 * 生命周期函数--监听页面显示
 */
onShow(async function () {
  prodListStyle.value = {}
  setTimeout(() => {
    uni.setNavigationBarTitle({
      title: $t('shoppingCart2')
    })
  }, 100)
  // showDeliveryWay = false
  if (!uni.getStorageSync('cbecB2cToken')) {
    addrInfo.value = null
    addrLoaded.value = true
    shopCartsLoaded.value = true
    shopCarts.value = []
    util.removeTabBadge()
  } else {
    // 切换地址信息
    const tempAddrInfo = uni.getStorageSync('stationSaddress')
    if (tempAddrInfo) {
      addrId = tempAddrInfo.addrId
      addrInfo.value = tempAddrInfo
      uni.removeStorageSync('stationSaddress')
    } else {
      addrId = 0
    }
    // 加载购物车
    loadBasketData(null)
  }
})
onHide(() => {
  // 重置状态
  hidePriModal.value = true
  isEditCarts.value = false
})

// 更新商品数量,comboInfo标识是否是套餐商品
const onUpdateProdNumber = (e, shopId, prod, comboInfo) => {
  let count = Number(e.detail.value.replace(/\D/g, ''))
  count = count > 9999 ? 9999 : count
  if (count < 1) {
    count = 1
  }
  const diffVal = count - (comboInfo?.comboCount || prod.prodCount)
  if (diffVal === 0) {
    if (comboInfo) {
      comboInfo.prodCountKey = `${prod.basketId}${Math.random() * 1000000}`
    } else {
      prod.prodCountKey = `${prod.basketId}${Math.random() * 1000000}`
    }
    return
  }
  // 判断是否是套餐
  if (comboInfo) {
    onUpdateBasket(shopId, prod, diffVal, null, comboInfo.comboCount)
  } else {
    onUpdateBasket(shopId, prod, diffVal)
  }
}

// 预估到手机回显格式化
const estimatedPriceFormat = (estimatedPrice, count) => {
  const price = new Big(estimatedPrice).div(count).valueOf()
  Big.DP = 2
  return price
}
// 处理头部管理
const onHandleManage = () => {
  isEditCarts.value = !isEditCarts.value
  if (!isEditCarts.value) {
    if (isCartsOp) {
      loadBasketData()
    }
  } else if (isEditCarts.value) {
    isCartsOp = false
  }
  checkAllSelected()
}
// 金额明细商品展开收起
const onHandlePriGoodsShow = () => {
  if (selectProdsList.value.length > 3) {
    priGoodsShow.value = !priGoodsShow.value
  }
}
/**
 * 新增收货地址
 */
const onAddAddr = () => {
  if (!uni.getStorageSync('cbecB2cToken')) {
    uni.navigateTo({
      url: '/package-user/pages/user-login/user-login'
    })
    return
  }
  uni.navigateTo({
    url: '/package-prod/pages/edit-address/edit-address'
  })
}
/**
 * 选择收货地址
 */
const onSelectAddr = () => {
  uni.navigateTo({
    url: '/package-user/pages/delivery-address/delivery-address?selectAddress=1'
  })
}

const finalMoney = ref(0)
const totalMoney = ref(0)
const totalReduceMoney = ref(0)
const freightAmount = ref(0)
const subtractMoney = ref(0)
const memberReduceAmount = ref(0) // 会员价格
/**
 * 加载购物车
 */
const loadBasketData = () => {
  shopCartsLoaded.value = false
  const params = {
    url: `/p/shopCart/info?addrId=${addrId || 0}`,
    method: 'POST'
  }
  http.request(params).then(({ data: res }) => {
    shopCartsLoaded.value = true
    const { shopCarts: tempShopCarts } = res
    shopCarts.value = tempShopCarts
    finalMoney.value = res.finalMoney
    totalMoney.value = res.totalMoney
    freightAmount.value = res.freightAmount
    subtractMoney.value = res.subtractMoney
    totalReduceMoney.value = res.totalReduceMoney
    memberReduceAmount.value = res.memberReduceAmount
    cartsCount.value = res.count
    // 用户地址信息
    addrInfo.value = res.userDeliveryInfo?.userAddr || null
    addrLoaded.value = true
    // 检查全选状态
    checkAllSelected()
    // 重新计算购物车总数量
    http.getCartCount()

    // 清空请求数据
    basketReqData = []
  })
}

/**
 * 结算(结算按钮)
 */
const onToFirmOrder = () => {
  hideModal.value = true
  hidePriModal.value = true

  const tempShopCarts = shopCarts.value
  const basketIds = []
  const shopIds = []
  const selectShopId = []
  const _hasShopDeliveryList = [] // 店铺配送
  let _hasShopDeliveryProdCount = 0 // 店铺配送商品总数
  let _totalSdPri = 0

  tempShopCarts.forEach(shopCart => {
    const shopCartItemDiscounts = shopCart.shopCartItemDiscounts
    shopCartItemDiscounts.forEach(shopCartItemDiscount => {
      shopCartItemDiscount.shopCartItems.forEach((shopCartItem) => {
        if (shopCartItem.isChecked) {
          // 已选择的购物车id
          basketIds.push(shopCartItem.basketId)
          if (shopIds.indexOf(shopCart.shopId) === -1) {
            shopIds.push(shopCart.shopId)
          }
          if (shopCartItem.comboId && shopCartItem.deliveryModeVO?.hasShopDelivery) {
            _hasShopDeliveryList.push(shopCartItem) // 店铺配送
            const x = new Big(_totalSdPri)
            const y = new Big(shopCartItem.price).times(shopCartItem.prodCount)
            _totalSdPri = x.plus(y).valueOf()
            _hasShopDeliveryProdCount += shopCartItem.prodCount
          }
        }
      })
    })
  })
  if (basketIds.length > 50) {
    uni.showModal({
      title: $t('tips'),
      content: $t('basketSelectedCount') + basketIds.length + $t('basketCountOver'),
      confirmText: $t('confirm'),
      showCancel: false
    })
    return
  }

  totalSdPri.value = _totalSdPri
  hasShopDeliveryProdCount.value = _hasShopDeliveryProdCount

  hasShopDeliveryList.value = _hasShopDeliveryList
  if (!basketIds.length) {
    uni.showToast({
      title: $t('selectProduct'),
      icon: 'none'
    })
    return
  }
  uni.setStorageSync('cbecB2cBasketIds', JSON.stringify(basketIds))
  uni.setStorageSync('cbecB2cShopIds', shopIds)
  shopId = selectShopId[0]
  // 保存当前购物车选中的地址，用于提交订单页请求
  uni.setStorageSync('cbecB2cCurCartSelAddr', addrInfo.value)
  uni.navigateTo({
    url: '/package-prod/pages/submit-order/submit-order?orderEntry=0' + '&dvyType=1' + '&shopId=' + shopId // dvyType 配送类型 1:快递 2:自提 3：无需快递 4:同城配送
  })
}

// 页面禁止滚动 1启动；0停止
const onStopPageScroll = (type = 0) => {
  if (type === 0) {
    let scorllVal = 0
    // #ifdef H5
    scorllVal = Math.abs(parseFloat(document.getElementById('prod-list').style.top))
    prodListStyle.value = {}
    nextTick(() => {
      document.getElementById('basket').scrollTop = scorllVal
    })
    // #endif

    // #ifdef MP-WEIXIN || APP-PLUS
    const query = wx.createSelectorQuery()
    query.select('#prod-list').fields({ rect: true }, (res) => {
      prodListStyle.value = {}
      scorllVal = Math.abs(parseFloat(res.top))
      prodListStyle.value = {}
      nextTick(() => {
        uni.pageScrollTo({
          duration: 0,
          scrollTop: scorllVal
        })
      })
    }).exec()
    // #endif
  } else {
    let scorllTop = 0
    // #ifdef H5
    scorllTop = document.getElementById('basket').scrollTop
    prodListStyle.value = {
      width: '702rpx',
      position: 'fixed',
      top: '-' + scorllTop + 'px',
      overflow: 'hidden'
    }
    // #endif
    // #ifdef MP-WEIXIN || APP-PLUS
    const query = wx.createSelectorQuery()
    query.select('#basket').boundingClientRect((rect) => {
      scorllTop = rect.top
      prodListStyle.value = {
        width: '702rpx',
        position: 'fixed',
        top: scorllTop + 'px',
        overflow: 'hidden'
      }
    }).exec()
    // #endif
  }
}

/**
 * 每一项的选择事件
 */
const onHandleCheckProdItem = (shopIndex, shopCartItemIndex, prodIndex) => {
  const _shopCarts = shopCarts.value
  const shopCartItemDiscounts = _shopCarts[shopIndex].shopCartItemDiscounts // 获取购物车列表
  const prod = shopCartItemDiscounts[shopCartItemIndex].shopCartItems[prodIndex]
  if (!isEditCarts.value && (shopCartItemDiscounts[shopCartItemIndex].unDelivery)) return
  isCartsOp = true
  basketReqData.push({
    basketId: prod.basketId,
    isChecked: !prod.isChecked ? 1 : 0
  })
  handleShopCartSelect()
}

/**
 * 全选
 */
const onHandleCheckAll = () => {
  isCartsOp = true
  let _allChecked = allChecked.value
  _allChecked = !_allChecked // 改变状态
  const _shopCarts = shopCarts.value
  _shopCarts.forEach(shopCart => {
    const shopCartItemDiscounts = shopCart.shopCartItemDiscounts
    for (let i = 0; i < shopCartItemDiscounts.length; i++) {
      const cItems = shopCartItemDiscounts[i].shopCartItems
      for (let j = 0; j < cItems.length; j++) {
        if ((shopCartItemDiscounts[i]?.unDelivery) && !isEditCarts.value) {
          continue
        }
        cItems[j].isChecked = _allChecked
        basketReqData.push({ basketId: cItems[j].basketId, isChecked: _allChecked ? 1 : 0 })
      }
    }
  })
  handleShopCartSelect()
}

/**
 * 购物车 勾选/取选 事件
 */
const handleShopCartSelect = (isLoadBasket = true) => {
  const params = {
    url: '/p/shopCart/checkItems',
    method: 'POST',
    data: basketReqData
  }
  http.request(params).then(() => {
    isLoadBasket && loadBasketData()
  })
}

/**
 * 检查全选状态
 */
const checkAllSelected = () => {
  let _allChecked = true
  const _shopCarts = shopCarts.value
  let flag = false
  _shopCarts.forEach((shopCart) => {
    const shopCartItemDiscounts = shopCart.shopCartItemDiscounts
    for (let i = 0; i < shopCartItemDiscounts.length; i++) {
      const cItems = shopCartItemDiscounts[i].shopCartItems
      for (let j = 0; j < cItems.length; j++) {
        if (!isEditCarts.value && !shopCartItemDiscounts[i].unDelivery && !cItems[j].isChecked) {
          _allChecked = false
          flag = true
        }
        if (isEditCarts.value && !cItems[j].isChecked) {
          _allChecked = false
          flag = true
        }
        cItems[j].isChecked = !!cItems[j].isChecked
      }
      if (flag) {
        flag = false
        break
      }
    }
  })
  if (!isEditCarts.value && !_shopCarts.length) {
    _allChecked = false
  }

  allChecked.value = _allChecked
  shopCarts.value = _shopCarts
}

/**
 * 设置sku
 */
const onHandleSetSku = (sku) => {
  const _currentProdItem = currentProdItem.value
  onUpdateBasket(_currentProdItem.shopId, _currentProdItem, _currentProdItem.prodCount, sku)
}

const editShopCartItem = ref({}) // 当前操作的项
/**
 * 修改 购物车数量、sku、满减活动
 * @param {Number} shopId 店铺
 * @param {Object} prod 修改的商品项
 * @param {Number} count 商品数量增量
 * @param {Object} sku 修改的sku
 */
// eslint-disable-next-line max-params
const onUpdateBasket = throttle((shopId, prod, count, sku = null, packageNum = null, useMember = undefined) => {
  if (!prod.isUpdateCount) return
  // 商品数量为1 且点击 -1
  if (count === -1 && prod.prodCount === 1 && !packageNum) {
    uni.showModal({
      title: $t('prompt'),
      content: $t('removeBasketProdTips'),
      success: function (res) {
        if (res.confirm) {
          submitUpdateBasket(shopId, prod, count, sku, useMember)
        }
      }
    })
    return
  }
  if (count === -1 && packageNum === 1) {
    uni.showModal({
      title: $t('prompt'),
      content: $t('removeBasketComboTips'),
      success: function (res) {
        if (res.confirm) {
          submitUpdateBasket(shopId, prod, count, sku, useMember)
        }
      }
    })
    return
  }
  // 商品数量为9999 且点击 +1，限制最大数量为9999
  if (count === 1 && (prod.prodCount === 9999 || packageNum === 9999)) {
    return
  }
  if (sku && prod.skuId === sku.skuId) {
    showSkuPop.value = false
    return
  }
  submitUpdateBasket(shopId, prod, count, sku, useMember)
}, 200)

const submitUpdateBasket = (shopId, prod, count, sku = null, useMember = undefined) => {
  currentProdItem.value = prod
  const matchingSkuIds = []
  if (prod.comboId) {
    const _shopCarts = shopCarts.value
    const shopIndex = _shopCarts.findIndex(el => el.shopId === shopId)
    const shopCartItemDiscounts = _shopCarts[shopIndex].shopCartItemDiscounts
    const shopCartItemIndex = shopCartItemDiscounts.findIndex(el => el.chooseComboItemDto?.comboId === prod.comboId && prod.basketId === el.chooseComboItemDto.mainProdBasketId)
    if (!prod.parentBasketId) {
      shopCartItemDiscounts[shopCartItemIndex].shopCartItems.forEach(el => {
        if (el.parentBasketId === prod.basketId) {
          matchingSkuIds.push(el.skuId)
        }
      })
    }
  }

  if (sku && editShopCartItem.value.chooseComboItemDto && editShopCartItem.value.chooseComboItemDto.comboId) {
    count = editShopCartItem.value.chooseComboItemDto.comboCount
  } else if (sku) {
    count = prod.prodCount
  }
  const updateBasketParam = {
    basketId: prod.basketId,
    comboId: prod.comboId || undefined,
    discountId: switchDiscountId.value || prod.discountId,
    count,
    oldSkuId: sku ? prod.skuId : undefined,
    matchingSkuIds: prod.comboId ? matchingSkuIds : undefined,
    prodId: prod.prodId,
    shopId,
    skuId: sku ? sku.skuId : prod.skuId,
    useMember
  }
  prod.isUpdateCount = false
  const params = {
    url: '/p/shopCart/changeItem',
    method: 'POST',
    data: updateBasketParam
  }
  http.request(params).then(() => {
    loadBasketData()
    showSkuPop.value = false
    switchDiscountId.value = ''
  }).catch(() => {
    prod.isUpdateCount = true
  })
}

// 处理商品编辑
let selectedCartProdIds = ''
let selectedBasketIds = []
const onHandleEditProd = (opType) => {
  const _shopCarts = shopCarts.value
  const basketIds = []
  const prodIds = []
  // 购物车商品勾选
  _shopCarts.forEach(shopCart => {
    const shopCartItemDiscounts = shopCart.shopCartItemDiscounts
    for (let i = 0; i < shopCartItemDiscounts.length; i++) {
      const cItems = shopCartItemDiscounts[i].shopCartItems
      for (let j = 0; j < cItems.length; j++) {
        if (cItems[j].isChecked) {
          basketIds.push(cItems[j].basketId)
          prodIds.push(cItems[j].prodId)
        }
      }
    }
  })
  if (basketIds.length === 0 || prodIds.length === 0) {
    uni.showToast({
      title: $t('selectProduct'),
      icon: 'none'
    })
    return
  }
  selectedCartProdIds = prodIds.join(',')
  selectedBasketIds = basketIds
  if (opType === 1) {
    onCollectProd()
  } else {
    popContent.value = {
      content: $t('deleteProductTips'),
      cancelText: $t('cancel'),
      confirmText: $t('confirm')
    }
    showPop.value = true
  }
}

// 收藏商品
const onCollectProd = () => {
  http.request({
    url: '/p/user/collection/orderProdCollectionAll',
    method: 'POST',
    data: selectedCartProdIds
  }).then(() => {
    onDeleteProd(1)
  })
}

/**
 * 删除购物车商品
 * @param {Boolean} isCollect 是否是收藏商品后的删除
 */
const onDeleteProd = (isCollect) => {
  http.request({
    url: '/p/shopCart/deleteItem',
    method: 'PUT',
    data: selectedBasketIds
  }).then(() => {
    uni.showToast({
      title: isCollect ? $t('collectionMoved') : $t('deleteSuccess'),
      duration: 2000,
      icon: 'none'
    })
    basketReqData = []
    loadBasketData() // 重新加载购物车
  })
}

/**
 * 根据商品id获取skuList
 * @param {Object} prod 当前要修改的选项
 */
const onGetSkuListByProdId = (prod, _shopCartItem) => {
  const comboList = []
  if (prod.comboId) {
    const _shopCarts = shopCarts.value
    const shopIndex = _shopCarts.findIndex(el => el.shopId === prod.shopId)
    const shopCartItemDiscounts = _shopCarts[shopIndex].shopCartItemDiscounts
    const shopCartItemIndex = shopCartItemDiscounts.findIndex(el => el.chooseComboItemDto.comboId === prod.comboId)
    shopCartItemDiscounts[shopCartItemIndex].shopCartItems.forEach(el => {
      comboList.push({
        basketId: el.basketId,
        skuId: el.skuId,
        prodId: el.prodId
      })
    })
    prod.comboList = comboList
  }
  currentProdItem.value = prod
  editShopCartItem.value = _shopCartItem
  const params = {
    url: prod.comboId ? '/combo/skuList' : '/prod/skuList',
    method: 'GET',
    data: {
      comboId: prod.comboId,
      prodId: prod.prodId
    },
    hasCatch: true
  }
  http.request(params).then(({ data: res }) => {
    currentProdItem.value.skuList = res
    showSkuPop.value = true
  })
}

/**
   * 隐藏sku弹窗
   */
const onHandleCloseSkuPop = () => {
  showSkuPop.value = false
}

// 动画集
const fadeIn = () => {
  animation.value.translateY(0).step()

  animationData.value = animation.value.export() // 动画实例的export方法导出动画数据传递给组件的animation属性
}
const fadeDown = () => {
  animation.value.translateY(300).step()

  animationData.value = animation.value.export()
}

/**
  * 跳转到商品详情
*/
const onToProdPage = (prodId) => {
  uni.navigateTo({
    url: '/package-prod/pages/prod/prod?prodId=' + prodId
  })
}

/**
  * 金额明细弹窗
*/
const onShowPriDet = () => {
  if (cartsCount.value === 0) {
    return
  }
  onStopPageScroll(1)
  if (hidePriModal.value == true) {
    hidePriModal.value = false
    hideModal.value = true

    const _animation = uni.createAnimation({
      duration: 500,
      timingFunction: 'ease'
    })
    animation.value = _animation
    setTimeout(() => {
      fadeIn()
    }, 100)
  } else if (hidePriModal.value == false) {
    onHidePriModalFun()
  }
}

const onHidePriModalFun = () => {
  onStopPageScroll(0)
  const _animation = uni.createAnimation({
    duration: 800,
    timingFunction: 'ease'
  })
  animation.value = _animation
  fadeDown()
  setTimeout(() => {
    hidePriModal.value = true
    hideModal.value = true
  }, 680)
}

/**
 * 确认删除
 */
const onConfirmPopConfirm = () => {
  onDeleteProd()
  showPop.value = false
}

</script>
<style lang="scss" scoped>
@use "./basket.scss";
</style>

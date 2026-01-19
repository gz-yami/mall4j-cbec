<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <z-paging
    ref="pagingRef"
    v-model="hotSalesList"
    :auto="false"
    :refresher-enabled="false"
    :default-page-size="10"
    :safe-area-inset-bottom="false"
    :auto-scroll-to-top-when-reload="false"
    :auto-clean-list-when-reload="false"
    hide-empty-view
    :paging-style="{'top': '0','background': '#f2f3f7','bottom':'var(--window-bottom)'}"
    @query="onGetHotSalesProds"
  >
    <view class="Mall4j page-user">
      <!-- 自定义导航头 -->
      <!-- #ifndef H5-->
      <lh-navigation-bar
        v-if="navigationBarIsShow"
        :bg-img-mode="bgImgMode"
        :show-back="false"
        :navigation-bar-style="tabConfig"
        :is-left="false"
        :is-bg-img="isBgImg"
        :is-bg-img-show="true"
        :bg-img-src="bgImgSrc"
        :title="$t('personalCenter')"
      />
      <!-- #endif -->
      <view class="top-wrap">
        <image
          class="top-bg-img"
          src="@/static/images/icon/bg-2.png"
        />
        <view
          v-if="isAuthInfo"
          class="top-infor-wrap"
        >
          <!-- 用户信息（登录） -->
          <view
            v-if="isAuthInfo"
            class="userinfo-wrap"
          >
            <view class="left-infor">
              <!-- 头像 -->
              <image
                class="avatsr"
                :src="userInfo.pic? util.checkFileUrl(userInfo.pic) : '/static/images/icon/head04.png'"
                mode="aspectFill"
                @tap="onToPersonalInformation"
                @error="userInfo.pic='/static/images/icon/head04.png'"
              />
              <view class="infor-wrap">
                <!-- 姓名 -->
                <view
                  class="user-name"
                  @tap="onToPersonalInformation"
                >
                  {{ userInfo.nickName }}
                </view>
              </view>
            </view>
            <view class="right-infor">
              <image
                class="sign-in"
                src="/static/images/icon/icon_sign.png"
                @tap="onToPointsCenter"
              />
              <view
                class="message"
                @tap="onToMyMessage"
              >
                <image
                  src="/static/images/icon/icon_message.png"
                />
                <view
                  v-if="messageCount > 0"
                  class="quantity-tip-dots"
                >
                  {{ messageCount }}
                </view>
              </view>
            </view>
          </view>

          <!-- 钱包数据 -->
          <view
            v-if="isAuthInfo"
            class="wallet-wrap"
          >
            <view class="cloumn-wrap">
              <view
                class="cloumn-item"
                @tap="onToMyWallet"
              >
                <view class="numbers-txt">
                  0
                </view>
                <view class="infor-txt">
                  {{ $t('balance') }}
                </view>
              </view>
              <view
                class="cloumn-item"
                @tap="onToMemberInteral"
              >
                <view class="numbers-txt">
                  0
                </view>
                <view class="infor-txt">
                  {{ $t('prodType4') }}
                </view>
              </view>
              <view
                class="cloumn-item"
                @tap="onMyCollectionHandle"
              >
                <view class="numbers-txt">
                  {{ collectionCount }}
                </view>
                <view class="infor-txt">
                  {{ $t('collection') }}
                </view>
              </view>
            </view>
          </view>
          <!-- 钱包数据 end -->
        </view>
        <!-- end 用户信息 -->

        <!-- 未登录 -->
        <view
          v-else
          class="default-userinfor-wrap top-infor-wrap"
        >
          <view class="left-infor">
            <!-- 头像 -->
            <image
              class="avatsr"
              src="/static/images/icon/head04.png"
              mode="scaleToFill"
              @tap="onGotUserInfo"
            />
            <view
              class="sign-in-txt"
              @tap="onGotUserInfo"
            >
              {{ $t('loginNow') }}
            </view>
          </view>
          <!-- 头像 end -->

          <!-- 钱包数据 -->
          <view
            class="wallet-wrap"
            @tap="onGotUserInfo"
          >
            <view class="cloumn-wrap">
              <view class="cloumn-item">
                <view class="numbers-txt">
                  -
                </view>
                <view class="infor-txt">
                  {{ $t('balance') }}
                </view>
              </view>
              <view class="cloumn-item">
                <view class="numbers-txt">
                  -
                </view>
                <view class="infor-txt">
                  {{ $t('prodType4') }}
                </view>
              </view>
              <view class="cloumn-item">
                <view class="numbers-txt">
                  -
                </view>
                <view class="infor-txt">
                  {{ $t('collection') }}
                </view>
              </view>
            </view>
          </view>
          <!-- 钱包数据 end -->
        </view>
      <!-- 未登录 end -->
      </view>

      <!-- 我的订单 -->
      <view class="my-order-wrap">
        <view class="top-title">
          <view class="title-left">
            {{ $t('myOrder') }}
          </view>
          <view
            class="title-right"
            data-sts="0"
            @tap="onToOrderListPage"
          >
            <text>{{ $t('allOrder') }}</text>
            <image
              src="/static/images/icon/arrow-right.png"
            />
          </view>
        </view>
        <view class="cloumn-wrap">
          <view
            class="cloumn-item"
            data-sts="1"
            @tap="onToOrderListPage"
          >
            <image
              class="item-img"
              src="/static/images/icon/icon_unpaid.png"
            />
            <view class="infor-txt">
              {{ $t('toBePaid') }}
            </view>
            <view
              v-if="orderAmount.unPay>0"
              class="quantity-tip-dots"
            >
              {{ dotNumLimit(orderAmount.unPay) }}
            </view>
          </view>
          <view
            class="cloumn-item"
            data-sts="2"
            @tap="onToOrderListPage"
          >
            <image
              class="item-img"
              src="/static/images/icon/icon_delivery.png"
            />
            <view class="infor-txt">
              {{ $t('toBeDelivered') }}
            </view>
            <view
              v-if="orderAmount.payed>0"
              class="quantity-tip-dots"
            >
              {{ dotNumLimit(orderAmount.payed) }}
            </view>
          </view>
          <view
            class="cloumn-item"
            data-sts="3"
            @tap="onToOrderListPage"
          >
            <image
              class="item-img"
              src="/static/images/icon/icon_logistics.png"
            />
            <view class="infor-txt">
              {{ $t('toBeReceived') }}
            </view>
            <view
              v-if="orderAmount.consignment>0"
              class="quantity-tip-dots"
            >
              {{ dotNumLimit(orderAmount.consignment ) }}
            </view>
          </view>
          <view
            class="cloumn-item"
            data-sts="5"
            @tap="onToOrderListPage"
          >
            <image
              class="item-img"
              src="/static/images/icon/icon_complete.png"
            />
            <view class="infor-txt">
              {{ $t('complete') }}
            </view>
          </view>
        </view>
      </view>
      <!-- end 我的订单 -->

      <!-- 会员中心 & 积分商店 -->
      <view class="distribution-points-wrap">
        <view
          class="distribution-item"
          @tap="onToDistCenter"
        >
          <view class="txt-left">
            <text>{{ $t('distributionUser') }}</text>
            <text>{{ $t('shareGet') }}</text>
          </view>
          <image
            class="img-right"
            src="/static/images/icon/distribution-center.png"
          />
        </view>
        <view
          class="points-item"
          @tap="onToMemberInteral"
        >
          <view class="txt-left">
            <text>{{ $t('pointsMall') }}</text>
            <text>{{ $t('savePoints') }}</text>
          </view>
          <image
            class="img-right"
            src="/static/images/icon/integral-mall.png"
          />
        </view>
      </view>
      <!-- end 分销中心 & 积分商店 -->

      <!-- 服务与工具 -->
      <view class="tools-wrap">
        <view class="title-txt">
          {{ $t('servicesTools') }}
        </view>
        <view class="cloumn-wrap">
          <view
            class="cloumn-item"
            @tap="onToAddressList"
          >
            <image
              class="item-img"
              src="/static/images/icon/icon_address.png"
            />
            <view class="infor-txt">
              {{ $t('addressManagenment') }}
            </view>
          </view>
          <view
            class="cloumn-item"
            @tap="onChangeLangs"
          >
            <image
              class="item-img"
              src="/static/images/icon/icon_switch.png"
            />
            <view class="infor-txt">
              {{ $t('switchLanguages') }}
            </view>
          </view>
          <view
            class="cloumn-item"
            @tap="onGotoCurrency"
          >
            <image
              class="item-img"
              src="/static/images/icon/icon_currency.svg"
            />
            <view class="infor-txt">
              {{ $t('changeCurrency') }}
            </view>
          </view>

          <view
            class="cloumn-item"
            @tap="onToSystemSetUp"
          >
            <image
              class="item-img"
              src="/static/images/icon/icon_set.png"
            />
            <view class="infor-txt">
              {{ $t('systemSetup') }}
            </view>
          </view>
        </view>
      </view>
      <!-- end 服务与工具 -->

      <!-- 为您推荐 -->
      <view
        v-if="+hotSalesList.length !== 0"
        class="recommend"
      >
        <image
          class="before-img"
          src="/static/images/icon/decor-1.png"
          mode="widthFix"
        />
        <view class="title-txt">
          {{ $t('RecommendedYou') }}
        </view>
        <image
          class="after-img"
          src="/static/images/icon/decor-2.png"
          mode="widthFix"
        />
      </view>
      <!-- end 为您推荐 -->
      <!-- 推荐内容 -->
      <view
        v-if="+hotSalesList.length !== 0"
        class="recommend-products"
      >
        <prod-list
          :coupon-show="false"
          :show-vip="true"
          :show-brief="true"
          :prod-name-type="1"
          new-data
          :prods="hotSalesList"
        />
      </view>
      <!-- 回到顶部 -->
      <back-top-btn v-if="showBacktop" />
    </view>
  </z-paging>
</template>

<script setup>
import util from '@/utils/util'

// 活动类型
const prodTypeArr = ref([])
// 回到顶部
const showBacktop = ref(false)

const bgImgSrc = ref('/static/images/icon/mine-bg.png')
const isBgImg = ref(false)
const bgImgMode = ref('top')
const tabConfig = reactive({
  top: '0',
  bottom: '',
  background: '',
  fontColor: '#333333',
  iconColor: '#333333'
})
const navigationBarIsShow = ref(false)

const orderAmount = ref({})
const collectionCount = ref(0)

const isAuthInfo = ref(false)

// 用户信息
const userInfo = ref({})
// 消息数量
const messageCount = ref(0)
// 用户积分
const score = ref(0)
// 用户余额
const totalBalance = ref(0)
onMounted(() => {
  const res = uni.getSystemInfoSync()
  if (res.system.toString().indexOf('Windows') < 0) {
    navigationBarIsShow.value = true
  }
  pagingRef?.value?.reload()
  // 热销商品推荐数据重置
  getApp().globalData.userPagesGetHotSalesProds = pagingRef?.value?.reload
  prodTypeArr.value = [
    $t('prodType1'),
    $t('prodType2'),
    $t('prodType3'),
    $t('prodType4')
  ]
})

/**
 * 用户点击右上角分享 (勿删)
 */
onShareAppMessage(() => {})

/**
 * 生命周期函数--监听页面加载
 */
onLoad(() => {
  util.transTabbar()
})
/**
 * 生命周期函数--监听页面显示
 */
onShow(async () => {
  await uni.setNavigationBarTitle({
    title: $t('personalCenter')
  })
  await Promise.resolve() // 解决在onshow 加载数据异常

  // #ifndef H5
  const logData = uni.getStorageSync('cbecB2cFlowAnalysisLogDto')
  uni.setStorageSync('cbecB2cStep', uni.getStorageSync('cbecB2cStep') / 1 + 1)
  if (logData && +logData.pageId !== 12) {
    logData.pageId = 12
    logData.step = uni.getStorageSync('cbecB2cStep')
    uni.setStorageSync('cbecB2cFlowAnalysisLogDto', logData)
    http.saveLog(logData, 1)
  }
  // #endif

  http.getCartCount()

  // 等待所有dom都挂载出来后再加载数据
  setTimeout(() => {
    onLoadData()
  }, 100)
})

const onLoadData = () => {
  // 排除影响，真实登陆后再请求数据
  if (uni.getStorageSync('cbecB2cToken')) {
    isAuthInfo.value = true
    // 加载订单数据
    loadOrderCountFun()
    // 查询用户信息
    queryUserInfo()
    // 查询所有的收藏量
    showCollectionCount()
  } else {
    isAuthInfo.value = false
    orderAmount.value = {}
    score.value = 0 // 用户积分
    totalBalance.value = 0 // 用户余额
    collectionCount.value = 0 // 总收藏数量
  }
}

// 推荐商品
const hotSalesList = ref([])
/**
 * 加载推荐商品列表
 */
const pagingRef = ref(null)
const onGetHotSalesProds = (pageNum, pageSize) => {
  let userId = null
  if (uni.getStorageSync('cbecB2cToken') && uni.getStorageSync('cbecB2cUserInfo')) {
    userId = uni.getStorageSync('cbecB2cUserInfo').userId
  }
  const categoryId = uni.getStorageSync('cbecB2cBrowseCategoryId')
  const userInfo = uni.getStorageSync('cbecB2cUserInfo')
  let paramUrl = '/search/page'
  const paramData = {
    current: pageNum,
    size: pageSize,
    userId
  }
  // prodRecommendation 0:关(新品推荐)  1:开(个性化推荐)
  if (userInfo && userInfo.prodRecommendation === 1) {
    paramUrl = '/prod/recommendList'
    paramData.categoryId = categoryId
  } else {
    paramData.sort = 21
  }
  const param = {
    url: paramUrl,
    method: 'GET',
    data: paramData
  }
  http.request(param).then(({ data: res }) => {
    pagingRef?.value?.complete(res.records[0].products)
  })
}

/**
 * 跳转站内消息
 */
const onToMyMessage = () => {
  util.notOpenTips()
}
/**
 * 切换语言
 */
const onChangeLangs = () => {
  uni.navigateTo({
    url: '/package-user/pages/lang/lang'
  })
}

// 加载订单数据
const loadOrderCountFun = () => {
  const params = {
    url: '/p/user/centerInfo',
    method: 'GET',
    dontTrunLogin: true,
    data: {}
  }
  http.request(params).then(({ data: res }) => {
    if (uni.getStorageSync('cbecB2cToken')) {
      orderAmount.value = res.orderCountData
    }
  })
}

// 我的钱包
const onToMyWallet = () => {
  util.notOpenTips()
}

/**
 * 获取用户信息
 */
const queryUserInfo = () => {
  const params = {
    url: '/p/user/userInfo',
    method: 'GET',
    data: {},
    dontTrunLogin: true
  }
  http.request(params).then(({ data: res }) => {
    userInfo.value = res
    uni.setStorageSync('cbecB2cUserInfo', res)
  })
}

/**
 * 跳转分销员中心
 */
const onToDistCenter = () => {
  util.notOpenTips()
}
const onToSystemSetUp = () => {
  util.checkAuthInfo(() => {
    uni.navigateTo({
      url: '/package-user/pages/account-settings/account-settings'
    })
  })
}

// 积分中心
const onToPointsCenter = () => {
  util.notOpenTips()
}

const onToAddressList = () => {
  util.checkAuthInfo(() => {
    uni.navigateTo({
      url: '/package-user/pages/delivery-address/delivery-address'
    })
  })
}
const onToOrderListPage = (e) => {
  util.checkAuthInfo(() => {
    const sts = e.currentTarget.dataset.sts
    uni.navigateTo({
      url: '/package-user/pages/order-list/order-list?sts=' + sts
    })
  })
}

/**
 * 查询所有的收藏量
 */
const showCollectionCount = () => {
  const params = {
    url: '/p/user/collection/collectionCount',
    method: 'GET'
  }
  http.request(params).then(({ data: res }) => {
    collectionCount.value = res
  })
}

/**
 * 我的收藏跳转
 */
const onMyCollectionHandle = () => {
  util.checkAuthInfo(() => {
    let url = '/package-user/pages/prod-classify/prod-classify?sts=5'
    const id = 0
    const title = $t('myCollection')
    if (id) {
      url += '&tagid=' + id + '&title=' + title
    }
    uni.navigateTo({
      url
    })
  })
}

/**
 * 跳转到修改用户头像昵称资料
 */
const onToPersonalInformation = () => {
  uni.navigateTo({
    url: '/package-user/pages/personal-information/personal-information'
  })
}

/**
 * 获取用户信息
 */
const onGotUserInfo = () => {
  util.checkAuthInfo()
}

/**
 * 跳转到切换货币界面
 */
const onGotoCurrency = () => {
  uni.navigateTo({
    url: '/package-user/pages/currency/currency'
  })
}

/**
 * 跳转积分中心
 */
const onToMemberInteral = () => {
  util.notOpenTips()
}

/**
 *  点数大于100，显示99+
 * @param {*} val 点数
 */
const dotNumLimit = (val) => {
  if (!val) return val
  if (val >= 100) return '99+'
  return val
}
</script>

<style lang="scss" scoped>
  @use "./user.scss";
</style>

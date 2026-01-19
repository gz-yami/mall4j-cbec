<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <view class="Mall4j page-delivery-address container">
    <view
      v-if="addressList.length"
      class="main"
    >
      <view class="radio-group">
        <view
          v-for="(item, index) in addressList"
          :key="index"
          class="address"
          @tap.stop="selectItem(item)"
        >
          <view
            class="personal"
            :data-addrid="item.addrId"
            @tap="selAddrToOrder"
          >
            <view class="addr">
              <text>
                {{ item.province }}
                <span v-if="item.city">
                  / {{ item.city }}
                </span>
                <span v-if="item.area">
                  / {{ item.area }}
                </span>
              </text>
            </view>
            <view class="addr-detail">
              <text>{{ item.addr }}</text>
            </view>
            <view class="info-tit">
              <text>{{ item.postCode }}</text>
            </view>
            <view class="info-tit">
              <text class="name">
                {{ item.receiver }}
              </text>
              <text class="tel">
                {{ item.mobile }}
              </text>
            </view>
          </view>
          <view class="option-box">
            <view class="select-btn">
              <view
                style="transform:scale(0.78)"
                color="#F81A1A"
                :class="['cs-radio', item.commonAddr === 1 ? 'active' : '']"
                @click.stop="onDefaultAddr(item.addrId)"
              >
                <image
                  v-if="item.commonAddr === 1"
                  class="right-icon"
                  src="@/package-user/static/images/icon/hook.png"
                />
              </view>
              <text :class="['radio-text',{asDef:item.commonAddr === 1}]">
                {{ item.commonAddr === 1 ? $t('toDefaultAddress') : $t('asDefaultAddress') }}
              </text>
            </view>
            <view class="op-box">
              <text
                v-if="item.commonAddr !== 1"
                class="del-btn"
                :data-addrid="item.addrId"
                @tap.stop="handleDelAddr"
              >
                {{ $t('delete') }}
              </text>
              <text
                class="edit-btn"
                :data-addrid="item.addrId"
                @tap.stop="toEditAddress"
              >
                {{ $t('invoice.update') }}
              </text>
            </view>
          </view>
        </view>
      </view>
    </view>
    <view class="footer-box">
      <view
        class="footer"
        @tap="onAddAddr"
      >
        <image
          src="@/package-user/static/images/icon/icon-add.png"
        />
        <text>{{ $t('addAddress') }}</text>
      </view>
    </view>

    <confirm-pop
      :show-pop="confirmPopShow"
      :pop-content="confirmPopContent"
      @confirm="handlePopConfirm"
      @cancel="confirmPopShow=false"
    />

    <pop-toast v-if="showToast" />

    <!-- 空列表或加载全部提示 -->
    <empty-all-tips
      v-if="isLoaded"
      :img-sty="{ width: '500rpx', height: '400rpx'}"
      :empty-img="emptyImg"
      :is-empty="addressList.length === 0"
      :empty-tips="$t('noShippingAddress')"
    />
  </view>
</template>

<script setup>
import emptyImg from '@/package-user/static/images/empty-img/empty_address.png'

let order = -1
let isSelect = false
onLoad((option) => {
  if (option.order) {
    order = option.order
  }
  if (option.selectAddress) {
    isSelect = true
  }
})

// 加载地址列表
onShow(() => {
  // 加载导航标题
  uni.setNavigationBarTitle({
    title: $t('shippingAddress')
  })
  getAddrList()
})

const isLoaded = ref(false)
const addressList = ref([])
/**
 * 获取收货地址列表
 */
const getAddrList = () => {
  isLoaded.value = false
  const params = {
    url: '/p/address/list',
    data: {
      isDefaultFirst: false
    },
    method: 'GET'
  }
  http.request(params).then(({ data }) => {
    isLoaded.value = true
    data.sort((a, b) => {
      return b.commonAddr - a.commonAddr
    })
    addressList.value = data
  })
}

/**
 * 新增收货地址
 */
const onAddAddr = () => {
  if (addressList.value.length === 10) {
    uni.showToast({
      title: $t('addNewAddressTips'),
      icon: 'none'
    })
  } else {
    uni.navigateTo({
      url: '/package-prod/pages/edit-address/edit-address'
    })
  }
}

/**
 * 设置为默认地址
 */
const onDefaultAddr = (addrId) => {
  const params = {
    url: '/p/address/defaultAddr/' + addrId,
    method: 'PUT'
  }
  http.request(params).then(() => {
    setTimeout(() => {
      getAddrList()
    }, 100)
  })
}

// 选择地址
const selectItem = (item) => {
  if (isSelect) {
    const obj = {
      name: item.receiver,
      userMobile: item.mobile,
      ...item
    }
    uni.setStorageSync('stationSaddress', obj)
    // 普通h5返回上一页
    // #ifdef H5
    history.back()
    // #endif
    // 微信小程序与APP返回上一页
    // #ifdef APP-PLUS
    uni.navigateBack({
      delta: 1
    })
    // #endif
  }
}

let delAddrId = 0
// 确认框参数
const confirmPopShow = ref(false)
const confirmPopContent = ref({})
const handleDelAddr = (e) => {
  delAddrId = e.currentTarget.dataset.addrid
  confirmPopShow.value = true
  confirmPopContent.value = {
    content: $t('deleteAddressTips'),
    confirmText: $t('delete')
  }
}

const handlePopConfirm = () => {
  deleteAddr()
}

const showToast = ref(false)
/**
 * 删除地址
 */
const deleteAddr = () => {
  const params = {
    url: '/p/address/deleteAddr/' + delAddrId,
    method: 'DELETE',
    data: {}
  }
  http.request(params).then(() => {
    delAddrId = 0
    confirmPopShow.value = false
    getAddrList()
    showToast.value = true
    setTimeout(() => {
      showToast.value = false
    }, 1200)
  })
}

/**
 * 修改地址
 */
const toEditAddress = (e) => {
  const addrId = e.currentTarget.dataset.addrid
  uni.navigateTo({
    url: '/package-prod/pages/edit-address/edit-address?addrId=' + addrId
  })
}

/**
 * 选择地址 跳转回提交订单页
 */
const selAddrToOrder = (e) => {
  const id = e.currentTarget.dataset.addrid
  let item = null
  addressList.value.forEach((addrItem) => {
    if (addrItem.addrId === id) {
      item = addrItem
    }
  })
  if (order === 0) {
    const pages = getCurrentPages() // 当前页面
    const prevPage = pages[pages.length - 2] // 上一页面
    prevPage.item = item
    prevPage.selAddress = 'yes'
    // 返回
    uni.navigateBack({
      delta: 1
    })
  }
}

</script>
<style lang="scss" scoped>
@use "delivery-address";
</style>

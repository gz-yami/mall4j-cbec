<!-- 账户设置 -->
<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <view class="Mall4j page-account-settings">
    <view class="item-wrap">
      <view
        class="cloumn-item"
        @tap="toPersonalInformation"
      >
        <view class="left-infor">
          <image
            :src="userInfo.pic? util.checkFileUrl(userInfo.pic) : head04Png"
            mode="aspectFill"
            @error="imageError(userInfo,'pic')"
          />
          <text class="nick-name">
            {{ userInfo.nickName }}
          </text>
        </view>
        <view class="right-img">
          <view class="txt-wrap">
            {{ $t('personalInfor') }}
          </view>
          <image
            class="right-img"
            src="@/static/images/icon/arrow-right.png"
            mode="scaleToFill"
          />
        </view>
      </view>
      <view
        class="cloumn-item"
        @tap="toAddressList"
      >
        <view class="txt-wrap">
          {{ $t('myAddress') }}
        </view>
        <image
          class="right-img"
          src="@/static/images/icon/arrow-right.png"
          mode="scaleToFill"
        />
      </view>
    </view>

    <view class="item-wrap">
      <view
        class="cloumn-item"
        @tap.stop=""
      >
        <view class="txt-wrap">
          {{ $t('emailAddress') }}
        </view>
        <view class="right-img">
          <view class="phone-number">
            {{ userInfo.userMail }}
          </view>
        </view>
      </view>
      <view
        class="cloumn-item"
        @tap.stop="goPersonalRecommend"
      >
        <view class="txt-wrap">
          {{ $t('personRec') }}
        </view>
        <view class="right-img">
          <view class="txt-wrap">
            {{ userInfo.prodRecommendation ? $t('openTips') : $t('close') }}
          </view>
          <image
            class="right-img"
            src="@/static/images/icon/arrow-right.png"
            mode="scaleToFill"
          />
        </view>
      </view>
      <view
        class="cloumn-item"
        @tap="cancellation"
      >
        <view class="txt-wrap">
          {{ $t('cancellationAccount') }}
        </view>
        <image
          class="right-img"
          src="@/static/images/icon/arrow-right.png"
          mode="scaleToFill"
        />
      </view>
    </view>
    <view class="item-wrap">
      <view
        class="cloumn-item"
        @tap="toTermsOfService('serviceTerms')"
      >
        <view class="txt-wrap">
          {{ $t('termService') }}
        </view>
        <image
          class="right-img"
          src="@/static/images/icon/arrow-right.png"
          mode="scaleToFill"
        />
      </view>
      <view
        class="cloumn-item"
        @tap="toTermsOfService('servicePolicy')"
      >
        <view class="txt-wrap">
          {{ $t('privacyPolicys') }}
        </view>
        <image
          class="right-img"
          src="@/static/images/icon/arrow-right.png"
          mode="scaleToFill"
        />
      </view>
    </view>
    <view class="out-btns">
      <button @tap="logout">
        {{ $t('logoutBtns') }}
      </button>
    </view>
  </view>
</template>

<script setup>
import util from '@/utils/util.js'
import head04Png from '@/static/images/icon/head04.png'

onShow(() => {
  uni.setNavigationBarTitle({
    title: $t('accountSettings')
  })
  // 获取用户消息
  queryUserInfo()
})

/**
 * 获取用户信息
 */
const userInfo = ref({}) // 用户信息
const queryUserInfo = () => {
  const params = {
    url: '/p/user/userInfo',
    method: 'GET'
  }
  http.request(params).then(({ data }) => {
    // 防止二次赋值时，data被污染
    userInfo.value = JSON.parse(JSON.stringify(data))
    userInfo.value.userMail = onEncryption(userInfo.value.userMail)
    uni.setStorageSync('cbecB2cUserInfo', data)
  })
}

/**
 * 加密邮箱
 * @param email
 */
const onEncryption = (email) => {
  const pre = email.substr(0, 3)
  const next = email.substr(7, email.length - 1)
  return pre + '****' + next
}

/**
 * 跳转到修改用户头像昵称资料
 */
const toPersonalInformation = () => {
  uni.navigateTo({
    url: '/package-user/pages/personal-information/personal-information'
  })
}

// 图片出错处理
const imageError = (target, attr) => {
  target[attr] = ''
}

/**
 * 跳转修改密码
 */
const setPassword = () => {
  uni.navigateTo({
    url: '/package-user/pages/user-login/user-login?isForgetPassword=true' + '&isPersonalCenter=true'
  })
}

/**
 * 跳转个性化推荐设置
 */
const goPersonalRecommend = () => {
  uni.navigateTo({
    url: '/package-user/pages/personal-recommend/personal-recommend'
  })
}

/**
 * 退出登录
 */
const logout = () => {
  uni.navigateTo({
    url: '/package-user/pages/logout/logout'
  })
}

/**
 * 跳转注销账号
 */
const cancellation = () => {
  util.checkAuthInfo(() => {
    uni.navigateTo({
      url: '/package-user/pages/cancellation/cancellation'
    })
  })
}

/**
 * 我的地址
 */
const toAddressList = () => {
  util.checkAuthInfo(() => {
    uni.navigateTo({
      url: '/package-user/pages/delivery-address/delivery-address'
    })
  })
}

/**
 * 去条款页
 */
const toTermsOfService = (key) => {
  uni.navigateTo({
    url: '/package-user/pages/terms-page/terms-page?sts=' + key
  })
}

</script>

<style lang="scss" scoped>
@use "account-settings";
</style>

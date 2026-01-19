<template>
  <view class="page-personal-recommend">
    <view class="recommend-con">
      <view class="recommend-set">
        <view class="recommend-til">
          {{ $t('personRec') }}
        </view>
        <switch
          class="switch-btn"
          style="transform:scale(0.7)"
          :checked="isOpen===1"
          color="#F81A1A"
          @change="recomChange"
        />
      </view>
      <view class="recommend-tips">
        {{ $t('personRecTips') }}
      </view>
    </view>
    <view class="confirm-box">
      <view
        class="confirm-btn"
        @tap="confirm"
      >
        {{ $t('confirmChanges') }}
      </view>
    </view>
  </view>
</template>

<script setup>
import util from '@/utils/util'

const isOpen = ref(0)

onLoad(() => {
  uni.setNavigationBarTitle({
    title: $t('personRec')
  })
  queryUserInfo()
})

// 获取用户信息
const queryUserInfo = () => {
  const params = {
    url: '/p/user/userInfo',
    method: 'GET',
    data: {}
  }
  http.request(params).then(({ data }) => {
    isOpen.value = data.prodRecommendation
  })
}

const confirm = util.debounce(() => {
  const params = {
    url: '/p/user/setProdRecommendation',
    method: 'PUT',
    data: {
      prodRecommendation: isOpen.value
    }
  }
  http.request(params).then(() => {
    uni.showToast({
      title: $t('successfullyModified'),
      icon: 'none',
      duration: 1000,
      mask: true
    })
    setTimeout(() => {
      http.request({
        url: '/p/user/userInfo',
        method: 'GET',
        data: {},
        dontTrunLogin: true
      }).then(({ data: res }) => {
        uni.setStorageSync('cbecB2cUserInfo', res)
        getApp().globalData.userPagesGetHotSalesProds && getApp().globalData.userPagesGetHotSalesProds()
        uni.navigateBack(1)
      })
    }, 1000)
  })
})

const recomChange = (e) => {
  isOpen.value = e.detail.value ? 1 : 0
}

</script>

<style lang="scss" scoped>
@use "personal-recommend";
</style>

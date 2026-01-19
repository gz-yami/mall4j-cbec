<!--
  Copyright (c) 2018-2999 广州市蓝海创新科技有限公司 All rights reserved.

  https://www.mall4j.com/

  未经允许，不可做商业用途！

  版权所有，侵权必究！
-->

<template>
  <!-- 隐私/条款弹窗 -->
  <view
    class="Mall4j component-privacy-pop confirm-pop"
    :class="{ show: showPop }"
  >
    <view class="confirm-pop-mask" />
    <view class="confirm-pop-main">
      <view class="pop-head">
        {{
          showPop === 1 ? $t('termsServiceAndPrivacyPolicy') : $t('ConfirmationTip')
        }}
      </view>
      <view class="pop-content">
        <view
          v-if="showPop === 1"
          class="pop-content-wrap"
        >
          {{ $t('content1') }}
        </view>
        <view
          v-if="showPop === 1"
          class="pop-content-wrap"
        >
          {{ $t('uCanRead')
          }}<text
            class="link-a"
            @tap="toTermsOfService('serviceTerms')"
          >
            《{{ $t('termsOfService') }}》
          </text>{{ $t('and')
          }}<text
            class="link-a"
            @tap="toTermsOfService('servicePolicy')"
          >
            《{{ $t('privacyPolicy') }}》
          </text>{{ $t('content2') }}
        </view>
        <!-- 二次确认弹窗 -->
        <!-- <view v-else class="pop-content-wrap">
          进入应用前，你需先同意<text class="link-a" @tap="toTermsOfService('serviceTerms')">《服务条款》</text>和<text class="link-a" @tap="toTermsOfService('servicePolicy')">《隐私策略》</text>，否则将退出应用。
        </view> -->
      </view>
      <view class="pop-btns">
        <text
          class="pop-btn cancel-btn"
          @tap="popCancel"
        >
          {{
            showPop === 1 ? $t('disagree') : $t('exitTheApplication')
          }}
        </text>
        <text
          class="pop-btn confirm-btn"
          @tap="popConfirm"
        >
          {{ $t('agreeAndAccept') }}
        </text>
      </view>
    </view>
  </view>
</template>

<script setup>

const emit = defineEmits(['confirm', 'hidePop'])

const showPop = ref(1)

/**
 * 弹窗确认事件
 */
const popConfirm = () => {
  uni.setStorageSync('cbecB2cIsPrivacy', 1)
  showPop.value = 0
  uni.showTabBar()
  emit('confirm', 1)
}

/**
 * 弹窗取消/否认事件
 */
const popCancel = () => {
  uni.setStorageSync('cbecB2cIsPrivacy', -1)
  showPop.value = 0
  uni.showTabBar()
  emit('hidePop')
}

/**
 * 去条款页
 */
const toTermsOfService = (key) => {
  uni.navigateTo({
    url: '/package-user/pages/terms-page/terms-page?sts=' + key
  })
}

/**
 * 关闭应用
 */
const closeApp = () => {
  const platform = uni.getSystemInfoSync().platform
  if (platform === 'ios') {
    plus.ios
      .import('UIApplication')
      .sharedApplication()
      .performSelector('ios')
  } else if (platform === 'android') {
    plus.runtime.quit()
  }
}
defineExpose({
  closeApp
})

</script>

<style lang="scss" scoped>
/* 弹窗样式 */
.confirm-pop {
  position: fixed;
  top: 0;
  left: 0;
  height: 100%;
  width: 100%;
  z-index: -1;
  &.show {
    z-index: 999;
    .confirm-pop-mask {
      height: 100%;
    }
    .confirm-pop-main {
      opacity: 1;
      transform: translate(-50%, -50%) scale(1);
    }
  }
  .confirm-pop-mask {
    height: 0;
    width: 100%;
    background: rgba(0, 0, 0, 0.4);
  }
  .confirm-pop-main {
    width: 80%;
    background-color: #fff;
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%) scale(0);
    transition: all 0.2s;
    opacity: 0;
    padding: 50rpx 30rpx 40rpx;
    text-align: center;
    border-radius: 20rpx;
    font-size: 28rpx;
    .pop-head {
      font-weight: bold;
      font-size: 28rpx;
    }
    .pop-content {
      word-break: break-word;
      margin: 30rpx 0 40rpx;
      .pop-content-wrap {
        .link-a {
          color: #fc1b35;
          text-decoration: underline;
        }
      }
    }
    .pop-btns {
      display: flex;
      text-align: center;
      .pop-btn {
        display: flex;
        align-items: center;
        flex: 1;
        padding: 10rpx 70rpx;
        text-align: center;
        color: #fc1b35;
        border-radius: 56px;
        border: 2rpx solid #fc1b35;
        box-sizing: border-box;
      }
      .confirm-btn {
        background-color: #fc1b35;
        color: #fff;
        margin-left: 30rpx;
      }
    }
  }
}
</style>

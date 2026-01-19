<template>
  <view class="Mall4j component-pop-toast">
    <view class="toast-pop-main">
      <view class="pop-icon">
        <view class="g-1" />
        <view class="g-2" />
      </view>
      <view class="pop-content">
        {{ $t('deleted') }}
      </view>
    </view>
  </view>
</template>

<script setup>
defineProps({
  showToast: {
    type: Boolean,
    default: false
  }
})
</script>

<style lang="scss" scoped>
.component-pop-toast {
  position: fixed;
  top: 0;
  left: 0;
  height: 100%;
  width: 100%;
  z-index: 999;

}

.toast-pop-main {
  width: 160rpx;
  height: 160rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  background: rgba(0, 0, 0, .5);
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  transition: all .2s;
  opacity: 1;
  border-radius: 12rpx;
}

.pop-icon {
  width: 100%;
  position: relative;

  .g-1 {
    width: 24rpx;
    height: 6rpx;
    background-color: #fff;
    transform: rotate(50deg);
    position: absolute;
    left: 34%;
    top: -10rpx;
  }

  .g-2 {
    width: 45rpx;
    height: 6rpx;
    background-color: #fff;
    transform: rotate(-55deg);
    position: absolute;
    left: 40%;
    top: -20rpx;
  }
}

.pop-content {
  font-size: 24rpx;
  color: #fff;
  position: absolute;
  bottom: 30rpx;
}

</style>

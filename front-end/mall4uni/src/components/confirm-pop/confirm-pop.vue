<template>
  <view
    class="Mall4j component-confirm-pop"
    :class="{show: isShowPop}"
  >
    <view
      class="confirm-pop-mask"
      @tap="cancel"
    />
    <view class="confirm-pop-main">
      <view :class="['pop-head',{hide:!showTitle}]">
        {{ popContent.title || $t('tips') }}
      </view>
      <view class="pop-content">
        {{ popContent.content }}
      </view>
      <view class="pop-btns">
        <text
          class="pop-btn cancel-btn"
          :class="{hide:!showCancel}"
          @tap="cancel"
        >
          {{ popContent.cancelText || $t('cancel') }}
        </text>
        <text
          class="pop-btn confirm-btn"
          :class="{noCancle:!showCancel}"
          @tap="confirm"
        >
          {{ popContent.confirmText || $t('confirm') }}
        </text>
      </view>
    </view>
  </view>
</template>

<script setup>
const emit = defineEmits(['update:showPop', 'cancel', 'confirm'])
const props = defineProps({
  showPop: {
    type: Boolean,
    default: false
  },
  popContent: {
    type: Object,
    default: () => {}
  },
  showTitle: {
    type: Boolean,
    default: false
  },
  isCancel: {
    type: Boolean,
    default: true
  },
  showCancel: {
    type: Boolean,
    default: true
  }
})

const isShowPop = computed({
  get () {
    return props.showPop
  },
  set (val) {
    emit('update:showPop', val)
  }
})

const cancel = () => {
  if (props.isCancel) {
    isShowPop.value = false
    emit('cancel')
  }
}
const confirm = () => {
  emit('confirm')
}
</script>

<style scoped lang="scss">
.component-confirm-pop {
  position: fixed;
  top: 0;
  left: 0;
  height: 100%;
  width: 100%;
  z-index: -1;
}
.component-confirm-pop.show {
  z-index: 999;
}
.confirm-pop-mask {
  height: 0;
  width: 100%;
  background: rgba(0, 0, 0, .4);
}
.show {
  .confirm-pop-mask {
    height: 100%;
  }
  .confirm-pop-main {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1);
  }
}
.confirm-pop-main {
  width: 596rpx;
  background-color: #fff;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) scale(0);
  transition: all .2s;
  opacity: 0;
  padding: 32rpx 62rpx;
  text-align: center;
  border-radius: 30rpx;
  box-sizing: border-box;
}
.pop-head {
  font-weight: bold;
  font-size: 28rpx;
}
.pop-content {
  word-break: break-all;
  margin-top: 40rpx;
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
}
.pop-btns {
  display: flex;
  text-align: center;
  margin-top: 60rpx;
}
.pop-btn {
  flex: 1;
  width: 220rpx;
  line-height: 62rpx;
  text-align: center;
  color: #F81A1A;
  border-radius: 56px;
  border: 1rpx solid #F81A1A;
  font-size: 28rpx;
}
.pop-btn.cancel-btn {
  font-weight: 400;
  color: #333333;
  border: 1rpx solid #ADADAD;
}
.confirm-btn {
  background-color: #F81A1A;
  color: #fff;
  margin-left: 30rpx;
}
.hide {
  display: none;
}
.pop-btn.noCancle {
  width: 80%;
  margin: 0;
}
</style>

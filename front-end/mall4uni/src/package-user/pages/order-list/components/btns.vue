<template>
  <view class="Mall4j component-btns">
    <view class="btns">
      <view class="open-btns">
        <view v-if="open">
          <view
            class="mask"
            @tap.stop="closeTap"
          />
          <view class="btn-list">
            <view
              v-for="(val,idx) in btnList"
              :key="idx"
              class="btns-botton"
              @tap.stop="butTap(idx)"
            >
              {{ val.text }}
            </view>
          </view>
        </view>
        <view
          class="opnBtn"
          @tap.stop="openTap"
        >
          <slot />
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>

defineProps({
  btnList: {
    default: function () {
      return []
    },
    type: Array
  }
})

const emit = defineEmits(['select'])

const open = ref(false)

const openTap = () => {
  open.value = true
}

const closeTap = () => {
  open.value = false
}

const butTap = (index) => {
  emit('select', index)
}

</script>

<style lang="scss" scoped>
.component-btns {
  .btns{
    position: relative;
    .open-btns{
      .mask{
        position: fixed;
        top:0;
        left:0;
        height: 100%;
        width: 100%;
        z-index:2;
        opacity: 0;
      }
      .btn-list{
        position: absolute;
        left:0;
        bottom: -23rpx;
        background-color: white;
        border-radius: 10rpx;
        padding: 14rpx 15rpx ;
        z-index:3;
        box-shadow: 0rpx 0rpx 8rpx 0rpx rgba(49,57,77,0.1);
        transform: translateY(100%);
        .btns-botton{
          text-align: center;
          height: 28rpx;
          font-size: 28rpx;
          font-family: PingFang SC-Regular, PingFang SC;
          font-weight: 400;
          color: #333333;
          white-space: nowrap;
        }
        .btns-botton+.btns-botton{
          border-top:2rpx solid #ADADAD;
          margin-top: 10rpx;
          padding-top: 10rpx;
        }
        &::before{
          content:'';
          width: 24rpx;
          height: 12rpx;
          display:block;
          position: absolute;
          left: 12rpx;
          top: 2rpx;
          background-image: url('../../../static/images/icon/order-list/btns_arrow_top.png');
          background-size: 100% 100%;
          transform: translateY(-100%);
        }
      }
    }
  }
}
</style>

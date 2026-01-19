<template>
  <view
    class="zb-tooltip"
    :style="{
      '--theme-bg-color':color
    }"
  >
    <view
      class="zb_tooltip_content"
      @click.stop="handleClick"
    >
      <slot />
      <view
        class="zb_tooltip__popper"
        :style="[style,{
          visibility:isShow?'visible':'hidden',
          color:color==='white'?'':'#fff',
          minWidth: width + 'rpx',
          boxShadow: color==='white'?'0 3px 6px -4px #0000001f, 0 6px 16px #00000014, 0 9px 28px 8px #0000000d':''
        }]"
        @click.stop="handleClick"
      >
        <slot name="content">
          {{ content }}
        </slot>
        <view
          class="zb_popper__icon"
          :style="[arrowStyle]"
          :class="[{
            'zb_popper__up':placement.indexOf('bottom')===0,
            'zb_popper__arrow':placement.indexOf('top')===0,
            'zb_popper__right':placement.indexOf('right')===0,
            'zb_popper__left':placement.indexOf('left')===0,
          }]"
        />
      </view>
    </view>
  </view>
</template>

<script setup>
const props = defineProps({
  // 是否显示 tooltip，支持 .sync 修饰符
  visible: {
    type: Boolean,
    default: false
  },
  // 对话框背景
  color: {
    type: String,
    default: '#303133'
  },
  // 显示方向
  // 可选值：top/top-start/top-end/bottom/bottom-start/bottom-end/left/left-start/left-end/right/right-start/right-end
  placement: {
    type: String,
    default: 'top'
  },
  // 显示的内容，也可以通过 slot#content
  content: {
    type: String,
    default: ''
  },
  width: {
    type: String,
    default: '200'
  }
})
const emit = defineEmits(['update:visible'])

const isShow = ref(props.visible)
const style = ref(null)
const arrowStyle = ref(null)

watch(() => isShow.value, (val) => {
  emit('update:visible', val)
}, {
  immediate: true
})

watch(() => props.visible, (val) => {
  if (val) {
    nextTick(() => {
      getPosition()
    })
  }
  isShow.value = val
})

onMounted(() => {
  // #ifdef H5
  window.addEventListener('click', () => {
    isShow.value = false
  })
  // #endif
  getPosition()
})

const handleClick = async () => {
  if (isShow.value) {
    isShow.value = false
    return
  }
  await getPosition()
  // eslint-disable-next-line require-atomic-updates
  isShow.value = true
}

const getPosition = () => {
  return new Promise((resolve) => {
    uni.createSelectorQuery().in(this).selectAll('.zb_tooltip_content,.zb_tooltip__popper').boundingClientRect(async (data) => {
      const { width, height } = data[0]
      const obj1 = data[1]
      const objStyle = {}
      const objStyle1 = {}
      switch (props.placement) {
        case 'top':
          if (obj1.width > width) {
            objStyle.left = `-${(obj1.width - width) / 2}px`
          } else {
            objStyle.left = `${Math.abs(obj1.width - width) / 2}px`
          }

          objStyle.bottom = `${height + 8}px`
          objStyle1.left = (obj1.width / 2 - 6) + 'px'
          break
        case 'top-start':
          objStyle.left = '0px'
          objStyle.bottom = `${height + 8}px`
          break
        case 'top-end':
          objStyle.right = '0px'
          objStyle.bottom = `${height + 8}px`
          objStyle1.right = '8px'
          break
        case 'bottom':
          if (obj1.width > width) {
            objStyle.left = `-${(obj1.width - width) / 2}px`
          } else {
            objStyle.left = `${Math.abs(obj1.width - width) / 2}px`
          }
          objStyle.top = `${height + 8}px`
          objStyle1.left = (obj1.width / 2 - 6) + 'px'
          break
        case 'bottom-start':
          objStyle.left = '0px'
          objStyle.top = `${height + 8}px`
          objStyle1.left = '8px'
          break

        case 'bottom-end':
          objStyle.right = '0px'
          objStyle.top = `${height + 8}px`
          objStyle1.right = '8px'
          break

        case 'right':
          objStyle.left = `${width + 8}px`
          if (obj1.height > height) {
            objStyle.top = `-${(obj1.height - height) / 2}px`
          } else {
            objStyle.top = `${Math.abs((obj1.height - height) / 2)}px`
          }

          objStyle1.top = `${obj1.height / 2 - 6}px`
          break
        case 'right-start':
          objStyle.left = `${width + 8}px`
          objStyle.top = '0px'
          objStyle1.top = '8px'
          break

        case 'right-end':
          objStyle.left = `${width + 8}px`
          objStyle.bottom = '0px'
          objStyle1.bottom = '8px'
          break

        case 'left':
          objStyle.right = `${width + 8}px`

          if (obj1.height > height) {
            objStyle.top = `-${(obj1.height - height) / 2}px`
          } else {
            objStyle.top = `${Math.abs((obj1.height - height) / 2)}px`
          }

          objStyle1.top = `${obj1.height / 2 - 6}px`
          break

        case 'left-start':
          objStyle.right = `${width + 8}px`
          objStyle.top = '0px'
          objStyle1.top = '8px'
          break

        case 'left-end':
          objStyle.right = `${width + 8}px`
          objStyle.bottom = '0px'
          objStyle1.bottom = '8px'
          break
      }
      style.value = objStyle
      // 三角形箭头
      arrowStyle.value = objStyle1
      resolve()
    }).exec()
  })
}

</script>

<style lang="scss" scoped>
$theme-bg-color: var(--theme-bg-color);

.zb-tooltip{
  display: inline-block;
  position: relative;

}
.zb_tooltip_content{
  height: 100%;
  position: relative;
  display: inline-block;
}
.zb_tooltip__popper{
  background: $theme-bg-color;

  visibility: hidden;
  position: absolute;
  border-radius: 10rpx;
  font-size: 24rpx;
  padding: 10rpx 8rpx 10rpx 20rpx;
  text-align: left;
  word-wrap: break-word;
  display: inline-block;
  z-index:9;
}
.zb_popper__icon{
  width: 0;
  height: 0;
  z-index:9;
  position: absolute;
}
.zb_popper__arrow{
  bottom: -5px;
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
  border-top: 6px solid $theme-bg-color;

}
.zb_popper__right{
  border-top: 6px solid transparent;
  border-bottom: 6px solid transparent;
  border-right: 6px solid $theme-bg-color;
  left:-5px;
}

.zb_popper__left{
  border-top: 6px solid transparent;
  border-bottom: 6px solid transparent;
  border-left: 6px solid $theme-bg-color;
  right:-5px;
}

.zb_popper__up{
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
  border-bottom: 6px solid $theme-bg-color;
  top:-5px;
}
</style>

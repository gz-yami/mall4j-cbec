<template>
  <img
    v-if="imgPath"
    :src="checkFileUrl(imgPath)"
    :class="classList"
    :alt="imgAlt"
    :style="wrapStyle"
    @error="onhandleError"
    @click="onHandleClick"
  >
  <img
    v-else
    src="~@/assets/img/def.png"
    :class="classList"
    :style="wrapStyle"
    @click="onHandleClick"
  >
</template>

<script setup>

const props = defineProps({
  src: {
    type: String,
    default: ''
  },
  width: {
    type: [String, Number],
    default: '100%'
  },
  height: {
    type: [String, Number],
    default: '100%'
  },
  radius: {
    type: [String, Number],
    default: 0
  },
  imgAlt: {
    type: String,
    default: ''
  },
  classList: {
    type: Array,
    default: () => {
      return []
    }
  },
  imgStyle: {
    type: Object,
    default: () => {
      return {}
    }
  }
})
const emit = defineEmits(['handleClick'])
// 图片地址
const imgPath = ref('')

watch(
  () => props.src,
  (path) => {
    imgPath.value = path
  },
  {
    immediate: true
  }
)

// 验证十进制数字
function number (value) {
  return /^[+-]?(\d+\.?\d*|\.\d+|\d\.\d+e\+\d+)$/.test(value)
}
// 是否携带单位
function addUnit (value = 'auto') {
  value = String(value)
  return number(value) ? `${value}px` : value
}
// 样式整合
const wrapStyle = computed(() => {
  const style = {}
  style.width = addUnit(props.width)
  style.height = addUnit(props.height)
  // 如果是显示圆形，设置一个很多的半径值即可
  style.borderRadius = addUnit(props.radius)
  // 如果设置圆角，必须要有hidden，否则可能圆角无效
  return { ...style, ...props.imgStyle }
})

function onHandleClick () {
  emit('handleClick')
}
function onhandleError () {
  imgPath.value = ''
}
</script>

<style lang="scss" scoped>
img {
  width: 100%;
  height: 100%;
  overflow: hidden;
}
</style>

<template>
  <image
    v-if="!isError && imgPath"
    :src="imgPath"
    :style="imgStyle"
    :class="classList"
    :mode="imgMode"
    @error="imgError"
    @load="imgLoad"
    @tap="handleTap"
  />
  <image
    v-else
    :src="defImgPath"
    :style="imgStyle"
    :class="classList"
    :mode="imgMode"
    @tap="handleTap"
  />
</template>

<script setup>
import util from '@/utils/util.js'
const props = defineProps({
  src: {
    type: String,
    default: ''
  },
  imgMode: {
    type: String,
    default: 'aspectFill'
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
  },
  defImgInx: {
    type: Number,
    default: 0
  }
})

const imgPath = computed(() => {
  return util.checkFileUrl(props.src)
})

const defImgPath = computed(() => {
  switch (props.defImgInx) {
    case 0:
      return '/static/images/icon/def.png'
    case 1:
      return '/static/images/icon/head04.png'
    default:
      return '/static/images/icon/def.png'
  }
})

const emit = defineEmits(['imgError', 'imgLoad', 'handleTap'])

const isError = ref(false)
const imgError = () => {
  isError.value = true
  emit('imgError')
}

const imgLoad = (e) => {
  emit('imgLoad', e)
}

const handleTap = () => {
  emit('handleTap')
}
</script>

<style scoped>
image {
  will-change: transform;
  width: 100%;
  height: 100%;
}
</style>

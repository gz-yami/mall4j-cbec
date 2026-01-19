<template>
  <div class="component-prod-pic">
    <div v-if="height && width">
      <img
        v-if="picNotNull(pic)"
        :src="checkFileUrl(pic)"
        :style="{ 'width': width, 'height': height }"
        :height="height"
        :width="width"
        @error="handlePicError"
      >
      <img
        v-else
        src="~@/assets/img/def.png"
        :style="{ 'width': width, 'height': height }"
        :height="height"
        :width="width"
      >
    </div>
    <div v-else-if="width">
      <img
        v-if="picNotNull(pic)"
        :src="checkFileUrl(pic)"
        :width="width"
        @error="handlePicError"
      >
      <img
        v-else
        src="~@/assets/img/def.png"
        :width="width"
      >
    </div>
    <div v-else-if="height">
      <img
        v-if="picNotNull(pic)"
        :src="checkFileUrl(pic)"
        :height="height"
        @error="handlePicError"
      >
      <img
        v-else
        src="~@/assets/img/def.png"
        :height="height"
      >
    </div>
    <div v-else>
      <img
        v-if="picNotNull(pic)"
        :src="checkFileUrl(pic)"
        @error="handlePicError"
      >
      <img
        v-else
        src="~@/assets/img/def.png"
      >
    </div>
  </div>
</template>

<script setup>
import defImg from '@/assets/img/def.png'

defineProps({
  pic: {
    default: null,
    type: String
  },
  height: {
    default: null,
    type: String
  },
  width: {
    default: null,
    type: String
  }
})

const picNotNull = (pic) => {
  return !!pic
}

const handlePicError = (e) => {
  e.target.src = defImg
}
</script>

<style lang="scss" scoped></style>

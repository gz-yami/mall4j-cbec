<template>
  <div>
    <!-- 预览 -->
    <img
      v-if="isPreview"
      class="preview-img"
      :style="customStyle"
      :src="checkFileUrl(modelValue)"
      @click="previewImg"
    >
    <el-upload
      v-else
      class="pic-uploader-component"
      :action="uploadAction"
      :headers="uploadHeaders"
      list-type="picture-card"
      :style="customStyle"
      accept=".png,.jpg,.jpeg,.gif"
      :show-file-list="false"
      :on-success="handleUploadSuccess"
      :before-upload="beforeAvatarUpload"
    >
      <img
        v-if="modelValue"
        alt=""
        :src="checkFileUrl(modelValue)"
        class="pic"
      >
      <el-icon
        v-else
        color="#8c939d"
        size="28"
      >
        <Plus />
      </el-icon>
    </el-upload>

    <el-dialog
      v-model="visible"
      :append-to-body="visible"
      :title="$t('prodList.lookOver')"
    >
      <img-show :src="modelValue" />
    </el-dialog>
  </div>
</template>

<script setup>
import { checkFileUrl } from '@/utils'
import $cookie from 'vue-cookies'
import { ElMessage } from 'element-plus'
const uploadHeaders = { Authorization: $cookie.get('cbecB2cAuthorization_vp') }
const uploadAction = http.adornUrl('/admin/file/upload/element')
const emit = defineEmits(['update:modelValue'])
// eslint-disable-next-line no-unused-vars
const props = defineProps({
  modelValue: {
    default: '',
    type: String
  },
  customStyle: {
    default: () => {
      return {
        height: '100px',
        width: '100px'
      }
    },
    type: Object
  },
  isPreview: {
    default: false,
    type: Boolean
  }
})
/**
 * 图片上传
 */
// eslint-disable-next-line no-unused-vars
const handleUploadSuccess = (response, file) => {
  emit('update:modelValue', file.response.data)
}

/**
 * 限制图片上传大小
 */
const beforeAvatarUpload = (file) => {
  const isLt2M = file.size / 1024 / 1024 < 2
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/gif' || file.type === 'image/jpg'
  if (!isJPG) {
    ElMessage.error('上传图片只能是jpeg/jpg/png/gif 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!')
  }
  return isLt2M && isJPG
}

const visible = ref(false)
/**
 * 图片预览
 */
const previewImg = () => {
  visible.value = true
}

</script>

<style lang="scss" scoped>
.preview-img {
  border: 1px #d9d9d9 dashed;
  border-radius: 6px;
}
.pic-uploader-component :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100%;
  height: 100%;
  .pic-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 100%;
    height: 100%;
    line-height: 100%;
    text-align: center;
  }
  .pic {
    width: 100%;
    height: 100%;
    display: block;
  }
}
.pic-uploader-component :deep(.el-upload:hover) {
  border-color: #409EFF;
}
:deep(.el-upload) {
  width: 148px;
  height: 148px;
}
</style>

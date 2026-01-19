<template>
  <!-- 发货信息，用于导出代发货订单的excel交给快递公司 -->
  <el-dialog
    v-model="visible"
    :modal="false"
    :title="$t('order.uploadTips1')"
    :close-on-click-modal="false"
    width="38%"
    class="box"
  >
    <div class="tips">
      <p>
        {{ $t('order.uploadTips0') }}
      </p>
      <p>
        {{ $t('order.attention') }}：
      </p>
      <p>
        {{ $t('order.uploadTips4') }}
      </p>
      <p>
        {{ $t('order.uploadTips5') }}
      </p>
    </div>
    <el-upload
      ref="uploadRef"
      v-loading="loading"
      :auto-upload="false"
      :v-loading="loading"
      :before-upload="beforeAvatarUpload"
      :file-list="files"
      :limit="1"
      :on-error="uploadFalse"
      :on-preview="handlePreview"
      :on-remove="handleRemove"
      :on-success="uploadSuccess"
      :action="http.adornUrl('/order/order/exportOrderExcel')"
      :headers="{Authorization: cookie.get('cbecB2cAuthorization_vp'),locale:lang}"
      class="upload-demo"
      name="orderExcelFile"
    >
      <template #tip>
        <div class="el-upload__tip" />
      </template>
      <div
        style="margin-left: 20px"
        class="default-btn"
        @click="submitUpload"
      >
        {{ $t("order.ImportingFiles") }}
      </div>
      <div
        class="default-btn"
        style="margin-left: 20px"
        @click="exportOrder"
      >
        {{ $t("order.exportOrder") }}
      </div>
      <template #trigger>
        <div class="default-btn primary-btn">
          {{
            $t("order.SelectFile")
          }}
        </div>
      </template>
    </el-upload>
  </el-dialog>
</template>
<script setup>
import http from '@/utils/http.js'
import cookie from 'vue-cookies'
import { ElMessage, ElMessageBox } from 'element-plus'
const emit = defineEmits(['refreshDataList1', 'refreshDataList1'])
const lang = reactive(localStorage.getItem('cbecB2cLang') || 'en')
const props = defineProps({
  param: {
    type: Object,
    default: () => {
      return {}
    }
  }
})
const visible = ref(false)
const loading = ref(false)
const upload = ref(false)
const files = ref([])
const exportOrder = () => {
  if (!props.param.startTime || !props.param.endTime) {
    ElMessage.error($t('order.pleExpOrderFirst'))
    return
  }
  ElMessageBox.confirm($t('order.sureToExport'), $t('text.tips'), {
    confirmButtonText: $t('order.confirm'),
    cancelButtonText: $t('order.cancel'),
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl('/order/order/unDeliveryOrderExcel'),
      methods: 'get',
      params: http.adornParams({
        startTime: props.param.startTime, // 开始时间
        endTime: props.param.endTime // 结束时间
      }),
      responseType: 'blob' // 解决文件下载乱码问题
    }).then(({ data }) => {
      const blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8' })
      const fileName = $t('order.PendingOrderInformation') + '.xlsx'
      const elink = document.createElement('a')
      if ('download' in elink) { // 非IE下载
        elink.download = fileName
        elink.style.display = 'none'
        elink.href = URL.createObjectURL(blob)
        document.body.appendChild(elink)
        elink.click()
        URL.revokeObjectURL(elink.href) // 释放URL 对象
        document.body.removeChild(elink)
      } else { // IE10+下载
        navigator.msSaveBlob(blob, fileName)
      }
    }).catch((e) => {
      ElMessage.error(e)
    })
  })
}
const uploadSuccess = (response) => {
  loading.value = false
  alert(response.data || $t('order.fileSuccess'))
  files.value = []
  visible.value = false
  emit('refreshDataList1')
}
const uploadFalse = () => {
  loading.value = false
  alert($t('order.fileUploadFail'))
}
const init = () => {
  visible.value = true
  loading.value = false
  files.value = []
}
defineExpose({ init })

/**
 * 上传前对文件的大小的判断
 * @param file
 * @returns {boolean}
 */
const beforeAvatarUpload = (file) => {
  upload.value = true
  const extension = file.name.split('.')[1] === 'xls'
  const extension2 = file.name.split('.')[1] === 'xlsx'
  const isLt2M = file.size / 1024 / 1024 < 10
  if (!extension && !extension2) {
    alert($t('order.downloadTemplateTips1'))
    return false
  }
  if (!isLt2M) {
    alert($t('order.downloadTemplateTips2'))
    return false
  }
  loading.value = true
  return extension || (extension2 && isLt2M)
}
const uploadRef = ref(null)
const submitUpload = () => {
  upload.value = false
  uploadRef.value?.submit()
  if (!upload.value) {
    ElMessage.error($t('shop.fileNullTip'))
  }
}
const handlePreview = (file) => {
  if (file.response.status) {
    alert($t('order.fileSuccess'))
  } else {
    alert($t('order.fileFail'))
  }
  visible.value = false
  emit('refreshDataList1')
}
</script>

<style lang="scss" scoped>
:deep(.el-dialog__body) {
  padding: 10px 20px 30px 20px;
}
.tips {
  margin-bottom: 30px;
}
</style>

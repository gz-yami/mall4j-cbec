<template>
  <el-dialog
    v-model="visible"
    :title="!dataForm.dvyId ? $t('brand.add') : $t('sysManagement.modify')"
    :close-on-click-modal="false"
    width="610px"
  >
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      :rules="dataRule"
      label-width="auto"
      @submit.prevent
      @keyup.enter="onSubmit()"
    >
      <el-form-item
        :label="$t('platform.logisticsCompany')"
        prop="dvyName"
      >
        <el-input
          v-model="dataForm.dvyName"
          maxlength="50"
          show-word-limit
          @blur="dataForm.dvyName = handleInputSpaces(dataForm.dvyName)"
        />
      </el-form-item>
      <el-form-item
        :label="$t('platform.companyHomepage')"
        prop="companyHomeUrl"
      >
        <el-input
          v-model="dataForm.companyHomeUrl"
          maxlength="255"
          @blur="dataForm.companyHomeUrl = handleInputSpaces(dataForm.companyHomeUrl)"
        />
      </el-form-item>
      <el-form-item
        :label="$t('platform.companyNumber') + '(' + $t('sysManagement.expressBird') + ')'"
        prop="dvyNo"
      >
        <el-input
          v-model="dataForm.dvyNo"
          maxlength="20"
          @blur="dataForm.dvyNo = handleInputSpaces(dataForm.dvyNo)"
        />
      </el-form-item>
      <el-form-item
        :label="$t('platform.companyNumber') + '(' + $t('sysManagement.quick100') + ')'"
        prop="dvyNoHd"
      >
        <el-input
          v-model="dataForm.dvyNoHd"
          maxlength="20"
          @blur="dataForm.dvyNoHd = handleInputSpaces(dataForm.dvyNoHd)"
        />
      </el-form-item>
      <el-form-item
        :label="$t('platform.companyNumber') + '(' + $t('sysManagement.ali') + ')'"
        prop="aliNo"
      >
        <el-input
          v-model="dataForm.aliNo"
          maxlength="20"
          @blur="dataForm.aliNo = handleInputSpaces(dataForm.aliNo)"
        />
      </el-form-item>
      <el-form-item
        :label="$t('platform.logisticsQueryInterface')"
        prop="queryUrl"
      >
        <el-input
          v-model="dataForm.queryUrl"
          maxlength="100"
          @blur="dataForm.queryUrl = handleInputSpaces(dataForm.queryUrl)"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <div
          class="default-btn"
          @click="visible = false"
        >
          {{ $t('remindPop.cancel') }}
        </div>
        <div
          class="default-btn primary-btn"
          @click="onSubmit()"
        >
          {{ $t('remindPop.confirm') }}
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ElMessage } from 'element-plus'

const emit = defineEmits(['refreshDataList'])

const dataForm = ref({
  dvyId: null,
  dvyName: null,
  companyHomeUrl: null,
  recTime: null,
  modifyTime: null,
  dvyNo: null,
  dvyNoHd: null,
  aliNo: null,
  queryUrl: null
})
const dataRule = {
  dvyName: [
    { required: true, message: $t('platform.errorTip4'), trigger: 'blur' }
  ],
  dvyNo: [
    { required: true, message: $t('platform.errorTip6'), trigger: 'blur' }
  ],
  dvyNoHd: [
    { required: true, message: $t('platform.errorTip6'), trigger: 'blur' }
  ],
  aliNo: [
    { required: true, message: $t('platform.errorTip6'), trigger: 'blur' }
  ]
}

const visible = ref(false)
let isSubmit = false
const dataFormRef = ref(null)
const init = (dvyId) => {
  dataForm.value.dvyId = dvyId || 0
  dataForm.value.queryUrl = ''
  visible.value = true
  isSubmit = false
  nextTick(() => {
    dataFormRef.value?.resetFields()
    if (dataForm.value.dvyId) {
      http({
        url: http.adornUrl('/platform/delivery/info/' + dataForm.value.dvyId),
        method: 'get',
        params: http.adornParams()
      }).then(({ data }) => {
        dataForm.value = data
      })
    }
  })
}

/**
 * 输入框纯空格处理
 */
const handleInputSpaces = (value) => {
  if (!value) {
    return
  }
  if (!value.trim()) {
    return value.trim()
  } else {
    return value
  }
}

// 表单提交
const onSubmit = () => {
  dataFormRef.value?.validate((valid) => {
    if (valid) {
      if (isSubmit) {
        return false
      }
      isSubmit = true
      http({
        url: http.adornUrl('/platform/delivery'),
        method: dataForm.value.dvyId ? 'put' : 'post',
        data: http.adornData(dataForm.value)
      }).then(() => {
        ElMessage({
          message: $t('remindPop.success'),
          type: 'success',
          duration: 1500,
          onClose: () => {
            visible.value = false
            emit('refreshDataList')
          }
        })
      }).catch(() => {
        isSubmit = false
      })
    }
  })
}

defineExpose({
  init
})

</script>

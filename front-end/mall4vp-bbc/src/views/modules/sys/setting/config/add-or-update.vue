<template>
  <el-dialog
    v-model="visible"
    :title="!dataForm.id ? $t('sysManagement.add') : $t('sysManagement.modify')"
    :close-on-click-modal="false"
  >
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      :rules="dataRule"
      :label-width="$t('language')!=='zh_CN'?'160px': 'auto'"
      @submit.prevent
      @keyup.enter="onSubmit()"
    >
      <el-form-item
        :label="$t('sys.parameteName')"
        prop="paramKey"
      >
        <el-input
          v-model="dataForm.paramKey"
          :placeholder="$t('sys.parameteName')"
          maxlength="48"
          show-word-limit
        />
      </el-form-item>
      <el-form-item
        :label="$t('sys.parameterValue')"
        prop="paramValue"
      >
        <el-input
          v-model="dataForm.paramValue"
          :placeholder="$t('sys.parameterValue')"
        />
      </el-form-item>
      <el-form-item
        :label="$t('publics.remark')"
        prop="remark"
      >
        <el-input
          v-model="dataForm.remark"
          :placeholder="$t('publics.remark')"
          maxlength="480"
          show-word-limit
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <div
          class="default-btn"
          @click="visible = false"
        >
          {{ $t('coupon.cancel') }}
        </div>
        <div
          class="default-btn primary-btn"
          @click="onSubmit(dataFormRef)"
        >
          {{ $t('coupon.confirm') }}
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>

import { ElMessage } from 'element-plus'

const visible = ref(false)
const dataForm = reactive({
  id: 0,
  paramKey: '',
  paramValue: '',
  remark: ''
})
const dataRule = {
  paramKey: [
    { required: true, message: $t('sys.parameteNameNoNull'), trigger: 'blur' }
  ],
  paramValue: [
    { required: true, message: $t('sys.parameterValuenoNull'), trigger: 'blur' }
  ],
  remark: [
    { required: true, message: $t('sys.remarkNotNull'), trigger: 'blur' }
  ]
}
const dataFormRef = ref(null)
const init = (id) => {
  dataForm.id = id || 0
  visible.value = true
  nextTick(() => {
    dataFormRef.value?.resetFields()
    if (dataForm.id) {
      http({
        url: http.adornUrl(`/sys/config/info/${dataForm.id}`),
        method: 'get',
        params: http.adornParams()
      }).then(({ data }) => {
        dataForm.paramKey = data.paramKey
        dataForm.paramValue = data.paramValue
        dataForm.remark = data.remark
      })
    }
  })
}
const emit = defineEmits(['refreshDataList'])
// 表单提交
const onSubmit = () => {
  dataFormRef.value?.validate((valid) => {
    if (valid) {
      http({
        url: http.adornUrl('/sys/config'),
        method: dataForm.id ? 'put' : 'post',
        data: http.adornData({
          id: dataForm.id || undefined,
          paramKey: dataForm.paramKey,
          paramValue: dataForm.paramValue,
          remark: dataForm.remark
        })
      }).then(() => {
        ElMessage({
          message: $t('publics.operation'),
          type: 'success',
          duration: 1500,
          onClose: () => {
            visible.value = false
            emit('refreshDataList')
          }
        })
      })
    }
  })
}
defineExpose({ init })
</script>

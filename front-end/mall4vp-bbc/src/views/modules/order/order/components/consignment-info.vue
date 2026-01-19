<template>
  <!-- 发货信息，用于导出代发货订单的excel交给快递公司 -->
  <el-dialog
    v-model="visible"
    :modal="false"
    :title="$t('order.pleEntShipInfo')"
    :close-on-click-modal="false"
    width="38%"
  >
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      :rules="dataRule"
      label-width="120px"
      @submit.prevent
      @keyup.enter="onSubmit()"
    >
      <el-form-item
        :label="$t('order.senderName')"
        prop="consignmentName"
      >
        <el-input
          v-model="dataForm.consignmentName"
          controls-position="right"
          :label="$t('order.senderName')"
        />
      </el-form-item>
      <el-form-item
        :label="$t('order.shiPhoNum')"
        prop="consignmentMobile"
      >
        <el-input
          v-model="dataForm.consignmentMobile"
          controls-position="right"
          :label="$t('order.shiPhoNum')"
        />
      </el-form-item>
      <el-form-item
        :label="$t('order.deliveryAddress')"
        prop="consignmentAddr"
      >
        <el-input
          v-model="dataForm.consignmentAddr"
          controls-position="right"
          :label="$t('order.deliveryAddress')"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <div
          class="default-btn"
          @click="visible = false"
        >
          {{ $t("crud.filter.cancelBtn") }}
        </div>
        <div
          class="default-btn primary-btn"
          @click="onSubmit()"
        >
          {{ $t("crud.filter.submitBtn") }}
        </div>
      </div>
    </template>
  </el-dialog>
</template>
<script setup>
const emit = defineEmits(['inputCallback'])
const dataRule = {
  consignmentName: [
    { required: true, message: $t('publics.noNull'), trigger: 'blur' }
  ],
  consignmentMobile: [
    { required: true, message: $t('publics.noNull'), trigger: 'blur' }
  ],
  consignmentAddr: [
    { required: true, message: $t('publics.noNull'), trigger: 'blur' }
  ]
}

const visible = ref(false)
const dataForm = reactive({
  consignmentName: '',
  consignmentMobile: '',
  consignmentAddr: ''
})
const dataFormRef = ref(null)
const init = () => {
  visible.value = true
  nextTick(() => {
    dataFormRef.value?.resetFields()
  })
}
defineExpose({ init })

/**
 * 表单提交
 */
const onSubmit = () => {
  dataFormRef.value?.validate((valid) => {
    if (valid) {
      visible.value = false
      emit('inputCallback', dataForm)
    }
  })
}
</script>

<template>
  <el-dialog
    v-model="visible"
    :title="$t('components.modifyUseAddress')"
    :modal="false"
    top="200px"
    :close-on-click-modal="false"
  >
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      :rules="dataRule"
      label-width="80px"
      @submit.prevent
      @keyup.enter="onSubmit()"
    >
      <el-form-item
        :label="$t('components.userComments')"
        prop="remarks"
      >
        <el-input
          v-model="dataForm.remarks"
          disabled
          :placeholder="$t('components.pleaseEnterRemarks')"
          type="textarea"
          :autosize="{ minRows: 2, maxRows: 10 }"
          maxlength="250"
          show-word-limit
        />
      </el-form-item>
      <el-form-item
        :label="$t('order.sellerNote')"
        prop="shopRemarks"
      >
        <el-input
          v-model="dataForm.shopRemarks"
          :placeholder="$t('components.pleaseEnterRemarks')"
          type="textarea"
          :autosize="{ minRows: 2, maxRows: 10 }"
          maxlength="250"
          show-word-limit
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div>
        <el-button
          type="primary"
          @click="visible = false"
        >
          {{ $t("crud.filter.cancelBtn") }}
        </el-button>
        <el-button
          type="primary"
          @click="submitProds()"
        >
          {{ $t("crud.filter.submitBtn") }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ElMessage } from 'element-plus'
const emit = defineEmits(['refreshUserRemarkOrder'])
const dataRule = ref({})

const visible = ref(false)
const dataForm = reactive({
  orderId: '',
  remarks: '',
  shopRemarks: '',
  orderNumber: ''
})
// 获取数据列表
const init = (order) => {
  const orderInfo = Object.assign({}, order)
  visible.value = true
  dataForm.orderId = orderInfo.orderId
  dataForm.orderNumber = orderInfo.orderNumber
  dataForm.remarks = orderInfo.remarks
  dataForm.shopRemarks = orderInfo.shopRemarks
}
defineExpose({ init })

const dataFormRef = ref(null)
// 确定事件
const submitProds = () => {
  dataFormRef.value?.validate(valid => {
    if (valid) {
      http({
        url: http.adornUrl('/order/order/changeOrderRamark'),
        method: 'put',
        data: http.adornData(dataForm)
      }).then(() => {
        ElMessage({
          message: $t('publics.operation'),
          type: 'success',
          duration: 1500,
          onClose: () => {
            visible.value = false
            emit('refreshUserRemarkOrder')
          }
        })
      })
    }
  })
}
</script>

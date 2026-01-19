<template>
  <el-dialog
    v-model="visible"
    class="component-printer-add-or-update"
    :title="!dataForm.printerId ? $t('crud.addTitle') : $t('temp.modify')"
    :close-on-click-modal="false"
  >
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      :rules="dataRule"
      :label-width="$t('language')==='zh_CN'?'120px': 'auto'"
      style="width: 400px; margin-left:10px;"
      @keyup.enter="onSubmit()"
    >
      <!-- 打印机名称 -->
      <el-form-item
        :label="$t('printer.printerName')"
        prop="printerName"
      >
        <el-input
          v-model.trim="dataForm.printerName"
          maxlength="50"
          show-word-limit
        />
      </el-form-item>
      <!-- 设备码 -->
      <el-form-item
        :label="$t('printer.siid')"
        prop="siid"
      >
        <el-input
          v-model.trim="dataForm.siid"
          maxlength="50"
          show-word-limit
          @input="inputLimit"
        />
        <span style="color: #999999; font-size: 13px">{{ $t('printer.siidRuleTips') }}</span>
      </el-form-item>
      <!-- 打印机备注 -->
      <el-form-item
        :label="$t('printer.printerRemark')"
        prop="printerRemark"
      >
        <el-input
          v-model.trim="dataForm.printerRemark"
          maxlength="50"
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
          {{ $t("crud.filter.cancelBtn") }}
        </div>
        <div
          class="default-btn primary-btn"
          type="primary"
          @click="onSubmit()"
        >
          {{ $t("crud.filter.submitBtn") }}
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ElMessage } from 'element-plus'

const emit = defineEmits(['refreshDataList'])
const visible = ref(false)

const dataFormRef = ref(null)
const dataForm = ref({
  printerId: 0,
  printerName: null,
  siid: null,
  printerRemark: null,
  isDefault: 0
})

const dataRule = reactive({
  printerName: [
    { required: true, message: $t('printer.printerNameEmptyTips2'), trigger: 'blur' }
  ],
  siid: [
    { required: true, message: $t('printer.siidEmptyTips'), trigger: 'blur' }
  ]
})
const init = (printerId) => {
  dataForm.value.printerId = printerId || 0
  visible.value = true
  nextTick(() => {
    dataFormRef.value?.resetFields()
    if (dataForm.value.printerId) {
      http({
        url: http.adornUrl('/platform/printer/info/' + dataForm.value.printerId),
        method: 'get',
        params: http.adornParams()
      }).then(({ data }) => {
        dataForm.value = data
      })
    }
  })
}

// 表单提交
const onSubmit = Debounce(function () {
  dataFormRef.value.validate(valid => {
    if (valid) {
      http({
        url: http.adornUrl('/platform/printer'),
        method: dataForm.value.printerId ? 'put' : 'post',
        data: http.adornData(dataForm.value)
      }).then(() => {
        ElMessage({
          message: $t('publics.operation'),
          type: 'success',
          duration: 1500,
          onClose: () => {
            visible.value = false
            emit('refreshDataList')
            dataFormRef.value?.resetFields()
          }
        })
      })
    }
  })
}, 1500)
const { proxy } = getCurrentInstance()
const inputLimit = (val) => {
  // 正则匹配英文及数字的字符
  const reg = /[^a-zA-Z0-9]/
  // 查找是否有英文及数字的字符
  // 没有则返回-1，有则返回对应位置
  const n = val.search(reg)
  if (n !== -1) {
    dataForm.value.siid = val.slice(0, n)
    proxy.$forceUpdate()
  }
}
defineExpose({
  init
})
</script>

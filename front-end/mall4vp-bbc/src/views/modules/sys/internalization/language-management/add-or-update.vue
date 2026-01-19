<template>
  <div class="component-language-management">
    <el-dialog
      v-model="visible"
      :title="
        !dataForm.langId ? $t('brand.add') : $t('sysManagement.modify')
      "
      :close-on-click-modal="false"
    >
      <el-form
        ref="dataFormRef"
        :model="dataForm"
        :rules="dataRule"
        :label-width="$t('language') !=='zh_CN' ? 'auto' : '160px'"
        @submit.prevent
        @keyup.enter="onSubmit()"
      >
        <el-form-item
          :label="$t('internalization.langName')"
          prop="name"
        >
          <el-input
            v-model.trim="dataForm.name"
            type="text"
            clearable
            :placeholder="$t('internalization.langName')"
          />
        </el-form-item>
        <el-form-item
          :label="$t('internalization.langCode')"
          prop="language"
        >
          <el-input
            v-model.trim="dataForm.language"
            type="text"
            clearable
            :placeholder="$t('internalization.langCode')"
          />
        </el-form-item>
        <el-form-item
          :label="$t('internalization.langNumber')"
          prop="lang"
        >
          <el-input-number
            v-model="dataForm.lang"
            controls-position="right"
            :min="0"
            :max="9999999"
            :precision="0"
            :label="$t('internalization.langNumber')"
            @keydown="channelInputLimit"
            @blur="changeSeq"
          />
        </el-form-item>
        <el-form-item
          :label="$t('internalization.langdel')"
          prop="master"
        >
          <el-radio-group v-model="dataForm.master">
            <el-radio :label="1">
              {{ $t('publics.yes') }}
            </el-radio>
            <el-radio :label="0">
              {{ $t('publics.no') }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          :label="$t('internalization.langEnable')"
          prop="hide"
        >
          <el-radio-group v-model="dataForm.hide">
            <el-radio :label="0">
              {{ $t('publics.yes') }}
            </el-radio>
            <el-radio :label="1">
              {{ $t('publics.no') }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <div
            class="default-btn"
            @click="visible = false"
          >
            {{ $t("remindPop.cancel") }}
          </div>
          <div
            class="default-btn primary-btn"
            @click="onSubmit()"
          >
            {{ $t("remindPop.confirm") }}
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
const emit = defineEmits(['refreshDataList'])

const dataForm = ref({
  langId: 0,
  name: '',
  lang: 0,
  language: '',
  master: 0,
  hide: 0
})
const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})

const dataRule = {
  name: [
    { required: true, message: $t('internalization.langNameNoNull'), trigger: 'blur' }
  ],
  lang: [
    { required: true, message: $t('internalization.langCodeNonNull'), trigger: 'blur' }
  ],
  language: [
    { required: true, message: $t('internalization.langNumberNoNull'), trigger: 'blur' }
  ]
}

const visible = ref(false)
let isSubmit = false
const dataFormRef = ref(null)
const init = (id) => {
  dataFormRef.value?.resetFields()
  visible.value = true
  isSubmit = false
  dataForm.value.langId = id || 0
  if (dataForm.value.langId) {
    nextTick(() => {
      getCityList()
    })
  }
}

/**
 * 表单提交
 */
const onSubmit = () => {
  dataFormRef.value?.validate(valid => {
    if (valid) {
      const param = dataForm.value
      if (isSubmit) {
        return false
      }
      isSubmit = true
      http({
        url: http.adornUrl(param.langId ? '/platform/lang/update' : '/platform/lang/save'),
        method: param.langId ? 'put' : 'post',
        data: http.adornData(param)
      })
        .then(() => {
          ElMessage({
            message: $t('remindPop.success'),
            type: 'success',
            duration: 1500,
            onClose: () => {
              visible.value = false
              emit('refreshDataList', page)
            }
          })
        })
        .catch(() => {
          isSubmit = false
        })
    }
  })
}

/**
 * 获取详情
 */
const getCityList = () => {
  http({
    url: http.adornUrl('/platform/lang/' + dataForm.value.langId),
    method: 'get'
  })
    .then(({ data }) => {
      if (!data) return
      dataForm.value = data
    })
}

const channelInputLimit = (e) => {
  const key = e.key
  // 不允许输入'e'
  if (key === 'e' || key === '.' || key === '+' || key === '-') {
    e.returnValue = false
    return false
  }
  return true
}

const changeSeq = () => {
  if (!dataForm.value.lang) {
    dataForm.value.lang = 0
  }
}

defineExpose({
  init
})

</script>

<style scoped lang="scss">
.component-language-management :deep(.el-form-item__label) {
  min-width: 160px;
}
</style>

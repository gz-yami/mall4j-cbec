<template>
  <div class="mod-hotSearch-add-or-update">
    <el-dialog
      v-model="visible"
      :title="
        !dataForm.hotSearchId ? $t('brand.add') : $t('sysManagement.modify')
      "
      :close-on-click-modal="false"
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
          :label="$t('shop.hotTitle')"
          prop="title"
        >
          <el-input
            v-model="dataForm.title"
            controls-position="right"
            :min="0"
            maxlength="50"
            show-word-limit
            :label="$t('distributionMsg.title')"
          />
        </el-form-item>

        <el-form-item
          :label="$t('shop.hotContent')"
          prop="content"
        >
          <el-input
            v-model="dataForm.content"
            controls-position="right"
            :min="0"
            maxlength="250"
            show-word-limit
            :label="$t('publics.content')"
            @change="checkcontent"
          />
        </el-form-item>
        <el-form-item
          :label="$t('brand.sequence')"
          prop="seq"
        >
          <el-input-number
            v-model="dataForm.seq"
            controls-position="right"
            :min="0"
            :max="9999999"
            :precision="0"
            :label="$t('hotSearch.seq')"
            @keydown="channelInputLimit"
            @blur="changeSeq"
          />
        </el-form-item>
        <el-form-item
          :label="$t('brand.status')"
          prop="status"
        >
          <el-radio-group v-model="dataForm.status">
            <el-radio :label="0">
              {{ $t('groups.notEnabled') }}
            </el-radio>
            <el-radio :label="1">
              {{ $t('brand.enAble') }}
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

const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})

const validateTitle = (rule, value, callback) => {
  if (String(value).trim() === '') {
    callback(new Error($t('order.cannotEmpty')))
  } else {
    callback()
  }
}

const validateContent = (rule, value, callback) => {
  if (String(value).trim() === '') {
    callback(new Error($t('order.cannotEmpty')))
  } else {
    callback()
  }
}
const dataRule = {
  title: [
    { required: true, message: $t('hotSearch.titleCannotBeEmpty'), trigger: 'blur' },
    { min: 1, max: 50, message: $t('hotSearch.length50'), trigger: 'blur' },
    { validator: validateTitle, trigger: 'blur' }
  ],
  content: [
    { required: true, message: $t('hotSearch.ContentCannotBeEmpty'), trigger: 'blur' },
    { min: 1, max: 255, message: $t('hotSearch.length250'), trigger: 'blur' },
    { validator: validateContent, trigger: 'blur' }
  ]
}

const visible = ref(false)
let isSubmit = false
const dataForm = ref({
  hotSearchId: 0,
  title: '',
  content: '',
  recDate: '',
  seq: 0,
  status: 0,
  type: 1
})
const dataFormRef = ref(null)
const init = (data) => {
  visible.value = true
  isSubmit = false
  if (data) {
    dataForm.value = Object.assign({}, data)
    dataFormRef.value?.clearValidate()
  } else {
    nextTick(() => {
      dataFormRef.value?.resetFields()
      dataForm.value = {
        hotSearchId: 0,
        title: '',
        content: '',
        recDate: '',
        seq: 0,
        status: 0,
        type: 1
      }
    })
  }
}

const checkcontent = () => {
  dataForm.value.content = dataForm.value.content.trim()
}

// 表单提交
const onSubmit = () => {
  dataFormRef.value?.validate(valid => {
    if (valid) {
      const param = dataForm.value
      if (isSubmit) {
        return false
      }
      isSubmit = true
      http({
        url: http.adornUrl('/platform/hotSearch'),
        method: param.hotSearchId ? 'put' : 'post',
        data: http.adornData(param)
      }).then(() => {
        ElMessage({
          message: $t('remindPop.success'),
          type: 'success',
          duration: 1500,
          onClose: () => {
            visible.value = false
            emit('refreshDataList', page)
          }
        })
      }).catch(() => {
        isSubmit = false
      })
    }
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
  if (!dataForm.value.seq) {
    dataForm.value.seq = 0
  }
}

defineExpose({
  init
})

</script>

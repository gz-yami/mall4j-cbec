<template>
  <el-dialog
    v-model="visible"
    :title="!dataForm.templateId ? $t('brand.add') : $t('sysManagement.modify')"
    :close-on-click-modal="false"
  >
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      :rules="dataRule"
      :label-width="$t('language')==='zh_CN'?'130px': 'auto'"
      @submit.prevent
      @keyup.enter="onSubmit()"
    >
      <el-form-item
        :label="$t('platform.msgType') + '：'"
        prop="sendType"
      >
        <span>{{ $t('platform.customisedMsg') }}</span>
      </el-form-item>
      <el-form-item
        :label="$t('platform.notifyConten') + '：'"
        prop="message"
      >
        <el-input
          v-model="dataForm.message"
          maxlength="255"
        />
      </el-form-item>
      <el-form-item
        :label="$t('platform.selectTag') + '：'"
        prop="selTagIds"
      >
        <el-select
          v-model="dataForm.selTagIds"
          multiple
          filterable
          :placeholder="$t('user.pleaseSelect')"
        >
          <el-option
            v-for="item in tagList"
            :key="item.value"
            :label="item.tagName"
            :value="item.userTagId"
          />
        </el-select>
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

const emit = defineEmits(['refreshDataList'])
const visible = ref(false)
const isCheck = ref(false)
const isConfirm = ref(false)
const dataForm = reactive({
  templateId: null,
  sendType: null,
  templateTypeList: [],
  message: null,
  templateCode: null,
  mpCode: null,
  createTime: null,
  selTagIds: []
})
const dataFormRef = ref(null)
const tagList = ref([])
const dataRule = reactive({
  message: [
    { required: true, message: $t('platform.notifyContenNoNull'), trigger: 'blur' }
  ],
  selTagIds: [
    { required: true, message: $t('platform.selTagError'), trigger: 'blur' }
  ]
})

const init = (templateId) => {
  isConfirm.value = false
  isCheck.value = false
  dataForm.templateTypeList = []
  dataForm.selTagIds = []
  dataForm.templateId = templateId || 0
  visible.value = true
  nextTick(() => {
    dataFormRef.value?.resetFields()
    if (dataForm.templateId) {
      http({
        url: http.adornUrl('/platform/sendTagNotify/info/' + dataForm.templateId),
        method: 'get',
        params: http.adornParams()
      }).then(({ data }) => {
        dataForm.templateId = data.templateId
        dataForm.sendType = data.sendType
        dataForm.templateTypeList = data.templateTypeList
        dataForm.message = data.message
        dataForm.templateCode = data.templateCode
        dataForm.mpCode = data.mpCode
        dataForm.createTime = data.createTime
        dataForm.selTagIds = data.selTagIds
        dataForm.tagParams = data.tagParams
        getTagList()
      })
    } else {
      getTagList()
    }
  })
}
const getTagList = () => {
  http({
    url: http.adornUrl('/user/userTag/getTagList'),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    tagList.value = data
    if (dataForm.templateId && dataForm && dataForm.tagParams) {
      const tagIds = []
      dataForm.tagParams.forEach(item => {
        const index = tagList.value.find(e => {
          return e.userTagId === item.tagId
        })
        if (index) {
          tagIds.push(item.tagId)
        }
      })
      dataForm.selTagIds = tagIds
    }
  })
}
const msg = (text) => {
  ElMessage({
    message: text,
    type: 'error',
    duration: 1500
  })
  isCheck.value = true
}
// 表单提交
const onSubmit = () => {
  dataFormRef.value?.validate((valid) => {
    if (valid) {
      isCheck.value = false
      if (isCheck.value || isConfirm.value) {
        return
      }
      const checkData = dataForm.selTagIds === null || dataForm.selTagIds === undefined || dataForm.selTagIds.length <= 0
      if (checkData) {
        msg($t('platform.selTagError'))
        return
      }
      isConfirm.value = true
      http({
        url: http.adornUrl('/platform/sendTagNotify'),
        method: dataForm.templateId ? 'put' : 'post',
        data: http.adornData(dataForm)
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
        isConfirm.value = false
      })
    }
  })
}
defineExpose({ init })
</script>

<template>
  <el-dialog
    v-model="visible"
    :title="!dataForm.id ? $t('brand.add') : $t('user.editBtn')"
    :close-on-click-modal="false"
    :z-index="1100"
    top="5vh"
    width="780px"
  >
    <el-form
      ref="dataFormRef"
      class="dialog-form"
      :model="dataForm"
      :rules="dataRule"
      :label-width="$t('language')!=='zh_CN'?'160px': '90px'"
      @submit.prevent
      @keyup.enter="onSubmit()"
    >
      <el-form-item
        :label="$t('shop.announcementTitle')"
        prop="title"
      >
        <el-input
          v-model="dataForm.title"
          maxlength="36"
        />
      </el-form-item>
      <el-form-item
        :label="$t('platform.sendTime')"
        prop="type"
      >
        <el-radio-group v-model="dataForm.immediatelySend">
          <el-radio :label="1">
            {{ $t('publics.sendNow') }}
          </el-radio>
          <el-radio :label="0">
            {{ $t('publics.scheduledTransmission') }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item
        v-show="dataForm.immediatelySend === 0"
        :label="$t('shop.selectTime')"
        prop="type"
        class="timer-item-wrap"
      >
        <el-date-picker
          v-model="dataForm.sendTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="datetime"
          :placeholder="$t('platform.selectSendingTime')"
          :picker-options="pickerOptions"
          :disabled-date="pickerOptions.disabledDate"
        />
      </el-form-item>
      <el-form-item
        v-show="dataForm.types.includes(2)"
        :label="$t('shop.userScope')"
        prop="type"
      >
        <el-radio-group v-model="dataForm.visibleUserType">
          <el-radio :label="0">
            {{ $t('shop.allUsers') }}
          </el-radio>
          <el-radio :label="2">
            {{ $t('shop.specifyUser') }}
          </el-radio>
        </el-radio-group>
      </el-form-item>

      <select-account-range
        v-if="dataForm.visibleUserType === 2 && dataForm.types.includes(2)"
        :type="2"
        :table-list="dataForm.userDetailList"
        @get-ids="(ids) => dataForm.userIds = ids"
      />

      <el-form-item
        :label="$t('publics.status')"
        prop="status"
      >
        <el-radio-group v-model="dataForm.status">
          <el-radio :label="1">
            {{ $t("publics.publicar") }}
          </el-radio>
          <el-radio :label="0">
            {{ $t("publics.cancel") }}
          </el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item
        :label="$t('publics.isTop')"
        prop="isTop"
      >
        <el-radio-group v-model="dataForm.isTop">
          <el-radio :label="1">
            {{ $t("publics.yes") }}
          </el-radio>
          <el-radio :label="0">
            {{ $t("publics.no") }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item
        required
        :label="$t('shop.noticeContent')"
        class="notice-content"
      >
        <tiny-mce
          v-if="visible"
          ref="tinyMceRef"
          v-model="dataForm.content"
          :tinymce-height="250"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <div
          class="default-btn"
          @click="onCancel"
        >
          {{ $t('crud.filter.cancelBtn') }}
        </div>
        <div
          class="primary-btn default-btn"
          @click="onSubmit()"
        >
          {{ $t('crud.filter.submitBtn') }}
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import TinyMce from '@/components/tiny-mce/index.vue'
import { isHtmlNull } from '@/utils/index.js'
import SelectAccountRange from '@/components/select-account-range/index.vue'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['refreshDataList', 'onClose'])
const pickerOptions = reactive({
  // 设置禁用状态，参数为当前日期
  disabledDate (time) {
    // return time.getTime() < Date.now(); //当天不可选
    return time.getTime() < new Date().getTime() - 86400000 //  - 86400000是否包括当天
  },
  // 控制时分秒不可选
  selectableRange: (() => {
    const data = new Date()
    const hour = data.getHours()
    const minute = data.getMinutes()
    const second = data.getSeconds()
    return [`${hour}:${minute}:${second} - 23:59:59`]
  })()
})
const tagList = ref([])
const visible = ref(false)
const dataForm = reactive({
  title: null,
  content: null,
  url: null,
  status: 1,
  type: 1,
  isTop: 0,
  sendTime: '', // 发送时间
  types: [2],
  immediatelySend: 1,
  shopRange: 1,
  visibleUserType: 0,
  userIds: [],
  multiShopIds: [],
  userDetailList: [],
  userTagIds: [],
  publishTime: ''
})
const dataFormRef = ref(null)
const isSubmit = ref(false)
const validateTitle = (rule, value, callback) => {
  if (!value.trim()) {
    callback(new Error($t('shop.titCanNoBlank')))
  } else {
    callback()
  }
}
// eslint-disable-next-line no-unused-vars
const validateContent = (rule, value, callback) => {
  if (isHtmlNull(value)) {
    callback(new Error($t('product.content')))
  } else {
    callback()
  }
}
const dataRule = {
  title: [
    { required: true, message: $t('shop.titCanNoBlank'), trigger: 'blur' },
    { validator: validateTitle, trigger: 'blur' }
  ]
}

const init = (id) => {
  dataForm.id = id || 0
  isSubmit.value = false
  dataForm.sendTime = ''
  dataForm.publishTime = ''
  dataForm.types = [2]
  dataForm.immediatelySend = 1
  dataForm.shopRange = 1
  dataForm.visibleUserType = 0
  dataForm.multiShopIds = []
  dataForm.userTagIds = []
  dataForm.shopDetailList = []
  dataForm.userDetailList = []
  dataForm.content = null
  dataFormRef.value?.resetFields()
  if (dataForm.id) {
    http({
      url: http.adornUrl('/platform/notice/info/' + dataForm.id),
      method: 'get'
    }).then(({ data }) => {
      dataForm.title = data.title
      dataForm.content = data.content
      dataForm.url = data.url
      dataForm.status = data.status
      dataForm.type = data.type
      dataForm.isTop = data.isTop
      dataForm.sendTime = data.sendTime
      dataForm.types = data.types.split(',').map(Number)
      dataForm.immediatelySend = data.immediatelySend
      dataForm.shopRange = 1
      dataForm.visibleUserType = data.visibleUserType
      dataForm.multiShopIds = data.multiShopIds
      dataForm.userTagIds = data.userTagIds
      dataForm.userIds = []
      if (data.userIds) {
        dataForm.userIds = data.userIds.split(',')
      }
      dataForm.publishTime = data.publishTime

      dataForm.shopDetailList = data.shopDetailList
      dataForm.userDetailList = data.userDetailList

      if (!dataForm.shopDetailList) {
        dataForm.shopDetailList = []
        dataForm.multiShopIds = []
      } else {
        dataForm.multiShopIds = dataForm.shopDetailList?.map(item => item.shopId)
      }
      dataForm.types?.forEach(type => {
        if (type === 1 && dataForm.shopDetailList.length > 0) {
          dataForm.shopRange = 2
        }
        if (type === 2 && dataForm.userTagIds && dataForm.userTagIds.length > 0) {
          dataForm.visibleUserType = 1
        }
      })
      visible.value = true
    })
  } else {
    visible.value = true
  }
}

const getTagList = () => {
  http({
    url: http.adornUrl('/user/userTag/getTagList'),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    tagList.value = data
    const tagIds = []
    if (dataForm.userTagIds) {
      dataForm.userTagIds.forEach(item => {
        const index = tagList.value.find(e => {
          return e.userTagId === item
        })
        if (index) {
          tagIds.push(item)
        }
      })
    }
    dataForm.userTagIds = tagIds
  })
}

const onCancel = () => {
  visible.value = false
  nextTick(() => {
    emit('onClose')
  })
}

// 表单提交
const onSubmit = () => {
  dataFormRef.value?.validate((valid) => {
    if (valid) {
      if (isSubmit.value) {
        return false
      }
      if (isHtmlNull(dataForm.content)) {
        ElMessage.error($t('shopProcess.noticeTip'))
        return
      }
      if (dataForm.types.length === 0) {
        ElMessage({
          message: $t('publics.notifyTips'),
          type: 'error',
          duration: 1500
        })
        return
      }
      if (dataForm.immediatelySend === 0 && !dataForm.sendTime) {
        ElMessage({
          message: `${$t('tip.select')} ${$t('platform.sendTime')}`,
          type: 'error',
          duration: 1500
        })
        return
      }
      if (dataForm.shopRange === 2 && (!dataForm.multiShopIds || !dataForm.multiShopIds.length)) {
        ElMessage({
          message: `${$t('tip.select')} ${$t('shop.designatedMerchant')}`,
          type: 'error',
          duration: 1500
        })
        return
      }
      if (dataForm.visibleUserType === 2 && !dataForm.userIds.length) {
        ElMessage({
          message: `${$t('tip.select')} ${$t('shop.specifyUser')}`,
          type: 'error',
          duration: 1500
        })
        return
      }
      if (dataForm.visibleUserType === 1 && !dataForm.userTagIds.length) {
        ElMessage({
          message: `${$t('tip.select')}  ${$t('shop.tag')}`,
          type: 'error',
          duration: 1500
        })
        return
      }
      if (dataForm.shopRange === 1) {
        dataForm.multiShopIds = []
      }
      if (dataForm.visibleUserType === 0) {
        dataForm.userTagIds = []
        dataForm.userIds = []
      } else if (dataForm.visibleUserType === 1) {
        dataForm.userIds = []
      } else {
        dataForm.userTagIds = []
      }

      const isContentLimit = LimitWordCount()
      if (!isContentLimit) {
        return ElMessage({
          message: $t('shopFeature.notice.contentLimitTxt'),
          type: 'error',
          duration: 1500
        })
      }

      const obj = JSON.parse(JSON.stringify(dataForm))
      obj.types = obj.types.length > 0 ? obj.types.join(',') : null
      obj.multiShopIds = obj.multiShopIds.length > 0 ? obj.multiShopIds.join(',') : null
      obj.userIds = obj.userIds.length > 0 ? obj.userIds.join(',') : null
      isSubmit.value = true
      http({
        url: http.adornUrl('/platform/notice'),
        method: dataForm.id ? 'put' : 'post',
        data: http.adornData(obj)
      }).then(() => {
        ElMessage({
          message: $t('remindPop.success'),
          type: 'success',
          duration: 1500,
          onClose: () => {
            visible.value = false
            emit('refreshDataList')
            dataForm.content = null
          }
        })
      }).catch((e) => {
        isSubmit.value = false
      })
    }
  })
}

/**
 * 富文本字数限制
 */
const LimitWordCount = () => {
  if (!dataForm.content) return true
  // 获取富文本中的内容（去标签去空格）
  const contentStr = dataForm.content
    .replace(/<[^<>]+>/g, '')
    .replace(/&nbsp;/gi, '')
    .replace(/(\s)+/gi, '')
  // 限制不超过两万字
  return contentStr.length < 20000
}

defineExpose({ init })
</script>

<style lang="scss" scoped>
.dialog-form {
  :deep(.el-form-item) {
    margin-bottom: 4px;
    padding: 4px 0;
  }
}
.timer-item-wrap {
  :deep(.el-input__inner ){
   width: 240px;
  }
}
</style>

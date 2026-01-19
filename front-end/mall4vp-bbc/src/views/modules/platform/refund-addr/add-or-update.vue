<template>
  <el-dialog
    v-model="visible"
    :title="
      !dataForm.refundAddrId
        ? $t('shop.newShipAdd')
        : $t('shop.modifyShipAdd')
    "
    :close-on-click-modal="false"
    :width="dialogWidth"
  >
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      :rules="dataRule"
      :inline="true"
      label-width="auto"
      @submit.prevent
      @keyup.enter="onSubmit()"
    >
      <el-form-item
        class="addressee"
        :label="$t('publics.addressee')"
        prop="receiverName"
      >
        <el-input
          v-model="dataForm.receiverName"
          :placeholder="$t('publics.addressee')"
          maxlength="30"

          style="width: 300px"
          show-word-limit
        />
      </el-form-item>

      <el-form-item
        :label="$t('publics.mobilePhone')"
        prop="receiverMobile"
      >
        <el-input
          v-model="dataForm.receiverMobile"
          maxlength="11"
          style="width: 300px"
          :placeholder="$t('publics.mobilePhone')"
        />
      </el-form-item>
      <el-form-item
        class="mobilePhone"
        :label="$t('shop.companyLandline')"
        prop="receiverTelephone"
      >
        <el-input
          v-model="receiverTelephonePrefix"
          maxlength="4"
          style="width: 100px"
          :placeholder="$t('admin.areaCode')"
        />
        ——
        <el-input
          v-model="receiverTelephone"
          maxlength="8"
          style="width: 300px"
          :placeholder="$t('shop.companyLandline')"
        />
      </el-form-item>
      <el-form-item
        :label="$t('address.postalCode')"
        prop="postCode"
      >
        <el-input
          v-model="dataForm.postCode"
          style="width: 300px"
          :placeholder="$t('address.postalCode')"
          maxlength="9"
        />
      </el-form-item>
      <el-form-item
        :label="$t('shopProcess.addr')"
        required
        prop="areaInfo"
      >
        <address-selection
          :ref="addressSelectionRef"
          :current-addr-val="selectAddr"
          @update-current-addr="updateCurrentAddr"
          @on-addr-change="onAddrChange"
        />
      </el-form-item>
      <el-form-item
        :label="$t('address.detailed')"
        prop="addr"
      >
        <el-input
          v-model="dataForm.addr"

          :placeholder="$t('address.detailed')"
          maxlength="50"
          style="width: 600px"
        />
      </el-form-item>
      <el-form-item
        :label="$t('address.defaultRefundAddr')"
        prop=""
      >
        <el-checkbox v-model="defaultAddr">
          {{ $t('address.defaultRefundAddr') }}
        </el-checkbox>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <div
          class="default-btn"
          @click="visible = false"
        >{{ $t("crud.filter.cancelBtn") }}</div>
        <div
          class="default-btn primary-btn"
          @click="onSubmit()"
        >{{ $t("crud.filter.submitBtn") }}</div>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { Debounce } from '@/utils/debounce'
import { isPhone, isPhoneStar } from '@/utils/validate'

const emit = defineEmits(['refreshDataList'])

const dataForm = reactive({
  refundAddrId: null,
  addrId: 0,
  addr: '',
  receiverName: '',
  receiverMobile: '',
  receiverTelephone: '',
  postCode: '',
  area: '',
  city: '',
  province: '',
  areaId: null,
  cityId: null,
  provinceId: null,
  defaultAddr: 0
})
const page = reactive({
  total: 0, // 总页数
  currentPage: 1, // 当前页数
  pageSize: 10 // 每页显示多少条
})

const validateReceiverName = (rule, value, callback) => {
  if (!value.trim()) {
    dataForm.receiverName = ''
    callback(new Error($t('shop.coneeNameCanEmpty')))
  } else {
    callback()
  }
}
// eslint-disable-next-line no-unused-vars
const validatePhone = (rule, value, callback) => {
  if (!onCheckPhonePrefix()) {
    callback(new Error($t('shop.pleeNormaeNumF')))
  } else {
    callback()
  }
}
const validateAddr = (rule, value, callback) => {
  if (!value.trim()) {
    dataForm.addr = ''
    callback(new Error($t('shop.addressCannotBeEmpty')))
  } else {
    callback()
  }
}
const validateNumber = (rule, value, callback) => {
  if (value && !/^[0-9]\d*$/.test(value)) {
    callback(new Error($t('shop.pleaseInputNumber')))
  } else {
    callback()
  }
}
const validateReceiverTelephone = (rule, value, callback) => {
  const validateVal = receiverTelephonePrefix.value + '-' + receiverTelephone.value
  if (value && (dataForm.refundAddrId ? !isPhoneStar(validateVal) : !isPhone(validateVal))) {
    callback(new Error($t('shop.pleaseInputNumber')))
  } else {
    callback()
  }
}
const validateProvince = (rule, value, callback) => {
  if (!dataForm.provinceId) {
    callback(new Error($t('shopProcess.addrInputTips')))
  } else {
    callback()
  }
}
const dataRule = reactive({
  receiverName: [
    { required: true, message: $t('shop.coneeNameCanEmpty'), trigger: 'blur' },
    { validator: validateReceiverName, trigger: 'blur' }
  ],
  addr: [{ required: true, message: $t('shop.addressCannotBeEmpty'), trigger: 'blur' },
    { validator: validateAddr, trigger: 'blur' }
  ],
  areaInfo: [
    { required: true, validator: validateProvince, trigger: 'blur' }
  ],
  receiverMobile: [
    { required: true, message: $t('sys.mobilePhoneNoNull'), trigger: 'blur' },
    { validator: validatePhone, trigger: 'blur' }
  ],
  receiverTelephone: [{ validator: validateReceiverTelephone, trigger: 'blur' }],
  postCode: [{ validator: validateNumber, trigger: 'blur' }]
})
const dialogWidth = ref('')
const defWidth = localStorage.getItem('cbecB2cLang') !== 'zh_CN' ? 850 : 750

onMounted(() => {
  dialogWidth.value = onSetDialogWidth(defWidth)
  window.onresize = () => {
    return (() => {
      dialogWidth.value = onSetDialogWidth(defWidth)
    })()
  }
})
const onSetDialogWidth = function (defWidthVal) {
  const val = document.body.clientWidth
  const def = defWidthVal || 850 // 默认宽度
  if (val < def) {
    return '97%'
  } else {
    return def + 'px'
  }
}

const dataFormRef = ref(null)
const visible = ref(false)
const defaultAddr = ref(false)
const receiverTelephonePrefix = ref('')
const receiverTelephone = ref('')
watch([receiverTelephonePrefix, receiverTelephone], ([newPrefix, newPhone]) => {
  newPrefix ??= ''
  newPhone ??= ''
  dataForm.receiverTelephone = newPrefix + (newPrefix && newPhone ? '-' : '') + newPhone
})
const init = (id) => {
  defaultAddr.value = false
  dataForm.refundAddrId = id || 0
  visible.value = true
  nextTick(() => {
    dataFormRef.value?.resetFields()
    dataForm.provinceId = null
    dataForm.cityId = null
    dataForm.areaId = null
    receiverTelephonePrefix.value = ''
    receiverTelephone.value = ''
    selectAddr.value = []
  })
  if (dataForm.refundAddrId) {
    http({
      url: http.adornUrl(
        `/shop/refundAddr/info/${dataForm.refundAddrId}`
      ),
      method: 'get',
      params: http.adornParams()
    }).then(({ data }) => {
      dataForm.addr = data.addr
      dataForm.receiverMobile = data.receiverMobile
      if (data.receiverTelephone) {
        if (data.receiverTelephone.includes('-')) {
          [receiverTelephonePrefix.value, receiverTelephone.value] = data.receiverTelephone.split('-')
        } else {
          [receiverTelephonePrefix.value, receiverTelephone.value] = ['', data.receiverTelephone]
        }
      } else {
        [receiverTelephonePrefix.value, receiverTelephone.value] = ['', '']
      }
      // dataForm.receiverTelephone = data.receiverTelephone
      dataForm.areaId = data.areaId
      dataForm.cityId = data.cityId
      dataForm.provinceId = data.provinceId
      dataForm.receiverName = data.receiverName
      dataForm.postCode = data.postCode
      dataForm.defaultAddr = data.defaultAddr
      defaultAddr.value = data.defaultAddr === 1

      const AddrVal = [data.provinceId]
      data.cityId && AddrVal.push(data.cityId)
      data.areaId && AddrVal.push(data.areaId)
      selectAddr.value = AddrVal
    })
  }
}
const addressSelectionRef = ref(null)

const selectAddr = ref([])
/**
 * 更新 selectAddr绑定数值
 */
const updateCurrentAddr = (val) => {
  selectAddr.value = val
}
/**
 * 选中地址
 * @param arrObj 选中地址数据
 */
const onAddrChange = (arrObj) => {
  dataForm.province = arrObj.province
  dataForm.provinceId = arrObj.provinceId
  dataForm.city = arrObj.city
  dataForm.cityId = arrObj.cityId
  dataForm.area = arrObj.area
  dataForm.areaId = arrObj.areaId
}

const onCheckPhonePrefix = () => {
  if (dataForm.receiverMobile) {
    const reg = /^(?:(?:\+|00)86)?1\d{2}([\d*]{4})\d{4}$/
    if (!reg.test(dataForm.receiverMobile)) {
      return false
    }
  }
  return true
}
// 表单提交
const isSubmitting = ref(false) // 正在提交
const onSubmit = Debounce(() => {
  // 将是否设为默认地址保存起来
  dataForm.defaultAddr = defaultAddr.value ? 1 : 0
  dataFormRef.value?.validate(valid => {
    if (!dataForm.provinceId) {
      ElMessage({
        message: $t('shopProcess.addrInputTips'),
        type: 'error',
        duration: 1500
      })
      return
    }
    if (valid) {
      if (isSubmitting.value) {
        return
      }
      isSubmitting.value = true
      http({
        url: http.adornUrl('/shop/refundAddr'),
        method: dataForm.refundAddrId ? 'put' : 'post',
        data: http.adornData({
          refundAddrId: dataForm.refundAddrId || undefined,
          addr: dataForm.addr,
          receiverName: dataForm.receiverName,
          receiverMobile: dataForm.receiverMobile,
          receiverTelephone: dataForm.receiverTelephone,
          area: dataForm.area,
          city: dataForm.city,
          province: dataForm.province,
          areaId: dataForm.areaId,
          cityId: dataForm.cityId,
          provinceId: dataForm.provinceId,
          postCode: dataForm.postCode,
          defaultAddr: dataForm.defaultAddr
        })
      }).then(() => {
        isSubmitting.value = false
        ElMessage({
          message: $t('publics.operation'),
          type: 'success',
          duration: 1500,
          onClose: () => {
            visible.value = false
            emit('refreshDataList', page)
          }
        })
      }).catch(() => {
        isSubmitting.value = false
      })
    }
  })
}, 1500)
defineExpose({
  init
})
</script>

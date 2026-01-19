<template>
  <el-dialog
    v-model="visible"
    class="component-outlet-config-add-or-update"
    :title="!dataForm.outletConfigId ? $t('crud.addTitle') : $t('temp.modify')"
    :close-on-click-modal="false"
  >
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      :rules="dataRule"
      :label-width="$t('language')!=='zh_CN'?'200px': 'auto'"
      style="width: 650px; margin-left:10px;"
      @keyup.enter="onSubmit()"
    >
      <!-- 快递公司类型 -->
      <el-form-item
        :label="$t('outletConfig.deliveryCompanyType')"
        prop="deliveryCompanyType"
      >
        <el-select
          v-model="dataForm.deliveryCompanyType"
          :placeholder="$t('order.seleCouCom')"
          :disabled="dataForm.outletConfigId"
        >
          <el-option
            v-for="item in deliveryCompanyType"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <!-- 发货地址 -->
      <el-form-item
        v-if="dataForm.outletConfigId"
        :label="$t('order.deliveryAddress')"
        prop="printAddr"
      >
        <div class="part-form-delivery">
          <el-input
            v-if="dataForm.outletConfigId"
            v-model="dataForm.printAddr"
            disabled
            style="width: 330px"
          />
        </div>
      </el-form-item>
      <el-form-item
        v-if="!dataForm.outletConfigId"
        :label="$t('order.deliveryAddress')"
        prop="shopAddrId"
      >
        <div class="part-form-delivery">
          <el-select
            v-model="dataForm.shopAddrId"
            :placeholder="$t('tip.select')"
            style="max-width: 450px;width: 100%"
          >
            <el-option
              v-for="item in addrList"
              :key="item.refundAddrId"
              :label="item.province + item.city + item.area + item.addr"
              :value="item.refundAddrId"
            />
          </el-select>
          <div
            class="lb"
          >
            <el-button
              type="primary"
              link
              @click="onManageAddr"
            >
              {{ $t('outletConfig.addrManage') }}
            </el-button>
            <span
              class="vertical"
            />
            <el-button
              type="primary"
              link
              @click="getAddrList"
            >
              {{ $t('admin.refresh') }}
            </el-button>
          </div>
        </div>
      </el-form-item>
      <!-- 发货人 -->
      <el-form-item
        :label="$t('outletConfig.shipper')"
        prop="shipper"
      >
        <el-input
          v-model.trim="dataForm.shipper"
          maxlength="10"
          show-word-limit
        />
      </el-form-item>
      <!-- 发货人电话 -->
      <el-form-item
        :label="$t('outletConfig.mobile')"
        prop="mobile"
      >
        <el-input
          v-model.trim="dataForm.mobile"
          maxlength="11"
          oninput="value=value.replace(/[^\d*]/g,'')"
          show-word-limit
        />
      </el-form-item>
      <!-- 电子面单客户账户或月结账号 -->
      <el-form-item
        :label="deliveryCompanyType[dataForm.deliveryCompanyType - 1].partnerId"
        prop="partnerId"
      >
        <el-input
          v-model.trim="dataForm.partnerId"
          maxlength="50"
          show-word-limit
        />
      </el-form-item>
      <!-- 电子面单密码 -->
      <el-form-item
        v-if="dataForm.deliveryCompanyType !== 1 && dataForm.deliveryCompanyType !== 2"
        :label="deliveryCompanyType[dataForm.deliveryCompanyType - 1].partnerKey"
        prop="partnerKey"
      >
        <el-input
          v-model.trim="dataForm.partnerKey"
          maxlength="50"
          show-word-limit
        />
      </el-form-item>
      <!-- 收件网点名称 -->
      <el-form-item
        v-if="dataForm.deliveryCompanyType === 5 || dataForm.deliveryCompanyType === 6"
        :label="deliveryCompanyType[dataForm.deliveryCompanyType - 1].net"
        prop="net"
      >
        <el-input
          v-model.trim="dataForm.net"
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
import { isMobile } from '@/utils/validate'

const emit = defineEmits(['refreshDataList'])
const visible = ref(false)

const dataFormRef = ref(null)
const dataForm = ref({
  outletConfigId: null,
  deliveryCompanyType: 1,
  shopAddrId: null,
  shipper: '',
  mobile: '',
  partnerId: '',
  partnerKey: '',
  net: '',
  paperSize: 1,
  isDefault: 0,
  printAddr: ''
})
const deliveryCompanyType = [{
  value: 1,
  label: $t('express.sf'),
  partnerId: $t('express.monthlyClosingNumber'),
  partnerKey: '',
  net: ''
}, {
  value: 2,
  label: $t('express.jd'),
  partnerId: $t('express.merchantCoding'),
  partnerKey: '',
  net: ''
}, {
  value: 3,
  label: $t('express.yto'),
  partnerId: $t('express.merchantCode'),
  partnerKey: $t('express.merchantKey'),
  net: ''
}, {
  value: 4,
  label: $t('express.yunDa'),
  partnerId: $t('express.yundaWhiteHorseAccount'),
  partnerKey: $t('express.jointCipher'),
  net: ''
}, {
  value: 5,
  label: $t('express.zto'),
  partnerId: $t('express.partnerCode'),
  partnerKey: $t('express.partnerKey'),
  net: $t('express.dotCoding')
}, {
  value: 6,
  label: $t('express.sto'),
  partnerId: $t('express.customerName'),
  partnerKey: $t('express.customerPassword'),
  net: $t('express.network')
}, {
  value: 7,
  label: $t('express.best'),
  partnerId: $t('express.operationCoding'),
  partnerKey: $t('express.secretKey'),
  net: ''
}, {
  value: 8,
  label: $t('express.ems'),
  partnerId: $t('express.ProtocolClientNumber'),
  partnerKey: $t('express.eCommerceCustomerLogos'),
  net: ''
}]

// eslint-disable-next-line no-unused-vars
const validateMobile = (rule, value, callback) => {
  if (dataForm.value.mobile) {
    const mobile = /^(?:(?:\+|00)86)?1\d{2}([\d*]{4})\d{4}$/
    if (mobile.test(value)) {
      callback()
    } else {
      callback(new Error($t('homes.InputCorrectPhone')))
    }
  } else if (!isMobile(value)) {
    callback(new Error($t('homes.InputCorrectPhone')))
  } else {
    callback()
  }
}
const addrList = ref([])
const dataRule = reactive({
  deliveryCompanyType: [
    { required: true, message: $t('outletConfig.deliveryCompanyTypeEmptyTips'), trigger: 'blur' }
  ],
  shopAddrId: [
    { required: true, message: $t('outletConfig.addrEmptyTips'), trigger: 'change' }
  ],
  shipper: [
    { required: true, message: $t('outletConfig.shipperEmptyTips'), trigger: 'blur' }
  ],
  partnerId: [
    { required: true, message: $t('publics.noNull'), trigger: 'blur' }
  ],
  partnerKey: [
    { required: true, message: $t('publics.noNull'), trigger: 'blur' }
  ],
  net: [
    { required: true, message: $t('publics.noNull'), trigger: 'blur' }
  ],
  mobile: [
    { required: true, message: $t('sys.mobilePhoneNoNull'), trigger: 'blur' },
    { validator: validateMobile, trigger: 'blur' }
  ]
})
const init = (outletConfigId) => {
  dataForm.value.outletConfigId = outletConfigId || 0
  visible.value = true
  nextTick(() => {
    dataFormRef.value?.resetFields()
    getAddrList()
    if (!dataForm.value.outletConfigId) {
      return
    }
    if (dataForm.value.outletConfigId) {
      http({
        url: http.adornUrl('/platform/outletConfig/info/' + dataForm.value.outletConfigId),
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
        url: http.adornUrl('/platform/outletConfig'),
        method: dataForm.value.outletConfigId ? 'put' : 'post',
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
// 加载地址库
const getAddrList = () => {
  http({
    url: http.adornUrl('/shop/refundAddr/list'),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    addrList.value = data
    if (addrList.value.find(i => i.defaultAddr === 1)) {
      dataForm.value.shopAddrId = addrList.value.find(i => i.defaultAddr === 1).refundAddrId
    }
  })
}

// 跳转管理地址库
const onManageAddr = () => {
  window.open('/platform/refund-addr/index', '_blank', 'noopener,noreferrer')
}

defineExpose({
  init
})
</script>
<style lang="scss" scoped>
.component-outlet-config-add-or-update {
  .part-form-delivery {
    display:flex;

    :deep(.el-select) {
      width: 330px !important;
    }
    .lb {
      display: flex;
      align-items: center;

      margin-left: 20px;

      .vertical {
        display: inline-block;
        width: 1px;
        height: 10px;
        background: #000;
        margin: 0 6px;
      }
    }
  }
}
</style>

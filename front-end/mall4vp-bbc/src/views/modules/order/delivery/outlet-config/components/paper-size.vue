<template>
  <el-dialog
    v-model="visible"
    class="component-outlet-config-paper-size"
    :title="$t('outletConfig.paperSize')"
    width="700"
    :close-on-click-modal="false"
  >
    <div class="paper-size-box">
      <div class="box-left">
        <div class="img">
          <img
            :src="outletConfigOneImg"
            alt
          >
        </div>
        <div class="text">
          <el-radio
            v-model="paperSize"
            :label="1"
          >
            {{ $t('outletConfig.oneSheet') }}(76*130)
          </el-radio>
        </div>
      </div>
      <div
        v-if="type !== 5 && type !== 6 && type !== 7"
        class="box-right"
      >
        <div class="img">
          <img
            :src="outletConfigTwoImg"
            alt
          >
        </div>
        <div class="text">
          <el-radio
            v-model="paperSize"
            :label="2"
          >
            {{ $t('outletConfig.twoSheet') }}({{ twoPaperSize }})
          </el-radio>
        </div>
      </div>
    </div>
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
import emsOne from '@/assets/img/outlet-config/ems-one.png'
import emsTwo from '@/assets/img/outlet-config/ems-two.png'
import htkyOne from '@/assets/img/outlet-config/htky-one.png'
import jdOne from '@/assets/img/outlet-config/jd-one.png'
import jdTwo from '@/assets/img/outlet-config/jd-two.png'
import sfOne from '@/assets/img/outlet-config/sf-one.png'
import sfTwo from '@/assets/img/outlet-config/sf-two.png'
import stoOne from '@/assets/img/outlet-config/sto-one.png'
import ytoOne from '@/assets/img/outlet-config/yto-one.png'
import ytoTwo from '@/assets/img/outlet-config/yto-two.png'
import yundaOne from '@/assets/img/outlet-config/yunda-one.png'
import yundaTwo from '@/assets/img/outlet-config/yunda-two.png'
import ztoOne from '@/assets/img/outlet-config/zto-one.png'
import ztoTwo from '@/assets/img/outlet-config/zto-two.png'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['refreshDataList'])
const visible = ref(false) // 弹窗显示隐藏
const id = ref(null)
const type = ref(0)

const paperSize = ref(1) // 类型 1 一联 2 二联
const twoPaperSize = ref('100*203') // 二联纸张规格
const outletConfigOneImg = ref('') // 一联图片
const outletConfigTwoImg = ref('') // 二联图片

watch(type, (val) => {
  // 监听type并根据id修改配置
  switch (val) {
    case 1:
      // 顺丰速运
      twoPaperSize.value = '100*150'
      outletConfigOneImg.value = sfOne
      outletConfigTwoImg.value = sfTwo
      break
    case 2:
      // 京东快递
      twoPaperSize.value = '100*180'
      outletConfigOneImg.value = jdOne
      outletConfigTwoImg.value = jdTwo
      break
    case 3:
      // 圆通速递
      twoPaperSize.value = '100*180'
      outletConfigOneImg.value = ytoOne
      outletConfigTwoImg.value = ytoTwo
      break
    case 4:
      // 韵达快递
      twoPaperSize.value = '100*203'
      outletConfigOneImg.value = yundaOne
      outletConfigTwoImg.value = yundaTwo
      break
    case 5:
      // 中通快递
      twoPaperSize.value = '100*180'
      outletConfigOneImg.value = ztoOne
      outletConfigTwoImg.value = ztoTwo
      break
    case 6:
      // 申通快递
      twoPaperSize.value = '100*180'
      outletConfigOneImg.value = stoOne
      break
    case 7:
      // 百世快递
      twoPaperSize.value = '100*180'
      outletConfigOneImg.value = htkyOne
      break
    case 8:
      // EMS
      twoPaperSize.value = '100*180'
      outletConfigOneImg.value = emsOne
      outletConfigTwoImg.value = emsTwo
      break
  }
})

let mobile = ''
const init = (outletInfo) => {
  id.value = outletInfo.outletConfigId
  type.value = outletInfo.deliveryCompanyType
  paperSize.value = outletInfo.paperSize
  mobile = outletInfo.mobile
  visible.value = true
}

// 表单提交
const onSubmit = () => {
  const tempUpdateForm = {}
  tempUpdateForm.outletConfigId = id.value
  tempUpdateForm.deliveryCompanyType = type.value
  tempUpdateForm.paperSize = paperSize.value
  tempUpdateForm.mobile = mobile
  http({
    url: http.adornUrl('/platform/outletConfig'),
    method: 'put',
    data: tempUpdateForm
  }).then(() => {
    ElMessage({
      message: $t('user.succeeded'),
      type: 'success',
      duration: 1500,
      onClose: () => {
        visible.value = false
        emit('refreshDataList')
      }
    })
  })
}

defineExpose({
  init
})
</script>
<style lang="scss" scoped>
@use './paper-size.scss';
</style>

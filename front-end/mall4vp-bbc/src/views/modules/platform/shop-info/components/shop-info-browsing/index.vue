<template>
  <div class="component-shop-info-browsing">
    <div class="business-info-mod">
      <el-form
        ref="shopBasicInfoRef"
        :label-width="$t('language') !== 'zh_CN' ? 'auto' : '140px'"
        :model="shopBasicInfo"

        @submit.prevent
      >
        <div class="ci-wrapper">
          <div class="left-wrap">
            <el-form-item
              :label="$t('shopProcess.logo') + '：'"
              required
            >
              <div class="business-license-box">
                <div class="logo-image-box">
                  <pic-upload
                    v-model="shopBasicInfo.shopLogo"
                    :is-preview="true"
                    :custom-style="{ width:'100px', height: '100px' }"
                  />
                </div>
              </div>
            </el-form-item>
            <el-form-item
              :label="$t('shopProcess.merchantName') + '：'"
              required
            >
              {{ shopBasicInfo.merchantName }}
            </el-form-item>
            <el-form-item
              :label="$t('shopProcess.shopName') + '：'"
              required
            >
              {{ shopBasicInfo.shopName }}
            </el-form-item>
            <el-form-item
              :label="$t('shopProcess.tel') + '：'"
              required
            >
              {{ shopBasicInfo.tel }}
            </el-form-item>
            <el-form-item :label="$t('shopProcess.intro') + '：'">
              <span v-if="shopBasicInfo.intro"> {{ shopBasicInfo.intro }} </span>
              <span
                v-else
                class="noyet-wrap"
              >{{ $t('shopProcess.noYet') }}</span>
            </el-form-item>
          </div>
        </div>
      </el-form>
    </div>

    <div class="footer">
      <div class="foot-box">
        <div
          class="btn default-btn primary-btn"
          @click="onModifyBasicInfor"
        >
          {{ $t('shopProcess.modify') }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>

const emit = defineEmits(['closeBrowse'])

onMounted(() => {
  onQueryShopBasicInfo()
})

// 修改基本
const onModifyBasicInfor = () => {
  emit('closeBrowse')
}
/**
   * 查询基本信息
   */
const shopBasicInfo = ref({
  mobile: '',
  intro: '',
  tel: '',
  shopName: '',
  merchantName: '',
  shopLogo: ''
})
const onQueryShopBasicInfo = () => {
  http({
    url: http.adornUrl('/shop/shopDetail/info'),
    method: 'get',
    params: http.adornParams()
  }).then(res => {
    shopBasicInfo.value = res.data
  })
}

</script>

<style scoped>
  .component-shop-info-browsing {
    color: #606266;
    font-size: 14px;
    margin-top: 30px;
  }
  .logo-image-box {
    width: 120px;
    height: 120px;
  }
  .foot-box {
    margin-left: 140px;
  }
  .noyet-wrap {
    color: #999;
  }
  .ci-wrapper .left-wrap {
    max-width: 50%;
  }

  div :deep(.el-form-item__label) {
    min-width: 140px;
  }
</style>

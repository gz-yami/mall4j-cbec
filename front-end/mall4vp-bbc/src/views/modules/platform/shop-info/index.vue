<template>
  <div class="app-container page-shop-info">
    <!-- 导航栏 -->
    <div class="nav-box">
      <div class="nav">
        <div
          :class="['nav-item', navStatus === 0 ? 'active' : '']"
          @click="onSwitchNav(0)"
        >
          {{ $t('shopProcess.basicInfo') }}
        </div>
      </div>
    </div>
    <!-- 模块 -->
    <div class="shop-info-box">
      <!-- 基本信息 -->
      <div
        v-if="navStatus === 0"
        class="basic-info-mod"
      >
        <!-- 基本信息浏览页面 -->
        <shop-info-browsing
          v-if="isShopInfoBrowsing"
          @close-browse="onBasicInforModify"
        />
        <!-- 基本信息 -->
        <div
          v-if="!isShopInfoBrowsing"
          class="business-info-mod-wrap"
        >
          <div class="business-info-mod">
            <el-form
              ref="shopBasicInfoRef"
              label-width="auto"
              :model="shopBasicInfo"
              :rules="shopBasicInfoRules"

              @submit.prevent
            >
              <div class="ci-wrapper">
                <div class="left-info">
                  <!-- 店铺logo -->
                  <el-form-item
                    :label="$t('shopProcess.logo')"
                    prop="shopLogo"
                  >
                    <div class="business-license-box">
                      <div class="license-content">
                        <pic-upload
                          v-model="shopBasicInfo.shopLogo"
                          :custom-style="{ width:'100px', height: '100px' }"
                        />
                      </div>
                      <div class="upload-tips">
                        {{ $t('shopProcess.shopLogoPicTips') }}{{ $t('shopProcess.logoTips') }}
                      </div>
                    </div>
                  </el-form-item>
                  <el-form-item
                    :label="$t('shopProcess.merchantName')"
                    prop="merchantName"
                  >
                    <p class="txt">
                      <el-input
                        v-model="shopBasicInfo.merchantName"
                        :placeholder="$t('shopProcess.merchantNameInputTips')"
                        maxlength="20"
                        @blur="
                          shopBasicInfo.merchantName =
                            shopBasicInfo.merchantName ?
                              removeHeadAndTailSpaces(shopBasicInfo.merchantName) :
                              shopBasicInfo.merchantName
                        "
                      />
                    </p>
                  </el-form-item>
                  <el-form-item
                    :label="$t('shopProcess.shopName')"
                    prop="shopName"
                  >
                    <p class="txt">
                      <el-input
                        v-model="shopBasicInfo.shopName"
                        :placeholder="$t('shopProcess.shopNameInputTips')"
                        maxlength="20"
                        @blur="
                          shopBasicInfo.shopName =
                            shopBasicInfo.shopName ?
                              removeHeadAndTailSpaces(shopBasicInfo.shopName) :
                              shopBasicInfo.shopName
                        "
                      />
                    </p>
                  </el-form-item>
                  <el-form-item
                    :label="$t('shopProcess.tel')"
                    prop="tel"
                  >
                    <p class="txt">
                      <el-input
                        v-model="shopBasicInfo.tel"
                        maxlength="11"
                        oninput="value=value.replace(/[^\d*]/g,'')"
                        :placeholder="$t('shopProcess.telInputTips')"
                      />
                    </p>
                  </el-form-item>

                  <el-form-item
                    :label="$t('shopProcess.intro')"
                    prop="intro"
                  >
                    <el-input
                      v-model="shopBasicInfo.intro"
                      type="textarea"
                      :rows="4"
                      class="store-intro"
                      maxlength="200"
                      :placeholder="$t('shopProcess.introInput')"
                      @blur="
                        shopBasicInfo.intro =
                          shopBasicInfo.intro ?
                            removeHeadAndTailSpaces(shopBasicInfo.intro) :
                            shopBasicInfo.intro
                      "
                    />
                  </el-form-item>
                </div>

                <div class="right-info" />
              </div>
            </el-form>
          </div>
          <div class="footer">
            <div class="foot-box">
              <div
                v-if="isAuth('shop:shopDetail:save')"
                class="btn default-btn primary-btn"
                @click="onValidationShopBasicInfo"
              >
                {{ $t('shopProcess.saveBasicInfo') }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- /基本信息 -->
    </div>
  </div>
</template>

<script setup>
import { isAuth } from '@/utils'
import { ElMessage } from 'element-plus'
import { removeHeadAndTailSpaces } from '@/utils/validate'
import shopInfoBrowsing from './components/shop-info-browsing/index.vue'

const validateMobile = (rule, value, callback) => {
  const phoneReg = /^(?:(?:\d{3}-)?\d{8}|^(?:\d{4}-)?\d{7,8})(?:-\d+)?$/
  const mobile = /^(?:(?:\+|00)86)?1\d{2}([\d*]{4})\d{4}$/
  if (phoneReg.test(value) || mobile.test(value)) {
    callback()
  } else {
    callback(new Error($t('shopProcess.telErrorTips')))
  }
}
const validateMerchantName = (rule, value, callback) => {
  if (!value.trim()) {
    callback(new Error($t('shopProcess.merchantNameNotEmpty')))
  } else {
    callback()
  }
}
const validateShopName = (rule, value, callback) => {
  if (!value.trim()) {
    callback(new Error($t('shopProcess.shopNameNotEmpty')))
  } else {
    callback()
  }
}

const shopBasicInfoRules = reactive({
  merchantName: [
    { required: true, message: $t('shopProcess.merchantNameNotEmpty'), trigger: 'blur' },
    { validator: validateMerchantName, trigger: 'blur' },
    { min: 2, max: 20, message: $t('shopProcess.merchantNameErrorTips'), trigger: 'blur' }
  ],
  shopName: [
    { required: true, message: $t('shopProcess.shopNameNotEmpty'), trigger: 'blur' },
    { validator: validateShopName, trigger: 'blur' },
    { min: 2, max: 20, message: $t('shopProcess.shopNameInputTips'), trigger: 'blur' }
  ],
  tel: [
    { required: true, message: $t('shopProcess.telNotEmpty'), trigger: 'blur' },
    { validator: validateMobile, trigger: 'blur' }
  ],
  shopLogo: [
    { required: true, message: $t('shopProcess.logoNotEmpty'), trigger: 'change' }
  ]
})

// 步骤
const navStatus = ref(0)
onMounted(() => {
  navStatus.value = parseInt(useRoute().query.navStatus) || 0

  if (useRoute().query.isSuccess === '0') {
    ElMessage.error($t('allinpay.signFail'))
    navStatus.value = 3
    useRouter().push({
      query: {}
    })
  }

  // 查询基本信息
  onSwitchNav(navStatus.value)
})

const onSwitchNav = (value) => {
  navStatus.value = value
  if (value === 1) {
    // 查询工商信息
    onQueryShopBusinessInfo()
  } else {
    // 查询基本信息
    onQueryShopBasicInfo()
  }
}

/**
 * 查询基本信息
 */
const shopBasicInfo = ref({}) // 店铺基本信息
const onQueryShopBasicInfo = () => {
  http({
    url: http.adornUrl('/shop/shopDetail/info'),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    if (data) {
      shopBasicInfo.value = data
    }
  })
}

/**
 * 查询工商信息
 */
const companyInfoForm = ref({ // 店铺工商信息
  creditCode: '',
  firmName: '',
  residence: '',
  representative: '',
  capital: '',
  foundTime: '',
  startTime: '',
  endTime: '',
  businessScope: '',
  businessLicense: '',
  identityCardFront: '',
  identityCardLater: '',
  legalPhone: '',
  legalIds: ''
})
const endTimeFlag = ref(true)
const onQueryShopBusinessInfo = () => {
  http({
    url: http.adornUrl('/shop/shopCompany'),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    if (data) {
      companyInfoForm.value = data
      endTimeFlag.value = true
    }
  })
}

/**
 * 更新基本信息
 */
const shopBasicInfoRef = ref(null)
const userStore = useUserStore()
const isShopInfoBrowsing = ref(true)
const onValidationShopBasicInfo = () => {
  shopBasicInfoRef.value?.validate((valid) => {
    if (!valid) {
      return
    }
    http({
      url: http.adornUrl('/shop/shopDetail'),
      method: 'PUT',
      data: http.adornData(shopBasicInfo.value)
    })
      .then(() => {
        ElMessage({
          message: $t('shopProcess.baseSaveSuccess'),
          type: 'success',
          duration: 1000,
          onClose: () => {
            isShopInfoBrowsing.value = true
          }
        })
        userStore.login()
      }).catch(() => {
        isShopInfoBrowsing.value = true
      })
  })
}

/**
 * 基本信息切换成编辑状态
 */
const onBasicInforModify = () => {
  isShopInfoBrowsing.value = false
  nextTick(() => {
    onQueryShopBasicInfo()
  })
}

</script>

<style lang="scss" scoped>
  @use './index.scss';
</style>

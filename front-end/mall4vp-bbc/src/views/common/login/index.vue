<template>
  <div
    class="page-login"
    :style="backgroundImage"
  >
    <div class="login-box">
      <div class="top">
        <div class="logo">
          <img
            :src="checkFileUrl(configuration.bsLoginLogoImg)"
            style="max-height: 98px; max-width: 198px"
          >
          <span class="login-title">
            {{ configuration.bsTitleContent }}
          </span>
        </div>
      </div>
      <div class="mid">
        <el-form
          ref="dataFormRef"
          :model="dataForm"
          :rules="dataRule"
          status-icon
        >
          <el-form-item prop="userName">
            <el-input
              v-model="dataForm.userName"
              v-input-rule
              class="info"
              maxlength="16"
              :placeholder="$t('sys.userName')"
              @keyup.enter="onSubmit(dataFormRef)"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="dataForm.password"
              v-input-rule
              class="info"
              type="password"
              maxlength="20"
              :placeholder="$t('sys.password')"
              @keyup.enter="onSubmit(dataFormRef)"
            >
              >
            </el-input>
          </el-form-item>
          <el-form-item>
            <div
              class="default-btn primary-btn"
              style=""
              @click="onSubmit(dataFormRef)"
            >
              {{ $t('homes.login') }}
            </div>
          </el-form-item>
        </el-form>
      </div>

      <div class="bottom">
        {{ configuration.bsCopyright }}
      </div>
      <!--切换语言-->
      <div class="langs-wrap">
        <el-dropdown
          class="content-right-item"
          @command="switchLang"
        >
          <span class="el-dropdown-link lang-name">
            {{ langName }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item
                v-for="(langItem,inx) in langList"
                :key="inx"
                :command="langItem.language"
              >
                {{ langItem.name }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <!--end切换语言-->
    </div>
    <Verify
      ref="verify"
      :captcha-type="'blockPuzzle'"
      :img-size="{ width: '400px', height: '200px' }"
      @success="login"
    />
  </div>
</template>

<script setup>
import Verify from '@/components/verifition/Verify.vue'
import cookie from 'vue-cookies'
import { useLangStore } from '@/stores/lang.js'
import { useCurrencyStore } from '@/stores/currency.js'
const langStore = useLangStore()
const langList = computed(() => langStore.langList)
const langName = computed(() => langStore.langName)
const switchLang = (lang) => {
  const langInfo = langList.value.find(f => f.language === lang)
  if (langInfo) {
    langStore.switchLang(langInfo)
  }
  window.location.reload()
}

const router = useRouter()
const configuration = reactive({
  bsLoginLogoImg: null,
  bsLoginBgImg: null,
  bsCopyright: null,
  bsTitleContent: null,
  bsTitleImg: null,
  bsMenuTitleOpen: null,
  bsMenuTitleClose: null
})
const setConfiguration = (val) => {
  configuration.bsLoginLogoImg = val.bsLoginLogoImg
  configuration.bsLoginBgImg = val.bsLoginBgImg
  configuration.bsCopyright = val.bsCopyright
  configuration.bsTitleContent = val.bsTitleContent
  configuration.bsTitleImg = val.bsTitleImg
  configuration.bsMenuTitleOpen = val.bsMenuTitleOpen
  configuration.bsMenuTitleClose = val.bsMenuTitleClose
}

const dataForm = reactive({
  userName: '',
  password: ''
})

const dataFormRef = ref()
const dataRule = reactive({
  userName: [{ required: true, message: $t('home.accNoNull'), trigger: 'blur' }],
  password: [{ required: true, message: $t('home.pawNoNull'), trigger: 'blur' }]
})
const verify = ref(null)
// 提交表单校验
const onSubmit = async formEl => {
  if (!formEl) return
  await formEl.validate(valid => {
    if (valid) {
      verify.value.show()
    }
  })
}

onBeforeMount(() => {
  getWebConfigData()
})
const currencyStore = useCurrencyStore() // 默认货币
const isSubmit = ref(false)
const login = verifyResult => {
  if (isSubmit.value) {
    return
  }
  isSubmit.value = true
  http({
    url: http.adornUrl('/platformLogin'),
    method: 'post',
    data: http.adornData({
      userName: dataForm.userName,
      passWord: encrypt(dataForm.password),
      captchaVerification: verifyResult.captchaVerification
    })
  }).then(({ data }) => {
    isSubmit.value = false
    localStorage.removeItem('cbecB2cVpNotificationMessage')
    cookie.set('cbecB2cAuthorization_vp', data.accessToken)
    currencyStore.INIT_CURRENCY_DATA()
    router.replace({ name: 'home' })
  }).catch(() => {
    isSubmit.value = false
  })
}

const webConfigStore = useWebConfigStore()
// 背景样式
const backgroundImage = reactive({
  width: '100%',
  height: '100%',
  backgroundSize: '100% 100%',
  position: 'fixed',
  top: 0
})

const getWebConfigData = () => {
  http({
    url: http.adornUrl('/sys/webConfig/getActivity'),
    method: 'get'
  }).then(({ data }) => {
    data = formatConfigInfo(data)
    webConfigStore.addData(data)
    setConfiguration(data)
    if (data.bsLoginBgImg) {
      backgroundImage.backgroundImage = `url(${checkFileUrl(data.bsLoginBgImg)})`
    }
  })
}
</script>

<style lang="scss" scoped>
.page-login {
  width: 100%;
  height: 100%;
  background: no-repeat;
  background-size: cover;
  position: fixed;
  .login-box {
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    height: 100%;
    padding-top: 10%;
    .top {
      margin-bottom: 30px;
      text-align: center;
      .logo {
        font-size: 0;
        .login-title {
          font-size: 30px;
          color: #999999;
          margin: 0.5em 0 0.5em 0.5em;
          vertical-align: middle;
        }
      }
    }
    .mid {
      font-size: 14px;
      .info {
        width: 100%;
        height: 40px;
      }
      .default-btn {
        height: 40px;
        line-height: 40px;
        width: 100%;
        text-align: center;
        margin-top: 32px;
      }
    }
    .bottom {
      position: absolute;
      bottom: 10%;
      width: 100%;
      color: #999;
      font-size: 12px;
      text-align: center;
    }
  }
  .langs-wrap {
    display: flex;
    justify-content: right;
    .content-right-item {
      line-height: 20px;
      font-size: 14px;
      margin-right: 25px;
      cursor: pointer;
      &:last-child {
        margin-right: 0;
      }
      &.service-box{
        display: flex;
        align-items: flex-end;
      }
      .el-dropdown-link {
        color: #155bd4;
        &.lang-name{
          display: flex;
          align-items: center;
        }
      }
      .el-dropdown-link:focus {
        outline: none;
      }
      .service-icon{
        position: relative;
        transition: .1s;
        &.show{
          opacity: 1;
        }
        &.hidden{
          opacity: 0;
        }
        .message-radio {
          border-radius: 50%;
          width: 5px;
          height: 5px;
          background-color: red;
          display: inline-block;
          position: absolute;
          top: 2px;
          left: 12px;
        }
      }
    }
  }
}
</style>

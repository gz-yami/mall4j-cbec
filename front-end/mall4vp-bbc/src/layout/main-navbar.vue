<template>
  <div>
    <nav class="site-navbar">
      <div
        class="site-navbar-header"
        :style="{ 'margin-right': sidebarFold ? 0 : '20px' }"
      >
        <img
          v-if="configuration.bsTopBarIcon"
          class="menu-image-logo"
          :src="checkFileUrl(configuration.bsTopBarIcon)"
          alt="logo"
        >
        <span
          v-if="!sidebarFold"
          class="site-navbar-lg"
        >
          {{ configuration.bsMenuTitleOpen }}
        </span>
        <span
          v-else
          class="site-navbar-mini"
          :style="fontCloseSize"
        >
          {{ configuration.bsMenuTitleClose }}
        </span>
      </div>
      <div class="site-navbar-content">
        <div class="navbar-content-left">
          <svg-icon
            class="left-item"
            icon-class="icon-zhedie"
            @click="setSidebarFold"
          />
        </div>
        <div class="navbar-content-right">
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

          <el-dropdown
            class="content-right-item"
            :show-timeout="0"
            placement="bottom"
          >
            <span class="el-dropdown-link">{{ userName }}</span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="updatePasswordHandle">
                  {{
                    $t('homes.updatePwd')
                  }}
                </el-dropdown-item>
                <el-dropdown-item @click="logoutHandle">
                  {{ $t('homes.exit') }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      <!-- 弹窗, 修改密码 -->
      <UpdatePassword
        v-if="updatePassowrdVisible"
        ref="updatePassowrdRef"
      />
    </nav>
  </div>
</template>

<script setup>
import { ElMessageBox } from 'element-plus'
import UpdatePassword from './main-navbar-update-password.vue'
import { useLangStore } from '../stores/lang.js'
import { clearLoginInfo } from '@/utils/index.js'
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const langStore = useLangStore()
const userName = computed(() => userStore.name)
const langList = computed(() => langStore.langList)
const langName = computed(() => langStore.langName)

const configuration = ref({
  bsLoginLogoImg: null,
  bsLoginBgImg: null,
  bsCopyright: null,
  bsTitleContent: null,
  bsTitleImg: null,
  bsMenuTitleOpen: null,
  bsMenuTitleClose: null
})

const webConfigStore = useWebConfigStore()
const fontOpenSize = reactive({
  fontSize: '16px'
})
const fontCloseSize = reactive({
  fontSize: '16px'
})
const updateWebConfigData = () => {
  http({
    url: http.adornUrl('/sys/webConfig/getActivity'),
    method: 'get'
  }).then(({ data }) => {
    data = formatConfigInfo(data)
    webConfigStore.addData(data)
    configuration.value = data
    if (data.bsMenuTitleOpen.length >= 17) {
      fontOpenSize.fontSize = '16px'
    }
    if (data.bsMenuTitleClose.length >= 6) {
      fontCloseSize.fontSize = '18px'
    }
  })
}

const switchLang = (lang) => {
  const langInfo = langList.value.find(f => f.language === lang)
  if (langInfo) {
    langStore.switchLang(langInfo)
  }
  window.location.reload()
}

onMounted(() => {
  updateWebConfigData()
})

const commonStore = useCommonStore()
const sidebarFold = computed(() => commonStore.sidebarFold)
const setSidebarFold = () => {
  const len = commonStore.selectMenu.length
  const flag = sessionStorage.getItem('cbecB2cIsExpand')
  if ((route.path === '/home' || len === 1) && flag === '0') {
    commonStore.updateSidebarFold(true)
  } else {
    const foldFlag = sidebarFold.value
    commonStore.updateSidebarFold(!foldFlag)
  }
}

const logoutHandle = () => {
  ElMessageBox.confirm($t('homes.isExit'), $t('text.tips'), {
    confirmButtonText: $t('remindPop.confirm'),
    cancelButtonText: $t('remindPop.cancel'),
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl('/logOut'),
      method: 'post',
      data: http.adornData()
    }).then(() => {
      clearLoginInfo()
      router.push({ name: 'login' })
    })
  })
}
const updatePassowrdVisible = ref(false)
const updatePassowrdRef = ref(null)
// 修改密码
const updatePasswordHandle = () => {
  updatePassowrdVisible.value = true
  nextTick(() => {
    updatePassowrdRef.value?.init()
  })
}

</script>

<style lang="scss" scoped>
.site-navbar {
  display: flex;
  align-items: center;
  background-color: #ffffff;
  color: #333333;
  border-bottom: 1px solid #ebedf0;
  .site-navbar-header {
    display: flex;
    align-items: center;
    margin-left: 20px;
    color: #333;
    font-weight: 700;
    height: 50px;
    line-height: 50px;
    .site-navbar-lg {
      font-size: 16px;
      word-break: break-all;
      word-wrap: break-word;
    }
    .site-navbar-lg,
    .site-navbar-mini {
      margin: 0 5px;
    }
  }
  .site-navbar-content {
    flex: 1;
    width: 100%;
    display: flex;
    justify-content: space-between;
    padding: 0 20px;
    font-size: 18px;
    align-items: center;
    .navbar-content-left {
      flex: 1;
      .left-item {
        cursor: pointer;
      }
    }

    .navbar-content-right {
      display: flex;
      .content-right-item {
        line-height: 20px;
        font-size: 14px;
        margin-right: 25px;
        cursor: pointer;
        &:last-child {
          margin-right: 0;
        }
        .el-dropdown-link {
          color: #333;
          &.lang-name{
            display: flex;
            align-items: center;
          }
        }
        .el-dropdown-link:focus {
          outline: none;
        }

      }
    }
  }
}
.menu-image-logo {
  object-fit: contain;
  height: 18px;
  width: 59px;
  margin-right: 10px;
}
</style>

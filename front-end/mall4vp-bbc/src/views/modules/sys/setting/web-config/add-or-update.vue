<template>
  <div class="component-add-or-update">
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      :rules="dataRule"
      :label-width="
        $t('language')==='zh_CN' ? '180px' : 'auto'
      "
      @submit.prevent
      @keyup.enter="onSubmit()"
    >
      <!--后台相关配置-->
      <div v-if="props.paramKey==='PLATFROM_WEBSITE_CONFIG' || props.paramKey==='MULTISHOP_WEBSITE_CONFIG'">
        <el-form-item
          :label="$t('webInfoConfig.loginLogo')"
          prop="bsLoginLogoImg"
        >
          <pic-upload
            v-model="dataForm.bsLoginLogoImg"
            :limit="1"
            :size="'380*96'"
          />
          <div class="el-form-item-tips">
            {{ $t("webInfoConfig.imgsTip") + '380*96' + $t("webInfoConfig.px") }}
          </div>
        </el-form-item>

        <el-form-item
          :label="$t('webInfoConfig.loginBg')"
          prop="bsLoginBgImg"
        >
          <pic-upload
            v-model="dataForm.bsLoginBgImg"
            :limit="1"
            :size="'1920*940'"
          />
          <div class="el-form-item-tips">
            {{ $t("webInfoConfig.imgsTip") + '1920*940' + $t("webInfoConfig.px") }}
          </div>
        </el-form-item>

        <el-form-item
          :label="$t('webInfoConfig.pcTitleImg')"
          prop="bsTitleImg"
        >
          <pic-upload
            v-model="dataForm.bsTitleImg"
            :limit="1"
            :size="'100*100'"
          />
          <div class="el-form-item-tips">
            {{ $t("webInfoConfig.imgsTip") + '100*100' + $t("webInfoConfig.px") }}
          </div>
        </el-form-item>

        <el-form-item
          :label="$t('webInfoConfig.TopIcon')"
          prop="bsTopBarIcon"
        >
          <pic-upload
            v-model="dataForm.bsTopBarIcon"
            :limit="1"
            :size="'160*48'"
          />
          <div class="el-form-item-tips">
            {{ $t("webInfoConfig.imgsTip") + '160*48' + $t("webInfoConfig.px") }}
          </div>
        </el-form-item>

        <el-form-item
          v-if="langItemList && langItemList.length > 1"
          :label="$t('product.selectLanguage')"
          prop="selectLanguage"
        >
          <el-select
            v-model="curLang"
            multiple
            :placeholder="$t('tip.select')"
            style="width: 500px;"
            @change="selectLang"
          >
            <el-option
              v-for="item in langItemList"
              :key="item.lang"
              :label="item.name"
              :value="item.lang"
            />
          </el-select>
        </el-form-item>

        <div
          v-for="item in webConfigLangList"
          :key="item.lang"
        >
          <el-form-item
            :label="$t('webInfoConfig.bsCopyright') + (langItemList && langItemList.length > 1 ? `(${item.langName})` : '')"
            prop="bsCopyright"
          >
            <el-input
              v-model="item.bsCopyright"
            />
          </el-form-item>
        </div>

        <div
          v-for="(item,index) in webConfigLangList"
          :key="index"
        >
          <el-form-item
            :label="$t('webInfoConfig.bsTitleContent') + (langItemList && langItemList.length > 1 ? `(${item.langName})` : '')"
            prop="bsTitleContent"
          >
            <el-input
              v-model="item.bsTitleContent"
              maxlength="20"
              show-word-limit
            />
          </el-form-item>
        </div>

        <div
          v-for="(item,index) in webConfigLangList"
          :key="index"
        >
          <el-form-item
            :label="$t('webInfoConfig.bsMenuTitleOpen') + (langItemList && langItemList.length > 1 ? `(${item.langName})` : '')"
            prop="bsMenuTitleOpen"
          >
            <el-input
              v-model="item.bsMenuTitleOpen"
              maxlength="20"
              show-word-limit
            />
          </el-form-item>
        </div>

        <div
          v-for="(item,index) in webConfigLangList"
          :key="index"
        >
          <el-form-item
            :label="$t('webInfoConfig.bsMenuTitleClose') + (langItemList && langItemList.length > 1 ? `(${item.langName})` : '')"
            prop="bsMenuTitleClose"
          >
            <el-input
              v-model="item.bsMenuTitleClose"
              maxlength="6"
              show-word-limit
            />
          </el-form-item>
        </div>
      </div>

      <!--H5端相关配置-->
      <div v-if="props.paramKey==='H5_WEBSITE_CONFIG'">
        <el-form-item
          :label="$t('webInfoConfig.loginLogo')"
          prop="uniLoginLogoImg"
        >
          <pic-upload
            v-model="dataForm.uniLoginLogoImg"
            :limit="1"
            :size="'100*100'"
          />
          <div class="el-form-item-tips">
            {{ $t("webInfoConfig.imgsTip") + '100*100' + $t("webInfoConfig.px") }}
          </div>
        </el-form-item>
        <el-form-item
          v-if="langItemList && langItemList.length > 1"
          :label="$t('product.selectLanguage')"
          prop="selectLanguage"
        >
          <el-select
            v-model="curLang"
            multiple
            :placeholder="$t('tip.select')"
            style="width: 500px;"
            @change="selectLang"
          >
            <el-option
              v-for="item in langItemList"
              :key="item.lang"
              :label="item.name"
              :value="item.lang"
            />
          </el-select>
        </el-form-item>
        <div
          v-for="(item,index) in webConfigLangList"
          :key="index"
        >
          <el-form-item
            :label="$t('webInfoConfig.uniTitleContent') + (langItemList && langItemList.length > 1 ? `(${item.langName})` : '')"
          >
            <el-input
              v-model.trim="item.uniTitleContent"
            />
          </el-form-item>
        </div>
      </div>

      <!--自提点端相关配置-->
      <div v-if="props.paramKey==='STATION_WEBSITE_CONFIG'">
        <el-form-item
          :label="$t('webInfoConfig.loginLogo')"
          prop="stationLoginLogoImg"
        >
          <pic-upload v-model="dataForm.stationLoginLogoImg" />
          <div class="el-form-item-tips">
            {{ $t("webInfoConfig.imgsTip") + '100*100' + $t("webInfoConfig.px") }}
          </div>
        </el-form-item>
      </div>

      <el-form-item label=" ">
        <el-button
          type="primary"
          @click="onSubmit()"
        >
          {{ $t('webInfoConfig.save') }}
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'

const props = defineProps({
  paramKey: {
    type: String,
    default: 'PLATFROM_WEBSITE_CONFIG'
  },
  remark: {
    type: String,
    default: ''
  }
})

const dataForm = ref({
  id: null,
  isActivity: 0,
  bsLoginLogoImg: null,
  bsLoginBgImg: null,
  bsCopyright: null,
  bsTitleContent: null,
  bsTitleImg: null,
  bsTopBarIcon: null,
  bsMenuTitleOpen: null,
  bsMenuTitleClose: null,
  pcLogoImg: null,
  pcCopyright: null,
  pcQrcodeImg: null,
  pcCompanyName: null,
  pcCompanyInfo: null,
  pcTitleContent: null,
  pcTitleImg: null,
  pcCompanyNameShort: null,
  pcLogoImgText: null,
  pcWelcome: null,
  pcLoginBgImg: null,
  uniLoginLogoImg: null,
  stationLoginLogoImg: null,
  webConfigLangList: []
})
const dataRule = reactive({})
// 语言列表
const langItemList = ref([])
const masterLangInfo = reactive({ name: '', lang: '' })
// 当前所选语言
const curLang = ref([])
const webConfigLangList = ref([])
const dataFormRef = ref()
onMounted(() => {
  nextTick(() => {
    dataFormRef.value?.resetFields()
    getLangList()
    getConfigData()
  })
})

const getConfigData = () => {
  if (props.paramKey) {
    http({
      url: http.adornUrl(`/sys/webConfig/info/${props.paramKey}`),
      method: 'get',
      params: http.adornParams()
    }).then(({ data }) => {
      if (!data || data === '{}') return
      dataForm.value = data
      webConfigLangList.value = dataForm.value.webConfigLangList
      getLangList()
    })
  }
}

/**
 * 获取商品配置的语言列表
 */
const getLangList = () => {
  http({
    url: http.adornUrl('/platform/lang/default'),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    if (data) {
      const info = data
      masterLangInfo.name = info.name
      masterLangInfo.lang = info.lang
      langItemList.value = info.langItemList
      if (!webConfigLangList.value) {
        webConfigLangList.value = []
      }
      const tempWebConfigLangList = []
      const tempCurLang = []
      for (const item of langItemList.value) {
        const fd = webConfigLangList.value.find(it => it.lang === item.lang)
        if (fd) {
          fd.langName = item.name
          tempWebConfigLangList.push(fd)
          tempCurLang.push(item.lang)
        }
      }
      webConfigLangList.value = tempWebConfigLangList
      selectLang(tempCurLang)
    }
  })
}
const selectLang = (value) => {
  curLang.value = value
  // 设置主语言不可删除
  if (!curLang.value.includes(masterLangInfo.lang)) {
    curLang.value.unshift(masterLangInfo.lang)
  }
  // 菜单名称
  const tempArr = webConfigLangList.value.filter(item => curLang.value.includes(item.lang))
  curLang.value.forEach((item, index) => {
    if (!tempArr.find(f => f.lang == item)) {
      const fd = langItemList.value.find(it => it.lang === item)
      if (fd) {
        tempArr.splice(index, 0, {
          lang: fd.lang,
          langName: fd.name,
          bsCopyright: '',
          bsTitleContent: '',
          bsMenuTitleOpen: '',
          bsMenuTitleClose: '',
          pcCopyright: '',
          pcCompanyName: '',
          pcCompanyInfo: '',
          pcTitleContent: '',
          pcCompanyNameShort: '',
          pcWelcome: '',
          uniTitleContent: ''
        })
      }
    }
  })
  webConfigLangList.value = tempArr
}
const checkLang = () => {
  for (let i = 0; i < webConfigLangList.value.length; i++) {
    const item = webConfigLangList.value[i]
    // 后台配置判断
    if (props.paramKey === 'PLATFROM_WEBSITE_CONFIG' || props.paramKey === 'MULTISHOP_WEBSITE_CONFIG') {
      if (!item.bsCopyright) {
        errorMsg($t('webInfoConfig.bsCopyright') + (langItemList.value.length === 1 ? ' ' : '(' + item.langName + ') ') + $t('webInfoConfig.canNotBeEmpty'))
        return false
      }
      if (!item.bsTitleContent) {
        errorMsg($t('webInfoConfig.bsTitleContent') + (langItemList.value.length === 1 ? ' ' : '(' + item.langName + ') ') + $t('webInfoConfig.canNotBeEmpty'))
        return false
      }
      if (!item.bsMenuTitleOpen) {
        errorMsg($t('webInfoConfig.bsMenuTitleOpen') + (langItemList.value.length === 1 ? ' ' : '(' + item.langName + ') ') + $t('webInfoConfig.canNotBeEmpty'))
        return false
      }
      if (!item.bsMenuTitleClose) {
        errorMsg($t('webInfoConfig.bsMenuTitleClose') + (langItemList.value.length === 1 ? ' ' : '(' + item.langName + ') ') + $t('webInfoConfig.canNotBeEmpty'))
        return false
      }
    }

    // pc端配置判断
    if (props.paramKey === 'PC_WEBSITE_CONFIG') {
      if (!item.pcCopyright) {
        errorMsg($t('webInfoConfig.bsCopyright') + (langItemList.value.length === 1 ? ' ' : '(' + item.langName + ') ') + $t('webInfoConfig.canNotBeEmpty'))
        return false
      }
      if (!item.pcCompanyName) {
        errorMsg($t('webInfoConfig.pcCompanyName') + (langItemList.value.length === 1 ? ' ' : '(' + item.langName + ') ') + $t('webInfoConfig.canNotBeEmpty'))
        return false
      }
      if (!item.pcCompanyInfo) {
        errorMsg($t('webInfoConfig.pcCompanyInfo') + (langItemList.value.length === 1 ? ' ' : '(' + item.langName + ') ') + $t('webInfoConfig.canNotBeEmpty'))
        return false
      }
      if (!item.pcTitleContent) {
        errorMsg($t('webInfoConfig.pcTitleContent') + (langItemList.value.length === 1 ? ' ' : '(' + item.langName + ') ') + $t('webInfoConfig.canNotBeEmpty'))
        return false
      }
      if (!item.pcWelcome) {
        errorMsg($t('webInfoConfig.pcWelcome') + (langItemList.value.length === 1 ? ' ' : '(' + item.langName + ') ') + $t('webInfoConfig.canNotBeEmpty'))
        return false
      }
    }
    // h5端配置判断
    if (props.paramKey === 'H5_WEBSITE_CONFIG') {
      if (!item.uniTitleContent) {
        errorMsg($t('webInfoConfig.uniTitleContent') + (langItemList.value.length === 1 ? ' ' : '(' + item.langName + ') ') + $t('webInfoConfig.canNotBeEmpty'))
        return false
      }
    }
  }
  return true
}
const checkImage = () => {
  // 后台配置判断
  if (props.paramKey === 'PLATFROM_WEBSITE_CONFIG' || props.paramKey === 'MULTISHOP_WEBSITE_CONFIG') {
    if (!dataForm.value.bsLoginLogoImg) {
      errorMsg($t('webInfoConfig.loginLogo') + $t('webInfoConfig.canNotBeEmpty'))
      return false
    }
    if (!dataForm.value.bsLoginBgImg) {
      errorMsg($t('webInfoConfig.loginBg') + $t('webInfoConfig.canNotBeEmpty'))
      return false
    }
    if (!dataForm.value.bsTitleImg) {
      errorMsg($t('webInfoConfig.bsTitleImg') + $t('webInfoConfig.canNotBeEmpty'))
      return false
    }
    // TopIcon
    if (!dataForm.value.bsTopBarIcon) {
      errorMsg($t('webInfoConfig.TopIcon') + $t('webInfoConfig.canNotBeEmpty'))
      return false
    }
  }

  // pc端配置判断
  if (props.paramKey === 'PC_WEBSITE_CONFIG') {
    if (!dataForm.value.pcLogoImg) {
      errorMsg($t('webInfoConfig.logo') + $t('webInfoConfig.canNotBeEmpty'))
      return false
    }
    if (!dataForm.value.pcLoginBgImg) {
      errorMsg($t('webInfoConfig.loginBg') + $t('webInfoConfig.canNotBeEmpty'))
      return false
    }
    if (!dataForm.value.pcQrcodeImg) {
      errorMsg($t('webInfoConfig.pcQrcodeImg') + $t('webInfoConfig.canNotBeEmpty'))
      return false
    }
    if (!dataForm.value.pcTitleImg) {
      errorMsg($t('webInfoConfig.bsTitleImg') + $t('webInfoConfig.canNotBeEmpty'))
      return false
    }
    if (!dataForm.value.pcLogoImgText) {
      errorMsg($t('webInfoConfig.pcLogoImgText') + $t('webInfoConfig.canNotBeEmpty'))
      return false
    }
  }
  // h5端配置判断
  if (props.paramKey === 'H5_WEBSITE_CONFIG') {
    if (!dataForm.value.uniLoginLogoImg) {
      errorMsg($t('webInfoConfig.loginLogo') + $t('webInfoConfig.canNotBeEmpty'))
      return false
    }
  }

  // 自提点端配置判断
  if (props.paramKey === 'STATION_WEBSITE_CONFIG') {
    if (!dataForm.value.stationLoginLogoImg) {
      errorMsg($t('webInfoConfig.loginLogo') + $t('webInfoConfig.canNotBeEmpty'))
      return false
    }
  }
  return true
}
// 表单提交
const onSubmit = () => {
  dataFormRef.value?.validate((valid) => {
    if (!valid || !checkImage()) {
      return
    }
    if (!checkLang() || !checkImage()) {
      return
    }
    dataForm.value.webConfigLangList = webConfigLangList

    // 新增或修改配置
    const paramsData = {
      id: dataForm.value.id || 0,
      paramKey: props.paramKey,
      paramValue: JSON.stringify(dataForm.value),
      remark: props.remark
    }
    http({
      url: http.adornUrl('/sys/webConfig/save'),
      method: dataForm.value.id ? 'put' : 'post',
      data: http.adornData(paramsData)
    }).then(() => {
      ElMessage({
        message: $t('remindPop.success'),
        type: 'success',
        duration: 1500,
        customClass: 'zZindex'
      })
      updateWebConfigData()
    })
  })
}

const errorMsg = (message) => {
  ElMessage({
    message,
    type: 'error',
    duration: 1500,
    customClass: 'zZindex'
  })
}

const webConfigStore = useWebConfigStore()
// 更新网站配置信息
const updateWebConfigData = () => {
  if (props.paramKey !== 'PLATFROM_WEBSITE_CONFIG') {
    return false
  }
  http({
    url: http.adornUrl('/sys/webConfig/getActivity'),
    method: 'get'
  }).then(({ data }) => {
    webConfigStore.addData(data)
  })
}

</script>

<style lang="scss" scoped>
.component-add-or-update {
  &:deep(.el-form-item) {
    img {
      // 单独设置：网站配置图标按原比例缩放显示
      object-fit: scale-down !important;
    }
  }

  .el-form-item-tips {
    font-size: 12px;
    color: #999;
    line-height: 1em;
    padding-top: 8px;
    padding-bottom: 5px;
    width: 100%;
  }
  :deep(.el-input) {
    width: 60%;
  }
  :deep(.el-textarea) {
    width: 60%;
  }

  div :deep(.el-form-item__label) {
    min-width: 180px;
  }
}
</style>

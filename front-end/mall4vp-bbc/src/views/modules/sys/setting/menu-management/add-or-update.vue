<template>
  <el-dialog
    v-model="visible"
    :title="!dataForm.id ? $t('sysManagement.add') : $t('sysManagement.modify')"
    :close-on-click-modal="false"
    :before-close="beforeClose"
  >
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      :rules="dataRule"
      :label-width="$t('language')==='zh_CN'?'155px': 'auto'"
      @submit.prevent
      @keyup.enter="onSubmit()"
    >
      <el-form-item
        :label="$t('sysManagement.type')"
        prop="type"
      >
        <el-radio-group
          v-model="dataForm.type"
          @change="() => {dataForm.type === 0 ? dataForm.hidden = 0 : ''}"
        >
          <el-radio
            v-for="(type, index) in dataForm.typeList"
            :key="index"
            :label="index"
          >
            {{ type }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <!-- 语言选择框 -->
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
            :key="item.langId"
            :label="item.name"
            :value="item.lang"
          />
        </el-select>
      </el-form-item>
      <!-- 名称 -->
      <div
        v-for="(item,index) in menuLangList"
        :key="index"
      >
        <el-form-item
          class="name"
          :label="dataForm.typeList[dataForm.type] + (langItemList && langItemList.length > 1 ? '(' + item.langName + ')' : '')"
        >
          <el-input
            v-model.trim="item.name"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
      </div>
      <el-form-item :label="$t('sys.parentMenu')">
        <el-cascader
          v-model="selectedMenu"
          expand-trigger="hover"
          :options="menuList"
          :props="menuListTreeProps"
          change-on-select
          :clearable="true"
          @change="handleSelectMenuChange"
        />
      </el-form-item>
      <el-form-item
        v-if="dataForm.type === 1"
        :label="$t('sysManagement.menuRouter')"
        prop="url"
      >
        <el-input
          v-model="dataForm.url"
          :placeholder="$t('sysManagement.menuRouter')"
          maxlength="100"
          show-word-limit
        />
      </el-form-item>
      <el-form-item
        :label="$t('sys.isHidden')"
        prop="hidden"
      >
        <el-select
          v-model="dataForm.hidden"
          :placeholder="$t('user.pleaseSelect')"
          :disabled="dataForm.disabled"
          @change="() => {isRef = true}"
        >
          <el-option
            v-for="item in isHidden"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        v-if="dataForm.type !== 0"
        :label="$t('sys.authorization')"
        prop="perms"
      >
        <el-input
          v-model="dataForm.perms"
          :placeholder="$t('sys.separated') + 'user:list,user:create'"
          maxlength="250"
          show-word-limit
        />
      </el-form-item>
      <el-form-item
        v-if="dataForm.type !== 2"
        :label="$t('hotSearch.seq')"
        prop="orderNum"
      >
        <el-input-number
          v-model="dataForm.orderNum"
          controls-position="right"
          :min="0"
          :label="$t('hotSearch.seq')"
          maxlength="100"
          show-word-limit
        />
      </el-form-item>
      <el-form-item
        v-if="dataForm.type !== 2"
        :label="$t('sys.menuIcon')"
        prop="icon"
      >
        <el-input
          ref="iconInputRef"
          v-model="dataForm.icon"
          v-click-outside="onClickOutside"
          :placeholder="$t('sys.menuIconName')"
          clearable
        />
        <el-popover
          ref="iconListPopoverRef"
          width="390px"
          :virtual-ref="iconInputRef"
          placement="bottom-start"
          trigger="click"
          virtual-triggering
        >
          <el-button
            v-for="(item, index) in iconList"
            :key="index"
            style="padding: 8px; margin: 8px 0 0 8px"
            :class="{ 'is-active': item === dataForm.icon }"
            @click="iconActiveHandle(item)"
          >
            <svg-icon
              :icon-class="`${item}`"
              style="stroke: #8a979e !important; color: #8a979e"
            />
          </el-button>
        </el-popover>
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
import { treeDataTranslate, idList } from '@/utils/index.js'
import { ElMessage, ClickOutside as vClickOutside } from 'element-plus'

const emit = defineEmits(['refreshDataList'])
const visible = ref(false)
const isRef = ref(false)
const dataForm = ref({
  id: 0,
  type: 1,
  typeList: [$t('sys.catalog'), $t('sys.menu'), $t('sys.button')],
  parentId: 0,
  url: '',
  perms: '',
  orderNum: 0,
  icon: '',
  iconList: [],
  hidden: 0,
  menuLangList: [],
  disabled: false
})
const validateUrl = (rule, value, callback) => {
  if (dataForm.value.type === 1 && !/\S/.test(value)) {
    callback(new Error($t('sys.menuUrlNoNull')))
  } else {
    callback()
  }
}
const dataRule = reactive({
  url: [
    { validator: validateUrl, required: true, trigger: 'blur' }
  ],
  hidden: [
    { required: true, message: $t('sys.menuNameNoNull'), trigger: 'blur' }
  ]
})
const isSubmit = ref(false)
const menuList = ref([])
const selectedMenu = ref([])
const menuListTreeProps = reactive({
  value: 'menuId',
  label: 'name',
  checkStrictly: true
})
const isHidden = [
  {
    label: $t('sys.no'),
    value: 0
  },
  {

    label: $t('sys.yes'),
    value: 1
  }
]
// 语言列表
const langItemList = ref([])
const masterLangInfo = reactive({ name: '', lang: '' })
// 当前所选语言
const curLang = ref([])
const menuLangList = ref([])
const dataFormRef = ref(null)
watch(
  () => dataForm.value.type,
  () => {
    dataFormRef.value.clearValidate()
  }
)
onMounted(() => {
  onLoadIcons()
})

const iconList = []
// 加载图标
const onLoadIcons = () => {
  const icons = import.meta.glob('@/icons/svg/*.svg')
  for (const icon in icons) {
    const iconName = icon.split('/src/icons/svg/')[1].split('.svg')[0]
    iconList.push(iconName)
  }
}
const init = (id) => {
  getLangList()
  dataForm.value.id = id || 0
  isSubmit.value = false
  dataForm.value.name = null
  http({
    url: http.adornUrl('/sys/menu/list'),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    menuList.value = treeDataTranslate(data, 'menuId')
  }).then(() => {
    visible.value = true
    nextTick(() => {
      dataFormRef.value?.resetFields()
    })
  }).then(() => {
    if (dataForm.value.id) {
      // 修改
      http({
        url: http.adornUrl(`/sys/menu/info/${dataForm.value.id}`),
        method: 'get',
        params: http.adornParams()
      }).then(({ data }) => {
        dataForm.value.id = data.menuId
        dataForm.value.type = data.type
        dataForm.value.parentId = data.parentId
        dataForm.value.url = data.url
        dataForm.value.perms = data.perms
        dataForm.value.orderNum = data.orderNum
        dataForm.value.icon = data.icon
        dataForm.value.hidden = data.hidden ? 1 : 0
        dataForm.value.menuLangList = data.menuLangList
        menuLangList.value = data.menuLangList
        selectedMenu.value = idList(menuList.value, data.parentId, 'menuId', 'children').reverse()
        getLangList()
      })
    } else {
      selectedMenu.value = []
      getLangList()
    }
  })
}

const iconListPopoverRef = ref(null)
const iconInputRef = ref(null)
const onClickOutside = () => {
  unref(iconListPopoverRef.value).popperRef?.delayHide?.()
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
      if (dataForm.value.id) {
        const curLang = []
        const tempMenuLangList = []
        for (const item of langItemList.value) {
          const fd = menuLangList.value.find(it => it.lang === item.lang)
          if (fd) {
            fd.langName = item.name
            tempMenuLangList.push(fd)
            curLang.push(item.lang)
          }
        }
        menuLangList.value = tempMenuLangList
        selectLang(curLang)
      } else {
        // 设置默认主语言
        selectLang([info.lang])
      }
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
  const tempArr = menuLangList.value.filter(item => curLang.value.includes(item.lang))
  curLang.value.forEach((item, index) => {
    if (!tempArr.find(f => f.lang == item)) {
      const fd = langItemList.value.find(it => it.lang === item)
      if (fd) {
        tempArr.splice(index, 0, { langName: fd.name, lang: item, name: '' })
      }
    }
  })
  menuLangList.value = tempArr
}
const handleSelectMenuChange = (val) => {
  dataForm.value.parentId = val && val[val.length - 1]
}
const clear = () => {
  dataForm.value = {
    id: 0,
    type: 1,
    typeList: [$t('sys.catalog'), $t('sys.menu'), $t('sys.button')],
    name: '',
    nameEn: '',
    parentId: 0,
    url: '',
    perms: '',
    orderNum: 0,
    icon: '',
    iconList: [],
    hidden: 0
  }
}
// 图标选中
const iconActiveHandle = (iconName) => {
  dataForm.value.icon = iconName.split('icon-')[1]
}
const beforeClose = (done) => {
  clear()
  done()
}
const close = () => {
  clear()
  visible.value = false
}
// 表单提交
const onSubmit = () => {
  dataFormRef.value?.validate((valid) => {
    if (valid) {
      if (isSubmit.value) {
        return false
      }
      for (let i = 0; i < menuLangList.value.length; i++) {
        if (!menuLangList.value[i].name) {
          errorMsg(dataForm.value.typeList[dataForm.value.type] + $t('sys.menuNameNoNull'))
          return false
        }
      }

      isSubmit.value = true
      http({
        url: http.adornUrl('/sys/menu'),
        method: dataForm.value.id ? 'put' : 'post',
        data: http.adornData({
          menuId: dataForm.value.id || undefined,
          type: dataForm.value.type,
          parentId: dataForm.value.parentId || 0,
          url: dataForm.value.type === 1 ? dataForm.value.url : '',
          perms: dataForm.value.type === 0 ? '' : dataForm.value.perms,
          orderNum: dataForm.value.orderNum,
          icon: dataForm.value.type === 2 ? '' : dataForm.value.icon,
          hidden: dataForm.value.hidden,
          menuLangList: menuLangList.value
        })
      }).then(() => {
        ElMessage({
          message: $t('publics.operation'),
          type: 'success',
          duration: 1500,
          onClose: () => {
            visible.value = false
            emit('refreshDataList')
            clear()
            if (isRef.value) {
              location.reload()
            }
          }
        })
      }).catch((e) => {
        isSubmit.value = false
      })
    }
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
defineExpose({ init, close })
</script>

<style lang="scss" scoped>
.name{
  :deep(.el-form-item__label:before) {
    content: "*";
    color: var(--el-color-danger);
    margin-right: 4px;
  }
}

</style>

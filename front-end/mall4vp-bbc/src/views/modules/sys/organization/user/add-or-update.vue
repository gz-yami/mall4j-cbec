<template>
  <el-dialog
    v-model="visible"
    :title="!dataForm.id ? $t('sysManagement.add') : $t('sysManagement.modify')"
    :close-on-click-modal="false"
  >
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      :rules="dataRule"
      :label-width="$t('language')!=='zh_CN'?'160px': 'auto'"
      @submit.prevent
      @keyup.enter="onSubmit()"
    >
      <el-form-item
        :label="$t('sys.userName')"
        prop="userName"
      >
        <el-input
          v-model="dataForm.userName"
          v-input-rule
          :placeholder="$t('sys.userName')"
          :disabled="dataForm.id === 1"
        />
      </el-form-item>
      <el-form-item
        :label="$t('sys.nickName')"
        prop="nickName"
      >
        <el-input
          v-model="dataForm.nickName"
          v-input-rule
          :placeholder="$t('sys.nickName')"
        />
      </el-form-item>
      <el-form-item
        :label="$t('sys.password')"
        prop="password"
        :class="{ 'is-required': !dataForm.id }"
      >
        <el-input
          v-model="dataForm.password"
          v-input-rule
          maxlength="20"
          type="password"
          :placeholder="$t('sys.password')"
          show-password
        />
      </el-form-item>
      <el-form-item
        :label="$t('sys.confirmPassword')"
        prop="comfirmPassword"
        :class="{ 'is-required': !dataForm.id }"
      >
        <el-input
          v-model="dataForm.comfirmPassword"
          v-input-rule
          type="password"
          maxlength="20"
          :placeholder="$t('sys.confirmPassword')"
          show-password
        />
      </el-form-item>
      <el-form-item
        :label="$t('sys.email')"
        prop="email"
      >
        <el-input
          v-model="dataForm.email"
          maxlength="320"
          :placeholder="$t('sys.email')"
        />
      </el-form-item>
      <el-form-item
        :label="$t('publics.phoneNumber')"
        prop="mobile"
      >
        <el-input
          v-model="dataForm.mobile"
          maxlength="11"
          :placeholder="$t('publics.phoneNumber')"
        />
      </el-form-item>
      <el-form-item
        v-if="dataForm.id !== 1"
        :label="$t('sys.roles')"
        prop="roleIdList"
      >
        <el-checkbox-group v-model="dataForm.roleIdList">
          <el-checkbox
            v-for="role in roleList"
            :key="role.roleId"
            :label="role.roleId"
          >
            {{ role.roleName }}
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item
        v-if="dataForm.id !== 1 && dataForm.isSelf !== 1"
        :label="$t('product.status')"
        prop="status"
      >
        <el-radio-group v-model="dataForm.status">
          <el-radio :label="0">
            {{ $t('publics.disable') }}
          </el-radio>
          <el-radio :label="1">
            {{ $t('publics.normal') }}
          </el-radio>
        </el-radio-group>
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
import { isMobile, validPassword } from '@/utils/validate'
import { encrypt } from '@/utils/crypto'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['refreshDataList'])

const dataFormRef = ref(null)
const visible = ref(false)
const isSubmit = ref(false)
const roleList = ref([])
const dataForm = reactive({
  id: 0,
  userName: '',
  nickName: '',
  password: '',
  comfirmPassword: '',
  email: '',
  mobile: '',
  roleIdList: [],
  status: 1,
  isSelf: 0
})
const validatePassword = (rule, value, callback) => {
  if (!dataForm.id && !/\S/.test(value)) {
    callback(new Error($t('sys.passwordNoNull')))
  } else if (!(dataForm.id && !value) && !validPassword(value)) {
    callback(new Error($t('passwordVerification')))
  } else {
    callback()
  }
}
// eslint-disable-next-line no-unused-vars
const validateComfirmPassword = (rule, value, callback) => {
  if (!dataForm.id && !/\S/.test(value)) {
    callback(new Error($t('sys.confirmPassNoNull')))
  } else if (dataForm.password !== value) {
    callback(new Error($t('sys.passworldContrast')))
  } else {
    callback()
  }
}
// eslint-disable-next-line no-unused-vars
const validateEmail = (rule, value, callback) => {
  if (!isEmail(value)) {
    callback(new Error($t('sys.emailaError')))
  } else {
    callback()
  }
}
// eslint-disable-next-line no-unused-vars
const validateMobile = (rule, value, callback) => {
  if (dataForm.mobile) {
    const mobile = /^(?:(?:\+|00)86)?1\d{2}([\d*]{4})\d{4}$/
    if (mobile.test(value)) {
      callback()
    } else {
      callback(new Error($t('sys.mobilePhoneError')))
    }
  } else if (!isMobile(value)) {
    callback(new Error($t('sys.mobilePhoneError')))
  } else {
    callback()
  }
}
const dataRule = {
  userName: [
    { required: true, message: $t('sys.userNameNoNull'), trigger: 'blur' },
    { min: 2, max: 20, message: $t('sys.userNameBetween'), trigger: 'blur' }
  ],
  nickName: [
    { required: true, message: $t('sys.nickNameNoNull'), trigger: 'blur' },
    { min: 2, max: 20, message: $t('sys.nickNameBetween'), trigger: 'blur' }
  ],
  password: [
    { validator: validatePassword, trigger: 'blur' }
  ],
  comfirmPassword: [
    { validator: validateComfirmPassword, trigger: 'blur' }
  ],
  email: [
    { required: true, message: $t('sys.emailaNoNull'), trigger: 'blur' },
    { validator: validateEmail, trigger: 'blur' }
  ],
  mobile: [
    { required: true, message: $t('sys.mobilePhoneNoNull'), trigger: 'blur' },
    { validator: validateMobile, trigger: 'blur' }
  ]
}

const init = (id) => {
  dataForm.id = id || 0
  isSubmit.value = false
  http({
    url: http.adornUrl('/sys/role/list'),
    method: 'get',
    params: http.adornParams()
  }).then(({ data }) => {
    roleList.value = data
  }).then(() => {
    visible.value = true
    nextTick(() => {
      dataFormRef.value?.resetFields()
    })
  }).then(() => {
    if (dataForm.id) {
      http({
        url: http.adornUrl(`/sys/user/info/${dataForm.id}`),
        method: 'get',
        params: http.adornParams()
      }).then(({ data }) => {
        dataForm.userName = data.username
        dataForm.nickName = data.nickName
        dataForm.email = data.email
        dataForm.mobile = data.mobile
        dataForm.roleIdList = data.roleIdList
        dataForm.status = data.status
        dataForm.isSelf = data.isSelf
      })
    }
  })
}
// 表单提交
const onSubmit = () => {
  dataFormRef.value?.validate((valid) => {
    if (valid) {
      if (isSubmit.value) {
        return false
      }
      isSubmit.value = true
      http({
        url: http.adornUrl('/sys/user'),
        method: dataForm.id ? 'put' : 'post',
        data: http.adornData({
          userId: dataForm.id || undefined,
          username: dataForm.userName,
          nickName: dataForm.nickName,
          password: encrypt(dataForm.password),
          email: dataForm.email,
          mobile: dataForm.mobile,
          status: dataForm.status,
          roleIdList: dataForm.roleIdList
        })
      }).then(() => {
        ElMessage({
          message: $t('publics.operation'),
          type: 'success',
          duration: 1500,
          onClose: () => {
            visible.value = false
            emit('refreshDataList')
          }
        })
      }).catch((e) => {
        isSubmit.value = false
      })
    }
  })
}
defineExpose({ init })
</script>

<style scoped lang="scss">
:deep(.el-form) {
  .el-form-item__label {
    word-wrap: break-word;
    word-break: break-word;
  }
}
</style>

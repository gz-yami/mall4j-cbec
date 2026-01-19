<template>
  <el-dialog
    v-model="visible"
    :title="$t('homes.updatePwd')"
    :append-to-body="true"
  >
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      :rules="dataRule"
      :label-width="$t('language')==='zh_CN'?'80px': 'auto'"
      @keyup.enter="onSubmit(dataFormRef)"
    >
      <el-form-item :label="$t('homes.userName')">
        <span>{{ userName }}</span>
      </el-form-item>
      <el-form-item
        :label="$t('sys.oldPassword')"
        prop="password"
      >
        <el-input
          v-model="dataForm.password"
          v-input-rule
          maxlength="20"
          type="password"
          show-password
        />
      </el-form-item>
      <el-form-item
        :label="$t('sys.newPassword')"
        prop="newPassword"
      >
        <el-input
          v-model="dataForm.newPassword"
          v-input-rule
          maxlength="20"
          type="password"
          show-password
        />
      </el-form-item>
      <el-form-item
        :label="$t('homes.confirmPassword')"
        prop="confirmPassword"
      >
        <el-input
          v-model="dataForm.confirmPassword"
          v-input-rule
          maxlength="20"
          type="password"
          show-password
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">{{ $t('remindPop.cancel') }}</el-button>
        <el-button
          type="primary"
          @click="onSubmit(dataFormRef)"
        >{{ $t('remindPop.confirm') }}</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { validPassword } from '@/utils/validate'
const visible = ref(false)
const dataForm = reactive({
  password: '',
  newPassword: '',
  confirmPassword: ''
})
const dataFormRef = ref()
const init = () => {
  visible.value = true
  nextTick(() => {
    dataFormRef.value?.resetFields()
  })
}
// eslint-disable-next-line no-unused-vars
const validateConfirmPassword = (rule, value, callback) => {
  if (dataForm.newPassword !== value) {
    callback(new Error($t('homes.istrue')))
  } else {
    callback()
  }
}
// eslint-disable-next-line no-unused-vars
const validatePassword = (rule, value, callback) => {
  if (validPassword(value)) {
    callback()
  } else {
    callback(new Error($t('passwordVerification')))
  }
}
const dataRule = reactive({
  password: [{ required: true, message: $t('sys.oldPwdNotNull'), trigger: 'blur' }],
  newPassword: [
    { required: true, message: $t('sys.newPwdNotNull'), trigger: 'blur' },
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: $t('sys.confirmPassNoNull'), trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
})
const userStore = useUserStore()
const userName = computed(() => userStore.name)
const router = useRouter()
const onSubmit = async formEl => {
  if (!formEl) return
  await formEl.validate(valid => {
    if (valid) {
      http({
        url: http.adornUrl('/sys/user/password'),
        method: 'post',
        data: http.adornData({
          password: encrypt(dataForm.password),
          newPassword: encrypt(dataForm.newPassword)
        })
      }).then(() => {
        ElMessage({
          message: $t('publics.operation'),
          type: 'success',
          duration: 1500,
          onClose: () => {
            visible.value = false
            nextTick(() => {
              clearLoginInfo()
              router.replace({ name: 'login' })
            })
          }
        })
      })
    }
  })
}

defineExpose({ init })
</script>

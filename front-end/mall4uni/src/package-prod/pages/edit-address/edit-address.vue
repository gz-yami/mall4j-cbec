<template>
  <view class="Mall4j container page-edit-address">
    <!--input列表 -->
    <view class="edit-addr-box">
      <view class="addr-item">
        <text class="label">
          {{ $t('recipient') }}
        </text>
        <view class="input-box">
          <view :class="['input-con', showError && !receiver ? 'addr-error' : '']">
            <input
              v-model.trim="receiver"
              type="text"
              maxlength="20"
              placeholder-class="inp-palcehoder"
              :placeholder="$t('recipientName')"
              @focus="handleClearBtn('receiverClearBtn', true)"
              @blur="handleClearBtn('receiverClearBtn', false)"
            >
            <image
              v-show="receiverClearBtn"
              src="../../static/images/icon/close.png"
              style="width:32rpx;height:32rpx"
              @tap="receiver = ''"
            />
          </view>
          <view
            v-if="showError && !receiver"
            class="error-text"
          >
            {{ $t('recipientNameTips') }}
          </view>
        </view>
      </view>
      <view class="addr-item">
        <text class="label">
          {{ $t('phoneNumber') }}
        </text>
        <view class="input-box">
          <view :class="['input-con', showError && !mobile ? 'addr-error' : '']">
            <input
              v-model="mobile"
              maxlength="13"
              placeholder-class="inp-palcehoder"
              :placeholder="$t('enterMobileNumber')"
              @input="formatMobile(mobile)"
              @pause="formatMobile(mobile)"
              @focus="handleClearBtn('mobileClearBtn', true)"
              @blur="handleClearBtn('mobileClearBtn', false)"
            >
            <image
              v-show="mobileClearBtn"
              src="../../static/images/icon/close.png"
              style="width:32rpx;height:32rpx"
              @tap="mobile = ''"
            />
          </view>
          <view
            v-if="showError && !mobile"
            class="error-text"
          >
            {{ $t('mobileTips') }}
          </view>
        </view>
      </view>
      <view class="addr-item">
        <text class="label">
          {{ $t('area') }}
        </text>
        <view class="input-box">
          <view
            :class="['input-con', showError && !province ? 'addr-error' : '']"
            @tap="openSelectAddrPup"
          >
            <view class="area-box">
              <text
                v-if="province"
                class="text"
              >
                {{ province }}
                <span v-if="city">
                  / {{ city }}
                </span>
                <span v-if="area">
                  / {{ area }}
                </span>
              </text>
              <text
                v-else
                class="inp-palcehoder"
              >
                {{ $t('selectProvinceCity') }}
              </text>
            </view>
            <view class="icon-right">
              <image src="../../static/images/icon/arrow-right.png" />
            </view>
          </view>
          <view
            v-if="showError && !province"
            class="error-text"
            @tap="openSelectAddrPup"
          >
            {{ $t('areaTips') }}
          </view>
        </view>
      </view>
      <view class="addr-item">
        <text class="label">
          {{ $t('detailedAddress') }}
        </text>
        <view class="input-box">
          <view :class="['input-con', showError && !addr ? 'addr-error' : '']">
            <textarea
              v-model.trim="addr"
              data-type="username"
              placeholder-class="inp-palcehoder"
              auto-height
              :placeholder="$t('addressHint')"
              @focus="handleClearBtn('addrClearBtn', true)"
              @blur="handleClearBtn('addrClearBtn', false)"
            />
            <image
              v-show="addrClearBtn"
              class="addr-clear-btn"
              src="../../static/images/icon/close.png"
              @tap="addr = ''"
            />
          </view>
          <view
            v-if="showError && !addr"
            class="error-text"
          >
            {{ $t('addressTips') }}
          </view>
        </view>
      </view>
      <view class="addr-item">
        <text class="label">
          {{ $t('postalCode') }}
        </text>
        <view class="input-box">
          <view :class="['input-con', showError && !postCode ? 'addr-error' : '']">
            <input
              v-model.trim="postCode"
              type="text"
              maxlength="15"
              placeholder-class="inp-palcehoder"
              :placeholder="$t('enterCorrectPostalCode')"
              @focus="handleClearBtn('postalCodeClearBtn', true)"
              @blur="handleClearBtn('postalCodeClearBtn', false)"
            >
            <image
              v-show="postalCodeClearBtn"
              src="../../static/images/icon/close.png"
              style="width:32rpx;height:32rpx"
              @tap="postCode = ''"
            />
          </view>
          <view
            v-if="showError && !postCode"
            class="error-text"
          >
            {{ $t('enterCorrectPostalCode') }}
          </view>
        </view>
      </view>
      <view
        v-if="!isEditDefAddr"
        class="addr-item-def"
      >
        <text class="label-def">
          {{ $t('setDefaultAddr') }}
        </text>
        <switch
          class="def-switch"
          style="transform:scale(0.7)"
          :checked="commonAddr === 1"
          color="#F81A1A"
          @change="commonAddrChange"
        />
      </view>
    </view>

    <!-- 功能按钮 -->
    <view class="btn-box">
      <view
        class="save-btn"
        @tap="onSaveAddr"
      >
        {{ $t('save') }}
      </view>
    </view>
    <!-- end 功能按钮 -->

    <!-- 选择地址组件 -->
    <address-select
      v-model="isShowSelectAddrPopup"
      :select-ids="[provinceId, cityId, areaId]"
      @select-addr="selectAddr"
    />
  </view>
</template>

<script setup>
import util from '../../../utils/util'
import AddressSelect from '../../components/address-select/index.vue'

const Data = reactive({
  isShowSelectAddrPopup: false,
  provArray: [],
  cityArray: [],
  areaArray: [],
  province: '',
  city: '',
  area: '',
  provinceId: 0,
  cityId: 0,
  areaId: 0,
  receiver: '',
  postCode: '',
  receiverClearBtn: false,
  postalCodeClearBtn: false,
  mobile: '',
  mobileClearBtn: false,
  addr: '',
  addrClearBtn: false,
  addrId: 0,
  commonAddr: 0, // 1默认
  lat: '',
  lng: '',
  isEdit: false,
  showError: false,
  isEditDefAddr: true // 当前修改的地址是否为默认地址
})

const { isShowSelectAddrPopup, province, city, area, provinceId, cityId, areaId, receiver, postCode, receiverClearBtn, postalCodeClearBtn, mobile, mobileClearBtn, addr, addrClearBtn, commonAddr, showError, isEditDefAddr } = toRefs(Data)

let addrId = null
onLoad((options) => {
  Data.isEdit = !!options.addrId
  if (options.addrId) {
    addrId = options.addrId
    // 获取地址详情
    getAddressInfoById(options.addrId)
  }
})
onShow(() => {
  // 加载导航标题
  uni.setNavigationBarTitle({
    title: Data.isEdit ? $t('editShippingAddress') : $t('addNewAddress')
  })
})

// 处理清除按钮显隐
const handleClearBtn = (btnAttr, isShow) => {
  if (!isShow) {
    setTimeout(() => {
      Data[btnAttr] = isShow
    }, 100)
  } else {
    Data[btnAttr] = isShow
  }
}

// 格式化手机输入
const formatMobile = (value) => {
  value = value.replace(/\D/g, '').substr(0, 11) // 不允许输入非数字字符，超过11位数字截取前11位
  const len = value.length
  if (len > 3 && len < 8) {
    value = value.replace(/^(\d{3})/g, '$1 ')
  } else if (len >= 8) {
    value = value.replace(/^(\d{3})(\d{4})/g, '$1 $2 ')
  }
  nextTick(() => {
    Data.mobile = value
  })
}

// 选择地址前的处理
const openSelectAddrPup = () => {
  Data.isShowSelectAddrPopup = true
}

// 选择地址确认
const selectAddr = (selInfo) => {
  Data.province = selInfo.province
  Data.city = selInfo.city
  Data.area = selInfo.area
  Data.provinceId = selInfo.provinceId
  Data.cityId = selInfo.cityId
  Data.areaId = selInfo.areaId
  Data.isShowSelectAddrPopup = false
}

const commonAddrChange = (e) => {
  Data.commonAddr = e.detail.value ? 1 : 0
}

/**
* 获取地址详情
*/
const getAddressInfoById = (addrId) => {
  http.request({
    url: '/p/address/addrInfo/' + addrId,
    method: 'GET',
    data: {}
  }).then(({ data }) => {
    if (data) {
      Data.provinceId = data.provinceId
      Data.cityId = data.cityId
      Data.areaId = data.areaId
      Data.province = data.province
      Data.city = data.city
      Data.area = data.area
      Data.receiver = data.receiver.trim()
      Data.postCode = data.postCode?.trim()
      Data.mobile = data.mobile.trim()
      Data.addr = data.addr.trim()
      Data.addrId = addrId
      Data.lat = data.lat
      Data.lng = data.lng
      Data.commonAddr = data.commonAddr
      Data.isEditDefAddr = !!data.commonAddr
      formatMobile(data.mobile)
    }
  })
}

/**
* 保存地址
*/
const onSaveAddr = util.debounce(() => {
  Data.showError = true
  const mobile = Data.mobile.replace(/\s/g, '')
  if (!Data.receiver || !mobile || !Data.province || !Data.addr || !Data.postCode) {
    return
  }
  if (Data.addr.length < 5) {
    uni.showToast({
      title: $t('selectDetailedAddress'),
      icon: 'none'
    })
    return
  }

  // 添加或修改地址
  let url = '/p/address/addAddr'
  let method = 'POST'

  if (Data.addrId) {
    url = '/p/address/updateAddr'
    method = 'PUT'
  }

  http.request({
    url,
    method,
    data: {
      addrId: Data.addrId,
      receiver: Data.receiver,
      postCode: Data.postCode,
      mobile,
      addr: Data.addr,
      province: Data.province,
      provinceId: Data.provinceId,
      city: Data.city,
      cityId: Data.cityId,
      areaId: Data.areaId,
      area: Data.area,
      userType: 0,
      commonAddr: Data.commonAddr,
      lat: Data.lat, // 经度
      lng: Data.lng // 纬度
    }
  }).then(({
    data
  }) => {
    uni.showToast({
      title: data,
      duration: 1200,
      icon: 'none'
    })
    setTimeout(() => {
      // 普通h5返回上一页
      // #ifdef H5
      history.back()
      // #endif

    }, 1200)
  })
}, 500)
</script>

<style lang="scss" scoped>
@use 'edit-address';
</style>

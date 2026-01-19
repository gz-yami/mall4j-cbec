<template>
  <!-- 02、编辑商品信息 -->
  <div class="page-posting-edit-product">
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      :label-width="lang !== 'zh_CN'? 'auto':'130px'"
      :rules="dataRule"
      label-position="right"
      class="posting-edit-product-form"
      @submit.prevent
    >
      <!-- 基本信息 -->
      <div class="edit-prod-content prod-basic-info">
        <div class="prod-title-row">
          <span>{{ $t("shopProcess.basicInfo") }}</span>
        </div>
        <div class="basic-info-content prod-con">
          <!-- 已选分类 -->
          <el-form-item
            :label="$t('product.selectedCategories')"
            class="selected-category"
          >
            <div class="category-sel">
              <div class="category-con">
                <div class="cur-sel">
                  <span class="weak-text">{{ $t("product.platformCategories") }}：</span>
                  <span :class="dataForm.prodId?'disable':''">{{ platCategoryName }}</span>
                </div>
                <div
                  class="default-btn text-btn"
                  @click="changeCategory(1)"
                >
                  {{ $t("resource.update") }}
                </div>
              </div>
            </div>
          </el-form-item>
          <!-- 选择语言 -->
          <el-form-item :label="$t('product.selectLanguage')">
            <el-select
              v-model="curLang"
              multiple
              :placeholder="$t('tip.select')"
              class="select-lang"
              @change="selectLang"
            >
              <el-option
                v-for="item in langItemList"
                :key="item.lang"
                :label="item.name"
                :value="item.lang"
              />
            </el-select>
            <div class="el-form-item-tips">
              {{ $t("product.postProductTips2") }}
            </div>
          </el-form-item>
          <!-- 商品名称 -->
          <div
            v-if="dataForm.prodLangList.length"
            class="prod-name-box"
          >
            <template
              v-for="(item,index) in dataForm.prodLangList"
              :key="index"
            >
              <el-form-item
                v-if="curLang.includes(item.lang)"
                :label="$t('product.prodName') + (langItemList.length === 1 ? '' : `(${item.langName})`)"
                class="prod-name-con"
                :required="true"
              >
                <el-input
                  v-model="item.prodName"
                  maxlength="120"
                  class="shop-input"
                />
                <div class="el-form-item-tips">
                  {{ $t('product.postProductTips3') }}
                </div>
              </el-form-item>
            </template>
          </div>
          <!-- 商品卖点 -->
          <div
            v-if="dataForm.prodLangList.length"
            class="prod-name-box"
          >
            <template
              v-for="(item,index) in dataForm.prodLangList"
              :key="index"
            >
              <el-form-item
                v-if="curLang.includes(item.lang)"
                :label="$t('product.productSellingPoints') + (langItemList.length === 1 ? '' : `(${item.langName})`)"
                class="prod-name-con"
              >
                <el-input
                  v-model="item.brief"
                  maxlength="120"
                  class="shop-input"
                />
                <div class="el-form-item-tips">
                  {{ $t('product.postProductTips4') }}
                </div>
              </el-form-item>
            </template>
          </div>
          <!-- 商品图片 -->
          <el-form-item
            :label="$t('product.pic')"
            prop="imgs"
            class="prod-img-box"
          >
            <mul-pic-upload
              v-model="dataForm.imgs"
              @update:model-value="imgsChange"
            />
            <div class="el-form-item-tips">
              {{ $t("product.postProductTips5") }}
            </div>
          </el-form-item>
          <!-- 是否活动商品 -->
          <el-form-item
            v-if="dataForm.mold!==1 && dataForm.mold !== 2"
            :label="$t('product.combinedProducts')"
          >
            <el-tooltip
              :disabled="!dataForm.prodId || dataForm.prodId === ''"
              class="item"
              effect="light"
              :content="$t('product.postProductTips22')"
              placement="top"
            >
              <el-radio-group
                v-model="dataForm.prodType"
                :disabled="!!dataForm.prodId && dataForm.prodId !== ''"
                @change="handleProdTpyeChange"
              >
                <el-radio :label="dataForm.prodType>0&&dataForm.prodType!==5?dataForm.prodType:0">
                  {{ $t("publics.no") }}
                </el-radio>
                <el-radio :label="5">
                  {{ $t("publics.yes") }}
                </el-radio>
              </el-radio-group>
            </el-tooltip>
            <div class="el-form-item-tips">
              {{ $t("product.notAvailableSeparatePurchase") }}
            </div>
          </el-form-item>
          <!-- 是否开启预售 -->
          <el-form-item
            v-show="dataForm.prodType !== 5 && dataForm.mold !== 1 && dataForm.mold !== 2"
            :label="$t('product.whetPreSale')"
          >
            <el-radio-group v-model="dataForm.preSellStatus">
              <el-radio :label="0">
                {{ $t("station.close") }}
              </el-radio>
              <el-radio :label="1">
                {{ $t("groups.turnOn") }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
          <!-- 预售发货时间 -->
          <el-form-item
            v-show="dataForm.preSellStatus === 1 && dataForm.prodType !== 5"
            :label="$t('product.preSaleTime')"
            prop="preSellTime"
            class="pre-sell-time"
          >
            <el-date-picker
              v-model="dataForm.preSellTime"
              type="date"
              :placeholder="$t('product.choosengDate')"
              value-format="YYYY-MM-DD HH:mm:ss"
              format="YYYY-MM-DD"
              :disabled-date="PickerOptions.disabledDate"
            />
          </el-form-item>
        </div>
      </div>

      <!-- 规格库存 -->
      <div class="edit-prod-content">
        <div class="prod-title-row">
          <span>{{ $t("product.specStock") }}</span>
        </div>
        <div class="spec-stock-content prod-con">
          <sku-tag
            ref="skuTagRef"
            :sku-list="dataForm.skuList"
            @change="skuTagChangeSkuHandler"
            @change-sku-img="changeSkuImgHandler"
            @clear-sku-img="clearSkuImg"
            @reset-init="resetInit"
          />

          <sku-table
            ref="skuTableRef"
            v-model="dataForm.skuList"
            v-model:prod-name-cn="dataForm.prodNameCn"
            v-model:prod-name-en="dataForm.prodNameEn"
            :mold="dataForm.mold"
            :delivery-mode="dataForm.deliveryMode"
            :stock-point-info="stockPointInfo"
            @on-change-data="skuListHandler"
          />

          <div class="total-stock">
            <el-form-item
              :label="$t('product.totalInventory')"
              :label-width="$t('language') !== 'zh_CN'?'auto':'130px'"
            >
              <input
                v-model="totalStocks"
                disabled
              >
              <div class="el-form-item-tips">
                {{ $t("product.postProductTips7") }}
              </div>
            </el-form-item>
          </div>
        </div>
      </div>

      <!-- 运费设置 -->
      <div
        v-if="dataForm.mold !== 1 && dataForm.prodType !== 5"
        class="edit-prod-content"
      >
        <div class="prod-title-row">
          <span>{{ $t("product.shippinngs") }}</span>
        </div>
        <div class="shipping-setup prod-con">
          <!-- 配送方式 -->
          <el-form-item
            :label-width="$t('language') !== 'zh_CN'?'auto':''"
            :label="$t('order.delType')"
            class="options-box"
            prop="deliveryMode"
          >
            <div class="fl-row">
              <el-checkbox
                v-model="dataForm.deliveryMode.hasShopDelivery"
                disabled
              >
                {{
                  $t("product.ExpressDistribution")
                }}
              </el-checkbox>
              <!-- 用户自提 -->
              <!--              <el-checkbox-->
              <!--                v-model="dataForm.deliveryMode.hasUserPickUp"-->
              <!--                class="delType-text"-->
              <!--              >-->
              <!--                {{-->
              <!--                  $t("product.userMention")-->
              <!--                }}-->
              <!--                <el-tooltip-->
              <!--                  class="box-item"-->
              <!--                  effect="dark"-->
              <!--                  :content="$t('prod.setStockQuantity')"-->
              <!--                  placement="right"-->
              <!--                >-->
              <!--                  <el-icon><QuestionFilled /></el-icon>-->
              <!--                </el-tooltip>-->
              <!--              </el-checkbox>-->
              <!-- 用户自提 end -->
              <!-- 同城配送 -->
              <!--              <el-checkbox-->
              <!--                v-model="dataForm.deliveryMode.hasCityDelivery"-->
              <!--                :disabled="sameCityStatus === 0"-->
              <!--                class="delType-text"-->
              <!--              >-->
              <!--                {{ $t("order.sameCityDelivery") }}-->
              <!--              </el-checkbox>-->
            </div>
            <!--            <div class="el-form-item-tips">-->
            <!--              {{ $t("product.postProductTips8") }}-->
            <!--            </div>-->
            <!-- 同城配送 end -->
          </el-form-item>
          <!-- 运费模板 -->
          <el-form-item
            :label-width="$t('language') !== 'zh_CN'?'auto':''"
            :label="$t('product.shippinngs')"
          >
            <div class="fl-row">
              <el-radio
                v-model="dataForm.tableRate"
                :label="0"
              >
                {{ $t('product.freeShipping') }}
              </el-radio>
              <el-radio
                v-model="dataForm.tableRate"
                :label="-1"
                @change="freight"
              >
                {{ $t('product.fixedFreight') }}
              </el-radio>
              <el-radio
                v-model="dataForm.tableRate"
                :label="1"
              >
                {{ $t('product.freTempl') }}
              </el-radio>
            </div>
          </el-form-item>

          <!-- 运费模板 -->
          <el-form-item
            v-if="dataForm.tableRate>0"
            :label-width="$t('language') !== 'zh_CN'?'auto':''"
            :label="$t('product.freTempl')"
            prop="deliveryTemplateId"
          >
            <div class="fl-row">
              <el-select
                v-model="dataForm.deliveryTemplateId"
                :placeholder="$t('tip.select')"
              >
                <el-option
                  v-for="transport in transportList"
                  :key="transport.transportId"
                  :label="transport.transName"
                  :value="transport.transportId"
                />
              </el-select>
              <!--新建/刷新-->
              <div class="create-refresh-btn">
                <div
                  class="default-btn text-btn"
                  @click="getTransportList"
                >
                  {{ $t('admin.refresh') }}
                </div>
                <el-divider direction="vertical" />
                <div
                  class="default-btn text-btn"
                  @click.stop="createTransportTemplate()"
                >
                  {{ $t('admin.newConstruction') }}
                </div>
              </div>
            </div>
            <div class="el-form-item-tips">
              {{ $t("product.postProductTips9") }}
            </div>
          </el-form-item>
          <el-form-item
            v-if="dataForm.tableRate===-1"
            :label-width="$t('language') !== 'zh_CN'?'auto':''"
            :label="$t('product.fixedFreight')"
            prop="deliveryAmount"
          >
            <div class="freight">
              <el-input
                v-model="dataForm.deliveryAmount"
                type="number"
                :min="0.01"
                maxlength="10"
                :placeholder="$t('product.pleaseEnterTheAmount')"
                @blur="handleInputValue(dataForm.deliveryAmount,'deliveryAmount',0.01,9999)"
              />
            </div>
          </el-form-item>
        </div>
      </div>

      <!-- 参数设置 -->
      <div class="edit-prod-content">
        <div class="prod-title-row">
          <span>{{ $t('product.parameterSetting') }}</span>
        </div>
        <div
          v-if="dataForm.prodParameterList.length > 0"
          class="params-box prod-con"
        >
          <el-form-item
            v-for="(item,index) in dataForm.prodParameterList"
            :key="'param_'+index"
            :label="$t('product.parameter')+ (index+1)"
          >
            <div
              v-for="(langItem,langInx) in item.prodParameterLangList"
              :key="'param_lang_'+langInx"
              class="zh-input"
            >
              <el-input
                v-model.trim="langItem.parameterKey"
                class="input"
                maxlength="10"
              >
                <template #prepend>
                  {{ $t('sys.parameteName') + ( langItemList.length === 1 ? '' : `(${langItem.langName})`) }}
                </template>
              </el-input>
              <el-input
                v-model.trim="langItem.parameterValue"
                class="input"
                maxlength="20"
              >
                <template #prepend>
                  {{ $t('sys.parameterValue') + ( langItemList.length === 1 ? '' : `(${langItem.langName})`) }}
                </template>
              </el-input>
              <div class="params-tips">
                {{ $t('sys.parameteNameAndParameterValue') }}
              </div>
            </div>
            <div class="btn-box">
              <div
                class="default-btn text-btn"
                @click="parameterDeltete(index)"
              >
                {{ $t('text.delBtn') }}
              </div>
              <div
                v-if="index === dataForm.prodParameterList.length-1"
                class="line"
              />
              <div
                v-if="index === dataForm.prodParameterList.length-1"
                class="default-btn text-btn"
                @click="parameterInsert"
              >
                {{ $t('shopProcess.add') }}
              </div>
            </div>
          </el-form-item>
        </div>
        <el-form-item
          v-else
          :label-width="lang !== 'zh_CN'? '200px':'130px'"
        >
          <div
            class="default-btn text-btn"
            @click="parameterInsert"
          >
            {{ $t('shopProcess.add') }}
          </div>
        </el-form-item>
      </div>
      <!-- 参数设置end -->

      <!-- 其他设置（虚拟商品才显示这个其他设置） -->
      <div
        v-if="dataForm.mold === 1"
        class="edit-prod-content"
      >
        <div class="prod-title-row">
          <span>{{ $t("product.otherSettings") }}</span>
        </div>
        <div class="other-settings prod-con">
          <!-- 核销次数 -->
          <el-form-item
            :label="$t('product.numberOfWriteOffs')"
            class="options-box"
          >
            <!-- -1.多次核销 0.无需核销 1.单次核销 -->
            <el-radio-group v-model="dataForm.writeOffNum">
              <el-radio :label="0">
                {{ $t("product.noWriteOffRequired") }}
              </el-radio>
              <!-- 单次核销 -->
              <!--              <el-radio :label="1">-->
              <!--                {{ $t("product.singleWriteOff") }}-->
              <!--              </el-radio>-->
              <!-- 多次核销 -->
              <!--              <el-radio :label="-1">-->
              <!--                {{ $t("product.multipleWriteOffs") }}-->
              <!--              </el-radio>-->
            </el-radio-group>
            <!-- 核销提示语-->
            <!--            <div class="el-form-item-tips">-->
            <!--              {{ $t("product.postProductTips10") }}-->
            <!--            </div>-->
          </el-form-item>
          <el-form-item
            v-if="dataForm.writeOffNum === -1"
            :label="$t('product.expiryNumberOfWriteOffs')"
            class="options-box expiry-date"
          >
            <el-radio-group
              v-model="writeOffMultipleCountSelect"
              @change="changeWriteOffMultipleCount"
            >
              <!-- 长期有效 -->
              <el-radio :label="-1">
                {{ $t("product.unlimitedTime") }}
              </el-radio>
              <!-- N天内有效 -->
              <el-radio :label="2">
                {{ $t("product.mostWriteOffs") }}
                <input
                  v-model="writeOffMultipleCount"
                  type="number"
                  class="native-input-style"
                  :disabled="writeOffMultipleCountSelect === -1"
                  :max="9999"
                  :min="2"
                  @change="changeWriteOffMultipleCountIn()"
                >
                {{ $t("user.bout") }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
          <!-- writeOffTime核销有效期（选择“无需核销”，不需要选择核销有效期） -1.长期有效 0.自定义 1.当天 x.x天内 -->
          <el-form-item
            v-if="dataForm.writeOffNum !== 0"
            :label="$t('product.expiryDate')"
            class="options-box expiry-date"
            prop="writeOffEnd"
          >
            <el-radio-group
              v-model="dataForm.writeOffTime"
              class="expiry-date-box"
              @change="changeWriteOffTime"
            >
              <div class="radio-item">
                <!-- 长期有效 -->
                <el-radio :label="-1">
                  {{ $t("product.longTermValidity") }}
                </el-radio>
              </div>
              <div class="radio-item">
                <!-- 当天有效 -->
                <el-radio :label="1">
                  {{ $t("product.validOnTheSameDay") }}<span class="weak-text">{{ $t("product.beforeTime") }}</span>
                </el-radio>
              </div>
              <div class="radio-item">
                <!-- N天内有效 -->
                <el-radio :label="2">
                  {{ $t("product.afterPurchase") }}
                  <input
                    v-model="validDays"
                    type="number"
                    class="native-input-style"
                    :disabled="dataForm.writeOffTime !== 2"
                    :max="9999"
                    :min="2"
                    @blur="getValidDays(validDays)"
                  >
                  {{ $t("product.validDays") }}
                </el-radio>
              </div>
              <div class="radio-item date-picker">
                <!-- 自定义（N 至 M 内有效） -->
                <el-radio :label="0">
                  {{ $t("product.validFrom") }}
                  <el-date-picker
                    v-model="dataForm.writeOffStart"
                    type="datetime"
                    :placeholder="$t('product.startDate')"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    :disabled-date="PickerOptions.disabledDate"
                    :disabled="dataForm.writeOffTime !== 0"
                    @change="changeDate(dataForm.writeOffStart, 0)"
                  />
                  {{ $t("product.to") }}
                  <el-date-picker
                    v-model="dataForm.writeOffEnd"
                    type="datetime"
                    :placeholder="$t('product.endDate')"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    :disabled-date="PickerOptions.disabledDate"
                    :disabled="dataForm.writeOffTime !== 0"
                    @change="changeDate(dataForm.writeOffStart, 1)"
                  />
                  <span v-if="lang === 'zh_CN'">内有效</span>
                </el-radio>
              </div>
            </el-radio-group>
          </el-form-item>
          <!-- 用户留言 -->
          <el-form-item
            :label="$t('product.userMessage')"
            class="user-message"
          >
            <div
              v-for="(item, index) in dataForm.virtualRemark"
              :key="index"
              class="msg-int-box"
            >
              <input
                v-model="dataForm.virtualRemark[index].name"
                class="native-input-style"
                type="text"
                maxlength="10"
                @blur="changeMsgInput(item.name, index)"
              >
              <el-checkbox
                v-model="item.isRequired"
                class="required-checkbox"
              >
                {{ $t("product.requiredField") }}
              </el-checkbox>
              <!--删除/添加-->
              <div class="create-refresh-btn">
                <div
                  class="default-btn text-btn"
                  @click="deleteUserMessage(index)"
                >
                  {{ $t('resource.Delete') }}
                </div>
              </div>
            </div>
            <div
              v-if="dataForm.virtualRemark.length < 10"
              class="add-field default-btn text-btn"
              @click="addUserMessage()"
            >
              <el-icon><Plus /></el-icon>{{ $t("product.addField") }}
            </div>
            <div class="el-form-item-tips">
              {{ $t('product.msgFieldTips') }}
            </div>
          </el-form-item>
          <!-- 售后服务 -->
          <el-form-item
            :label="$t('product.afterSalesService')"
            class="options-box"
          >
            <el-radio-group v-model="dataForm.isRefund">
              <el-radio :label="1">
                {{ $t('product.supportApplyRefund') }}
              </el-radio>
              <el-radio :label="0">
                {{ $t('product.doNotSupportApplyRefund') }}
              </el-radio>
            </el-radio-group>
            <div
              v-if="dataForm.isRefund === 0"
              class="el-form-item-tips"
            >
              {{ $t('product.afterSalesServiceTips') }}
            </div>
          </el-form-item>
        </div>
      </div>
    </el-form>

    <!-- 平台分类弹窗 -->
    <category-select
      v-show="editPlatformCategoriesSelect"
      ref="categorySelectRef"
      @get-category-selected="getCategorySelected"
    />

    <!-- 新建运费模板 -->
    <transport-add-or-update
      v-if="visibleTransportTemplate"
      ref="transportAddOrUpdateRef"
      @close="transportAddOrUpdateClose"
      @refresh-transport-list="getTransportList"
    />
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import SkuTag from '../../components/sku-tag.vue'
import SkuTable from '../../components/sku-table.vue'
import moment from 'moment'
import { watch } from 'vue'

const prodStore = useProdStore()

const emit = defineEmits([
  'update:modelValue',
  'updataProdDataForm',
  'changeWriteOffTime',
  'changeCategory'
])
const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({})
  },
  platCategoryName: {
    type: String,
    default: ''
  },
  stockPointInfo: {
    type: Object,
    default: () => ({
      warehouseSize: 0
    })
  }
})

const lang = ref(localStorage.getItem('cbecB2cLang'))

const writeOffMultipleCount = ref('')
const dataFormRef = ref(null)
const dataForm = ref({
  tableRate: -1, // 0 包邮 -1 固定运费 大于0运费模板id
  // 商品类型(0普通商品 1拼团 2秒杀 3积分 5活动商品)
  prodType: 0,
  video: '',
  pic: '',
  imgs: '',
  seq: 0,
  preSellStatus: 0,
  preSellTime: null,
  prodId: 0,
  brandId: 0,
  skuList: [],
  categoryId: '',
  prodLangList: [],
  brandName: '',
  deliveryMode: {
    hasShopDelivery: true,
    hasUserPickUp: false,
    hasCityDelivery: false
  },
  deliveryAmount: 0.01, // 统一运费的金额

  // 运费模板id
  deliveryTemplateId: 0,
  // 库存总和
  totalStocks: 0,

  /* 其他设置(虚拟商品时出现) */
  // 留言
  virtualRemark: [],
  // 核销次数 -1.多次核销 0.无需核销 1.单次核销
  writeOffNum: 0,
  // 核销有效期 -1.长期有效 0.自定义 1.当天24点前 x.x天内有效
  writeOffTime: -1,
  writeOffMultipleCount: -1,
  writeOffStart: null,
  writeOffEnd: null,
  // 售后服务 0不支持卖家申请退款 1支持卖家申请退款
  isRefund: 0,
  // 参数
  prodParameterList: []
})

const validateTime = (rule, value, callback) => {
  if (rule.field === 'preSellTime' && new Date() > Date.parse(value) && dataForm.value.preSellStatus === 1 && dataForm.value.prodType !== 5) {
    callback(new Error($t('groups.deliveryTime')))
  } else if (rule.field === 'writeOffEnd' && new Date() >= Date.parse(value)) {
    callback(new Error($t('product.dateErrTips2')))
  } else {
    callback()
  }
}
const validatePreSellTime = (rule, value, callback) => {
  if (dataForm.value.preSellStatus === 1 && dataForm.value.prodType !== 5 && !value) {
    callback(new Error($t('product.thePreSaleDtBeEmpty')))
  } else {
    callback()
  }
}
const validEmptyTab = (rule, value, callback) => {
  if (validNoEmptySpace(value)) {
    callback(new Error($t('shopProcess.inputAllSpace')))
  } else {
    callback()
  }
}
const dataRule = {
  prodNameCn: [
    { required: true, message: $t('product.postProductTips17'), trigger: 'blur' },
    { validator: validEmptyTab, trigger: 'blur' }
  ],
  prodNameEn: [
    { required: true, message: $t('product.postProductTips18'), trigger: 'blur' },
    { validator: validEmptyTab, trigger: 'blur' }
  ],
  imgs: [
    { required: true, message: $t('product.postProductTips21'), trigger: ['blur', 'change'] }
  ],
  preSellTime: [
    { required: true, validator: validatePreSellTime, trigger: 'blur' },
    { required: true, validator: validateTime, trigger: 'blur' }
  ],
  writeOffEnd: [
    { validator: validateTime, trigger: 'blur' }
  ],
  deliveryMode: [
    { required: true }
  ],
  deliveryTemplateId: [
    { required: true, message: $t('product.shippingtBeEmpty'), trigger: ['blur', 'change'] }
  ],
  deliveryAmount: [
    { required: true, message: $t('product.pleaseEnterTheAmount'), trigger: 'blur' }
  ]
}

// 限制日期
const PickerOptions = reactive({
  disabledDate (time) {
    const date = moment().add(-1, 'days').startOf('days')
    return time.getTime() <= date.valueOf()
  }
})

// 购买后有效天数
const validDays = ref(null)
watch(() => dataForm.value, (nv) => {
  if (nv.writeOffNum) {
    // 核销次数 -1.多次核销 0.无需核销 1.单次核销
    emit('changeWriteOffTime', validDays.value)
  }
}, { deep: true })

const curLang = ref([1]) // 当前语言
watch(() => curLang.value, () => {
  emit('updataProdDataForm', dataForm.value)
}, { deep: true })

const writeOffMultipleCountSelect = ref(-1)
const totalStocks = ref(0) // 总库存
onMounted(() => {
  totalStocks.value = props.modelValue.totalStocks || 0
  dataForm.value = Object.assign(dataForm.value, props.modelValue)
  writeOffMultipleCountSelect.value = dataForm.value.writeOffMultipleCount === -1 ? -1 : 2
  writeOffMultipleCount.value = dataForm.value.writeOffMultipleCount > -1 ? dataForm.value.writeOffMultipleCount : ''

  // 虚拟商品
  if (props.modelValue.mold === 1) {
    if (props.modelValue.virtualRemark && props.modelValue.virtualRemark.length) {
      const isStr = Object.prototype.toString.call(props.modelValue.virtualRemark) === '[object String]'
      if (isStr) {
        dataForm.value.virtualRemark = JSON.parse(props.modelValue.virtualRemark)
      }
    } else {
      dataForm.value.virtualRemark = []
    }
    const writeOffTime = props.modelValue.writeOffTime
    if (writeOffTime !== -1 && writeOffTime !== 0 && writeOffTime !== 1) {
      validDays.value = writeOffTime
      dataForm.value.writeOffTime = 2
    }
    if (writeOffTime !== 0) {
      dataForm.value.writeOffStart = null
      dataForm.value.writeOffEnd = null
    }
  }
  // 活动商品
  if (props.modelValue.prodType === 5) {
    handleProdTpyeChange(props.modelValue.prodType)
  }
  initSkuTag(props.modelValue.skuList)
  initSkuTable(props.modelValue.skuList)
  // 同城配送设置
  // getSameCityDetail()
  // 运费模板
  getTransportList()
  // 获取语言列表
  getLangList()
})

const langItemList = ref([]) // 语言列表
const masterLangInfo = { name: '', lang: '' }
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

      if (!dataForm.value.prodLangList.length) {
        selectLang([info.lang])
      } else {
        const tempCurLang = []
        let masterInfo = null
        // 将主语言信息移到第一个
        for (let i = 0; i < dataForm.value.prodLangList.length; i++) {
          const prodLang = dataForm.value.prodLangList[i]
          if (i !== 0 && prodLang.lang === info.lang) {
            masterInfo = prodLang
            dataForm.value.prodLangList.splice(i, 1)
            break
          }
        }
        if (masterInfo) {
          dataForm.value.prodLangList.unshift(masterInfo)
        }
        // 设置langName并且获取当前语言的信息
        dataForm.value.prodLangList.forEach(item => {
          const fd = langItemList.value.find(f => f.lang === item.lang)
          if (fd) {
            item.langName = fd ? fd.name : ''
            tempCurLang.push(item.lang)
          }
        })

        dataForm.value.prodParameterList.forEach(item => {
          let masterParInx = 0
          item.prodParameterLangList.forEach((parame, parInx) => {
            if (parInx !== 0 && parame.lang === info.lang) {
              masterParInx = parInx
            }
            const fd = langItemList.value.find(f => f.lang === parame.lang)
            if (fd) {
              parame.langName = fd ? fd.name : ''
            }
          })
          // 将主语言的参数提取到第一位
          if (masterParInx) {
            const masterParInfo = item.prodParameterLangList.splice(masterParInx, 1)
            item.prodParameterLangList.unshift(...masterParInfo)
          }
        })
        selectLang(tempCurLang)
      }
    }
  })
}

// 重置sku-table表格
const resetInit = (skuList) => {
  setTimeout(() => {
    initSkuTable(skuList)
  }, 10)
}

const freight = () => {
  dataForm.value.deliveryAmount = dataForm.value.deliveryAmount < 0.01 ? 0.01 : dataForm.value.deliveryAmount
}

/**
 * 处理输入框数据
 * @param data
 * @param dataFields
 * @param min 最小值
 * @param max 最大值
 */
const handleInputValue = (data, dataFields, min, max) => {
  dataForm.value[dataFields] = checkInput(data)
  if (data || data === 0) {
    // 保留两位小数
    if (data > max) {
      dataForm.value[dataFields] = max
    }
    if (data < min) {
      dataForm.value[dataFields] = min
    }
  }
}

/**
 * 只允许输入正数和小数(保留小数点后两位)
 */
const checkInput = (num) => {
  if (num) {
    let tmpVal = String(num).replace(/[^\d^\\.]/g, '')
    const reg = /^(0|([1-9]\d*))(\.\d{1,2})?$/ // 最多允许后输入两位小数
    if (!reg.test(tmpVal)) {
      tmpVal = tmpVal + ''
      tmpVal = tmpVal.substring(0, tmpVal.indexOf('.') + 3)
      const n = (tmpVal.split('.')).length - 1
      if (n > 1) {
        tmpVal = tmpVal.substring(0, tmpVal.indexOf('.'))
      }
    }
    return tmpVal
  } else {
    return ''
  }
}
/**
 * 商品类型切换
 */
const handleProdTpyeChange = (val) => {
  // 活动商品则支持所有配送类型
  if (val === 5) {
    for (const key in dataForm.value.deliveryMode) {
      if (Object.hasOwnProperty.call(dataForm.value.deliveryMode, key)) {
        dataForm.value.deliveryMode[key] = true
      }
    }
  }
}

/**
 * 初始化sku表格
 */
const skuTagRef = ref(null)
const initSkuTag = (data) => {
  nextTick(() => {
    skuTagRef.value?.init(data)
  })
}
const skuTableRef = ref(null)
const initSkuTable = (data) => {
  nextTick(() => {
    skuTableRef.value?.init(data)
  })
}

/**
 * 修改分类信息
 */
const categorySelectRef = ref(null)
// 控制平台分类选择下拉框
const editPlatformCategoriesSelect = ref(false)
const changeCategory = (type) => {
  switch (type) {
    case 1:
      // 获取平台分类初始化数据
      // eslint-disable-next-line no-case-declarations
      const selectedCategories = []
      selectedCategories.push(Object.assign({}, prodStore.platProdCategory.firstCat))
      selectedCategories.push(Object.assign({}, prodStore.platProdCategory.secondCat))
      selectedCategories.push(Object.assign({}, prodStore.platProdCategory.threeCat))
      // 显示平台分类选择框
      editPlatformCategoriesSelect.value = true
      categorySelectRef.value?.init(0, selectedCategories)
      break
    default:
      editPlatformCategoriesSelect.value = false
  }
}

/**
 * 获取分类选择组件返回数据
 */
const getCategorySelected = (selectedCategories) => {
  editPlatformCategoriesSelect.value = false
  dataForm.value.categoryId = selectedCategories[2].id
  prodStore.updatePlatProdCategory({
    platProdCategory: {
      firstCat: selectedCategories[0],
      secondCat: selectedCategories[1],
      threeCat: selectedCategories[2]
    },
    selectUpdate: true
  })
  // 发送平台分类变化事件,通知父组件改变分类id与分类名称
  emit('changeCategory', 1)
}

/**
 * 保存信息
 */
const upadteProdInfo = () => {
  emit('updataProdDataForm', dataForm.value)
}

/**
 * 选择语言(主语言信息必填)
 */

const selectLang = (langVal) => {
  curLang.value = JSON.parse(JSON.stringify(langVal))
  if (!langVal.length) {
    curLang.value = [masterLangInfo.lang]
    return
  }
  // 设置主语言不可删除
  if (!curLang.value.includes(masterLangInfo.lang)) {
    curLang.value.unshift(masterLangInfo.lang)
  }

  // 所选语言改变
  const tempArr = dataForm.value.prodLangList.filter(item => curLang.value.includes(item.lang))

  curLang.value.forEach((item, index) => {
    if (!tempArr.find(f => f.lang == item)) {
      const fd = langItemList.value.find(it => it.lang === item)
      tempArr.splice(index, 0, { langName: fd.name, brief: '', content: '', lang: item, prodId: dataForm.value.prodId, prodName: '' })
    }
  })
  dataForm.value.prodLangList = tempArr

  // 参数设置
  dataForm.value.prodParameterList.forEach(item => {
    const flList = item.prodParameterLangList.filter(fl => curLang.value.includes(fl.lang))
    curLang.value.forEach((langItem, index) => {
      if (flList && !flList.find(f => f.lang == langItem)) {
        const fd = langItemList.value.find(it => it.lang === langItem)
        if (fd) {
          flList.splice(index, 0, { langName: fd.name, lang: langItem, parameterKey: '', parameterValue: '', prodParameterId: null })
        }
      }
    })
    item.prodParameterLangList = flList
  })
}

/**
 * 图片的值发生改变，重新校验该表单项
 */
const imgsChange = () => {
  if (dataForm.value.imgs) {
    dataFormRef.value?.validateField('imgs')
  }
}

/**
 * 规格
 */
// eslint-disable-next-line no-unused-vars
const skuTagChangeSkuHandler = (skuList, skuTags, type) => {
  if (type === 5) {
    dataForm.value.skuList = skuList
    return
  }
  let stocks = 0
  skuList.forEach(sku => {
    const stock = parseInt(sku.stocks)
    stocks += isNaN(stock) ? 0 : stock
    if (sku.properties) {
      sku.skuName = ''
      sku.skuNameEn = ''
      const properties = sku.properties.split(';')
      for (const propertiesKey in properties) {
        sku.skuName += properties[propertiesKey].substring(properties[propertiesKey].indexOf(':') + 1) + ' '
      }
    }
  })
  totalStocks.value = stocks
  dataForm.value.skuList = skuList
}
const changeSkuImgHandler = (propValue, img) => {
  nextTick(() => {
    skuTableRef.value?.changeSkuImg(propValue, img)
  })
}
const clearSkuImg = () => {
  nextTick(() => {
    skuTableRef.value?.clearSkuImg()
  })
}

const skuListHandler = (skuList) => {
  let temp = 0
  skuList.forEach(el => {
    const stock = parseInt(el.stocks)
    temp += isNaN(stock) ? 0 : stock
    if ((!el.stockPointList || !el.stockPointList.length)) {
      el.stockPointList = [{
        stock: 0,
        stockPointId: props.stockPointInfo.defaultWarehouseId,
        stockPointType: 1,
        type: 0
      }]
    }
  })
  totalStocks.value = temp
  dataForm.value.totalStocks = temp
}

/**
 * 获取店铺同城配送的配置
 */
// const sameCityStatus = ref(0) // 同城配送(是否开启)
// const getSameCityDetail = () => {
//   http({
//     url: http.adornUrl('/delivery/sameCity/getSameCityInfo'),
//     method: 'get',
//     params: http.adornParams()
//   }).then(({ data }) => {
//     if (data) {
//       sameCityStatus.value = data.status
//     }
//   })
// }

/**
 * 运费模板
 */
const transportList = ref([])
const getTransportList = () => {
  http({
    url: http.adornUrl('/platform/transport/list'),
    method: 'get',
    params: http.adornParams({})
  }).then(({ data }) => {
    transportList.value = data
  })
}
/**
 * 新建运费模板
 */
const transportAddOrUpdateRef = ref(null)
const visibleTransportTemplate = ref(false) // 创建运费模板弹窗显隐
const createTransportTemplate = (id) => {
  visibleTransportTemplate.value = true
  nextTick(() => {
    transportAddOrUpdateRef.value?.init(id)
  })
}
const transportAddOrUpdateClose = () => {
  visibleTransportTemplate.value = false
}

/**
 * 虚拟商品 - 核销有效期
 */
const changeWriteOffTime = (val) => {
  if (val !== 2) validDays.value = ''
  if (val !== 0) {
    dataForm.value.writeOffStart = null
    dataForm.value.writeOffEnd = null
  }
}

const changeWriteOffMultipleCount = (val) => {
  if (val === -1) {
    writeOffMultipleCount.value = ''
  } else {
    writeOffMultipleCount.value = 2
  }
}

// 自定义核销有效期天数
const getValidDays = (validDaysPar) => {
  validDays.value = parseInt(validDaysPar)
  if (validDays.value > 9999) {
    validDays.value = 9999
  }
  if (validDays.value < 2) {
    validDays.value = 2
  }
  emit('changeWriteOffTime', validDays.value)
}

const changeWriteOffMultipleCountIn = () => {
  writeOffMultipleCount.value = parseInt(writeOffMultipleCount.value)
  if (writeOffMultipleCount.value === '' || writeOffMultipleCount.value < 2) {
    writeOffMultipleCount.value = 2
  }
}

// 校验自定义核销有效期日期
// eslint-disable-next-line no-unused-vars
const changeDate = (v, sts) => {
  // 校验开始时间
  if (sts === 0) {
    if (Date.parse(dataForm.value.writeOffStart) >= Date.parse(dataForm.value.writeOffEnd)) {
      ElMessage({
        message: $t('product.dateErrTips1'),
        type: 'error',
        duration: 1500
      })
      dataForm.value.writeOffStart = ''
      return
    }
  }
  // 校验结束时间
  if (sts === 1) {
    if (new Date() >= Date.parse(dataForm.value.writeOffEnd)) {
      ElMessage({
        message: $t('product.dateErrTips2'),
        type: 'error',
        duration: 1500
      })
      dataForm.value.writeOffEnd = ''
      return
    }
    if (Date.parse(dataForm.value.writeOffStart) >= Date.parse(dataForm.value.writeOffEnd)) {
      ElMessage({
        message: $t('product.dateErrTips3'),
        type: 'error',
        duration: 1500
      })
      dataForm.value.writeOffEnd = ''
    }
  }
}

/**
 * 用户留言-添加字段
 */
const addUserMessage = () => {
  if (dataForm.value.virtualRemark.length >= 10) {
    ElMessage({
      message: $t('product.msgMaxLength'),
      type: 'error',
      duration: 1000
    })
    return
  }
  const data = {
    name: '',
    isRequired: false
  }
  dataForm.value.virtualRemark.push(data)
}
/**
 * 用户留言-删除字段
 */
const deleteUserMessage = (index) => {
  dataForm.value.virtualRemark.splice(index, 1)
}

/**
 * 留言输入框失焦校验
 */
const changeMsgInput = (name, index) => {
  dataForm.value.virtualRemark[index].name = name.trim()
}
/**
 * 错误提示框
 */
const errorMsg = (message) => {
  ElMessage({
    message,
    type: 'error',
    duration: 1500
  })
}

/**
 * 表单验证
 */
const dataFormValidation = async () => {
  // 获取sku-table的最新数据
  dataForm.value.skuList = skuTableRef.value?.getDataList()
  if (writeOffMultipleCountSelect.value !== -1) {
    dataForm.value.writeOffMultipleCount = writeOffMultipleCount.value || 2
  } else {
    dataForm.value.writeOffMultipleCount = -1
  }
  // 拼接商品sku名称
  dataForm.value.skuList.forEach(sku => {
    if (sku.properties) {
      sku.skuName = ''
      sku.skuNameEn = ''
      const properties = sku.properties.split(';')
      for (const propertiesKey in properties) {
        sku.skuName += properties[propertiesKey].substring(properties[propertiesKey].indexOf(':') + 1) + ' '
      }
    }
  })

  emit('updataProdDataForm', dataForm.value)
  let isValid = true
  await dataFormRef.value?.validate((valid) => {
    if (!valid) {
      errorMsg($t('product.fillInTheNecessaryInfo'))
      isValid = false
      return
    }
    if (!dataForm.value.imgs) {
      // 请选择图片上传
      errorMsg($t('product.plePictureToUpload'))
      isValid = false
      return
    }
    if (dataForm.value.mold !== 1 && dataForm.value.prodType !== 5 && !dataForm.value.deliveryMode) {
      // 请选择配送方式
      errorMsg($t('product.pleeliveryMethod'))
      isValid = false
      return
    }
    if (dataForm.value.mold !== 1 && dataForm.value.prodType !== 5 && dataForm.value.deliveryMode.hasShopDelivery && dataForm.value.tableRate > 0 && dataForm.value.deliveryTemplateId === null) {
      // 请选择运费模板
      errorMsg($t('product.pleShgTlate'))
      isValid = false
      return
    }

    if (dataForm.value.prodParameterList.length && !validParameter()) {
      isValid = false
      return
    }
    // 虚拟商品校验
    if (dataForm.value.mold === 1) {
      // 自定义核销有效天数
      if (dataForm.value.writeOffNum !== 0 && dataForm.value.writeOffTime === 2) {
        if (!validDays.value) {
          ElMessage({
            message: $t('product.validDaysEmptyTips'),
            type: 'error',
            duration: 1000
          })
          isValid = false
          return
        }
      }
      // 自定义核销有效天数
      if (dataForm.value.writeOffNum !== 0 && dataForm.value.writeOffTime === 0 && (!dataForm.value.writeOffStart || !dataForm.value.writeOffEnd)) {
        if (!validDays.value) {
          ElMessage({
            message: $t('product.validDateEmptyTips'),
            type: 'error',
            duration: 1000
          })
          isValid = false
          return
        }
      }
      if (dataForm.value.virtualRemark.length && dataForm.value.virtualRemark.find(el => !el.name)) {
        ElMessage({
          message: $t('product.msgEmptyTips'),
          type: 'error',
          duration: 1000
        })
        isValid = false
        return
      }
    }
    const langList = dataForm.value.prodLangList
    for (const item of langList) {
      if (!item.prodName) {
        errorMsg($t('prod.prodNameTip'))
        isValid = false
        return
      }
    }
    // 编码是否重复
    const partyCodes = dataForm.value.skuList.filter(el => el.partyCode).map(el => el.partyCode) || []
    if (partyCodes.length && new Set(partyCodes).size !== partyCodes.length) {
      ElMessage({
        message: $t('product.postProductTips16'),
        type: 'error',
        duration: 1500
      })
      isValid = false
      return
    }

    // 校验是否含有空规格值
    if (!skuTagRef.value?.vaildSkuTag()) {
      isValid = false
      return
    }
    isValid = true
  })
  return isValid
}

/** 添加参数 */
const parameterInsert = () => {
  const prodParameterLangList = []
  for (const item of curLang.value) {
    const fd = langItemList.value.find(it => it.lang === item)
    prodParameterLangList.push({
      langName: fd.name,
      lang: item,
      parameterKey: '',
      parameterValue: '',
      prodParameterId: null
    })
  }

  dataForm.value.prodParameterList.push({
    prodId: dataForm.value.prodId || 0,
    parameterKey: '',
    parameterValue: '',
    prodParameterId: null,
    prodParameterLangList
  })
}
const parameterDeltete = (index) => {
  dataForm.value.prodParameterList.splice(index, 1)
}
const validParameter = () => {
  const data = dataForm.value.prodParameterList
  for (let i = data.length - 1; i >= 0; i--) {
    let emptyNum = 0
    for (const item of data[i].prodParameterLangList) {
      if (!item.parameterKey && !item.parameterValue) {
        emptyNum++
      }
    }
    if (emptyNum === data[i].prodParameterLangList.length) {
      parameterDeltete(i)
    }
  }
  for (let i = 0; i < data.length; i++) {
    for (const item of data[i].prodParameterLangList) {
      if (!item.parameterKey || !item.parameterValue) {
        ElMessage.error($t('product.parameterTips'))
        return false
      }
    }
  }
  return true
}

defineExpose({
  dataFormValidation,
  upadteProdInfo
})

</script>

<style lang="scss" scoped>
@use './posting-edit-product.scss';
</style>

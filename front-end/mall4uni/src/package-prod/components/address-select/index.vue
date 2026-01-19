<template>
  <pop-comm
    v-model="popShow"
    :title="$t('area')"
    :pad-b="0"
    :is-btn="false"
  >
    <view class="component-address-select">
      <!-- 步骤 -->
      <view class="step-box">
        <view
          :class="['step', step === 1 ? 'step-act' : '']"
          @tap="stepTap(1)"
        >
          <text>{{ province || $t('pleaseChoose') }}</text>
        </view>
        <view
          v-if="cityId || step > 1"
          :class="['step', step === 2 ? 'step-act' : '']"
          @tap="stepTap(2)"
        >
          <text>{{ city || $t('pleaseChoose') }}</text>
        </view>
        <view
          v-if="areaId || step > 2"
          :class="['step', step === 3 ? 'step-act' : '']"
          @tap="stepTap(3)"
        >
          <text>{{ area || $t('pleaseChoose') }}</text>
        </view>
      </view>
      <!-- 滚动区域 -->
      <scroll-view
        class="scroll-box"
        scroll-y
        :scroll-into-view="currentIntoView"
        :scroll-top="scrollTop"
        scroll-with-animation
        @scroll="onScroll"
      >
        <view style="flex: 1">
          <!-- 热门国家/地区 -->
          <view
            v-if="step === 1 && hotCityList.length > 0"
            class="hot-city-box"
          >
            <view class="title">
              {{ $t('hotCountry') }}
            </view>
            <view class="city-list">
              <block
                v-for="item in hotCityList"
                :key="item.areaId"
              >
                <view
                  class="city-item"
                  @tap="hotCityItemTap(item)"
                >
                  {{ item[showLang] }}
                </view>
              </block>
            </view>
          </view>
          <view
            class="area-box"
          >
            <view
              v-if="step === 1"
              class="title prov-til"
            >
              {{ $t('countryOrArea') }}
            </view>
            <view
              v-if="step === 1"
              class="area-list"
            >
              <view
                v-for="(item, index) in provArray"
                :id="item.letter ? ('prov-' + item.letter) : ('prov-' + index)"
                :key="item.areaId"
              >
                <view
                  class="area-item"
                  @tap="onSelectItem(0, item)"
                >
                  <text class="color9 letter">
                    {{ item.letter }}
                  </text>
                  <text :class="['color3', provinceId === item.areaId ? 'sel-color' : '']">
                    {{ item[showLang] }}
                  </text>
                </view>
              </view>
            </view>
            <view
              v-if="step === 2"
              class="area-list"
            >
              <view
                v-for="item in cityArray"
                :id="item.letter ? ('city-' + item.letter) : ''"
                :key="item.areaId"
              >
                <view
                  class="area-item"
                  @tap="onSelectItem(1, item)"
                >
                  <text class="color9 letter">
                    {{ item.letter }}
                  </text>
                  <text :class="['color3', cityId === item.areaId ? 'sel-color' : '']">
                    {{ item[showLang] }}
                  </text>
                </view>
              </view>
            </view>
            <view
              v-if="step === 3"
              class="area-list"
            >
              <view
                v-for="item in areaArray"
                :id="item.letter ? ('area-' + item.letter) : ''"
                :key="item.areaId"
              >
                <view
                  class="area-item"
                  @tap="onSelectItem(2, item)"
                >
                  <text class="color9 letter">
                    {{ item.letter }}
                  </text>
                  <text :class="['color3', areaId === item.areaId ? 'sel-color' : '']">
                    {{ item[showLang] }}
                  </text>
                </view>
              </view>
            </view>
          </view>
        </view>
      </scroll-view>

      <view class="index-bar">
        <view
          v-for="(item, idx) in (step === 1 ? provIndexList : ( step === 2 ? cityIndexList : areaIndexList))"
          :key="idx"
          class="index-item"
          :class="{active: nowIndexKey === item}"
          @click="scrollToIndex(item, idx)"
        >
          <view class="index-text">
            {{ item }}
          </view>
          <view
            v-if="nowIndexKey === item && showIndexTip"
            class="index-tip"
          >
            {{ item }}
          </view>
        </view>
      </view>
    </view>
  </pop-comm>
</template>

<script setup>

const emit = defineEmits(['update:modelValue', 'selectAddr'])

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  selectIds: {
    type: Array,
    default: () => [0, 0, 0]
  }
})

const showLang = computed(() => {
  return 'areaNameEn'
})

const Data = reactive({
  step: 1, // 1国家 2省 3城市
  provArray: [],
  cityArray: [],
  areaArray: [],
  province: '',
  city: '',
  area: '',
  provinceId: 0,
  cityId: 0,
  areaId: 0,
  addrInfoList: [],
  hotCityList: [],
  isFirstShow: true // 是否第一次显示
})

const { step, provArray, cityArray, areaArray, province, city, area, provinceId, cityId, areaId, hotCityList } = toRefs(Data)

const popShow = computed({
  get () {
    return props.modelValue
  },
  set () {
    emit('update:modelValue', false)
    Data.provinceId = null
    Data.cityId = null
    Data.areaId = null
    Data.province = ''
    Data.city = ''
    Data.area = ''
  }
})

watch(() => props.modelValue, (show) => {
  if (show) {
    Data.step = 1
    if (Data.cityId) {
      Data.step = 2
    }
    if (Data.areaId) {
      Data.step = 3
    }
    if (Data.isFirstShow) {
      getProvArray(Data.provinceId, Data.cityId, Data.areaId)
      getHotCityList()
      getAreaListInfo()
      // 延迟执行，防止多次请求
      setTimeout(() => {
        Data.isFirstShow = false
      }, 100)
    }
  }
})

watch(() => props.selectIds, (ids = []) => {
  Data.provinceId = ids[0] || 0
  Data.cityId = ids[1] || 0
  Data.areaId = ids[2] || 0
  if (!Data.isFirstShow) {
    getProvArray(Data.provinceId, Data.cityId, Data.areaId)
  }
},
{
  immediate: true,
  deep: true
})

/**
 * type 0-国家；1-省份；2-城市
 * 选择国家/地区, 并获取下一级地址列表
 */
const onSelectItem = util.debounce(async (type, item) => {
  clearSelect(type)
  // 获取下一级数据
  if (type === 0) {
    Data.provinceId = item.areaId
    Data.province = item[showLang.value]

    // 获取下一级数据
    const { curNode, areas } = getCurrentAreas(Data.provArray, item.areaId)
    let cityArray = areas
    if (!areas) {
      cityArray = await listAreaByParentId(item.areaId)
    }

    if (cityArray && cityArray.length > 0) {
      cityArray = formatLetterSort(cityArray, 'city')
      // 将获取到的列表加入当前上级子节点areas下
      curNode.areas = cityArray
      // eslint-disable-next-line require-atomic-updates
      Data.cityArray = cityArray
      // eslint-disable-next-line require-atomic-updates
      Data.step = 2
      goTop()
    } else {
      selectEnd()
    }
  } else if (type === 1) {
    Data.cityId = item.areaId
    Data.city = item[showLang.value]

    // 获取下一级数据
    const { curNode, areas } = getCurrentAreas(Data.cityArray, item.areaId)
    let areaArray = areas
    if (!areas) {
      areaArray = await listAreaByParentId(item.areaId)
    }

    if (areaArray && areaArray.length > 0) {
      areaArray = formatLetterSort(areaArray, 'area')
      // 将获取到的列表加入当前上级子节点areas下
      curNode.areas = areaArray
      // eslint-disable-next-line require-atomic-updates
      Data.areaArray = areaArray
      // eslint-disable-next-line require-atomic-updates
      Data.step = 3
      goTop()
    } else {
      selectEnd()
    }
  } else {
    Data.areaId = item.areaId
    Data.area = item[showLang.value]
    selectEnd()
  }
})

const clearSelect = (type) => {
  if (type === 0) {
    // 清空所选的省份数据
    Data.cityId = 0
    Data.city = ''
    Data.cityArray = null
  }
  // 清空所选的城市数据
  Data.areaId = 0
  Data.area = ''
  Data.areaArray = null
}

/**
 * 返回选择结果
 */
const selectEnd = () => {
  emit('selectAddr', {
    province: Data.province,
    city: Data.city,
    area: Data.area,
    provinceId: Data.provinceId,
    cityId: Data.cityId,
    areaId: Data.areaId
  })
  popShow.value = false
}

// 处理 国家 省份 城市步骤
const stepTap = (stepNum) => {
  Data.step = stepNum
  goTop()
}

// 获取首字母
const getLetterFirst = (text) => {
  return text.substring(0, 1)
}

const nowIndexKey = ref('A')

const provIndexList = ref([])
const cityIndexList = ref([])
const areaIndexList = ref([])

// 格式化列表，让其根据字母正序排序
const formatLetterSort = (list, key) => {
  // 获取大写首字母
  list.forEach(item => {
    item.letter = item[showLang.value] ? getLetterFirst(item[showLang.value]) : '#'
    item.letterKey = item.letter
  })
  // 排序
  list.sort((a, b) => {
    if (a.letter < b.letter) {
      return -1
    }
    if (a.letter > b.letter) {
      return 1
    }
    return 0
  })
  const arr = []
  // 将与首项相同字母后面的项清空letter
  for (let i = 0; i < list.length; i++) {
    if (!list[i].letter) continue
    arr.push(list[i].letter)
    for (let j = i + 1; j < list.length; j++) {
      if (list[i].letter === list[j].letter) {
        list[j].letter = ''
      } else {
        break
      }
    }
  }
  if (key === 'prov') provIndexList.value = arr
  if (key === 'city') cityIndexList.value = arr
  if (key === 'area') areaIndexList.value = arr
  return list
}

// 处理选择热门选择
const hotCityItemTap = (hotCity) => {
  // 国家级地址处理
  for (const prov of Data.provArray) {
    if (prov.areaId === hotCity.areaId) {
      onSelectItem(0, prov)
      return
    }
  }
  // 省份级地址处理
  for (const prov of Data.addrInfoList) {
    for (const city of prov.areas) {
      if (city.areaId === hotCity.areaId) {
        onSelectItem(0, prov)
        onSelectItem(1, city)
        return
      }
    }
  }
}

// 获取热门国家/地区
const getHotCityList = () => {
  http.request({
    url: '/hotCity/listHotCity',
    method: 'GET'
  }).then(({ data }) => {
    Data.hotCityList = data
  })
}

// 获取拥有三级地址的地区列表
const getAreaListInfo = () => {
  http.request({
    url: '/p/area/getAreaListInfo',
    method: 'GET',
    data: {}
  }).then(({ data }) => {
    Data.addrInfoList = formatLetterSort(data, 'prov')
  })
}

// 获取当前id下的地址信息
const listAreaByParentId = (pid) => {
  let paramData = {}
  if (!pid) {
    paramData = { level: 1 }
  } else {
    paramData = { pid }
  }
  return new Promise((resolve) => {
    http.request({
      url: '/p/area/listByPid',
      method: 'GET',
      data: paramData
    }).then(({ data }) => {
      resolve(data)
    })
  })
}

// 获取当前节点下的地址
const getCurrentAreas = (curList, curId) => {
  for (const item of curList) {
    if (item.areaId === curId) {
      return {
        curNode: item,
        areas: item.areas
      }
    }
  }
}

// 获取国家数据
const getProvArray = async (provinceId, cityId, areaId) => {
  // 获取国家列表
  if (!Data.provArray.length) {
    const provArray = await listAreaByParentId()
    // eslint-disable-next-line require-atomic-updates
    Data.provArray = formatLetterSort(provArray, 'prov')
  }

  if (provinceId) {
    for (let i = 0, len = Data.provArray.length; i < len; i++) {
      if (Data.provArray[i].areaId === provinceId) {
        Data.province = Data.provArray[i][showLang.value]
      }
    }
  }

  if (provinceId) {
    getCityArray(provinceId, cityId, areaId)
  }
}

/**
 * 根据国家ID获取省份数据
 */
const getCityArray = async (provinceId, cityId, areaId) => {
  const { curNode, areas } = getCurrentAreas(Data.provArray, provinceId)
  let cityArray = areas
  if (!areas) {
    cityArray = await listAreaByParentId(provinceId)
  }
  cityArray = formatLetterSort(cityArray, 'city')
  // 将获取到的列表加入当前上级子节点areas下
  curNode.areas = cityArray
  // eslint-disable-next-line require-atomic-updates
  Data.cityArray = cityArray
  if (cityId) {
    for (const city of Data.cityArray) {
      if (city.areaId === cityId) {
        Data.city = city[showLang.value]
        break
      }
    }
  }
  if (cityId) {
    getAreaArray(cityId, areaId)
  }
}

/**
 * 根据省份ID获取 城市数据
 */
const getAreaArray = async (cityId, areaId) => {
  const { curNode, areas } = getCurrentAreas(Data.cityArray, cityId)
  let areaArray = areas
  if (!areas) {
    areaArray = await listAreaByParentId(cityId)
  }
  areaArray = formatLetterSort(areaArray, 'area')
  // 将获取到的列表加入当前上级子节点areas下
  curNode.areas = areaArray
  // eslint-disable-next-line require-atomic-updates
  Data.areaArray = areaArray
  if (areaId) {
    for (const area of Data.areaArray) {
      if (area.areaId === areaId) {
        Data.area = area[showLang.value]
        break
      }
    }
  }
}

const currentIntoView = ref(null)
const showIndexTip = ref(false)
const scrollTop = ref(0)
let timeout
let isClickScroll = false
/**
 * 根据key值及所属的模块计算出需要滚动的子元素的样式id
 * @param key
 */
const scrollToIndex = (key) => {
  nowIndexKey.value = key
  showIndexTip.value = true
  isClickScroll = true
  if (timeout) {
    clearTimeout(timeout)
  }
  timeout = setTimeout(() => {
    showIndexTip.value = false
    isClickScroll = false
  }, 1000)

  // 根据索引计算目标 ID（这里假设每个字母对应一个分组，并且分组数量与索引条数量一致）
  // 你可能需要根据实际的数据结构来调整这个计算逻辑
  let str
  if (step.value === 1) {
    str = 'prov'
  } else if (step.value === 2) {
    str = 'city'
  } else {
    str = 'area'
  }
  const id = str + '-' + key
  if (id === currentIntoView.value) {
    currentIntoView.value = ''
  }
  nextTick(() => {
    currentIntoView.value = str + '-' + key
  })
}

let oldScrollTop = 0
/**
 * 获取当前滚动位置
 */
const onScroll = (e) => {
  const { scrollHeight, scrollTop } = e.detail
  oldScrollTop = scrollTop
  // 如果是通过点击索引条发生的滚动，则不重新获取key
  if (!isClickScroll) nowIndexKey.value = judgeNowPosition(scrollHeight, scrollTop)
}

/**
 * 判断当前位置所属区域，返回A-Z
 * @param scrollHeight 区域总高度
 * @param scrollTop 距顶部位置
 */
const judgeNowPosition = (scrollHeight, scrollTop) => {
  let list, indexList, nowKey

  // 计算第一项A的位置
  const aBox = document.getElementById('prov-A')
  if (step.value === 1) {
    scrollHeight = scrollHeight - aBox.offsetTop
    scrollTop = scrollTop - aBox.offsetTop > 0 ? scrollTop - aBox.offsetTop : 0
  }

  // 获取当前数据列表
  if (step.value === 1) {
    list = Data.provArray
    indexList = provIndexList.value
  } else if (step.value === 2) {
    list = Data.cityArray
    indexList = cityIndexList.value
  } else {
    list = Data.areaArray
    indexList = areaIndexList.value
  }

  // 计算每一行占有高度
  const length = list.length
  const average = (scrollHeight / length).toFixed(2)

  // 计算当前滚动第一行序号
  let index = 0
  if (scrollTop > average) {
    index = Math.ceil(scrollTop / average)
  }

  let obj, objPar
  // 根据获取到的序号拿到当前对象
  if (index === 0) {
    obj = list[0]
  } else if (index >= length) {
    obj = list[length - 1]
  } else {
    obj = list[index - 1]
  }

  // 获取5项之后的对象
  if (index + 5 <= length) {
    objPar = list[index + 5]
  }

  // 判断5项之后的对象与当前对象的key一致，一致则返回当前key，不一致则返回下一项的key
  if (objPar && objPar.letterKey === obj.letterKey) {
    nowKey = obj.letterKey
  } else {
    const i = indexList.indexOf(obj.letterKey)
    if (i + 1 < indexList.length) {
      nowKey = indexList[i + 1]
    } else {
      nowKey = indexList[indexList.length - 1]
    }
  }

  // 如果当前对象存在letter，则表明正好处于第一项，直接返回当前key
  if (obj.letter) {
    nowKey = obj.letter
  }
  return nowKey
}

/**
 * 返回顶部
 */
const goTop = () => {
  let list
  if (step.value === 1) {
    list = Data.provArray
  } else if (step.value === 2) {
    list = Data.cityArray
  } else {
    list = Data.areaArray
  }
  // 解决view层不同步的问题
  scrollTop.value = oldScrollTop
  nextTick(() => {
    // 重置为第一项
    nowIndexKey.value = list[0].letterKey
    scrollTop.value = 0
  })
}

</script>

<style lang="scss" scoped>
@use "index";
</style>

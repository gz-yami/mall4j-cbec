<template>
  <aside
    :class="['site-sidebar', $t('language') !== 'zh_CN'?'site-sidebar-en':''] "
  >
    <div class="menu-mod">
      <el-scrollbar class="menu-left">
        <ul>
          <li>
            <div
              :class="[
                selectLeftId === '' ? 'menu-left-active' : '',
                $t('language') !== 'zh_CN' ? 'menu-left-item1' : 'menu-left-item'
              ]"
              @click="toHome()"
            >
              <svg-icon
                icon-class="icon-shouye"
                style="font-size: 16px; margin-right: 3px !important"
              />
              <span style="font-size: 14px">{{ $t('homes.home') }}</span>
            </div>
          </li>
        </ul>
        <ul>
          <template
            v-for="menu in menuList"
            :key="menu.menuId"
          >
            <li
              v-if="!menu.hidden && !(menu.menuLangList && menu.menuLangList.some(item => item.name === '客服' || item.name === 'Customer service'))"
              class="menu-left-active"
            >
              <div
                v-if="menu.list"
                :class="[
                  selectLeftId === menu.menuId ? 'menu-left-active' : '',
                  $t('language') !== 'zh_CN' ? 'menu-left-item1' : 'menu-left-item'
                ]"
                @click="expandMenu(menu)"
              >
                <svg-icon
                  v-if="menu.icon"
                  :icon-class="`icon-${menu.icon}`"
                  style="font-size: 16px; margin-right: 3px !important"
                />
                <span
                  class="item-text"
                  :title="menu.name?.length > 4 ? menu.name : ''"
                  style="font-size: 14px"
                >{{ menu.name }}</span>
              </div>
              <div
                v-else
                :class="[
                  selectLeftId === menu.menuId ? 'menu-left-active' : '',
                  $t('language') !== 'zh_CN' ? 'menu-left-item1' : 'menu-left-item'
                ]"
                @click="gotoRouteHandle(menu)"
              >
                <svg-icon
                  v-if="menu.icon"
                  :icon-class="`icon-${menu.icon}`"
                  style="font-size: 16px; margin-right: 3px !important"
                />
                <span
                  class="item-text"
                  :title="menu.name?.length > 4 ? menu.name : ''"
                  style="font-size: 14px"
                >{{ menu.name }}</span>
              </div>
            </li>
          </template>
        </ul>
      </el-scrollbar>
      <SubMenu
        v-if="!sidebarFold"
        :key="selectLeftId"
        class="menu-right-con"
        :expand-menu="expandMenuList"
      />
    </div>
  </aside>
</template>
<script setup>
import { useRouterStore } from '../stores/router'
import SubMenu from './main-sidebar-sub-menu.vue'
const route = useRoute()
const router = useRouter()
const commonStore = useCommonStore()
const dynamicMenuRoutes = reactive([])
let expandMenuList = reactive([])

const sidebarFold = computed(() => commonStore.sidebarFold)
const menuList = computed({
  get: () => {
    return commonStore.menuList
  },
  set: val => {
    commonStore.updateMenuList(val)
  }
})

const selectLeftId = computed({
  get: () => {
    handleRightRoute(commonStore.selectLeftId)
    return commonStore.selectLeftId || ''
  }
})

onBeforeMount(() => {
  menuList.value = JSON.parse(sessionStorage.getItem('cbecB2cMenuList') || '[]')
  dynamicMenuRoutes.value = JSON.parse(sessionStorage.getItem('cbecB2cDynamicMenuRoutes') || '[]')
  routeHandle(route)
  if (selectLeftId.value) {
    handleRightRoute(selectLeftId.value)
  }
})

const handleRightRoute = selectLeftId => {
  menuList.value.forEach(item => {
    if (selectLeftId === item.menuId) {
      expandMenu(item, true)
    }
  })
}

const toHome = () => {
  router.push({ name: 'home' })
  expandMenuList = []
  sessionStorage.setItem('cbecB2cIsExpand', '0')
  commonStore.updateSidebarFold(true)
  commonStore.updateSelectLeftId('')
  commonStore.updateSelectRightId('')
}

const routeHandle = route => {
  if (route.name === 'home') {
    expandMenuList = []
    sessionStorage.setItem('cbecB2cIsExpand', '0')
    commonStore.updateSelectLeftId('')
    commonStore.updateSelectRightId('')
  }
}

const gotoRouteHandle = menu => {
  if (route.name === menu.url) {
    expandMenuList = []
    commonStore.updateSidebarFold(true)
    sessionStorage.setItem('cbecB2cIsExpand', '0')
    commonStore.updateSelectRightId('')
    commonStore.updateSelectLeftId(menu.menuId || '')
    return
  }
  expandMenuList = []
  commonStore.updateSidebarFold(true)
  sessionStorage.setItem('cbecB2cIsExpand', '0')
  commonStore.updateSelectRightId('')
  commonStore.updateSelectLeftId(menu.menuId || '')
  router.push({ name: menu.url })
}

const expandMenu = menu => {
  expandMenuList = menu.list || []
  commonStore.updateSidebarFold(menu.list === null)
  const id1 = commonStore.selectLeftId
  commonStore.updateSelectLeftId(menu.menuId || '')
  const id2 = commonStore.selectLeftId
  if (menu.list) {
    sessionStorage.setItem('cbecB2cIsExpand', '1')
  }
  if (id1 !== id2) {
    routeJump(menu)
  }
}

const routerStore = useRouterStore()
const routeJump = menu => {
  const routes = menu.list
  for (let i = 0; i < routes.length; i++) {
    if (!routes[i].hidden && !routes[i].list) {
      // 修改缓存页面
      routerStore.updateIncludePageList([routes[i].url])
      routerStore.pushHisPageList([routes[i].url])
      router.push({ name: routes[i].url })
      break
    } else if (routes[i].list) {
      let flag = false
      for (let j = 0; j < routes[i].list.length; j++) {
        if (!routes[i].list[j].hidden) {
          // 修改缓存页面
          routerStore.updateIncludePageList([routes[i].list[j].url])
          routerStore.pushHisPageList([routes[i].list[j].url])
          router.push({ name: routes[i].list[j].url })
          flag = true
          break
        }
      }
      if (flag) {
        break
      }
    }
  }
}
</script>

<style scoped>
.menu-mod {
  display: flex;
}

.menu-right-con {
  position: absolute;
  z-index: 1;
  left: 100px;
}
.menu-left {
  background: #222222;
  color: #ffffff !important;
  width: 100px;
  height: calc(100vh - 50px);
  overflow-y: auto;
  /* border-top: 1px solid #EBEDF0; */
}
.menu-mod .menu-left-item {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.menu-mod .menu-left-item1 {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  text-align: left;
  padding-left: 12px;
}
.menu-mod .menu-left ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
  width: 100px;
  text-align: center;
}
.menu-mod .menu-right ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
  width: 130px;
  text-align: center;
}
.menu-left li {
  background: #222222;
  color: #fff;
  height: 40px;
  cursor: pointer;
  font-size: 14px;
  stroke: #fff !important;
}

.menu-right li {
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 14px;
  color: #333333;
}

/* 鼠标移动到选项上修改背景颜色 */
.menu-left li:hover {
  background-color: #ffffff;
  color: #155bd4;
  stroke: #155bd4 !important;
}
/* 鼠标移动到选项上修改背景颜色 */
.menu-right li:hover {
  background-color: rgba(21, 91, 212, 0.1);
  color: #155bd4;
}
.menu-right li:hover {
  background-color: rgba(21, 91, 212, 0.1);
  color: #155bd4;
}
.menu-left-active {
  background-color: #ffffff;
  color: #155bd4;
  stroke: #155bd4 !important;
}
.item-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-block;
  max-width: 70px;
}
</style>

// 网站配置默认信息
const configDefInfo = {
  // 登录logo
  bsLoginLogoImg: new URL('@/assets/img/website-config/login-logo.png', import.meta.url).href,
  // 登录背景
  bsLoginBgImg: new URL('@/assets/img/website-config/login-bg.jpg', import.meta.url).href,
  // 版权声明-中文
  bsCopyright: 'Copyright © 2018-2099 广州蓝海创新科技有限公司',
  // 标题文本-中文
  bsTitleContent: '蓝海-后台端',
  // 标题文本-英文
  bsTitleContentEn: 'LANHAI-PLATFORM',
  // 网站标题图标
  bsTitleImg: new URL('@/assets/img/website-config/title-icon.png', import.meta.url).href,
  // 菜单栏顶部图标
  bsTopBarIcon: new URL('@/assets/img/website-config/menu-top-icon.png', import.meta.url).href,
  // 菜单展开文本-中文
  bsMenuTitleOpen: '蓝海商城-后台端',
  // 菜单收缩文本-中文
  bsMenuTitleClose: '蓝海'
}

// 格式化配置信息
const formatConfigInfo = function (config) {
  const data = {}
  config = JSON.parse(JSON.stringify(config))
  // 为空使用默认配置
  data.bsLoginLogoImg = config.bsLoginLogoImg || configDefInfo.bsLoginLogoImg
  data.bsLoginBgImg = config.bsLoginBgImg || configDefInfo.bsLoginBgImg
  data.bsCopyrightCn = config.bsCopyrightCn || configDefInfo.bsCopyrightCn
  data.bsCopyrightEn = config.bsCopyrightEn || configDefInfo.bsCopyrightEn
  data.bsTitleContentCn = config.bsTitleContentCn || configDefInfo.bsTitleContentCn
  data.bsTitleContentEn = config.bsTitleContentEn || configDefInfo.bsTitleContentEn
  data.bsTitleImg = config.bsTitleImg || configDefInfo.bsTitleImg
  data.bsTopBarIcon = config.bsTopBarIcon || configDefInfo.bsTopBarIcon
  data.bsMenuTitleOpenCn = config.bsMenuTitleOpenCn || configDefInfo.bsMenuTitleOpenCn
  data.bsMenuTitleOpenEn = config.bsMenuTitleOpenEn || configDefInfo.bsMenuTitleOpenEn
  data.bsMenuTitleCloseCn = config.bsMenuTitleCloseCn || configDefInfo.bsMenuTitleCloseCn
  data.bsMenuTitleCloseEn = config.bsMenuTitleCloseEn || configDefInfo.bsMenuTitleCloseEn
  return Object.assign(config, data)
}

export { configDefInfo, formatConfigInfo }

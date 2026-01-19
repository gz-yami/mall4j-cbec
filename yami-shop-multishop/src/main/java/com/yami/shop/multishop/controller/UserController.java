package com.yami.shop.multishop.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yami.shop.bean.model.User;
import com.yami.shop.bean.param.*;
import com.yami.shop.common.enums.SysTypeEnum;
import com.yami.shop.common.i18n.I18nMessage;
import com.yami.shop.common.response.ServerResponseEntity;
import com.yami.shop.common.util.*;
import com.yami.shop.security.common.bo.UidInfoBO;
import com.yami.shop.security.common.manager.TokenStore;
import com.yami.shop.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@Tag(name = "用户")
public class UserController {

    private final UserService userService;
    private final TokenStore tokenStore;



    @GetMapping("/page")
    @PreAuthorize("@pms.hasPermission('plateform:user:page')")
    @Operation(summary = "分页获取用户基本信息", description = "分页获取用户基本信息")
    public ServerResponseEntity<IPage<User>> page( User user, PageParam<User> page) {
        IPage<User> userPage = userService.getUserPage(page, user);
        return ServerResponseEntity.success(userPage);
    }

    @GetMapping("/info/{userId}")
    @PreAuthorize("@pms.hasPermission('plateform:user:info')")
    @Operation(summary = "获取信息", description = "获取信息")
    public ServerResponseEntity<UserManagerParam> info(@PathVariable("userId") String userId) {
        UserManagerParam param = userService.getuserInfo(userId);
        return ServerResponseEntity.success(param);
    }

    @PutMapping
    @PreAuthorize("@pms.hasPermission('plateform:user:update')")
    @Operation(summary = "修改", description = "修改")
    public ServerResponseEntity<Void> update(@RequestBody User user) {
        User dbUser = userService.getUserByUserId(user.getUserId());
        user.setModifyTime(new Date());
        user.setNickName(user.getNickName());

        tokenStore.deleteTokenByUidInfo(new UidInfoBO(SysTypeEnum.ORDINARY, user.getUserId()));
        // 将用户踢出设备
        if (Objects.equals(user.getStatus(), 0)) {
            userService.updateUsrInfo(user,SysTypeEnum.PLATFORM);
        }else{
            userService.updateById(user);
        }
        return ServerResponseEntity.success();
    }

    @GetMapping("/userPage")
    @Operation(summary = "分页获取用户及扩展信息", description = "分页获取用户及扩展信息")
    @PreAuthorize("@pms.hasPermission('admin:user:page')")
    public ServerResponseEntity<IPage<UserManagerParam>> userPage(PageParam<User> page, UserManagerReqParam user) {
        Integer lang = I18nMessage.getLang();
        IPage<UserManagerParam> userPage = userService.getUserInfoPage(page, user, lang);
        return ServerResponseEntity.success(userPage);
    }

}

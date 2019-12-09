package com.ljtao.sbflexiblea.web.controller;

import com.github.pagehelper.PageInfo;
import com.ljtao.sbflexiblea.common.core.controller.BaseController;
import com.ljtao.sbflexiblea.common.core.domain.AjaxResult;
import com.ljtao.sbflexiblea.common.core.page.TableDataInfo;
import com.ljtao.sbflexiblea.domian.Params;
import com.ljtao.sbflexiblea.domian.master.SysDept;
import com.ljtao.sbflexiblea.domian.master.SysDictType;
import com.ljtao.sbflexiblea.domian.master.SysUser;
import com.ljtao.sbflexiblea.service.master.SysDictDataService;
import com.ljtao.sbflexiblea.service.master.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/system/user")
public class UserController extends BaseController {
    private String prefix="system/user";
    @Autowired
    private SysDictDataService dictDataService;
    @Autowired
    private SysUserService userService;

    @RequestMapping
    public String toUser(ModelMap modelMap){
        modelMap.put("dict",dictDataService.selectDictDataByType("sys_normal_disable"));
        return prefix+"/user";
    }
    @RequestMapping("/list")
    @ResponseBody
    public TableDataInfo list(SysUser sysUser, SysDept sysDept, Params params){
        List<SysUser> sysUsers = userService.selectUserList(sysUser, params);
        TableDataInfo resData=new TableDataInfo();
        resData.setCode(0);
        resData.setRows(sysUsers);
        resData.setTotal(userService.selectUserListCount(sysUser,params));
        return resData;
    }
}

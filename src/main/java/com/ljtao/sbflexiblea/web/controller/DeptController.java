package com.ljtao.sbflexiblea.web.controller;

import com.ljtao.sbflexiblea.common.constant.UserConstants;
import com.ljtao.sbflexiblea.common.core.controller.BaseController;
import com.ljtao.sbflexiblea.common.core.domain.AjaxResult;
import com.ljtao.sbflexiblea.common.core.domain.Ztree;
import com.ljtao.sbflexiblea.domian.master.SysDept;
import com.ljtao.sbflexiblea.domian.master.SysDictData;
import com.ljtao.sbflexiblea.service.master.SysDeptService;
import com.ljtao.sbflexiblea.service.master.SysDictDataService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/system/dept")
public class DeptController extends BaseController {
    @Autowired
    private SysDictDataService dictDataService;
    @Autowired
    private SysDeptService deptService;
    private String prefix="system/dept";
    @RequestMapping("")
    public String dept(){
        return prefix+"/dept";
    }
    @RequestMapping("/list")
    @ResponseBody
    public List<SysDept> list(SysDept sysDept){
        List<SysDept> list = deptService.selectDeptList(sysDept);
        return list;
    }
    @RequestMapping("/edit/{deptId}")
    public String edit(@PathVariable("deptId") Long deptId, ModelMap modelMap){
        SysDept dept = deptService.selectDeptById(deptId);
        if(dept!=null && deptId==100L){
            dept.setParentName("无");
        }
        List<SysDictData> dictList = dictDataService.selectDictDataByType("sys_normal_disable");
        modelMap.put("dept",dept);
        modelMap.put("dictList",dictList);
        return prefix+"/edit";

    }
    @RequestMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(SysDept dept){
        //检查部门名称是否已存在
        if(UserConstants.DEPT_NAME_NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept))){
            return error("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        else if(dept.getParentId().equals(dept.getDeptId())){
            return error("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        }
        else if(deptService.checkIsRelationError(dept)){
            return error("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己的子部门");
        }
        return toAjax(deptService.updateDept(dept));
    }
    /**
     * 校验部门名称
     */
    @RequestMapping("/checkDeptNameUnique")
    @ResponseBody
    public String checkDeptNameUnique(SysDept dept)
    {
        return deptService.checkDeptNameUnique(dept);
    }
    @RequestMapping("/selectDeptTree/{deptId}")
    public String selectDeptTree(@PathVariable("deptId") Long deptId,ModelMap model){
        List<SysDept> list = deptService.selectDeptList(new SysDept());
        model.put("dept",deptService.selectDeptById(deptId));
        return prefix+"/tree";
    }

    /**
     * 加载部门列表树
     */
    @RequestMapping("/treeData")
    @ResponseBody
    public List<Ztree> treeData(){
        List<Ztree> ztrees = deptService.selectDeptTree(new SysDept());
        return ztrees;
    }




    @RequestMapping("/add/{parentId}")
    public String add(@PathVariable("parentId") Long parentId,ModelMap modelMap){
        SysDept dept = deptService.selectDeptById(parentId);
        if(dept!=null){
            dept.setParentName("无");
        }
        List<SysDictData> dictList = dictDataService.selectDictDataByType("sys_normal_disable");
        modelMap.put("dept",dept);
        modelMap.put("dictList",dictList);
        return prefix+"/add";
    }
    @RequestMapping("/add")
    @ResponseBody
    public AjaxResult addDept(SysDept dept)  {
        if (UserConstants.DEPT_NAME_NOT_UNIQUE.equals(deptService.checkDeptNameUnique(dept)))
        {
            return error("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        try {
            return toAjax(deptService.insertDept(dept));
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }
    @RequestMapping("/remove/{deptId}")
    @ResponseBody
    public AjaxResult removeDept(@PathVariable("deptId")  Long deptId){
        if(deptService.selectDeptCount(deptId)>0){
            return AjaxResult.warn("该部门有子部门，不能删除！");
        }
        else if(deptService.checkDeptExistUser(deptId)>0){
            return AjaxResult.warn("该部门有用户，不能删除！");
        }
        return toAjax(deptService.deleteDeptById(deptId));
    }
}

package com.ljtao.sbflexiblea.service.master;


import com.ljtao.sbflexiblea.common.constant.UserConstants;
import com.ljtao.sbflexiblea.common.core.domain.Ztree;
import com.ljtao.sbflexiblea.dao.master.SysDeptMapper;
import com.ljtao.sbflexiblea.domian.master.SysDept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SysDeptService {
    @Autowired
    private SysDeptMapper deptMapper;
    public List<SysDept> selectDeptList(SysDept sysDept){
        return deptMapper.selectDeptList(sysDept);
    }
    public SysDept selectDeptById(Long deptId){
        return deptMapper.selectDeptById(deptId);
    }

    public String checkDeptNameUnique(SysDept dept) {
        Long deptId=dept.getDeptId()==null?-1L:dept.getDeptId();
        deptMapper.selectDeptById(dept.getDeptId());
        SysDept info=deptMapper.checkDeptNameUnique(dept.getDeptName(),dept.getParentId());
        if(info!=null && deptId.longValue()!=info.getDeptId().longValue()){
            return UserConstants.DEPT_NAME_NOT_UNIQUE;
        }
        return UserConstants.DEPT_NAME_UNIQUE;
    }
    @Transactional
    public int updateDept(SysDept dept) {
        SysDept newParentDept=selectDeptById(dept.getParentId());
        SysDept oldSysDept = selectDeptById(dept.getDeptId());
        if(newParentDept !=null && oldSysDept!=null){
            String newAncestors=newParentDept.getAncestors()+","+newParentDept.getDeptId();
            String oldAncestors=oldSysDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
        }
        int result=deptMapper.updateDept(dept);
        if(UserConstants.DEPT_NORMAL.equals(dept.getStatus())){
            updateParentDeptStatus(dept);
        }
        return result;
    }
    /**
     * 修改子元素关系
     *
     * @param deptId 被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors)
    {
        List<SysDept> childList = deptMapper.selectChildrenDeptById(deptId);
        for(SysDept child:childList){
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors,newAncestors));
        }
        if (childList!=null && childList.size()>0){
            deptMapper.updateDeptChildren(childList);
        }
    }
    /**
     * 修改该部门的父级部门状态
     *
     * @param dept 当前部门
     */
    private void updateParentDeptStatus(SysDept dept)
    {
        String updateBy = dept.getUpdateBy();
        dept = deptMapper.selectDeptById(dept.getDeptId());
        dept.setUpdateBy(updateBy);
        deptMapper.updateDeptStatus(dept);
    }

    /**
     * 查询部门管理树
     *
     * @param dept 部门信息
     * @return 所有部门信息
     */
    public List<Ztree> selectDeptTree(SysDept dept)
    {
        List<SysDept> deptList = deptMapper.selectDeptList(dept);
        List<Ztree> ztrees = initZtree(deptList);
        return ztrees;
    }
    /**
     * 对象转部门树
     *
     * @param deptList 部门列表
     * @return 树结构列表
     */
    public List<Ztree> initZtree(List<SysDept> deptList)
    {
        return initZtree(deptList, null);
    }
    /**
     * 对象转部门树
     *
     * @param deptList 部门列表
     * @param roleDeptList 角色已存在菜单列表
     * @return 树结构列表
     */
    public List<Ztree> initZtree(List<SysDept> deptList, List<String> roleDeptList)
    {

        List<Ztree> ztrees = new ArrayList<Ztree>();
        boolean isCheck = (roleDeptList!=null);
        for (SysDept dept : deptList)
        {
            if (UserConstants.DEPT_NORMAL.equals(dept.getStatus()))
            {
                Ztree ztree = new Ztree();
                ztree.setId(dept.getDeptId());
                ztree.setpId(dept.getParentId());
                ztree.setName(dept.getDeptName());
                ztree.setTitle(dept.getDeptName());
                if (isCheck)
                {
                    ztree.setChecked(roleDeptList.contains(dept.getDeptId() + dept.getDeptName()));
                }
                ztrees.add(ztree);
            }
        }
        return ztrees;
    }

    public int insertDept(SysDept dept) throws Exception {
        SysDept info = deptMapper.selectDeptById(dept.getParentId());
        // 如果父节点不为"正常"状态,则不允许新增子节点
        if (!UserConstants.DEPT_NORMAL.equals(info.getStatus()))
        {
            throw new Exception("部门停用，不允许新增");
        }
        dept.setAncestors(info.getAncestors() + "," + dept.getParentId());
        return deptMapper.insertDept(dept);
    }
    public int selectDeptCount(Long deptId){
        SysDept dept=new SysDept();
        dept.setParentId(deptId);
        return deptMapper.selectDeptCount(dept);
    }
    public int checkDeptExistUser(Long deptId){
        return deptMapper.checkDeptExistUser(deptId);
    }
    public int deleteDeptById(Long deptId){
        return deptMapper.deleteDeptById(deptId);
    }
    /*
        检查是否父子关系错误
     */
    public boolean checkIsRelationError(SysDept dept) {
        SysDept newPDept = deptMapper.selectDeptById(dept.getParentId());
        String[] split = newPDept.getAncestors().split(",");
        Set<Long> set=new HashSet<>();
        for (String s:split){
            set.add(Long.parseLong(s));
        }
        boolean contains = set.contains(dept.getDeptId());
        return contains;
    }
}

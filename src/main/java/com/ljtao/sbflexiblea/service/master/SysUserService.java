package com.ljtao.sbflexiblea.service.master;

import com.ljtao.sbflexiblea.dao.master.SysUserMapper;
import com.ljtao.sbflexiblea.domian.Params;
import com.ljtao.sbflexiblea.domian.master.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;
    public List<SysUser> selectUserList(SysUser user, Params params){

        int offset=(params.getPageNum()-1)*params.getPageSize();
        params.setOffset(offset);
        return sysUserMapper.selectUserList(user,params);
    }
    public int selectUserListCount(SysUser user, Params params){
        int total = sysUserMapper.selectUserListCount(user, params);
        return total;
    }
}

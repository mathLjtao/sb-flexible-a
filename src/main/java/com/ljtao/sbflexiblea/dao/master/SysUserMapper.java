package com.ljtao.sbflexiblea.dao.master;

import com.ljtao.sbflexiblea.domian.Params;
import com.ljtao.sbflexiblea.domian.master.SysDept;
import com.ljtao.sbflexiblea.domian.master.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper {
    public List<SysUser> selectUserList(@Param("user") SysUser user,@Param("params") Params params);
}

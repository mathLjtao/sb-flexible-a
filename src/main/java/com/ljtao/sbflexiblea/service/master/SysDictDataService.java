package com.ljtao.sbflexiblea.service.master;


import com.ljtao.sbflexiblea.dao.master.SysDictDataMapper;
import com.ljtao.sbflexiblea.domian.master.SysDictData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SysDictDataService {
    @Autowired
    private SysDictDataMapper dictDataMapper;
    public List<SysDictData> selectDictDataByType(String dictType){
        return dictDataMapper.selectDictDataByType(dictType);
    }
}

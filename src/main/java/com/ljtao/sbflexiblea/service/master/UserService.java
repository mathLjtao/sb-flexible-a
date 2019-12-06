package com.ljtao.sbflexiblea.service.master;


import com.ljtao.sbflexiblea.dao.master.UserDao;
import com.ljtao.sbflexiblea.domian.master.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    public User findByName(String name){
        return userDao.findByName(name);
    }
}

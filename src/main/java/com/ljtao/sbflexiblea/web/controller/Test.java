package com.ljtao.sbflexiblea.web.controller;


import com.ljtao.sbflexiblea.common.core.domain.AjaxResult;
import com.ljtao.sbflexiblea.common.utils.StringUtils;
import com.ljtao.sbflexiblea.domian.master.User;
import com.ljtao.sbflexiblea.service.master.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Api(value="测试接口Controller",tags = { "测试访问接口" })
public class Test {
    @Autowired
    private UserService userServer;
    @ApiOperation(value = "根据名字获取电影fun1")
    @RequestMapping("/fun1")
    public String fun1(){
        System.out.println("console : success!");
        return "success!";
    }
    @RequestMapping("/fun2")
    public User fun2(){
        return userServer.findByName("1");
    }
    /**
     * 测试springboot + swagger 的效果
     * swagger访问地址 http://localhost:8081/swagger-ui.html#/
     * 如果有设置项目访问前缀，要记得加上 http://localhost:8081/flexible/swagger-ui.html#/
     * @param fileName
     * @return
     */
    @RequestMapping("/fun3")
    @ApiOperation(value = "根据名字获取电影")
    @ApiResponses(value = { @ApiResponse(code = 0, message = "成功"), @ApiResponse(code = 500, message = "失败"),
            @ApiResponse(code = 1001, message = "缺少参数") })
    public AjaxResult fun3(@ApiParam("电影名称") @RequestParam("fileName") String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return AjaxResult.error("参数为空");
        }
        return AjaxResult.success("操作成功","电影名："+fileName);

    }
}

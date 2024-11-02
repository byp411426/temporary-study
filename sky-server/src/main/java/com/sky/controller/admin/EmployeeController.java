package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation(value = "员工退出")
    public Result<String> logout() {
        return Result.success();
    }


//    实现新增员工功能.post方式请求
    @PostMapping
    @ApiOperation("新增员工功能")
    public Result save(@RequestBody EmployeeDTO employeeDTO){//用DTo接收前段提交的参数,json格式的数据需要加@RequestBody

        log.info("新增员工：{}",employeeDTO);
        System.out.println("currrent thread_id = " + Thread.currentThread().getId());

        employeeService.save(employeeDTO);

        return Result.success();
    }

@GetMapping("/page")
@ApiOperation("员工分页查询")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){//接受前端提交的参数，Query格式并不是JSON格式
        log.info("员工分页查询,参数为：{}",employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }
//非查询类，不需要泛型
    @PostMapping("/status/{status}")
    @ApiOperation("员工状态修改")
    public Result start_stop(@PathVariable("status") Integer status,  Long id){ //路径传参要加PathVariable注解，用于接受路径传参
        log.info("启用禁用员工账号：状态：{},id：{}",status,id);
        employeeService.start_stop(status,id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询员工信息")
    public Result<Employee> getById(@PathVariable Long id){

       Employee employee =  employeeService.getById(id);
       return Result.success(employee);

    }

@PutMapping
@ApiOperation("编辑员工信息")
    public Result update(@RequestBody EmployeeDTO employeeDTO){  //提交的是json格式的数据RequestBody注解，接受前端提交的数据EmployeeDTO

        log.info("编辑员工信息：{}",employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }
}

package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @Override
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus().equals(StatusConstant.DISABLE)) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @return
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        // 1.创建一个Entity对象
        Employee employee = new Employee();

        // 1.设置Entity中DTO有的属性
        BeanUtils.copyProperties(employeeDTO, employee);

        // 2.设置Entity的其他属性
        employee.setStatus(StatusConstant.ENABLE);
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        // 3.调用mapper把Entity对象传入
        employeeMapper.insert(employee);
    }

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult queryPage(EmployeePageQueryDTO employeePageQueryDTO) {
        // 1.开启分页
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        // 2.调用mapper查询
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        // 3.封装结果并返回
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 修改员工状态
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        // 1.创建一个Entity对象
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();

        // 2.调用mapper修改
        employeeMapper.update(employee);
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        // 1.调用mapper查询
        Employee employee = employeeMapper.getById(id);
        // 2.把密码隐藏
        employee.setPassword("****");
        // 3.返回用户
        return employee;
    }

    /**
     * 修改员工信息
     * @param employeeDTO
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        // 1.创建一个Entity对象
        Employee employee = new Employee();

        // 2.先把DTO属性复制给Entity对象
        BeanUtils.copyProperties(employeeDTO, employee);

        // 3.调用mapper修改
        employeeMapper.update(employee);
    }

    @Override
    public void editPassword(PasswordEditDTO passwordEditDTO) {
        // 1.根据员工id查询该员工的密码
        Employee employee = employeeMapper.getById(BaseContext.getCurrentId());

        // 2.对旧密码进行加密
        String oldPassword = DigestUtils.md5DigestAsHex(passwordEditDTO.getOldPassword().getBytes());

        // 3.比对密码是否正确
        if (employee.getPassword().equals(oldPassword)) {
            // 4.如果密码正确，则进行密码修改
            employee.setPassword(DigestUtils.md5DigestAsHex(passwordEditDTO.getNewPassword().getBytes()));
            employeeMapper.update(employee);
        } else {
            // 5.如果密码错误，则抛出密码错误异常
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
    }
}

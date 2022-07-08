package com.example.mybatis.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mybatis.system.entity.City;
import com.example.mybatis.system.service.ICityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author songwz
 * @since 2022-07-07
 */
@RestController
@RequestMapping("/system")
@Slf4j
public class CityController {

    @Autowired
    private ICityService iCityService;

    @RequestMapping(value = "/city/insert", method = RequestMethod.POST)
    public void insertCity(@RequestParam(name = "action") String action) {
        City city = new City();
        city.setCountry("CN");
        city.setName("China");
        city.setState("SD");
        //iCityService.save(city);

        city.setId(21);
        UpdateWrapper<City> updateWrapper = new UpdateWrapper<>(city);
        iCityService.updateById(city);

        Page<City> page = new Page<>(1,10);
        QueryWrapper<City> queryWrapper = new QueryWrapper<>();
        page = iCityService.page(page,queryWrapper);
        log.info(page.toString());
        log.info(page.getRecords().toString());
    }

}

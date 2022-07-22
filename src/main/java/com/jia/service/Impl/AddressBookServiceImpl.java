package com.jia.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jia.common.BaseContext;
import com.jia.domain.AddressBook;
import com.jia.mapper.AddressBookMapper;
import com.jia.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.servlet.http.HttpSession;
import java.util.List;

@Service
@Slf4j
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    @Override
    public List<AddressBook> AddressBooklist(HttpSession session) {
        Long userId=(Long) session.getAttribute("user");
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,userId);
        List<AddressBook> list = this.list(queryWrapper);
        return list;
    }

    @Override
    public void addAddressBook(HttpSession session, AddressBook addressBook) {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        Long userId=(Long) session.getAttribute("user");
        addressBook.setUserId(userId);
        this.save(addressBook);
    }

    @Transactional
    public void AddressBookDefault(AddressBook addressBook) {
        //搜索所有的该用户下的默认地址
        Long userId = BaseContext.getCurrentId();
        LambdaUpdateWrapper<AddressBook> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,userId);
        queryWrapper.set(AddressBook::getIsDefault,0);
        this.update(queryWrapper);

        addressBook.setIsDefault(1);
        this.updateById(addressBook);

    }

}

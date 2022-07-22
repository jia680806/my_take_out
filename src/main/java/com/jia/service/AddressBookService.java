package com.jia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jia.domain.AddressBook;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface AddressBookService extends IService<AddressBook> {
    public List<AddressBook> AddressBooklist(HttpSession session);
    public void addAddressBook(HttpSession session, AddressBook addressBook);
    public void AddressBookDefault(AddressBook addressBook);

}

package com.jia.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jia.common.BaseContext;
import com.jia.common.R;
import com.jia.domain.AddressBook;
import com.jia.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.List;



@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @GetMapping("/list")
    public R<List<AddressBook>> list(HttpSession session){
        List<AddressBook> addressBooks = addressBookService.AddressBooklist(session);
        return R.success(addressBooks);
    }

    @PostMapping
    public R<String> add(HttpSession session,@RequestBody AddressBook addressBook){
        addressBookService.addAddressBook(session,addressBook);
        return R.success("添加成功");
    }

    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook){
        addressBookService.AddressBookDefault(addressBook);
        return R.success("设置默认地址成功");

    }
    @GetMapping("/default")
    public R<AddressBook> getDefault() {
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,userId);
        queryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        return R.success(addressBook);
    }


    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }
    @PutMapping
    public R<String> edit(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }
    @DeleteMapping
    public R<String> delete(@RequestParam Long ids){
        addressBookService.removeById(ids);
        return R.success("删除成功");

    }


}

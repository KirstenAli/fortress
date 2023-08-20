package com.fortress.security.controller;

import com.fortress.security.dto.DTOInterface;
import com.fortress.security.entity.EntityInterface;
import com.fortress.security.service.CRUDService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class CRUDController<T extends EntityInterface, U extends DTOInterface>{
    private final CRUDService<T, U> CRUDService;

    public CRUDController(CRUDService<T,U> CRUDService) {
        this.CRUDService = CRUDService;
    }

    @PostMapping("add")
    void add(@RequestBody U dto){
        CRUDService.add(dto);
    }

    @GetMapping("get/{id}")
    U get(@PathVariable String id){
        return CRUDService.get(id);
    }

    @PostMapping("update")
    void update(@RequestBody U dto){
        CRUDService.update(dto);
    }

    @PostMapping("delete")
    void delete(@RequestBody U dto){
        CRUDService.delete(dto);
    }

    @GetMapping("getAll")
    List<U> getAll(){
        return CRUDService.getAll();
    }

    @PostMapping("deleteAllById")
    void deleteAllById(@RequestBody Iterable<String> ids){
        CRUDService.deleteAllById(ids);
    }
}

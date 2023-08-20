package com.fortress.controller;

import com.fortress.entity.EntityInterface;
import com.fortress.dto.DTOInterface;
import com.fortress.service.CRUDService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class CRUDController<T extends EntityInterface, U extends DTOInterface>{
    private final com.fortress.service.CRUDService<T, U> CRUDService;

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

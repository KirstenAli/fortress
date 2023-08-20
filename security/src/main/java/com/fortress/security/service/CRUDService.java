package com.fortress.security.service;

import com.fortress.security.dto.DTOInterface;
import com.fortress.security.entity.EntityInterface;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.stream.Collectors;

public abstract class CRUDService<T extends EntityInterface, U extends DTOInterface> {

    private final MongoRepository<T, String> repository;

    @Autowired
    protected ModelMapper modelMapper;

    public CRUDService(MongoRepository<T, String> repository) {
        this.repository = repository;
    }

    public U get(String id){
        var entity = findById(id);

        if(entity==null)
            return null;

        return mapToDTO(entity);
    }

    public void update(U dto){
        String id = dto.getId();
        T currentEntity = findById(id);
        assert currentEntity != null;

        updateEntity(dto, currentEntity);

        repository.save(currentEntity);
    }

    public void add(U dto){
        if(beforeAdd(dto)) {
            var entity = (T) dto.getEntity();
            repository.save(entity);
            dto.setId(entity.getId());
            update(dto);
            afterAdd(dto);
        }
    }

    public List<U> getAll(){
        List<T> users = repository.findAll();

        return mapToDTO(users);
    }

    public void deleteAllById(Iterable<String> ids){
        repository.deleteAllById(ids);
    }

    public abstract void updateEntity(U dto, T currentEntity);

    public abstract boolean beforeAdd(U dto);
    public abstract void afterAdd(U dto);

    public void delete(U dto){
        repository.deleteById(dto.getId());

    }

    public T findById(String id){
        return repository.findById(id).orElse(null);
    }

    public List<U> mapToDTO(List<T> entities){
        return entities.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public U mapToDTO(T entity){
        var dto = entity.getDTO();
        modelMapper.map(entity, dto);

        return (U) dto;
    }

    public T mapToEntity(U dto){
        var entity = dto.getEntity();
        modelMapper.map(dto, entity);

        return (T) entity;
    }
}

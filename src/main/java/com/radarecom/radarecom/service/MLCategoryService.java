package com.radarecom.radarecom.service;

import com.radarecom.radarecom.dto.response.MlCategoryResponse;
import com.radarecom.radarecom.entity.MLCategory;
import com.radarecom.radarecom.exception.NotFoundException;
import com.radarecom.radarecom.repository.MLCategoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MLCategoryService {

    @Autowired
    private MLCategoryRepository mlCategoryRepository;

    public List<MlCategoryResponse> getParentCategories(){
        var categories =  mlCategoryRepository.findAllByLevel(0);
        var response = new ArrayList<MlCategoryResponse>();
        categories.forEach((category) -> {
            var categoryDTO = MlCategoryResponse.builder().build();
            BeanUtils.copyProperties(category, categoryDTO);
            categoryDTO.setId(category.getId().getId());
            categoryDTO.setParentId(category.getId().getParentId());
            response.add(categoryDTO);
        });
        return response;
    }

    public MlCategoryResponse getCategoryById(String categoryId){
        var category = getCategoryEntity(categoryId);
        var children = mlCategoryRepository.findAllByIdParentId(category.getId().getId());
        var childrenList = new ArrayList<MlCategoryResponse>();
        children.forEach((mlCategory) -> {
            var categoryDTO = MlCategoryResponse
                    .builder()
                    .id(mlCategory.getId().getId())
                    .name(mlCategory.getName())
                    .build();
            childrenList.add(categoryDTO);
        });
        var response = MlCategoryResponse
                .builder()
                .id(category.getId().getId())
                .parentId(category.getId().getParentId())
                .name(category.getName())
                .children(childrenList)
                .build();
        return response;
    }

    public MLCategory getCategoryEntity(String categoryId){
        return mlCategoryRepository.findByIdId(categoryId).orElseThrow(() -> new NotFoundException(String.format("Category [%s] not found.", categoryId)));
    }

}

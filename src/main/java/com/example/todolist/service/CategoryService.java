package com.example.todolist.service;

import com.example.todolist.dto.request.CategoryRequest;
import com.example.todolist.dto.response.CategoryResponse;
import com.example.todolist.exception.DataNotFoundException;
import com.example.todolist.exception.DuplicateDataException;
import com.example.todolist.model.Category;
import com.example.todolist.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoryResponse> findAll() {
        try {
            return categoryRepository.findAll()
                    // {}
                    .stream()
                    // { name:... }
                    .map(this::convertToResponse)
                    .toList(); // [{}]
        } catch (Exception e) {
            throw new RuntimeException("Failed to get data categories");
        }
    }

    public CategoryResponse create(CategoryRequest categoryRequest) {
        try {
            if(categoryRepository.findByName(categoryRequest.getName()).isPresent()) {
                throw new DuplicateDataException("category already exist");
            }
            Category category = new Category();
            category.setName(categoryRequest.getName());
            category = categoryRepository.save(category);
            return convertToResponse(category);
        } catch (DuplicateDataException e) {
            throw e; // error yang dilempar ke controller
        } catch (Exception e) {
            throw new RuntimeException("Failed to create category");
        }
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest){
        try{
            Category category = categoryRepository.findById(id)
                    .orElseThrow(()-> new DataNotFoundException("Category with id " + id + " not found")) ;
            if(categoryRepository.findByName(categoryRequest.getName()).isPresent()) {
                throw new DuplicateDataException("category already exist");
            } else {
                category.setName(categoryRequest.getName());
                category = categoryRepository.save(category);
                return convertToResponse(category);
            }
        } catch (DataNotFoundException e) {
            throw e;
        } catch (DuplicateDataException e) {
            throw e; // error yang dilempar ke controller
        } catch (Exception e) {
            throw new RuntimeException("Failed to update category" + e.getMessage(), e);
        }
    }

    public void deleteCategory(Long id) {
        try {
            if(!categoryRepository.existsById(id)) {
                throw new DataNotFoundException("Category with id " + id + " not found");
            }
            categoryRepository.deleteById(id);
        } catch (DataNotFoundException e){
            throw e;
        }catch (Exception e) {
            throw new RuntimeException("Failed to delete category" + e.getMessage(), e);
        }
    }

    public Optional<CategoryResponse> findByName(String name) {
        try {
            return categoryRepository.findByName(name)
                    .map(this::convertToResponse);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find category by name: "+ e.getMessage());
        }
    }

    private CategoryResponse convertToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        return response;
    }

}

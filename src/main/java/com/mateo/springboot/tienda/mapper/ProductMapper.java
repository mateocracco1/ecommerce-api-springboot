package com.mateo.springboot.tienda.mapper;


import com.mateo.springboot.tienda.dto.product.ProductCreateDto;
import com.mateo.springboot.tienda.dto.product.ProductDto;
import com.mateo.springboot.tienda.dto.product.ProductUpdateDto;
import com.mateo.springboot.tienda.models.Category;
import com.mateo.springboot.tienda.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {




    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ProductDto toDto(Product product);



    @Mapping(target = "id", ignore = true) //
    @Mapping(source = "category", target = "category") //
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "description", source = "dto.description")
    Product toEntity(ProductCreateDto dto, Category category);



    @Mapping(target = "id", ignore = true)
    @Mapping(source = "category", target = "category")
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "description", source = "dto.description")
    void updateProduct(@MappingTarget Product product, ProductUpdateDto dto, Category category);

}

package com.mateo.springboot.tienda.mapper;


import com.mateo.springboot.tienda.dto.product.ProductCreateDto;
import com.mateo.springboot.tienda.dto.product.ProductDto;
import com.mateo.springboot.tienda.dto.product.ProductUpdateDto;
import com.mateo.springboot.tienda.models.Category;
import com.mateo.springboot.tienda.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {


    // 1. De Entidad a DTO
    // MapStruct es inteligente, pero si los nombres cambian, se lo indicamos explícitamente:
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ProductDto toDto(Product product);


    // 2. De DTO a Entidad (Creación)
    @Mapping(target = "id", ignore = true) // El ID lo genera la base de datos
    @Mapping(source = "category", target = "category") // Tomamos el 2do parámetro y lo asignamos
    Product toEntity(ProductCreateDto dto, Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "category", target = "category")
    void updateProduct(Product product, ProductUpdateDto dto, Category category);

}

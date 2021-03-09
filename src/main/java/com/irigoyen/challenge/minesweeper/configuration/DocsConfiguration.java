package com.irigoyen.challenge.minesweeper.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Configuration class for application documentation
 */
@Configuration
@OpenAPIDefinition
public class DocsConfiguration {
    @Bean
    public OpenAPI customOpenAPI() {
        //These schemas are needed to show a valid test example in swagger docs
        Schema newGameSchema = new Schema<Map<String, Object>>()
                .addProperties("userId",new IntegerSchema().example(1))
                .addProperties("rows",new IntegerSchema().example(4))
                .addProperties("columns",new IntegerSchema().example(5))
                .addProperties("mines",new IntegerSchema().example(4));
        Schema newUserSchema = new Schema<Map<String, Object>>()
                .addProperties("name",new StringSchema().example("John123"))
                .addProperties("password",new StringSchema().example("P4SSW0RD"))
                .addProperties("image",new StringSchema().example("https://robohash.org/John123.png"));

        return new OpenAPI()
                .info(new Info()
                        .title("Minesweeper API")
                        .description("Solution the minesweeper api challenge")
                        .version("1.0")
                        .license(new License().name("GNU/GPL").url("https://www.gnu.org/licenses/gpl-3.0.html"))
                )
                .components(new Components()
                        .addSchemas("NewGameBody" , newGameSchema)
                        .addSchemas("NewUserBody" , newUserSchema)
                );
    }
}

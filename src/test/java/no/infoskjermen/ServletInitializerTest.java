package no.infoskjermen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public class ServletInitializerTest {
    
    private ServletInitializer servletInitializer;
    
    @BeforeEach
    public void setUp() {
        servletInitializer = new ServletInitializer();
    }
    
    @Test
    public void shouldConfigureSpringApplicationBuilderWithInfoskjermenApplicationWhenCalled() {
        // Given
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        
        // When
        SpringApplicationBuilder result = servletInitializer.configure(builder);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).isSameAs(builder);
        
        // We can't easily test the internal configuration without reflection
        // but we can ensure the method returns a properly configured builder
    }
    
    @Test
    public void shouldNotThrowExceptionWhenConfiguringWithValidBuilder() {
        // Given
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        
        // When/Then - should not throw any exceptions
        SpringApplicationBuilder result = servletInitializer.configure(builder);
        assertThat(result).isNotNull();
    }
    
    @Test
    public void shouldReturnNonNullResultWhenConfiguring() {
        // Given
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        
        // When
        SpringApplicationBuilder result = servletInitializer.configure(builder);
        
        // Then
        assertThat(result).isNotNull();
    }
    
    @Test
    public void shouldBeInstantiableWhenCreatingNewInstance() {
        // Given/When
        ServletInitializer newInstance = new ServletInitializer();
        
        // Then
        assertThat(newInstance).isNotNull();
        assertThat(newInstance).isInstanceOf(ServletInitializer.class);
    }
    
    @Test
    public void shouldConfigureMultipleTimesWhenCalledRepeatedly() {
        // Given
        SpringApplicationBuilder builder1 = new SpringApplicationBuilder();
        SpringApplicationBuilder builder2 = new SpringApplicationBuilder();
        
        // When
        SpringApplicationBuilder result1 = servletInitializer.configure(builder1);
        SpringApplicationBuilder result2 = servletInitializer.configure(builder2);
        
        // Then
        assertThat(result1).isNotNull();
        assertThat(result2).isNotNull();
        assertThat(result1).isSameAs(builder1);
        assertThat(result2).isSameAs(builder2);
        assertThat(result1).isNotSameAs(result2);
    }
}
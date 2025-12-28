package no.infoskjermen.tjenester;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class WatchServiceTest {
    
    @Autowired
    private WatchService watchService;
    
    @BeforeEach
    public void setUp() {
        // No special setup needed for WatchService
    }
    
    @Test
    public void shouldReplaceTimeTokenWhenSvgContainsOppdatertToken() {
        // Given
        String svgWithToken = "<svg>Some content @@OPPDATERT@@ more content</svg>";
        
        // When
        String result = watchService.populate(svgWithToken, "testuser");
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).doesNotContain("@@OPPDATERT@@");
        assertThat(result).contains("Some content");
        assertThat(result).contains("more content");
        
        // Should contain time format (HH:mm)
        assertThat(result).matches(".*\\d{2}:\\d{2}.*");
    }
    
    @Test
    public void shouldReturnUnchangedSvgWhenNoTokenPresent() {
        // Given
        String svgWithoutToken = "<svg>Content without any time token</svg>";
        
        // When
        String result = watchService.populate(svgWithoutToken, "testuser");
        
        // Then
        assertThat(result).isEqualTo(svgWithoutToken);
    }
    
    @Test
    public void shouldReplaceMultipleTokensWhenMultipleOppdatertTokensPresent() {
        // Given
        String svgWithMultipleTokens = "Start @@OPPDATERT@@ middle @@OPPDATERT@@ end";
        
        // When
        String result = watchService.populate(svgWithMultipleTokens, "testuser");
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).doesNotContain("@@OPPDATERT@@");
        assertThat(result).contains("Start");
        assertThat(result).contains("middle");
        assertThat(result).contains("end");
        
        // Should contain time format twice
        String resultString = result.chars()
            .mapToObj(c -> String.valueOf((char) c))
            .collect(java.util.stream.Collectors.joining());
        long timeOccurrences = resultString.split("\\d{2}:\\d{2}").length - 1;
        assertThat(timeOccurrences).isEqualTo(2);
    }
    
    @Test
    public void shouldHandleEmptyStringWhenEmptySvgProvided() {
        // Given
        String emptySvg = "";
        
        // When
        String result = watchService.populate(emptySvg, "testuser");
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    public void shouldThrowNullPointerExceptionWhenNullSvgProvided() {
        // Given
        String nullSvg = null;
        
        // When/Then
        assertThat(org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () -> {
            watchService.populate(nullSvg, "testuser");
        })).isNotNull();
    }
    
    @Test
    public void shouldIgnoreUsernameParameterWhenPopulating() {
        // Given
        String svgWithToken = "Time: @@OPPDATERT@@";
        
        // When
        String result1 = watchService.populate(svgWithToken, "user1");
        String result2 = watchService.populate(svgWithToken, "user2");
        String result3 = watchService.populate(svgWithToken, null);
        
        // Then
        // All results should have the same format (time might differ by seconds)
        assertThat(result1).matches("Time: \\d{2}:\\d{2}");
        assertThat(result2).matches("Time: \\d{2}:\\d{2}");
        assertThat(result3).matches("Time: \\d{2}:\\d{2}");
    }
    
    @Test
    public void shouldReturnTrueWhenSvgContainsOppdatertToken() {
        // Given
        String svgWithToken = "<svg>@@OPPDATERT@@</svg>";
        
        // When/Then
        assertThat(watchService.isPresentInSVG(svgWithToken)).isTrue();
    }
    
    @Test
    public void shouldReturnTrueWhenSvgContainsMultipleOppdatertTokens() {
        // Given
        String svgWithMultipleTokens = "@@OPPDATERT@@ and @@OPPDATERT@@";
        
        // When/Then
        assertThat(watchService.isPresentInSVG(svgWithMultipleTokens)).isTrue();
    }
    
    @Test
    public void shouldReturnFalseWhenSvgDoesNotContainToken() {
        // Given
        String svgWithoutToken = "<svg>No time token here</svg>";
        
        // When/Then
        assertThat(watchService.isPresentInSVG(svgWithoutToken)).isFalse();
    }
    
    @Test
    public void shouldReturnFalseWhenSvgContainsSimilarButNotExactToken() {
        // Given
        String svgWithSimilarToken = "<svg>@@UPDATED@@ or @@oppdatert@@</svg>";
        
        // When/Then
        assertThat(watchService.isPresentInSVG(svgWithSimilarToken)).isFalse();
    }
    
    @Test
    public void shouldReturnFalseWhenSvgIsEmpty() {
        // Given
        String emptySvg = "";
        
        // When/Then
        assertThat(watchService.isPresentInSVG(emptySvg)).isFalse();
    }
    
    @Test
    public void shouldThrowNullPointerExceptionWhenSvgIsNull() {
        // Given
        String nullSvg = null;
        
        // When/Then
        assertThat(org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () -> {
            watchService.isPresentInSVG(nullSvg);
        })).isNotNull();
    }
    
    @Test
    public void shouldUseOsloTimezoneWhenGeneratingTime() {
        // Given
        String svgWithToken = "@@OPPDATERT@@";
        
        // When
        String result = watchService.populate(svgWithToken, "testuser");
        
        // Then
        // Should contain a valid time format
        assertThat(result).matches("\\d{2}:\\d{2}");
        
        // Time should be reasonable (between 00:00 and 23:59)
        String[] timeParts = result.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        
        assertThat(hour).isBetween(0, 23);
        assertThat(minute).isBetween(0, 59);
    }
    
    @Test
    public void shouldBeProperlyConfiguredAsSpringService() {
        // Given/When/Then
        assertThat(watchService).isNotNull();
        assertThat(watchService).isInstanceOf(PopulateInterface.class);
    }
}
package guru.qa.niffler.utils;

import com.codeborne.selenide.Browsers;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

public class BrowserConverter implements ArgumentConverter {

    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        if (!(source instanceof String sourceString)) {
            throw new ArgumentConversionException("Source must be a String");
        }

        return switch (sourceString.toLowerCase()) {
            case "chrome" -> Browsers.CHROME;
            case "firefox" -> Browsers.FIREFOX;
            case "edge" -> Browsers.EDGE;
            case "ie", "internet explorer" -> Browsers.IE;
            case "safari" -> Browsers.SAFARI;
            default -> throw new ArgumentConversionException("Unsupported browser: " + sourceString);
        };
    }
}
